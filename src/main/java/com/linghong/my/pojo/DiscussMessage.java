package com.linghong.my.pojo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/7 16:14
 * @Version 1.0
 * @Description: 评论信息
 */
@Entity
@Table(name = "discuss_message")
public class DiscussMessage implements Serializable {
    private Long discussMessageId;
    private User fromUser;
    private String message;
    private Set<Image> images;
    private Date createTime;

    @Id
    @GeneratedValue
    public Long getDiscussMessageId() {
        return discussMessageId;
    }

    public void setDiscussMessageId(Long discussMessageId) {
        this.discussMessageId = discussMessageId;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fromUserId")
    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "discussMessageId")
    public Set<Image> getImages() {
        return images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "DiscussMessage{" +
                "discussMessageId=" + discussMessageId +
                ", fromUser=" + fromUser +
                ", message='" + message + '\'' +
                ", images=" + images +
                ", createTime=" + createTime +
                '}';
    }
}
