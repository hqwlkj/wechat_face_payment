package com.parsec.wxfacepay.entry;

import java.io.Serializable;

/**
 * Author:Yanghc
 * Time:2020/5/7 15:34
 * Description:[一句话描述]
 */
public class FacePayResult implements Serializable {


    /**
     * order_status : S
     * transaction_id : 4200000571202005070654739437
     */

    private String order_status;
    private String transaction_id;

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }
}
