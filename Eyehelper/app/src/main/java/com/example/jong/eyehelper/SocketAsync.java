package com.example.jong.eyehelper;


import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

//Looks good for a Socket implementation
//Could use some commenting love
public class SocketAsync extends AsyncTask<String, Void, String> {
    public SocketCallback cb;
    public SocketAsync(SocketCallback cb){
         this.cb = cb;
    }
    @Override
    protected String doInBackground(String[] strings) {
        int port = 8888;
        int BUFFER_LENGTH = 128;
        Socket socket = null;
        DataOutputStream dataOutputStream = null;
        BufferedReader bufferedReader = null; // todo: rename to sth right.
        String ipAddress = strings[0];
        String messageToSend = strings[1];
        try {
            socket = new Socket(ipAddress, port);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            dataOutputStream.writeUTF(messageToSend);
            byte[] buffer = new byte[BUFFER_LENGTH];
            int bytes_received;
            String receivedMessage = "";
            receivedMessage += bufferedReader.readLine();

            dataOutputStream.close();
            bufferedReader.close();
            socket.close();

            return receivedMessage;

        } catch (IOException ex) {
            Log.e("socket_error", ex.getMessage());
        }
        return "error";
    }
    @Override
    protected void onPostExecute(String receivedData){
        cb.onTaskCompleted(receivedData);
    }

}