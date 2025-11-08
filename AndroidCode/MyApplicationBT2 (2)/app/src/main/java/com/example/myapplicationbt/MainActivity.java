package com.example.myapplicationbt;


import static com.example.myapplicationbt.BTthread.ConnectThread.bluetoothSocket;
import static com.example.myapplicationbt.BluetoothActivity.connectedThread;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplicationbt.BTthread.ConnectThread;
import com.example.myapplicationbt.BTthread.ConnectedThread;


public class MainActivity extends AppCompatActivity implements ConnectedThread.OnDataReceivedListener{
    public int servor_color = 1;
    public int weight_color = 1;
    public int full_color = 1;
    Button toBT = null;
    Intent intent = null;
    private static final String HEX_DATA_PER = "0103";
    private static final String HEX_DATA_SERVOR = "00";
    private static final String HEX_DATA_WEIGHT = "01";
    private static final String HEX_DATA_FULL = "02";
    private static final String HEX_DATA_RED = "03";
    private static final String HEX_DATA_TEL = "04";
    private static final String HEX_DATA_S2 = "05";
    private static final String HEX_DATA_FIN = "FF\r\n";

    public TextView weight_value = null;

    public TextView full_value = null;
    public TextView RED_1 = null;
    public TextView RED_2 = null;
    public TextView tel_num = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 初始化按钮
        Button servor_1 = findViewById(R.id.servor_1);
        Button servor_2 = findViewById(R.id.servor_2);
        Button servor_3 = findViewById(R.id.servor_3);
        Button servor_4 = findViewById(R.id.servor_4);
        Button servor_5 = findViewById(R.id.servor_5);
        Button servor_6 = findViewById(R.id.servor_6);

        Button weight_1 = findViewById(R.id.weight_1);
        Button weight_2 = findViewById(R.id.weight_2);
        Button weight_3 = findViewById(R.id.weight_3);
        Button weight_4 = findViewById(R.id.weight_4);
        Button weight_5 = findViewById(R.id.weight_5);
        Button weight_6 = findViewById(R.id.weight_6);

        Button full_1 = findViewById(R.id.full_1);
        Button full_2 = findViewById(R.id.full_2);
        Button full_3 = findViewById(R.id.full_3);
        Button full_4 = findViewById(R.id.full_4);
        Button full_5 = findViewById(R.id.full_5);
        Button full_6 = findViewById(R.id.full_6);
        Button servor_Z = findViewById(R.id.servor_Z);
        Button servor_F = findViewById(R.id.servor_F);
        Button R1_btu = findViewById(R.id.R1_btu);
        Button R2_btu = findViewById(R.id.R2_btu);
        Button tel_btu = findViewById(R.id.tel_btu);
        Button S2_btu1 = findViewById(R.id.S2_btu1);
        Button S2_btu2 = findViewById(R.id.S2_btu2);


        TextView bluetooth_name=findViewById(R.id.bluetooth_name);
        EditText S2_Data = findViewById(R.id.S2_Data);

        toBT =(Button) findViewById(R.id.toBT);
        weight_value = (TextView) findViewById(R.id.weight_value);
        full_value = (TextView) findViewById(R.id.full_value);
        RED_1 = (TextView) findViewById(R.id.RED_1);
        RED_2 = (TextView) findViewById(R.id.RED_2);
        tel_num = (TextView) findViewById(R.id.tel_num);

