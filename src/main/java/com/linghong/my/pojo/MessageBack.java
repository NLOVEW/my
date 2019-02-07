package com.linghong.my.pojo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/7 09:21
 * @Version 1.0
 * @Description:
 */
@Entity
@Table(name = "message_back")
public class MessageBack implements Serializable {
    private Long messageBackId;
    private String messageType;
    private String message;
    private String imagePath;
    private String mobilePhone;
    private Date pushTime;

    @Id
    @GeneratedValue
    public Long getMessageBackId() {
        return messageBackId;
    }

    public void setMessageBackId(Long messageBackId) {
        this.messageBackId = messageBackId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Date getPushTime() {
        return pushTime;
    }

    public void setPushTime(Date pushTime) {
        this.pushTime = pushTime;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }
}
