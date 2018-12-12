package com.linghong.my.repository;

import com.linghong.my.pojo.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface SellerRepository extends JpaRepository<Seller,Long> {
    Seller findByMobilePhone(@Param("mobilePhone") String mobilePhone);
}
