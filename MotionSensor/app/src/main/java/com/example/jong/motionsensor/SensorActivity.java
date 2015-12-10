package com.example.jong.motionsensor;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;


public class SensorActivity extends Activity implements Runnable{

    private float[] vOrientation = new float[3];

    // Handler for the UI plots so everything plots smoothly
    protected Handler handler;

    private com.example.jong.motionsensor.Orientation orientation;

    protected Runnable runable;

    // Acceleration plot titles
    private String plotAccelXAxisTitle = "Azimuth";
    private String plotAccelYAxiTitle = "Pitch";
    private String plotAccelZAxisTitle = "Roll";

    private TextView tvXAxis;
    private TextView tvYAxis;
    private TextView tvZAxis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        initUI();
    }

    private void initUI()
    {
        // Initialize the calibrated text views
        tvXAxis = (TextView) this.findViewById(R.id.azimuth);
        tvYAxis = (TextView) this.findViewById(R.id.pitch);
        tvZAxis = (TextView) this.findViewById(R.id.roll);
    }
    public void onResume()
    {
        super.onResume();
        reset();
        orientation.onResume();
        handler.post(runable);

    }

    public void onPause()
    {
        super.onPause();

        orientation.onPause();
        handler.removeCallbacks(runable);
    }

    private void reset()
    {
        orientation = new GyroscopeOrientation(this);
        orientation = new ImuOKfQuaternion(this);
        handler = new Handler();

        runable = new Runnable()
        {
            @Override
            public void run()
            {
                handler.postDelayed(this, 100);

                vOrientation = orientation.getOrientation();
                updateText();

            }
        };
    }

    @Override
    public void run() {

    }



    private void updateText()
    {
        tvXAxis.setText(String.format("%.2f", Math.toDegrees(vOrientation[0])));
        tvYAxis.setText(String.format("%.2f", Math.toDegrees(vOrientation[1])));
        tvZAxis.setText(String.format("%.2f", Math.toDegrees(vOrientation[2])));
        Log.d("aa",String.format("%.2f", Math.toDegrees(vOrientation[0])));
    }

}
