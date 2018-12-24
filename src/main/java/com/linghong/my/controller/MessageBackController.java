package com.linghong.my.controller;

import com.linghong.my.dto.Response;
import com.linghong.my.pojo.MessageBack;
import com.linghong.my.service.MessageBackService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/10 12:55
 * @Version 1.0
 * @Description:
 */
@RestController
public class MessageBackController {
    @Resource
    private MessageBackService messageBackService;

    /**
     * 提交意见反馈
     *
     * messageType  message  base64
     * @param messageBack
     * @param base64
     * @return
     */
    @PostMapping("/messageBack/pushMessageBack")
    public Response pushMessageBack(MessageBack messageBack,
                                    @RequestParam(required = false) String base64){
        boolean flag = messageBackService.pushMessageBack(messageBack,base64);
        if (flag){
            return new Response(true,200 ,null ,"提交成功" );
        }
        return new Response(false,101 ,null ,"提交失败" );
    }
}
