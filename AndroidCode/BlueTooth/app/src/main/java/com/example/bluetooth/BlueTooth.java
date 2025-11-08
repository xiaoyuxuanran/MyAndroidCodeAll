package com.example.bluetooth;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;
import static androidx.core.content.ContextCompat.registerReceiver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Set;

@SuppressLint("MissingPermission")
public class BlueTooth {

    private final BluetoothAdapter myBlueAdapter;
    private final Context context;
    private final AppCompatActivity activity;
    private final ActivityResultLauncher<Intent> enableBluetoothLauncher;
    private BroadcastReceiver receiver;




    public BlueTooth(Context context, AppCompatActivity activity, ActivityResultLauncher<Intent> enableBluetoothLauncher) {
        this.context = context;
        this.activity = activity;
        this.enableBluetoothLauncher = enableBluetoothLauncher;


        BluetoothManager bluetoothmanager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        if (Build.VERSION.SDK_INT >= 31) {
            myBlueAdapter = bluetoothmanager.getAdapter();
        } else {
            myBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        if (myBlueAdapter == null) {
            showToast("Not Supported");
            activity.finish();
        }
    }


    public void initializeBluetooth() {
        if (RequestPermission()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBluetoothLauncher.launch(enableBtIntent);
        }

//        if (myBlueAdapter != null && !myBlueAdapter.isEnabled()) {
//            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
//                // 权限未被授予，请求权限
//                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 100);
//                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                enableBluetoothLauncher.launch(enableBtIntent);
//                return;
//            }
//        } else {
//            showToast("BlueTooth was Enabled!!!");
//        }
    }


    public boolean isEnable() {
        return myBlueAdapter.isEnabled();
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public boolean hasPermission (String permission) {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean hasPermissions (String[] permissions) {
        for (String permission : permissions) {
            if (!hasPermission(permission))
                return false;
        }
        return  true;
    }

    public void RequestPremissions(String[] permissions, int code) {
        if (!hasPermissions(permissions)) {
            if (Build.VERSION.SDK_INT >= 31) {
                ActivityCompat.requestPermissions(activity, permissions, code);
            }
        }
    }

    public boolean RequestPermission(){
        //compileSdkVersion项目中编译SDK版本大于30申请以下权限可使用
        //Manifest.permission.BLUETOOTH_SCAN、Manifest.permission.BLUETOOTH_ADVERTISE、Manifest.permission.BLUETOOTH_CONNECT
        //若小于30可以直接使用权限对应的字符串
        if (Build.VERSION.SDK_INT>30){
            if (ContextCompat.checkSelfPermission(context,
                    "android.permission.BLUETOOTH_SCAN")
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(context,
                    "android.permission.BLUETOOTH_ADVERTISE")
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(context,
                    "android.permission.BLUETOOTH_CONNECT")
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity,new String[]{
                        "android.permission.BLUETOOTH_SCAN",
                        "android.permission.BLUETOOTH_ADVERTISE",
                        "android.permission.BLUETOOTH_CONNECT"}, 0);
                return false;
            }
        }
        return true;
    }


}