        R1_btu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder hexDataBuilder = new StringBuilder();
                hexDataBuilder.append(HEX_DATA_PER)
                        .append(HEX_DATA_RED)
                        .append("01")
                        .append("FF")
                        .append(HEX_DATA_FIN);
                String hexData = hexDataBuilder.toString();
                try {
                    // 调用 sendHexData 方法并捕获异常
                    connectedThread.sendHexData(hexData);
                } catch (Exception e) {
                    // 处理异常，例如记录日志或显示错误信息
                    e.printStackTrace();
                    // 可以在这里添加更多的异常处理逻辑
                }
            }
        });
        R2_btu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder hexDataBuilder = new StringBuilder();
                hexDataBuilder.append(HEX_DATA_PER)
                        .append(HEX_DATA_RED)
                        .append("02")
                        .append("FF")
                        .append(HEX_DATA_FIN);
                String hexData = hexDataBuilder.toString();
                try {
                    // 调用 sendHexData 方法并捕获异常
                    connectedThread.sendHexData(hexData);
                } catch (Exception e) {
                    // 处理异常，例如记录日志或显示错误信息
                    e.printStackTrace();
                    // 可以在这里添加更多的异常处理逻辑
                }
            }
        });
        tel_btu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder hexDataBuilder = new StringBuilder();
                hexDataBuilder.append(HEX_DATA_PER)
                        .append(HEX_DATA_TEL)
                        .append("01")
                        .append("FF")
                        .append(HEX_DATA_FIN);
                String hexData = hexDataBuilder.toString();
                try {
                    // 调用 sendHexData 方法并捕获异常
                    connectedThread.sendHexData(hexData);
                } catch (Exception e) {
                    // 处理异常，例如记录日志或显示错误信息
                    e.printStackTrace();
                    // 可以在这里添加更多的异常处理逻辑
                }
            }
        });
        servor_Z.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder hexDataBuilder = new StringBuilder();
                hexDataBuilder.append(HEX_DATA_PER)
                        .append(HEX_DATA_SERVOR)
                        .append("0")
                        .append(servor_color)
                        .append("01")
                        .append(HEX_DATA_FIN);
                String hexData = hexDataBuilder.toString();
                try {
                    // 调用 sendHexData 方法并捕获异常
                    connectedThread.sendHexData(hexData);
                } catch (Exception e) {
                    // 处理异常，例如记录日志或显示错误信息
                    e.printStackTrace();
                    // 可以在这里添加更多的异常处理逻辑
                }
            }
        });
        servor_F.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder hexDataBuilder = new StringBuilder();
                hexDataBuilder.append(HEX_DATA_PER)
                        .append(HEX_DATA_SERVOR)
                        .append("0")
                        .append(servor_color)
                        .append("00")
                        .append(HEX_DATA_FIN);
                String hexData = hexDataBuilder.toString();
                try {
                    // 调用 sendHexData 方法并捕获异常
                    connectedThread.sendHexData(hexData);
                } catch (Exception e) {
                    // 处理异常，例如记录日志或显示错误信息
                    e.printStackTrace();
                    // 可以在这里添加更多的异常处理逻辑
                }
            }
        });
        S2_btu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s2_values = String.format("%02X", Integer.parseInt(S2_Data.getText().toString()));
                StringBuilder hexDataBuilder = new StringBuilder();
                hexDataBuilder.append(HEX_DATA_PER)
                        .append(HEX_DATA_S2)
                        .append(s2_values)
                        .append("01")
                        .append(HEX_DATA_FIN);
                String hexData = hexDataBuilder.toString();
                try {
                    // 调用 sendHexData 方法并捕获异常
                    connectedThread.sendHexData(hexData);
                } catch (Exception e) {
                    // 处理异常，例如记录日志或显示错误信息
                    e.printStackTrace();
                    // 可以在这里添加更多的异常处理逻辑
                }
            }
        });
        S2_btu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s2_values = String.format("%02X", Integer.parseInt(S2_Data.getText().toString()));
                StringBuilder hexDataBuilder = new StringBuilder();
                hexDataBuilder.append(HEX_DATA_PER)
                        .append(HEX_DATA_S2)
                        .append(s2_values)
                        .append("00")
                        .append(HEX_DATA_FIN);
                String hexData = hexDataBuilder.toString();
                try {
                    // 调用 sendHexData 方法并捕获异常
                    connectedThread.sendHexData(hexData);
                } catch (Exception e) {
                    // 处理异常，例如记录日志或显示错误信息
                }
            }
        });

        setServorBackground(servor_color);
        setWeightBackground(weight_color);
        setFullBackground(full_color);
        // 设置点击监听器
        servor_1.setOnClickListener(view -> setServorBackground(1));
        servor_2.setOnClickListener(view -> setServorBackground(2));
        servor_3.setOnClickListener(view -> setServorBackground(3));
        servor_4.setOnClickListener(view -> setServorBackground(4));
        servor_5.setOnClickListener(view -> setServorBackground(5));
        servor_6.setOnClickListener(view -> setServorBackground(6));

        weight_1.setOnClickListener(view -> setWeightBackground(1));
        weight_2.setOnClickListener(view -> setWeightBackground(2));
        weight_3.setOnClickListener(view -> setWeightBackground(3));
        weight_4.setOnClickListener(view -> setWeightBackground(4));
        weight_5.setOnClickListener(view -> setWeightBackground(5));
        weight_6.setOnClickListener(view -> setWeightBackground(6));

        full_1.setOnClickListener(view -> setFullBackground(1));
        full_2.setOnClickListener(view -> setFullBackground(2));
        full_3.setOnClickListener(view -> setFullBackground(3));
        full_4.setOnClickListener(view -> setFullBackground(4));
        full_5.setOnClickListener(view -> setFullBackground(5));
        full_6.setOnClickListener(view -> setFullBackground(6));
        toBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, BluetoothActivity.class);
                startActivity(intent);
            }
        });


