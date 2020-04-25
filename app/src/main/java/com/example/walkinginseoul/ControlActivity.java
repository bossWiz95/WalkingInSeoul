package com.example.walkinginseoul;

import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.lang.ref.WeakReference;

public class ControlActivity {
    private static final String TAG = "ControlActivity";

    protected ScreenLoadingBar mScreenLoadingBar;

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

    protected final long DEFAULT_LOADING_TIMEOUT = 30000;

    // 3초를 준 이유는 추후에 로딩창을 겹쳐 띄울 때
    // 겹쳐질 수 있고, 3초 뒤에 실행되게끔 하기 위함.
    private Runnable mRunnableLoadingTimeout = new Runnable() {
        @Override
        public void run() {
            if(mScreenLoadingBar != null) mScreenLoadingBar.close();
        }
    };

    protected void showFullscreenLoading(long delay, String msg){
        showFullscreenLoading(delay, msg, DEFAULT_LOADING_TIMEOUT);
    }

    protected void showFullscreenLoading(long delay, String msg, long timeout){
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
