# wechat_face_payment

[![pub package](https://img.shields.io/pub/v/wechat_face_payment.svg)](https://pub.dartlang.org/packages/wechat_face_payment)
[![GitHub Stars](https://img.shields.io/github/stars/hqwlkj/wechat_face_payment.svg?logo=github)](https://github.com/hqwlkj/wechat_face_payment)
[![Platform](https://img.shields.io/badge/platform-android-green.svg)](https://img.shields.io/badge/platform-Android-green.svg)


微信刷脸支付、刷脸认证、扫码支持等 Flutter 插件。

## 说明
该插件目前官方只提供了 Android SDK 和 Windows SDK  的文档，为提供IOS SDK 的文档，所以 [wechat_face_payment](https://github.com/hqwlkj/wechat_face_payment.git)  插件目前仅支持 Android 设备使用

## 功能
[刷脸支付](https://pay.weixin.qq.com/wiki/doc/wxfacepay/develop/android/facepay.html)
    <ul>
       <li>该流程通过识别用户人脸、手机号，获取人脸凭证(face_code)，该人脸凭证具有较高的安全等级，可用于支付。</li>
    </ul>
    
[人脸识别](https://pay.weixin.qq.com/wiki/doc/wxfacepay/develop/android/faceuser.html) 
    <ul>
       <li>人脸识别通过识别用户人脸，获取用户信息（openid）。</li>
       <li>此功能常用于商户会员、商品推荐等场景， 此流程无法用于支付。</li>
       <li>FACEID-ONCE为直接启动人脸识别流程。</li>
    </ul>

[实名认证](https://pay.weixin.qq.com/wiki/doc/wxfacepay/develop/android/sid.html)
    <ul>
       <li>该流程首先通过识别用户人脸、手机号，向商户返回face_sid ， 商户根据 face_sid 进行用户是否已认证的判断；对于未认证的用户，可根据 face_sid 向微信后台请求进行实名认证。对于已认证的用户，可直接调用接口关闭实名认证。</li>
       <li>该能力目前仅针对医院行业开放申请。</li>
    </ul>
    
[扫码功能](https://pay.weixin.qq.com/wiki/doc/wxfacepay/develop/android/scancode.html)
    <ul>
       <li>对于使用微信人脸sdk的支付设备，我们提供对外接口，可以在启动摄像头进行扫码，当微信人脸sdk扫码成功后，会通知扫码结果给商户APP，扫码成功间隔3000ms。</li>
    </ul>
    
    
[更多相关信息请查看微信刷脸支付官网](https://pay.weixin.qq.com/wiki/doc/wxfacepay/)


## 安装
1、添加依赖

将此添加到项目的 pubspec.yaml 文件中：
```
 dependencies:
   wechat_face_payment: 
      git:
        url:git://github.com/hqwlkj/wechat_face_payment.git
```

2、安装

您可以从命令行安装软件包：

使用Flutter：

```
 $ flutter pub get

```

或者，您的编辑器可能支持flutter pub get。 查看您的编辑器文档以了解更多信息。


3、使用

在Dart代码中，您可以使用：

```
import 'package:wechat_face_payment/wechat_face_payment.dart';
```
