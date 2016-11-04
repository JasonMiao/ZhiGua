package com.inanhu.zhigua.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private static Mqtt2Client mqtt2Client;
    public static void shutDownMqtt2Client() {
        if (mqtt2Client != null) {
            mqtt2Client.shutdown();
        }
    }
    private JQEscPrinterManager printer;

    // 打印消息缓存队列
    private static BlockingDeque<String> blockingDeque = new LinkedBlockingDeque<String>();
    private Thread thread;
    ScheduledExecutorService executor;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.e(TAG, "==onCreate===");

        printer = ZhiGuaApp.getPrinter();


        // 打印结算单线程
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String data = blockingDeque.take();
                        LogUtil.e(TAG, "打印队列-" + data);
                        print(data);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        // 打印机状态上送
//        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor = ZhiGuaApp.getInstance().getScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                String ret = printer.isPrinterOk();
                LogUtil.e(TAG, "定时任务" + "状态：" + ret);
                switch (ret) {
                    case "05":
                        JQEscPrinterManager.printerStatus("false", "false", "true", "false", "false");
                        break;
                    case "06":
                        JQEscPrinterManager.printerStatus("false", "false", "false", "true", "false");
                        break;
                    case "07":
                        JQEscPrinterManager.printerStatus("true", "false", "false", "false", "false");
                        break;
                    case "08":
                        JQEscPrinterManager.printerStatus("false", "false", "false", "false", "true");
                        break;
                    case "09":
                        JQEscPrinterManager.printerStatus("false", "true", "false", "false", "false");
                        break;
                    default:
                        JQEscPrinterManager.printerStatus("false", "false", "false", "false", "false");
                        break;
                }
            }
        }, 0, 60 * 1000, TimeUnit.MILLISECONDS);

        // 在API11之后构建Notification的方式
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
//        Intent nfIntent = new Intent(this, MainActivity.class);
        builder/*.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))*/ // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setContentTitle("智瓜管家") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("智瓜管家服务运行中") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        Notification notification = builder.build(); // 获取构建好的Notification
//        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        // 参数一：唯一的通知标识；参数二：通知消息。
        startForeground(110, notification);// 开始前台服务
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.e(TAG, "==onStartCommand===" + intent.getStringExtra(Constant.Key.HOST));

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
//        shutDownMqtt2Client();
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

        // 商户名称
        String merchantName = (String) printData.get("merchantName");
        // 商户电话
        String shopPhone = (String) printData.get("shopPhone");
        // 商户地址
        String shopAddress = (String) printData.get("shopAddress");
        // 银行账号列表
        //bankAccount银行帐号  name账户名称  bankName银行名称
        JSONArray bankList = (JSONArray) printData.get("bankList");

        // 进货数量
        Long pgoodsCount = (Long) printData.get("pgoodsCount");
        // 退货数量
        Long rgoodsCount = (Long) printData.get("rgoodsCount");

        String empName = (String) printData.get("empName");
        String customerName = (String) printData.get("customerName");
        // 总金额(欠款+本单金额)
        Double billAmount = (Double) printData.get("billAmount");
        // 订单总金额
        Double receivablePay = (Double) printData.get("receivablePay");
        // 上欠金额
        Double historyArrears = (Double) printData.get("historyArrears");
        // 优惠支付
        Double discountPay = (Double) printData.get("discountPay");
        // 实收
        Double actualPayment = (Double) printData.get("actualPayment");
        String createTime = (String) printData.get("createTime");
//                printCount = 3L;
        if (printer.isPrinterOpened() && "00".equals(printer.isPrinterOk())) { // 打印机可用
            for (int j = 0; j < printCount; j++) {
                // 标题
                printer.printText(JQPrinter.ALIGN.CENTER, ESC.FONT_HEIGHT.x64, true, ESC.TEXT_ENLARGE.HEIGHT_DOUBLE, merchantName);
                // 实线
                ESC.LINE_POINT[] lines = new ESC.LINE_POINT[1];
                lines[0] = new ESC.LINE_POINT(0, 575);
                // 虚线
                String line = "-----------------------------------------------";
                printer.feedLines(1);
//                for (int i = 0; i < 4; i++) {
//                    printer.printLine(lines);
//                }
                printer.printText(line);
//                printer.feedDots(4);

                // 摘要
                printer.printText(JQPrinter.ALIGN.LEFT, false, "客户：" + customerName + "     ");
                printer.printText("开单员：" + empName + " ");
                printer.printText("开单时间：" + createTime + " ");
                printer.feedDots(4);
//                for (int i = 0; i < 4; i++) {
//                    printer.printLine(lines);
//                }
                printer.printText(line);
                printer.feedDots(4);

                // 订单列表
                printer.printText(0, 0, "商品名称");
                printer.printText(200, 0, "单价");
                printer.printText(325, 0, "数量");
                printer.printText(450, 0, "小计");
                printer.feedEnter();
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
//                for (int i = 0; i < 4; i++) {
//                    printer.printLine(lines);
//                }
                printer.printText(line);
                printer.feedDots(4);

                printer.printText(JQPrinter.ALIGN.LEFT, false, "进：" + String.valueOf(pgoodsCount) + "   ");
                printer.printText("退：" + String.valueOf(rgoodsCount) + "   ");
                printer.printText("本单总金额：" + String.format("%.1f", billAmount) + "  ");
                printer.printText(JQPrinter.ALIGN.LEFT, false, "上欠金额：" + String.format("%.1f", historyArrears));
                printer.feedEnter();
                printer.printText("优惠金额：" + String.format("%.1f", discountPay) + " ");
                printer.printText("应收金额：" + String.format("%.1f", receivablePay) + " ");
                printer.feedDots(6);

//                for (int i = 0; i < 4; i++) {
//                    printer.printLine(lines);
//                }
                printer.printText(line);
                printer.feedDots(4);

                // 实收金额
                printer.printText("实收：" + String.format("%.1f", actualPayment));
//                printer.feedLines(2);
                printer.feedLines(1);

                // 地址
                printer.printText(JQPrinter.ALIGN.LEFT, false, "地址：" + shopAddress);
                printer.feedEnter();
                // 联系方式
                printer.printText(JQPrinter.ALIGN.LEFT, false, "联系方式：" + shopPhone);
                printer.feedEnter();

                // 银行账号
                int bankListSize = bankList.size();
                if (bankListSize > 0) {
                    for (int k = 0; k < bankListSize; k++) {
                        JSONObject bankInfo = (JSONObject) JSONValue.parse(bankList.get(k).toString());
                        printer.printText(JQPrinter.ALIGN.LEFT, false, "户名账号" + (k + 1) + "：" + bankInfo.get("name") + "  " + bankInfo.get("bankName") + "：" + bankInfo.get("bankAccount"));
                        printer.feedEnter();
                    }
                }

                printer.feedLines(1);

                // 底部提示
                printer.printText(JQPrinter.ALIGN.LEFT, false, "提醒：钱款，货物请当面点清，离店概不负责");
                printer.feedEnter();
                printer.printText(JQPrinter.ALIGN.LEFT, false, "智瓜商客系统");
                printer.feedLines(1);

                for (int i = 0; i < 2; i++) {
                    printer.printLine(lines);
                }
                printer.feedLines(3);

//                if (printCount > 1) {
                    // 延迟2秒打印
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                }
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
        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
        LogUtil.e(TAG, "==onDestroy===");
        shutDownMqtt2Client();
        if (thread != null) {
            thread.stop();
        }
        if (executor != null) {
            executor.shutdown();
        }
    }
}
