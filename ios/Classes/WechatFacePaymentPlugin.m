#import "WechatFacePaymentPlugin.h"
#if __has_include(<wechat_face_payment/wechat_face_payment-Swift.h>)
#import <wechat_face_payment/wechat_face_payment-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "wechat_face_payment-Swift.h"
#endif

@implementation WechatFacePaymentPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftWechatFacePaymentPlugin registerWithRegistrar:registrar];
}
@end
