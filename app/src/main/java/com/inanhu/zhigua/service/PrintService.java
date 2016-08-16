package com.inanhu.zhigua.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.inanhu.zhigua.activity.PrintActivity;
import com.inanhu.zhigua.util.LogUtil;
import com.inanhu.zhigua.util.ToastUtil;
import com.zgone.zgonemqtt.client.Mqtt2Client;
import com.zgone.zgonemqtt.client.MqttCallbackClentExtend;
import com.zgone.zgonemqtt.model.MqttMessageDto;

/**
 * 接收打印消息的服务
 * <p/>
 * Created by iNanHu on 2016/7/25.
 */
public class PrintService extends Service {

    private static final String TAG = "PrintService";

    private Mqtt2Client mqtt2Client;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.e(TAG, "==onCreate===");
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                initMqtt2Client();
            }
        });
    }

    private void initMqtt2Client() {
        mqtt2Client = new Mqtt2Client("tcp://139.196.154.235:61613", "99", "admin", "zgone123456");
        mqtt2Client.setMqttCallbackClentExtend(new MqttCallbackClentExtend() {

            @Override
            public void messageHandle(String topic, MqttMessageDto mqttMessageDto) {
                LogUtil.e(TAG, "收到打印任务：" + mqttMessageDto.getContent());
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(PrintService.this, PrintActivity.class);
                intent.putExtra("DATA", mqttMessageDto.getContent());
                startActivity(intent);
            }

            @Override
            public void connectionLost(Throwable throwable) {
                LogUtil.e(TAG, "服务断开连接");
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.e(TAG, "==onDestroy===");
        if (mqtt2Client != null) {
            mqtt2Client.close();
        }
    }
}
