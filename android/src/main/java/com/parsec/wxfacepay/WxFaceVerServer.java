package com.parsec.wxfacepay;

import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.parsec.wxfacepay.entry.FaceAuthResp;
import com.parsec.wxfacepay.entry.FaceCodeRequest;
import com.parsec.wxfacepay.entry.FaceResult;
import com.parsec.wxfacepay.utils.ICallback;
import com.parsec.wxfacepay.utils.LoggerUtil;
import com.parsec.wxfacepay.utils.ThreadUtils;
import com.parsec.wxfacepay.utils.Tools;
import com.tencent.wxpayface.IWxPayfaceCallback;
import com.tencent.wxpayface.WxPayFace;
import com.tencent.wxpayface.WxfacePayCommonCode;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author:yin.juan
 * Time:2020/8/18 11:33
 * Description:[一句话描述]
 */
public class WxFaceVerServer {

    //初始化微信人脸支付
    public static final String FACE_PAY_INIT = "com.ysf.wx.initWxpayface";
    //获取微信人脸支付rawdata
    public static final String FACE_PAY_RAW_DATA = "com.ysf.wx.getWxpayfaceRawdata";
    //微信人脸支付刷脸获取face_code
    public static final String FACE_PAY_FACE_CODE = "com.ysf.wx.getWxpayfaceCode";

    //
    private String mRawData; //微信sdk返回的验证信息
    private long anInt = 0L;
    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .writeTimeout(12, TimeUnit.SECONDS)
            .readTimeout(12, TimeUnit.SECONDS)
            .build();
    private String mAuthinfo = ""; //微信刷脸支付的凭证
    private HashMap<String, String> mFaceMap; //调用刷脸需要的map
    private String face_sid;
    private String openid;

    private FaceCodeRequest mFaceJson;


    /**
     * 初始化微信刷脸插件
     * @param context
     * @param callback
     */
    public void initWxFacePay(Context context, final ICallback callback) {
        Map<String, String> m1 = new HashMap<>();
        WxPayFace.getInstance().initWxpayface(context, new IWxPayfaceCallback() {
            @Override
            public void response(Map map) throws RemoteException {
                if (map != null) {
                    String code = (String) map.get(WxConstant.RETURN_CODE);
                    String msg = (String) map.get(WxConstant.RETURN_MSG);
                    Log.i("initWxFacePay:", msg);
                    if (!(code != null && code.equals(WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS))) {
                        //初始化失败
                        LoggerUtil.i_file("initWxPayFace 初始化失败");
                        Map<String, Object> params = new HashMap<String, Object>();
                        params.put("message", "初始化失败");
                        params.put("code", "ERROR");
                        callback.callback(params);
                    } else {
                        //初始化
                        LoggerUtil.i_file("initWxPayFace 初始化成功");
                        Map<String, Object> params = new HashMap<String, Object>();
                        params.put("message", "初始化成功");
                        params.put("code", "SUCCESS");
                        callback.callback(params);
                    }
                }
            }
        });


    }

    //第二步 获取RawData
    public void getWxPayFaceRawData(Context context, final ICallback callback) {
        anInt = System.currentTimeMillis();
        WxPayFace.getInstance().getWxpayfaceRawdata(new IWxPayfaceCallback() {
            @Override
            public void response(Map map) throws RemoteException {
                String code = (String) map.get(WxConstant.RETURN_CODE);
                String msg = (String) map.get(WxConstant.RETURN_MSG);
                if (!Tools.isSuccessInfo(map)) {
                    LoggerUtil.i_file("rawData获取失败" + "map.get():" + map.get("return_code") + "--msg:" + map.get("return_msg"));
                    FaceResult faceResult = new FaceResult();
                    faceResult.setReturn_msg("rawData获取失败： " + map.get("return_code") + "--msg:" + map.get("return_msg"));
                    faceResult.setReturn_code(code);
                    faceResult.setFace_type("1");
                    faceResultToH5(faceResult, callback);
                } else {
                    //获取到RAW_DATA
                    if (map.get(WxConstant.RAW_DATA) != null) {
                        String rawdata = map.get(WxConstant.RAW_DATA).toString();
                        LoggerUtil.i_file("rawData获取成功");
                        LoggerUtil.i_file(rawdata);
                        getWxPayFaceAuthInfo(rawdata, callback);
                    } else {
                        LoggerUtil.i_file("rawData获取失败");
                        FaceResult faceResult = new FaceResult();
                        faceResult.setReturn_msg(msg);
                        faceResult.setReturn_code(code);
                        faceResult.setFace_type("1");
                        faceResultToH5(faceResult, callback);
                    }
                }
            }
        });
    }

