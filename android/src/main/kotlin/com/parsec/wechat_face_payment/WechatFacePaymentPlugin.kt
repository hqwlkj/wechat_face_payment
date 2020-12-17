package com.parsec.wechat_face_payment

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.NonNull
import com.parsec.wechat_face_payment.handlers.WechatFacePaymentHandler
import com.parsec.wxfacepay.utils.ICallback
import com.parsec.wxfacepay.utils.WxFaceUtil
import com.tencent.wxpayface.*
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar


/** WechatFacePaymentPlugin */
public class WechatFacePaymentPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {

    private val uiThreadHandler = Handler(Looper.getMainLooper())

    private val PARAMS_FACE_AUTHTYPE = "face_authtype"
    private val PARAMS_AUTH_CODE = "auth_code"
    private val PARAMS_APPID = "appid"
    private val PARAMS_SUB_APPID = "sub_appid"
    private val PARAMS_MCH_ID = "mch_id"
    private val PARAMS_MCH_NAME = "mch_name"
    private val PARAMS_SUB_MCH_ID = "sub_mch_id"
    private val PARAMS_NONCE_STR = "nonce_str"
    private val PARAMS_SIGN = "sign"
    private val PARAMS_SIGN_TYPE = "sign_type"
    private val PARAMS_BODY = "body"
    private val PARAMS_STORE_ID = "store_id"
    private val PARAMS_AUTHINFO = "authinfo"
    private val PARAMS_FACE_SID = "face_sid"
    private val PARAMS_INFO_TYPE = "info_type"
    private val PARAMS_OUT_TRADE_NO = "out_trade_no"
    private val PARAMS_TOTAL_FEE = "total_fee"
    private val PARAMS_TELEPHONE = "telephone"

    /*
     * 微信刷脸SDK返回常量
     */
    private val RETURN_CODE = "return_code"
    private val RETURN_MSG = "return_msg"

    /*
    刷脸支付相关参数
     */
    private lateinit var appId: String //商户公众号或小程序APPIdD
    private lateinit var mchId: String //商户ID
    private lateinit var storeId: String // 店铺ID
    private lateinit var telPhone: String //用户手机号
    private lateinit var openId: String //用户微信OPENID
    private lateinit var outTradeNo: String //外部订单号
    private lateinit var totalFee: String // 支付金额
    private lateinit var faceAuthType: String //FACEPAY 人脸凭证  FACEPAY_DELAY 延迟支付 FACE_AUTH 实名认证 FACEID-ONCE 人脸识别(单次模式) FACEID-LOOP 人脸识别(循环模式) SCAN_CODE  扫码支付

    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "wechat_face_payment")
        channel.setMethodCallHandler(this);
    }

    // This static function is optional and equivalent to onAttachedToEngine. It supports the old
    // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
    // plugin registration via this function while apps migrate to use the new Android APIs
    // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
    //
    // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
    // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
    // depending on the user's project. onAttachedToEngine or registerWith must both be defined
    // in the same class.
    companion object {
        const val tag = "WeChatFacePaymentPlugin"

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            WechatFacePaymentHandler.setContext(registrar.activity())
            WechatFacePaymentHandler.initDialog(registrar.activity())
            val channel = MethodChannel(registrar.messenger(), "wechat_face_payment")
            channel.setMethodCallHandler(WechatFacePaymentPlugin())
        }
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            "initFacePay" -> {
                appId = call.argument<String>("appId")!!
                mchId = call.argument<String>("mchId")!!
                storeId = call.argument<String>("storeId")!!
                telPhone = call.argument<String>("telPhone")!!
                openId = call.argument<String>("openId")!!
                outTradeNo = call.argument<String>("outTradeNo")!!
                totalFee = call.argument<String>("totalFee")!!
                faceAuthType = call.argument<String>("faceAuthType")!!
                initWxpayface(result)
            }
            "initScanCodePay" -> { //  扫码支付
//                initScanCodePay(result);
            }
            "faceVerified" -> {
                faceRecognition(result)
            }
            "wxFacePay" -> {
                wxFacePay(result)
            }
            "wxScanCode" -> {
                wxScanCode(result)
            }
            "wxStopCodeScanner" -> {
                wxStopCodeScanner()
            }
            "releaseWxpayface" -> {
                releaseWxpayface()
            }
            "showPayLoading" -> {
                showDialog()
            }
            "hidePayLoading" -> {
                hideDialog()
            }
            "releaseWxPayFace" -> { //  释放资源
                WxPayFace.getInstance().releaseWxpayface(WechatFacePaymentHandler.getContext());
                result.success("SUCCESS")
            }
            "testFacePay" -> { // 测试返回信息
                val params: HashMap<String, String> = HashMap()
                params["result_code"] = "SUCCESS"
                params["code"] = "200"
                params["message"] = "TestFacePay SUCCESS"
                result.success(params)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    private fun showDialog(){
        Log.i(tag, "showDialog" +WechatFacePaymentHandler.getContext())
        WechatFacePaymentHandler.showDialog()
    }

    private fun hideDialog(){
        Log.i(tag, "hideDialog" +WechatFacePaymentHandler.getContext())
        WechatFacePaymentHandler.hideDialog()
    }

    /**
     * 初始化人脸识别
     */
    private fun initWxpayface(@NonNull result: Result) {
        WxFaceUtil.init(WechatFacePaymentHandler.getContext(), object : ICallback {
            override fun callback(params: MutableMap<String, Any>?) {
                uiThreadHandler.post {
                    result.success(params)
                }
            }
        })
    }

    /**
     * 人脸识别
     */
    private fun faceRecognition(@NonNull result: Result) {
        WxFaceUtil.InfoVer(WechatFacePaymentHandler.getContext(), object : ICallback {
            override fun callback(params: MutableMap<String, Any>?) {
                uiThreadHandler.post {
                    result.success(params)
                }
            }
        })
    }

    /**
     * 支付
     */
    private fun wxFacePay(@NonNull result: Result) {
        WxFaceUtil.FacePay(WechatFacePaymentHandler.getContext(), "", object : ICallback {
            override fun callback(params: MutableMap<String, Any>?) {
                uiThreadHandler.post {
                    result.success(params)
                }
            }
        })
    }

    /**
     * 扫码
     */
    private fun wxScanCode(@NonNull result: Result) {
        WxFaceUtil.ScanCode(WechatFacePaymentHandler.getContext(), object : ICallback {
            override fun callback(params: MutableMap<String, Any>?) {
                uiThreadHandler.post {
                    result.success(params)
                }
            }
        })
    }

    /**
     * 关闭扫码
     */
    private fun wxStopCodeScanner() {
        WxFaceUtil.stopCodeScanner()
    }

    /**
     * 释放微信刷脸
     */
    private fun releaseWxpayface() {
        WxFaceUtil.releaseWxpayface(WechatFacePaymentHandler.getContext())
    }

    override fun onDetachedFromActivity() {
        TODO("Not yet implemented")
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        TODO("Not yet implemented")
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        WechatFacePaymentHandler.setContext(binding.activity)
        WechatFacePaymentHandler.initDialog(binding.activity)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        TODO("Not yet implemented")
    }
}
