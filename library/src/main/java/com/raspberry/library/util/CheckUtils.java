package com.raspberry.library.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xiaohu on 2016/9/19.
 * <p/>
 * 校验工具类
 */
public class CheckUtils {

    /**
     * 校验手机号
     *
     * @param phoneNumber 手机号
     * @return 是否为手机号
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        CharSequence inputStr = phoneNumber;
        //正则表达式
        String phone = "^1[34578]\\d{9}$";

        Pattern pattern = Pattern.compile(phone);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}
