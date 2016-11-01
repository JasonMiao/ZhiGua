package com.inanhu.zhigua.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.inanhu.zhigua.R;
import com.inanhu.zhigua.adapter.DeviceInfoAdapter;
import com.inanhu.zhigua.base.BaseActivity;
import com.inanhu.zhigua.base.Constant;
import com.inanhu.zhigua.base.ErrorResult;
import com.inanhu.zhigua.base.GlobalValue;
import com.inanhu.zhigua.base.ZhiGuaApp;
import com.inanhu.zhigua.service.PrintService;
import com.inanhu.zhigua.util.LogUtil;
import com.inanhu.zhigua.util.ToastUtil;
import com.inanhu.zhigua.widget.DividerItemDecoration;
import com.uzmap.pkg.uzkit.request.APICloudHttpClient;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;
import printer.porting.JQEscPrinterManager;

/**
 * 蓝牙连接界面
 * <p/>
 * Created by iNanHu on 2016/7/17.
 */
public class BtConfigActivity extends BaseActivity implements BGAOnRVItemClickListener {

    public static final String BLUETOOTH_DEVICE_ADDRESS = "Bluetooth Device Adrress";
    public static final String BLUETOOTH_DEVICE_NAME = "Bluetooth Device Name";

//    // 济强打印机对象
//    private JQEscPrinterManager printer = new JQEscPrinterManager();

    BluetoothAdapter btAdapter = null;
    // 蓝牙是否可用
    private boolean isBtOk = true;
    private String btName, btAddress;
    // 是否静默打开蓝牙
    private boolean mBtOpenSilent = true;
    private String mNameFilter = Constant.DEFAULT_NAME_FILTER;
    private DeviceInfoAdapter mAdapter;
    private RecyclerView recyclerView;
    private List<BluetoothDevice> deviceInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btconfig);

		// 设定默认返回值为取消
//		setResult(Activity.RESULT_CANCELED);
        initView();
        initBluetooth();

        RegisterReceiver();
        startScan();
    }

    private void initView() {
        showTopBarBack(true);
        setTopBarTitle("设备连接");
        setTopBarRight("断开");
        findViewById(R.id.id_topbar_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (printer != null && printer.isPrinterOpened()) {
                    if (printer.close()) {
                        // 停止定时上送状态
                        ZhiGuaApp.getInstance().getScheduledExecutor().shutdown();
                        ToastUtil.showToast("断开打印机成功");
                        // 打印机离线
                        JQEscPrinterManager.printerOffline();
                        finish();
                    }
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.list_bluetooth);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DeviceInfoAdapter(recyclerView);
        mAdapter.setOnRVItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    private void initBluetooth() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            ToastUtil.showToast("本机没有找到蓝牙硬件或驱动！");
            // TODO 处理设备不带蓝牙的情况
            isBtOk = false;
            return;
        } else {
//            // 保存全局变量方便其他地方调用
//            GlobalValue.getInstance().saveGlobal(Constant.Key.BTADAPTER, btAdapter);
            // 如果本地蓝牙没有开启，则开启
            if (!btAdapter.isEnabled()) {
                ToastUtil.showToast("正在开启蓝牙");
                if (!mBtOpenSilent) {
//                    // 我们通过startActivityForResult()方法发起的Intent将会在onActivityResult()回调方法中获取用户的选择，比如用户单击了Yes开启，
//                    // 那么将会收到RESULT_OK的结果，
//                    // 如果RESULT_CANCELED则代表用户不愿意开启蓝牙
//                    Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(mIntent, REQUEST_BT_ENABLE);
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

    private void startScan() {
//        btAdapter = (BluetoothAdapter) GlobalValue.getInstance().getGlobal(Constant.Key.BTADAPTER);
        if (btAdapter == null) {
            ToastUtil.showToast("蓝牙模块不可用");
            finish();
        }
        if (!btAdapter.isEnabled()) {
            btAdapter.enable();
            while (btAdapter.getState() != BluetoothAdapter.STATE_ON) {
            }
        }
        //获取蓝牙设备，并开始搜索设备
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }
        btAdapter.startDiscovery();
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
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                LogUtil.e(TAG, device.getName());
                String deviceName = device.getName();
                if (TextUtils.isEmpty(deviceName)) {
                    deviceName = getString(R.string.unknown_device);
                }
                //过滤蓝牙设备.有名字，且不包含特定字符的设备，过滤掉
                if (!deviceName.contains(mNameFilter)) {
                    return;
                }
                discoverOneDevice(device);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                LogUtil.e(TAG, "搜索结束");
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) { // 蓝牙连接断开
                if (printer != null) {
                    printer.close();
                }
                ToastUtil.showToast("蓝牙连接已断开");
            }
        }
    };

    public void discoverOneDevice(BluetoothDevice device) {
        if (device != null && device.getName() != null) {
            boolean noMatch = false;
            for (BluetoothDevice bd : deviceInfos) {
                if (bd.getName().equals(device.getName()) && bd.getAddress().equals(device.getAddress())) {
                    noMatch = true;
                    break;
                }
            }
            //去重
            if (!noMatch) {
                deviceInfos.add(device);
                int index = deviceInfos.size() - 1;
                mAdapter.addItem(index, deviceInfos.get(index));
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }
        deviceInfos.clear();
        // 注销广播接收器
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onRVItemClick(ViewGroup viewGroup, View view, int position) {
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }
        btName = mAdapter.getItem(position).getName();
        btAddress = mAdapter.getItem(position).getAddress();
//        Intent intent = new Intent();
//        intent.putExtra(EXTRA_BLUETOOTH_DEVICE_NAME, mAdapter.getItem(position).getName());
//        intent.putExtra(EXTRA_BLUETOOTH_DEVICE_ADDRESS, mAdapter.getItem(position).getAddress());
//        setResult(Activity.RESULT_OK, intent);
//        finish();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String ret = printer.init(btAdapter, btAddress);
                if ("00".equals(ret)) {
//                    ToastUtil.showToast("打印机初始化成功");
                    //TODO 打印机上线
                    GlobalValue.getInstance().saveGlobal(BLUETOOTH_DEVICE_NAME, btName);
                    GlobalValue.getInstance().saveGlobal(BLUETOOTH_DEVICE_ADDRESS, btAddress);
//                    printerOnline(btName, btAddress);
                    JQEscPrinterManager.printerOnline(BtConfigActivity.this, btName, btAddress);
//                    startService(new Intent(BtConfigActivity.this, PrintService.class));
                    finish();
                } else {
                    ToastUtil.showToast(ErrorResult.getError(ret));
                }
                // 保存打印机对象到全局变量
                GlobalValue.getInstance().saveGlobal(Constant.Key.PRINTER, printer);
                // 注册蓝牙断开广播
                IntentFilter filter = new IntentFilter();
                filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);//蓝牙断开
                registerReceiver(mBroadcastReceiver, filter);
                return null;
            }
        }.execute();
    }
}
