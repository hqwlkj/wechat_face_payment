import 'dart:async';

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
  /// 初始化 刷脸支付、人脸识别、实名认证
  ///
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
    return WechatFacePayment(
      resultCode: map['return_code'],
      returnMsg: map['return_msg'],
      nickname: map['nickname'],
      headImgurl: map['head_imgurl'],
      subAppid: map['sub_appid'],
      subOpenid: map['sub_openid'],
      faceAuthType: map['face_authtype'],
      faceCode: map['face_code'],
      faceSid: map['face_sid'],
      underageState: map['underage_state'],
      telephoneUsed: map['telephone_used'],
      openid: map['openid'],
      token: map['token'],
      unionidMsg: map['unionid_msg'],
      unionidCode: map['unionid_code'],
    );
  }

  ///
  /// 初始化 扫码支付
  ///
  static Future<WechatFacePayment> initScanCodePay(
      String appId,
      String mchId,
      String storeId,
      String telPhone,
      String openId,
      String outTradeNo,
      String totalFee,
      String faceAuthType) async {
    final Map<String, dynamic> map =
        await _channel.invokeMapMethod('initScanCodePay', {
      "appId": appId,
      "mchId": mchId,
      "storeId": storeId,
      "telPhone": telPhone,
      "openId": openId,
      "outTradeNo": outTradeNo,
      "totalFee": totalFee,
      "faceAuthType": faceAuthType,
    });
    return WechatFacePayment(
      resultCode: map['return_code'],
      returnMsg: map['return_msg'],
      nickname: map['nickname'],
      headImgurl: map['head_imgurl'],
      subAppid: map['sub_appid'],
      subOpenid: map['sub_openid'],
      faceAuthType: map['face_authtype'],
      faceCode: map['face_code'],
      faceSid: map['face_sid'],
      underageState: map['underage_state'],
      telephoneUsed: map['telephone_used'],
      openid: map['openid'],
      token: map['token'],
      unionidMsg: map['unionid_msg'],
      unionidCode: map['unionid_code'],
    );
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
}
