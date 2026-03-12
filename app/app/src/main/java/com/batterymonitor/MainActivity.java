package com.batterymonitor;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LineChart chart;
    private TextView statusText;
    private List<Entry> entries = new ArrayList<>();
    private long startTime;
    private BluetoothHeadset headsetProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chart = findViewById(R.id.chart);
        statusText = findViewById(R.id.statusText);
        startTime = System.currentTimeMillis();

        setupChart();
        checkPermissions();
        setupBluetooth();
    }

    private void setupChart() {
        chart.getDescription().setText("蓝牙耳机电量监控");
        chart.getXAxis().setGranularity(1f);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisLeft().setAxisMaximum(100f);
        chart.getAxisRight().setEnabled(false);
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }
    }

    private void setupBluetooth() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            statusText.setText("设备不支持蓝牙");
            return;
        }

        adapter.getProfileProxy(this, new BluetoothProfile.ServiceListener() {
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                headsetProxy = (BluetoothHeadset) proxy;
                statusText.setText("等待蓝牙耳机连接...");
            }
            public void onServiceDisconnected(int profile) {
                headsetProxy = null;
            }
        }, BluetoothProfile.HEADSET);

        IntentFilter filter = new IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction("android.bluetooth.device.action.BATTERY_LEVEL_CHANGED");
        registerReceiver(batteryReceiver, filter);
    }

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
                if (state == BluetoothProfile.STATE_CONNECTED) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    statusText.setText("已连接: " + device.getName());
                }
            } else if ("android.bluetooth.device.action.BATTERY_LEVEL_CHANGED".equals(action)) {
                int batteryLevel = intent.getIntExtra("android.bluetooth.device.extra.BATTERY_LEVEL", -1);
                if (batteryLevel >= 0) {
                    updateChart(batteryLevel);
                }
            }
        }
    };

    private void updateChart(int battery) {
        float minutes = (System.currentTimeMillis() - startTime) / 60000f;
        entries.add(new Entry(minutes, battery));

        LineDataSet dataSet = new LineDataSet(entries, "电量 (%)");
        dataSet.setColor(0xFF2196F3);
        dataSet.setValueTextSize(10f);

        chart.setData(new LineData(dataSet));
        chart.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
        if (headsetProxy != null) {
            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(BluetoothProfile.HEADSET, headsetProxy);
        }
    }
}
