package com.linghong.my.controller;

import com.alibaba.fastjson.JSON;
import com.linghong.my.dto.Response;
import com.linghong.my.service.BaseService;
import com.linghong.my.utils.FastDfsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 基础控制层
 */

@RestController
public class BaseController {
    private static Logger logger = LoggerFactory.getLogger(BaseController.class);
    @Resource(name = "baseService")
    private BaseService baseService;

    /**
     * 获取手机验证码
     *
     * @param mobilePhone
     * @return
     */
    @PostMapping("/api/getCode")
    public Response getCode(String mobilePhone) {
        String code = baseService.getCode(mobilePhone);
        Response response = new Response();
        if (code != null) {
            String result = "{'result':" + code + "}";
            response.set(true, 200, JSON.parseObject(result), "手机验证码");
        } else {
            response.set(false, 500, null, "获取手机验证码错误，请重试");
        }
        logger.info(JSON.toJSONString(response));
        return response;
    }

    @PostMapping("/api/uploadImage")
    public Response uploadImage(String base64Image){
        FastDfsUtil fastDfsUtil = new FastDfsUtil();
        String url = fastDfsUtil.uploadBase64Image(base64Image);
        return new Response(true,200 ,url ,"图片路径");
    }
}
