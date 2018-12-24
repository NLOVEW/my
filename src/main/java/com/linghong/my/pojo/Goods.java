package com.linghong.my.pojo;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/7 14:34
 * @Version 1.0
 * @Description:  商品
 */
@Entity
@Table(name = "goods")
public class Goods implements Serializable {
    private String goodsId;
    private Seller seller;
    private String title;//标题
    private String goodsType;//类型
    private BigDecimal price;//价格
    private String introduce;//介绍
    private Integer number;//数量
    private Integer salesVolume;//销量
    private Set<Image> images;//图片
    private Integer status;//状态 0上架 1下架 2删除
    private Date updateTime;
    private Date createTime;

    @Id
    @Column(name = "goodsId",length = 32,unique = true)
    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sellerId")
    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "goodsId")
    public Set<Image> getImages() {
        return images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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

    public Integer getSalesVolume() {
        return salesVolume;
    }

    public void setSalesVolume(Integer salesVolume) {
        this.salesVolume = salesVolume;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "goodsId='" + goodsId + '\'' +
                ", seller=" + seller +
                ", title='" + title + '\'' +
                ", goodsType='" + goodsType + '\'' +
                ", price='" + price + '\'' +
                ", introduce='" + introduce + '\'' +
                ", number=" + number +
                ", images=" + images +
                ", status=" + status +
                ", updateTime=" + updateTime +
                ", createTime=" + createTime +
                '}';
    }
}
