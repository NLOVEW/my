package com.linghong.my.controller;

import com.alibaba.fastjson.JSONObject;
import com.linghong.my.bean.ShoppingCart;
import com.linghong.my.dto.Response;
import com.linghong.my.pojo.DiscussMessage;
import com.linghong.my.pojo.GoodsOrder;
import com.linghong.my.service.GoodsOrderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/10 15:40
 * @Version 1.0
 * @Description:
 */
@RestController
public class GoodsOrderController {
    @Resource
    private GoodsOrderService goodsOrderService;

    /**
     * 添加商品到购物车
     * @param userId
     * @param goodsId
     * @param number
     * @return
     */
    @PostMapping("/order/pushShoppingCart")
    public Response pushShoppingCart(Long userId,String goodsId,Integer number){
        boolean flag = goodsOrderService.pushShoppingCart(userId,goodsId,number);
        if (flag){
            return new Response(true,200,null ,"添加成功" );
        }
        return new Response(false,101,null ,"添加失败" );
    }

    /**
     * 删除购物车中的商品
     * @param goodsIds
     * @return
     */
    @PostMapping("/order/cancelShoppingCart")
    public Response cancelShoppingCart(Long userId,String[] goodsIds){
        List<ShoppingCart> result = goodsOrderService.cancelShoppingCart(userId,goodsIds);
        return new Response(true,200,result ,"查询结果" );
    }

    /**
     * 根据用户Id获取此用户的购物车信息
     * @param userId
     * @return
     */
    @GetMapping("/order/getShoppingCartByUserId/{userId}")
    public Response getShoppingCartByUserId(@PathVariable Long userId){
        List<ShoppingCart> result = goodsOrderService.getShoppingCartByUserId(userId);
        return new Response(true,200,result ,"查询结果" );
    }

    /**
     * 进行结算  在进行结算前  请获取收货地址  封装到  ShoppingCart 中
     * 接收json格式
     * @param carts
     * @return
     */
    @PostMapping("/order/settleAccounts")
    public Response settleAccounts(@RequestBody List<ShoppingCart> carts, HttpServletRequest request){
       Map<String,Object> result = goodsOrderService.settleAccounts(carts,request);
       return new Response(true,200 , result, "计算结果");
    }

    /**
     * 修改地址的时候使用  进行重新计算价格   配送费
     * @return
     */
    @PostMapping("/order/updateSettleAccounts")
    public Response updateSettleAccounts(Long addressId,String[] goodsIds){
        Map<String,Object> result = goodsOrderService.updateSettleAccounts(addressId,goodsIds);
        return new Response(true,200 , result, "计算结果");
    }

    /**
     * 提交订单
     * @param goodsOrdersIds
     * @param
     * @return
     */
    @PostMapping("/order/submitOrder")
    public Response submitOrder(String[] goodsOrdersIds,String comment){
        String orderId = goodsOrderService.submitOrder(goodsOrdersIds,comment);
        return new Response(true,200 , orderId, "订单Id");
    }

    /**
     * 查询本用户下所有的订单
     * @param userId
     * @return
     */
    @GetMapping("/order/findAllOrderByUserId/{userId}")
    public Response findAllOrderByUserId(@PathVariable Long userId){
        List<GoodsOrder> goodsOrders = goodsOrderService.findAllOrderByUserId(userId);
        return new Response(true,200 ,goodsOrders ,"本用户所有订单" );
    }

    /**
     * 查询本商家下所有的订单
     * @param sellerId
     * @return
     */
    @GetMapping("/order/findAllOrderBySellerId/{sellerId}")
    public Response findAllOrderBySellerId(@PathVariable Long sellerId){
        Map<Integer,List<GoodsOrder>> result = goodsOrderService.findAllOrderBySellerId(sellerId);
        return new Response(true,200 ,result ,"本商家所有订单" );
    }

    /**
     * 根据订单Id获取订单详情
     * @param goodsOrderId
     * @return
     */
    @GetMapping("/order/getOrderDetail/{goodsOrderId}")
    public Response getOrderDetail(@PathVariable String goodsOrderId){
        GoodsOrder order = goodsOrderService.getOrderDetail(goodsOrderId);
        return new Response(true,200 ,order ,"订单信息" );
    }



    /**
     * 根据orderId取消订单
     * @param goodsOrderId
     * @return
     */
    @DeleteMapping("/order/cancelOrder/{goodsOrderId}")
    public Response cancelOrder(@PathVariable String goodsOrderId){
        boolean flag = goodsOrderService.cancelOrder(goodsOrderId);
        if (flag){
            return new Response(true,200,null ,"操作成功" );
        }
        return new Response(false,101,null ,"已派送无法取消" );
    }

    /**
     * 获取骑手信息
     * @param goodsOrderId
     * @return
     */
    @GetMapping("/order/getExpress/{goodsOrderId}")
    public Response getExpress(@PathVariable String goodsOrderId){
        JSONObject result = goodsOrderService.getExpress(goodsOrderId);
        return new Response(true,200 ,result ,"骑手信息" );
    }

    /**
     * 商家处理 取消订单
     * @param goodsOrderId
     * @param status
     * @return
     */
    @PostMapping("/order/dealCancelOrder")
    public Response dealCancelOrder(String goodsOrderId,Integer status){
        boolean flag = goodsOrderService.dealCancelOrder(goodsOrderId,status);
        if (flag){
            return new Response(true,200,null ,"操作成功" );
        }
        return new Response(false,101,null ,"操作失败" );
    }

    /**
     * 对商品进行评价 可上传多个图片
     * 参数 message
     * @param orderId
     * @param discussMessage
     * @return
     */
    @PostMapping("/order/discussOrder")
    public Response discussOrder(String orderId,
                                 DiscussMessage discussMessage,
                                 @RequestParam(required = false) String base64Images){
        boolean flag = goodsOrderService.discussOrder(orderId,discussMessage,base64Images);
        if (flag){
            return new Response(true,200,null ,"操作成功" );
        }
        return new Response(false,101,null ,"操作失败" );
    }

    @GetMapping("/test/json")
    public List<ShoppingCart> testJson(){
        return goodsOrderService.testJson();
    }
}
