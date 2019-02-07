package com.linghong.my.pojo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/11 14:11
 * @Version 1.0
 * @Description: 收藏 关注
 *
 */
@Entity
@Table(name = "collection")
public class Collection implements Serializable {
    private Long collectionId;
    private User user;
    private Set<Seller> sellers;
    private Set<Goods> goods;

    @Id
    @GeneratedValue
    public Long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @OneToMany(cascade = CascadeType.MERGE,fetch = FetchType.LAZY)
    @JoinColumn(name = "collectionId")
    public Set<Seller> getSellers() {
        return sellers;
    }

    public void setSellers(Set<Seller> sellers) {
        this.sellers = sellers;
    }

    @OneToMany(cascade = CascadeType.MERGE,fetch = FetchType.LAZY)
    @JoinColumn(name = "collectionId")
    public Set<Goods> getGoods() {
        return goods;
    }

    public void setGoods(Set<Goods> goods) {
        this.goods = goods;
    }
}
