package com.inanhu.zhigua.base;

import android.app.Application;
import android.content.Intent;

import com.inanhu.zhigua.service.PrintService;
import com.inanhu.zhigua.util.LogUtil;
import com.inanhu.zhigua.util.ToastUtil;
import com.uzmap.pkg.openapi.APICloud;
import com.uzmap.pkg.uzkit.request.APICloudHttpClient;
import com.zgone.zgonemqtt.client.Mqtt2Client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import printer.porting.JQEscPrinterManager;

/**
 * Created by iNanHu on 2016/7/16.
 */
public class ZhiGuaApp extends Application {

    private static ZhiGuaApp application;
    private static JQEscPrinterManager printer;
    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @Override
    public void onCreate() {
        super.onCreate();
        // 日志开关
        LogUtil.setPrintable(true);
        // Toast工具初始化
        ToastUtil.init(this);
        // 初始化APICloud框架
        initAPICloud();
        // Toast工具初始化
        ToastUtil.init(this);
        printer = new JQEscPrinterManager();
//        startService(new Intent(this, PrintService.class));
    }

    private void initAPICloud() {
        APICloud.initialize(this);
        APICloudHttpClient.createInstance(this);
    }

    public static ZhiGuaApp getInstance() {
        return application;
    }

    public static JQEscPrinterManager getPrinter() {
        return printer;
    }

    public static ScheduledExecutorService getScheduledExecutor() {
        return executor;
    }
}
