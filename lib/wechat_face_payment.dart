import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

class WechatFacePayment {
  final String resultCode;
  final String returnMsg;
  final String faceAuthType;
  final String subOpenid;
  final String subAppid;
  final int underageState;
  final int telephoneUsed;
  final String faceCode;
  final String faceSid;
  final String openid;
  final String nickname;
  final String token;
  final String unionidMsg;
  final String unionidCode;
  final String headImgurl;
  static const MethodChannel _channel =
      const MethodChannel('wechat_face_payment');

  WechatFacePayment(
      {this.resultCode,
      this.returnMsg,
      this.faceAuthType,
      this.subOpenid,
      this.underageState,
      this.telephoneUsed,
      this.subAppid,
      this.headImgurl,
      this.faceCode,
      this.faceSid,
      this.openid,
      this.nickname,
      this.token,
      this.unionidMsg,
      this.unionidCode});

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  ///
  /// 初始化
  /// 更多参数说明查看  https://pay.weixin.qq.com/wiki/doc/wxfacepay/develop/android/facepay.html
  static Future<WechatFacePayment> initFacePay(
      String appId,
      String mchId,
      String storeId,
      String telPhone,
      String openId,
      String outTradeNo,
      String totalFee,
      String faceAuthType) async {
    final Map<String, dynamic> map =
        await _channel.invokeMapMethod('initFacePay', {
      "appId": appId,
      "mchId": mchId,
      "storeId": storeId,
      "telPhone": telPhone,
      "openId": openId,
      "outTradeNo": outTradeNo,
      "totalFee": totalFee,
      "faceAuthType": faceAuthType,
    });
    print(map);
    return WechatFacePayment(
      resultCode: map['code'],
      returnMsg: map['message']
    );
  }


  /// 人脸识别获取 face_sid 和 opneid
  static Future<Map<String, dynamic>> wxFaceVerify() async {
    final Map<String, dynamic> result = await _channel.invokeMapMethod('faceVerified');
    return result;
  }

  static Future<Map<String, dynamic>> wxFacePay() async {
    final Map<String, dynamic> result = await _channel.invokeMapMethod('wxFacePay');
    return result;
  }

  ///
  /// 微信扫码
  ///
  static Future<Map<String, dynamic>> wxScanCode() async {
    final Map<String, dynamic> result = await _channel.invokeMapMethod('wxScanCode');
    return result;
  }

  ///
  /// 关闭扫码
  ///
  static Future<void> get wxStopCodeScanner async {
    await _channel.invokeMethod('wxStopCodeScanner');
  }

  ///
  /// 释放刷脸支付资源
  ///
  static Future<String> get releaseWxPayFace async {
    final String msg = await _channel.invokeMethod('releaseWxPayFace');
    return msg;
  }

  ///
  /// 测试返回结果
  ///
  static Future<WechatFacePayment> get testWxPayFace async {
    final Map<String, dynamic> map =
        await _channel.invokeMapMethod<String, dynamic>('testFacePay');
    return WechatFacePayment(resultCode: map['result_code']);
  }

  /// 开启 loading
  static Future<void> showPayLoadingDialog() async {
    await _channel.invokeMethod('showPayLoading');
  }

  /// 关闭 loading
  static Future<void> hidePayLoadingDialog() async {
    await _channel.invokeMethod('hidePayLoading');
  }
}
