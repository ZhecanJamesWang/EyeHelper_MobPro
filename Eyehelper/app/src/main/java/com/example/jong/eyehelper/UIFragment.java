package com.example.jong.eyehelper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UIFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UIFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UIFragment extends Fragment{
    private OnFragmentInteractionListener mListener;
    public SensorHandler sensorHandler;
    private MainActivity mainActivity;

    private String TAG = "UIFragment";
    private String[] prefList;
    private ArrayList<String> wordlist = new ArrayList<String>();
    public String currentPres;
    public SocketCallback socketCallback;


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
        mainActivity = (MainActivity)getActivity();
        socketCallback = new SocketCallback() {
            @Override
            public void onTaskCompleted(String receivedData) {
                Log.d("receivedData", receivedData);
                String [] response = receivedData.split(",");
                if (response[0].equals("xyz")){
                    String x = response[1];
                    String y = response[2];
                    String point = x+","+y+";";
                    Log.d("onTaskCompleted","saving "+point);
                    addToDatabase("point", point, true);
                }
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView ");

        sensorHandler = new SensorHandler(getActivity(), socketCallback);
        View uiLayout = inflater.inflate(R.layout.fragment_ui, container, false);

        Button existingRoutes = (Button) uiLayout.findViewById(R.id.existingRoutes);
        Button startNewRoute = (Button) uiLayout.findViewById(R.id.startNewRoute);
        Button endNewRoute = (Button) uiLayout.findViewById(R.id.endNewRoute);
        Button Zero = (Button) uiLayout.findViewById(R.id.Zero);
        Button point = (Button) uiLayout.findViewById(R.id.point);


        existingRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordlist.clear();
                String outSpeech = "The existing routes are as followings: ";
                mainActivity.speakAndListen(outSpeech, true, false, true, null);
                Log.d("existing routes", "clicked");
                prefList = AllSharePref();
                Log.d("Data File Length", String.valueOf(prefList.length));
                for (int i = 0; i<prefList.length; i++)
                {
                    Log.d("index", String.valueOf(i));
                    outSpeech = datafiletoRoute(prefList[i]);
                    if (outSpeech != null){
//                        Log.d("the routes", outSpeech);
                        wordlist.add(outSpeech);
                    }
                }
                mainActivity.findWordFromList(wordlist, true);

                    }
        });

        startNewRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listForAsync[];
                String messageText = "cmd new";
                listForAsync = new String[] {sensorHandler.ipAddress, messageText};
                new SocketAsync(socketCallback).execute(listForAsync);

                String latitude = String.valueOf(getLatitude());
                String longitude = String.valueOf(getLongitude());
                String [] allpreflist = AllSharePref();
                int max = 0;
                for (int i =0; i<allpreflist.length; i++){
                    if ((allpreflist[i].contains(latitude))&& (allpreflist[i].contains(longitude))){
                        String[] tmpArray = allpreflist[i].split(",");
                        int tmp = Integer.parseInt(tmpArray[2]);
                        if (tmp > max){max = tmp;}
                    }
                }

                currentPres = latitude + "," + longitude + "," + String.valueOf(max+1);
                Log.d("currentPres", currentPres.toString());
                Log.d("start new route", "clicked");
                String outputSpeech = "Starting a new route. What do you want the first landmark name to be?";
                mainActivity.speakAndListen(outputSpeech, true, true, false, null);
            }
        });


        endNewRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listForAsync[];
                String messageText = "cmd end";
                listForAsync = new String[] {sensorHandler.ipAddress, messageText};
                new SocketAsync(socketCallback).execute(listForAsync);
                Log.d("end new route", "clicked");
                String outputSpeech = "Finishing this route. What do you want the last landmark name to be?";
//                String route = datafiletoRoute(currentPres);
//                Log.d("endNewRoute", route);
                mainActivity.speakAndListen(outputSpeech, true, true, false, null);
//                startNavigatingRoute(route);
            }
        });


        Zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Ok", "clicked");
                String listForAsync[];
                String messageText = "cmd zero";
                listForAsync = new String[] {sensorHandler.ipAddress, messageText};
                new SocketAsync(socketCallback).execute(listForAsync);
            }
        });

        point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("point", "clicked");
                String listForAsync[];
                String messageText = "cmd point";
                listForAsync = new String[] {sensorHandler.ipAddress, messageText};
                new SocketAsync(socketCallback).execute(listForAsync);

