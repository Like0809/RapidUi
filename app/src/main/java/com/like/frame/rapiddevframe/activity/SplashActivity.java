package com.like.frame.rapiddevframe.activity;

import android.os.Bundle;
import android.os.Handler;

import com.like.frame.rapiddevframe.MainActivity;
import com.like.frame.rapiddevframe.R;
import com.like.rapidui.base.BaseActivity;

/**
 * Created By Like on 2018/4/18.
 */

public class SplashActivity extends BaseActivity<Object> {

    private Handler mHandler = new Handler(msg -> {
        jumpTo(MainActivity.class);
        finish();
        return false;
    });

    @Override
    public int getContentView() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler.sendEmptyMessageDelayed(0, 3000);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

}
