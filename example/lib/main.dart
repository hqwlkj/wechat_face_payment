import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:wechat_face_payment/wechat_face_payment.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String _initFacePayMsg = 'Unknown';
  String _releaseFacePayMsg = '';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await WechatFacePayment.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }
  ///
  /// FACEPAY 人脸凭证，常用于人脸支付
  /// FACEPAY_DELAY 延迟支付(提供商户号信息联系微信支付开通权限)
  ///
  Future<void> initFacePay() async{
    String msg = '';
    try {
      msg = await WechatFacePayment.initFacePay("wx34aa1d8ffa545b06","1506994921","123455","","","13249817234123412343","1","FACEPAY");
    } on PlatformException {
      _initFacePayMsg = 'Failed to get platform initFacePay.';
    }
    if (!mounted) return;
    setState(() {
      _initFacePayMsg = msg;
    });
  }

  ///
  /// 人脸识别模式。可选值：
  ///   FACEID-ONCE: 人脸识别(单次模式)
  ///   FACEID-LOOP: 人脸识别(循环模式)
  ///   ONCE与LOOP的区别：
  ///
  ///   ONCE只会识别一次。 即调用本接口后， 如果在指定时间内（比如５秒）没有识别出来，则会返回识别失败。
  ///   LOOP会持续识别人脸， 直到识别成功为止。（或者调用停止接口
  ///
  Future<void> startFacePay() async{
    String msg = '';
    try {
      msg = await WechatFacePayment.initFacePay("wx34aa1d8ffa545b06","1506994921","123455","","","13249817234123412343","1","FACEID-LOOP");
    } on PlatformException {
      _initFacePayMsg = 'Failed to get platform startFacePay.';
    }
    if (!mounted) return;
    setState(() {
      _initFacePayMsg = msg;
    });
  }

  ///
  ///  实名认证 FACE_AUTH
  ///
  Future<void> startFaceVerified() async{
    String msg = '';
    try {
      msg = await WechatFacePayment.initFacePay("wx34aa1d8ffa545b06","1506994921","123455","","","13249817234123412343","1","FACE_AUTH");
    } on PlatformException {
      _initFacePayMsg = 'Failed to get platform startFaceVerified.';
    }
    if (!mounted) return;
    setState(() {
      _initFacePayMsg = msg;
    });
  }

  ///
  /// 扫码支付 SCAN_CODE
  /// 该状态码为 插件自定义 WxPayFace 没有提供
  ///
  Future<void> startScanCodeToPay() async {
    String msg = '';
    try {
      msg = await WechatFacePayment.initScanCodePay("wx34aa1d8ffa545b06","1506994921","123455","","","13249817234123412343","1","SCAN_CODE");
    } on PlatformException {
      _initFacePayMsg = 'Failed to get platform startScanCodeToPay.';
    }
    if (!mounted) return;
    setState(() {
      _initFacePayMsg = msg;
    });
  }

  Future<void> releaseWxpayface()async{
    String msg = await WechatFacePayment.releaseWxPayFace;
    if (!mounted) return;
    setState(() {
      _releaseFacePayMsg = msg;
    });
  }
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Wechat face payment example app'),
        ),
        body: Center(
          child: Column(
            children: [
              SizedBox(height: 30),
              Text('Running on: $_platformVersion\n'),
              SizedBox(height: 30),
              Text('Running on: $_initFacePayMsg\n'),
              FlatButton(
                onPressed: () {
                  initFacePay();
                },
                child: Text('刷脸支付'),
              ),
              SizedBox(height: 20),
              FlatButton(
                onPressed: () {
                  startFacePay();
                },
                child: Text('人脸识别'),
              ), SizedBox(height: 20),
              FlatButton(
                onPressed: () {
                  startFaceVerified();
                },
                child: Text('实名认证'),
              ), SizedBox(height: 20),
              FlatButton(
                onPressed: () {
                  startScanCodeToPay();
                },
                child: Text('扫码支付'),
              ),
              SizedBox(height: 20),
              Text('releaseWxpayface: $_releaseFacePayMsg\n', style: TextStyle(color: Colors.redAccent)),
              FlatButton(
                onPressed: () {
                  releaseWxpayface();
                },
                child: Text('释放资源，这步需要在组件销毁的时候调用'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
