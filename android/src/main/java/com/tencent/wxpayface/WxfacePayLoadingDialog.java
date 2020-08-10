package com.tencent.wxpayface;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.parsec.wechat_face_payment.R;


/**
 * Created by admin on 2018/5/15.
 */

public class WxfacePayLoadingDialog extends Dialog{

    private static final int MSG_UPDATE_LOADING_DOT = 1;

    private ImageView mLoadingDot1;
    private ImageView mLoadingDot2;
    private ImageView mLoadingDot3;

    private Handler mHandler;
    private int mDotSeq;

    public WxfacePayLoadingDialog(Context context){
        super(context, R.style.Loading);
        // 加载布局
        setContentView(R.layout.vs_main_loading);
        // 设置Dialog参数
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);

        mLoadingDot1= (ImageView)findViewById(R.id.loading_dot1);
        mLoadingDot2= (ImageView)findViewById(R.id.loading_dot2);
        mLoadingDot3= (ImageView)findViewById(R.id.loading_dot3);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case MSG_UPDATE_LOADING_DOT: {
                        if(mDotSeq == 0){
                            mLoadingDot1.setAlpha(0.6f);
                            mLoadingDot2.setAlpha(1f);
                            mLoadingDot3.setAlpha(0.6f);
                            mDotSeq++;
                        }else if(mDotSeq == 1){
                            mLoadingDot1.setAlpha(0.3f);
                            mLoadingDot2.setAlpha(0.6f);
                            mLoadingDot3.setAlpha(1f);
                            mDotSeq++;
                        }else if(mDotSeq == 2){
                            mLoadingDot1.setAlpha(1f);
                            mLoadingDot2.setAlpha(0.6f);
                            mLoadingDot3.setAlpha(0.3f);
                            mDotSeq = 0;
                        }

                        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_LOADING_DOT,800);
                    }
                }

            }
        };

    }

    public void show(){
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_LOADING_DOT,800);
        mDotSeq = 0;
        super.show();
    }

    public void hide(){
        mHandler.removeMessages(MSG_UPDATE_LOADING_DOT);
        mDotSeq = 0;
        mLoadingDot1.setAlpha(1f);
        mLoadingDot2.setAlpha(0.6f);
        mLoadingDot3.setAlpha(0.3f);
        super.dismiss();
    }

}
