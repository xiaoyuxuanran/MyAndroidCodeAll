package com.example.myappmqtt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView tv_title;

    TextView tv_temp;
    TextView tv_humi;
    ImageView img_switch;
    AppCompatButton btn_connect;
    AppCompatButton btn_disconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img_switch = findViewById(R.id.img_switch);
        tv_temp = findViewById(R.id.tv_temp);
        tv_humi = findViewById(R.id.tv_humi);
        btn_connect = findViewById(R.id.btn_connect);
        btn_disconnect = findViewById(R.id.btn_disconnect);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setSelected( true);
        tv_temp.setTypeface(Typeface.createFromAsset(getAssets(),"Ni7seg.ttf"));
        tv_humi.setTypeface(Typeface.createFromAsset(getAssets(),"Ni7seg.ttf"));
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_switch.setImageResource(R.drawable.switchg);
            }
        });

        btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_switch.setImageResource(R.drawable.switchb);
            }
        });





    }
}