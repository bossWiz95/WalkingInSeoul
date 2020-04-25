package com.example.walkinginseoul;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ScreenLoadingBar {

        private static final String TAG = "ScreenLoadingBar";

        private LinearLayout mLoadingScreen;
        private ProgressBar mLoadingScreenBar;
        private Handler mHandler;
        private TextView mTxtInfo;

        private void initLoadingBar(View screen, View bar, View msg){

            mLoadingScreen = (LinearLayout)screen;
            mLoadingScreenBar = (ProgressBar)bar;
            mTxtInfo = (TextView)msg;

            if(mLoadingScreen != null) {
                //Loading 화면에서 터치가 되지 않도록 막는다.
                //RelativeLayout에서는 view가 겹쳐져 있더라도 상위 view의 터치가 자동으로 하위 view로 전달된다.
                mLoadingScreen.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
            }
        }

        public ScreenLoadingBar(Handler handler, View screen, View bar) {
            mHandler = handler;
            initLoadingBar(screen, bar, null);
        }

        public ScreenLoadingBar(Handler handler, View screen, View bar, View msg) {
            mHandler = handler;
            initLoadingBar(screen, bar, msg);
        }

        //region Fullscreen loading 관련

        /**
         * Loading 화면을 위한 count를 사용한다.
         * show() 함수 호출 시 하나씩 증가하며, hide() 시에 하나씩 감소한다.
         * Count가 0이 되었을 때에만 화면에서 loading bar를 사라지게 한다.
         */
        private int mShowCount = 0;
        private String mShowMsg = null;

        private Runnable mDelayedShowLoading = null;

        public void show(long delayed) {

            if(mLoadingScreen == null) return;

            mShowCount++;

            Log.e(TAG, "LoadingShow: " + mShowCount);

            if (mShowCount > 1) {
                //이미 로딩이 보여지고 있음
                return;
            }

            //Delay 시간이 있으면 runnable을 생성하고 delay를 준다.
            if (delayed > 0) {
                mDelayedShowLoading = new Runnable() {
                    @Override
                    public void run() {
                        mLoadingScreen.setBackgroundColor(0xdd000000);
                        mLoadingScreenBar.setVisibility(View.VISIBLE);

                        if(mTxtInfo != null){
                            mTxtInfo.setVisibility(View.VISIBLE);
                        }
                        mLoadingScreen.setVisibility(View.VISIBLE);
                    }
                };

                mHandler.postDelayed(mDelayedShowLoading, delayed);

                //터치를 막기위해 투명 상태로 띄운다.
                mLoadingScreen.setBackgroundColor(0x00000000);
                mLoadingScreenBar.setVisibility(View.INVISIBLE);

                if(mTxtInfo != null) mTxtInfo.setVisibility(View.INVISIBLE);

                mLoadingScreen.setVisibility(View.VISIBLE);

            } else {
                mLoadingScreen.setBackgroundColor(0xdd000000);
                mLoadingScreenBar.setVisibility(View.VISIBLE);

                if(mTxtInfo != null) mTxtInfo.setVisibility(View.VISIBLE);

                mLoadingScreen.setVisibility(View.VISIBLE);
            }
            mLoadingScreen.bringToFront();
        }

        public void show(long delayed, String info) {
            if(mTxtInfo != null){
                if(info != null){
                    mTxtInfo.setText(info);
                }
                else{
                    mTxtInfo.setText("");
                }
            }
            show(delayed);
        }

        public void hide() {
            if(mLoadingScreen == null)
                return;

            mShowCount--;

            Log.e(TAG, "LoadingHide: " + mShowCount);

            if (mShowCount > 0) {
                //Count가 0이되면 사라지게 함
                return;
            }

            mShowCount = 0;

            //delay로 pending되어 있는 show runnable이 있을 경우에 해당 객체 제거
            if (mDelayedShowLoading != null) {

                mHandler.removeCallbacks(mDelayedShowLoading);

                mDelayedShowLoading = null;
            }

            mLoadingScreen.setVisibility(View.GONE);
        }

        //Show count에 관계없이 바로 사라지게 함
        public void close(){

            if(mLoadingScreen == null)
                return;

            mShowCount = 0;

            //delay로 pending되어 있는 show runnable이 있을 경우에 해당 객체 제거
            if (mDelayedShowLoading != null) {
                mHandler.removeCallbacks(mDelayedShowLoading);

                mDelayedShowLoading = null;
            }
            mLoadingScreen.setVisibility(View.GONE);
        }

        public boolean isShow() {
            if (mShowCount > 0)
                return true;

            return false;
        }

        //endregion
}
