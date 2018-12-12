package com.linghong.my.service;

import com.linghong.my.bean.CreateUser;
import com.linghong.my.constant.UrlConstant;
import com.linghong.my.pojo.Seller;
import com.linghong.my.pojo.Wallet;
import com.linghong.my.repository.SellerRepository;
import com.linghong.my.utils.BeanUtil;
import com.linghong.my.utils.FastDfsUtil;
import com.linghong.my.utils.IDUtil;
import com.linghong.my.utils.IdCardUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/10 12:59
 * @Version 1.0
 * @Description:
 */
@Service
public class SellerService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private SellerRepository sellerRepository;
    @Resource
    private IMServiceImpl imServiceImpl;

    public Seller register(Seller seller) {
        Seller secondSeller = sellerRepository.findByMobilePhone(seller.getMobilePhone());
        if (secondSeller == null) {
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(seller.getMobilePhone(), seller.getPassword());
            ByteSource salt = ByteSource.Util.bytes(seller.getMobilePhone());
            String md5 = new SimpleHash("MD5", seller.getPassword(), salt, 2).toHex();
            seller.setPassword(md5);
            seller.setCreateTime(new Date());
            Wallet wallet = new Wallet();
            wallet.setSeller(seller);
            wallet.setCreateTime(new Date());
            wallet.setWalletId(IDUtil.getId());
            wallet.setBalance(new BigDecimal(0));
            seller = sellerRepository.save(seller);
            //注册即时通信
            String mobilePhone = seller.getMobilePhone();
            new Thread(() -> {
                CreateUser createUser = new CreateUser();
                createUser.setAccid(mobilePhone);
                try {
                    imServiceImpl.createUser(createUser);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            try {
                subject.login(token);
            } catch (AuthenticationException e) {
                logger.error("{} 登录失败，密码或用户名错误", seller.getMobilePhone());
                throw new AuthenticationException("密码或用户名不正确");
            }
            return seller;
        }
        return null;
    }

    public Seller login(Seller seller) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            UsernamePasswordToken token = new UsernamePasswordToken(seller.getMobilePhone(), seller.getPassword());
            subject.login(token);
            logger.info("进行登录");
        }
        seller = sellerRepository.findByMobilePhone(seller.getMobilePhone());
        return seller;
    }

    public Seller findSellerBySellerId(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId).get();
        return seller;
    }

    public boolean updateSellerMessage(Seller seller,String base64) {
        Seller target = sellerRepository.findById(seller.getSellerId()).get();
        BeanUtil.copyPropertiesIgnoreNull(seller, target);
        if (base64 != null){
            target.setAvatar(UrlConstant.IMAGE_URL+new FastDfsUtil().uploadBase64Image(base64));
        }
        return true;
    }

    public boolean updatePassword(Long sellerId, String password) {
        Seller seller = sellerRepository.findById(sellerId).get();
        ByteSource salt = ByteSource.Util.bytes(seller.getMobilePhone());
        String md5 = new SimpleHash("MD5", password, salt, 2).toHex();
        seller.setPassword(md5);
        return true;
    }

    public boolean uploadIdCard(Long sellerId, String base64IdCard, String idCardNumber) {
        Seller seller = sellerRepository.findById(sellerId).get();
        if (idCardNumber != null) {
            boolean flag = IdCardUtil.idCardValidate(idCardNumber);
            if (!flag) {
                return false;
            }
            seller.setIdCardNumber(idCardNumber);
        }
        if (base64IdCard != null) {
            seller.setIdCardPath(UrlConstant.IMAGE_URL + new FastDfsUtil().uploadBase64Image(base64IdCard));
        }
        return true;
    }

    public boolean updateBusinessMessage(Long sellerId,
                                         String businessImage,
                                         String businessLicense,
                                         String base64IdCard) {
        Seller seller = sellerRepository.findById(sellerId).get();
        if (StringUtils.isNotEmpty(businessImage)){
            seller.setBusinessImage(UrlConstant.IMAGE_URL+new FastDfsUtil().uploadBase64Image(businessImage));
        }
        if (StringUtils.isNotEmpty(businessLicense)){
            seller.setBusinessLicense(UrlConstant.IMAGE_URL+new FastDfsUtil().uploadBase64Image(businessLicense));
        }
        if (StringUtils.isNotEmpty(base64IdCard)){
            seller.setIdCardPath(UrlConstant.IMAGE_URL+new FastDfsUtil().uploadBase64Image(base64IdCard));
        }
        return true;
    }
}
