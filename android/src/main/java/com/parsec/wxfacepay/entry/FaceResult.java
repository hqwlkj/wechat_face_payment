package com.parsec.wxfacepay.entry;

import java.io.Serializable;

/**
 * Author: Yanghc
 * Time:2019/12/19 17:15
 * Description:[微信刷脸的返回]
 */
public class FaceResult implements Serializable {

    private static final long serialVersionUID = 80441988761998892L;
    private String return_code;
    private String return_msg;
    private String face_sid;
    private String face_code;
    private String openid;
    private String raw_data;
    private String face_type;

    public String getFace_type() {
        return face_type;
    }

    public void setFace_type(String face_type) {
        this.face_type = face_type;
    }

    public String getRaw_data() {
        return raw_data;
    }

    public void setRaw_data(String raw_data) {
        this.raw_data = raw_data;
    }

    public String getFace_code() {
        return face_code;
    }

    public void setFace_code(String face_code) {
        this.face_code = face_code;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }


    public String getFace_sid() {
        return face_sid;
    }

    public void setFace_sid(String face_sid) {
        this.face_sid = face_sid;
    }

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    @Override
    public String toString() {
        return "WxPayFaceResult{" +
                "return_code='" + return_code + '\'' +
                ", return_msg='" + return_msg + '\'' +
                '}';
    }
}
