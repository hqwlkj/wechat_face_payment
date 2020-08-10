package com.tencent.wxpayface;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.parsec.wechat_face_payment.BuildConfig;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tencent.wxpayface.FacePayConstants.GET_USER_PAYSCORE_STATUS;

/**
 * Created by admin on 2018/5/2.
 */

public class WxPayFace {

    public static final String TAG = "WxPayFace";
    public static final String KEY_PROXY = "proxy";

    public static final String API_GET_WXPAYFACE_CODE = "getWxpayfaceCode";
    public static final String API_UPDATE_WXPAYFACE_PAYRESULT= "updateWxpayfacePayResult";
    public static final String API_REPORT_INFO = "reportInfo";
    public static final String API_REPORT_ORDER = "reportOrder";
    public static final String API_GET_WXPAYFACE_USERINFO = "getWxpayfaceUserInfo";
    public static final String API_STOP_WXPAYFACE = "stopWxpayface";
    public static final String API_UPDATE_WXPAYFACE_BANNER_STATE = "updateWxpayfaceBannerState";
    public static final String API_GET_WXPAY_AUTH = "getWxpayAuth";
    public static final String API_REPORT_PAYCODE= "reportPaycode";
    public static final String API_GET_MP_CONFIG = "getMpConfig";
    public static final String API_LAUNCH_MP = "launchMiniProgram";
    public static final String API_SET_PROXY= "setProxy";
    public static final String API_ENABLE_FIRE_WALL= "enableFirewall";
    public static final String API_DISABLE_FIRE_WALL= "disableFirewall";

    public static final String RETURN_CODE = "return_code";
    public static final String KEY_RET_COMMON_ERROR_CODE = "err_code";//返回错误码
    public static final String RETURN_MSG = "return_msg";

    public static final String RETURN_SUCCESS = "SUCCESS";
    public static final String RETURN_FAIL = "FAIL";
    public static final String RETURN_MSG_NOT_SUPPORT = "API NOT SUPPORT";
    public static final Integer ERR_FACE_APP_CONNECT_SERVICE_NOT_INIT = 271378620;//调用初始化
    public static final Integer ERR_FACE_APP_CONNECT_SERVICE_INITING = 271378621;//等待500ms左右重新调用init
    public static final String AAR_VERSION_NAME = BuildConfig.VERSION_NAME+"."+BuildConfig.VERSION_CODE;

    private static WxPayFace instance = null;

    private boolean mIsServiceConnected = false;
    private static boolean mIsServiceConnecting = false;
    private IWxPayFaceAIDL service;
    private IWxPayFaceCallbackAIDL mInitCallback;
    private Context mContext;
    private static Map PROXY_MAP = null;

    private IBinder bd;

    private static final int RETRY_CONNECT_OVER_TIME = 0;

    public static WxPayFace getInstance() {

        synchronized (WxPayFace.class) {
            if (instance == null) {
                instance = new WxPayFace();
            }
        }

        return instance;
    }

