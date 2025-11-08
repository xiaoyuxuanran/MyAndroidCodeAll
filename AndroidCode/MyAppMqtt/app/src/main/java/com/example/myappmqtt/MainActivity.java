package com.example.myappmqtt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;


class MyTcpClient{
    private Socket client;
    private OutputStream out;
    private InputStream in;
    public int serverStatus =1; //服务端状态，服务端主动断开为-1，需要用inputStream.read()判断
    public MyTcpClient(){}

    public boolean connect(String ip, int port){
        boolean isConnect = false;
        try{
//            client = new Socket(ip, port);
            if(client==null){
                client = new Socket();
            }
            SocketAddress socketAddress = new InetSocketAddress(ip, port);
            client.connect(socketAddress, 2000);
            if(client.isConnected()){
                Log.d("tcp connect message ---->", "连接成功");
                isConnect = true;
            }
        }catch (IOException e){
            Log.d("tcp connect message ---->", "连接失败"+e.getMessage());
            isConnect = false;
            e.printStackTrace();
        }
        return isConnect;
    }
    public void sendMsg(String msg){
        try{
            if (out==null){
                out = client.getOutputStream();
            }
            out.write(msg.getBytes());
            out.flush(); // 清空输出流
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public String rcvMsg(){
        byte[] msg = new byte[128];
        int msg_len;
        String Msg=null;
        try{
            if (in==null){
                in = client.getInputStream();
            }
            msg_len = in.read(msg);
            if (msg_len==-1){
                serverStatus = msg_len;
                Log.d("tcp resive data ---->", "服务端已断开 ");
                closeAll();
                return null;
            }
            Msg = new String(msg, 0, msg_len);
        }catch (IOException e){
            Log.d("tcp connect message ---->", "接收失败"+e.getMessage());
            e.printStackTrace();
        }
        return Msg;

    }
    public void closeAll(){
        try{
            if (out!=null){
                out.close();
                out = null;
            }
            if (in!=null){
                in.close();
                in = null;
            }
            if (client!=null){
                client.close();
                client = null;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void disconnect(){
        try{
            if (out!=null){
                out.close();
                out = null;
            }
            if (in!=null){
                in.close();
                in = null;
            }
            if (client!=null){
                client.close();
                client = null;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }


}

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    Button setclockbtu;
    Button managementclockbtu;
//    private MyTcpClient myTcpClient;
    public static final String myipaddress = "192.168.4.1";
    public static final int myport = 8585;



    TextView nowtimetext;
    TextView nowdatatext;
    TextView weathertext;
    TextView temptext;
    TextView humitext;
    private Handler handler = new Handler();
    private Runnable dataUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateDataFromSharedStorage();
            handler.postDelayed(this, 1000); // 每秒检查一次
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        MyTcpClient_init();
        handler.post(dataUpdateRunnable);

        nowtimetext = findViewById(R.id.nowtimetext);
        nowdatatext = findViewById(R.id.nowdatatext);
        weathertext = findViewById(R.id.weathertext);
        temptext    = findViewById(R.id.temptext);
        humitext    = findViewById(R.id.humitext);
        Intent serviceIntent = new Intent(this, TcpService.class);
        startService(serviceIntent);
        setclockbtu = findViewById(R.id.setclockbtu);
        managementclockbtu = findViewById(R.id.managementclockbtu);
        setclockbtu.setOnClickListener(this);
        managementclockbtu.setOnClickListener(this);
    }

    private void updateDataFromSharedStorage() {
        SharedData data = SharedData.getInstance();

        // 打印日志
        if (data.year != null) {
            Log.d("定时获取数据:", "年: " + data.year);
            Log.d("定时获取数据:", "月: " + data.month);
            Log.d("定时获取数据:", "日: " + data.day);
            Log.d("定时获取数据:", "时: " + data.hour);
            Log.d("定时获取数据:", "分: " + data.minute);
            Log.d("定时获取数据:", "天气: " + data.weather);
            Log.d("定时获取数据:", "温度上限: " + data.tempUp);
            Log.d("定时获取数据:", "温度下限: " + data.tempDown);
            Log.d("定时获取数据:", "温度: " + data.temperature);
            Log.d("定时获取数据:", "湿度: " + data.humidity);
            Log.d("定时获取数据:", "Node1: " + data.n1);
            Log.d("定时获取数据:", "Node2: " + data.n2);
            Log.d("定时获取数据:", "Node3: " + data.n3);

            // 更新UI
            nowdatatext.setText(data.year + "/" + data.month + "/" + data.day);
            nowtimetext.setText(data.hour + ":" + data.minute);
            weathertext.setText("当前天气:"+data.weather+" "+data.tempUp+"\u2103"+"~"+data.tempDown+"\u2103");
            temptext.setText("温度:"+data.temperature + "\u2103");
            humitext.setText("湿度:"+data.humidity + "%");
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止定时器
        handler.removeCallbacks(dataUpdateRunnable);
    }
    public void send_Cmd(String cmd){
        Intent serviceIntent = new Intent(this, TcpService.class);
        serviceIntent.putExtra("command", cmd);
        startService(serviceIntent);
    }
    @Override
    public void onClick(View v) {
            if (v.getId() == setclockbtu.getId()){
                Intent intent = new Intent();
                intent.setClass(this, ClockSetMeau.class);
                startActivity(intent);
            } else if (v.getId()==managementclockbtu.getId()) {
                Intent intent2 = new Intent();
                intent2.setClass(this, ClockAdmin.class);
                startActivity(intent2);

            }


    }
}