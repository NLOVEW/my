package com.linghong.my.pojo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/29 10:48
 * @Version 1.0
 * @Description: 收货地址
 */
@Entity
@Table(name = "address")
public class Address implements Serializable {
    private Long addressId;
    private User user;
    private String receiver;//收货人
    private String receiverPhone;//收货人手机号
    private String expressAddress;//快递目的地址
    private Boolean userful;//是否可用
    private Boolean def;//是否为默认
    private Date createTime;

    public Address() {
    }

    public Address(Long addressId, User user, String receiver, String receiverPhone, String expressAddress, Boolean userful, Boolean def, Date createTime) {
        this.addressId = addressId;
        this.user = user;
        this.receiver = receiver;
        this.receiverPhone = receiverPhone;
        this.expressAddress = expressAddress;
        this.userful = userful;
        this.def = def;
        this.createTime = createTime;
    }

    @Id
    @GeneratedValue
    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "userId")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getExpressAddress() {
        return expressAddress;
    }

    public void setExpressAddress(String expressAddress) {
        this.expressAddress = expressAddress;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getUserful() {
        return userful;
    }

    public void setUserful(Boolean userful) {
        this.userful = userful;
    }

    public Boolean getDef() {
        return def;
    }

    public void setDef(Boolean def) {
        this.def = def;
    }

    @Override
    public String toString() {
        return "Address{" +
                "addressId=" + addressId +
                ", user=" + user +
                ", receiver='" + receiver + '\'' +
                ", receiverPhone='" + receiverPhone + '\'' +
                ", expressAddress='" + expressAddress + '\'' +
                ", userful=" + userful +
                ", def=" + def +
                ", createTime=" + createTime +
                '}';
    }
}
