package com.example.wifiapplication_840_7;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button connectButton;
    private Switch swh1_status;
    private Switch swh2_status;
    private Switch swh3_status;
    private MediaPlayer mMediaPlayer;  //定义一个媒体文件
    private Vibrator vibrator;
    Socket clientSocket;
    boolean StartPlayFlag = false;
    boolean StopPlayFlag = false;
    private PrintWriter clientPrintWriter;
    private InputStreamReader clientInputStream;
    private String smsg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibrator = (Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        mMediaPlayer = MediaPlayer.create(MainActivity.this,R.raw.tal); //引入mp3文件

        connectButton = (Button)findViewById(R.id.connectButton);
        swh1_status = (Switch) findViewById(R.id.time1Switch);
        swh2_status = (Switch) findViewById(R.id.time2Switch);
        swh3_status = (Switch) findViewById(R.id.time3Switch);
        //设置Switch事件监听
        swh1_status.setOnCheckedChangeListener(this);
        swh2_status.setOnCheckedChangeListener(this);
        swh3_status.setOnCheckedChangeListener(this);
}
    /**
     * 重写事件分发
     */
    @Override
    public  boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                v.clearFocus();//清除Edittext的焦点从而让光标消失
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        boolean vinstanceof;
        if (v !=null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationOnScreen(l);;
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getRawX() > left &&event.getRawX() < right
                    && event.getRawY()> top && event.getRawY() < bottom) {
                //点击EditText的时候不做隐藏处理
                return false;
            } else {
                return true;
            }
        }
        //如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param token
     */
    private void hideKeyboard(IBinder token) {

        if (token !=null) {
            //若token不为空则获取输入法管理器使其隐藏输入法键盘
            InputMethodManager im =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    boolean isConnect = true;
    public void onConnectButtonClicked(View view) {
        connectButton.setEnabled(false);
        if(connectButton.getText().toString().equals("连接")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // clientSocket = new Socket(ipStr, port);
                        clientSocket = new Socket("192.168.4.1", 8080);
                        clientSocket.setSoTimeout(6000);
                        clientPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8")), true);
                        clientInputStream = new InputStreamReader(clientSocket.getInputStream());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                connectButton.setEnabled(true);
                                connectButton.setText("断开");
                            }
                        });
                        isConnect = true;
                        while (isConnect && !clientSocket.isClosed() && !clientSocket.isInputShutdown()) {
                            try {
                                char buff[]  = new char[4096];
                                int rcvLen = clientInputStream.read(buff);
                                if (rcvLen != -1 ){
                                    String rcvMsg = new String(buff,0,rcvLen);
                                    Log.i(TAG, "run:收到消息: " + rcvMsg);
                                    smsg+=rcvMsg;   //写入接收缓存
                                    handler.sendMessage(handler.obtainMessage());
                                }else {
                                    break;
                                }
                            } catch (Exception e) {}
//                                String receiveMsg = clientInputStream.readLine();
//                                if (receiveMsg != null) {
//                                    Log.d(TAG, "receiveMsg:" + receiveMsg);
//                                    smsg+=receiveMsg;   //写入接收缓存
//                                    handler.sendMessage(handler.obtainMessage());
//                                }
                        }
                        Log.e(TAG, "client socket close!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                connectButton.setEnabled(true);
                                connectButton.setText("连接");
                            }
                        });
                        try{
                            clientSocket.close();
                        }catch (Exception e){}
                    } catch (final Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                connectButton.setEnabled(true);
                                connectButton.setText("连接");
                            }
                        });
                        Log.e(TAG, ("connectService:" + e.getMessage()));   //如果Socket对象获取失败，即连接建立失败，会走到这段逻辑
                    }
                }
            }).start();
        }else if(connectButton.getText().toString().equals("断开")){
//                connectButton.setEnabled(true);
            isConnect = false;
        }
    }

    public void onExitClicked(View view) {
        if(clientSocket!=null)  //关闭连接socket
        try{
            clientSocket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        mMediaPlayer.stop();
        vibrator.cancel();
        finish();     //退出APP
    }

    //消息处理队列
    @SuppressLint("HandlerLeak")
    Handler handler= new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);

            int intIndex1 = smsg.indexOf("$temp:");
            if(intIndex1!=-1) {
                String str = smsg;
                TextView textView = findViewById(R.id.textViewTemperature);
                textView.setText(str.substring(intIndex1+6,str.indexOf("#",intIndex1))+"℃");   //显示数据
            }

            int intIndex2 = smsg.indexOf("$humi:");
            if(intIndex2!=-1) {
                String str = smsg;
                TextView textView = findViewById(R.id.textViewHumi);
                textView.setText(str.substring(intIndex2+6,str.indexOf("#",intIndex2))+"%");   //显示数据
            }
            int intIndex3 = smsg.indexOf("$weight:");
            if(intIndex3!=-1) {
                String str = smsg;
                TextView textView = findViewById(R.id.textViewWeight);
                textView.setText(str.substring(intIndex3+8,str.indexOf("#",intIndex3))+"g");   //显示数据
            }
            int intIndex4 = smsg.indexOf("time1_on");
            if(intIndex4!=-1) {
                swh1_status.setChecked(true);
            }
            int intIndex5 = smsg.indexOf("time1_off");
            if(intIndex5!=-1) {
                swh1_status.setChecked(false);
            }
            int intIndex6 = smsg.indexOf("time2_on");
            if(intIndex6!=-1) {
                swh2_status.setChecked(true);
            }
            int intIndex7 = smsg.indexOf("time2_off");
            if(intIndex7!=-1) {
                swh2_status.setChecked(false);
            }
            int intIndex8 = smsg.indexOf("time3_on");
            if(intIndex8!=-1) {
                swh3_status.setChecked(true);
            }
            int intIndex9 = smsg.indexOf("time3_off");
            if(intIndex9!=-1) {
                swh3_status.setChecked(false);
            }
            int intIndex10 = smsg.indexOf("play");
            if(intIndex10!=-1) {
                if(StartPlayFlag==false) {
                    StartPlayFlag = true;
                    StopPlayFlag = false;
                    mMediaPlayer = MediaPlayer.create(MainActivity.this,R.raw.tal); //引入mp3文件

                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.start();

                    vibrator = (Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(new long[]{0,500,300,500},0);
                }
            }
            int intIndex12 = smsg.indexOf("stop");
            if(intIndex12!=-1) {
                if(StopPlayFlag==false) {
                    StopPlayFlag = true;
                    StartPlayFlag = false;

                    mMediaPlayer.stop();
                    vibrator.cancel();
                }
            }
            smsg="";
           // inTextView.setText(smsg);   //显示数据
           // scrollView.scrollTo(0, inTextView.getMeasuredHeight()); //跳至数据最后一页
        }
    };

    //关闭程序掉用处理部分
    public void onDestroy(){
        super.onDestroy();
        if(clientSocket!=null)  //关闭连接socket
            try{
                clientSocket.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        mMediaPlayer.stop();
        vibrator.cancel();
    }

    /*
              继承监听器的接口并实现onCheckedChanged方法
      * */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.time1Switch:
                if(clientSocket==null || clientPrintWriter==null){
                    Toast.makeText(this, "请先连接", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(isChecked){
                    Toast.makeText(MainActivity.this, "“吃药时间1”开启提醒", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            clientPrintWriter.println("time1Switch_on\r\n");
                            clientPrintWriter.flush();
                        }
                    }).start();
                }else {
                    Toast.makeText(MainActivity.this, "“吃药时间1”关闭提醒", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            clientPrintWriter.println("time1Switch_off\r\n");
                            clientPrintWriter.flush();
                        }
                    }).start();
                }
                break;

            case R.id.time2Switch:
                if(clientSocket==null || clientPrintWriter==null){
                    Toast.makeText(this, "请先连接", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(isChecked){
                    Toast.makeText(MainActivity.this, "“吃药时间2”开启提醒", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            clientPrintWriter.println("time2Switch_on\r\n");
                            clientPrintWriter.flush();
                        }
                    }).start();
                }else {
                    Toast.makeText(MainActivity.this, "“吃药时间2”关闭提醒", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            clientPrintWriter.println("time2Switch_off\r\n");
                            clientPrintWriter.flush();
                        }
                    }).start();
                }
                break;

            case R.id.time3Switch:
                if(clientSocket==null || clientPrintWriter==null){
                    Toast.makeText(this, "请先连接", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(isChecked){
                    Toast.makeText(MainActivity.this, "“吃药时间3”开启提醒", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            clientPrintWriter.println("time3Switch_on\r\n");
                            clientPrintWriter.flush();
                        }
                    }).start();
                }else {
                    Toast.makeText(MainActivity.this, "“吃药时间3”关闭提醒", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            clientPrintWriter.println("time3Switch_off\r\n");
                            clientPrintWriter.flush();
                        }
                    }).start();
                }
                break;
            default:
                break;
        }
    }
        public void onSendButtonClicked(View view) {
        if(clientSocket==null || clientPrintWriter==null){
            Toast.makeText(this, "请先连接", Toast.LENGTH_SHORT).show();
            return;
        }

        TextView edit0 = findViewById(R.id.setTime1Hour);
        TextView edit1 = findViewById(R.id.setTime1Min);
        TextView edit2 = findViewById(R.id.setTime2Hour);
        TextView edit3 = findViewById(R.id.setTime2Min);
        TextView edit4 = findViewById(R.id.setTime3Hour);
        TextView edit5 = findViewById(R.id.setTime3Min);

        TextView edit6 = findViewById(R.id.setMedicineANum);
        TextView edit7 = findViewById(R.id.setMedicineBNum);
        TextView edit8 = findViewById(R.id.setMedicineCNum);
        TextView edit9 = findViewById(R.id.setMedicineDNum);

        if(edit0.getText().length()==0 || edit1.getText().length()==0 || edit2.getText().length()==0
        || edit3.getText().length()==0 || edit4.getText().length()==0 || edit5.getText().length()==0
        || edit6.getText().length()==0 || edit7.getText().length()==0 || edit8.getText().length()==0
        || edit9.getText().length()==0){
            Toast.makeText(this, "请先输入数据", Toast.LENGTH_SHORT).show();
            return;
        }

        final String str1 = edit0.getText().toString();
        final String str2 = edit1.getText().toString();
        final String str3 = edit2.getText().toString();
        final String str4 = edit3.getText().toString();
        final String str5 = edit4.getText().toString();
        final String str6 = edit5.getText().toString();
        final String str7 = edit6.getText().toString();
        final String str8 = edit7.getText().toString();
        final String str9 = edit8.getText().toString();
        final String str10 = edit9.getText().toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
                clientPrintWriter.println("one:"+str1+":"+str2 +
                                          "two:"+str3+":"+str4 +
                                          "three:"+str5+":"+str6 + "," +
                                          "MedA"+str7+"," +"MedB"+str8+ "," +
                                          "MedC"+str9+"," +"MedD"+str10+"," +"\r\n");
                clientPrintWriter.flush();
            }
        }).start();
    }

    public void oncloseRemButtonClicked(View view) {

        if(clientSocket==null || clientPrintWriter==null){
            Toast.makeText(this, "请先连接", Toast.LENGTH_SHORT).show();
            return;
        }
        mMediaPlayer.pause();
        vibrator.cancel();
        new Thread(new Runnable() {
            @Override
            public void run() {
                clientPrintWriter.println("closeRemind\r\n");
                clientPrintWriter.flush();
            }
        }).start();
    }
}
