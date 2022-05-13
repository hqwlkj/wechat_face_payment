package com.parsec.wechat_face_payment

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.NonNull
import com.parsec.wechat_face_payment.handlers.WechatFacePaymentHandler
import com.parsec.wxfacepay.utils.LoggerUtil
import com.parsec.wxfacepay.utils.WxFaceUtil
import com.tencent.wxpayface.WxPayFace
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar


/** WechatFacePaymentPlugin */
class WechatFacePaymentPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {

    private val uiThreadHandler = Handler(Looper.getMainLooper())

    /*
    刷脸支付相关参数
     */
    private lateinit var appId: String //商户公众号或小程序APPIdD
    private lateinit var mchId: String //商户ID
    private lateinit var storeId: String // 店铺ID
    private lateinit var serverPath: String // 服务器地址


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
                serverPath = call.argument<String>("serverPath")!!
                LoggerUtil.i("serverPath:$serverPath");
                initWxpayface(result)
            }
            "faceVerified" -> {
                faceRecognition(result)
            }
            "wxFacePay" -> {
                val merchant_id = call.argument<String>("merchant_id")!!
                val channel_id = call.argument<String>("channel_id")!!
                val order_title = call.argument<String>("order_title")!!
                val out_trade_no = call.argument<String>("out_trade_no")!!
                val total_fee = call.argument<String>("total_fee")!!
                wxFacePay(result, merchant_id, channel_id, order_title, out_trade_no, total_fee);
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

    private fun showDialog() {
        Log.i(tag, "showDialog" + WechatFacePaymentHandler.getContext())
        WechatFacePaymentHandler.showDialog()
    }

    private fun hideDialog() {
        Log.i(tag, "hideDialog" + WechatFacePaymentHandler.getContext())
        WechatFacePaymentHandler.hideDialog()
    }

    /**
     * 初始化人脸识别
     */
    private fun initWxpayface(@NonNull result: Result) {
        WxFaceUtil.init(WechatFacePaymentHandler.getContext()) { params ->
            uiThreadHandler.post {
                result.success(params)
            }
        }
    }

    /**
     * 人脸识别
     */
    private fun faceRecognition(@NonNull result: Result) {
        WxFaceUtil.InfoVer(WechatFacePaymentHandler.getContext(), serverPath) { params ->
            uiThreadHandler.post {
                result.success(params)
            }
        }
    }

    /**
     * 支付
     */
    private fun wxFacePay(@NonNull result: Result, merchant_id: String, channel_id: String, order_title: String, out_trade_no: String, total_fee: String) {
        WxFaceUtil.FacePay(WechatFacePaymentHandler.getContext(), serverPath, merchant_id, channel_id, order_title, out_trade_no, total_fee) { params ->
            uiThreadHandler.post {
                result.success(params)
            }
        }
    }

    /**
     * 扫码
     */
    private fun wxScanCode(@NonNull result: Result) {
        WxFaceUtil.ScanCode(WechatFacePaymentHandler.getContext()) { params ->
            uiThreadHandler.post {
                result.success(params)
            }
        }
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
