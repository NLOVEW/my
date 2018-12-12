package com.linghong.my.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linghong.my.bean.UUPTOrder;
import com.linghong.my.bean.UUPTPrice;
import com.linghong.my.bean.UUPTWallet;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/7 19:47
 * @Version 1.0
 * @Description: uu跑腿工具类
 */
public class UuPaoTuiUtil {
    private Logger logger  = LoggerFactory.getLogger(getClass());
    private OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(chain -> {
        Request request = chain.request().newBuilder().header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                .build();
        return chain.proceed(request);
    }).build();

    /**
     *  对uu跑腿发起计算配送费请求
     *
     * @param orderId         第三方对接平台订单id
     * @param formAddress     起始地址
     * @param toAddress       目的地址
     * @param city            订单所在城市名 称(如郑州市就填”郑州市“，必须带上“市”)
     * @param sign            加密签名，详情见 消息体签名算法
     * @param openId          用户openid,详情见 获取openid接口
     * @param appId           appid
     * @return
     */
    public UUPTPrice getPrice(String orderId,String formAddress,String toAddress,String city,String sign,String openId,String appId,String appKey){
        try {
            Map<String,String> parameters = new HashMap<>();
            parameters.put("origin_id", orderId);
            parameters.put("from_address", formAddress);
            parameters.put("to_address", toAddress);
            parameters.put("city_name", city);
            parameters.put("send_type", "0");
            parameters.put("to_lat", "0");
            parameters.put("to_lng", "0");
            parameters.put("from_lat", "0");
            parameters.put("from_lng", "0");
            parameters.put("timestamp", Instant.now().getEpochSecond()+"");
            parameters.put("nonce_str", IDUtil.getId());
            parameters.put("openid", orderId);
            parameters.put("appid", orderId);
            parameters.put("sign",UUPTSignUtil.CreateMd5Sign(parameters, appKey) );
            FormBody.Builder builder = new FormBody.Builder();
            parameters.forEach((key, value) -> {
                    builder.add(key,value );
            });
            FormBody body = builder.build();
            Request request = new Request.Builder().url("http://openapi.uupaotui.com/v2_0/getorderprice.ashx").post(body).build();
            Response response = okHttpClient.newCall(request).execute();
            String result = response.body().string();
            logger.info("请求UU跑腿计算价格的结果：{}",request);
            UUPTPrice uuptPrice = JSON.parseObject(result, UUPTPrice.class);
            return uuptPrice;
        }catch (Exception e){
            logger.error("请求UU跑腿计算价格出错");
            return null;
        }
    }

