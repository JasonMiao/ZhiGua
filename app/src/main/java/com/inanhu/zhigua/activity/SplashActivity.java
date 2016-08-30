package com.inanhu.zhigua.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.inanhu.zhigua.R;
import com.inanhu.zhigua.base.BaseActivity;
import com.inanhu.zhigua.base.Constant;

/**
 * Created by iNanHu on 2016/7/16.
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // ？可以把登录的html放在本地，这样跳转到WebPageActivity界面就比较顺畅
//                startActivity(new Intent(SplashActivity.this, WebPageActivity.class).putExtra(Constant.Key.START_URL, Constant.START_URL));
                startActivity(new Intent(SplashActivity.this, PrintActivity.class));
                finish();
            }
        }, 3000);
    }
}
