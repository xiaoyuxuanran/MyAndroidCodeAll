package com.example.myapplicationbt;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.utils.widget.ImageFilterButton;

import java.util.Set;

public class DeviceListActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        ArrayAdapter<String> deviceArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        ListView deviceListView = findViewById(R.id.deviceListView);
        deviceListView.setAdapter(deviceArrayAdapter);

        for (BluetoothDevice device : pairedDevices) {
            deviceArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        }

        deviceListView.setOnItemClickListener((parent, view, position, id) -> {
            String deviceInfo = (String) parent.getItemAtPosition(position);
            String address = deviceInfo.substring(deviceInfo.length() - 17);
            returnSelectedDevice(address);
        });
    }

    // 返回所选蓝牙设备的地址
    private void returnSelectedDevice(String address) {
        if (address != null) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("address", address);
            setResult(RESULT_OK, resultIntent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }
}
