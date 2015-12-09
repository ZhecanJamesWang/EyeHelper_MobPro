package com.example.jong.eyehelper;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UIFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UIFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UIFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    public SensorHandler sensorHandler;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sensorHandler = new SensorHandler(getActivity());
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
            }
        });

        newLandmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("newlandmark","clicked");
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }



}
