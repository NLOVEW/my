package com.linghong.my.service;


import com.linghong.my.constant.UrlConstant;
import com.linghong.my.pojo.MessageBack;
import com.linghong.my.repository.MessageBackRepository;
import com.linghong.my.utils.FastDfsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/13 10:26
 * @Version 1.0
 * @Description:
 */

@Service
@Transactional(rollbackOn = Exception.class)
public class MessageBackService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private MessageBackRepository messageBackRepository;

    public boolean pushMessageBack(MessageBack messageBack, String base64) {
        if (base64!= null){
            messageBack.setImagePath(UrlConstant.IMAGE_URL+new FastDfsUtil().uploadBase64Image(base64));
        }
        messageBackRepository.save(messageBack);
        return true;
    }
}
