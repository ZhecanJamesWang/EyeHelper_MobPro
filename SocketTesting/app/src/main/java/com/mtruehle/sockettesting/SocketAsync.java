package com.mtruehle.sockettesting;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * An extension of AsyncTask; used to create a socket, send a string to a given IP address, and close the socket.
 * Used to pass information to a server.
 * Parameters: a String[] with (ipAddress, stringToSend).
 * Created by matt on 11/10/15.
 */
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
