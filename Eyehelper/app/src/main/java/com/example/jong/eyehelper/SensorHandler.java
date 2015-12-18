package com.example.jong.eyehelper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

/**
 * Created by root on 11/17/15.
 */
public class SensorHandler implements SensorEventListener{
    public String ipAddress = "192.168.32.162";
    private SensorManager mSensorManager;
    private Sensor mRotationSensor;

    private static final int SENSOR_DELAY = 500 * 1000; // 500ms
    private static final int FROM_RADS_TO_DEGS = -57;
    private boolean firstTime = true;
    private float offset;
    private SocketCallback socketCallback;


    public SensorHandler(Context context, SocketCallback socketCallback){
        try {
            this.socketCallback = socketCallback;
            mSensorManager = (SensorManager) context.getSystemService(Activity.SENSOR_SERVICE);
            mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY);
        } catch (Exception e) {
            Toast.makeText(context, "Hardware compatibility issue", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mRotationSensor) {
            if (event.values.length > 4) {
                float[] truncatedRotationVector = new float[4];
                System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4);
                update(truncatedRotationVector);
            } else {
                update(event.values);
            }
        }
    }

    private void update(float[] vectors) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);
        int worldAxisX = SensorManager.AXIS_X;
        int worldAxisY = SensorManager.AXIS_Y;
        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisY, adjustedRotationMatrix);
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);


        float yaw = orientation[0] * FROM_RADS_TO_DEGS;

//        if (firstTime){
//            offset = -yaw;
//            firstTime = false;
//        }

        float pitch = orientation[1] * FROM_RADS_TO_DEGS;
        float Roll = orientation[2] * FROM_RADS_TO_DEGS;

//        yaw = yaw + offset;
        String messageText = "Yaw" + " " + Float.valueOf(yaw).toString();
        String listForAsync[];
        listForAsync = new String[] {ipAddress, messageText};
        new SocketAsync(socketCallback).execute(listForAsync);

    }


}
