package com.linghong.my.service;

import com.linghong.my.pojo.Coupon;
import com.linghong.my.pojo.User;
import com.linghong.my.pojo.UserCoupon;
import com.linghong.my.repository.CouponRepository;
import com.linghong.my.repository.UserCouponRepository;
import com.linghong.my.repository.UserRepository;
import com.linghong.my.utils.JwtUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: luck_nhb
 * @Date: 2019/1/28 18:32
 * @Version 1.0
 * @Description:
 */
@Service
@Transactional(rollbackOn = Exception.class)
public class UserCouponService {
    @Resource
    private UserCouponRepository userCouponRepository;
    @Resource
    private CouponRepository couponRepository;
    @Resource
    private UserRepository userRepository;


    public void pushCoupon(Long couponId, HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        User user = userRepository.findById(userId).get();
        Coupon coupon = couponRepository.findById(couponId).get();
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUser(user);
        userCoupon.setCoupon(coupon);
        userCoupon.setStatus(0);
        userCoupon.setCreateTime(new Date());
        userCouponRepository.save(userCoupon);
    }

    public List<UserCoupon> getCoupon(HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        List<UserCoupon> userCoupons = userCouponRepository.findAllByUser_UserId(userId);
        userCoupons.stream().filter(userCoupon -> {
            if (userCoupon.getStatus().equals(0)){
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        return null;
    }

    public void deleteCoupon(Long id){
        List<UserCoupon> userCoupons = userCouponRepository.findAllByCoupon_CouponId(id);
        userCoupons.forEach(userCoupon -> {
            userCoupon.setStatus(1);
        });
    }
}
