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

    public String year;
    public String month;
    public String day;
    public String hour;
    public String minute;
    public String weather;
    public String tempUp;
    public String tempDown;
    public String temperature;
    public String humidity;
    public String n1;
    public String n2;
    public String n3;

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
        String year        = jsonObject.getString("y");
        String month       = jsonObject.getString("m");
        String day         = jsonObject.getString("d");
        String hour        = jsonObject.getString("h");
        String minute      = jsonObject.getString("M");
        String weather     = jsonObject.getString("w");
        String tempUp      = jsonObject.getString("Tup");
        String tempDown    = jsonObject.getString("Tdown");
        String temperature = jsonObject.getString("temp");
        String humidity    = jsonObject.getString("humi");
        String n1          = jsonObject.getString("n1");
        String n2          = jsonObject.getString("n2");
        String n3          = jsonObject.getString("n3");

        // 记录解析结果
        Log.d("TcpService", "解析JSON成功: " + jsonStr);
        Log.d("TcpService", "=============================================");
        Log.d("TcpService", "年: " + year);
        Log.d("TcpService", "月: " + month);
        Log.d("TcpService", "日: " + day);
        Log.d("TcpService", "时: " + hour);
        Log.d("TcpService", "分: " + minute);
        Log.d("TcpService", "天气: " + weather);
        Log.d("TcpService", "温度上限: " + tempUp);
        Log.d("TcpService", "温度下限: " + tempDown);
        Log.d("TcpService", "温度: " + temperature);
        Log.d("TcpService", "湿度: " + humidity);
        Log.d("TcpService", "Node1: " + n1);
        Log.d("TcpService", "Node2: " + n2);
        Log.d("TcpService", "Node3: " + n3);
        Log.d("TcpService", "=============================================");
        // 这里可以根据需要处理提取出的值
        SharedData data = SharedData.getInstance();
        data.year = year;
        data.month = month;
        data.day = day;
        data.hour = hour;
        data.minute = minute;
        data.weather = weather;
        data.tempUp = tempUp;
        data.tempDown = tempDown;
        data.temperature = temperature;
        data.humidity = humidity;
        data.n1 = n1;
        data.n2 = n2;
        data.n3 = n3;

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
