package com.linghong.my.repository;

import com.linghong.my.pojo.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon,Long> {
    List<Coupon> findAllBySeller_SellerId(@Param("sellerId") Long sellerId);
}
