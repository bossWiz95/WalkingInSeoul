package com.example.walkinginseoul;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ResourceBundle;

public class ControlActivity {
    protected  ScreenLoadingBar mScreenLoadingBar;

    protected static final int MSG_SHOW_FULLSCREEN_LOAD = 100;
    protected static final int MSG_HIDE_FULLSCREEN_LOAD = 101;


    protected void initCommonControls(View[] loadingBar){

        if(loadingBar != null ){
            mScreenLoadingBar = new ScreenLoadingBar(mHandler, loadingBar[0], loadingBar[1], loadingBar[2]);
        }

    }


    protected static class MainHandler extends Handler {

        private final WeakReference<ControlActivity> mMainAcitivity;

        public MainHandler(ControlActivity activity) {
            mMainAcitivity = new WeakReference<ControlActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);

            ControlActivity activity = mMainAcitivity.get();

            if (activity == null)
                return;

            activity.processMessage(msg);
        }
    }

    protected final MainHandler mHandler = new MainHandler(this);
    protected MainHandler getHandler(){return mHandler;}

    protected void processMessage(Message msg){

        if(msg.what == MSG_SHOW_FULLSCREEN_LOAD){

            if(mScreenLoadingBar != null) mScreenLoadingBar.show(msg.arg1, (String)msg.obj);
        }
        else if(msg.what == MSG_HIDE_FULLSCREEN_LOAD){

            if(mScreenLoadingBar != null) mScreenLoadingBar.hide();
        }
    }

    //region Screen loading 제어 관련

    protected final long DEFAULT_LOADING_TIMEOUT = 30000;

    private Runnable mRunnableLoadingTimeout = new Runnable() {
        @Override
        public void run() {

            if(mScreenLoadingBar != null) mScreenLoadingBar.close();
        }
    };

    protected void showFullscreenLoading(long delay, String msg){

        showFullscreenLoading(delay, msg, DEFAULT_LOADING_TIMEOUT);
    }

    //timeout 값이 0이면 timeout을 진행하지 않는다.
    protected void showFullscreenLoading(long delay, String msg, long timeout){

        //show는 중복으로 호출될 수 있음
        //show가 마지막으로 호출되었을 기준으로 timeout이 설정되도록 한다.
        mHandler.removeCallbacks(mRunnableLoadingTimeout);

        if(timeout > 0){

            mHandler.postDelayed(mRunnableLoadingTimeout, timeout);
        }

        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_FULLSCREEN_LOAD, (int)delay, 0, msg));
    }

    protected void hideFullscreenLoading(){
        mHandler.sendEmptyMessage(MSG_HIDE_FULLSCREEN_LOAD);
    }
}
