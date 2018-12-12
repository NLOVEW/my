package com.linghong.my.repository;

import com.linghong.my.pojo.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GoodsRepository extends JpaRepository<Goods,String>, JpaSpecificationExecutor<Goods> {
    List<Goods> findAllBySeller_SellerId(@Param("sellerId") Long sellerId);
    List<Goods> findAllByGoodsType(@Param("goodsType") String goodsType);
}
