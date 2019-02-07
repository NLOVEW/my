package com.linghong.my.service;

import com.alibaba.fastjson.JSON;
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
import javax.transaction.Transactional;
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
@Transactional(rollbackOn = Exception.class)
public class GoodsOrderService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${uupt.appId}")
    private String uuptAppId;
    @Value("${uupt.appKey}")
    private String uuptAppKey;
    @Value("${uupt.openId}")
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
    @Resource
    private UserCouponService userCouponService;
    @Resource
    private AddressRepository addressRepository;

    public boolean pushShoppingCart(Long userId, String goodsId, Integer number) {
        logger.info("添加购物车商品Id:{}", goodsId);
        ShoppingCart cart = new ShoppingCart();
        Goods goods = goodsRepository.findById(goodsId).get();
        cart.setGoods(goods);
        cart.setNumber(number);
        //先检索redis中之前有无数据
        String string = (String) redisService.get(String.valueOf(userId));
        logger.info("string:{}", string);
        if (string != null && !string.startsWith("[{")) {
            string = "[" + string + "]";
        }
        logger.info("json:{}", string);
        List<ShoppingCart> list = JSON.parseArray(string, ShoppingCart.class);
        List<ShoppingCart> result = new ArrayList<>();
        if (list != null && list.size() > 0) {//说明之前此用户购物车已经有东西
            int temp = 0;
            for (ShoppingCart object : list) {
                logger.info("购物车：{}", object);
                //如果有相同的添加数据
                if (object.getGoods().getGoodsId().equals(goodsId)) {
                    ShoppingCart shoppingCart = new ShoppingCart();
                    shoppingCart.setGoods(object.getGoods());
                    shoppingCart.setNumber(object.getNumber().intValue() + number.intValue());
                    result.add(shoppingCart);
                    temp = 1;
                } else {
                    result.add(object);
                }
            }
            if (temp == 0) {
                result.add(cart);
            }
            redisService.set(String.valueOf(userId), JSON.toJSONString(result));
        } else {
            List<ShoppingCart> carts = new ArrayList<>();
            carts.add(cart);
            redisService.set(String.valueOf(userId), JSON.toJSONString(carts));
        }
        return true;
    }

    public List<ShoppingCart> cancelShoppingCart(Long userId, String[] goodsIds) {
        String string = (String) redisService.get(String.valueOf(userId));
        if (string != null) {
            if (!string.startsWith("[{")) {
                string = "[" + string + "]";
            }
            List<ShoppingCart> list = JSON.parseArray(string, ShoppingCart.class);
            Iterator<ShoppingCart> it = list.iterator();
            while (it.hasNext()) {
                ShoppingCart o = it.next();
                for (String id : goodsIds) {
                    if (o.getGoods().getGoodsId().equals(id)) {
                        it.remove();
                    }
                }
            }
            redisService.set(String.valueOf(userId), JSON.toJSONString(list));
            return list;
        }
        return null;
    }

    public List<ShoppingCart> getShoppingCartByUserId(Long userId) {
        String string = (String) redisService.get(String.valueOf(userId));
        if (string != null && !string.startsWith("[{")) {
            string = "[" + string + "]";
        }
        List<ShoppingCart> list = JSON.parseArray(string, ShoppingCart.class);
        return list;
    }

    public Map<String, Object> settleAccounts(List<ShoppingCart> carts, HttpServletRequest request) {
        carts.stream().forEach(cart -> {
            logger.info("结算传入的参数：{}", cart.toString());
        });
        Long userId = JwtUtil.getUserId(request);
        User user = userRepository.findById(userId).get();
        Map<String, Object> result = new HashedMap();
        BigDecimal price = new BigDecimal(0);
        List<GoodsOrder> resultOrder = new ArrayList<>();
        Set<Coupon> coupons = new HashSet<>();
        //循环遍历数据 配送费 优惠券 商品原来价格
        for (ShoppingCart cart : carts) {
            //创建订单
            GoodsOrder goodsOrder = new GoodsOrder();
            goodsOrder.setGoodsOrderId(IDUtil.getOrderId());
            goodsOrder.setUser(user);
            //查询所有满减
            List<Coupon> all = couponRepository.findAllBySeller_SellerId(cart.getGoods().getSeller().getSellerId());
            //排除过期的优惠券
            all = all.stream().filter(coupon -> {
                if (coupon.getStartTime().compareTo(new Date()) <= 0 && coupon.getEndTime().compareTo(new Date()) >= 0) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            //对满减策略进行排序
            all.sort(Comparator.comparing(Coupon::getSubPrice));
            BigDecimal totalPrice = new BigDecimal(String.valueOf(cart.getGoods().getPrice().multiply(new BigDecimal(cart.getNumber().intValue()))));
            logger.info("totalPrice:{}", totalPrice.toPlainString());
            //设置订单原来的总价格
            goodsOrder.setTotalPrice(totalPrice);
            //设置订单的满减集合
            //循环遍历进行满减优惠操作  只要符合条件就进行减免
            for (Coupon coupon : all) {
                BigDecimal requirementPrice = coupon.getRequirementPrice();
                if (totalPrice.compareTo(requirementPrice) >= 0) {
                    coupons.add(coupon);
                    //使用优惠卷
                    userCouponService.deleteCoupon(coupon.getCouponId());
                    totalPrice = totalPrice.subtract(coupon.getSubPrice());
                    logger.info("满减后：{}", totalPrice);
                }
            }
            price = price.add(totalPrice);
            logger.info("当前商品价格总额:{}", price.toPlainString());
            //TODO 获取UU跑腿订单价格
            logger.info("address:{}", cart.getGoods().getSeller().getAddress());
            logger.info("city:{}", cart.getGoods().getSeller().getCity());
            JSONObject jsonObject = UUPTUtil.getOrderPrice(goodsOrder.getGoodsOrderId(),
                    cart.getGoods().getSeller().getAddress(), cart.getAddress().getExpressAddress(),
                    cart.getGoods().getSeller().getCity() + "市", uuptOpenId, uuptAppId, uuptAppKey);
            logger.info("计算配送费：{}", jsonObject);
            //todo 设置配送费
            goodsOrder.setExpressPrice(new BigDecimal((String) jsonObject.get("need_paymoney")));
            goodsOrder.setCoupons(coupons);
            goodsOrder.setNumber(cart.getNumber());
            goodsOrder.setGoods(cart.getGoods());
            goodsOrder.setCreateTime(new Date());
            //result.put(cart.getGoods().getGoodsId(), goodsOrder);
            resultOrder.add(goodsOrder);
            redisService.set(goodsOrder.getGoodsOrderId(), JSON.toJSONString(goodsOrder));
        }
        //相同商家配送费合一
        Map<Seller, List<ShoppingCart>> collect = carts.stream().collect(Collectors.groupingBy(cart -> {
            return cart.getGoods().getSeller();
        }));
        BigDecimal expressPrice = new BigDecimal(0);
        for (Map.Entry<Seller, List<ShoppingCart>> entry : collect.entrySet()) {
            //TODO 获取UU跑腿订单价格
            JSONObject jsonObject = UUPTUtil.getOrderPrice(IDUtil.getId(),
                    entry.getValue().get(0).getGoods().getSeller().getAddress(), entry.getValue().get(0).getAddress().getExpressAddress(),
                    entry.getValue().get(0).getGoods().getSeller().getCity() + "市", uuptOpenId, uuptAppId, uuptAppKey);
            //累加跑腿费
            expressPrice = expressPrice.add(new BigDecimal((String) jsonObject.get("need_paymoney")));
        }
        result.put("可使用的满减活动", coupons);
        result.put("配送费", expressPrice);
        result.put("所有商品订单", resultOrder);
        result.put("总价", price.add(expressPrice));
        return result;
    }


    public Map<String, Object> updateSettleAccounts(Long addressId, String[] goodsIds) {
        Address address = addressRepository.findById(addressId).get();
        Map<String, Object> result = new HashedMap();
        BigDecimal price = new BigDecimal(0);
        List<GoodsOrder> resultOrder = new ArrayList<>();
        Set<Coupon> coupons = new HashSet<>();
        List<GoodsOrder> orders = new ArrayList<>();
        for (String goodsId : goodsIds){
            String goodsString = (String) redisService.get(goodsId);
            GoodsOrder goodsOrder = JSON.parseObject(goodsString, GoodsOrder.class);
            //重新设置收货地址
            goodsOrder.setAddress(address);
            //查询所有满减
            List<Coupon> all = couponRepository.findAllBySeller_SellerId(goodsOrder.getGoods().getSeller().getSellerId());
            //排除过期的优惠券
            all = all.stream().filter(coupon -> {
                if (coupon.getStartTime().compareTo(new Date()) <= 0 && coupon.getEndTime().compareTo(new Date()) >= 0) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            //对满减策略进行排序
            all.sort(Comparator.comparing(Coupon::getSubPrice));
            BigDecimal totalPrice = new BigDecimal(String.valueOf(goodsOrder.getGoods().getPrice().multiply(new BigDecimal(goodsOrder.getNumber().intValue()))));
            logger.info("totalPrice:{}", totalPrice.toPlainString());
            //设置订单原来的总价格
            goodsOrder.setTotalPrice(totalPrice);
            //设置订单的满减集合
            //循环遍历进行满减优惠操作  只要符合条件就进行减免
            for (Coupon coupon : all) {
                BigDecimal requirementPrice = coupon.getRequirementPrice();
                if (totalPrice.compareTo(requirementPrice) >= 0) {
                    coupons.add(coupon);
                    //使用优惠卷
                    userCouponService.deleteCoupon(coupon.getCouponId());
                    totalPrice = totalPrice.subtract(coupon.getSubPrice());
                    logger.info("满减后：{}", totalPrice);
                }
            }
            price = price.add(totalPrice);
            logger.info("当前商品价格总额:{}", price.toPlainString());
            //TODO 获取UU跑腿订单价格
            logger.info("address:{}", goodsOrder.getGoods().getSeller().getAddress());
            logger.info("city:{}", goodsOrder.getGoods().getSeller().getCity());
            JSONObject jsonObject = UUPTUtil.getOrderPrice(goodsOrder.getGoodsOrderId(),
                    goodsOrder.getGoods().getSeller().getAddress(), goodsOrder.getAddress().getExpressAddress(),
                    goodsOrder.getGoods().getSeller().getCity() + "市", uuptOpenId, uuptAppId, uuptAppKey);
            logger.info("计算配送费：{}", jsonObject);
            //todo 设置配送费
            goodsOrder.setExpressPrice(new BigDecimal((String) jsonObject.get("need_paymoney")));
            goodsOrder.setCoupons(coupons);
            goodsOrder.setCreateTime(new Date());
            orders.add(goodsOrder);
            resultOrder.add(goodsOrder);
            redisService.set(goodsOrder.getGoodsOrderId(), JSON.toJSONString(goodsOrder));
        }

        //相同商家配送费合一
        Map<Long, List<GoodsOrder>> listMap = orders.stream().collect(Collectors.groupingBy(goodsOrder -> {
            return goodsOrder.getGoods().getSeller().getSellerId();
        }));
        BigDecimal expressPrice = new BigDecimal(0);
        for (Map.Entry<Long,List<GoodsOrder>> entry : listMap.entrySet()){
            //TODO 获取UU跑腿订单价格
            JSONObject jsonObject = UUPTUtil.getOrderPrice(IDUtil.getId(),
                    entry.getValue().get(0).getGoods().getSeller().getAddress(), entry.getValue().get(0).getAddress().getExpressAddress(),
                    entry.getValue().get(0).getGoods().getSeller().getCity() + "市", uuptOpenId, uuptAppId, uuptAppKey);
            //累加跑腿费
            expressPrice = expressPrice.add(new BigDecimal((String) jsonObject.get("need_paymoney")));
        }
        result.put("可使用的满减活动", coupons);
        result.put("配送费", expressPrice);
        result.put("所有商品订单", resultOrder);
        result.put("总价", price.add(expressPrice));
        return result;
    }

    public String submitOrder(String[] goodsOrdersIds,String comment) {
        String id = IDUtil.getOrderId();
        List<GoodsOrder> goodsOrders = new ArrayList<>();
        logger.info("循环遍历id:{}", Arrays.toString(goodsOrdersIds));
        for (String orderId : goodsOrdersIds){
            String temp = (String) redisService.get(orderId);
            GoodsOrder goodsOrder = JSON.parseObject(temp, GoodsOrder.class);
            goodsOrders.add(goodsOrder);
        }
        goodsOrders.stream().forEach(goodsOrder -> {
            goodsOrder.setComment(comment);
            goodsOrder.setRedisId(id);
        });
        redisService.set(id, JSON.toJSONString(goodsOrders));
        //删除购物车
        String string = (String) redisService.get(String.valueOf(goodsOrders.get(0).getUser().getUserId()));
        if (string != null && !string.startsWith("[{")) {
            string = "[" + string + "]";
        }
        int temp = 0;
        List<ShoppingCart> list = JSON.parseArray(string, ShoppingCart.class);
        int sum = list.size();
        Iterator<ShoppingCart> it = list.iterator();
        while (it.hasNext()) {
            ShoppingCart o = it.next();
            for (GoodsOrder order : goodsOrders) {
                if (o.getGoods().getGoodsId().equals(order.getGoods().getGoodsId())) {
                    temp++;
                    it.remove();
                }
            }
        }
        if(temp == sum){
            redisService.del(String.valueOf(goodsOrders.get(0).getUser().getUserId()));
        }else {
            redisService.set(String.valueOf(goodsOrders.get(0).getUser().getUserId()), JSON.toJSONString(list));
        }
        return id;
    }

    public GoodsOrder getOrderDetail(String goodsOrderId) {
        return goodsOrderRepository.findById(goodsOrderId).get();
    }

    public boolean cancelOrder(String goodsOrderId) {
        GoodsOrder goodsOrder = goodsOrderRepository.findById(goodsOrderId).get();
        if (goodsOrder.getStatus().intValue() >= 2) {
            return false;
        }
        goodsOrder.setStatus(6);
        //修改redis中缓存的状态
        String string = (String) redisService.get(String.valueOf(goodsOrder.getRedisId()));
        if (string != null && !string.startsWith("[{")) {
            string = "[" + string + "]";
        }
        List<GoodsOrder> list = JSON.parseArray(string, GoodsOrder.class);
        for (GoodsOrder temp : list) {
            if (temp.getGoodsOrderId().equals(goodsOrderId)) {
                temp.setStatus(6);
            }
        }
        redisService.set(goodsOrder.getRedisId(), JSON.toJSONString(list));
        return true;
    }

    public JSONObject getExpress(String goodsOrderId) {
        GoodsOrder goodsOrder = goodsOrderRepository.findById(goodsOrderId).get();
        return UUPTUtil.getOrderDetail(goodsOrder.getExpressId(), uuptOpenId, uuptAppId, uuptAppKey);
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
        if (status.equals(7)) {//同意取消订单
            rabbitTemplate.convertAndSend("order", "backPrice", goodsOrder);
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
        if (base64Images != null) {
            Set<Image> images = new HashSet<>();
            String[] split = base64Images.split("。");
            for (String result : split) {
                Image image = new Image();
                image.setCreateTime(new Date());
                image.setPath(UrlConstant.IMAGE_URL + new FastDfsUtil().uploadBase64Image(result));
                images.add(image);
            }
            discussMessage.setImages(images);
        }
        Set<DiscussMessage> discussMessages = goodsOrder.getDiscussMessages();
        discussMessages.add(discussMessage);
        goodsOrder.setDiscussMessages(discussMessages);
        return true;
    }

    public List<ShoppingCart> testJson() {
        List<Address> addressList = addressRepository.findAllByUser_UserId(3L);
        String string = (String) redisService.get("3");
        List<ShoppingCart> list = null;
        if (string != null) {
            if (!string.startsWith("[{")) {
                string = "[" + string + "]";
            }
            list = JSON.parseArray(string, ShoppingCart.class);
            Iterator<ShoppingCart> it = list.iterator();
            while (it.hasNext()) {
                ShoppingCart o = it.next();
                o.setAddress(addressList.get(0));
            }
        }
        return list;
    }


}
