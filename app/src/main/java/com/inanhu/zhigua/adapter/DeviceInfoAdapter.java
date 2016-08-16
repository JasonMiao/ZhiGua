package com.inanhu.zhigua.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;

import com.inanhu.zhigua.R;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;

/**
 * Created by Jason on 2016/7/18.
 */
public class DeviceInfoAdapter extends BGARecyclerViewAdapter<BluetoothDevice> {

    public DeviceInfoAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_bt);
    }

    @Override
    protected void fillData(BGAViewHolderHelper bgaViewHolderHelper, int i, BluetoothDevice device) {
        bgaViewHolderHelper.setText(R.id.tv_device_name, device.getName());
    }

}
