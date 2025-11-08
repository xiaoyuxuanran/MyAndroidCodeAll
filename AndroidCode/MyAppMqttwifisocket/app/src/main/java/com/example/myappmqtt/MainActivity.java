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


//    private MyTcpClient myTcpClient;
    public static final String myipaddress = "192.168.4.1";
    public static final int myport = 8585;



    TextView text_ec , text_ph , text_tempofwater , text_waterhigh ,text_dry , text_text;
    Button btu_ecdown , btu_ecup ;
    Button btu_phdown , btu_phup ;
    Button btu_tempdown, btu_tempup;
    Button btu_highdown, btu_highup;
    Button btu_drydown, btu_dryup;
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

        text_ec = findViewById(R.id.text_ec);
        text_ph = findViewById(R.id.text_ph);
        text_tempofwater = findViewById(R.id.text_tempofwater);
        text_waterhigh = findViewById(R.id.text_waterhigh);
        text_dry = findViewById(R.id.text_dry);
        text_text = findViewById(R.id.text_text);
        btu_ecdown = findViewById(R.id.btu_ecdown);
        btu_ecup = findViewById(R.id.btu_ecup);
        btu_phdown = findViewById(R.id.btu_phdown);
        btu_phup = findViewById(R.id.btu_phup);
        btu_tempdown = findViewById(R.id.btu_tempdown);
        btu_tempup = findViewById(R.id.btu_tempup);
        btu_highdown = findViewById(R.id.btu_highdown);
        btu_highup = findViewById(R.id.btu_highup);
        btu_drydown = findViewById(R.id.btu_drydown);
        btu_dryup = findViewById(R.id.btu_dryup);
        Intent serviceIntent = new Intent(this, TcpService.class);
        startService(serviceIntent);
        btu_ecup.setOnClickListener(this);
        btu_ecdown.setOnClickListener(this);
        btu_phup.setOnClickListener(this);
        btu_phdown.setOnClickListener(this);
        btu_tempup.setOnClickListener(this);
        btu_tempdown.setOnClickListener(this);
        btu_highup.setOnClickListener(this);
        btu_highdown.setOnClickListener(this);
        btu_dryup.setOnClickListener(this);
        btu_drydown.setOnClickListener(this);
    }

    private void updateDataFromSharedStorage() {
        SharedData data = SharedData.getInstance();

        // 打印日志
        if (data.ec != null) {
            // 记录解析结果
            Log.d("TcpService", "=============================================");
            Log.d("TcpService", "电导率: " + data.ec);
            Log.d("TcpService", "PH: " + data.ph);
            Log.d("TcpService", "水温: " + data.temp);
            Log.d("TcpService", "水位: " + data.water);
            Log.d("TcpService", "浊度: " + data.dry);
            Log.d("TcpService", "文本: " + data.text);
            Log.d("TcpService", "=============================================");

            // 更新UI
           text_ec.setText(data.ec);
           text_ph.setText(data.ph);
           text_tempofwater.setText(data.temp);
           text_waterhigh.setText(data.water);
           text_dry.setText(data.dry);
           text_text.setText(data.text);

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
            if (v.getId() == btu_ecup.getId()){
                send_Cmd("A\n");
            } else if (v.getId()==btu_ecdown.getId()) {
                send_Cmd("B\n");
            }
            else if (v.getId()==btu_phup.getId()) {
                send_Cmd("C\n");
            }
            else if (v.getId()==btu_phdown.getId()) {
                send_Cmd("D\n");
            }
            else if (v.getId()==btu_tempup.getId()) {
                send_Cmd("E\n");
            }
            else if (v.getId()==btu_tempdown.getId()) {
                send_Cmd("F\n");
            }
            else if (v.getId()==btu_highup.getId()) {
                send_Cmd("G\n");
            }
            else if (v.getId()==btu_highdown.getId()) {
                send_Cmd("H\n");
            }
            else if (v.getId()==btu_dryup.getId()) {
                send_Cmd("I\n");
            }
            else if (v.getId()==btu_drydown.getId()) {
                send_Cmd("J\n");
            }


    }
}