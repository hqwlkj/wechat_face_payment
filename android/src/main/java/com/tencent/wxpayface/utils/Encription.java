package com.tencent.wxpayface.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

import okhttp3.HttpUrl;

public class Encription {

    public static final String schema = "WECHATPAY2-SHA256-RSA2048";


    public static String getMd5(String content){
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        try {
            byte[] b = content.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(b);
            byte[] hash = md.digest();
            StringBuilder outStrBuf = new StringBuilder(32);
            for (int i = 0; i < hash.length; i++) {
                int v = hash[i] & 0xFF;
                if (v < 16) {
                    outStrBuf.append('0');
                }
                outStrBuf.append(Integer.toString(v, 16).toLowerCase());
            }
            return outStrBuf.toString();
        } catch (Exception e) {
            Log.e("sign_utils签名错误",e.toString());
        }
        return null;
    }

    /**
     *
     * @param mchid 商户号
     * @param method HTTP 请求方式
     * @param serial_no 商户API证书序列号serial_no
     * @param url 请求的 URL
     * @param body get 为空 其他为 提交的参数信息
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getToken(String mchid, String method, String serial_no, HttpUrl url, String body) throws UnsupportedEncodingException, SignatureException, NoSuchAlgorithmException {
        String nonceStr = "your nonce string";
        long timestamp = System.currentTimeMillis() / 1000;
        String message = buildMessage(method, url, timestamp, nonceStr, body);
        String signature = sign(message.getBytes("utf-8"));

        return "mchid=\"" + mchid + "\","
                + "nonce_str=\"" + nonceStr + "\","
                + "timestamp=\"" + timestamp + "\","
                + "serial_no=\"" + serial_no + "\","
                + "signature=\"" + signature + "\"";
    }

    private static String sign(byte[] message) throws SignatureException, NoSuchAlgorithmException {
        Signature sign = Signature.getInstance("SHA256withRSA");
//        sign.initSign("");
//        sign.update(message);

        return Base64.getEncoder().encodeToString(sign.sign());
    }

    private static String buildMessage(String method, HttpUrl url, long timestamp, String nonceStr, String body) {
        String canonicalUrl = url.encodedPath();
        if (url.encodedQuery() != null) {
            canonicalUrl += "?" + url.encodedQuery();
        }

        return method + "\n"
                + canonicalUrl + "\n"
                + timestamp + "\n"
                + nonceStr + "\n"
                + body + "\n";
    }
}
