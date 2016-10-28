package com.inanhu.zhigua.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.inanhu.zhigua.base.Constant;
import com.inanhu.zhigua.base.ZhiGuaApp;
import com.inanhu.zhigua.util.LogUtil;
import com.inanhu.zhigua.util.ToastUtil;
import com.inanhu.zhigua.widget.CustomProgress;
import com.uzmap.pkg.openapi.ExternalActivity;
import com.uzmap.pkg.openapi.WebViewProvider;

import printer.porting.JQEscPrinterManager;

/**
 * Created by iNanHu on 2016/7/16.
 */
public class WebPageActivity extends ExternalActivity {

    private JQEscPrinterManager printer;

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
        LogUtil.e(TAG, "url:" + url);
        if (Constant.PRINTER_LINK_URL.equals(url)) {
            startActivity(new Intent(WebPageActivity.this, BtConfigActivity.class));
            return true;
        }
        return false;
    }

    @Override
    protected void onPageStarted(WebViewProvider provider, String url, Bitmap favicon) {
//        //远程Url，加载较慢
//        if (url.startsWith("http")) {
//            showProgressDialog("");
//        }
    }

    @Override
    protected void onPageFinished(WebViewProvider provider, String url) {
//        //远程Url，加载较慢
//        if (url.startsWith("http")) {
//            closeProgressDialog();
//        }
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
                // 清除缓存
                CookieManager.getInstance().removeAllCookie();
                printer = ZhiGuaApp.getPrinter();
                if (printer != null && printer.isPrinterOpened()) {
                    if (printer.close()) {
                        JQEscPrinterManager.printerOffline();
                        finish();
                    }
                } else {
                    finish();
                }
            } else {
                ToastUtil.showToast("再按一次退出");
                firstTime = System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
