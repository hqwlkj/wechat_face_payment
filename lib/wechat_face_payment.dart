
import 'dart:async';

import 'package:flutter/services.dart';

class WechatFacePayment {
  static const MethodChannel _channel =
      const MethodChannel('wechat_face_payment');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  ///
  /// 初始化 刷脸支付、人脸识别、实名认证
  ///
  static Future<String> initFacePay(
      String appId,
      String mchId,
      String storeId,
      String telPhone,
      String openId,
      String outTradeNo,
      String totalFee,
      String faceAuthType
      ) async{
    final String msg = await _channel.invokeMethod('initFacePay',{
      "appId": appId,
      "mchId": mchId,
      "storeId": storeId,
      "telPhone": telPhone,
      "openId": openId,
      "outTradeNo": outTradeNo,
      "totalFee": totalFee,
      "faceAuthType": faceAuthType,
    });
    return msg;
  }


  ///
  /// 初始化 扫码支付
  ///
  static Future<String> initScanCodePay(
      String appId,
      String mchId,
      String storeId,
      String telPhone,
      String openId,
      String outTradeNo,
      String totalFee,
      String faceAuthType
      ) async{
    final String msg = await _channel.invokeMethod('initScanCodePay',{
      "appId": appId,
      "mchId": mchId,
      "storeId": storeId,
      "telPhone": telPhone,
      "openId": openId,
      "outTradeNo": outTradeNo,
      "totalFee": totalFee,
      "faceAuthType": faceAuthType,
    });
    return msg;
  }

  ///
  /// 释放刷脸支付资源
  ///
  static Future<String> get releaseWxPayFace async{
    final String msg = await _channel.invokeMethod('releaseWxPayFace');
    return msg;
  }
}
