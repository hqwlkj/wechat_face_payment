package com.parsec.wxfacepay.utils;


import android.text.TextUtils;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesUtil {

    private final static String HEX = "0123456789ABCDEF";
    //AES是加密方式 CBC是工作模式 PKCS5Padding是填充模式
    private static final String CBC_PKCS5_PADDING = "AES/ECB/PKCS5Padding";
    //AES 加密
    private static final String AES = "AES";
    // SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法
    private static final String SHA1PRNG = "SHA1PRNG";

    /**
     * @return 动态生成秘钥
     */
    public static String generateKey() {
        try {
            SecureRandom localSecureRandom = SecureRandom.getInstance(SHA1PRNG);
            byte[] bytes_key = new byte[20];
            localSecureRandom.nextBytes(bytes_key);
            return toHex(bytes_key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 对秘钥进行处理
     *
     * @param seed 动态生成的秘钥
     * @return
     * @throws Exception
     */
    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(AES);
        //for android
        SecureRandom sr = null;
        // 在4.2以上版本中，SecureRandom获取方式发生了改变
        int sdk_version = android.os.Build.VERSION.SDK_INT;
        // Android  6.0 以上
        if (sdk_version > 23) {
            sr = SecureRandom.getInstance(SHA1PRNG, new CryptoProvider());
            //4.2及以上
        } else {
            sr = SecureRandom.getInstance(SHA1PRNG);
        }


        // for Java
        // secureRandom = SecureRandom.getInstance(SHA1PRNG);
        sr.setSeed(seed);
        //256 bits or 128 bits,192bits
        kgen.init(128, sr);
        //AES中128位密钥版本有10个加密循环，192比特密钥版本有12个加密循环，256比特密钥版本则有14个加密循环。
        SecretKey skey = kgen.generateKey();
        return skey.getEncoded();
    }

    /**
     * 加密
     *
     * @param key
     * @param cleartext
     * @return
     */
    public static String encrypt(String key, String cleartext) {
        if (TextUtils.isEmpty(cleartext)) {
            return cleartext;
        }
        try {
            byte[] result = encrypt(key, cleartext.getBytes());
            return new String(Base64.encode(result, Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static byte[] encrypt(String key, byte[] clear) throws Exception {
        // 判断Key是否为16位
        if (key == null || key.length()%16 != 0) {
            throw new RuntimeException("秘钥异常");
        }
        byte[] raw = key.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
        Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        return cipher.doFinal(clear);
    }

    /**
     * 解密
     *
     * @param key
     * @param encrypted
     * @return
     */
    public static String decrypt(String key, String encrypted) {
        if (TextUtils.isEmpty(encrypted)) {
            return encrypted;
        }
        try {
            byte[] enc = Base64.decode(encrypted, Base64.DEFAULT);
            byte[] result = decrypt(key, enc);
            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] decrypt(String key, byte[] encrypted) throws Exception {
        byte[] raw = getRawKey(key.getBytes());
        SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
        Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        return cipher.doFinal(encrypted);
    }


    // ----------------------------------------------- 辅助方法 ------------------------------------


    /**
     * 二进制转字符
     * @param buf
     * @return
     */
    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    // 增加  CryptoProvider  类
    public static class CryptoProvider extends Provider {
        /**
         * Creates a Provider and puts parameters
         */
        public CryptoProvider() {
            super("Crypto", 1.0, "HARMONY (SHA1 digest; SecureRandom; SHA1withDSA signature)");
            put("SecureRandom.SHA1PRNG",
                    "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl");
            put("SecureRandom.SHA1PRNG ImplementedIn", "Software");
        }
    }
}
