package com.linghong.my.bean;

import com.linghong.my.pojo.Address;
import com.linghong.my.pojo.Goods;

import java.io.Serializable;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/10 11:17
 * @Version 1.0
 * @Description: 购物车 购物车信息存放到redis中
 */
public class ShoppingCart implements Serializable {
    private Goods goods;
    private Integer number;
    private Address address;

    public ShoppingCart() {
    }

    public ShoppingCart(Goods goods, Integer number, Address address) {
        this.goods = goods;
        this.number = number;
        this.address = address;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "ShoppingCart{" +
                "goods=" + goods +
                ", number=" + number +
                ", address=" + address +
                '}';
    }
}
