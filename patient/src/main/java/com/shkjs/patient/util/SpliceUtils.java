package com.shkjs.patient.util;

import com.raspberry.library.util.NumberUtils;
import com.raspberry.library.util.TimeFormatUtils;
import com.shkjs.patient.bean.PlatformDiscount;
import com.shkjs.patient.bean.SitDiagnose;

/**
 * Created by xiaohu on 2016/11/1.
 * <p>
 * 字符串拼接工具类
 */

public class SpliceUtils {

    /**
     * 将金额单位由分转换为元
     *
     * @param balance 金额
     * @return str
     */
    public static String formatBalance(long balance) {
        balance = Math.abs(balance);
        String money;
        if (balance < 10) {
            money = "0.0" + balance;
        } else if (balance < 100) {
            money = "0." + balance;
        } else {
            long spent = balance % 100;
            if (spent < 10) {
                money = balance / 100 + ".0" + spent;
            } else {
                money = balance / 100 + "." + spent;
            }
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
        balance = Math.abs(balance);
        String money;
        if (balance < 10) {
            money = "0.0" + balance;
        } else if (balance < 100) {
            money = "0." + balance;
        } else {
            long spent = balance % 100;
            if (spent < 10) {
                money = balance / 100 + ".0" + spent;
            } else {
                money = balance / 100 + "." + spent;
            }
        }
        return "￥" + NumberUtils.moneyNumber(money);
    }

    /**
     * 拼接金额显示内容
     *
     * @param balance 长整型金额
     * @return 金额
     */
    public static String splicePrice(long balance) {
        balance = Math.abs(balance);
        String money;
        if (balance < 10) {
            money = "0.0" + balance;
        } else if (balance < 100) {
            money = "0." + balance;
        } else {
            long spent = balance % 100;
            if (spent < 10) {
                money = balance / 100 + ".0" + spent;
            } else {
                money = balance / 100 + "." + spent;
            }
        }
        return "￥" + money;
    }

    /**
     * 拼接显示时间
     *
     * @param diagnose 坐诊时间
     * @return str
     */
    public static String getTime(SitDiagnose diagnose) {
        String showTime;
        String startTime;
        String endTime;
        if (diagnose.getStartTime() < 10) {
            startTime = "0" + diagnose.getStartTime() + ":00";
        } else {
            startTime = diagnose.getStartTime() + ":00";
        }
        if (diagnose.getEndTime() < 10) {
            endTime = "0" + diagnose.getEndTime() + ":00";
        } else {
            endTime = diagnose.getEndTime() + ":00";
        }
        showTime = startTime + "--" + endTime;
        return showTime;
    }

    /**
     * 拼接显示时间
     *
     * @param diagnose 坐诊时间
     * @return str
     */
    public static String getTime(String diagnose) {
        String startTime = TimeFormatUtils.getLocalTime("HH", Long.parseLong(diagnose));
        String endTime = TimeFormatUtils.getLocalTime("HH", Long.parseLong(diagnose) + 60 * 60 * 1000);
        String showTime = startTime + ":00 -- " + endTime + ":00";
        return showTime;
    }

    /**
     * 拼接优惠展示数据
     *
     * @param discount 优惠
     * @return 优惠展示数据
     */
    public static String getDiscount(PlatformDiscount discount) {
        switch (discount.getType()) {
            case FIX:
                return "(-" + SpliceUtils.formatBalance(discount.getDelta()) + "元)";
            case PERCENT:
                return "(" + (discount.getDelta() / 10 + "." + discount.getDelta() % 10) + "折)";
            default:
                break;
        }
        return null;
    }
}
