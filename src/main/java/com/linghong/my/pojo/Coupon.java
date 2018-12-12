package com.linghong.my.pojo;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/7 14:56
 * @Version 1.0
 * @Description: 优惠券
 */
@Entity
@Table(name = "coupon")
public class Coupon implements Serializable {
    private Long couponId;
    private Seller seller;//设置方
    private String title;//标题
    private Date startTime;//优惠开始时间
    private Date endTime;//结束时间
    private String[] goodsIds;//可以使用的商品id
    private BigDecimal requirementPrice;//要求达到的金额
    private BigDecimal subPrice;//减的金额
    private Date createTime;

    @Id
    @GeneratedValue
    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sellerId")
    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String[] getGoodsIds() {
        return goodsIds;
    }

    public void setGoodsIds(String[] goodsIds) {
        this.goodsIds = goodsIds;
    }

    public BigDecimal getRequirementPrice() {
        return requirementPrice;
    }

    public void setRequirementPrice(BigDecimal requirementPrice) {
        this.requirementPrice = requirementPrice;
    }

    public BigDecimal getSubPrice() {
        return subPrice;
    }

    public void setSubPrice(BigDecimal subPrice) {
        this.subPrice = subPrice;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "couponId=" + couponId +
                ", seller=" + seller +
                ", title='" + title + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", goodsIds=" + goodsIds +
                ", requirementPrice=" + requirementPrice +
                ", subPrice=" + subPrice +
                ", createTime=" + createTime +
                '}';
    }
}
