package com.tencent.wxpayface;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.parsec.wechat_face_payment.R;


/**
 * Created by admin on 2018/5/14.
 */

public class WxfacePayLoading extends RelativeLayout {

    public WxfacePayLoading(Context context){
        super(context);

    }

    public WxfacePayLoading(Context context, AttributeSet attrs) {
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.vs_main_loading, this);
    }


}
