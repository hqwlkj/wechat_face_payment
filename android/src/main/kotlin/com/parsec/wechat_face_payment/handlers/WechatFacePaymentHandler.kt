package com.parsec.wechat_face_payment.handlers

import android.content.Context
import com.tencent.wxpayface.WxfacePayLoadingDialog

object WechatFacePaymentHandler{

    private var context: Context? = null
    private  lateinit var wxfacePayLoadingDialog: WxfacePayLoadingDialog
    fun setContext(context: Context?) {
        WechatFacePaymentHandler.context = context
    }
    fun initDialog(context: Context){
        wxfacePayLoadingDialog = WxfacePayLoadingDialog(context)
    }


    fun getContext(): Context? {
        return this.context
    }

    fun showDialog(){
        wxfacePayLoadingDialog.show()
    }
    fun hideDialog(){
        wxfacePayLoadingDialog.hide()
    }
}
