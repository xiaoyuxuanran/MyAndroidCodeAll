package com.example.myapplicationbt.BTthread;

import static com.example.myapplicationbt.BluetoothActivity.MY_UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;

public class ConnectThread extends Thread{

    BluetoothDevice bluetoothDevice=null;
    public static BluetoothSocket bluetoothSocket=null;
    public static int ble_station_flag=0;

    public ConnectThread(BluetoothDevice bluetoothDevice){
        this.bluetoothDevice=bluetoothDevice;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        super.run();
        try {
            bluetoothSocket=this.bluetoothDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            ble_station_flag=1;
        } catch (IOException e) {
            try {
                bluetoothSocket.close();
                ble_station_flag=0;
            } catch (IOException ex) {}
        }
    }
    public void cancel(){
        if (bluetoothSocket!=null){
            try {
                bluetoothSocket.close();
                ble_station_flag=0;
            } catch (IOException e) {}
            bluetoothSocket=null;
        }
    }









}
