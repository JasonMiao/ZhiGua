package com.inanhu.zhigua.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;

import com.inanhu.zhigua.util.LogUtil;
import com.inanhu.zhigua.util.ToastUtil;
import com.inanhu.zhigua.widget.CustomProgress;
import com.uzmap.pkg.openapi.ExternalActivity;
import com.uzmap.pkg.openapi.WebViewProvider;

/**
 * Created by iNanHu on 2016/7/16.
 */
public class WebPageActivity extends ExternalActivity {

    private static final String TAG = WebPageActivity.class.getSimpleName();

    protected CustomProgress dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 重写该函数，可实现处理拦截某Html5页面是否允许访问某API，如模块的API，APICloud引擎的API
     */
    @Override
    protected boolean shouldForbiddenAccess(String host, String module, String api) {
        return true;
    }

    @Override
    protected boolean shouldOverrideUrlLoading(WebViewProvider provider, String url) {
//        if(url.contains("taobao")){
//            return true;
//        }
        ToastUtil.showToast(url);
        return false;
    }

    @Override
    protected void onPageStarted(WebViewProvider provider, String url, Bitmap favicon) {
        //远程Url，加载较慢
        if (url.startsWith("http")) {
            showProgressDialog("");
        }
    }

    @Override
    protected void onPageFinished(WebViewProvider provider, String url) {
        //远程Url，加载较慢
        if (url.startsWith("http")) {
            closeProgressDialog();
        }
    }

    /**
     * 显示进度条
     *
     * @param message
     */
    protected void showProgressDialog(String message) {
        dialog = CustomProgress.show(this, message, true, null);
    }

    /**
     * 关闭进度条
     */
    protected void closeProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long secondTime = System.currentTimeMillis();
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (secondTime - firstTime < 2000) {
                finish();
            } else {
                ToastUtil.showToast("再按一次退出");
                firstTime = System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
