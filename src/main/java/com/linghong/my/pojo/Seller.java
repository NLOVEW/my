package com.linghong.my.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/7 09:27
 * @Version 1.0
 * @Description:  商家
 */
@Entity
@Table(name = "seller")
public class Seller implements Serializable {
    private Long sellerId;
    private String mobilePhone;
    private String password;
    private String userName;
    private String avatar;
    private String idCardNumber;//法人代表身份证号
    private String idCardPath;
    private String sellerName;//商家店铺名
    private String businessImage;//店铺照片
    private String businessLicense;//营业执照
    private String city;//所在城市 必填
    private String address;//详细地址
    private String companyType;//公司类型
    private Date startTime;//开始营业时间
    private Date endTime;//结束营业时间
    private String introduce;//公司简介
    private Boolean businessStatus;//营业状态
    private Boolean auth;//是否认证
    private Date createTime;//创建时间

    @Id
    @GeneratedValue
    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
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

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public Boolean getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(Boolean businessStatus) {
        this.businessStatus = businessStatus;
    }

    public Boolean getAuth() {
        return auth;
    }

    public void setAuth(Boolean auth) {
        this.auth = auth;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getBusinessImage() {
        return businessImage;
    }

    public void setBusinessImage(String businessImage) {
        this.businessImage = businessImage;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIdCardPath() {
        return idCardPath;
    }

    public void setIdCardPath(String idCardPath) {
        this.idCardPath = idCardPath;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Seller{" +
                "sellerId=" + sellerId +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", password='" + password + '\'' +
                ", userName='" + userName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", idCardNumber='" + idCardNumber + '\'' +
                ", idCardPath='" + idCardPath + '\'' +
                ", sellerName='" + sellerName + '\'' +
                ", businessImage='" + businessImage + '\'' +
                ", businessLicense='" + businessLicense + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", companyType='" + companyType + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", introduce='" + introduce + '\'' +
                ", businessStatus=" + businessStatus +
                ", auth=" + auth +
                ", createTime=" + createTime +
                '}';
    }
}
