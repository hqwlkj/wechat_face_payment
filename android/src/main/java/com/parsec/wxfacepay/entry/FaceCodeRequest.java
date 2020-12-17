package com.parsec.wxfacepay.entry;

import java.io.Serializable;

/**
 * Author:Yanghc
 * Time:2020/8/19 10:31
 * Description:[一句话描述]
 */
public class FaceCodeRequest implements Serializable {

    private static final long serialVersionUID = 804419887619988921L;
    private String appid;
    private String mch_id;
    private String sub_appid;
    private String sub_mch_id;
    private String store_id;
    private String openid;
    private String out_trade_no;
    private String total_fee;
    private String face_authtype;
    private String authinfo;
    private String ask_face_permit;
    private String face_code_type;
    private String screen_index;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getSub_appid() {
        return sub_appid;
    }

    public void setSub_appid(String sub_appid) {
        this.sub_appid = sub_appid;
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

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public String getFace_authtype() {
        return face_authtype;
    }

    public void setFace_authtype(String face_authtype) {
        this.face_authtype = face_authtype;
    }

    public String getAuthinfo() {
        return authinfo;
    }

    public void setAuthinfo(String authinfo) {
        this.authinfo = authinfo;
    }

    public String getAsk_face_permit() {
        return ask_face_permit;
    }

    public void setAsk_face_permit(String ask_face_permit) {
        this.ask_face_permit = ask_face_permit;
    }

    public String getFace_code_type() {
        return face_code_type;
    }

    public void setFace_code_type(String face_code_type) {
        this.face_code_type = face_code_type;
    }

    public String getScreen_index() {
        return screen_index;
    }

    public void setScreen_index(String screen_index) {
        this.screen_index = screen_index;
    }
}
