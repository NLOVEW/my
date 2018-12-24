package com.linghong.my.service;

import com.alibaba.fastjson.JSONObject;
import com.linghong.my.bean.ShoppingCart;
import com.linghong.my.constant.UrlConstant;
import com.linghong.my.pojo.*;
import com.linghong.my.repository.*;
import com.linghong.my.utils.FastDfsUtil;
import com.linghong.my.utils.IDUtil;
import com.linghong.my.utils.JwtUtil;
import com.linghong.my.utils.uupt.UUPTUtil;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/10 15:40
 * @Version 1.0
 * @Description:
 */
@Service
public class GoodsOrderService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("{uupt.appId}")
    private String uuptAppId;
    @Value("{uupt.appKey}")
    private String uuptAppKey;
    @Value("{uupt.openId}")
    private String uuptOpenId;

    @Resource
    private GoodsRepository goodsRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private CouponRepository couponRepository;
    @Resource
    private SellerRepository sellerRepository;
    @Resource
    private RedisService redisService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private GoodsOrderRepository goodsOrderRepository;

    public boolean pushShoppingCart(Long userId, String goodsId, Integer number) {
        ShoppingCart cart = new ShoppingCart();
        Goods goods = goodsRepository.findById(goodsId).get();
        cart.setGoods(goods);
        cart.setNumber(number);
        //先检索redis中之前有无数据
        List<Object> list = redisService.lGet(String.valueOf(userId), 0, -1);
        if (list != null && list.size() > 0){//说明之前此用户购物车已经有东西
            for (Object object : list){
                ShoppingCart shoppingCart = (ShoppingCart) object;
                //如果有相同的添加数据
                if (shoppingCart.getGoods().getGoodsId().equals(goodsId)){
                    shoppingCart.setNumber(shoppingCart.getNumber().intValue() + number.intValue());
                    Collections.replaceAll(list,object ,shoppingCart );
                }
            }
            list.add(cart);
        }else {
            List<ShoppingCart> carts = new ArrayList<>();
            carts.add(cart);
            redisService.lSet(String.valueOf(userId),carts);
        }
        return true;
    }

    public List<Object> cancelShoppingCart(Long userId,String[] goodsIds) {
        List<Object> objects = redisService.lGet(String.valueOf(userId), 0, -1);
        Iterator<Object> it = objects.iterator();
        while(it.hasNext()){
            Object o = it.next();
            ShoppingCart c = (ShoppingCart) o;
            for (String id : goodsIds){
                if (c.getGoods().getGoodsId().equals(id)){
                    it.remove();
                }
            }
        }
        redisService.lSet(String.valueOf(userId),objects );
        return objects;
    }

    public List<Object> getShoppingCartByUserId(Long userId) {
        List<Object> list = redisService.lGet(String.valueOf(userId), 0, -1);
        return list;
    }

    public Map<String,Object> settleAccounts(List<ShoppingCart> carts, HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        User user = userRepository.findById(userId).get();
        Map<String,Object> result = new HashedMap();
        BigDecimal price = new BigDecimal(0);
        //循环遍历数据 配送费 优惠券 商品原来价格
        for (ShoppingCart cart : carts){
            //创建订单
            GoodsOrder goodsOrder = new GoodsOrder();
            goodsOrder.setGoodsOrderId(IDUtil.getOrderId());
            goodsOrder.setUser(user);
            //查询所有满减
            List<Coupon> all = couponRepository.findAllBySeller_SellerId(cart.getGoods().getSeller().getSellerId());
            //排除过期的优惠券
            all = all.stream().filter(coupon -> {
                if (coupon.getStartTime().compareTo(new Date()) <= 0 && coupon.getEndTime().compareTo(new Date()) >= 0){
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            //对满减策略进行排序
            all.sort(Comparator.comparing(Coupon::getSubPrice));
            BigDecimal totalPrice = new BigDecimal(String.valueOf(cart.getGoods().getPrice().multiply(new BigDecimal(cart.getNumber().intValue()))));
            //设置订单原来的总价格
            goodsOrder.setTotalPrice(totalPrice);
            //设置订单的满减集合
            Set<Coupon> coupons = new HashSet<>();
            //循环遍历进行满减优惠操作  只要符合条件就进行减免
            for (Coupon coupon : all){
                BigDecimal requirementPrice = coupon.getRequirementPrice();
                if (totalPrice.compareTo(requirementPrice) >= 0){
                    coupons.add(coupon);
                    totalPrice = totalPrice.subtract(coupon.getSubPrice());
                }
            }
            price.add(totalPrice);
            //TODO 获取UU跑腿订单价格
            JSONObject jsonObject = UUPTUtil.getOrderPrice(goodsOrder.getGoodsOrderId(),
                    cart.getGoods().getSeller().getAddress(), cart.getAddress().getExpressAddress(),
                    cart.getGoods().getSeller().getCity() + "市", uuptOpenId, uuptAppId, uuptAppKey);
            logger.info("计算配送费：{}",jsonObject);
            //todo 设置配送费
            goodsOrder.setExpressPrice(new BigDecimal((String) jsonObject.get("need_paymoney")));
            goodsOrder.setCoupons(coupons);
            goodsOrder.setNumber(cart.getNumber());
            goodsOrder.setGoods(cart.getGoods());
            goodsOrder.setCreateTime(new Date());
            result.put(cart.getGoods().getGoodsId(), goodsOrder);
        }
        //相同商家配送费合一
        Map<Seller, List<ShoppingCart>> collect = carts.stream().collect(Collectors.groupingBy(cart -> {
            return cart.getGoods().getSeller();
        }));
        BigDecimal expressPrice = new BigDecimal(0);
        for (Map.Entry<Seller,List<ShoppingCart>> entry : collect.entrySet()){
            //TODO 获取UU跑腿订单价格
            JSONObject jsonObject = UUPTUtil.getOrderPrice(IDUtil.getId(),
                    entry.getValue().get(0).getGoods().getSeller().getAddress(), entry.getValue().get(0).getAddress().getExpressAddress(),
                    entry.getValue().get(0).getGoods().getSeller().getCity() + "市", uuptOpenId, uuptAppId, uuptAppKey);
            //累加跑腿费
            expressPrice = expressPrice.add(new BigDecimal((String) jsonObject.get("need_paymoney")));
        }
        result.put("配送费",expressPrice );
        result.put("总价",price.add(expressPrice) );
        return result;
    }

    public String submitOrder(List<GoodsOrder> goodsOrders) {
        String id = IDUtil.getOrderId();
        goodsOrders.stream().forEach(goodsOrder -> {
            goodsOrder.setRedisId(id);
        });
        redisService.lSet(id,goodsOrders );
        //删除购物车
        List<Object> objects = redisService.lGet(String.valueOf(goodsOrders.get(0).getUser().getUserId()), 0, -1);
        Iterator<Object> it = objects.iterator();
        while(it.hasNext()){
            Object o = it.next();
            ShoppingCart c = (ShoppingCart) o;
            for (GoodsOrder order : goodsOrders){
                if (c.getGoods().getGoodsId().equals(order.getGoods().getGoodsId())){
                    it.remove();
                }
            }
        }
        redisService.lSet(String.valueOf(goodsOrders.get(0).getUser().getUserId()),objects );
        return id;
    }

    public GoodsOrder getOrderDetail(String goodsOrderId) {
        return goodsOrderRepository.findById(goodsOrderId).get();
    }

    public boolean cancelOrder(String goodsOrderId) {
        GoodsOrder goodsOrder = goodsOrderRepository.findById(goodsOrderId).get();
        if (goodsOrder.getStatus().intValue() >= 2){
            return false;
        }
        goodsOrder.setStatus(6);
        //修改redis中缓存的状态
        List<Object> objects = redisService.lGet(goodsOrder.getRedisId(), 0, -1);
        for (Object object : objects){
            GoodsOrder temp = (GoodsOrder) object;
            if (temp.getGoodsOrderId().equals(goodsOrderId)){
                temp.setStatus(6);
            }
        }
        redisService.lSet(goodsOrder.getRedisId(), objects);
        return true;
    }

    public JSONObject getExpress(String goodsOrderId) {
        GoodsOrder goodsOrder = goodsOrderRepository.findById(goodsOrderId).get();
        return UUPTUtil.getOrderDetail(goodsOrder.getExpressId(), uuptOpenId,uuptAppId ,uuptAppKey );
    }

    public List<GoodsOrder> findAllOrderByUserId(Long userId) {
        List<GoodsOrder> orders = goodsOrderRepository.findAllByUser_UserId(userId);
        return orders;
    }

    public Map<Integer, List<GoodsOrder>> findAllOrderBySellerId(Long sellerId) {
        List<GoodsOrder> all = goodsOrderRepository.findAllByGoods_Seller_SellerId(sellerId);
        Map<Integer, List<GoodsOrder>> collect = all.stream().collect(Collectors.groupingBy(goodsOrder -> {
            return goodsOrder.getStatus();
        }));
        return collect;
    }

    public boolean dealCancelOrder(String goodsOrderId, Integer status) {
        GoodsOrder goodsOrder = goodsOrderRepository.findById(goodsOrderId).get();
        if (status.equals(7)){//同意取消订单
            rabbitTemplate.convertAndSend("order","backPrice" ,goodsOrder);
        }
        goodsOrder.setStatus(status);
        return true;
    }

    public boolean discussOrder(String orderId,
                                DiscussMessage discussMessage,
                                String base64Images) {
        GoodsOrder goodsOrder = goodsOrderRepository.findById(orderId).get();
        discussMessage.setFromUser(goodsOrder.getUser());
        discussMessage.setCreateTime(new Date());
        if (base64Images != null){
            Set<Image> images = new HashSet<>();
            String[] split = base64Images.split("。");
            for (String result : split){
                Image image = new Image();
                image.setCreateTime(new Date());
                image.setPath(UrlConstant.IMAGE_URL+new FastDfsUtil().uploadBase64Image(result));
                images.add(image);
            }
            discussMessage.setImages(images);
        }
        Set<DiscussMessage> discussMessages = goodsOrder.getDiscussMessages();
        discussMessages.add(discussMessage);
        goodsOrder.setDiscussMessages(discussMessages);
        return true;
    }

}
