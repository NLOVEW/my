package com.linghong.my.repository;

import com.linghong.my.pojo.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill,Long> {
    List<Bill> findAllByUser_UserId(@Param("userId") Long userId);
}
