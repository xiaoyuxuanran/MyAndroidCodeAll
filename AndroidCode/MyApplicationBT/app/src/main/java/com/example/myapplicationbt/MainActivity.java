package com.example.myapplicationbt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

public class MainActivity extends AppCompatActivity {
    int servor_color = 1;
    int weight_color = 1;
    int full_color = 1;
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;
    private EditText dataDisplay;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // Standard SerialPortService ID
    private ActivityResultLauncher<Intent> enableBtLauncher;
    private ActivityResultLauncher<Intent> selectDeviceLauncher;


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

        Button choose_bt1 = findViewById(R.id.choose_bt1);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            finish();
        }
//        choose_bt1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!bluetoothAdapter.isEnabled()) {
//                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//                } else {
//                    selectBluetoothDevice();
//                }
//            }
//        });
        // 初始化 ActivityResultLauncher
        enableBtLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        selectBluetoothDevice();
                    } else {
                        Toast.makeText(this, "未启动蓝牙", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        selectDeviceLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        String address = result.getData().getStringExtra("address");
                        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                            try {
                                BluetoothSocket mmSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                                mmSocket.connect();
                                startListening(mmSocket);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
                        }
                    }
                }
        );

        choose_bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    enableBtLauncher.launch(enableBtIntent);
                } else {
                    selectBluetoothDevice();
                }
            }
        });

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
    }

    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_ENABLE_BT) {
//            if (resultCode == RESULT_OK) {
//                selectBluetoothDevice();
//            } else {
//                Toast.makeText(this, "为启动蓝牙", Toast.LENGTH_SHORT).show();
//            }
//        } else if (requestCode == 2 && resultCode == RESULT_OK) {
//            String address = data.getStringExtra("address");
//            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
//            try {
//                BluetoothSocket mmSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
//                mmSocket.connect();
//                startListening(mmSocket);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                selectBluetoothDevice();
            } else {
                Toast.makeText(this, "未启动蓝牙", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            String address = data.getStringExtra("address");
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                try {
                    BluetoothSocket mmSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                    mmSocket.connect();
                    startListening(mmSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限已授予，重新尝试连接
                String address = getIntent().getStringExtra("address");
                if (address != null) {
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                    try {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        BluetoothSocket mmSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                       mmSocket.connect();
                       startListening(mmSocket);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
           } else {
               // 权限被拒绝，提示用户
               Toast.makeText(this, "权限被拒绝，无法连接蓝牙设备", Toast.LENGTH_SHORT).show();
           }
       }
   }

    private void startListening(BluetoothSocket socket) {
        try {

            InputStream mmInputStream = socket.getInputStream();
            BufferedReader mmBufferedReader = new BufferedReader(new InputStreamReader(mmInputStream));
            Thread listenThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            String receivedData = mmBufferedReader.readLine();
                            if (receivedData != null) {
                                int receivedValue = Integer.parseInt(receivedData);
                                if (receivedValue > 500) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 如果接收到的值大于300，执行相应的操作
                                            // 例如，显示一个提示消息或者触发某个事件
                                            dataDisplay.append("Received value is greater than 300: " + receivedData + "\n");
                                            // 这里可以添加其他操作
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 如果接收到的值不大于300，将其显示在界面上
                                            dataDisplay.append(receivedData + "\n");
                                        }
                                    });
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });
            listenThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     private void selectBluetoothDevice() {
       Intent intent = new Intent(this, DeviceListActivity.class);
       selectDeviceLauncher.launch(intent);
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
    }

}
