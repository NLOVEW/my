package com.linghong.my.repository;

import com.linghong.my.pojo.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCouponRepository extends JpaRepository<UserCoupon,Long> {
    public List<UserCoupon> findAllByUser_UserId(@Param("userId") Long userId);
    public List<UserCoupon> findAllByCoupon_CouponId(@Param("couponId") Long couponId);
}
