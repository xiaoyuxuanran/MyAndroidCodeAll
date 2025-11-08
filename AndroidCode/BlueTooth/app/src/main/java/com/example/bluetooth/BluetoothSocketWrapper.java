package com.example.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Parcelable;

import java.io.Serializable;

public class BluetoothSocketWrapper implements Serializable {
    private transient BluetoothSocket socket;

    public BluetoothSocketWrapper(BluetoothSocket socket) {
        this.socket = socket;
    }

    public  BluetoothSocket getSocket() {
        return socket;
    }
}
