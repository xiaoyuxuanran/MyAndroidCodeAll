package com.example.myappmqtt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ClockSetMeau extends AppCompatActivity implements View.OnClickListener{
    Button btn_set_time;
    Button btn_ring;
    Button btn_light;
    Button btn_both;
    Button btn_nor;
    Button btn_yaohuang;
    Button btn_clickthree;
    Button btn_yes;
    Button btn_no;
    EditText et_hour;
    EditText et_min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_set_meau);
        btn_set_time = findViewById(R.id.btn_set_time);
        btn_ring = findViewById(R.id.btn_ring);
        btn_light = findViewById(R.id.btn_light);
        btn_both = findViewById(R.id.btn_both);
        btn_nor = findViewById(R.id.btn_nor);
        btn_yaohuang = findViewById(R.id.btn_yaohuang);
        btn_clickthree = findViewById(R.id.btn_clickthree);
        btn_yes = findViewById(R.id.btn_yes);
        btn_no = findViewById(R.id.btn_no);
        et_hour = findViewById(R.id.et_hour);
        et_min = findViewById(R.id.et_min);
        btn_set_time.setOnClickListener(this);
        btn_ring.setOnClickListener(this);
        btn_light.setOnClickListener(this);
        btn_both.setOnClickListener(this);
        btn_nor.setOnClickListener(this);
        btn_yaohuang.setOnClickListener(this);
        btn_clickthree.setOnClickListener(this);
        btn_yes.setOnClickListener(this);
        btn_no.setOnClickListener(this);

    }
    public void send_Cmd(String cmd){
        Intent serviceIntent = new Intent(this, TcpService.class);
        serviceIntent.putExtra("command", cmd);
        startService(serviceIntent);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == btn_set_time.getId()){
            String hour = et_hour.getText().toString();
            String min = et_min.getText().toString();
            Log.d("Send Time data ----->", "hour:"+hour+" min:"+min);
            send_Cmd("A"+hour+min+"\n");
        } else if (v.getId()==btn_ring.getId()) {
            send_Cmd("B\n");
        } else if (v.getId()==btn_light.getId()) {
            send_Cmd("C\n");
        } else if (v.getId()==btn_both.getId()) {
            send_Cmd("D\n");
        } else if (v.getId()==btn_nor.getId()) {
            send_Cmd("E\n");
        } else if (v.getId()==btn_yaohuang.getId()) {
            send_Cmd("F\n");
        } else if (v.getId()==btn_clickthree.getId()){
            send_Cmd("G\n");
        } else if (v.getId()==btn_yes.getId()) {
            send_Cmd("H\n");
        } else if (v.getId()==btn_no.getId()) {
            send_Cmd("I\n");
        }


    }
}