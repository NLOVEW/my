package com.linghong.my.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: luck_nhb
 * @Date: 2019/1/28 18:28
 * @Version 1.0
 * @Description:
 */
@Entity
@Table(name = "user_coupon")
public class UserCoupon implements Serializable {
    private Long userCouponId;
    private User user;
    private Coupon coupon;
    @JsonIgnore
    private Integer status; //0代表可用  1代表不可用
    private Date createTime;

    @Id
    @GeneratedValue
    public Long getUserCouponId() {
        return userCouponId;
    }

    public void setUserCouponId(Long userCouponId) {
        this.userCouponId = userCouponId;
    }

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "userId")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "couponId")
    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserCoupon{" +
                "userCouponId=" + userCouponId +
                ", user=" + user +
                ", coupon=" + coupon +
                ", createTime=" + createTime +
                '}';
    }
}
