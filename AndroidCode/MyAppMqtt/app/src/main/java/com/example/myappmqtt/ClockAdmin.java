package com.example.myappmqtt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Handler;

public class ClockAdmin extends AppCompatActivity implements View.OnClickListener{
    Button btu_delete1;
    Button btu_delete2;
    Button btu_delete3;
    TextView text_time1;
    TextView text_time2;
    TextView text_time3;
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
        setContentView(R.layout.activity_clock_admin);
        handler.post(dataUpdateRunnable);
        btu_delete1 = findViewById(R.id.btu_delete1);
        btu_delete2 = findViewById(R.id.btu_delete2);
        btu_delete3 = findViewById(R.id.btu_delete3);
        text_time1 = findViewById(R.id.text_time1);
        text_time2 = findViewById(R.id.text_time2);
        text_time3 = findViewById(R.id.text_time3);
        btu_delete1.setOnClickListener(this);
        btu_delete2.setOnClickListener(this);
        btu_delete3.setOnClickListener(this);

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
            text_time1.setText(data.n1.substring(0, 2)+":"+data.n1.substring(2, 4));
            text_time2.setText(data.n2.substring(0, 2)+":"+data.n2.substring(2, 4));
            text_time3.setText(data.n3.substring(0, 2)+":"+data.n3.substring(2, 4));
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
    public void onClick(View view) {
        if (view.getId() == btu_delete1.getId()){
            send_Cmd("J\n");
        } else if (view.getId() == btu_delete2.getId()) {
            send_Cmd("K\n");
        } else if (view.getId() == btu_delete3.getId()) {
            send_Cmd("L\n");
        }
    }
}