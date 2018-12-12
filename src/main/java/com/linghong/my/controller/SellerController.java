package com.linghong.my.controller;

import com.linghong.my.dto.Response;
import com.linghong.my.pojo.Seller;
import com.linghong.my.service.SellerService;
import com.linghong.my.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/10 12:59
 * @Version 1.0
 * @Description:
 */
@RestController
public class SellerController {
    @Resource
    private SellerService sellerService;

    /**
     * 用户注册
     * 参数：mobilePhone password
     * @param seller
     * @return
     */
    @PostMapping("/seller/register")
    public Response register(Seller seller){
        seller = sellerService.register(seller);
        if (seller != null){
            Map<String,Object> map = new HashMap<>();
            map.put("mobilePhone", seller.getMobilePhone());
            map.put("sellerId", seller.getSellerId());
            String jwt = JwtUtil.createJWT(map);
            return new Response(true,200 ,jwt ,"token" );
        }
        return new Response(false,101 , null,"账号已被注册" );
    }

    /**
     * 登录
     * 参数：mobilePhone password
     * @param seller
     * @return
     */
    @PostMapping("/seller/login")
    public Response login(Seller seller){
        seller = sellerService.login(seller);
        if (seller != null){
            Map<String,Object> map = new HashMap<>();
            map.put("mobilePhone", seller.getMobilePhone());
            map.put("sellerId", seller.getSellerId());
            String jwt = JwtUtil.createJWT(map);
            return new Response(true,200 ,jwt ,"token" );
        }
        return new Response(false,101 , null,"账号或密码错误" );
    }

    /**
     * 完善店铺信息
     * 参数 ： sellerId  sellerName  startTime  endTime companyType businessStatus
     * @param seller
     * @return
     */
    @PostMapping("/seller/updateSellerMessage")
    public Response updateSellerMessage(Seller seller,String base64Avatar){
        boolean flag = sellerService.updateSellerMessage(seller,base64Avatar);
        if (flag){
            return new Response(true,200 ,null , "更新完成");
        }
        return new Response(false,101 , null, "无此用户");
    }

    /**
     * 更新 密码
     * @param sellerId
     * @param password
     * @return
     */
    @PostMapping("/seller/updatePassword")
    public Response updatePassword(Long sellerId,String password){
        boolean flag = sellerService.updatePassword(sellerId,password);
        if (flag){
            return new Response(true, 200, null,"更新完成" );
        }
        return new Response(false,101 , null, "无此用户");
    }

    /**
     * 上传身份证号 身份证照片一张
     * @param sellerId
     * @param base64IdCard
     * @param idCardNumber
     * @return
     */
    @PostMapping("/seller/uploadIdCard")
    public Response uploadIdCard(Long sellerId,
                                 @RequestParam(required = false) String base64IdCard,
                                 @RequestParam(required = false) String idCardNumber){
        boolean flag = sellerService.uploadIdCard(sellerId,base64IdCard,idCardNumber);
        if (flag){
            return new Response(true,200 ,null ,"完善成功" );
        }
        return new Response(false,101 ,null ,"身份证不匹配" );
    }

    /**
     * 上传营业执照  身份证  商铺照片
     * @param businessImage
     * @param businessLicense
     * @param base64IdCard
     * @return
     */
    @PostMapping("/seller/updateBusinessMessage")
    public Response updateBusinessMessage(Long sellerId,
                                          @RequestParam(required = false) String businessImage,
                                          @RequestParam(required = false) String businessLicense,
                                          @RequestParam(required = false) String base64IdCard){
        boolean flag = sellerService.updateBusinessMessage(sellerId,businessImage,businessLicense,base64IdCard);
        if (flag){
            return new Response(true,200 ,null ,"完善成功" );
        }
        return new Response(false,101 ,null ,"失败" );
    }

    /**
     * 根据sellerId查询商家信息
     * @param sellerId
     * @return
     */
    @GetMapping("/seller/findSellerBySellerId/{sellerId}")
    public Response findSellerBySellerId(@PathVariable Long sellerId){
        Seller seller = sellerService.findSellerBySellerId(sellerId);
        if (seller != null){
            return new Response(true,200 ,seller ,"查询结果" );
        }
        return new Response(false,101 ,null ,"无此用户" );
    }
}