    private static class OverTimeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RETRY_CONNECT_OVER_TIME:
                    if (StateInfoController.mSpecialCallback != null) {
                        initErrorCallback(StateInfoController.mSpecialCallback);
                    }
                    break;
            }
        }
    }

    private OverTimeHandler handler;

    public void initWxpayface(Context cxt, final IWxPayFaceCallbackAIDL wxpayfaceCallBack) {
        initWxpayface(cxt,null,wxpayfaceCallBack);
    }

    public void initWxpayface(Context cxt, Map info, final IWxPayFaceCallbackAIDL wxpayfaceCallBack) {
        if(cxt == null){
            Log.e(TAG,"initWxpayface cxt is null");
            return;
        }
        Log.i(TAG,"initWxpayface version:"+AAR_VERSION_NAME);
        wrapMapWithAARInfo(info);
        if (bd != null && bd.isBinderAlive()) {
            String wxVersion = getSdkVersion(mContext);
            //如果版本高于2.13.0，直接调用代理接口，否则直接返回成功
            if(compareVersion(wxVersion,"2.13.0.0")>=0) {
                try {
                    service.dispatchBindApi(API_SET_PROXY, info, wxpayfaceCallBack);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                initSuccessCallback(wxpayfaceCallBack);
            }
        }  else {
            //先release，再重新绑定
            releaseWxpayface(cxt);
            PROXY_MAP = info;
            bindFaceService(cxt, info);
            mInitCallback = wxpayfaceCallBack;
            mContext = cxt;
        }
    }

    private synchronized void bindFaceService(Context cxt, Map info) {
        Intent intent = new Intent("com.tencent.faceservice");
        if (intent != null) {
            Intent explicitIntent = createExplicitFromImplicitIntent(cxt, intent);
            if (explicitIntent != null) {
                Intent eintent = new Intent(explicitIntent);
                if (eintent != null && info != null) {
                    eintent.putExtra(KEY_PROXY, (Serializable) info);
                }
                boolean connectSuccess = cxt.bindService(eintent, conn, Context.BIND_AUTO_CREATE);
                if (connectSuccess){

                    mIsServiceConnecting = true;

                }
            }
        }

    }

    public void getWxpayfaceRawdata(final IWxPayFaceCallbackAIDL wxpayfaceCallBack) {
        try {
            if (tryReconnectService(FacePayConstants.GET_WXPAYFACE_RAWDATA, wxpayfaceCallBack)) {

                return;
            }
            service.getWxpayfaceRawdata(wxpayfaceCallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getWxpayfaceCode(final Map bean, final IWxPayFaceCallbackAIDL wxpayfaceCallBack) {
        Log.d(TAG, "client| getWxpayfaceCode");
        try {
            if (tryReconnectService(FacePayConstants.GET_WXPAYFACE_CODE, bean, wxpayfaceCallBack)) {
                return;
            }
            service.getWxpayfaceCode(bean, wxpayfaceCallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getWxpayfaceCode(final Map bean, final IWxPayFaceCallbackAIDL wxpayfaceCallBack,final IWxPayFaceCallbackAIDL secondCb) {
        Log.d(TAG, "client| dispatchBindApiWithTwoCb");
        try {
            if (tryReconnectService(FacePayConstants.GET_WXPAYFACE_CODE, bean, wxpayfaceCallBack, secondCb)) {
                return;
            }
            //本身调用这个api不会异常，不过可以分为两个调度，如果发现是低版本，直接转换调度方式
            String wxVersion = getSdkVersion(mContext);
            if(compareVersion(wxVersion,"2.13.0.0")>=0) {
                service.dispatchBindApiWithTwoCb(WxPayFace.API_GET_WXPAYFACE_CODE, bean, wxpayfaceCallBack, secondCb);
            }else{
                service.getWxpayfaceCode(bean, wxpayfaceCallBack);
                service.updateWxpayfacePayResult(bean,wxpayfaceCallBack);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getWxpayAuth(final Map bean, final IWxPayFaceCallbackAIDL wxpayfaceCallBack) {
        Log.d(TAG, "client| getWxpayAuth");
        try{
            if (tryReconnectService(FacePayConstants.GET_WXPAY_AUTH, bean, wxpayfaceCallBack)) {
                return;
            }
            service.getWxpayAuth(bean, wxpayfaceCallBack);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startCodeScanner(IWxPayFaceCallbackAIDL cb) {
        Log.d(TAG,"client| startCodeScanner");
        try {
            if (tryReconnectService(FacePayConstants.START_CODE_SCANNER, cb)) {
                return;
            }
            service.startCodeScanner(cb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void stopCodeScanner(){
        Log.d(TAG,"client| stopCodeScanner");
        try {
            if (tryReconnectService(FacePayConstants.STOP_CODE_SCANNER)) {
                return;
            }
            service.stopCodeScanner();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateWxpayfacePayResult(final Map bean, final IWxPayFaceCallbackAIDL wxpayfaceCallBack) {
        Log.d(TAG, "client| speed | updateWxpayfacePayResult");
        try {
            if (tryReconnectService(FacePayConstants.UPDATE_WXPAYFACE_PAYRESULT, bean, wxpayfaceCallBack)) {
                return;
            }
            wrapMapWithAARInfo(bean);
            service.updateWxpayfacePayResult(bean, wxpayfaceCallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void reportInfo(Map info, final IWxPayFaceCallbackAIDL wxpayfaceCallBack) {
        try {
            if (tryReconnectService(FacePayConstants.REPORT_INFO, info, wxpayfaceCallBack)) {
                return;
            }
            service.reportInfo(info, wxpayfaceCallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void reportOrder(Map info, final IWxPayFaceCallbackAIDL wxpayfaceCallBack) {
        try {
            if (tryReconnectService(FacePayConstants.REPORT_ORDER, info, wxpayfaceCallBack)) {
                return;
            }
            service.reportOrder(info, wxpayfaceCallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void reportPaycode(Map info, final IWxPayFaceCallbackAIDL wxpayfaceCallBack) {
        try {
            if (tryReconnectService(FacePayConstants.REPORT_PAYCODE, info, wxpayfaceCallBack)){
            return;
        }
        service.dispatchBindApi(API_REPORT_PAYCODE, info, wxpayfaceCallBack);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public void getWxpayfaceUserInfo(final Map bean, final IWxPayFaceCallbackAIDL wxpayfaceCallBack) {
        try {
            if (tryReconnectService(FacePayConstants.GET_WXPAY_FACE_USER_INFO, bean, wxpayfaceCallBack)) {
                return;
            }
            service.getWxpayfaceUserInfo(bean, wxpayfaceCallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void stopWxpayface(final Map bean, final IWxPayFaceCallbackAIDL wxpayfaceCallBack) {
        try {
            if (tryReconnectService(FacePayConstants.STOP_WXPAYFACE, bean, wxpayfaceCallBack)) {
                return;
            }
            service.stopWxpayface(bean, wxpayfaceCallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateWxpayfaceBannerState(final Map bean, final IWxPayFaceCallbackAIDL wxpayfaceCallBack) {
        try {
            if (tryReconnectService(FacePayConstants.UPDATE_WXPAYFACE_BANNER_STATE, bean, wxpayfaceCallBack)) {
                return;
            }
            service.updateWxpayfaceBannerState(bean, wxpayfaceCallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ttsSpeak(final String text) {
        try {
            if (tryReconnectService(FacePayConstants.TTS_SPEAK, text)) {
                return;
            }
            service.ttsSpeak(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMpConfig(IWxPayFaceCallbackAIDL wxpayfaceCallBack){
        try {
            if (tryReconnectService(FacePayConstants.GET_MP_CONFIG, wxpayfaceCallBack)) {
                return;
            }
            service.dispatchBindApi(API_GET_MP_CONFIG, null, wxpayfaceCallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void launchMp(Map bean, IWxPayFaceCallbackAIDL wxpayfaceCallBack){
        try {
            if (tryReconnectService(FacePayConstants.LAUNCH_MP, bean, wxpayfaceCallBack)) {
                return;
            }
            service.dispatchBindApi(API_LAUNCH_MP, bean, wxpayfaceCallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void releaseWxpayface(Context cxt) {
        Log.d(TAG, "releaseWxpayface");
        if(cxt == null){
            Log.e(TAG,"releaseWxpayface cxt is null");
            return;
        }
        try {
            if(service != null) {
                service.releaseWxpayface();
                bd = null;
                StateInfoController.reset();
            }
            cxt.unbindService(conn);
        } catch (Exception e) {
            Log.e(TAG,"releaseWxpayface fail");
        }
        mIsServiceConnected = false;
        mIsServiceConnecting = false;
        mInitCallback = null;
        mContext = null;
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mIsServiceConnected = false;
            mIsServiceConnecting = false;
            connectService();
            Log.d(TAG, "onServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mIsServiceConnected = true;
            mIsServiceConnecting = false;
            if (handler != null) {
                handler.removeMessages(RETRY_CONNECT_OVER_TIME); // 移除超时的兜底逻辑
            }
            bd = binder;
            Log.d(TAG, "onServiceConnected");
            service = IWxPayFaceAIDL.Stub.asInterface(binder);
            try {
                if (mInitCallback != null) {
                    service.initWxpayface(mInitCallback);
                    mInitCallback = null;
                }
                if (StateInfoController.mFunctionName != null) {
                    Log.d(TAG, "onServiceConnected:" + " " + "functionName:" + StateInfoController.mFunctionName);
                    switch (StateInfoController.mFunctionName) {
                        case FacePayConstants.GET_WXPAYFACE_RAWDATA:
                            getWxpayfaceRawdata(StateInfoController.mSpecialCallback);
                            break;
                        case FacePayConstants.GET_WXPAYFACE_CODE:
                            if (StateInfoController.mSecondCb == null) {
                                getWxpayfaceCode(StateInfoController.mInfo, StateInfoController.mSpecialCallback);
                            } else {
                                getWxpayfaceCode(StateInfoController.mInfo, StateInfoController.mSpecialCallback, StateInfoController.mSecondCb);
                            }
                            break;
                        case FacePayConstants.GET_WXPAY_AUTH:
                            getWxpayAuth(StateInfoController.mInfo, StateInfoController.mSpecialCallback);
                            break;
                        case FacePayConstants.START_CODE_SCANNER:
                            startCodeScanner(StateInfoController.mSpecialCallback);
                            break;
                        case FacePayConstants.UPDATE_WXPAYFACE_PAYRESULT:
                            updateWxpayfacePayResult(StateInfoController.mInfo, StateInfoController.mSpecialCallback);
                            break;
                        case FacePayConstants.REPORT_INFO:
                            reportInfo(StateInfoController.mInfo, StateInfoController.mSpecialCallback);
                            break;
                        case FacePayConstants.REPORT_ORDER:
                            reportOrder(StateInfoController.mInfo, StateInfoController.mSpecialCallback);
                            break;
                        case FacePayConstants.REPORT_PAYCODE:
                            reportPaycode(StateInfoController.mInfo, StateInfoController.mSpecialCallback);
                            break;
                        case FacePayConstants.GET_WXPAY_FACE_USER_INFO:
                            getWxpayfaceUserInfo(StateInfoController.mInfo, StateInfoController.mSpecialCallback);
                            break;
                        case FacePayConstants.STOP_WXPAYFACE:
                            stopWxpayface(StateInfoController.mInfo, StateInfoController.mSpecialCallback);
                            break;
                        case FacePayConstants.UPDATE_WXPAYFACE_BANNER_STATE:
                            updateWxpayfaceBannerState(StateInfoController.mInfo, StateInfoController.mSpecialCallback);
                            break;
                        case FacePayConstants.TTS_SPEAK:
                            ttsSpeak(StateInfoController.mTTsSpeakText);
                            break;
                        case FacePayConstants.GET_MP_CONFIG:
                            getMpConfig(StateInfoController.mSpecialCallback);
                            break;
                        case FacePayConstants.LAUNCH_MP:
                            launchMp(StateInfoController.mInfo, StateInfoController.mSpecialCallback);
                            break;
                        case API_ENABLE_FIRE_WALL:
                            enableFirewall(StateInfoController.mInfo, StateInfoController.mSpecialCallback);
                            break;
                        case API_DISABLE_FIRE_WALL:
                            disableFirewall(StateInfoController.mSpecialCallback);
                            break;
                        default:
                            break;
                    }
                    StateInfoController.reset(); // 商户状态信息重置
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    private void connectService() {
        if (mIsServiceConnecting) {
            return;
        }
        // 兜底逻辑
        if (handler == null) {
            handler = new OverTimeHandler();
            handler.sendEmptyMessageDelayed(RETRY_CONNECT_OVER_TIME, 10 * 1000);
        }
        if(mContext==null){
            try {
                mContext=getApplicationUsingReflection().getApplicationContext();
                if(mContext==null){
                    Log.d(TAG,"can not get context");
                    throw new Exception("can not get context");
                }
                bindFaceService(mContext, PROXY_MAP);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            bindFaceService(mContext, PROXY_MAP);
        }

    }

    private boolean tryReconnectService(String functionName) {
        if (bd != null && bd.isBinderAlive()) { // 已连接状态
            return false;
        }
        Log.d(TAG, "tryReconnectService-1:" + " " + "functionName:" + functionName);
        StateInfoController.mFunctionName = functionName;
        // 进行重连
        connectService();
        return true;
    }

    private boolean tryReconnectService(String functionName, String ttsSpeakText) {
        if (bd != null && bd.isBinderAlive()) { // 已连接状态
            return false;
        }
        Log.d(TAG, "tryReconnectService-2:" + " " + "functionName:" + functionName + " " + "ttsSpeakText:" + ttsSpeakText);
        StateInfoController.mFunctionName = functionName;
        StateInfoController.mTTsSpeakText = ttsSpeakText;
        // 进行重连
        connectService();
        return true;
    }

    private boolean tryReconnectService(String functionName, IWxPayFaceCallbackAIDL callbackAIDL) {
        if (bd != null && bd.isBinderAlive()) { // 已连接状态
            return false;
        }
        Log.d(TAG, "tryReconnectService-2:" + " " + "functionName:" + functionName);
        StateInfoController.mFunctionName = functionName;
        StateInfoController.mSpecialCallback = callbackAIDL;
        // 进行重连
        connectService();
        return true;
    }

    private boolean tryReconnectService(String functionName, Map bean, IWxPayFaceCallbackAIDL callbackAIDL) {
        if (bd != null && bd.isBinderAlive()) { // 已连接状态
            wrapMapWithAARInfo(bean);
            return false;
        }
        Log.d(TAG, "tryReconnectService-3:" + " " + "functionName:" + functionName + " " + "map:" + bean);
        StateInfoController. mInfo = bean;
        StateInfoController.mFunctionName = functionName;
        StateInfoController.mSpecialCallback = callbackAIDL;
        // 进行重连
        connectService();
        return true;
    }

    private boolean tryReconnectService(String functionName, Map bean, final IWxPayFaceCallbackAIDL callbackAIDL, final IWxPayFaceCallbackAIDL secondCb) {
        if (bd != null && bd.isBinderAlive()) { // 已连接状态
            wrapMapWithAARInfo(bean);
            return false;
        }
        Log.d(TAG, "tryReconnectService-4:" + " " + "functionName:" + functionName + " " + "map:" + bean);
        StateInfoController.mInfo = bean;
        StateInfoController.mFunctionName = functionName;
        StateInfoController.mSpecialCallback = callbackAIDL;
        StateInfoController.mSecondCb = secondCb;
        // 进行重连
        connectService();
        return true;
    }

    private void initSuccessCallback(IWxPayFaceCallbackAIDL callbackAIDL) {
        HashMap map = new HashMap();
        map.put(RETURN_CODE, RETURN_SUCCESS);
        map.put(RETURN_MSG, RETURN_SUCCESS);
        try {
            callbackAIDL.response(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void initNotSupportCallback(IWxPayFaceCallbackAIDL callbackAIDL) {
//        HashMap map = new HashMap();
//        map.put(RETURN_CODE, RETURN_FAIL);
//        map.put(RETURN_MSG, RETURN_MSG_NOT_SUPPORT);
//        try {
//            callbackAIDL.response(map);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private static void initErrorCallback(IWxPayFaceCallbackAIDL callbackAIDL) {
        if(callbackAIDL == null){
            Log.e(TAG,"initErrorCallback cb is null");
            return;
        }
        HashMap map = new HashMap();
        map.put(RETURN_CODE, RETURN_FAIL);
        if (mIsServiceConnecting) {
            map.put(KEY_RET_COMMON_ERROR_CODE, ERR_FACE_APP_CONNECT_SERVICE_INITING);
            map.put(RETURN_MSG, "刷脸服务初始化中");
        } else {
            map.put(KEY_RET_COMMON_ERROR_CODE, ERR_FACE_APP_CONNECT_SERVICE_NOT_INIT);
            map.put(RETURN_MSG, "刷脸服务未初始化");
        }
        try {
            callbackAIDL.response(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }

    /**
     * 获取支付SDK版本
     * @param context
     * @return
     */
    private String getSdkVersion(Context context){
        try {
            if(context == null){
                return null;
            }
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo("com.tencent.wxpayface", 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 因为bind本身不一定调用，如果先后A，B调用bind，只有A调用时FaceConnectService会执行onBind，B不会
     * //https://blog.csdn.net/u013553529/article/details/54754491
     * 如果info不为空，直接包装部分AAR信息给到Service
     * @param info
     */
    private void wrapMapWithAARInfo(Map info){
        if(info != null){
            info.put(FacePayConstants.KEY_REQ_PARAMS_AAR_VERSION,AAR_VERSION_NAME);
        }
    }

    /**
     * 比较两个版本号 从前向后比较 版本号格式为 xxx.xxx.xxx.xxx
     * @return 1 version1 > version2
     * @return 0 version1 == version2
     * @return -1 version1 < version2
     * */
    private static int compareVersion(String version1, String version2){

        if (TextUtils.isEmpty(version1) && TextUtils.isEmpty(version2)) {
//            PayLog.i(TAG,"version1: " + version1 + " version2: " + version2 + " return 0");
            return 0;
        }

        if (TextUtils.isEmpty(version1)){
            return -1;
        }

        if (TextUtils.isEmpty(version2)){
            return 1;
        }

//        PayLog.i(TAG,"version1: " + version1 + " version2: " + version2);
        String[] version1s = version1.split("\\.");
        String[] version2s = version2.split("\\.");

        int maxLen = version1s.length > version2s.length ? version1s.length : version2s.length;

        int result = 0;
        int[] version1ints = new int[maxLen];
        int[] version2ints = new int[maxLen];

        try {
            for (int index = 0; index < maxLen; index++){
                if (index < version1s.length){
                    version1ints[index] = Integer.valueOf(version1s[index]);
                }
                if (index < version2s.length){
                    version2ints[index] = Integer.valueOf(version2s[index]);
                }
                if (version1ints[index] > version2ints[index]){
                    result = 1;
                    break;
                }else if (version1ints[index] == version2ints[index]){
                    continue;
                }else {
                    result = -1;
                    break;
                }
            }
        }catch (Exception e){

        }

        return result;
    }

    /**
     * 商户状态信息管控类
     */
    private static class StateInfoController {
        private static String mFunctionName; // 方法名
        private static String mTTsSpeakText; // tts语音文本
        private static IWxPayFaceCallbackAIDL mSpecialCallback; // 覆盖安装/进程被杀/Crash导致的缓存商户回调对象
        private static IWxPayFaceCallbackAIDL mSecondCb; // 覆盖安装/进程被杀/Crash导致的缓存商户回调对象
        private static Map mInfo; // 覆盖安装/进程被杀/Crash导致的缓存商户Map对象

        public static void reset() {
            mFunctionName = null;
            mTTsSpeakText = null;
            mSpecialCallback = null;
            mSecondCb = null;
            mInfo = null;
        }
    }

    public void getUserPayScoreStatus(Map info, final IWxPayFaceCallbackAIDL wxpayfaceCallBack) {
        try {
            if (!mIsServiceConnected) {
                tryReconnectService(GET_USER_PAYSCORE_STATUS, wxpayfaceCallBack);
                return;
            }
            service.dispatchBindApi("getUserPayScoreStatus", info, wxpayfaceCallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果使用防火墙，则调用该函数把一些域名转换成ip
     * @param mchId 商户Id
     * @param subMchId 子商户Id
     * @param wxpayfaceCallBack 回调
     */
    public void enableFirewall(String mchId, String subMchId, final IWxPayFaceCallbackAIDL wxpayfaceCallBack) {
        HashMap map = new HashMap();
        if (!TextUtils.isEmpty(mchId)) {
            map.put(FacePayConstants.KEY_REQ_PARAMS_MCHID, mchId);
        }
        if (!TextUtils.isEmpty(subMchId)) {
            map.put(FacePayConstants.KEY_REQ_PARAMS_SUB_MCH_ID, subMchId);
        }
        enableFirewall(map, wxpayfaceCallBack);
    }

    public void enableFirewall(Map info, final IWxPayFaceCallbackAIDL wxpayfaceCallBack) {
        try {
            String wxVersion = getSdkVersion(mContext);
            if (wxVersion!= null && compareVersion(wxVersion, "2.20.0.0") < 0) {
                HashMap map = new HashMap();
                map.put(RETURN_CODE, RETURN_FAIL);
                map.put(RETURN_MSG, "SDK版本过低，请更新SDK");
                wxpayfaceCallBack.response(map);
                return;
            }
            if (tryReconnectService(API_ENABLE_FIRE_WALL, info, wxpayfaceCallBack)) {
                return;
            }
            service.dispatchBindApi(API_ENABLE_FIRE_WALL, info, wxpayfaceCallBack);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void disableFirewall(final IWxPayFaceCallbackAIDL wxpayfaceCallBack) {
        try {
            String wxVersion = getSdkVersion(mContext);
            if (wxVersion!= null && compareVersion(wxVersion, "2.20.0.0") < 0) {
                HashMap map = new HashMap();
                map.put(RETURN_CODE, RETURN_FAIL);
                map.put(RETURN_MSG, "SDK版本过低，请更新SDK");
                wxpayfaceCallBack.response(map);
                return;
            }
            if (tryReconnectService(API_DISABLE_FIRE_WALL, wxpayfaceCallBack)) {
                return;
            }
            service.dispatchBindApi(API_DISABLE_FIRE_WALL, new HashMap(), wxpayfaceCallBack);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    public static Application getApplicationUsingReflection() throws Exception {
        return (Application) Class.forName("android.app.AppGlobals")
                .getMethod("getInitialApplication").invoke(null, (Object[]) null);
    }
}


