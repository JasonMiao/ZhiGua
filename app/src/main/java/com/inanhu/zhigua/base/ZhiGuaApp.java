package com.inanhu.zhigua.base;

import android.app.Application;
import android.content.Intent;

import com.inanhu.zhigua.service.PrintService;
import com.inanhu.zhigua.util.ToastUtil;
import com.uzmap.pkg.openapi.APICloud;
import com.uzmap.pkg.uzkit.request.APICloudHttpClient;

/**
 * Created by iNanHu on 2016/7/16.
 */
public class ZhiGuaApp extends Application {

    private static ZhiGuaApp application;

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化APICloud框架
        initAPICloud();
        // Toast工具初始化
        ToastUtil.init(this);
        startService(new Intent(this, PrintService.class));
    }

    private void initAPICloud() {
        APICloud.initialize(this);
        APICloudHttpClient.createInstance(this);
    }

    public static ZhiGuaApp getInstance() {
        return application;
    }
}
