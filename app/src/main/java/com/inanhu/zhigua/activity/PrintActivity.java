package com.inanhu.zhigua.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
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
import com.jq.printer.JQPrinter;
import com.jq.printer.jpl.JPL;
import com.jq.printer.jpl.Text;
import com.jq.printer.porting.PrinterManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Created by iNanHu on 2016/7/17.
 */
public class PrintActivity extends BaseActivity implements View.OnClickListener {

    Button btnPrint, btnConnect, btnDisconnect;
    private BluetoothAdapter btAdapter = null;
    private PrinterManager printer = new PrinterManager();

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
        initData();
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

    private void print(JSONObject printData) {
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

        String ret = printer.isPrinterOk();
        if ("00".equals(ret)) {
            printer.pageStart();
//                            printer.printBitmap(0, 0, PrintActivity.this.getResources(), R.mipmap.logo, Image.IMAGE_ROTATE.ANGLE_0);
            // ??64号字体有问题？？？
            printer.printText(JQPrinter.ALIGN.CENTER, 8, "WALMART", 48, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);
            printer.printText(JQPrinter.ALIGN.CENTER, 76, "沃 尔 玛", 32, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);

            printer.printLine(new Point(8, 136), new Point(568, 136), 3);

            printer.printText(28, 156, "客户", 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);
            printer.printText(28, 196, customerName, 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);

            printer.printText(178, 156, "开单员", 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);
            printer.printText(178, 196, empName, 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);


            printer.printText(378, 156, "开单时间", 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);
            printer.printText(328, 196, createTime, 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);

            printer.printLine(new Point(8, 232), new Point(568, 232), 1);

            printer.printText(28, 252, "总金额：", 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);
            printer.printText(118, 252, String.format("%.1f", billAmount), 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);

            printer.printText(28, 292, "订单总金额：", 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);
            printer.printText(168, 292, String.format("%.1f", receivablePay), 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);

            printer.printText(308, 292, "优惠金额：", 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);
            printer.printText(428, 292, String.format("%.1f", discountPay), 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);

            printer.printLine(new Point(8, 332), new Point(568, 332), 3);

            printer.printText(38, 352, "商品名称", 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);
            printer.printText(248, 352, "单价", 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);
            printer.printText(360, 352, "数量", 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);
            printer.printText(460, 352, "合计", 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);

            int height = 392;
            int step = 40;
            JSONArray goodsJson = (JSONArray) printData.get("goods");
            for (int i = 0; i < goodsJson.size(); i++) {
                JSONObject goods = (JSONObject) goodsJson.get(i);
                String commodityName = (String) goods.get("commodityName");
                Double price = (Double) goods.get("price");
                Long count = (Long) goods.get("count");
                Double total = (Double) goods.get("total");
                height += step * i;
                printer.printText(38, height, commodityName, 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);
                printer.printText(248, height, String.format("%.1f", price), 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);
                printer.printText(372, height, String.valueOf(count), 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);
                printer.printText(460, height, String.format("%.1f", total), 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);
            }

            height += 40;
            printer.printLine(new Point(8, height), new Point(568, height), 3);
            height += 20;
            printer.printText(28, height, "实收：", 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);
            printer.printText(98, height, String.format("%.1f", actualPayment), 24, false, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.ROTATE_0);

            printer.pageEnd();
            new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected Boolean doInBackground(Void... params) {
                    boolean result = printer.print();
                    printer.feedMarkEnd(0);
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean success) {
                    if (success) {
                        ToastUtil.showToast("打印成功");
                    } else {
                        ToastUtil.showToast("打印失败");
                    }
                }
            }.execute();
        } else {
            ToastUtil.showToast(ErrorResult.getError(ret));
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
                String ret = printer.isPrinterOk();
                ToastUtil.showToast(ErrorResult.getError(ret));
//                print(printData);
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
        if (printer != null && printer.isPrinterOpened()) {
            if (printer.close()) {
                ToastUtil.showToast("断开打印机成功");
            }
        }
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
