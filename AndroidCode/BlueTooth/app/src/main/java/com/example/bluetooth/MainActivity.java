package com.example.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


@SuppressLint("MissingPermission")
public class MainActivity extends AppCompatActivity {

    private static final int CREATE_FILE_REQUEST_CODE = 1;
    private ActivityResultLauncher<Intent> createFileLauncher;

    Button Record, Clear, Stop, SaveCSV;
    private static final  String CONNECT_SUCCESS_ACTION = "com.example.CONNECT_SUCCESS";
    private BluetoothSocket connectedSocket;
    private volatile boolean isRecording = false;
    private final ArrayList<String> Datas = new ArrayList<>();
    private ScrollView scrollView;
    private TextView DataMessage;

    private static final String TAG = "BluetoothReceiverThread";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(MainActivity.this);
        setContentView(R.layout.activity_main);

        // Initialize the launcher for file creation
        createFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            SaveCSVToUri(uri);
                        }
                    }
                }
        );

        scrollView = findViewById(R.id.DataScrollView);
        DataMessage = findViewById(R.id.DataText);

        Record = findViewById(R.id.Record);
        Clear = findViewById(R.id.Clear);
        Stop = findViewById(R.id.Stop);
        SaveCSV = findViewById(R.id.Save);

        Record.setOnClickListener(this::StartRecordData);
        Stop.setOnClickListener(this::StopRecordData);
        Clear.setOnClickListener(this::ClearData);
        SaveCSV.setOnClickListener(this::Save);

        //
        Spinner spinner = findViewById(R.id.SpinnerSelecter);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.SpinnerSelecter, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();

                if ("SearchDevices".equals(selected)) {
                    // 跳转到指定页面
                    Intent intent = new Intent(MainActivity.this, DiscoverActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // registerReceiver BlueSocket Connect HC06 is Ok?
        LocalBroadcastManager.getInstance(this).registerReceiver(connectReceiver,
                new IntentFilter(CONNECT_SUCCESS_ACTION));

    }


    private void Save(View view) {
        if (Datas.isEmpty()) {
            Toast.makeText(this, "Not Have Data!!!", Toast.LENGTH_SHORT).show();
        } else {
            //TODO Save CSV Send Message Let stm32 not Send data to android
            Toast.makeText(this, "Start Save File!!! Stop Record Data and DisConnect BlueTooth", Toast.LENGTH_SHORT).show();
            stopReceivingMessages();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String currentDataAndTime = sdf.format(new Date());

            //TODO : filename need to change for now time
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_TITLE, currentDataAndTime + "_record.csv");
            createFileLauncher.launch(intent);
        }
    }

    private void SaveCSVToUri(Uri uri) {
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            if (outputStream != null) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                for (String line : Datas) {
                    writer.write(line);
                    writer.newLine();
                }
                writer.flush();
                Toast.makeText(this, "Data saved to CSV file.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show();
        }
    }

    private void ClearData(View view) {
        Datas.clear();
        DataMessage.setText("");

        ScrollView scrollView = findViewById(R.id.DataScrollView);
        scrollView.post(() -> scrollView.scrollTo(0, 0));
        Toast.makeText(this, "Messages cleared", Toast.LENGTH_SHORT).show();

        //TODO: Send Message To Reset Stm32 COUNTER
        sendMessage("RESETCOUNT");
    }

    private void StopRecordData(View view) {
        if (isRecording) {
            //TODO: Send Message To Reset Stm32 ALL State
            sendMessage("STOP");
            stopReceivingMessages();
            Toast.makeText(this,"Recording stopped", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No recording in progress", Toast.LENGTH_SHORT).show();
        }
    }

    private void StartRecordData(View view) {
        if (connectedSocket != null && connectedSocket.isConnected()) {
            sendMessage("START");
            isRecording = true;
            startReceivingMessages();
            Toast.makeText(this, "Recording started...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No Bluetooth connection established", Toast.LENGTH_SHORT).show();
        }
    }

    private final BroadcastReceiver connectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("SOCKET_EXTRA")) {
                BluetoothSocketWrapper wrapper = (BluetoothSocketWrapper) intent.getSerializableExtra("SOCKET_EXTRA");
                connectedSocket = wrapper.getSocket();
                // 处理连接成功后的操作
                Toast.makeText(MainActivity.this, "Successfully connected!!! Turn Back To Main Page", Toast.LENGTH_SHORT).show();

                //TODO: Send Message To Stm32 To Send The Frist Data
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                    for (String line : Datas) {
                        writer.write(line);
                        writer.newLine();
                    }
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("MainActivity", "Failed to save data", e);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播接收器
        LocalBroadcastManager.getInstance(this).unregisterReceiver(connectReceiver);
    }

    private void startReceivingMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = connectedSocket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String message;
                    while ((message = reader.readLine()) != null) {
                        String finalMessage = message;
                        runOnUiThread(() -> {
                            // 在 UI 线程中更新 UI
                            // 处理接收到的消息，例如更新 TextView
                            handleReceivedMessage(finalMessage);
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void stopReceivingMessages() {
        isRecording = false;
        try {
            if (connectedSocket != null && connectedSocket.isConnected()) {
                connectedSocket.getInputStream().close(); // 关闭输入流
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleReceivedMessage(String message) {
        Datas.add(message);
        StringBuilder sb = new StringBuilder();
//        for (String msg : Datas) {
//            sb.append(msg).append('\n');
//        }
//        DataMessage.setText(sb.toString());
        DataMessage.setText(message);
        Log.d(TAG, "ArrayList size: " + Datas.size());

        scrollView = findViewById(R.id.DataScrollView);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void sendMessage(String message) {
        if (connectedSocket != null && connectedSocket.isConnected()) {
            try {
                OutputStream outputStream = connectedSocket.getOutputStream();
                PrintWriter writer = new PrintWriter(outputStream, true);
                writer.println(message); // 发送消息
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No connection established", Toast.LENGTH_SHORT).show();
        }
    }

}