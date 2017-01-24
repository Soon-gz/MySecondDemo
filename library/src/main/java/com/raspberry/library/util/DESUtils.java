package com.raspberry.library.util;

/**
 * Created by xiaohu on 2016/11/17.
 * <p>
 * 加解密工具
 */

public class DESUtils {

    /**
     * 加密过程，先AES加密，然后base64加密
     *
     * @param str 待加密字符串
     * @return 加密后字符串
     */
    public static String encode(String str) {
        return new String(BASE64Utils.encodeBase64(AesEncoder.encrypt(str)));
    }

    /**
     * 解密过程，先base64解密，然后AES解密
     *
     * @param str 待解密字符串
     * @return 解密后字符串
     */
    public static String decode(String str) {
        try {
            return AesEncoder.decrypt(BASE64Utils.decodeBase64(str.getBytes("utf-8")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 加密过程，先AES加密，然后base64加密
     *
     * @param str 待加密字符串
     * @return 加密后字符串
     */
    public static String encodeUrl(String str) {
        return new String(BASE64Utils.encodeBase64Url(AesEncoder.encrypt(str)));
    }

    /**
     * 解密过程，先base64解密，然后AES解密
     *
     * @param str 待解密字符串
     * @return 解密后字符串
     */
    public static String decodeUrl(String str) {
        try {
            return AesEncoder.decrypt(BASE64Utils.decodeBase64Url(str.getBytes("utf-8")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
