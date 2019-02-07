package com.linghong.my.service;

import com.linghong.my.constant.UrlConstant;
import com.linghong.my.pojo.Collection;
import com.linghong.my.pojo.*;
import com.linghong.my.repository.*;
import com.linghong.my.utils.BeanUtil;
import com.linghong.my.utils.FastDfsUtil;
import com.linghong.my.utils.IDUtil;
import com.linghong.my.utils.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/10 13:27
 * @Version 1.0
 * @Description:
 */
@Service
@Transactional(rollbackOn = Exception.class)
public class GoodsService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private GoodsRepository goodsRepository;
    @Resource
    private SellerRepository sellerRepository;
    @Resource
    private GoodsOrderRepository goodsOrderRepository;
    @Resource
    private CouponRepository couponRepository;
    @Resource
    private CollectionRepository collectionRepository;
    @Resource
    private DiscussMessageRepository discussMessageRepository;
    @Resource
    private ImageRepository imageRepository;

    public boolean pushGoods(Goods goods, String baseImages, HttpServletRequest request) {
        Long sellerId = JwtUtil.getSellerId(request);
        Seller seller = sellerRepository.findById(sellerId).get();
        goods.setSeller(seller);
        if (StringUtils.isNotEmpty(baseImages)) {
            logger.info("商品图片：{}", baseImages);
            Set<Image> images = new HashSet<>();
            String[] split = baseImages.split("。");
            for (String base64 : split) {
                Image image = new Image();
                image.setCreateTime(new Date());
                image.setPath(UrlConstant.IMAGE_URL + new FastDfsUtil().uploadBase64Image(base64));
                images.add(image);
            }
            goods.setImages(images);
        }
        goods.setGoodsId(IDUtil.getId());
        goods.setStatus(0);
        goodsRepository.save(goods);
        return true;
    }

    public boolean updateGoods(Goods goods, String baseImages) {
        Goods target = goodsRepository.findById(goods.getGoodsId()).get();
        logger.info("修改前：{}", target);
        BeanUtil.copyPropertiesIgnoreNull(goods, target);
        if (StringUtils.isNotEmpty(baseImages)) {
            Set<Image> images = target.getImages();
            imageRepository.deleteAll(images);
            images = new HashSet<>();
            String[] split = baseImages.split("。");
            for (String base64 : split) {
                Image image = new Image();
                image.setCreateTime(new Date());
                image.setPath(UrlConstant.IMAGE_URL + new FastDfsUtil().uploadBase64Image(base64));
                images.add(image);
            }
            target.setImages(images);
        }
        logger.info("修改后：{}", target);
        return true;
    }

    public boolean deleteGoods(String goodsId) {
        Goods target = goodsRepository.findById(goodsId).get();
        target.setStatus(2);
        return true;
    }

    public boolean obtainedGoods(String goodsId) {
        Goods target = goodsRepository.findById(goodsId).get();
        target.setStatus(1);
        return true;
    }

    public Map<String, List<Goods>> findGoodsBySellerId(Long sellerId) {
        List<Goods> all = goodsRepository.findAllBySeller_SellerId(sellerId);
        Map<Integer, List<Goods>> collect = all.stream().collect(Collectors.groupingBy(Goods::getStatus));
        Map<String, List<Goods>> result = new HashMap<>();
        collect.forEach((key, value) -> {
            if (key.equals(0)) {
                result.put("已上架", value);
            } else if (key.equals(1)) {
                result.put("已下架", value);
            } else {
                result.put("已删除", value);
            }
        });
        result.forEach((key, value) -> {
            if (key.equals(0)) {
                //查询总销售量
                value.stream().forEach(goods -> {
                    Integer count = goodsOrderRepository.countAllByGoods_GoodsId(goods.getGoodsId());
                    goods.setSalesVolume(count);
                });
            }
        });
        return result;
    }

    public Map<String, Object> getGoodsDetailByGoodsId(String goodsId) {
        Map<String, Object> result = new HashMap<>();
        Goods goods = goodsRepository.findById(goodsId).get();
        Integer count = goodsOrderRepository.countAllByGoods_GoodsId(goods.getGoodsId());
        goods.setSalesVolume(count);
        //检索符合此商品的优惠券
        List<Coupon> allCoupon = couponRepository.findAllBySeller_SellerId(goods.getSeller().getSellerId());
        allCoupon.stream().forEach(coupon -> {
            //判断优惠券是否过期
            if (coupon.getEndTime().compareTo(new Date()) < 0 && coupon.getStartTime().compareTo(new Date()) > 0) {
                String[] goodsIds = coupon.getGoodsIds();
                for (String gds : goodsIds) {
                    if (gds.equals(goodsId)) {
                        result.put("满减优惠券Id:" + coupon.getCouponId(), coupon);
                    }
                }
            }
        });
        List<GoodsOrder> goodsOrders = goodsOrderRepository.findAllByGoods_GoodsId(goodsId);
        List<Set<DiscussMessage>> discussMessages = new ArrayList<>();
        for (GoodsOrder goodsOrder : goodsOrders) {
            if (goodsOrder.getDiscussMessages() != null && goodsOrder.getDiscussMessages().size() > 0) {
                discussMessages.add(goodsOrder.getDiscussMessages());
            }
        }
        result.put("商品信息", goods);
        result.put("评论", discussMessages);
        return result;
    }

    public List<Goods> findByCityAndType(String city, String type) {
        Specification<Goods> specification = (root, query, builder) -> {
            Predicate predicate = builder.like(root.get("type").as(String.class), "%" + type + "%");
            Predicate status = builder.equal(root.get("status").as(Integer.class), 0);
            return builder.and(predicate, status);
        };
        List<Goods> all = goodsRepository.findAll(specification);
        all = all.stream().filter(goods -> {
            if (goods.getSeller().getAddress().contains(city)) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        return all;
    }

    public List<Goods> findByCityAndKey(String city, String key) {
        Specification<Goods> specification = (root, query, builder) -> {
            Predicate type = builder.like(root.get("type").as(String.class), "%" + key + "%");
            Predicate status = builder.equal(root.get("status").as(Integer.class), 0);
            return builder.or(type, builder.and(status));
        };
        List<Goods> all = goodsRepository.findAll(specification);
        all = all.stream().filter(goods -> {
            if (goods.getSeller().getAddress().contains(city)) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        return all;
    }

    public List<Goods> getLoveGoods(Long userId) {
        Collection collection = collectionRepository.findByUser_UserId(userId);
        List<Goods> result = new ArrayList<>();
        if (collection.getGoods() != null && collection.getGoods().size() > 0){
            Set<Goods> goods = collection.getGoods();
            if (goods != null && goods.size() > 0) {
                for (Goods goods1 : goods) {
                    List<Goods> temp = goodsRepository.findAllByGoodsType(goods1.getGoodsType());
                    result = Stream.concat(result.stream(), temp.stream()).collect(Collectors.toList());
                }
            }
            return result;
        }
        return null;
    }

    public List<Goods> getGoodsByAuto() {
        List<GoodsOrder> all = goodsOrderRepository.findAll();
        Map<Goods, List<GoodsOrder>> collect = all.stream().collect(Collectors.groupingBy(GoodsOrder::getGoods));
        Map<Integer, Goods> temp = new TreeMap<Integer, Goods>((o1, o2) -> {
            return o1.intValue() > o2.intValue() ? o1 : o2;
        });
        for (Map.Entry<Goods, List<GoodsOrder>> entry : collect.entrySet()) {
            temp.put(entry.getValue().size(), entry.getKey());
        }
        List<Goods> result = new ArrayList<>();
        for (Map.Entry<Integer, Goods> entry : temp.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

    public boolean upGoods(String goodsId) {
        Goods goods = goodsRepository.findById(goodsId).get();
        goods.setStatus(0);
        return true;
    }

    public List<Goods> limitShoppingByTime() {
        List<Coupon> all = couponRepository.findAll();
        Set<String> goodsIds = new HashSet<>();
        for (Coupon coupon : all) {
            if (coupon.getStartTime().compareTo(new Date()) <= 0 && coupon.getEndTime().compareTo(new Date()) >= 0) {
                for (String goodsId : coupon.getGoodsIds()){
                    goodsIds.add(goodsId);
                }
            }
        }
        List<Goods> goods = goodsRepository.findAllById(goodsIds);
        return goods;
    }

    public Map<String, List<Goods>> getGoodsByType() {
        List<Goods> goodsList = goodsRepository.findAll();
        Map<String, List<Goods>> collect = goodsList.stream().collect(Collectors.groupingBy(Goods::getGoodsType));
        return collect;
    }

    public List<Goods> getLoveGoodsBySellerId(Long sellerId) {
        List<Goods> goods = goodsRepository.findAllBySeller_SellerIdOrderByPriceAsc(sellerId);
        goods = goods.stream().filter(goods1 -> {
            if (goods1.getStatus().equals(1) || goods1.getStatus().equals(2)){
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        return goods;
    }

    public List<Goods> getGoodsByGoodGoods() {
        List<Goods> goodsList = goodsRepository.findAll();
        goodsList = goodsList.stream().filter(goods -> {
            if (goods.getStatus().equals(1) || goods.getStatus().equals(2)){
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        return goodsList;
    }

    public List<Goods> getGoodsByChild() {
        List<Goods> goodsList = goodsRepository.findAll();
        goodsList = goodsList.stream().filter(goods -> {
            if (goods.getStatus().equals(1) || goods.getStatus().equals(2) ){
                return false;
            }
            if (goods.getGoodsType().contains("婴")){
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        return goodsList;
    }
}
