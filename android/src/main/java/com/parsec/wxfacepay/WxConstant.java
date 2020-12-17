package com.parsec.wxfacepay;


public class WxConstant {


    /*获取微信人脸识别凭证*/
    public static final String getFaceAuthInfo = "https://payapp.weixin.qq.com/face/get_wxpayface_authinfo";
    public static final String getFacePay = "https://api.mch.weixin.qq.com/pay/facepay";

    //正式环境
    public static final String BASE_URL= "https://mp.hipay365.com/";
//    public static final String BASE_URL= "http://parsec.cqkqinfo.com/app/stage-exhibition-api/face/";


    /*人脸支付  返回码*/
    public static final String RETURN_CODE = "return_code";
    /*人脸支付 错误信息*/
    public static final String RETURN_MSG = "return_msg";

    /*人脸支付第二步获取rawdata*/
    public static final String RAW_DATA = "rawdata";

    /*人脸识别 获取到face Code*/
    public static final String FACE_CODE = "face_code";

    /*人脸识别 获取到face Code*/
    public static final String FACE_SID = "face_sid";

    public static final String OPEN_ID = "openid";
    public static final String SUB_OPEN_ID = "sub_openid";


}
