package com.tencent.wxpayface.utils;

import android.text.TextUtils;
import android.util.Log;

import java.security.MessageDigest;

public class Encription {
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
}
