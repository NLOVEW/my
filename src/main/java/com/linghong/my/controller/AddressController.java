package com.linghong.my.controller;

import com.linghong.my.dto.Response;
import com.linghong.my.pojo.Address;
import com.linghong.my.service.AddressService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/11 09:24
 * @Version 1.0
 * @Description:
 */
@RestController
public class AddressController {
    @Resource
    private AddressService addressService;
    // fixme 切记收货地址不可修改  只可删除  添加
    /**
     * 用户添加收货地址
     * 参数 receiver 收货人  receiverPhone 收货人手机号
     *                      expressAddress 快递目的地址
     * @param request
     * @param address
     * @return
     */
    @PostMapping("/address/addAddress")
    public Response addAddress(Address address, HttpServletRequest request){
        boolean flag = addressService.addAddress(address,request);
        if (flag){
            return new Response(true,200 ,null ,"添加成功" );
        }
        return new Response(false,101 ,null ,"添加失败" );
    }

    /**
     * 根据用户userId 获取收货地址列表
     * @param userId
     * @return
     */
    @GetMapping("/address/getAllAddressByUserId/{userId}")
    public Response getAllAddressByUserId(@PathVariable Long userId){
        Map<String, List<Address>> addresses = addressService.getAllAddressByUserId(userId);
        return new Response(true,200 ,addresses ,"地址列表" );
    }

    @GetMapping("/address/findAddressByAddressId/{addressId}")
    public Response findAddressByAddressId(@PathVariable Long addressId){
        Address address = addressService.findAddressByAddressId(addressId);
        return new Response(true,200 ,address ,"地址信息" );
    }

    /**
     * 根据addressId删除地址信息
     * @param addressId
     * @return
     */
    @DeleteMapping("/address/deleteAddress/{addressId}")
    public Response deleteAddress(@PathVariable Long addressId){
        boolean flag = addressService.deleteAddress(addressId);
        if (flag){
            return new Response(true,200 ,null ,"删除成功" );
        }
        return new Response(false,101 ,null ,"删除失败" );
    }

    /**
     * 设置默认地址
     * @param addressId
     * @param request
     * @return
     */
    @PostMapping("/address/setDefault")
    public Response setDefault(Long addressId,HttpServletRequest request){
        boolean flag = addressService.setDefault(addressId,request);
        if (flag){
            return new Response(true,200 ,null ,"设置成功" );
        }
        return new Response(false,101 ,null ,"设置失败" );
    }

}
