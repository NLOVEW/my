package com.linghong.my.controller;

import com.linghong.my.dto.Response;
import com.linghong.my.pojo.Coupon;
import com.linghong.my.service.CouponService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/10 14:33
 * @Version 1.0
 * @Description:
 */
@RestController
public class CouponController {
    @Resource
    private CouponService couponService;

    /**
     * 商家发布优惠信息
     * 参数： startTime  endTime requirementPrice  subPrice goodsIds(数组 值为商品id)
     * @param request
     * @param coupon
     * @return
     */
    @PostMapping("/coupon/pushCoupon")
    public Response pushCoupon(Coupon coupon, HttpServletRequest request){
        boolean flag = couponService.pushCoupon(coupon,request);
        if (flag){
            return new Response(true,200 ,null ,"添加成功" );
        }
        return new Response(false,101 ,null ,"添加失败" );
    }

    /**
     * 根据商家Id获取满减信息 返回两类  过期  未过期
     * @param sellerId
     * @return
     */
    @GetMapping("/coupon/findCouponBySellerId/{sellerId}")
    public Response findCouponBySellerId(@PathVariable Long sellerId){
        Map<String, List<Coupon>> result = couponService.findCouponBySellerId(sellerId);
        return new Response(true,200 , result, "查询结果");
    }

    /**
     * 根据优惠券Id获取其详细信息
     * @param couponId
     * @return
     */
    @GetMapping("/coupon/getCouponDetail/{couponId}")
    public Response getCouponDetail(@PathVariable Long couponId){
        Map<String,Object> result = couponService.getCouponDetail(couponId);
        return new Response(true,200 , result, "查询结果");
    }

    /**
     * 根据商品获取满减优惠
     * @return
     */
    @GetMapping("/coupon/getCouponByGoodsId/{goodsId}")
    public Response getCouponByGoodsId(@PathVariable String goodsId){
        List<Coupon> coupons = couponService.getCouponByGoodsId(goodsId);
        return new Response(true,200 , coupons, "查询结果");
    }
}
