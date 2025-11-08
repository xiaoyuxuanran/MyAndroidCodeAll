package com.example.wheel;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    LinearLayout background=null;
    ImageView center=null;
    TextView info=null;
    Button toBT=null;
    Intent intent=null;
    private double centerPoint=300;//LinearLayout组件最中心坐标，X和Y都是300
    public static int[] wheelData=new int[]{0,0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        {//绑定组件id
            background=(LinearLayout) findViewById(R.id.background);
            center=(ImageView) findViewById(R.id.center);
            info=(TextView) findViewById(R.id.info);
            toBT=(Button) findViewById(R.id.toBT);
        }

        {//LinearLayout子控件空间内的操作
            //LinearLayout的触屏事件:只需要:按下，抬起，移动三个事件
            background.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    //判断事件类型
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN://按下
                        case MotionEvent.ACTION_MOVE://移动
                            //数学问题，判断手指在一个圆形的区域内，范围半径是300,用圆形的特征公式
                            if((motionEvent.getX()-300)*(motionEvent.getX()-300)+(motionEvent.getY()-300)*(motionEvent.getY()-300)<=300*300){
                                //用ImageView的相对初始位置平移的方法，做ImageView这个中心轮盘的移动
                                center.setTranslationX(motionEvent.getX()-300);
                                center.setTranslationY(motionEvent.getY()-300);
                                //将角度和距离值赋值给TextView组件
                                info.setText("角度:"+String.valueOf((int)getAngle(motionEvent.getX(),motionEvent.getY()))
                                +"+"+"距离:"+String.valueOf((int)getDistance(motionEvent.getX(), motionEvent.getY())));
                                wheelData[0]=(int)getAngle(motionEvent.getY(),motionEvent.getX());
                                wheelData[1]=(int)getDistance(motionEvent.getY(),motionEvent.getX());
                            }
                            break;
                        case MotionEvent.ACTION_UP://抬起操作
                            //不要忘记，中心轮盘要归中
                            center.setTranslationX(0);
                            center.setTranslationY(0);
                            info.setText("角度:0+距离:0");
                            wheelData[0]=0;
                            wheelData[1]=0;
                            break;
                        default://其他操作，不做响应
                            break;
                    }
                    return true;//表示触摸事件已处理，则可以进行移动的响应
                }
            });
            toBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent=new Intent(MainActivity.this,BluetoothActivity.class);
                    startActivity(intent);
                }
            });
        }

    }
    //获得轮盘的角度
    private double getAngle(double X,double Y){
        double angle=0;
        //根据反三角函数来转化坐标成为角度，arctan()函数
        angle=Math.atan2(Y-centerPoint,X-centerPoint);//都是与中心坐标而言
        //上面是转化为弧度制，接下来转化为角度值
        angle=Math.toDegrees(angle);
        return angle;
    }
    //获得轮盘的移动距离
    private double getDistance(double X,double Y){
        double distance=0;
        //根据两点之间的坐标来得到距离值
        distance=Math.sqrt(Math.pow(X-centerPoint,2)+Math.pow(Y-centerPoint,2));
        return distance;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //软件退出后清空，断开蓝牙操作
        BluetoothActivity.connectThread.cancel();
        BluetoothActivity.connectedThread.cancel();
    }
}