    /**
     * 第三步
     * @param mRawData
     * @param callback
     */
    private void getWxPayFaceAuthInfo(String mRawData, final ICallback callback) {
        try {
            String path = WxConstant.BASE_URL + "wechat/getWxFaceAuthInfo?dev_id="
                    + android.os.Build.SERIAL + "&raw_data=" + URLEncoder.encode(mRawData, "utf-8") + "&his_cd=1";
            LoggerUtil.i("请求地址："+ path);
            Request request = new Request.Builder()
                    .url(path)
                    .get()
                    .build();
            LoggerUtil.i(path);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    LoggerUtil.i("获取人脸凭证接口请求失败" + e.toString());
                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    String payVoucher = response.body().string();
                    LoggerUtil.i("获取到的微信支付凭证:" + payVoucher);
                    try {
                        FaceAuthResp faceAuthResp = new Gson().fromJson(payVoucher, FaceAuthResp.class);
                        LoggerUtil.i(faceAuthResp.toString());
                        if (!TextUtils.isEmpty(faceAuthResp.getAppid())) {
                            mAuthinfo = faceAuthResp.getAuthinfo();
                            mFaceMap = new HashMap<>();
                            mFaceMap.put("appid", faceAuthResp.getAppid());
                            mFaceMap.put("mch_id", faceAuthResp.getMch_id());
                            mFaceMap.put("sub_appid", faceAuthResp.getSub_app_id());
                            mFaceMap.put("sub_mch_id", faceAuthResp.getSub_mch_id());
                            mFaceMap.put("store_id", faceAuthResp.getStore_id());
                            // mFaceMap.put("telephone", iphone);
                            mFaceMap.put("out_trade_no", anInt + "");
                            mFaceMap.put("total_fee", 1 + "");
                            // mFaceMap.put("face_code_type", "1");
                            // mFaceMap.put("ignore_update_pay_result", "0");
                            mFaceMap.put("face_authtype", "FACE_AUTH");
                            mFaceMap.put("authinfo", mAuthinfo);
                            mFaceMap.put("ask_face_permit", "1");
                            //  mFaceMap.put("ask_ret_page", "1");
                            getWxPayFaceCodeByCamera(mFaceMap, callback);
                        } else {
                            LoggerUtil.i("获取人脸凭证失败");
                            FaceResult faceResult = new FaceResult();
                            faceResult.setReturn_msg("获取人脸凭证失败");
                            faceResult.setReturn_code("ERROR");
                            faceResultToH5(faceResult, callback);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LoggerUtil.i("获取人脸凭证失败：" + e.getMessage());
                        FaceResult faceResult = new FaceResult();
                        faceResult.setReturn_msg("获取人脸凭证失败：" + e.getMessage());
                        faceResult.setReturn_code("ERROR");
                        faceResultToH5(faceResult, callback);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 调用摄像头进行刷脸
     *
     * @param map
     */
    private void getWxPayFaceCodeByCamera(HashMap<String, String> map, final ICallback callback) {
        WxPayFace.getInstance().getWxpayfaceCode(map, new IWxPayfaceCallback() {
            @Override
            public void response(final Map map) {
                final String code = (String) map.get(WxConstant.RETURN_CODE);
                String message = (String) map.get(WxConstant.RETURN_MSG);
                LoggerUtil.i(code + "--" + message);
                FaceResult faceResult = new FaceResult();
                if (!Tools.isSuccessInfo(map)) {
                    faceResult.setReturn_code("ERROR");
                    if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_USER_CANCEL)) {
                        //用户取消
                        LoggerUtil.e("刷脸失败：用户取消");
                        faceResult.setReturn_msg("刷脸失败：用户取消");
                    } else if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_SCAN_PAYMENT)) {
                        //扫码支付
                        LoggerUtil.e("刷脸失败--扫码支付");
                        faceResult.setReturn_msg("刷脸失败:扫码支付");
                    } else if (TextUtils.equals(code, "FACEPAY_NOT_AUTH")) {
                        //无即时支付无权限
                        LoggerUtil.e("刷脸失败--用户无即时支付无权限");
                        faceResult.setReturn_msg("刷脸失败:用户无即时支付无权限");
                    } else {
                        //失败
                        LoggerUtil.e("刷脸失败");
                        faceResult.setReturn_msg("刷脸失败");
                    }
                    faceResultToH5(faceResult, callback);
                } else {
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS)) {
                                face_sid = (String) map.get(WxConstant.FACE_SID);
                                openid = (String) map.get(WxConstant.OPEN_ID);
                                // String sub_open_id = (String) map.get(WxConstant.SUB_OPEN_ID);
                                //刷脸成功
                                LoggerUtil.i_file("faceSid: =" + face_sid);
                                HashMap<String, String> mapAuth = new HashMap<>();
                                mapAuth.put(WxConstant.FACE_SID, face_sid);
                                mapAuth.put(WxConstant.OPEN_ID, openid);
                                mapAuth.put("authinfo", mAuthinfo);
                                getWxAuth(mapAuth,callback);
                            } else if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_USER_CANCEL)) {
                                //用户取消
                                LoggerUtil.e("刷脸失败：用户取消");
                                FaceResult faceResult = new FaceResult();
                                faceResult.setReturn_msg("刷脸失败：用户取消");
                                faceResult.setReturn_code("ERROR");
                                faceResultToH5(faceResult, callback);
                            } else if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_SCAN_PAYMENT)) {
                                //扫码支付
                                LoggerUtil.e("刷脸失败--扫码支付");
                                FaceResult faceResult = new FaceResult();
                                faceResult.setReturn_msg("刷脸失败：扫码支付");
                                faceResult.setReturn_code("ERROR");
                                faceResultToH5(faceResult, callback);
                            } else if (TextUtils.equals(code, "FACEPAY_NOT_AUTH")) {
                                //无即时支付无权限
                                LoggerUtil.e("刷脸失败--用户无即时支付无权限");
                                FaceResult faceResult = new FaceResult();
                                faceResult.setReturn_msg("刷脸失败：用户无即时支付无权限");
                                faceResult.setReturn_code("ERROR");
                                faceResultToH5(faceResult, callback);
                            } else {
                                //失败
                                LoggerUtil.e("刷脸失败");
                                FaceResult faceResult = new FaceResult();
                                faceResult.setReturn_msg("刷脸失败");
                                faceResult.setReturn_code("ERROR");
                                faceResultToH5(faceResult, callback);
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 获取用户授权信息
     * @param mapAuth
     * @param callback
     */
    private void getWxAuth(final HashMap<String, String> mapAuth, final  ICallback callback) {

        WxPayFace.getInstance().getWxpayAuth(mapAuth, new IWxPayfaceCallback() {
            @Override
            public void response(Map map) throws RemoteException {
                final String code = (String) map.get(WxConstant.RETURN_CODE);
                String message = (String) map.get(WxConstant.RETURN_MSG);
                Log.d("WxUserInfoActivity", "code: " + code);
                Log.d("WxUserInfoActivity", "message: " + message);
                FaceResult faceResult = new FaceResult();
                if (code.equals("SUCCESS")) {
                    LoggerUtil.i_file("获取face_sid成功");
                } else if (code.equals("USER_CANCEL")) {
                    LoggerUtil.i_file("用户退出实名认证");
                }
                faceResult.setFace_sid(mapAuth.get(WxConstant.FACE_SID));
                faceResult.setOpenid(mapAuth.get(WxConstant.OPEN_ID));
                faceResult.setReturn_code(code);
                faceResult.setReturn_msg(message);
                faceResultToH5(faceResult, callback);
            }
        });
    }



    public void getWxpayfaceCode(final HashMap<String, String> hashmap, final ICallback callback) {
        //打印Map
        for (String key : hashmap.keySet()) {
            String value = hashmap.get(key);
            LoggerUtil.i("key:" + key + "   vaule:" + value);
        }

        WxPayFace.getInstance().getWxpayfaceCode(hashmap, new IWxPayfaceCallback() {
            @Override
            public void response(Map map) {
                final String code = (String) map.get(WxConstant.RETURN_CODE);
                String message = (String) map.get(WxConstant.RETURN_MSG);
                LoggerUtil.i(code + "--" + message);
                FaceResult faceResult = new FaceResult();
                if (!Tools.isSuccessInfo(map)) {
                    if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_USER_CANCEL)) {
                        //用户取消
                        LoggerUtil.e("刷脸失败：用户取消");
                        faceResult.setReturn_code("-1");
                        faceResult.setReturn_msg("刷脸失败：用户取消");
                        faceResult.setFace_type("2");
                        faceResultToH5(faceResult, callback);
                    } else if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_SCAN_PAYMENT)) {
                        //扫码支付
                        LoggerUtil.e("刷脸失败--扫码支付");
                        faceResult.setReturn_code("-1");
                        faceResult.setReturn_msg("刷脸失败：扫码支付");
                        faceResult.setFace_type("2");
                        faceResultToH5(faceResult, callback);
                    } else if (TextUtils.equals(code, "FACEPAY_NOT_AUTH")) {
                        //无即时支付无权限
                        LoggerUtil.e("刷脸失败--用户无即时支付无权限");
                        faceResult.setReturn_code("-1");
                        faceResult.setReturn_msg("刷脸失败：用户无即时支付无权限");
                        faceResult.setFace_type("2");
                        faceResultToH5(faceResult, callback);
                    } else {
                        //失败
                        LoggerUtil.e("刷脸失败");
                        faceResult.setReturn_code("-1");
                        faceResult.setReturn_msg("刷脸失败：其他");
                        faceResult.setFace_type("2");
                        faceResultToH5(faceResult, callback);
                    }
                } else {
                    if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS)) {
                        if (map.get("face_authtype").equals("FACEPAY")) {
                            String face_code = (String) map.get(WxConstant.FACE_CODE);
                            String openid = (String) map.get(WxConstant.OPEN_ID);
                            //刷脸成功
                            faceResult.setFace_code(face_code);
                            faceResult.setOpenid(openid);
                            faceResult.setReturn_code("0");
                            faceResult.setReturn_msg("刷脸成功");
                            faceResult.setFace_type("2");
                            faceResultToH5(faceResult, callback);
                        } else if (map.get("face_authtype").equals("FACE_AUTH")) {
                            String face_sid = (String) map.get(WxConstant.FACE_SID);
                            String openid = (String) map.get(WxConstant.OPEN_ID);
                            //刷脸成功
                            faceResult.setFace_sid(face_sid);
                            faceResult.setOpenid(openid);
                            faceResult.setReturn_code("0");
                            faceResult.setFace_type("3");
                            faceResult.setReturn_msg("刷脸成功");
                            faceResultToH5(faceResult, callback);

                            //实名验证--授权
                            HashMap<String, String> mapAuth = new HashMap<>();
                            mapAuth.put("face_sid", face_sid);
                            mapAuth.put("authinfo", (String) hashmap.get("authinfo"));
                            getWxpayAuth(mapAuth, callback);
                        }

                    } else if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_USER_CANCEL)) {
                        //用户取消
                        LoggerUtil.e("刷脸失败：用户取消");
                        faceResult.setReturn_code("-1");
                        faceResult.setReturn_msg("刷脸失败：用户取消");
                        faceResult.setFace_type("2");
                        faceResultToH5(faceResult, callback);
                    } else if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_SCAN_PAYMENT)) {
                        //扫码支付
                        LoggerUtil.e("刷脸失败--扫码支付");
                        faceResult.setReturn_code("-1");
                        faceResult.setReturn_msg("刷脸失败：扫码支付");
                        faceResult.setFace_type("2");
                        faceResultToH5(faceResult, callback);
                    } else if (TextUtils.equals(code, "FACEPAY_NOT_AUTH")) {
                        //无即时支付无权限
                        LoggerUtil.e("刷脸失败--用户无即时支付无权限");
                        faceResult.setReturn_code("-1");
                        faceResult.setReturn_msg("刷脸失败：用户无即时支付无权限");
                        faceResult.setFace_type("2");
                        faceResultToH5(faceResult, callback);
                    } else {
                        //失败
                        LoggerUtil.e("刷脸失败");
                        faceResult.setReturn_code("-1");
                        faceResult.setReturn_msg("刷脸失败：其他");
                        faceResult.setFace_type("2");
                        faceResultToH5(faceResult, callback);
                    }
                }

            }

        });

    }

    /**
     * 实名验证--授权
     */
    private void getWxpayAuth(HashMap<String, String> mapAuth, final ICallback callback) {
        for (String key : mapAuth.keySet()) {
            String value = mapAuth.get(key);
            LoggerUtil.i("key:" + key + "   vaule:" + value);
        }
        WxPayFace.getInstance().getWxpayAuth(mapAuth, new IWxPayfaceCallback() {
            @Override
            public void response(Map map) throws RemoteException {
                final String code = (String) map.get(WxConstant.RETURN_CODE);
                String message = (String) map.get(WxConstant.RETURN_MSG);
                LoggerUtil.i_file(code);
                LoggerUtil.i_file(message);
                if (code.equals("SUCCESS")) {
                    callback.callback(map);
                } else if (code.equals("USER_CANCEL")) {
                    callback.callback(map);
                }

            }
        });
    }

    /**
     * 返回刷脸结果
     */
    private void faceResultToH5(FaceResult faceResult, final ICallback callback) {
        Map<String, Object> params = new HashMap<String, Object>();
        LoggerUtil.i(new Gson().toJson(faceResult));
        params.put("message", faceResult.getReturn_msg());
        params.put("code", faceResult.getReturn_code());
        params.put("data", new Gson().toJson(faceResult));
        callback.callback(params);

    }
}

