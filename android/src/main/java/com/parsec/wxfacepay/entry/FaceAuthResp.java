package com.parsec.wxfacepay.entry;

import java.io.Serializable;

/**
 * Author:Yanghc
 * Time:2020/3/20 10:37
 * Description:[一句话描述]
 */
public class FaceAuthResp implements Serializable {


    /**
     * appid : string
     * authinfo : string
     * expires_in : string
     * mch_id : string
     * nonce_str : string
     * sign : string
     */

    private String appid;
    private String authinfo;
    private String expires_in;
    private String mch_id;
    private String nonce_str;
    private String sign;

    private String sub_app_id;
    private String sub_mch_id;
    private String store_id;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAuthinfo() {
        return authinfo;
    }

    public void setAuthinfo(String authinfo) {
        this.authinfo = authinfo;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSub_app_id() {
        return sub_app_id;
    }

    public void setSub_app_id(String sub_app_id) {
        this.sub_app_id = sub_app_id;
    }

    public String getSub_mch_id() {
        return sub_mch_id;
    }

    public void setSub_mch_id(String sub_mch_id) {
        this.sub_mch_id = sub_mch_id;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    @Override
    public String toString() {
        return "FaceAuthResp{" +
                "appid='" + appid + '\'' +
                ", authinfo='" + authinfo + '\'' +
                ", expires_in='" + expires_in + '\'' +
                ", mch_id='" + mch_id + '\'' +
                ", nonce_str='" + nonce_str + '\'' +
                ", sign='" + sign + '\'' +
                ", sub_app_id='" + sub_app_id + '\'' +
                ", sub_mch_id='" + sub_mch_id + '\'' +
                ", store_id='" + store_id + '\'' +
                '}';
    }
}
