package com.inanhu.zhigua.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.inanhu.zhigua.base.GlobalValue;
import com.inanhu.zhigua.util.LogUtil;
import com.inanhu.zhigua.util.ToastUtil;
import com.inanhu.zhigua.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;

/**
 * 蓝牙连接界面
 * <p/>
 * Created by iNanHu on 2016/7/17.
 */
public class BtConfigActivity extends BaseActivity implements BGAOnRVItemClickListener {

    public static final String EXTRA_BLUETOOTH_DEVICE_ADDRESS = "Bluetooth Device Adrress";
    public static final String EXTRA_BLUETOOTH_DEVICE_NAME = "Bluetooth Device Name";

    BluetoothAdapter btAdapter = null;
    private String mNameFilter = Constant.DEFAULT_NAME_FILTER;
    private DeviceInfoAdapter mAdapter;
    private RecyclerView recyclerView;
    private List<BluetoothDevice> deviceInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btconfig);

		// 设定默认返回值为取消
		setResult(Activity.RESULT_CANCELED);
        initView();

        RegisterReceiver();
        startScan();
    }

    private void initView() {
        showTopBarBack(true);
        setTopBarTitle("设备连接");

        recyclerView = (RecyclerView) findViewById(R.id.list_bluetooth);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DeviceInfoAdapter(recyclerView);
        mAdapter.setOnRVItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    private void startScan() {
        btAdapter = (BluetoothAdapter) GlobalValue.getInstance().getGlobal(Constant.Key.BTADAPTER);
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
        LogUtil.e(TAG, "==onDestroy===");
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
        Intent intent = new Intent();
        intent.putExtra(EXTRA_BLUETOOTH_DEVICE_NAME, mAdapter.getItem(position).getName());
        intent.putExtra(EXTRA_BLUETOOTH_DEVICE_ADDRESS, mAdapter.getItem(position).getAddress());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
