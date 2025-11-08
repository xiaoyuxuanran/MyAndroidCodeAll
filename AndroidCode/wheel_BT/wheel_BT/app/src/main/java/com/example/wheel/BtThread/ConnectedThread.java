package com.example.wheel.BtThread;

import android.bluetooth.BluetoothSocket;

import com.example.wheel.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

//连接了蓝牙设备建立通信之后的数据交互线程类
public class ConnectedThread extends Thread{

    BluetoothSocket bluetoothSocket=null;
    InputStream inputStream=null;//获取输入数据
    OutputStream outputStream=null;//获取输出数据
    int[] lastData=new int[]{0,0};
    public ConnectedThread(BluetoothSocket bluetoothSocket){
        this.bluetoothSocket=bluetoothSocket;
        //先新建暂时的Stream
        InputStream inputTemp=null;
        OutputStream outputTemp=null;
        try {
            inputTemp=this.bluetoothSocket.getInputStream();
            outputTemp=this.bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            try {
                bluetoothSocket.close();//出错就关闭线程吧
            } catch (IOException ex) {}
        }
        inputStream=inputTemp;
        outputStream=outputTemp;
    }

    @Override
    public void run() {
        super.run();
        while(true){
            //发送数据
            //btWriteString("hello I love you!\r\n");//发送一组字符串
            if(!Arrays.toString(MainActivity.wheelData).equals(Arrays.toString(lastData))){//数组是否相等的判断！！！
                btWriteInt(MainActivity.wheelData);
            }
            lastData=MainActivity.wheelData;//做完一次发送重新给lastData赋值
        }
    }

    public void btWriteInt(int[] intData){
        for(int sendInt:intData){
            try {
                outputStream.write(sendInt);
            } catch (IOException e) {}
        }
    }

    //自定义的发送字符串的函数
    public void btWriteString(String string){
        for(byte sendData:string.getBytes()){
            try {
                outputStream.write(sendData);//outputStream发送字节的函数
            } catch (IOException e) {}
        }
    }

    //自定义的关闭Socket线程的函数
    public void cancel(){
        try {
            bluetoothSocket.close();
        } catch (IOException e) {}
    }
}
