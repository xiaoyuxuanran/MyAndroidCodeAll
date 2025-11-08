package com.example.studyandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
//    TextView tvhello;
    TextView tv_buttonclick;
    private Button btn_click2;
    Integer count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        tvhello = findViewById(R.id.tv_hello);
//        tvhello.setSelected( true);
//        tvhello.requestFocus();
        tv_buttonclick = findViewById(R.id.tv_buttonclick);
        btn_click2 = findViewById(R.id.btn_click2);

        btn_click2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                tv_buttonclick.setText("Button2 clicked " + count.toString());
            }
        });
    }

    public void onClickTest(View view) {
        count++;
        tv_buttonclick.setText("Button clicked " + count.toString());

    }


}