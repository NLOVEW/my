package com.linghong.my.shiro;


import com.linghong.my.pojo.Seller;
import com.linghong.my.pojo.User;
import com.linghong.my.repository.SellerRepository;
import com.linghong.my.repository.UserRepository;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * 根据项目需求更改此认证方式
 */
public class ShiroRealm extends AuthorizingRealm {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private UserRepository userRepository;
    @Resource
    private SellerRepository sellerRepository;

    /**
     * 获取授权信息
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    /**
     * 身份认证
     * 根据用户登录凭证及密码进行认证
     * 加密进行MD5及加盐
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String mobilePhone = token.getUsername();
        User user = userRepository.findByMobilePhone(mobilePhone);

        Seller seller = sellerRepository.findByMobilePhone(mobilePhone);
        if (user == null && seller != null ){
            ByteSource salt = ByteSource.Util.bytes(seller.getMobilePhone());
            //用户名  密码 盐 当前的Realm
            SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(mobilePhone,seller.getPassword(),salt,getName());
            return info;
        }else if (user != null){
            ByteSource salt = ByteSource.Util.bytes(user.getMobilePhone());
            //用户名  密码 盐 当前的Realm
            SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(mobilePhone,user.getPassword(),salt,getName());
            return info;
        }else {
            throw new AccountException("用户不存在");
        }
    }
}
