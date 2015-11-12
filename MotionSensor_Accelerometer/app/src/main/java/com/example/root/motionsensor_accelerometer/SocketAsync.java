package com.example.root.motionsensor_accelerometer;

/**
 * Created by shruti on 11/11/15.
 */
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketAsync extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String[] strings) {
        int port = 8888;
        Socket socket = null;
        DataOutputStream dataOutputStream = null;
        String ipAddress = strings[0];
        String messageToSend = strings[1];
        try {
            socket = new Socket(ipAddress, port);
//            socket = new Socket("10.7.64.225", 8888);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(messageToSend);
            dataOutputStream.close();
            socket.close();
        } catch (IOException ex) {
            Log.e("socket_error", ex.getMessage());
        }
        return null;
    }
}