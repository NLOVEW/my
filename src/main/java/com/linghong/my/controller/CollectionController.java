package com.linghong.my.controller;

import com.linghong.my.dto.Response;
import com.linghong.my.pojo.Collection;
import com.linghong.my.service.CollectionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/11 14:19
 * @Version 1.0
 * @Description:
 */
@RestController
public class CollectionController {
    @Resource
    private CollectionService collectionService;

    /**
     * 添加关注或者收藏
     * @param sellerId
     * @param goodsId
     * @param userId
     * @return
     */
    @PostMapping("/collection/addCollection")
    public Response addCollection(@RequestParam(required = false) Long sellerId,
                                  @RequestParam(required = false) String goodsId,
                                  Long userId) {
        boolean flag = collectionService.addCollection(sellerId,goodsId,userId);
        if (flag){
            return new Response(true, 200, null, "操作成功");
        }
        return new Response(false, 101, null, "操作失败");
    }

    /**
     * 删除关注或者收藏
     * @param sellerId
     * @param goodsId
     * @param userId
     * @return
     */
    @PostMapping("/collection/cancelCollection")
    public Response cancelCollection(@RequestParam(required = false) Long sellerId,
                                     @RequestParam(required = false) String goodsId,
                                     Long userId){
        boolean flag = collectionService.cancelCollection(sellerId,goodsId,userId);
        if (flag){
            return new Response(true, 200, null, "操作成功");
        }
        return new Response(false, 101, null, "操作失败");
    }

    /**
     * 获取收藏 关注的信息
     * @param userId
     * @return
     */
    @GetMapping("/collection/getCollection/{userId}")
    public Response getCollection(@PathVariable Long userId){
        Collection collection = collectionService.getCollection(userId);
        return new Response(true, 200, collection, "数据");
    }
}
