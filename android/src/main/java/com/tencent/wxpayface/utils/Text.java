package com.tencent.wxpayface.utils;

import io.flutter.plugin.common.MethodChannel.Result;

import android.os.Handler;
import android.os.Looper;

public class Text {
    private Handler uiThreadHandler = new Handler(Looper.getMainLooper());
    public void text(final Result result) {
        //Result result  flutter的result
        new Thread(new Runnable() {
            public void run() {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        result.success("");
                    }
                });
            }
        }).start();
//        uiThreadHandler.post(() -> )
    }
}
