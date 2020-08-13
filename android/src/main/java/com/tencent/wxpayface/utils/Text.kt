package com.tencent.wxpayface.utils

import android.os.Handler
import io.flutter.plugin.common.MethodChannel

class Text {
    fun text(result: MethodChannel.Result) {
        //Result result  flutter的result
        Thread(Runnable { Handler().post { result.success("") } }).start()
    }
}