package com.shkjs.doctor.util;

import com.raspberry.library.util.NumberUtils;

/**
 * Created by xiaohu on 2016/11/1.
 */

public class BalanceUtils {

    /**
     * 将金额单位由分转换为元
     *
     * @param balance
     * @return
     */
    public static String formatBalance(long balance) {
        String money;
        if (balance < 10) {
            money = "0.0" + balance;
        } else if (balance < 100) {
            money = "0." + balance;
        } else {
            money = balance / 100 + "." + balance % 100;
        }
        return NumberUtils.moneyNumber(money);
    }

    /**
     * 将金额由分转换为元
     *
     * @param balance 余额
     * @return 余额
     */
    public static String formatBalance2(long balance) {
        String money = "";
        if (balance >= 0){
            if (balance < 10) {
                money = "0.0" + balance;
            } else if (balance < 100) {
                money = "0." + balance;
            } else {
                money = balance / 100 + "." + balance % 100;
            }
        }
        return NumberUtils.moneyNumber(money);
    }

    private static String money(long balance){
        String money = String.valueOf(balance);
        return money.substring(1,money.length());
    }

    /**
     * 拼接金额显示内容
     *
     * @param balance 长整型金额
     * @return 金额
     */
    public static String splicePrice(long balance) {
        String money;
        if (balance < 10) {
            money = "0.0" + balance;
        } else if (balance < 100) {
            money = "0." + balance;
        } else {
            if (balance % 100 < 10) {
                money = balance / 100 + "." + balance % 100 + "0";
            } else {
                money = balance / 100 + "." + balance % 100;
            }
        }
        return "￥" + money;
    }
}
