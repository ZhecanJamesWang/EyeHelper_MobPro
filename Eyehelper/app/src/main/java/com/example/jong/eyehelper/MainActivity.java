package com.example.jong.eyehelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button gotoexisting = (Button) findViewById(R.id.existing_landmark);
        Button newlandmark = (Button) findViewById(R.id.new_landmark);
        Button nextlandmark = (Button) findViewById(R.id.next_landmark);
        Button prevlandmark= (Button) findViewById(R.id.prev_landmark);
        Button settings = (Button) findViewById(R.id.settings);

        gotoexisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("goexisting","clicked");
            }
        });

        newlandmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("newlandmark","clicked");
            }
        });

        nextlandmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("nextlandmark","clicked");
            }
        });

        prevlandmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("prevlandmark","clicked");
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("settings","clicked");
            }
        });
     }

}
