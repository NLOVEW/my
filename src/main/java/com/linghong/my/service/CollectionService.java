package com.linghong.my.service;

import com.linghong.my.pojo.Collection;
import com.linghong.my.pojo.Goods;
import com.linghong.my.pojo.Seller;
import com.linghong.my.pojo.User;
import com.linghong.my.repository.CollectionRepository;
import com.linghong.my.repository.GoodsRepository;
import com.linghong.my.repository.SellerRepository;
import com.linghong.my.repository.UserRepository;
import com.linghong.my.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/11 14:19
 * @Version 1.0
 * @Description:
 */
@Service
@Transactional(rollbackOn = Exception.class)
public class CollectionService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private CollectionRepository collectionRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private GoodsRepository goodsRepository;
    @Resource
    private SellerRepository sellerRepository;

    public boolean addCollection(Long sellerId, String goodsId, HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        Collection collection = collectionRepository.findByUser_UserId(userId);
        if (collection == null) {
            User user = userRepository.findById(userId).get();
            collection = new Collection();
            collection.setUser(user);
            Set<Goods> goodsSet = new HashSet<>();
            Set<Seller> sellerSet = new HashSet<>();
            collection.setSellers(sellerSet);
            collection.setGoods(goodsSet);
        }
        Seller seller = null;
        Goods goods = null;
        if (sellerId != null) {
            seller = sellerRepository.findById(sellerId).get();
            Set<Seller> sellers = collection.getSellers();
            sellers.add(seller);
            collection.setSellers(sellers);
        } else {
            goods = goodsRepository.findById(goodsId).get();
            Set<Goods> goodsSet = collection.getGoods();
            goodsSet.add(goods);
            collection.setGoods(goodsSet);
        }
        collectionRepository.save(collection);
        return true;
    }

    /**
     * 谨慎  可能存在级联删除情况
     * @param sellerId
     * @param goodsId
     * @param request
     * @return
     */
    public boolean cancelCollection(Long[] sellerId, String[] goodsId, HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        Collection collection = collectionRepository.findByUser_UserId(userId);
        if (sellerId != null && sellerId.length > 0) {
            Set<Seller> sellers = collection.getSellers();
            if (sellers != null && sellers.size() > 0) {
                for (Long temp : sellerId){
                    Iterator<Seller> iterator = sellers.iterator();
                    while (iterator.hasNext()){
                        Seller seller = iterator.next();
                        if (seller.getSellerId().equals(temp)){
                            iterator.remove();
                        }
                    }
                }
            }
        }
        if (goodsId != null && goodsId.length > 0) {
            Set<Goods> goods = collection.getGoods();
            if (goods != null && goods.size() > 0) {
                for (String temp : goodsId) {
                    Iterator<Goods> iterator = goods.iterator();
                    while (iterator.hasNext()) {
                        Goods removeGoods = iterator.next();
                        if (removeGoods.getGoodsId().equals(temp)) {
                            iterator.remove();
                        }
                    }
                }
            }
        }
        return true;
    }

    public Collection getCollection(Long userId) {
        return collectionRepository.findByUser_UserId(userId);
    }

    public String isCollectionByGoodsId(String goodsId, Long userId) {
        Collection collection = collectionRepository.findByUser_UserId(userId);
        if (collection != null && collection.getGoods() != null && collection.getGoods().size() > 0) {
            Set<Goods> goodsSet = collection.getGoods();
            for (Goods goods : goodsSet) {
                if (goodsId.equals(goods.getGoodsId())) {
                    return "已收藏";
                }
            }
        }
        return "false";
    }
}
