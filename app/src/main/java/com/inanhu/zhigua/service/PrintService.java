package com.inanhu.zhigua.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.inanhu.zhigua.R;
import com.inanhu.zhigua.base.Constant;
import com.inanhu.zhigua.base.ZhiGuaApp;
import com.inanhu.zhigua.util.LogUtil;
import com.zgone.zgonemqtt.client.Mqtt2Client;
import com.zgone.zgonemqtt.client.MqttCallbackClentExtend;
import com.zgone.zgonemqtt.model.MqttMessageDto;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import printer.JQPrinter;
import printer.esc.ESC;
import printer.porting.JQEscPrinterManager;

/**
 * 接收打印消息的服务
 * <p/>
 * Created by iNanHu on 2016/7/25.
 */

public class PrintService extends Service {

    private static final String TAG = "PrintService";

    private Mqtt2Client mqtt2Client;
    private JQEscPrinterManager printer;

    // 打印消息缓存队列
    private static BlockingDeque<String> blockingDeque = new LinkedBlockingDeque<String>();
    private Thread thread;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.e(TAG, "==onCreate===");

        printer = ZhiGuaApp.getPrinter();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        print(blockingDeque.take());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.e(TAG, "==onStartCommand===");
        final String host = intent.getStringExtra(Constant.Key.HOST);
        final String serverId = intent.getStringExtra(Constant.Key.SERVERID);
        final String clientTopic = intent.getStringExtra(Constant.Key.CLIENTTOPIC);
        final String userName = intent.getStringExtra(Constant.Key.USERNAME);
        final String passWord = intent.getStringExtra(Constant.Key.PASSWORD);
        // 初始化消息客户端
        initMqtt2Client(host, Long.parseLong(serverId), clientTopic, userName, passWord);
        return super.onStartCommand(intent, flags, startId);
    }

    private void initMqtt2Client(String host, Long serverId, String clientTopic, String userName, String passWord) {
        mqtt2Client = new Mqtt2Client(host, serverId, clientTopic, userName, passWord);
        mqtt2Client.setMqttCallbackClentExtend(new MqttCallbackClentExtend() {

            @Override
            public void messageHandle(String topic, MqttMessageDto mqttMessageDto) {
                LogUtil.e(TAG, "收到打印任务：" + mqttMessageDto.getContent());
                try {
                    blockingDeque.putLast(mqttMessageDto.getContent());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void connectionLost(Throwable throwable) {
                LogUtil.e(TAG, "服务断开连接");
            }
        });
    }

    private void print(String data) {
        JSONObject printData = (JSONObject) JSONValue.parse(data);
        // 打印联数
        Long printCount = (Long) printData.get("printCount");
        String empName = (String) printData.get("empName");
        String customerName = (String) printData.get("customerName");
        // 总金额(欠款+本单金额)
        Double billAmount = (Double) printData.get("billAmount");
        // 订单总金额
        Double receivablePay = (Double) printData.get("receivablePay");
        // 优惠支付
        Double discountPay = (Double) printData.get("discountPay");
        // 实收
        Double actualPayment = (Double) printData.get("actualPayment");
        String createTime = (String) printData.get("createTime");
//                printCount = 3L;
        if (printer.isPrinterOpened() && "00".equals(printer.isPrinterOk())) { // 打印机可用
            for (int j = 0; j < printCount; j++) {
                // 标题
                printer.printText(JQPrinter.ALIGN.CENTER, ESC.FONT_HEIGHT.x64, true, ESC.TEXT_ENLARGE.HEIGHT_DOUBLE, "智瓜科技");
                ESC.LINE_POINT[] lines = new ESC.LINE_POINT[1];
                lines[0] = new ESC.LINE_POINT(0, 575);
                for (int i = 0; i < 4; i++) {
                    printer.printLine(lines);
                }
                printer.feedDots(4);

                // 摘要
                printer.printText(JQPrinter.ALIGN.LEFT, false, "客户：" + customerName + "  ");
                printer.printText("开单员：" + empName + " ");
                printer.printText("开单时间：" + createTime + " ");
                printer.feedDots(4);
                printer.printText(JQPrinter.ALIGN.LEFT, false, "总金额：" + String.format("%.1f", billAmount) + "  ");
                printer.printText("订单总金额：" + String.format("%.1f", receivablePay) + " ");
                printer.printText("优惠金额：" + String.format("%.1f", discountPay) + " ");
                printer.feedDots(4);
                for (int i = 0; i < 4; i++) {
                    printer.printLine(lines);
                }
                printer.feedDots(4);

                // 订单列表
                JSONArray goodsJson = (JSONArray) printData.get("goods");
                for (int i = 0; i < goodsJson.size(); i++) {
                    JSONObject goods = (JSONObject) goodsJson.get(i);
                    String commodityName = (String) goods.get("commodityName");
                    Double price = (Double) goods.get("price");
                    Long count = (Long) goods.get("count");
                    Double total = (Double) goods.get("total");
                    printer.printText(0, 0, commodityName);
                    printer.printText(200, 0, String.format("%.1f", price));
                    printer.printText(325, 0, String.valueOf(count));
                    printer.printText(450, 0, String.format("%.1f", total));
                    printer.feedEnter();
                }
                for (int i = 0; i < 4; i++) {
                    printer.printLine(lines);
                }
                printer.feedDots(4);
                // 实收金额
                printer.printText("实收：" + String.format("%.1f", actualPayment));
                printer.feedLines(2);

                for (int i = 0; i < 4; i++) {
                    printer.printLine(lines);
                }
                if (printCount > 1) {
                    // 延迟2秒打印
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
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
            mqtt2Client.shutdown();
        }
        if (thread != null) {
            thread.stop();
        }
    }
}
