package com.jancar.bluetooth.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.jancar.bluetooth.global.Global;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class AcceptThread extends Thread {
    private final BluetoothServerSocket mmServerSocket;

    public AcceptThread() {
        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.
        BluetoothServerSocket tmp = null;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            // MY_UUID is the app's UUID string, also used by the client code.
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord
                    (bluetoothAdapter.getName(), Global.getUUID());
        } catch (IOException e) {
            Log.e("?!", "Socket's listen() method failed", e);
        }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e("?!", "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
//                manageMyConnectedSocket(socket);
                // 获取输出流，以便发送请求
                OutputStream outputStream = null;
                try {
                    outputStream = socket.getOutputStream();
                    // 发送请求获取通讯录数据
                    String request = "GET_CONTACTS"; // 根据通讯协议定义请求
                    outputStream.write(request.getBytes());
                    outputStream.flush();

                    // 获取输入流，以便接收数据
                    InputStream inputStream = socket.getInputStream();

                    // 读取并处理从另一侧设备发送的通讯录数据
                    StringBuilder contactData = new StringBuilder();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        contactData.append(new String(buffer, 0, bytesRead));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
//                mmServerSocket.close();
                break;
            }
        }
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e("?!", "Could not close the connect socket", e);
        }
    }
}

