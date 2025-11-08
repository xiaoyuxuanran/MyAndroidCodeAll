package com.example.myapplicationbt.BTthread;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread{

    BluetoothSocket bluetoothSocket=null;
    InputStream inputStream=null;
    OutputStream outputStream=null;
    public static String bt_read_string_of_value=null;
    private OnDataReceivedListener onDataReceivedListener;
    public interface OnDataReceivedListener {
        void onDataReceived(String data);
    }

    private byte[] mmBuffer;

    public ConnectedThread(BluetoothSocket bluetoothSocket){
        this.bluetoothSocket=bluetoothSocket;
        InputStream inputTemp=null;
        OutputStream outputTemp=null;

        try {
            inputTemp=this.bluetoothSocket.getInputStream();
            outputTemp=this.bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            try {
                bluetoothSocket.close();
            } catch (IOException ex) {
            }
        }

        inputStream=inputTemp;
        outputStream=outputTemp;

    }

    @Override
    public void run() {
        super.run();
//        btWriteSring("bluetoothIsConnect\r\n");
//        sendHexData("0103000401FF\r\n");
        mmBuffer = new byte[1024];
        int numBytes;
        while (true) {
            try {
                numBytes = inputStream.read(mmBuffer);
                String readMessage = new String(mmBuffer, 0, numBytes);
                if (onDataReceivedListener != null) {
                    onDataReceivedListener.onDataReceived(readMessage);
                }

            } catch (IOException e) {
                break;
            }
        }
    }

    public void sendHexData(String hexString) {
        byte[] bytes = hexStringToBytes(hexString);
        for (byte sendData : bytes) {
            try {
                outputStream.write(sendData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] hexStringToBytes(String hexString) {
        int len = hexString.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                                 + Character.digit(hexString.charAt(i + 1), 16));
        }
        return bytes;
    }


    public void btWriteSring(String string){
        for(byte sendData:string.getBytes()){
            try {
                outputStream.write(sendData);
            } catch (IOException e) {}

        }
    }
    public String btRead(){
        byte[] buffer=new byte[1024];
        int bytes;
        StringBuilder sb=new StringBuilder();
        try {
            bytes=inputStream.read(buffer);
            sb.append(new String(buffer,0,bytes));

        } catch (IOException e) {
        }
        return sb.toString();
    }
    public void cancel(){
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
        }


    }
    public void setOnDataReceivedListener(OnDataReceivedListener listener) {
        this.onDataReceivedListener = listener;
    }
}
