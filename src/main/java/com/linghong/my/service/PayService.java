package com.linghong.my.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linghong.my.pojo.*;
import com.linghong.my.repository.*;
import com.linghong.my.utils.DateUtil;
import com.linghong.my.utils.IDUtil;
import com.linghong.my.utils.uupt.UUPTUtil;
import com.nhb.pay.alipay.AliPayConfig;
import com.nhb.pay.alipay.AliPayService;
import com.nhb.pay.alipay.AliTransactionType;
import com.nhb.pay.alipay.AliTransferResult;
import com.nhb.pay.common.bean.PayOrder;
import com.nhb.pay.common.bean.TransferOrder;
import com.nhb.pay.common.http.HttpConfig;
import com.nhb.pay.common.type.MethodType;
import com.nhb.pay.common.utils.SignUtils;
import com.nhb.pay.wxpay.WxPayConfig;
import com.nhb.pay.wxpay.WxPayService;
import com.nhb.pay.wxpay.WxTransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/13 10:34
 * @Version 1.0
 * @Description:
 */
@Service
@Scope("prototype")
@Transactional(rollbackOn = Exception.class)
public class PayService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static AliPayService aliPayService = null;
    private static WxPayService wxPayService = null;
    private static AliPayConfig aliPayConfig = new AliPayConfig();
    private static WxPayConfig wxPayConfig = new WxPayConfig();
    private static HttpConfig httpConfig = new HttpConfig();

    @Resource
    private BillRepository billRepository;
    @Resource
    private WalletRepository walletRepository;
    @Resource
    private RedisService redisService;
    @Resource
    private UserRepository userRepository;
    @Resource
    private GoodsRepository goodsRepository;
    @Resource
    private GoodsOrderRepository goodsOrderRepository;
    @Resource
    private AddressRepository addressRepository;

    private Timer timer;

    @Value("{uupt.appId}")
    private String uuptAppId;
    @Value("{uupt.appKey}")
    private String uuptAppKey;
    @Value("{uupt.openId}")
    private String uuptOpenId;

    /**
     * 初始化支付信息
     */
    @PostConstruct
    public void init() {
        //todo 上线时 修改所有信息 支付宝配置文件-----------------------------------
        aliPayConfig.setPid("2088331069315784");
        aliPayConfig.setAppId("2018111462154440");
        aliPayConfig.setKeyPublic("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArOaW8g4J5cSQC6gvcSVfDj28YzQbRO0c7RWSRcWvFWFf+1ZdMdp6Za5aYWLSDsVeb9GOXCP2sDXd4cGn3PARxBtZBB1du/lqmk/vnNhaN5VHInn2lqXELb2+V2831yM9H4a/OLNmXxc5hepe9dG+pkac5rxCV6RAd3DPMQ7IbXhb0ZHgppRwooklNTFI3Kr/p5KLTzcTcXS1itSDBKf6BWufltYJPieMH5pR0WKCYLOqvLWxsIM/FcT57L6b2oxX1t0ze6/ghICXkDdyZ3niTLjsZgER5x9W5GjEbKzsK1TIcCzPEFkTv2qW/02E/k7Rxpk8EK7hfTAeWu3XOj8+VQIDAQAB");
        aliPayConfig.setKeyPrivate("MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDWchQOMq6xi+M5" +
                "dqKPVDgRumWbGiqCdvXGwj/NFMN/GoKlS7ZllVPsNpMjAXPsOrvUgFYMGddeZshW" +
                "rMfAi4gzuiwDLrGP5u3Rpk4xoq8I6VSC8y7Db9sKnPvx/pUYXvWZpRghAuAz4wx2" +
                "g3DeV1uxnE4Xt2C2DodDslR+28Rd5w4PvWOF+P3vK++kdG3dJg93uHMRvAiWwbN8" +
                "oApdCob1epwKMGQ8L5QLIJHIPEtXwxnDV/ESuc3Gisgxx5ZGcLdMogCCMA0pk/S6" +
                "NnsnKHPmSrHCjzO82ViOpFeifIoKCdeT6kDQSsJxRAEl99WEkXLzjpYBb7+v6zEd" +
                "sWgn16HxAgMBAAECggEAK+fLZ2TFE6DacudxPPs3R5nsN6dR0MheZcVbnreDl0Bs" +
                "Qz+PJpk+R3yc/2vKujEER+vGsk+QIsnUdrqFY0yuJDkXzcb/n8DeLDBqjgsK3z42" +
                "iWEUz/rU1AV0KAXBrO20RzLgD9Iw6S6xXIpoz3z/Twv7iSuIZ33t/9RBLH/+YmMO" +
                "CiyUkh1MbWb4IsdBc+axApY31wXx/t0GhbP8L1W2vkBwKVEQVhMk88gUGdR9493C" +
                "nHSA26b5p5lzJSvKeDDWZ3Q3wFF65XL6FYQzqZP/LG/Fgk6Jma+5YSoyQmhpYi83" +
                "L9yhwNhP4puWYu1GUkVJ6BNCLlNpgR0rT6EV3ws96QKBgQD4GUNou1gPkwYe+ABb" +
                "z2Jp82FCwvluuQyiigwPZkqoJ+77gzHb5r0Iqo+N4t0ivnd4CODAnF5g4FvOiaxS" +
                "9gNsKJplAmqIxeqL0Y4gFrWVm5Q0XZE/JuZmlDGLQH/FoAi12tXibGrd7lUwoV+8" +
                "KoAM4TrleQUbN4mwQz1wI/YzhwKBgQDdRnF0znwsxcec0nhW+49t71l4U2GfdbVE" +
                "yjxVkC776vsKJwrdNoLuwqZ2S2TjOKYOb4CqX4rBEGcq1IETFUu0EaA3GJHUFUAG" +
                "8tcdVEyyP+VswZmQdKZ09Ox0wttiG8ULm+435iTKn3whtSzj5s0ODc1U2Q0yjzl3" +
                "wa5cD+rMxwKBgQCLHT9bHpGGSh6Ihu9Mi8DXQA0tbj6HA2Q+T8HrcKQ0HhA3H515" +
                "fKxKi7jrCmaM8Nf0iqXb2tJg5+0SpsflzOSmZS4NrYknIDMgK2TSQWmYdiBoLH8h" +
                "NiLDKh2Nw6Dho5a+wfJkf/58awOBvTr9O6eJkVGBEpb2Z/Hg0BNHKiMHSQKBgQCG" +
                "k9hfNVJanLOJ0ow0Qu157E+bGgNOy3VC8Ej/tSCOQN0L0LEP156MfkBlw/cJJyP/" +
                "tZsog9FNGJ/WccZLB/GyA+JQgBX5Si9Vyo5AnUvEQY5Ute6i5/9xNKE3ZmetZLxU" +
                "EjMxNjz8K0GA8sLpnS7rtENErnoTXP6Tsm1MOrYQcwKBgQDYFNeJTFsvWz4aKrDM" +
                "PyYOcRXmmLmJ+m+tfDqZlqIlhnJETphLh5C9GtonWiuUCd+B+o3jB8TAHXa99URj" +
                "fXJQSIobhcTLcY+kJJJWOOZrdOOcp4+dO2hlXzhIuss8lol4PWNgUVjwbzqJoaZX" +
                "urDND+ZLTqeVwIxfNAO/nLRNSw==");
        aliPayConfig.setNotifyUrl("www.ddqm365.com:9796/pay/aliPayCallBack");
        //aliPayConfig.setReturnUrl("http://2509d113.all123.net/aliPayBack");
        aliPayConfig.setSignType(SignUtils.RSA2.name());
        aliPayConfig.setSeller("2088102175938099");
        aliPayConfig.setInputCharset("utf-8");
        //是否为测试账号，沙箱环境
        aliPayConfig.setTest(false);
        //最大连接数
        httpConfig.setMaxTotal(20);
        //默认的每个路由的最大连接数
        httpConfig.setDefaultMaxPerRoute(10);
        aliPayService = new AliPayService(aliPayConfig, httpConfig);

        //todo 微信配置文件
        wxPayConfig.setMchId("1473426802");
        wxPayConfig.setAppid("wx8397f8696b538317");
        wxPayConfig.setKeyPublic("T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");
        wxPayConfig.setSecretKey("T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");
        wxPayConfig.setNotifyUrl("异步通知地址");
        wxPayConfig.setReturnUrl("同步通知地址");
        wxPayConfig.setSignType(SignUtils.MD5.name());
        wxPayConfig.setInputCharset("utf-8");
        wxPayService = new WxPayService(wxPayConfig);
    }


    public List<Bill> getBillsByUserId(Long userId) {
        return billRepository.findAllByUser_UserId(userId);
    }


    public List<Bill> getBillsBySellerId(Long sellerId) {
        return billRepository.findAllBySeller_SellerId(sellerId);
    }

    public Bill getDetailBill(Long billId) {
        return billRepository.findById(billId).get();
    }

    public Wallet getWallet(Long userId) {
        Wallet wallet = walletRepository.findByUser_UserId(userId);
        return wallet;
    }

    public String aliRecharge(Long userId, BigDecimal price) {
        PayOrder payOrder = new PayOrder("充值", "充值", price, IDUtil.getOrderId(), AliTransactionType.WAP);
        User user = userRepository.findById(userId).get();
        Map<String, Object> orderInfo = aliPayService.orderInfo(payOrder);
        Bill bill = new Bill();
        bill.setOutTradeNo(payOrder.getOutTradeNo());
        bill.setPrice(payOrder.getPrice());
        bill.setType(1);
        bill.setIntroduce("通过支付宝  充值" + bill.getPrice() + "元");
        bill.setTime(new Date());
        bill.setUser(user);
        redisService.set(payOrder.getOutTradeNo(), bill);
        return aliPayService.buildRequest(orderInfo, MethodType.POST);
    }

    public boolean aliRechargeCallBack(HttpServletRequest request) {
        //获取支付方返回的对应参数
        Map<String, Object> params = null;
        try {
            params = aliPayService.getParameter2Map(request.getParameterMap(), request.getInputStream());
            if (null == params) {
                return false;
            }
            //校验
            if (aliPayService.verify(params)) {
                String outTradeNo = (String) params.get("out_trade_no");
                Bill bill = (Bill) redisService.get(outTradeNo);
                if (bill != null) {
                    Wallet wallet = walletRepository.findByUser_UserId(bill.getUser().getUserId());
                    wallet.setUser(bill.getUser());
                    wallet.setUpdateTime(new Date());
                    wallet.setBalance(wallet.getBalance().add(bill.getPrice()));
                    billRepository.save(bill);
                    redisService.del(outTradeNo);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String wxRecharge(Long userId,
                             BigDecimal price,
                             HttpServletRequest request) {
        PayOrder payOrder = new PayOrder("钱包充值", "钱包充值", price, IDUtil.getOrderId(), WxTransactionType.MWEB);
        User user = userRepository.findById(userId).get();
        StringBuffer requestURL = request.getRequestURL();
        //设置网页地址
        payOrder.setWapUrl(requestURL.substring(0, requestURL.indexOf("/") > 0 ?
                requestURL.indexOf("/") : requestURL.length()));
        payOrder.setWapName("钱包充值");
        Map<String, Object> orderInfo = wxPayService.orderInfo(payOrder);
        Bill bill = new Bill();
        bill.setType(1);
        bill.setOutTradeNo(payOrder.getOutTradeNo());
        bill.setIntroduce("通过微信充值 " + price + " 元");
        bill.setTime(new Date());
        bill.setUser(user);
        redisService.set(payOrder.getOutTradeNo(), bill);
        return wxPayService.buildRequest(orderInfo, MethodType.POST);
    }


    public boolean wxRechargeCallBack(HttpServletRequest request) {
        //获取支付方返回的对应参数
        Map<String, Object> params = null;
        try {
            params = wxPayService.getParameter2Map(request.getParameterMap(), request.getInputStream());
            if (null == params) {
                return false;
            }
            //校验
            if (wxPayService.verify(params)) {
                String outTradeNo = (String) params.get("out_trade_no");
                Bill bill = (Bill) redisService.get(outTradeNo);
                if (bill != null) {
                    Wallet wallet = walletRepository.findByUser_UserId(bill.getUser().getUserId());
                    wallet.setUser(bill.getUser());
                    wallet.setUpdateTime(new Date());
                    wallet.setBalance(wallet.getBalance().add(bill.getPrice()));
                    billRepository.save(bill);
                    redisService.del(outTradeNo);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean aliTransfer(Long userId, TransferOrder transferOrder) {
        transferOrder.setOutNo(IDUtil.getOrderId());
        User user = userRepository.findById(userId).get();
        Wallet wallet = walletRepository.findByUser_UserId(user.getUserId());
        if (wallet.getBalance().compareTo(transferOrder.getAmount()) >= 0) {
            AliTransferResult transResult = JSON.parseObject(JSON.toJSONString(aliPayService.transfer(transferOrder)), AliTransferResult.class);
            if (transResult.getAlipay_fund_trans_toaccount_transfer_response().getCode().equals("10000")) {
                Bill bill = new Bill();
                bill.setType(4);
                bill.setUser(user);
                bill.setTime(new Date());
                bill.setOutTradeNo(transferOrder.getOutNo());
                bill.setPrice(transferOrder.getAmount());
                bill.setIntroduce("钱包余额转账到支付宝 " + bill.getPrice() + " 元");
                billRepository.save(bill);
                wallet.setBalance(wallet.getBalance().subtract(transferOrder.getAmount()));
                return true;
            }
        }
        return false;
    }


    /**
     * 支付先到平台   买家收货后、到一定时间后自动转到卖家账号
     *
     * @param orderId
     * @param price
     * @return
     */
    public String aliPay(String orderId, BigDecimal price) {
        PayOrder payOrder = new PayOrder("支付", "支付", price, orderId, AliTransactionType.WAP);
        Map<String, Object> orderInfo = aliPayService.orderInfo(payOrder);
        return aliPayService.buildRequest(orderInfo, MethodType.POST);
    }


    public boolean aliPayCallBack(HttpServletRequest request) {
        //获取支付方返回的对应参数
        Map<String, Object> params = null;
        try {
            params = aliPayService.getParameter2Map(request.getParameterMap(), request.getInputStream());
            if (null == params) {
                return false;
            }
            //校验
            if (aliPayService.verify(params)) {
                String outTradeNo = (String) params.get("out_trade_no");
                String result = (String) redisService.get("out_trade_no");
                if (result != null && !result.startsWith("[{")){
                    result = "["+result+"]";
                }
                List<GoodsOrder> goodsOrders = JSON.parseArray(result, GoodsOrder.class);
                GoodsOrder goodsOrder = goodsOrders.get(0);
                User user = goodsOrder.getUser();
                Bill bill = new Bill();
                bill.setOutTradeNo(outTradeNo);
                bill.setUser(user);
                bill.setType(0);
                bill.setTime(new Date());
                String price = (String) params.get("total_amount");
                bill.setPrice(new BigDecimal(price));
                bill.setIntroduce("支付宝支付 " + price + " 元");
                billRepository.save(bill);
                for (GoodsOrder temp : goodsOrders) {
                    temp.setStatus(0);
                    temp.getGoods().setSalesVolume(temp.getNumber().intValue()+temp.getGoods().getSalesVolume().intValue());
                    goodsOrderRepository.save(temp);
                }
                //uu跑腿下单
                //pushUUPTOrder(objects);
                pushUUPTOrder(goodsOrders);
                //支付完成后  10天没有退换/款  则自动付款给商家
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        List<Object> obs = redisService.lGet(outTradeNo, 0, -1);
                        for (Object object : obs ){
                            GoodsOrder goodsOrder = (GoodsOrder) object;
                            //先判断骑手已送达
                            if (goodsOrder.getStatus().equals(4) || goodsOrder.getStatus().equals(8)) {
                                //判断是否申请退换货 退款
                                if (goodsOrder.getBackGoods() == null) {
                                    TransferOrder transferOrder = new TransferOrder();
                                    transferOrder.setPayeeAccount(goodsOrder.getGoods().getSeller().getMobilePhone());
                                    transferOrder.setOutNo(IDUtil.getOrderId());
                                    transferOrder.setAmount(goodsOrder.getPrice());
                                    transferOrder.setRemark("商铺收入");
                                    AliTransferResult transResult = JSON.parseObject(JSON.toJSONString(aliPayService.transfer(transferOrder)), AliTransferResult.class);
                                    if (transResult.getAlipay_fund_trans_toaccount_transfer_response().getCode().equals("10000")) {
                                        Bill bill = new Bill();
                                        bill.setType(2);
                                        bill.setSeller(goodsOrder.getGoods().getSeller());
                                        bill.setTime(new Date());
                                        bill.setOutTradeNo(transferOrder.getOutNo());
                                        bill.setPrice(transferOrder.getAmount());
                                        bill.setIntroduce("商铺收入到支付宝账号：" + goodsOrder.getGoods().getSeller().getMobilePhone() + "   " + bill.getPrice() + " 元");
                                        billRepository.save(bill);
                                    }
                                }
                            }
                        }
                    }
                }, 10 * 24 * 60 * 60 * 1000);
                redisService.del(outTradeNo);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String wxPay(String orderId, BigDecimal price, HttpServletRequest request) {
        PayOrder payOrder = new PayOrder("支付", "支付", price, orderId, WxTransactionType.MWEB);
        StringBuffer requestURL = request.getRequestURL();
        //设置网页地址
        payOrder.setWapUrl(requestURL.substring(0, requestURL.indexOf("/") > 0 ?
                requestURL.indexOf("/") : requestURL.length()));
        payOrder.setWapName("支付");
        Map<String, Object> orderInfo = wxPayService.orderInfo(payOrder);
        return wxPayService.buildRequest(orderInfo, MethodType.POST);
    }

    public boolean wxPayCallBack(HttpServletRequest request) {
        //获取支付方返回的对应参数
        Map<String, Object> params = null;
        try {
            params = wxPayService.getParameter2Map(request.getParameterMap(), request.getInputStream());
            if (null == params) {
                return false;
            }
            //校验
            if (wxPayService.verify(params)) {
                String outTradeNo = (String) params.get("out_trade_no");
                List<Object> objects = redisService.lGet(outTradeNo, 0, -1);
                Object ob = objects.get(0);
                GoodsOrder goodsOrder = (GoodsOrder) ob;
                User user = goodsOrder.getUser();
                Bill bill = new Bill();
                bill.setOutTradeNo(outTradeNo);
                bill.setUser(user);
                bill.setType(0);
                bill.setTime(new Date());
                String price = (String) params.get("total_amount");
                bill.setPrice(new BigDecimal(price));
                bill.setIntroduce("微信支付 " + price + " 元");
                billRepository.save(bill);
                for (Object o : objects) {
                    GoodsOrder goodsOrder1 = (GoodsOrder) o;
                    goodsOrder1.setStatus(0);
                    goodsOrderRepository.save(goodsOrder1);
                }
                //uu跑腿下单
               // pushUUPTOrder();
                //支付完成后  10天没有退换/款  则自动付款给商家
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        List<Object> obs = redisService.lGet(outTradeNo, 0, -1);
                        for (Object object : obs ){
                            GoodsOrder goodsOrder = (GoodsOrder) object;
                            //先判断骑手已送达
                            if (goodsOrder.getStatus().equals(4) || goodsOrder.getStatus().equals(8)) {
                                //判断是否申请退换货 退款
                                if (goodsOrder.getBackGoods() == null) {
                                    TransferOrder transferOrder = new TransferOrder();
                                    transferOrder.setPayeeAccount(goodsOrder.getGoods().getSeller().getMobilePhone());
                                    transferOrder.setOutNo(IDUtil.getOrderId());
                                    transferOrder.setAmount(goodsOrder.getPrice());
                                    transferOrder.setRemark("商铺收入");
                                    AliTransferResult transResult = JSON.parseObject(JSON.toJSONString(aliPayService.transfer(transferOrder)), AliTransferResult.class);
                                    if (transResult.getAlipay_fund_trans_toaccount_transfer_response().getCode().equals("10000")) {
                                        Bill bill = new Bill();
                                        bill.setType(2);
                                        bill.setSeller(goodsOrder.getGoods().getSeller());
                                        bill.setTime(new Date());
                                        bill.setOutTradeNo(transferOrder.getOutNo());
                                        bill.setPrice(transferOrder.getAmount());
                                        bill.setIntroduce("商铺收入到支付宝账号：" + goodsOrder.getGoods().getSeller().getMobilePhone() + "   " + bill.getPrice() + " 元");
                                        billRepository.save(bill);
                                    }
                                }
                            }
                            goodsOrder.setStatus(5);
                            goodsOrderRepository.save(goodsOrder);
                        }
                    }
                }, 10 * 24 * 60 * 60 * 1000);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 取消订单  或者退款
     * @param order
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "backPrice",autoDelete = "false"),
                    exchange = @Exchange(value = "order",type = ExchangeTypes.DIRECT),
                    key = "backPrice")
    )
    public void backPrice(GoodsOrder order){
        TransferOrder transferOrder = new TransferOrder();
        transferOrder.setPayeeAccount(order.getUser().getMobilePhone());
        transferOrder.setOutNo(IDUtil.getOrderId());
        transferOrder.setAmount(order.getPrice());
        transferOrder.setRemark("退款");
        AliTransferResult transResult = JSON.parseObject(JSON.toJSONString(aliPayService.transfer(transferOrder)), AliTransferResult.class);
        if (transResult.getAlipay_fund_trans_toaccount_transfer_response().getCode().equals("10000")) {
            Bill bill = new Bill();
            bill.setType(5);
            bill.setUser(order.getUser());
            bill.setTime(new Date());
            bill.setOutTradeNo(transferOrder.getOutNo());
            bill.setPrice(transferOrder.getAmount());
            bill.setIntroduce("退款到支付宝账号：" + order.getUser().getMobilePhone() + "   " + bill.getPrice() + " 元");
            billRepository.save(bill);
        }
        //TODO 取消UU跑腿
        UUPTUtil.cancelOrder(order.getExpressId(),"买家订单取消" ,uuptOpenId ,uuptAppId , uuptAppKey);
    }

    public Map<String, Object> getPriceByDate(Long sellerId) {
        List<GoodsOrder> all = goodsOrderRepository.findAllByGoods_Seller_SellerId(sellerId);
        Map<String, List<GoodsOrder>> collect = all.stream().collect(Collectors.groupingBy(goodsOrder -> {
            return DateUtil.date2SimpleDay(goodsOrder.getCreateTime());
        }));
        Map<String,Object> result = new HashMap<>();
        for (Map.Entry<String,List<GoodsOrder>> entry : collect.entrySet()){
            List<GoodsOrder> goodsOrders = entry.getValue();
            goodsOrders = goodsOrders.stream().filter(goodsOrder -> {
                if (goodsOrder.getStatus().equals(5)){
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            BigDecimal price = new BigDecimal(0);
            for (GoodsOrder goodsOrder : goodsOrders){
                price.add(goodsOrder.getPrice());
            }
            result.put(entry.getKey(),price );
        }
        return result;
    }

    /**
     * uu跑腿下单
     */
    public void pushUUPTOrder(List<GoodsOrder> goodsOrders){
        //相同商家配送费合一
        Map<Seller, List<GoodsOrder>> collect = goodsOrders.stream().collect(Collectors.groupingBy(order -> {
            return order.getGoods().getSeller();
        }));
        BigDecimal expressPrice = new BigDecimal(0);
        for (Map.Entry<Seller,List<GoodsOrder>> entry : collect.entrySet()){
            //TODO 获取UU跑腿订单价格
            JSONObject jsonObject = UUPTUtil.getOrderPrice(entry.getValue().get(0).getGoodsOrderId(),
                    entry.getValue().get(0).getGoods().getSeller().getAddress(), entry.getValue().get(0).getAddress().getExpressAddress(),
                    entry.getValue().get(0).getGoods().getSeller().getCity() + "市", uuptOpenId, uuptAppId, uuptAppKey);
            for (GoodsOrder order : entry.getValue()){
                order.setExpressId(entry.getValue().get(0).getGoodsOrderId());
            }
            //todo  uu跑腿下单
            UUPTUtil.pushOrder((String) jsonObject.get("price_token"), (String) jsonObject.get("total_money"), (String) jsonObject.get("need_paymoney"), entry.getValue().get(0).getAddress().getReceiver(), entry.getValue().get(0).getAddress().getReceiver(), "请快速派送", "13592589109", uuptOpenId, uuptAppId, uuptAppKey, null);
        }
    }
}