//        bluetooth_name.setText(BluetoothActivity.bleetoothName!=null?BluetoothActivity.bleetoothName:"未连接");
        bluetooth_name.setText((bluetoothSocket!=null&&bluetoothSocket.isConnected())?BluetoothActivity.bleetoothName:"未连接");
        if (connectedThread!=null){
            connectedThread.setOnDataReceivedListener(this);
        }

    }


    /**
     * 设置 servor 按钮的背景
     * @param color 选中的 servor 颜色
     */
    private void setServorBackground(int color) {
        servor_color = color;

        // 缓存资源
        Drawable edit3 = ContextCompat.getDrawable(this, R.drawable.edit3);
        Drawable edit3_1 = ContextCompat.getDrawable(this, R.drawable.edit3_1);

        // 使用数组存储 servor 对象
        Button[] servors = {
            findViewById(R.id.servor_1),
            findViewById(R.id.servor_2),
            findViewById(R.id.servor_3),
            findViewById(R.id.servor_4),
            findViewById(R.id.servor_5),
            findViewById(R.id.servor_6)
        };

        // 循环设置背景
        for (int i = 0; i < servors.length; i++) {
            servors[i].setBackground((servor_color == i + 1) ? edit3_1 : edit3);
        }


    }

    /**
     * 设置 Weight 按钮的背景
     * @param color 选中的 Weight 颜色
     */
    private void setWeightBackground(int color) {
        weight_color = color;

        // 缓存资源
        Drawable edit3 = ContextCompat.getDrawable(this, R.drawable.edit3);
        Drawable edit3_1 = ContextCompat.getDrawable(this, R.drawable.edit3_1);

        // 使用数组存储 servor 对象
        Button[] weights = {
            findViewById(R.id.weight_1),
            findViewById(R.id.weight_2),
            findViewById(R.id.weight_3),
            findViewById(R.id.weight_4),
            findViewById(R.id.weight_5),
            findViewById(R.id.weight_6)
        };

        // 循环设置背景
        for (int i = 0; i < weights.length; i++) {
            weights[i].setBackground((weight_color == i + 1) ? edit3_1 : edit3);
        }

        StringBuilder hexDataBuilder = new StringBuilder();
        hexDataBuilder.append(HEX_DATA_PER)
                .append(HEX_DATA_WEIGHT)
                .append("0")
                .append(weight_color)
                .append("FF")
                .append(HEX_DATA_FIN);
        String hexData = hexDataBuilder.toString();
        try {
            // 调用 sendHexData 方法并捕获异常
            connectedThread.sendHexData(hexData);
        } catch (Exception e) {
            // 处理异常，例如记录日志或显示错误信息
            e.printStackTrace();
            // 可以在这里添加更多的异常处理逻辑
        }
    }

    /**
     * 设置 Full 按钮的背景
     * @param color 选中的 Full 颜色
     */
    private void setFullBackground(int color) {
        full_color = color;

        Drawable edit3 = ContextCompat.getDrawable(this, R.drawable.edit3);
        Drawable edit3_1 = ContextCompat.getDrawable(this, R.drawable.edit3_1);

        // 使用数组存储 servor 对象
        Button[] fulls = {
            findViewById(R.id.full_1),
            findViewById(R.id.full_2),
            findViewById(R.id.full_3),
            findViewById(R.id.full_4),
            findViewById(R.id.full_5),
            findViewById(R.id.full_6)
        };

        // 循环设置背景
        for (int i = 0; i < fulls.length; i++) {
            fulls[i].setBackground((full_color == i + 1) ? edit3_1 : edit3);
        }
        StringBuilder hexDataBuilder = new StringBuilder();
        hexDataBuilder.append(HEX_DATA_PER)
                .append(HEX_DATA_FULL)
                .append("0")
                .append(full_color)
                .append("FF")
                .append(HEX_DATA_FIN);
        String hexData = hexDataBuilder.toString();
        try {
            // 调用 sendHexData 方法并捕获异常
            connectedThread.sendHexData(hexData);
        } catch (Exception e) {
            // 处理异常，例如记录日志或显示错误信息
            e.printStackTrace();
            // 可以在这里添加更多的异常处理逻辑
        }


    }

    @Override
    public void onDataReceived(String data) {
        if (data.substring(0,1).equals("W")){
            weight_color = Integer.parseInt(data.substring(2,3));
            runOnUiThread(() -> weight_value.setText(data.substring(3,10)+"kg"));
        }
        if (data.substring(0,1).equals("Y")){
            full_color = Integer.parseInt(data.substring(2,3));
            runOnUiThread(() -> full_value.setText(data.substring(3,4)));
        }
        if (data.substring(0,1).equals("R")){
            if (data.substring(2,3).equals("1")){
                runOnUiThread(() -> RED_1.setText(data.substring(3,4)));
            }
            else if (data.substring(2,3).equals("2")){
                runOnUiThread(() -> RED_2.setText(data.substring(3,4)));
            }
            }
        if (data.substring(0,1).equals("T")){
            runOnUiThread(() -> tel_num.setText(data.substring(2,data.length())));
        }
        setFullBackground(full_color);
        setWeightBackground(weight_color);
        setServorBackground(servor_color);

        }


}
