package com.linghong.my.utils.uupt;

import com.alibaba.fastjson.JSONObject;
import com.linghong.my.utils.IDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/8 12:01
 * @Version 1.0
 * @Description:
 */
public class UUPTUtil {
    private static Logger logger = LoggerFactory.getLogger(UUPTUtil.class);

    /**
     * 发布订单
     * @param priceToken      金额令牌，计算订单价格接口返回的price_token
     * @param orderPrice       订单金额，计算订单价格接口返回的total_money
     * @param balancePaymoney 实际余额支付金额计算订单价格接口返回的need_paymoney
     * @param receiver         收件人
     * @param receiver_phone   收件人电话 手机号码； 虚拟号码格式（手机号_分机号码）例如：13700000000_1111
     * @param note              订单备注 最长140个汉字
     * @param pubuserMobile     发件人电话，（如果为空则是用户注册的手机号）
     * @param openId        用户openid,详情见 获取openid接口
     * @param appId             appid
     * @param appKey
     * @return
     */
    public static JSONObject pushOrder(String priceToken,String orderPrice,String balancePaymoney,
                                       String receiver,String receiver_phone,String note,
                                       String pubuserMobile,String openId,
                                       String appId,String appKey,
                                       String backCallUrl) {
        Dictionary<String, String> mydic = new Dictionary<String, String>();
        mydic.add("appid", appId);
        mydic.add("nonce_str", UUCommonFun.NewGuid());
        mydic.add("timestamp", UUCommonFun.getTimeStamp());
        mydic.add("openid", openId);
        mydic.add("price_token", priceToken);
        mydic.add("order_price", orderPrice);
        mydic.add("balance_paymoney", balancePaymoney);
        mydic.add("receiver", receiver);
        mydic.add("receiver_phone", receiver_phone);
       // mydic.add("note",note);
        //mydic.add("callback_url", backCallUrl);
        mydic.add("push_type", "2");
        mydic.add("special_type", "1");
        mydic.add("callme_withtake", "1");
        mydic.add("pubUserMobile", pubuserMobile);
        mydic.add("sign", UUCommonFun.CreateMd5Sign(mydic,appKey));
        String result = UUHttpRequestHelper.HttpPost(ApiConfig.AddOrderUrl, mydic);
        logger.info(result);
        return JSONObject.parseObject(result);
    }

    /**
     *
     * @param orderCode
     * @param reason
     * @param openId
     * @param appId
     * @param appKey
     * @return
     */
    public static JSONObject cancelOrder(String orderCode,String reason,String openId,String appId,String appKey) {
        Dictionary<String, String> mydic = new Dictionary<String, String>();
        mydic.add("appid", appId);
        mydic.add("nonce_str", UUCommonFun.NewGuid());
        mydic.add("timestamp", UUCommonFun.getTimeStamp());
        mydic.add("openid", openId);
        mydic.add("order_code", orderCode);
        mydic.add("reason", reason);
        mydic.add("sign", UUCommonFun.CreateMd5Sign(mydic, appKey));
        String result = UUHttpRequestHelper.HttpPost(ApiConfig.CancelOrderUrl, mydic);
        return JSONObject.parseObject(result);
    }

    /**
     * 获取订单详情
     * @param orderCode
     * @param openId
     * @param appId
     * @param appKey
     * @return
     */
    public static JSONObject getOrderDetail(String orderCode,String openId,String appId,String appKey) {
        Dictionary<String, String> mydic = new Dictionary<String, String>();
        mydic.add("appid",appId);
        mydic.add("nonce_str", UUCommonFun.NewGuid());
        mydic.add("timestamp", UUCommonFun.getTimeStamp());
        mydic.add("openid", openId);
        mydic.add("order_code", orderCode);
        mydic.add("sign", UUCommonFun.CreateMd5Sign(mydic, appKey));
        String result = UUHttpRequestHelper.HttpPost(ApiConfig.GetOrderDetailUrl, mydic);
        return JSONObject.parseObject(result);
    }


    /**
     *  对uu跑腿发起计算配送费请求
     *
     * @param originId         第三方对接平台订单id
     * @param formAddress     起始地址
     * @param toAddress       目的地址
     * @param city            订单所在城市名 称(如郑州市就填”郑州市“，必须带上“市”)
     * @param openId          用户openid,详情见 获取openid接口
     * @param appId           appid
     * @return
     */
    public static JSONObject getOrderPrice(String originId,String formAddress,String toAddress,String city,String openId,String appId,String appKey) {
        Dictionary<String, String> mydic = new Dictionary<>();
        mydic.add("origin_id", originId);
        logger.info("origin_id:{}",mydic.get("origin_id"));
        mydic.add("appid", appId);
        logger.info("appid:{}",mydic.get("appid"));
        mydic.add("nonce_str", UUCommonFun.NewGuid());
        logger.info("nonce_str:{}",mydic.get("nonce_str"));
        mydic.add("timestamp", UUCommonFun.getTimeStamp());
        logger.info("timestamp:{}",mydic.get("timestamp"));
        mydic.add("openid", openId);
        logger.info("openid:{}",mydic.get("openid"));
        mydic.add("send_type", "0");
        logger.info("send_type:{}",mydic.get("send_type"));
        mydic.add("from_address", formAddress);
        logger.info("from_address:{}",mydic.get("from_address"));
        mydic.add("to_address", toAddress);
        logger.info("to_address:{}",mydic.get("to_address"));
        mydic.add("city_name", city);
        logger.info("city_name:{}",mydic.get("city_name"));
        mydic.add("from_lng", "0");
        mydic.add("from_lat", "0");
        mydic.add("to_lng", "0");
        mydic.add("to_lat", "0");
        mydic.add("sign", UUCommonFun.CreateMd5Sign(mydic, appKey));
        logger.info("sign:{}",mydic.get("sign"));
        String result = UUHttpRequestHelper.HttpPost(ApiConfig.GetOrderPriceUrl, mydic);
        return JSONObject.parseObject(result);
    }

    public static void main(String[] args) {
        JSONObject orderPrice = getOrderPrice(IDUtil.getId(), "郑州市新郑市龙湖镇中原工学院", "郑州市管城区黄河科技学院", "郑州市", "e03b337029204ad6959c3f1caf17c278", "7bf70045911c4980be2df525c2074a52", "3bb3d2a2900f4b85a9cd8aef6c99175d");
        System.out.println(orderPrice);
        JSONObject jsonObject = pushOrder((String) orderPrice.get("price_token"), (String) orderPrice.get("total_money"), (String) orderPrice.get("need_paymoney"), "某某某", "13592589109", "请快速", "13592589109", "e03b337029204ad6959c3f1caf17c278", "7bf70045911c4980be2df525c2074a52", "3bb3d2a2900f4b85a9cd8aef6c99175d", null);
        System.out.println(jsonObject);
    }

}
