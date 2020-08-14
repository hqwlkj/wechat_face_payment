package com.parsec.wechat_face_payment

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.RemoteException
import android.util.Log
import androidx.annotation.NonNull
import com.parsec.wechat_face_payment.handlers.WechatFacePaymentHandler
import com.tencent.wxpayface.*
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import okhttp3.*
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


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
                initFacePay(result)
            }
            "initScanCodePay" -> { //  扫码支付
                initScanCodePay(result);
            }
            "showPayLoading" -> {
                showDialog()
            }
            "hidePayLoading" -> {
                hideDialog()
            }
            "releaseWxPayFace" -> { //  释放资源
                WxPayFace.getInstance().releaseWxpayface(context);
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
     * 初始化微信刷脸支付
     */
    private fun initFacePay(@NonNull result: Result) {
        WxPayFace.getInstance().initWxpayface(context, object : IWxPayfaceCallback() {
            override fun response(info: MutableMap<Any?, Any?>?) {
                if (isSuccessInfo(info)) {
                    getWxPayFaceRawData(result)
                } else {
                    uiThreadHandler.post(Runnable {
                        result.success(info)
                    })
                }
            }
        })
    }

    /**
     *  初始化扫码支付
     */
    private fun initScanCodePay(result: Result) {
        WxPayFace.getInstance().initWxpayface(context, object : IWxPayfaceCallback() {
            override fun response(info: MutableMap<Any?, Any?>?) {
                if (isSuccessInfo(info)) {
                    WxPayFace.getInstance().startCodeScanner(object : IWxPayfaceCallback() {
                        @Throws(RemoteException::class)
                        override fun response(info: Map<*, *>) {
                            /**
                             *关闭扫码
                             */
                            WxPayFace.getInstance().stopCodeScanner()
                            /**
                             *释放资源
                             *
                             */
                            WxPayFace.getInstance().releaseWxpayface(context)
                            if (isSuccessInfo(info)) {
                                Log.d(tag, "扫码完成:$info")
                                uiThreadHandler.post(Runnable {
                                    result.success(info)
                                })
                                //                        postReportPayCode(codeMsg, result);
                            } else {
                                uiThreadHandler.post(Runnable {
                                    result.success(info)
                                })
                            }
                        }
                    })
                } else {
                    uiThreadHandler.post(Runnable {
                        result.success(info)
                    })
                }
            }
        })
    }

    /**
     * 获取数据getWxpayfaceRawdata
     * 接口作用：获取rawdata数据
     */
    private fun getWxPayFaceRawData(@NonNull result: Result) {
        WxPayFace.getInstance().getWxpayfaceRawdata(object : IWxPayfaceCallback() {
            @Throws(RemoteException::class)
            override fun response(info: Map<*, *>) {
                if (!isSuccessInfo(info)) {
                    uiThreadHandler.post(Runnable {
                        result.success(info)
                    })
                } else {
                    val rawData = info["rawdata"].toString()
                    getWxPayFaceAuthInfo(rawData, result)
                }
            }
        })
    }

    /**
     * 取得刷脸支付授权信息
     * 获取调用凭证get_wxpayface_authinfo(rawdata)（获取调用凭证）
     * 接口作用：获取调用凭证

     * 接口地址：https://payapp.weixin.qq.com/face/get_wxpayface_authinfo
     */
    @Throws(IOException::class)
    private fun getWxPayFaceAuthInfo(@NonNull rawData: String, @NonNull result: Result) {
        var authInfo = ""
        try {
            val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                        }

                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return arrayOf()
                        }
                    }
            )

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val client = OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory)
                    .hostnameVerifier { hostname, session -> true }
                    .build()
            val body = RequestBody.create(null, rawData)
            val request = Request.Builder()
                    .url("https://wxpay.wxutil.com/wxfacepay/api/getWxpayFaceAuthInfo.php")
                    .post(body)
                    .build()
            client.newCall(request)
                    .enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            println("onFailure | getAuthInfo $e")
                        }

                        @Throws(IOException::class)
                        override fun onResponse(call: Call, response: Response) {
                            try {
                                authInfo = ReturnXMLParser.parseGetAuthInfoXML(response.body()!!.byteStream())
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            Log.i(tag, "273 取得AuthInfo:$authInfo")
                            // 人脸识别
                            if (faceAuthType == "FACEID-LOOP" || faceAuthType == "FACEID-ONCE") {
                                getWxPayFaceUserInfo(authInfo, result)
                            } else {
                                getWxPayFaceCode(authInfo, result)
                            }
                        }
                    })
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }

    /**
     * 获取用户信息getWxpayfaceUserInfo（authinfo）
     * 接口作用：通过人脸识别获取用户信息。

     * 该接口与[人脸识别 getWxpayfaceCode](#人脸识别 getWxpayfaceCode)的区别：

     * 不需要输入手机号；
     * 无法用于订单支付；
     * UI交互不同。
     * 适用范围：**适用于会员、推荐等场景。
     */
    private fun getWxPayFaceUserInfo(authInfo: String, @NonNull result: Result) {
        val params: HashMap<String, String> = HashMap()
        params[PARAMS_FACE_AUTHTYPE] = faceAuthType
        params[PARAMS_APPID] = appId
        params[PARAMS_MCH_ID] = mchId
        params[PARAMS_STORE_ID] = storeId
        params[PARAMS_AUTHINFO] = authInfo
        params["ask_unionid"] = "1"
        WxPayFace.getInstance().getWxpayfaceUserInfo(params, object : IWxPayfaceCallback() {
            @Throws(RemoteException::class)
            override fun response(info: Map<*, *>) {
                if (isSuccessInfo(info)) {
                    Log.i(tag, "313 获取用户信息： $info");
                    uiThreadHandler.post(Runnable {
                        result.success(info)
                    })
                } else {
                    uiThreadHandler.post(Runnable {
                        result.success(info)
                    })
                }
            }

        });
    }

    /**
     * 进行人脸识别getWxpayfaceCode（获取用户信息）
     * 接口作用：启动人脸APP主界面入口，开启人脸识别，获取支付凭证或用户信息。
     *
     *  (获取用户信息)
     */
    private fun getWxPayFaceCode(authInfo: String, @NonNull result: Result) {
        val params: HashMap<String, String> = HashMap<String, String>()
        params[PARAMS_FACE_AUTHTYPE] = faceAuthType
        params[PARAMS_APPID] = appId
        params[PARAMS_MCH_ID] = mchId
        params[PARAMS_STORE_ID] = storeId
        params[PARAMS_OUT_TRADE_NO] = "" + System.currentTimeMillis() / 100000
        params[PARAMS_TOTAL_FEE] = totalFee
        params[PARAMS_TELEPHONE] = telPhone
        params["ask_face_permit"] = "1"
        params["face_code_type"] = "1"
        params[PARAMS_AUTHINFO] = authInfo


        WxPayFace.getInstance().getWxpayfaceCode(params, object : IWxPayfaceCallback() {
            override fun response(info: Map<*, *>) {
                when (info[RETURN_CODE] as String?) {
                    WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS -> {
                        if (info[PARAMS_FACE_AUTHTYPE] == "FACE_AUTH") { // 实名认证
                            getWxPayAuth(authInfo, info["face_sid"].toString(), result)
                        } else {
                            uiThreadHandler.post(Runnable {
                                result.success(info)
                            })
                        }
                    }
                    WxfacePayCommonCode.VAL_RSP_PARAMS_USER_CANCEL -> {
                        Log.i(tag, "362 用户取消支付")
                        uiThreadHandler.post(Runnable {
                            result.success(info)
                        })
                    }
                    WxfacePayCommonCode.VAL_RSP_PARAMS_SCAN_PAYMENT -> {
                        Log.i(tag, "368 扫码支付")
                    }
                    WxfacePayCommonCode.VAL_RSP_PARAMS_ERROR -> {
                        Log.i(tag, "371 发生错误")
                        uiThreadHandler.post(Runnable {
                            result.success(info)
                        })
                    }

                }
            }
        })
    }


    /**
     *  请求用户授权认证getWxpayAuth（实名认证授权）
     *  接口作用： 获得用户授权商户获取实名认证信息
     *
     *  authInfo : 调用凭证。获取方式参见: get_wxpayface_authinfo
     *  face_sid : 用户身份信息查询凭证。获取方式见 [getWxpayfaceCode]
     */
    private fun getWxPayAuth(authInfo: String, face_sid: String, @NonNull result: Result) {
        val params: HashMap<String, String> = HashMap()
        params[PARAMS_AUTHINFO] = authInfo
        params[PARAMS_FACE_SID] = face_sid
        WxPayFace.getInstance().getWxpayAuth(params, object : IWxPayfaceCallback() {
            @Throws(RemoteException::class)
            override fun response(info: Map<*, *>) {
                Log.i(tag, "336 实名认证返回")
                if (!isSuccessInfo(info)) {
                    uiThreadHandler.post(Runnable {
                        result.success(info)
                    })
                } else {
                    params[RETURN_CODE] = info[RETURN_CODE].toString()
                    params[RETURN_MSG] = info[RETURN_MSG].toString()
                    Log.i(tag, "404 用户同意授权：$params")
                    uiThreadHandler.post(Runnable {
                        result.success(params)
                    })
                }
            }

        })
    }

    /**
     * 接口作用：查询用户信息
     * 接口地址：https://api.mch.weixin.qq.com/v3/facemch/users
     * 请求方式：GET
     */
    private fun getFaceMchUserInfo(face_sid: String, @NonNull result: Result) {
        Log.i(tag, "用户授权成功，请求微信后台获取实名信息")
        var authInfo = "";
        try {
            val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                        }

                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return arrayOf()
                        }
                    }
            )

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val client = OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory)
                    .hostnameVerifier { hostname, session -> true }
                    .build()
            val request = Request.Builder()
                    .url("https://api.mch.weixin.qq.com/v3/facemch/users/$face_sid?appid=$appId&face_sid=$face_sid&info_type=ASK_REAL_NAME") // ASK_UNIONID 、 ASK_REAL_NAME
                    .build()
            client.newCall(request)
                    .enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            TODO("Not yet implemented")
                        }

                        @Throws(IOException::class)
                        override fun onResponse(call: Call, response: Response) {
                            Log.i(tag, "449 取得users_response: $response");
                            val params: HashMap<String, String> = HashMap()
                            try {
                                authInfo = ReturnXMLParser.parseGetAuthInfoXML(response.body()!!.byteStream())
                                Log.i(tag, "取得users:$authInfo")
                                params[PARAMS_AUTHINFO] = authInfo
                                params[RETURN_CODE] = "SUCCESS"
                                uiThreadHandler.post(Runnable {
                                    result.success(params)
                                })
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Log.i(tag, "取得users 异常")
                                params[PARAMS_AUTHINFO] = ""
                                params[RETURN_CODE] = "ERROR"
                                uiThreadHandler.post(Runnable {
                                    result.success(params)
                                })
                            }
                        }
                    })
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }
//
//    /**
//     * 付款码上报 reportPaycode
//     * 接口作用： 设备获取到微信付款码，而无法获取到商户号和订单号的可以通过此接口做交易上报
//     * auth_code: 微信支付18位付款码
//     */
//    private fun postReportPayCode(auth_code: String, @NonNull result: Result) {
//        val params: HashMap<String, String> = HashMap()
//        params[PARAMS_AUTH_CODE] = auth_code
//        WxPayFace.getInstance().reportPaycode(params, object : IWxPayfaceCallback() {
//            @Throws(RemoteException::class)
//            override fun response(info: Map<*, *>) {
//                if (!isSuccessInfo(info)) {
//                    return
//                }
//                Log.i(tag, "456 付款码上报返回：$info")
//                uiThreadHandler.post(Runnable {
//                    result.success(info)
//                })
//            }
//        })
//    }


    private fun isSuccessInfo(info: Map<*, *>?): Boolean {
        if (info == null) {
            Log.i(tag, "409 调用返回为空, 请查看日志")
            return false
        }
        val code = info[RETURN_CODE] as String?
        if (code == null || code != WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS) {
            Log.i(tag, "510 调用返回非成功信息, 请查看日志")
            return false
        }
        return true
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
