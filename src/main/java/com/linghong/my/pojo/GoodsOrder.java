package com.linghong.my.pojo;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/7 14:53
 * @Version 1.0
 * @Description:
 */
@Entity
@Table(name = "goods_order")
public class GoodsOrder implements Serializable {
    private String goodsOrderId;
    private String redisId;//redis中存储的key
    private String expressId;//uu跑腿订单
    private Goods goods;//商品
    private User user;//购买者
    private Address address;//收货地址
    private Set<Coupon> coupons;//优惠券使用
    private Integer number;//数量
    private BigDecimal totalPrice;//总的原价
    private BigDecimal price;//实际价格 商品价格-优惠券
    private BigDecimal expressPrice;//配送费
    private Set<DiscussMessage> discussMessages;
    private Integer status;//0买家下单 1卖家接单 2骑手接单 3骑手配送 4已送达 5已支付 6买家请求取消订单 7同意取消 8拒绝取消
    private String comment;//买家留言
    private BackGoods backGoods;
    private Date createTime;

    @Id
    @Column(name = "goodsOrderId",length = 32,unique = true)
    public String getGoodsOrderId() {
        return goodsOrderId;
    }

    public void setGoodsOrderId(String goodsOrderId) {
        this.goodsOrderId = goodsOrderId;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "goodsId")
    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "addressId")
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "goodsOrderId")
    public Set<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(Set<Coupon> coupons) {
        this.coupons = coupons;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "goodsOrderId")
    public Set<DiscussMessage> getDiscussMessages() {
        return discussMessages;
    }

    public void setDiscussMessages(Set<DiscussMessage> discussMessages) {
        this.discussMessages = discussMessages;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getExpressPrice() {
        return expressPrice;
    }

    public void setExpressPrice(BigDecimal expressPrice) {
        this.expressPrice = expressPrice;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "backGoodsId")
    public BackGoods getBackGoods() {
        return backGoods;
    }

    public void setBackGoods(BackGoods backGoods) {
        this.backGoods = backGoods;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRedisId() {
        return redisId;
    }

    public void setRedisId(String redisId) {
        this.redisId = redisId;
    }

    public String getExpressId() {
        return expressId;
    }

    public void setExpressId(String expressId) {
        this.expressId = expressId;
    }

    @Override
    public String toString() {
        return "GoodsOrder{" +
                "goodsOrderId='" + goodsOrderId + '\'' +
                ", redisId='" + redisId + '\'' +
                ", expressId='" + expressId + '\'' +
                ", goods=" + goods +
                ", user=" + user +
                ", address=" + address +
                ", coupons=" + coupons +
                ", number=" + number +
                ", totalPrice=" + totalPrice +
                ", price=" + price +
                ", expressPrice=" + expressPrice +
                ", discussMessages=" + discussMessages +
                ", status=" + status +
                ", comment='" + comment + '\'' +
                ", backGoods=" + backGoods +
                ", createTime=" + createTime +
                '}';
    }
}
