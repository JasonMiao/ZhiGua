package com.inanhu.zhigua.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.inanhu.zhigua.R;
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

        printer = ZhiGuaApp.getPrinter();
        RegisterReceiver();
    }

    /**
     * 注册广播接收器
     */
    private void RegisterReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mBroadcastReceiver, filter);
    }

    // 广播接收器
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                LogUtil.e(TAG, "搜索结束");
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) { // 蓝牙连接断开，打印机下线
                if (printer != null) {
                    printer.close();
                    // 停止定时上送状态
                    ZhiGuaApp.getInstance().getScheduledExecutor().shutdown();
                    JQEscPrinterManager.printerOffline();
                }
                ToastUtil.showToast("蓝牙连接已断开");
            }
        }
    };

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
        } else if (url.startsWith(Constant.SERVER + "/zgskwechat/WechatLoginAction_exit")) {
            if (printer != null && printer.isPrinterOpened()) {
                if (printer.close()) {
                    JQEscPrinterManager.printerOffline();
                    // 停止定时上送状态
                    ZhiGuaApp.getInstance().getScheduledExecutor().shutdown();
                }
            }
            startActivity(new Intent(WebPageActivity.this, LoginActivity.class));
//            finish();
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
                if (printer != null && printer.isPrinterOpened()) {
                    if (printer.close()) {
                        JQEscPrinterManager.printerOffline();
                        // 停止定时上送状态
                        ZhiGuaApp.getInstance().getScheduledExecutor().shutdown();
                        finish();
//                        System.exit(0);
                    }
                } else {
                    finish();
//                    System.exit(0);
                }
            } else {
                ToastUtil.showToast("再按一次退出");
                firstTime = System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        // 注销广播接收器
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
