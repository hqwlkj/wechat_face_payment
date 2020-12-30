package com.parsec.wxfacepay;

import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.parsec.wxfacepay.entry.FaceAuthResp;
import com.parsec.wxfacepay.entry.FacePayResq;
import com.parsec.wxfacepay.entry.FacePayResult;
import com.parsec.wxfacepay.entry.FaceResult;
import com.parsec.wxfacepay.utils.ICallback;
import com.parsec.wxfacepay.utils.LoggerUtil;
import com.parsec.wxfacepay.utils.ThreadUtils;
import com.parsec.wxfacepay.utils.Tools;
import com.tencent.wxpayface.IWxPayfaceCallback;
import com.tencent.wxpayface.WxPayFace;
import com.tencent.wxpayface.WxfacePayCommonCode;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Author:yin.juan
 * Time:2020/8/18 11:33
 * Description:[一句话描述]
 */
public class WxFacePayServer {

    //
    private String mRawData; //微信sdk返回的验证信息
    private long anInt = 0L;
    private String server_path = WxConstant.BASE_URL;
    private OkHttpClient client  = new OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .writeTimeout(12, TimeUnit.SECONDS)
            .readTimeout(12, TimeUnit.SECONDS)
            .build();
    private String mAuthinfo = ""; //微信刷脸支付的凭证
    private HashMap<String, String> mFaceMap; //调用刷脸需要的map
    private String face_code;
    private String openid;

    private FaceAuthResp faceAuthResp;

    /**
     * 第二步 获取RawData
     * @param context
     * @param callback
     */
    public void getWxPayFaceRawData(Context context, String serverPath, final ICallback callback) {
        anInt = System.currentTimeMillis();
        server_path = serverPath;
        WxPayFace.getInstance().getWxpayfaceRawdata(new IWxPayfaceCallback() {
            @Override
            public void response(Map map) throws RemoteException {
                if (!Tools.isSuccessInfo(map)) {
                    LoggerUtil.i_file("rawData获取失败" + "map.get():" + map.get("return_code") + "--msg:" + map.get("return_msg"));
                    FaceResult faceResult = new FaceResult();
                    faceResult.setReturn_msg("rawData获取失败： " + map.get("return_code") + "--msg:" + map.get("return_msg"));
                    faceResult.setReturn_code("-1");
                    faceResult.setFace_type("1");
                    faceResultToH5(faceResult, callback);
                } else {
                    //获取到RAW_DATA
                    if (map.get(WxConstant.RAW_DATA) != null) {
                        String rawdata = map.get(WxConstant.RAW_DATA).toString();
                        LoggerUtil.i_file("rawData获取成功");
                        getWxPayFaceAuthInfo(rawdata, callback);
                    } else {
                        LoggerUtil.i_file("rawData获取失败");
                        FaceResult faceResult = new FaceResult();
                        faceResult.setReturn_msg("rawData获取失败");
                        faceResult.setReturn_code("-1");
                        faceResult.setFace_type("1");
                        faceResultToH5(faceResult, callback);
                    }
                }
            }
        });
    }

    /**
     * 启动扫码
     * @param context
     * @param callback
     */
    public void startCodeScanner(Context context, final ICallback callback) {
        WxPayFace.getInstance().startCodeScanner(new IWxPayfaceCallback(){

            @Override
            public void response(Map info) throws RemoteException {
                if (!Tools.isSuccessInfo(info)){
                    String return_code = (String) info.get("return_code");
                    Integer err_code = (Integer) info.get("err_code");
                    String return_msg = (String) info.get("return_msg");
                    String code_msg = (String) info.get("code_msg");
                    String resultString = "startCodeScanner, return_code : " + return_code + " return_msg : " + return_msg + " err_code: " + err_code+ " code_msg: " + code_msg;
                    LoggerUtil.i(resultString);
                    FaceResult faceResult = new FaceResult();
                    faceResult.setReturn_msg(resultString);
                    faceResult.setReturn_code(return_code);
                    faceResult.setFace_type("1");
                    faceResultToH5(faceResult, callback);
                    /**
                     这里添加业务自定义的工作即可 注意该回调在异步线程
                     **/
                } else {
                    FaceResult faceResult = new FaceResult();
                    String return_code = (String) info.get("return_code");
                    String return_msg = (String) info.get("return_msg");
                    String code_msg = (String) info.get("code_msg");
                    faceResult.setReturn_msg(return_msg);
                    faceResult.setReturn_code(return_code);
                    faceResult.setCode_msg(code_msg);
                    faceResultToH5(faceResult, callback);
                }
            }
        });
    }

