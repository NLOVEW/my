package com.linghong.my.bean;

import java.io.Serializable;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/7 19:59
 * @Version 1.0
 * @Description: uu跑腿计算订单价格 返回的数据
 */
public class UUPTPrice extends UUPT implements Serializable {
    private String price_token;//金额令牌，提交订单前必须先计算价格
    private String total_money;//订单总金额（优惠前）
    private String need_paymoney;//实际需要支付金额
    private String total_priceoff;//总优惠金额
    private String distance;//配送距离（单位：米）
    private String freight_money;//跑腿费
    private String couponid;//优惠券ID
    private String coupon_amount;//优惠券金额
    private String addfee;//加价金额
    private String goods_insurancemoney;//商品保价金额
    private String expires_in;//Token过期时间

    public String getPrice_token() {
        return price_token;
    }

    public void setPrice_token(String price_token) {
        this.price_token = price_token;
    }

    public String getTotal_money() {
        return total_money;
    }

    public void setTotal_money(String total_money) {
        this.total_money = total_money;
    }

    public String getNeed_paymoney() {
        return need_paymoney;
    }

    public void setNeed_paymoney(String need_paymoney) {
        this.need_paymoney = need_paymoney;
    }

    public String getTotal_priceoff() {
        return total_priceoff;
    }

    public void setTotal_priceoff(String total_priceoff) {
        this.total_priceoff = total_priceoff;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getFreight_money() {
        return freight_money;
    }

    public void setFreight_money(String freight_money) {
        this.freight_money = freight_money;
    }

    public String getCouponid() {
        return couponid;
    }

    public void setCouponid(String couponid) {
        this.couponid = couponid;
    }

    public String getCoupon_amount() {
        return coupon_amount;
    }

    public void setCoupon_amount(String coupon_amount) {
        this.coupon_amount = coupon_amount;
    }

    public String getAddfee() {
        return addfee;
    }

    public void setAddfee(String addfee) {
        this.addfee = addfee;
    }

    public String getGoods_insurancemoney() {
        return goods_insurancemoney;
    }

    public void setGoods_insurancemoney(String goods_insurancemoney) {
        this.goods_insurancemoney = goods_insurancemoney;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }
}
