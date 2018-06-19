package com.miaxis.btdemo;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_PERMISSION_LOCATION = 2009;
    @BindView(R.id.rv_bt_device)
    RecyclerView rvBtDevice;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> deviceList;
    private BTDeviceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceList = new ArrayList<>();
        adapter = new BTDeviceAdapter(deviceList, this);
        rvBtDevice.setLayoutManager(new LinearLayoutManager(this));
        rvBtDevice.setAdapter(adapter);
        adapter.setListener(new BTDeviceAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, int position) {
                connect(deviceList.get(position));
            }
        });
    }

    @OnClick(R.id.tv_search)
    void scanDevice() {
        mayRequestLocation();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (!deviceList.contains(device)) {
                    deviceList.add(device);
                }
            }
            adapter.notifyDataSetChanged();
        }

        if (bluetoothAdapter.isDiscovering()) {//正在查找
            Log.e(TAG, "onClick: 正在查找设备");
        } else {
            deviceList.clear();
            adapter.notifyDataSetChanged();
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            registerReceiver(receiver, filter);
            filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(receiver, filter);
            filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver, filter);
            bluetoothAdapter.startDiscovery();
        }
    }

    private void mayRequestLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            //6.0以上设备
            int checkCallPhonePermission = checkSelfPermission(Manifest.permission.
                    ACCESS_COARSE_LOCATION);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            }
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (Objects.requireNonNull(action)) {
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED: {
                    Log.e(TAG, "onReceive: 结束查找设备");
                    unregisterReceiver(this);
                    break;
                }
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED: {
                    Log.e(TAG, "onReceive: 开始查找设备");
                    break;
                }
                case BluetoothDevice.ACTION_FOUND: {
                    /* 从intent中取得搜索结果数据 */
                    Log.e(TAG, "onReceive: 查找到设备");
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.e(TAG, "设备：" + device.getName() + " address: " + device.getAddress());
                    deviceList.add(device);
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    };

    private void connect(BluetoothDevice device) {
        
    }
}
