package com.linghong.my.controller;

import com.linghong.my.dto.Response;
import com.linghong.my.pojo.UserCoupon;
import com.linghong.my.service.UserCouponService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Auther: luck_nhb
 * @Date: 2019/1/28 18:33
 * @Version 1.0
 * @Description:
 */
@RestController
public class UserCouponController {
    @Resource
    private UserCouponService userCouponService;

    /**
     * 用户领取优惠卷
     * @return
     */
    @PostMapping("/userCoupon/pushCoupon")
    public Response pushCoupon(Long couponId, HttpServletRequest request){
        userCouponService.pushCoupon(couponId,request);
        return new Response(true, 200, null, "已添加");
    }

    @GetMapping("/userCoupon/getCoupon")
    public Response getCoupon(HttpServletRequest request){
        List<UserCoupon> userCoupons = userCouponService.getCoupon(request);
        return new Response(true, 200, userCoupons, "可用的优惠卷");
    }
}
