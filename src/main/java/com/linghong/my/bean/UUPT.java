package com.linghong.my.bean;

import java.io.Serializable;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/8 10:29
 * @Version 1.0
 * @Description: 抽取返回的的公共参数
 */
public class UUPT implements Serializable {
    private String origin_id;//第三方对接平台订单id
    private String nonce_str;//随机字符串，不长于32位
    private String sign;//加密签名，详情见消息体签名算法
    private String appid;//第三方用户唯一凭证
    private String return_msg;//返回信息，如非空，为错误原因，如签名失败、参数格式校验错误
    private String return_code;//状态，ok/fail表示成功

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getOrigin_id() {
        return origin_id;
    }

    public void setOrigin_id(String origin_id) {
        this.origin_id = origin_id;
    }
}
