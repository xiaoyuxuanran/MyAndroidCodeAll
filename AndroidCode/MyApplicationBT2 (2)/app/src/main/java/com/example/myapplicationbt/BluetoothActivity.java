package com.example.myapplicationbt;

import static com.example.myapplicationbt.BTthread.ConnectThread.bluetoothSocket;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.Toast;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplicationbt.BTthread.ConnectThread;
import com.example.myapplicationbt.BTthread.ConnectedThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {


    public static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int REQUEST_ENABLE_BT = 1;
    Button back = null;
    ListView btList = null;
    public static String bleetoothName=null;
    Intent intent = null;

    BluetoothAdapter bluetoothAdapter = null;
    List<String> devicesNames = new ArrayList<>();
    ArrayList<BluetoothDevice> readDevices = null;
    ArrayAdapter<String> btName= null;
    private ActivityResultLauncher<Intent> enableBtLauncher;

    ConnectThread connectThread=null;
    public static ConnectedThread connectedThread=null;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        back=(Button) findViewById(R.id.back);
        btList=(ListView) findViewById(R.id.btList);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothSocket!=null&&bluetoothSocket.isConnected()){
                    connectedThread=new ConnectedThread(bluetoothSocket);
                    connectedThread.start();
                    Toast.makeText(BluetoothActivity.this, "已开启数据线程", Toast.LENGTH_SHORT).show();
                }


                intent=new Intent(BluetoothActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


//        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
//        if (!bluetoothAdapter.isEnabled()){
//            intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            enableBtLauncher.launch(intent);
//        }
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
          // Device doesn't support Bluetooth
            Toast.makeText(this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
        }
        if (!bluetoothAdapter.isEnabled()) {
              Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
              startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        @SuppressLint("MissingPermission") Set<BluetoothDevice> pairedDevice=bluetoothAdapter.getBondedDevices();
        readDevices=new ArrayList();
        if (pairedDevice.size()>0&&pairedDevice!=null){
            for (BluetoothDevice device:pairedDevice){
                readDevices.add(device);
                devicesNames.add(device.getName());
                btName=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, devicesNames);
            }
            btList.setAdapter(btName);

        }else{
            Toast.makeText(this, "没有设备已配对", Toast.LENGTH_SHORT).show();
        }
        btList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(BluetoothActivity.this, "点击了"+readDevices.get(position).getName(), Toast.LENGTH_SHORT).show();
                if (connectThread!=null){
                    Toast.makeText(BluetoothActivity.this, "已断开"+readDevices.get(position).getName(), Toast.LENGTH_SHORT).show();
                    connectThread.cancel();
                    connectThread=null;
                    bleetoothName=null;

                }else{
                    connectThread=new ConnectThread(readDevices.get(position));
                    connectThread.start();
                    Toast.makeText(BluetoothActivity.this, "已连接"+readDevices.get(position).getName(), Toast.LENGTH_SHORT).show();
                    bleetoothName=readDevices.get(position).getName();
                }

            }
        });



    }
}
