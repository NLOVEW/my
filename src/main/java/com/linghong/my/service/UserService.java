package com.linghong.my.service;

import com.linghong.my.bean.CreateUser;
import com.linghong.my.constant.UrlConstant;
import com.linghong.my.pojo.User;
import com.linghong.my.pojo.Wallet;
import com.linghong.my.repository.UserRepository;
import com.linghong.my.utils.BeanUtil;
import com.linghong.my.utils.FastDfsUtil;
import com.linghong.my.utils.IDUtil;
import com.linghong.my.utils.IdCardUtil;
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
 * @Date: 2018/12/10 12:39
 * @Version 1.0
 * @Description:
 */
@Service
public class UserService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private UserRepository userRepository;
    @Resource
    private IMServiceImpl imServiceImpl;

    public User register(User user) {
        User secondUser = userRepository.findByMobilePhone(user.getMobilePhone());
        if (secondUser == null){
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(user.getMobilePhone(), user.getPassword());
            ByteSource salt = ByteSource.Util.bytes(user.getMobilePhone());
            String md5 = new SimpleHash("MD5", user.getPassword(), salt, 2).toHex();
            user.setPassword(md5);
            user.setCreateTime(new Date());
            Wallet wallet = new Wallet();
            wallet.setUser(user);
            wallet.setCreateTime(new Date());
            wallet.setWalletId(IDUtil.getId());
            wallet.setBalance(new BigDecimal(0));
            user = userRepository.save(user);

            //注册即时通信
            String mobilePhone = user.getMobilePhone();
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
            }catch (AuthenticationException e){
                logger.error("{} 登录失败，密码或用户名错误",user.getMobilePhone());
                throw  new AuthenticationException("密码或用户名不正确");
            }
            return user;
        }
        return null;
    }

    public User login(User user) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()){
            UsernamePasswordToken token = new UsernamePasswordToken(user.getMobilePhone(),user.getPassword() );
            subject.login(token);
            logger.info("进行登录");
        }
        user = userRepository.findByMobilePhone(user.getMobilePhone());
        return user;
    }

    public User findUserByUserId(Long userId) {
        User user = userRepository.findById(userId).get();
        return user;
    }

    public boolean updateUserMessage(User user) {
        User target = userRepository.findById(user.getUserId()).get();
        BeanUtil.copyPropertiesIgnoreNull(user,target);
        return true;
    }

    public boolean uploadAvatar(Long userId, String base64Avatar) {
        User user = userRepository.findById(userId).get();
        user.setAvatar(UrlConstant.IMAGE_URL+new FastDfsUtil().uploadBase64Image(base64Avatar));
        return true;
    }

    public boolean updatePassword(Long userId, String password) {
        User user = userRepository.findById(userId).get();
        ByteSource salt = ByteSource.Util.bytes(user.getMobilePhone());
        String md5 = new SimpleHash("MD5", password, salt, 2).toHex();
        user.setPassword(md5);
        return true;
    }

    public boolean uploadIdCard(Long userId, String base64IdCard, String idCardNumber) {
        User user = userRepository.findById(userId).get();
        if (idCardNumber != null){
            boolean flag = IdCardUtil.idCardValidate(idCardNumber);
            if (!flag){
                return false;
            }
            user.setIdCardNumber(idCardNumber);
        }
        if (base64IdCard != null){
            user.setIdCardPath(UrlConstant.IMAGE_URL+new FastDfsUtil().uploadBase64Image(base64IdCard));
        }
        return true;
    }

}
