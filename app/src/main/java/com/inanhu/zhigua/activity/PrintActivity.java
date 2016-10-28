/*
package com.inanhu.zhigua.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.inanhu.zhigua.R;
import com.inanhu.zhigua.base.BaseActivity;
import com.inanhu.zhigua.base.Constant;
import com.inanhu.zhigua.base.ErrorResult;
import com.inanhu.zhigua.base.GlobalValue;
import com.inanhu.zhigua.util.LogUtil;
import com.inanhu.zhigua.util.ToastUtil;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

*
 * Created by iNanHu on 2016/7/17.


public class PrintActivity extends BaseActivity implements View.OnClickListener {

    Button btnPrint, btnConnect, btnDisconnect;
    private BluetoothAdapter btAdapter = null;
    //    private PrinterManager printer = new PrinterManager();
    private JQEscPrinterManager printer = new JQEscPrinterManager();

    private final static int REQUEST_BT_ENABLE = 0;
    private final static int REQUEST_BT_ADDR = 1;
    // 是否静默打开蓝牙
    private boolean mBtOpenSilent = true;
    // 蓝牙是否可用
    private boolean isBtOk = true;

    private JSONObject printData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        initView();
//        initData();
        initBluetooth();
    }

    private void initData() {
        Intent intent = getIntent();
        String data = intent.getStringExtra("DATA");
        if (!TextUtils.isEmpty(data)) {
            LogUtil.e(TAG, "去打印啦");
            printData = (JSONObject) JSONValue.parse(data);
//            print(printData);
        } else {
            ToastUtil.showToast("打印数据为空");
        }
    }

    private void print(
JSONObject printData
) {
//        String empName = (String) printData.get("empName");
//        String customerName = (String) printData.get("customerName");
//        // 总金额(欠款+本单金额)
//        Double billAmount = (Double) printData.get("billAmount");
//        // 订单总金额
//        Double receivablePay = (Double) printData.get("receivablePay");
//        // 优惠支付
//        Double discountPay = (Double) printData.get("discountPay");
//        // 实收
//        Double actualPayment = (Double) printData.get("actualPayment");
//        String createTime = (String) printData.get("createTime");

//        String ret = printer.isPrinterOk();
//        if ("00".equals(ret)) {

        if (printer.wakeUp()) {
            // 标题
            printer.printText(JQPrinter.ALIGN.CENTER, ESC.FONT_HEIGHT.x64, true, ESC.TEXT_ENLARGE.HEIGHT_DOUBLE, "智瓜科技");
            ESC.LINE_POINT[] lines = new ESC.LINE_POINT[1];
            lines[0] = new ESC.LINE_POINT(0, 575);
            for (int i = 0; i < 4; i++) {
                printer.printLine(lines);
            }
            printer.feedDots(4);

            // 摘要
//            printer.printText(JQPrinter.ALIGN.LEFT, false, "客户：" + customerName + "  ");
//            printer.printText("开单员：" + empName + " ");
//            printer.printText("开单时间：" + createTime + " ");
//            printer.feedDots(4);
//            printer.printText(JQPrinter.ALIGN.LEFT, false, "总金额：" + String.format("%.1f", billAmount) + "  ");
//            printer.printText("订单总金额：" + String.format("%.1f", receivablePay) + " ");
//            printer.printText("优惠金额：" + String.format("%.1f", discountPay) + " ");
//            printer.feedDots(4);
//            for (int i = 0; i < 4; i++) {
//                printer.printLine(lines);
//            }
//            printer.feedDots(4);

            printer.printText(JQPrinter.ALIGN.LEFT, false, "客户：" + "李白" + "  ");
            printer.printText("开单员：" + "冯智华" + " ");
            printer.printText("开单时间：" + "2016-09-01 11:06:03" + " ");
            printer.feedDots(4);
            printer.printText(JQPrinter.ALIGN.LEFT, false, "总金额：" + "470.00" + "  ");
            printer.printText("订单总金额：" + "350.00" + " ");
            printer.printText("优惠金额：" + "0.00" + " ");
            printer.feedDots(4);
            for (int i = 0; i < 4; i++) {
                printer.printLine(lines);
            }
            printer.feedDots(4);

            // 订单列表
//            JSONArray goodsJson = (JSONArray) printData.get("goods");
//            for (int i = 0; i < goodsJson.size(); i++) {
//                JSONObject goods = (JSONObject) goodsJson.get(i);
//                String commodityName = (String) goods.get("commodityName");
//                Double price = (Double) goods.get("price");
//                Long count = (Long) goods.get("count");
//                Double total = (Double) goods.get("total");
//                printer.printText(0, 0, commodityName);
//                printer.printText(200, 0, String.format("%.1f", price));
//                printer.printText(325, 0, String.valueOf(count));
//                printer.printText(450, 0, String.format("%.1f", total));
//                printer.feedEnter();
//            }
//            for (int i = 0; i < 4; i++) {
//                printer.printLine(lines);
//            }
//            printer.feedDots(4);
//            // 实收金额
//            printer.printText("实收：" + String.format("%.1f", actualPayment));
//            printer.feedLines(2);

            printer.printText(0, 0, "商品名称");
            printer.printText(200, 0, "单价");
            printer.printText(325, 0, "数量");
            printer.printText(450, 0, "合计");
            printer.feedEnter();


            printer.printText(0, 0, "金仕达");
            printer.printText(200, 0, "20.0");
            printer.printText(325, 0, "2");
            printer.printText(450, 0, "40.0");
            printer.feedEnter();
            printer.printText(0, 0, "金仕达");
            printer.printText(200, 0, "20.0");
            printer.printText(325, 0, "2");
            printer.printText(450, 0, "40.0");
            printer.feedEnter();
            printer.printText(0, 0, "金仕达");
            printer.printText(200, 0, "20.0");
            printer.printText(325, 0, "2");
            printer.printText(450, 0, "40.0");
            printer.feedEnter();
            for (int i = 0; i < 4; i++) {
                printer.printLine(lines);
            }
            printer.feedDots(4);
            // 实收金额
            printer.printText("实收：" + "350.00");
            printer.feedLines(2);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.qr_);
            printer.printImage(0, bitmap, 0, 50);
            printer.feedLines(2);
        } else {
            ToastUtil.showToast("设备未唤醒");
        }
    }

    private void initBluetooth() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            ToastUtil.showToast("本机没有找到蓝牙硬件或驱动！");
            // TODO 处理设备不带蓝牙的情况
            isBtOk = false;
            return;
        } else {
            // 保存全局变量方便其他地方调用
            GlobalValue.getInstance().saveGlobal(Constant.Key.BTADAPTER, btAdapter);
            // 如果本地蓝牙没有开启，则开启
            if (!btAdapter.isEnabled()) {
                ToastUtil.showToast("正在开启蓝牙");
                if (!mBtOpenSilent) {
                    // 我们通过startActivityForResult()方法发起的Intent将会在onActivityResult()回调方法中获取用户的选择，比如用户单击了Yes开启，
                    // 那么将会收到RESULT_OK的结果，
                    // 如果RESULT_CANCELED则代表用户不愿意开启蓝牙
                    Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(mIntent, REQUEST_BT_ENABLE);
                } else { // 用enable()方法来开启，无需询问用户(无声息的开启蓝牙设备),这时就需要用到android.permission.BLUETOOTH_ADMIN权限。
                    btAdapter.enable();
                    isBtOk = true;
                    ToastUtil.showToast("本地蓝牙已打开");
                }
            } else {
                ToastUtil.showToast("本地蓝牙已打开");
                isBtOk = true;
            }
        }
    }

    private void initView() {
        btnPrint = (Button) findViewById(R.id.btn_print);
        btnPrint.setOnClickListener(this);
        btnConnect = (Button) findViewById(R.id.btn_connect);
        btnConnect.setOnClickListener(this);
        btnDisconnect = (Button) findViewById(R.id.btn_disconnect);
        btnDisconnect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_disconnect:
                if (printer != null && printer.isPrinterOpened()) {
                    if (printer.close()) {
                        ToastUtil.showToast("断开打印机成功");
                    }
                }
                break;
            case R.id.btn_connect:
                if (btAdapter == null) {
                    return;
                }
                startActivityForResult(new Intent(PrintActivity.this, BtConfigActivity.class), REQUEST_BT_ADDR);
                break;
            case R.id.btn_print:
//                String ret = printer.isPrinterOk();
//                ToastUtil.showToast(ErrorResult.getError(ret));
//                print(printData);
                print();
                break;
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(android.content.Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) { // 蓝牙连接断开
                if (printer != null) {
                    printer.close();
                }
                ToastUtil.showToast("蓝牙连接已断开");
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BT_ENABLE) {
            if (resultCode == RESULT_OK) {
                isBtOk = true;
                ToastUtil.showToast("蓝牙已打开");
            } else if (resultCode == RESULT_CANCELED) {
                isBtOk = false;
                ToastUtil.showToast("不允许蓝牙开启");
                return;
            }
        } else if (requestCode == REQUEST_BT_ADDR) {
            if (resultCode == Activity.RESULT_OK) {
                final String btDeviceString = data.getStringExtra(BtConfigActivity.EXTRA_BLUETOOTH_DEVICE_ADDRESS);
                if (btDeviceString != null) {
                    if (btAdapter.isDiscovering())
                        btAdapter.cancelDiscovery();
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            String ret = printer.init(btAdapter, btDeviceString);
                            if ("00".equals(ret)) {
                                ToastUtil.showToast("打印机初始化成功");
                            } else {
                                ToastUtil.showToast(ErrorResult.getError(ret));
                            }
                            // 保存打印机对象到全局变量
                            GlobalValue.getInstance().saveGlobal(Constant.Key.PRINTER, printer);
                            // 注册蓝牙断开广播
                            IntentFilter filter = new IntentFilter();
                            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);//蓝牙断开
                            registerReceiver(mReceiver, filter);
                            return null;
                        }
                    }.execute();
                }
            } else {
                ToastUtil.showToast("选择打印机");
            }
        }
    }

    @Override
    protected void onDestroy() {
//        if (printer != null && printer.isPrinterOpened()) {
//            if (printer.close()) {
//                ToastUtil.showToast("断开打印机成功");
//            }
//        }
//        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
*/
