package com.example.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class DiscoverActivity extends AppCompatActivity {

    Button DisCoverBtn, ConnectBtn;
    ImageButton Home;
    private BlueTooth blueToothHelper;
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private final ArrayList<String> divicelist = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private List<BluetoothDevice> devices = new ArrayList<>();

    private RecyclerView pairedView;
    private final ArrayList<String> pairedlist = new ArrayList<>();
    private ArrayAdapter<String> pairedadapter;
    BluetoothSocket HC06;

    private final ActivityResultLauncher<Intent> enableBluetoothLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            Toast.makeText(this, "藍牙已開啟", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "藍牙開啟失敗", Toast.LENGTH_SHORT).show();
                        }
                    });

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    if(!devices.contains(device)){
                        devices.add(device);
                    }
                    String deviceName = device.getName();
                    String deviceAddress = device.getAddress();
                    String deviceInfo = (deviceName != null ? deviceName : "Unknown Device") + "\n" + deviceAddress;

                    if (!divicelist.contains(deviceInfo)) {
                        divicelist.add(deviceInfo);
                        adapter.notifyDataSetChanged();  // 更新ListView
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_discover);

        blueToothHelper = new BlueTooth(this, DiscoverActivity.this, enableBluetoothLauncher);

        listView = findViewById(R.id.devices);
        adapter = new ArrayAdapter<String>(this, R.layout.listitem, R.id.itemText, divicelist);
        listView.setAdapter(adapter);


        DisCoverBtn = findViewById(R.id.Discover);
        DisCoverBtn.setOnClickListener(this::StartDiscover);


        Home = findViewById(R.id.Home);
        Home.setOnClickListener(this::ToMainActivity);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Bluetooth", "Clicked position: " + position + ", devices size: " + devices.size());

                if (position >= 0 && position < devices.size()) {
                    BluetoothDevice selectedDevice = devices.get(position);
                    HC06 = connectToDevice(selectedDevice);
                } else {
                    Log.e("Bluetooth", "Position out of bounds: " + position);
                }
            }
        });



    }

    //Bluetooth HC-06
    private BluetoothSocket connectToDevice(BluetoothDevice device) {
        BluetoothSocket socket = null;
        UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //蓝牙串口服务 UUID
        try {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
            Toast.makeText(this, "Connected to " + device.getName(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent("com.example.CONNECT_SUCCESS");
            intent.putExtra("SOCKET_EXTRA", new BluetoothSocketWrapper(socket));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            finish(); // 关闭当前活动
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show();
            finish();
//            try {
//                socket.close();
//            } catch (IOException closeException) {
//                closeException.printStackTrace();
//            }
        }
        return socket;
    }

    private void ToMainActivity(View view) {
        Intent intent = new Intent(DiscoverActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void StartDiscover(View v)
    {
        if(Build.VERSION.SDK_INT >= 31){
            String[] permissions = {
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };

            blueToothHelper.RequestPremissions(permissions, 100);
        }

        searchBluetoothDevices();
    }

    private void searchBluetoothDevices() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter;
        if (Build.VERSION.SDK_INT >= 31) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        } else {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Not able to use bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }

        //check enabled bluetooth
        if (!bluetoothAdapter.isEnabled()) {
            blueToothHelper.initializeBluetooth();
        }

        devices.clear();
        divicelist.clear();
        adapter.notifyDataSetChanged();

        //register filter
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        //enable bluetooth discover
        if(bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();

        listView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                searchBluetoothDevices();
            } else {
                Toast.makeText(this, "权限被拒绝，无法进行蓝牙设备搜索", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播接收器
        unregisterReceiver(receiver);
    }
}
