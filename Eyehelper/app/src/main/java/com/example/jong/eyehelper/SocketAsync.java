package com.example.jong.eyehelper;


import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketAsync extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String[] strings) {
        int port = 8888;
        int BUFFER_LENGTH = 16;
        Socket socket = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        String ipAddress = strings[0];
        String messageToSend = strings[1];
        try {
            socket = new Socket(ipAddress, port);
//            socket = new Socket("10.7.64.225", 8888);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());

            dataOutputStream.writeUTF(messageToSend);



            Log.d("socket_info", "be receive");
            byte[] buffer = new byte[BUFFER_LENGTH];
            int bytes_received;
            String receivedMessage = "";
//            while ((bytes_received = dataInputStream.read(buffer)) != -1) {
//            receivedMessage += new String(buffer, "UTF-8");
            receivedMessage = dataInputStream.readUTF();
//            Log.d("socket_info", "in while" + receivedMessage);
//            }
            Log.d("socket_info", receivedMessage);

            dataOutputStream.flush();
            dataOutputStream.close();
            dataInputStream.close();
            socket.close();


        } catch (IOException ex) {
            Log.e("socket_error", ex.getMessage());
        }
        return null;
    }
}