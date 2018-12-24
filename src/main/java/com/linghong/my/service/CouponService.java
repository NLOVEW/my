package com.linghong.my.service;

import com.linghong.my.pojo.Coupon;
import com.linghong.my.pojo.Goods;
import com.linghong.my.pojo.Seller;
import com.linghong.my.repository.CouponRepository;
import com.linghong.my.repository.GoodsRepository;
import com.linghong.my.repository.SellerRepository;
import com.linghong.my.utils.JwtUtil;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/10 14:33
 * @Version 1.0
 * @Description:
 */
@Service
@Transactional(rollbackOn = Exception.class)
public class CouponService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private CouponRepository couponRepository;
    @Resource
    private GoodsRepository goodsRepository;
    @Resource
    private SellerRepository sellerRepository;


    public boolean pushCoupon(Coupon coupon, HttpServletRequest request) {
        Long sellerId = JwtUtil.getSellerId(request);
        Seller seller = sellerRepository.findById(sellerId).get();
        coupon.setSeller(seller);
        coupon.setCreateTime(new Date());
        logger.info("添加优惠券信息：{}",coupon);
        couponRepository.save(coupon);
        return true;
    }

    public Map<String, List<Coupon>> findCouponBySellerId(Long sellerId) {
        List<Coupon> all = couponRepository.findAllBySeller_SellerId(sellerId);
        Map<String, List<Coupon>> collect = all.stream().collect(Collectors.groupingBy(coupon -> {
            Date endTime = coupon.getEndTime();
            if (endTime.compareTo(new Date()) < 0) {
                return "可用";
            } else {
                return "过期";
            }
        }));
        return collect;
    }

    public Map<String,Object> getCouponDetail(Long couponId) {
        Map<String,Object> result = new HashedMap();
        Coupon coupon = couponRepository.findById(couponId).get();
        String[] goodsIds = coupon.getGoodsIds();
        for (String goodsId : goodsIds){
            Goods goods = goodsRepository.findById(goodsId).get();
            //判断此商品是否可购买
            if (goods.getStatus().equals(0)){
                result.put("商品Id:"+goodsId,goods );
            }
        }
        result.put("优惠券信息",coupon );
        return result;
    }
}
