package com.linghong.my.repository;

import com.linghong.my.pojo.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address,Long> {
    List<Address> findAllByUser_UserId(@Param("userId") Long userId);
}
