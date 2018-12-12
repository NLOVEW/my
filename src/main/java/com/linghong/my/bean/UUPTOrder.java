package com.linghong.my.bean;

import java.io.Serializable;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/8 10:38
 * @Version 1.0
 * @Description:  UU跑腿下订单
 */
public class UUPTOrder extends UUPT implements Serializable {
    private String ordercode; //发布订单时返回的订单号
    private String order_code; //取消订单时返回的订单号

    public String getOrdercode() {
        return ordercode;
    }

    public void setOrdercode(String ordercode) {
        this.ordercode = ordercode;
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }
}
