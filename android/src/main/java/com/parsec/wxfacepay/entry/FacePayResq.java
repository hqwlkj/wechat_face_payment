package com.parsec.wxfacepay.entry;

import java.io.Serializable;

/**
 * Author:Yanghc
 * Time:2020/5/7 12:11
 * Description:[一句话描述]
 */
public class FacePayResq implements Serializable {

    private String face_code;
    private String his_cd;
    private String open_id;
    private String total_fee;
    private String order_id;


    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getFace_code() {
        return face_code;
    }

    public void setFace_code(String face_code) {
        this.face_code = face_code;
    }

    public String getHis_cd() {
        return his_cd;
    }

    public void setHis_cd(String his_cd) {
        this.his_cd = his_cd;
    }

    public String getOpen_id() {
        return open_id;
    }

    public void setOpen_id(String open_id) {
        this.open_id = open_id;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }
}