    /**
     * 第三步 获取调用凭证
     * @param mRawData
     * @param callback
     */
    private void getWxPayFaceAuthInfo(String mRawData, final ICallback callback) {
        try {
            String path = server_path + "/wechat/getWxFaceAuthInfo?dev_id="
                    + android.os.Build.SERIAL + "&raw_data=" + URLEncoder.encode(mRawData, "utf-8") + "&his_cd=1";
            LoggerUtil.i("请求地址："+ path);
            Request request = new Request.Builder()
                    .url(path)
                    .get()
                    .build();
            LoggerUtil.i(path);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LoggerUtil.i("获取人脸凭证接口请求失败" + e.toString());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
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
//                             mFaceMap.put("face_code_type", "1");
                            // mFaceMap.put("ignore_update_pay_result", "0");
                            mFaceMap.put("face_authtype", "FACEPAY");
                            mFaceMap.put("authinfo", mAuthinfo);
                            mFaceMap.put("ask_face_permit", "1");
                            //  mFaceMap.put("ask_ret_page", "1");
                            LoggerUtil.i("139:" + new Gson().toJson(mFaceMap));
                            getWxPayFaceCodeByCamera(mFaceMap, callback);
                        } else {
                            LoggerUtil.i("获取人脸凭证失败");
                            FaceResult faceResult = new FaceResult();
                            faceResult.setReturn_msg("获取人脸凭证失败");
                            faceResult.setFace_code("ERROR");
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
     * 第四步 调用摄像头进行刷脸
     *
     * @param map
     */
    private void getWxPayFaceCodeByCamera(HashMap<String, String> map, final ICallback callback) {
        WxPayFace.getInstance().getWxpayfaceCode(map, new IWxPayfaceCallback() {
            @Override
            public void response(final Map map) {
                final String code = (String) map.get(WxConstant.RETURN_CODE);
                String message = (String) map.get(WxConstant.RETURN_MSG);
                LoggerUtil.i(code + "-220-" + message);
                LoggerUtil.i(new Gson().toJson(map));
                FaceResult faceResult = new FaceResult();
                if (!Tools.isSuccessInfo(map)) {
                    faceResult.setReturn_code(code);
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
                            FaceResult faceResult = new FaceResult();
                            faceResult.setReturn_code(code);
                            if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS)) {
                                face_code = (String) map.get(WxConstant.FACE_CODE);
                                LoggerUtil.i("205:"+face_code);
                                openid = (String) map.get(WxConstant.OPEN_ID);
                                // String sub_open_id = (String) map.get(WxConstant.SUB_OPEN_ID);
                                //刷脸成功
                                faceResult.setFace_code(face_code);
                                faceResult.setOpenid(openid);
//                                faceResultToH5(faceResult, callback);
                                WxFacePayfor(callback);
                            } else if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_USER_CANCEL)) {
                                //用户取消
                                LoggerUtil.e("刷脸失败：用户取消");
                                faceResult.setReturn_msg("刷脸失败：用户取消");
                                faceResultToH5(faceResult, callback);
                            } else if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_SCAN_PAYMENT)) {
                                //扫码支付
                                LoggerUtil.e("刷脸失败--扫码支付");
                                faceResult.setReturn_msg("刷脸失败：扫码支付");
                                faceResultToH5(faceResult, callback);
                            } else if (TextUtils.equals(code, "FACEPAY_NOT_AUTH")) {
                                //无即时支付无权限
                                LoggerUtil.e("刷脸失败--用户无即时支付无权限");
                                faceResult.setReturn_msg("刷脸失败：用户无即时支付无权限");
                                faceResultToH5(faceResult, callback);
                            } else {
                                //失败
                                LoggerUtil.e("刷脸失败");
                                faceResult.setReturn_msg("刷脸失败");
                                faceResultToH5(faceResult, callback);
                            }
                        }
                    });
                }
            }
        });
    }




    /**
     * 微信刷脸支付
     * @param callback
     */
    private void WxFacePayfor(final ICallback callback) {
        try {
            RequestBody requestBody = new FormBody.Builder()
                    .add("face_code", URLEncoder.encode(face_code, "utf-8"))
                    .add("his_cd", "2")
                    .add("open_id", openid)
                    .add("order_id", anInt + "")
                    .add("total_fee", 1 + "")
                    .build();
            FacePayResq facePayResq = new FacePayResq();
            facePayResq.setFace_code(face_code);
            facePayResq.setHis_cd("2");
            facePayResq.setOpen_id(openid);
            facePayResq.setOrder_id(anInt + "");
            facePayResq.setTotal_fee(1 + ""); // 订单的具体金额 分
            LoggerUtil.i(new Gson().toJson(facePayResq));
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, new Gson().toJson(facePayResq));

            Request request = new Request.Builder()
                    .url(server_path + "/wechat/facePay")
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, IOException e) {
                    LoggerUtil.i_file("微信刷脸支付失败" + e.toString());
                    FaceResult faceResult = new FaceResult();
                    faceResult.setReturn_code("ERROR");
                    faceResult.setReturn_msg("微信刷脸支付失败" + e.toString());
                    faceResultToH5(faceResult,callback);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String payResult = Objects.requireNonNull(response.body()).string();
                    LoggerUtil.i_file("payResult: " + payResult);
                    try {
                        FacePayResult facePayResult = new Gson().fromJson(payResult, FacePayResult.class);
                        if (facePayResult != null && !TextUtils.isEmpty(facePayResult.getTransaction_id())) {
                            WxPayFaceResult(payResult, callback);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            LoggerUtil.i(e.toString());
        }


    }

    /**
     * 支付结果
     * @param payVoucher
     * @param callback
     */
    private void WxPayFaceResult(String payVoucher, final ICallback callback) {

        Map<String, String> map = new HashMap<>();
        map.put("appid", faceAuthResp.getAppid());
        //  map.put("sub_appid", "");
        //     map.put("sub_openid", "");
        map.put("mch_id", faceAuthResp.getMch_id());
        map.put("store_id", faceAuthResp.getStore_id());
        map.put("authinfo", mAuthinfo);
        map.put("payresult", "SUCCESS");

        WxPayFace.getInstance().updateWxpayfacePayResult(map, new IWxPayfaceCallback() {
            @Override
            public void response(Map map) throws RemoteException {
                String returnCode = map.get(WxConstant.RETURN_CODE).toString();
                String returnMsg = map.get(WxConstant.RETURN_MSG).toString();
                LoggerUtil.i("支付结果查询：" + returnCode + "---" + returnMsg);
                FaceResult faceResult = new FaceResult();
                faceResult.setReturn_code(returnCode);
                faceResult.setReturn_msg(returnMsg);
                faceResultToH5(faceResult, callback);
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

