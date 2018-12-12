package com.linghong.my.shiro;


import com.linghong.my.utils.DateUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 加密算法
 */
public class ShiroHashedCredentialsMatcher extends HashedCredentialsMatcher {
    private Logger logger = LoggerFactory.getLogger(getClass());
    //使用缓存记录登录
    private Cache<String, AtomicInteger> cache;
    //定义密码错误尝试次数
    private Integer count = 2;

    public ShiroHashedCredentialsMatcher(CacheManager cacheManager) {
        cache = cacheManager.getCache("passwordTryCount");
    }

    /**
     * 重写密码加密匹配方式
     *
     * @param token
     * @param info
     * @return
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        String mobilePhone = usernamePasswordToken.getUsername();
        AtomicInteger tryCount = cache.get(mobilePhone);
        if (tryCount == null) {
            tryCount = new AtomicInteger (0);
            cache.put(mobilePhone, tryCount);
        }else if (tryCount.incrementAndGet() > count.intValue()) {
            logger.warn("{} 用户 {} 达到尝试登录次数", DateUtil.date2SimpleDate(new Date()),mobilePhone);
            throw new ExcessiveAttemptsException("您已连续尝试 3 次,请5分钟后重试");
        }
        boolean flag = super.doCredentialsMatch(token, info);
        if (flag) {
            cache.remove(mobilePhone);
            return true;
        } else {
            throw new IncorrectCredentialsException("账号或密码错误，已错误" + tryCount.intValue() + "次，最多错误3次");
        }
    }
}