    /**
     * 发布订单
     * @param priceToken      金额令牌，计算订单价格接口返回的price_token
     * @param orderPrice       订单金额，计算订单价格接口返回的total_money
     * @param balancePaymoney 实际余额支付金额计算订单价格接口返回的need_paymoney
     * @param receiver         收件人
     * @param receiver_phone   收件人电话 手机号码； 虚拟号码格式（手机号_分机号码）例如：13700000000_1111
     * @param note              订单备注 最长140个汉字
     * @param specialType         特殊处理类型，是否需要保温箱 1需要 0不需要
     * @param callmeWithtake     取件是否给我打电话 1需要 0不需要
     * @param pubusermobile     发件人电话，（如果为空则是用户注册的手机号）
     * @param openId        用户openid,详情见 获取openid接口
     * @param appId             appid
     * @param appKey
     * @return
     */
    public UUPTOrder pushOrder(String priceToken,String orderPrice,String balancePaymoney,String receiver,String receiver_phone,String note,String specialType,String callmeWithtake,String pubusermobile,String openId,String appId,String appKey){
        try {
            Map<String,String> parameters = new HashMap<>();
            parameters.put("price_token", priceToken);
            parameters.put("order_price", orderPrice);
            parameters.put("balance_paymoney", balancePaymoney);
            parameters.put("receiver", receiver);
            parameters.put("receiver_phone", receiver_phone);
            parameters.put("note", priceToken);
            parameters.put("push_type", "0");
            parameters.put("special_type", "1");
            parameters.put("callme_withtake", "1");
            parameters.put("openid", openId);
            parameters.put("appid", appId);
            parameters.put("timestamp", Instant.now().getEpochSecond()+"");
            parameters.put("nonce_str", IDUtil.getId());
            parameters.put("sign", UUPTSignUtil.CreateMd5Sign(parameters,appKey ));
            FormBody.Builder builder = new FormBody.Builder();
            parameters.forEach((key, value)->{
                builder.add(key,value );
            });
            FormBody body = builder.build();
            Request request = new Request.Builder()
                    .url("http://openapi.uupaotui.com/v2_0/addorder.ashx")
                    .post(body)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            String result = response.body().string();
            UUPTOrder uuptOrder = JSON.parseObject(result, UUPTOrder.class);
            return uuptOrder;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param orderCode   UU跑腿订单编号，order_code
     * @param reason      取消原因
     * @param openId        用户openid,详情见 获取openid接口
     * @param appId       appid
     * @param appKey
     * @return
     */
    public UUPTOrder cancelOrder(String orderCode,String reason,String openId,String appId,String appKey){
        try {
            Map<String,String> parameters = new HashMap<>();
            parameters.put("order_code", orderCode);
            parameters.put("reason", reason);
            parameters.put("openid", openId);
            parameters.put("appid", appId);
            parameters.put("timestamp", Instant.now().getEpochSecond()+"");
            parameters.put("nonce_str", IDUtil.getId());
            parameters.put("sign", UUPTSignUtil.CreateMd5Sign(parameters,appKey ));
            FormBody.Builder builder = new FormBody.Builder();
            parameters.forEach((key, value)->{
                builder.add(key,value );
            });
            FormBody body = builder.build();
            Request request = new Request.Builder()
                    .url("http://openapi.uupaotui.com/v2_0/cancelorder.ashx")
                    .post(body)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            String result = response.body().string();
            UUPTOrder cancelOrder = JSON.parseObject(result, UUPTOrder.class);
            return cancelOrder;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取账户余额
     * @param openId
     * @param appId
     * @param appKey
     * @return
     */
    public UUPTWallet getWallet(String openId, String appId, String appKey){
        try {
            Map<String,String> parameters = new HashMap<>();
            parameters.put("user_openid", openId);
            parameters.put("openid", openId);
            parameters.put("appid", appId);
            parameters.put("timestamp", Instant.now().getEpochSecond()+"");
            parameters.put("nonce_str", IDUtil.getId());
            parameters.put("sign", UUPTSignUtil.CreateMd5Sign(parameters,appKey ));
            FormBody.Builder builder = new FormBody.Builder();
            parameters.forEach((key, value)->{
                builder.add(key,value );
            });
            FormBody body = builder.build();
            Request request = new Request.Builder()
                    .url("http://openapi.uupaotui.com/v2_0/getbalancedetail.ashx")
                    .post(body)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            String result = response.body().string();
            UUPTWallet uuptWallet = JSON.parseObject(result, UUPTWallet.class);
            return uuptWallet;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取订单详情
     * @param orderCode
     * @param openId
     * @param appId
     * @param appKey
     * @return
     */
    public JSONObject getOrderDetail(String orderCode,String openId,String appId,String appKey){
        try {
            Map<String,String> parameters = new HashMap<>();
            parameters.put("order_code", orderCode);
            parameters.put("openid", openId);
            parameters.put("appid", appId);
            parameters.put("timestamp", Instant.now().getEpochSecond()+"");
            parameters.put("nonce_str", IDUtil.getId());
            parameters.put("sign", UUPTSignUtil.CreateMd5Sign(parameters,appKey ));
            FormBody.Builder builder = new FormBody.Builder();
            parameters.forEach((key, value)->{
                builder.add(key,value );
            });
            FormBody body = builder.build();
            Request request = new Request.Builder()
                    .url("http://openapi.uupaotui.com/v2_0/getorderdetail.ashx")
                    .post(body)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            String result = response.body().string();
            return JSON.parseObject(result);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取开通的城市列表
     * @param appId
     * @param openId
     * @param appKey
     * @return
     */
    public JSONObject getCity(String appId,String openId,String appKey){
        try {
            Map<String,String> parameters = new HashMap<>();
            parameters.put("openid", openId);
            parameters.put("appid", appId);
            parameters.put("timestamp", Instant.now().getEpochSecond()+"");
            parameters.put("nonce_str", IDUtil.getId());
            parameters.put("sign", UUPTSignUtil.CreateMd5Sign(parameters,appKey ));
            FormBody.Builder builder = new FormBody.Builder();
            parameters.forEach((key, value)->{
                builder.add(key,value );
            });
            FormBody body = builder.build();
            Request request = new Request.Builder()
                    .url("http://openapi.uupaotui.com/v2_0/getcitylist.ashx")
                    .post(body)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            String result = response.body().string();
            return JSON.parseObject(result);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获得充值地址
     * @param openId
     * @param appId
     * @param appKey
     * @return
     */
    public JSONObject getRecharge(String openId,String appId,String appKey){
        try {
            Map<String,String> parameters = new HashMap<>();
            parameters.put("user_openid", openId);
            parameters.put("openid", openId);
            parameters.put("appid", appId);
            parameters.put("timestamp", Instant.now().getEpochSecond()+"");
            parameters.put("nonce_str", IDUtil.getId());
            parameters.put("sign", UUPTSignUtil.CreateMd5Sign(parameters,appKey ));
            FormBody.Builder builder = new FormBody.Builder();
            parameters.forEach((key, value)->{
                builder.add(key,value );
            });
            FormBody body = builder.build();
            Request request = new Request.Builder()
                    .url("http://openapi.uupaotui.com/v2_0/getrecharge.ashx")
                    .post(body)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            String result = response.body().string();
            return JSON.parseObject(result);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }





    public static void main(String[] args) {

    }
}
