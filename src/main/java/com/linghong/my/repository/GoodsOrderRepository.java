package com.linghong.my.repository;

import com.linghong.my.pojo.GoodsOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GoodsOrderRepository extends JpaRepository<GoodsOrder,String> {
    List<GoodsOrder> findAllByGoods_GoodsId(@Param("goodsId") String goodsId);
    Integer countAllByGoods_GoodsId(@Param("goodsId") String goodsId);
    List<GoodsOrder> findAllByUser_UserId(@Param("userId") Long userId);
    List<GoodsOrder> findAllByGoods_Seller_SellerId(@Param("sellerId") Long sellerId);
}
