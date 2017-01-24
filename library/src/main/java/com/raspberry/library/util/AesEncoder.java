package com.raspberry.library.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 加解密工具类
 *
 * @author
 * @description AES 加解密工具类
 */
public class AesEncoder {

    public static String key = "!@#$%^&*()okngre";
    public static boolean initialized = false;

    public static final String ALGORITHM = "AES/ECB/PKCS7Padding";

    /**
     * @param str
     * @param key key 加/解密要用的长度为32的字节数组（256位）密钥
     * @return byte[] 加密后的字节数组
     */
    public static byte[] encrypt(String str, byte[] key) {
        initialize();
        byte[] result = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM, "BC");
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES"); // 生成加密解密需要的Key
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            result = cipher.doFinal(str.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 加密二维码
     * @param str
     * @return
     */
    public static String encryptWithBase64(String str){
        byte[]bytes = encrypt(str);
        String content = Base64.toBase64String(bytes);
        return content;
    }

    /**
     * @param str
     * @return byte[] 加密后的字节数组
     */
    public static byte[] encrypt(String str) {
        initialize();
        byte[] result = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM, "BC");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES"); // 生成加密解密需要的Key
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            result = cipher.doFinal(str.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param bytes 要被解密的字节数组
     * @param key   加/解密要用的长度为32的字节数组（256位）密钥
     * @return String 解密后的字符串
     */
    public static String decrypt(byte[] bytes, byte[] key) {
        initialize();
        String result = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM, "BC");
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES"); // 生成加密解密需要的Key
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = cipher.doFinal(bytes);
            result = new String(decoded, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 解析二维码
     * @param content
     * @return
     */
    public static String decryptWithBase64(String content){
      return   decrypt(Base64.decode(content), key.getBytes());
    }
    /**
     * @param bytes 要被解密的字节数组
     * @return String 解密后的字符串
     */
    public static String decrypt(byte[] bytes) {
        initialize();
        String result = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM, "BC");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES"); // 生成加密解密需要的Key
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = cipher.doFinal(bytes);
            result = new String(decoded, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void initialize() {
        if (!initialized) {
            synchronized (AesEncoder.class) {
                if (!initialized) {
                    Security.addProvider(new BouncyCastleProvider());
                    initialized = true;
                }
            }
        }
    }

    //    public static void main(String[] args) {
    //        byte[] x = encrypt("ttt", key.getBytes());
    //        String content = Base64.encodeBase64String(x);
    //        System.out.println(content);
    //        System.out.println(decrypt(Base64.decodeBase64(content), key.getBytes()));
    //    }
}
