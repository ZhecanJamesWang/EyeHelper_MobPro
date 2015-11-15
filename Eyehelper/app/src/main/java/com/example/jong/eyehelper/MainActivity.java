package com.example.jong.eyehelper;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements UIFragment.OnFragmentInteractionListener {

    FragmentManager manager;
    FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = getSupportFragmentManager();
        setContentView(R.layout.activity_main);
        transaction = manager.beginTransaction();
        UIFragment uiFragment = new UIFragment();
        transaction.replace(R.id.container, uiFragment);
        transaction.commit();

     }

    public void onFragmentInteraction(Uri uri) {
        //empty right now
    }
}
