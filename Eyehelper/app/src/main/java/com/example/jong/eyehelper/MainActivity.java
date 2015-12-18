package com.example.jong.eyehelper;

import android.content.SharedPreferences;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements UIFragment.OnFragmentInteractionListener, TextToSpeech.OnInitListener{

    FragmentManager manager;
    FragmentTransaction transaction;

    //variable for checking Voice Recognition support on user device
    private static final int VR_REQUEST = 999;
    private final String TAG = "SpeechRepeatActivity";
    private Boolean firstTIme = true;
    private ArrayList<String> suggestedWords;
    private ArrayList<String>  suggestedCommand;
    private int MY_DATA_CHECK_CODE = 0;
    public TextToSpeech repeatTTS;
    private Boolean ttsFinish = false;
    public UIFragment uiFragment;
    private Boolean routes;
    int deleteConfirm = 0;
    private String endNavRoute = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        suggestedWords = new ArrayList<>();
        Intent checkTTSIntent = new Intent();
//            //check TTS data
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
//            //start the checking Intent - will retrieve result in onActivityResult
        this.startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
//        Log.d(TAG, "tts launch sucessfully");

        manager = getSupportFragmentManager();
        setContentView(R.layout.activity_main);
        transaction = manager.beginTransaction();
        uiFragment = new UIFragment();
        transaction.replace(R.id.container, uiFragment);
        transaction.commit();

    }

    public void onFragmentInteraction(Uri uri) {
        //empty right now
    }

    public void speakAndListen(String outputSpeech, Boolean firstTIme, Boolean Listening, Boolean routes, String endNavRoute){
        this.routes = routes;
        this.endNavRoute = endNavRoute;
        //listen for results
        ttsFinish = false;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");
        Log.d(TAG, "Before Speech");
        repeatTTS.speak(outputSpeech, TextToSpeech.QUEUE_FLUSH, map);
        Log.d(TAG, "After Speech");
        setTtsListener();
        while(ttsFinish != true);
        {
            Log.d(TAG, "tts on progress");
        }
        this.firstTIme = firstTIme;
        if (Listening)
        {
            listenToSpeech();
        }

    }

    public void listenToSpeech() {

        //start the speech recognition intent passing required data
        Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //indicate package
        listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        //message to display while listening
        if (firstTIme) {
            listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a word!");
        }
        //set speech model
        listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //specify number of results to retrieve
        listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);

        //start listening
        startActivityForResult(listenIntent, VR_REQUEST);

    }


    public void findWordFromList(ArrayList<String>  detectedWords, boolean routes) {
        suggestedWords = detectedWords;
        String word = suggestedWords.get(0);
        this.routes = routes;
        String outputSpeech = getOutputSpeech(word);
        if(endNavRoute!=null){
            speakAndListen(outputSpeech, false, true, routes, endNavRoute);
        }
        else{
            speakAndListen(outputSpeech, false, true, routes, null);
        }
    }

    /**
     * onActivityResults handles:
     *  - retrieving results of speech recognition listening
     *  - retrieving result of TTS data check
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //check speech recognition result
        Log.d(TAG, "onAcitivityResult");
        String word = "haha";
        if (requestCode == VR_REQUEST && resultCode == RESULT_OK)
        {
            Log.d(TAG, "voice recognition result entry");
            //store the returned word list as an ArrayList
            ArrayList<String>  detectedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            Log.d("WHAT YOU SAY", detectedWords.get(0));
            if (firstTIme){
                findWordFromList(detectedWords, false);
            }
            else{
                suggestedCommand = detectedWords;

                if (suggestedCommand.get(0).equals("yes")) {
                    String tmpCommand = suggestedWords.get(0);
                    suggestedWords.clear();
                    suggestedWords.add(tmpCommand);
                    Toast.makeText(getApplicationContext(), "you choose " + tmpCommand, Toast.LENGTH_SHORT).show();
                    Log.d("YOU SAY YES!", tmpCommand);
                    if (endNavRoute == null) {
                        String outSpeech = "You choose " + tmpCommand;
                        ;
//                    if (endNavRoute != null){
                        speakAndListen(outSpeech, true, false, routes, endNavRoute);
                    }
//                    }
//                    else{
//                        speakAndListen(outSpeech, true, false, routes, null);
//                    }
                    if(routes){
                        uiFragment.startNavigatingRoute(tmpCommand);
                    }
                    else if(endNavRoute != null)
                    {
                        tmpCommand = endNavRoute;
                        uiFragment.startNavigatingRoute(tmpCommand);}

                    else{
                        uiFragment.addLandmarks(tmpCommand);
                    }

                }
                else if (suggestedCommand.get(0).equals("cancel")){
                    String outputSpeech = "You cancel the service";
                    speakAndListen(outputSpeech, false, false, routes, null);

                }
                else if (suggestedCommand.get(0).equals("next")){
                    try
                    {
                        suggestedWords.remove(0);
                        word = suggestedWords.get(0);
                        String outputSpeech = getOutputSpeech(word);
                        speakAndListen(outputSpeech, false, true, routes, null);
                    }
                    catch (Exception exception)
                    {
                        if (routes)
                        {
                            String outputSpeech = "Sorry, there is not route left any more";
                            speakAndListen(outputSpeech, true, false, routes, null);
                        }
                        else{

                        String outputSpeech = "Sorry, please try to speak again!";
                        speakAndListen(outputSpeech, true, true, routes, null);

                        }

                    }


                }
                else if (suggestedCommand.get(0).equals("delete")){
                    if (deleteConfirm >= 1){
                        SharedPreferences pref;
                        SharedPreferences.Editor editor;
                        pref = uiFragment.routetoDatafile(suggestedWords.get(0));
                        Log.d("delete pref", pref.toString());
                        editor = pref.edit();
                        editor.clear();
                        editor.commit();
                        File file = new File(getApplicationInfo().dataDir,"shared_prefs/"+pref.toString()+".xml");
                        file.delete();

                        deleteConfirm =0;
                        String outputSpeech = "You successfully delete the route!";
                        speakAndListen(outputSpeech, false, false, routes, null);
                    }
                    else {
                        deleteConfirm ++;
                        String outputSpeech = "Do you want to delete this route? If yes, please say the word, delete again";
                        speakAndListen(outputSpeech, false, true, routes, null);
                    }
                }
                else if (suggestedCommand.get(0).equals("reverse")){
                    Collections.reverse(suggestedWords);
                    findWordFromList(suggestedWords, true);
                }
                else{
                    word = suggestedWords.get(0);
                    String outputSpeech;
                    if (routes){
                        outputSpeech = "Sorry. I don't get it. Did you want to choose the route, " + word + " before ?" + " Please answer yes, next or cancel";
                    }
                    else{
                        outputSpeech = "Sorry. I don't get it. Did you say" + word + " before ?" + " Please answer yes, next or cancel";
                    }
                    if (endNavRoute != null){
                        outputSpeech = "Sorry, I dont get it. Do you want to start navigation this route?";
                        speakAndListen(outputSpeech, false, true, false, endNavRoute);
                    }
                }


            }
        }
        if (requestCode == MY_DATA_CHECK_CODE)
        {
            //we have the data - create a TTS instance
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                repeatTTS = new TextToSpeech(this, this);
                //data not installed, prompt the user to install it
                Log.d(TAG, "create repeatTTS instance");
            }
            else
            {
                //intent will take user to TTS download page in Google Play
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }

        //call superclass method
        super.onActivityResult(requestCode, resultCode, data);

        }


    private void setTtsListener() {
        if (Build.VERSION.SDK_INT >= 15)
        {
            int listenerResult = repeatTTS.setOnUtteranceProgressListener(new UtteranceProgressListener()
            {
                @Override
                public void onDone(String utteranceId)
                {
                    Log.d(TAG,"progress on Done " + utteranceId);
                    ttsFinish = true;
                }

                @Override
                public void onError(String utteranceId)
                {
                    Log.d(TAG,"progress on Error " + utteranceId);
                }

                @Override
                public void onStart(String utteranceId)
                {
                    Log.d(TAG,"progress on Start " + utteranceId);
                }
            });
            if (listenerResult != TextToSpeech.SUCCESS)
            {
                Log.e(TAG, "failed to add utterance progress listener");
            }
        }
        else
        {
            int listenerResult = repeatTTS.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener()
            {
                @Override
                public void onUtteranceCompleted(String utteranceId)
                {
                    Log.d(TAG,"progress on Completed " + utteranceId);
                }
            });
            if (listenerResult != TextToSpeech.SUCCESS)
            {
                Log.e(TAG, "failed to add utterance completed listener");
            }
        }
    }


    public String getOutputSpeech(String word){
        String outputSpeech;
        if (routes){
            outputSpeech = "Do you want to choose the route, " + word + "?" + "Please answer yes, next or cancel";
        }
        else{
            outputSpeech = "Did you say" + word + "?" + "Please answer yes, next or cancel";
        }
        return outputSpeech;
    }

    public void onInit(int initStatus) {
        //if successful, set locale
        if (initStatus == TextToSpeech.SUCCESS)
            repeatTTS.setLanguage(Locale.UK);//***choose your own locale here***
            String outputSpeech = "Welcome to Eye Helper project";
            speakAndListen(outputSpeech, false, false, routes, null);
            Log.d(TAG,"onInit");

    }


}
