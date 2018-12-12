package com.linghong.my.controller;

import com.linghong.my.dto.Response;
import com.linghong.my.pojo.Goods;
import com.linghong.my.service.GoodsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/10 13:27
 * @Version 1.0
 * @Description:
 */
@RestController
public class GoodsController {
    @Resource
    private GoodsService goodsService;

    /**
     * 商铺添加商品
     * 参数 sellerId title goodsType price introduce  baseImages
     * @param sellerId
     * @param goods
     * @param baseImages
     * @return
     */
    @PostMapping("/goods/pushGoods")
    public Response pushGoods(Long sellerId,
                              Goods goods,
                              String baseImages){
        boolean flag = goodsService.pushGoods(sellerId,goods,baseImages);
        if (flag){
            return new Response(true,200 ,null ,"添加成功" );
        }
        return new Response(false,101 ,null ,"添加失败" );
    }

    /**
     * 修改商品信息
     * 参数： goodsId  title goodsType price introduce  baseImages
     * @param goods
     * @param baseImages
     * @return
     */
    @PostMapping("/goods/updateGoods")
    public Response updateGoods(Goods goods ,String baseImages){
        boolean flag = goodsService.updateGoods(goods,baseImages);
        if (flag){
            return new Response(true,200 ,null ,"修改成功" );
        }
        return new Response(false,101 ,null ,"修改失败" );
    }

    /**
     * 删除商品
     * @param goodsId
     * @return
     */
    @DeleteMapping("/goods/deleteGoods/{goodsId}")
    public Response deleteGoods(@PathVariable String goodsId){
        boolean flag = goodsService.deleteGoods(goodsId);
        if (flag){
            return new Response(true,200 ,null ,"删除成功" );
        }
        return new Response(false,101 ,null ,"删除失败" );
    }

    /**
     * 下架商品
     * @param goodsId
     * @return
     */
    @DeleteMapping("/goods/obtainedGoods/{goodsId}")
    public Response obtainedGoods(@PathVariable String goodsId){
        boolean flag = goodsService.obtainedGoods(goodsId);
        if (flag){
            return new Response(true,200 ,null ,"删除成功" );
        }
        return new Response(false,101 ,null ,"删除失败" );
    }

    /**
     * 根据商家Id 获取商家商品列表  结果会有分类  上架类、下架类、已删除类
     * @param sellerId
     * @return
     */
    @GetMapping("/goods/findGoodsBySellerId/{sellerId}")
    public Response findGoodsBySellerId(@PathVariable Long sellerId){
        Map<String, List<Goods>> result = goodsService.findGoodsBySellerId(sellerId);
        return new Response(true,200 ,result ,"查询结果" );
    }

    /**
     * 根据商品id获取商品的详细信息
     * @param goodsId
     * @return
     */
    @GetMapping("/goods/getGoodsDetailByGoodsId/{goodsId}")
    public Response getGoodsDetailByGoodsId(@PathVariable String goodsId){
        Map<String,Object> result = goodsService.getGoodsDetailByGoodsId(goodsId);
        return new Response(true,200 ,result ,"查询结果" );
    }

    //todo -------------------商品检索---------------------

    /**
     * 根据城市  商品类型查询
     * @param city
     * @param type
     * @return
     */
    @PostMapping("/goods/findByCityAndType")
    public Response findByCityAndType(String city,String type){
        List<Goods> goods = goodsService.findByCityAndType(city,type);
        return new Response(true,200 ,goods ,"查询结果" );
    }

    /**
     * 根据城市  商商品名或者商铺名检索
     * @param city
     * @param key
     * @return
     */
    @PostMapping("/goods/findByCityAndKey")
    public Response findByCityAndKey(String city,String key){
        List<Goods> goods = goodsService.findByCityAndKey(city,key);
        return new Response(true,200 ,goods ,"查询结果" );
    }

    /**
     * 获取推荐的喜爱的商品
     * @param userId
     * @return
     */
    @GetMapping("/goods/getLoveGoods")
    public Response getLoveGoods(@PathVariable Long userId){
        List<Goods> goods = goodsService.getLoveGoods(userId);
        return new Response(true,200 ,goods ,"查询结果" );
    }

    /**
     * 根据销售量进行推荐
     * @return
     */
    @GetMapping("/goods/getGoodsByAuto")
    public Response getGoodsByAuto(){
        List<Goods> goods = goodsService.getGoodsByAuto();
        return new Response(true,200 ,goods ,"查询结果" );
    }
}
