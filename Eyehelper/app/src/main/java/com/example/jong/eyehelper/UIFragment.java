package com.example.jong.eyehelper;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UIFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UIFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UIFragment extends Fragment implements Runnable{
    private OnFragmentInteractionListener mListener;
    private String ipAddress = "192.168.35.53";
    private static final int FROM_RADS_TO_DEGS = 57;
    protected Handler handler;
    private Context context;
    private Orientation orientation;
    private float[] vOrientation = new float[3];
    protected Runnable runable;



    public static UIFragment newInstance(String param1, String param2) {
        UIFragment fragment = new UIFragment();
        return fragment;
    }

    public UIFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View uiLayout = inflater.inflate(R.layout.fragment_ui, container, false);
        Button goToExisting = (Button) uiLayout.findViewById(R.id.existing_landmark);
        Button newLandmark = (Button) uiLayout.findViewById(R.id.new_landmark);
        Button nextLandmark = (Button) uiLayout.findViewById(R.id.next_landmark);
        Button prevLandmark= (Button) uiLayout.findViewById(R.id.prev_landmark);
        Button settings = (Button) uiLayout.findViewById(R.id.settings);

        goToExisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("goexisting", "clicked");
                reset();
                orientation.onResume();
                handler.post(runable);
            }
        });

        newLandmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("newlandmark","clicked");
                orientation.onPause();
                handler.removeCallbacks(runable);
            }
        });

        nextLandmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("nextlandmark","clicked");
            }
        });

        prevLandmark.setOnClickListener(new View.OnClickListener() {
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


        return uiLayout;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void run() {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void reset()
    {
        orientation = new GyroscopeOrientation(context);
        Log.d("reset called","aaaaaaaa");
        handler = new Handler();

        runable = new Runnable()
        {
            @Override
            public void run()
            {
                handler.postDelayed(this, 100);
                vOrientation = orientation.getOrientation();
                update(vOrientation);

            }
        };
    }

    private void update(float[] vectors) {
        float yaw = vectors[0] * FROM_RADS_TO_DEGS;
        String messageText = "Yaw" + " " + Float.valueOf(yaw).toString();
        String listForAsync[];
        listForAsync = new String[] {ipAddress, messageText};
        new SocketAsync().execute(listForAsync);

    }


}