//                Double x = getX();
//                Double y = getY();
//                String point = x.toString()+","+y.toString()+";";
//                addToDatabase("point", point, true);


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

    public String [] AllSharePref(){
        File prefsdir = new File(getActivity().getApplicationInfo().dataDir,"shared_prefs");
        Log.d("pref_file_path", prefsdir.toString());
        if(prefsdir.exists() && prefsdir.isDirectory()){
            prefList = prefsdir.list();
            for (int i=0; i < prefList.length; i++){
                prefList[i] = prefList[i].replace(".xml", "");
//                Log.d("prefList "+String.valueOf(i), prefList[i]);
            }
            Log.d(TAG, String.valueOf(prefList));
        }
        return prefList;
    }

    public String datafiletoRoute(String prefName)
    {
        SharedPreferences pref = getActivity().getSharedPreferences(prefName, Context.MODE_PRIVATE);
        String outSpeech;
        if (pref.contains("landmark1") && pref.contains("landmark2"))
        {
            String landmark1 = pref.getString("landmark1", null);
            String landmark2 = pref.getString("landmark2", null);
            outSpeech = "from " + landmark1 + " to " + landmark2;
        }
        else
        {
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();
            File file = new File(getActivity().getApplicationInfo().dataDir,"shared_prefs/"+prefName+".xml");
            file.delete();
            outSpeech = null;
        }

        return outSpeech;
    }
    public void startNavigatingRoute(String routes)
    {

        String listForAsync[];
        String messageText = "cmd nav";
        Log.d("satr nav", messageText);
        listForAsync = new String[] {sensorHandler.ipAddress, messageText};
        new SocketAsync(socketCallback).execute(listForAsync);
        SharedPreferences pref = routetoDatafile(routes);
        Log.d("pref", pref.toString());
        String pointsString = pref.getString("point", null);
        try {
            Log.d("pointString", pointsString);
//        String pointsString = "1.0,2.0;3.0,4.0;5.0,6.0;7.0,8.0";
            String[] point = pointsString.split(";");
            for (int i = 0; i < point.length; i++) {
                Log.d("point", point[i]);
                listForAsync = new String[]{sensorHandler.ipAddress, point[i]};
                new SocketAsync(socketCallback).execute(listForAsync);
            }
        }
        catch (Exception e){
            Log.d("Exception", e.toString());
        }
        mainActivity.speakAndListen("start navigating the route: " + routes, true, false, true, null);

    }
    public SharedPreferences routetoDatafile(String routes) {
        routes = routes.replace("from ", "");
        String [] landmarks = routes.split(" to ");
        String[] allpreflist = AllSharePref();
        for (int i = 0; i < allpreflist.length; i++) {
            SharedPreferences pref = getActivity().getSharedPreferences(allpreflist[i], Context.MODE_PRIVATE);
                if (pref.contains("landmark1")&&pref.contains("landmark2")){
                String landmark1 = pref.getString("landmark1", null);
                String landmark2 = pref.getString("landmark2", null);
                Log.d("landmark1", landmark1);
                Log.d("landmark2", landmark2);
                Log.d("landmarks[0]",landmarks[0]);
                Log.d("landmarks[1]",landmarks[1]);
                if ((landmarks[0].contains(landmark1)) && landmarks[1].contains(landmark2)) {
                    Log.d(TAG, "find the pref");
                    return pref;
                }
            }
        }
        return null;
    }

    public void addLandmarks(String landmarkName){
        Log.d(TAG, "reaching addlandmark");
        SharedPreferences pref;
        SharedPreferences.Editor editor;
        pref = getActivity().getSharedPreferences(currentPres, Context.MODE_PRIVATE);
        if (!pref.contains("landmark1")){
            addToDatabase("landmark1", landmarkName, false);
            Log.d("landmark1", landmarkName);
        }
        else if(!pref.contains("landmark2")){
            addToDatabase("landmark2", landmarkName, false);
            Log.d("landmark2", landmarkName);
            Log.d("reaching ", "endNewRoute Navigation");
            String route = datafiletoRoute(currentPres);
            String outputSpeech = "Do you want to start navigation this route?";
            mainActivity.speakAndListen(outputSpeech, false, true, false, route);
            }
        else {
            Log.d(TAG, "too many landmarks");
        }

    }
    public void addToDatabase(String key, String value, Boolean recoveryPref){
        SharedPreferences pref;
        SharedPreferences.Editor editor;
        pref = getActivity().getSharedPreferences(currentPres, Context.MODE_PRIVATE);
        editor = pref.edit();
        String tmp;
        if (recoveryPref){
            tmp = pref.getString(key, null);}
        else{
            tmp = "";
        }
        value = tmp + value;
        Log.d("addint to database", key+":"+value);
        editor.putString(key, value);
        editor.commit();
        }
    public double getLatitude(){
        double latitude = 2.000;
        return latitude;
    }
    public double getLongitude(){
        double longitude = 2.000;
        return longitude;
    }

}
