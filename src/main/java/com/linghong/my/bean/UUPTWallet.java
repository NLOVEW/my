package com.linghong.my.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/8 10:56
 * @Version 1.0
 * @Description: uu跑腿余额
 */
public class UUPTWallet extends UUPT implements Serializable {
    private BigDecimal accountMoney;//账户余额
    private BigDecimal lockedMoney;//账户冻结余额

    public BigDecimal getAccountMoney() {
        return accountMoney;
    }

    public void setAccountMoney(BigDecimal accountMoney) {
        this.accountMoney = accountMoney;
    }

    public BigDecimal getLockedMoney() {
        return lockedMoney;
    }

    public void setLockedMoney(BigDecimal lockedMoney) {
        this.lockedMoney = lockedMoney;
    }
}
