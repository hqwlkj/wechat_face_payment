package com.parsec.wechat_face_payment.handlers

import android.content.Context

object WechatFacePaymentHandler{

    private var context: Context? = null

    fun setContext(context: Context?) {
        WechatFacePaymentHandler.context = context
    }
    fun getContext(): Context? {
        return this.context
    }
}
