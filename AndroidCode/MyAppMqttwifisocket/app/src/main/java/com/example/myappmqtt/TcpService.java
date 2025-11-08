// TcpService.java
package com.example.myappmqtt;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import org.json.JSONObject;
import org.json.JSONException;


public class TcpService extends Service {
    private MyTcpClient myTcpClient;
    public static final String myipaddress = "192.168.4.1";
    public static final int myport = 8585;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

@Override
public int onStartCommand(Intent intent, int flags, int startId) {
    // 处理来自MainActivity的命令发送请求
    if (intent != null) {
        String command = intent.getStringExtra("command");
        if (command != null && myTcpClient != null) {
            sendCommand(command);
            Log.d("TcpService", "接收到命令: " + command);
        }
    }

    // 初始化TCP连接（仅在首次启动时）
    if (myTcpClient == null) {
        myTcpClient = new MyTcpClient();
        connectToServer();
    }

    return START_STICKY;
}


    private void connectToServer() {
        new Thread(() -> {
            if(myTcpClient.connect(myipaddress, myport)) {
                handleReceivedMessages();
            }
        }).start();
    }

    private boolean isCompleteMessage(String message) {
    // 首先检查是否以换行符结尾
    if (!message.endsWith("\n")) {
        return false;
    }

    // 然后检查是否包含完整的JSON对象
    int openBrace = message.indexOf("{");
    int closeBrace = message.lastIndexOf("}");

    // 确保JSON对象在换行符之前
    if (openBrace != -1 && closeBrace != -1 &&
        closeBrace > openBrace &&
        closeBrace < message.length() - 1) { // 确保}后面还有换行符
        return true;
    }

    return false;
}


private void handleReceivedMessages() {
    new Thread(() -> {
        StringBuilder completeMessage = new StringBuilder();
        while (true) {
            if(myTcpClient.serverStatus != -1) {
                String msg = myTcpClient.rcvMsg();
                if (msg != null) {
                    completeMessage.append(msg);
                    // 检查是否收到了完整的JSON消息
                    String currentMessage = completeMessage.toString();
                    if (isCompleteMessage(currentMessage)) {
                        // 提取JSON内容（去除换行符）
                        int startIndex = currentMessage.indexOf("{");
                        int endIndex = currentMessage.lastIndexOf("}") + 1;
                        if (startIndex != -1 && endIndex > startIndex) {
                            String jsonStr = currentMessage.substring(startIndex, endIndex);
                            parseJsonMessage(jsonStr);
                        }
                        completeMessage.setLength(0); // 清空缓冲区
                    }
                } else {
                    break;
                }
            }
        }
    }).start();
}

private void parseJsonMessage(String jsonStr) {
    try {
        JSONObject jsonObject = new JSONObject(jsonStr);

        // 提取所有值
        String ec_value        = jsonObject.getString("ec");
        String ph_value       = jsonObject.getString("ph");
        String temp_value         = jsonObject.getString("tv");
        String water_value        = jsonObject.getString("wv");
        String dry_value        = jsonObject.getString("dry");
        String text_value        = jsonObject.getString("text");

        // 记录解析结果
        Log.d("TcpService", "解析JSON成功: " + jsonStr);
        Log.d("TcpService", "=============================================");
        Log.d("TcpService", "电导率: " + ec_value);
        Log.d("TcpService", "PH: " + ph_value);
        Log.d("TcpService", "水温: " + temp_value);
        Log.d("TcpService", "水位: " + water_value);
        Log.d("TcpService", "浊度: " + dry_value);
        Log.d("TcpService", "文本: " + text_value);
        Log.d("TcpService", "=============================================");
        // 这里可以根据需要处理提取出的值
        SharedData data = SharedData.getInstance();
        data.ec = ec_value;
        data.ph = ph_value;
        data.temp = temp_value;
        data.water = water_value;
        data.dry = dry_value;
        data.text = text_value;

    } catch (JSONException e) {
        Log.e("TcpService", "JSON解析失败: " + jsonStr, e);
    }
}



public void sendCommand(String cmd) {
    if (myTcpClient != null) {
        new Thread(() -> {
            myTcpClient.sendMsg(cmd);
            Log.d("TcpService", "发送命令成功: " + cmd);
        }).start();
    } else {
        Log.e("TcpService", "TCP客户端未初始化，无法发送命令: " + cmd);
    }
}


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myTcpClient != null) {
            myTcpClient.closeAll();
        }
    }
}
