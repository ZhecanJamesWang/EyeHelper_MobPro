package com.example.jong.eyehelper;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech.OnInitListener;

/**
 * SpeechRepeatActivity
 * - processes speech input
 * - presents user with list of suggested words
 * - when user selects a word from the list, the app speaks the word back using the TTS engine
 */
public class VoiceRecognitionAndTTS extends Activity implements TextToSpeech.OnInitListener {

    //variable for checking Voice Recognition support on user device
    private static final int VR_REQUEST = 999;


    //ListView for displaying suggested words
    private ListView wordList;

    //Log tag for output information
    private final String TAG = "SpeechRepeatActivity";

    private Boolean firstTIme = true;

    private ArrayList<String> suggestedWords;
    private ArrayList<String>  suggestedCommand;
    private int MY_DATA_CHECK_CODE = 0;
    public TextToSpeech repeatTTS;
    private Boolean ttsFinish = false;
    private ArrayAdapter wordlistAdapter;

    /** Create the Activity, prepare to process speech and repeat */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        //call superclass
        super.onCreate(savedInstanceState);


        //set content view
//        setContentView(R.layout.activity_main);

        //gain reference to speak button

        //gain reference to word list
        suggestedWords = new ArrayList<>();



        Intent checkTTSIntent = new Intent();
//            //check TTS data
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
//            //start the checking Intent - will retrieve result in onActivityResult
        this.startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

    }
    /**
     * Called when the user presses the speak button
     */
//    public void onClick(View v) {
//        if (v.getId() == R.id.speech_btn) {
//            //listen for results
//            String outputSpeech = "What do you want the landmark name to be?";
//            speakAndListen(outputSpeech, true);
//        }
//    }

    /**
     * Instruct the app to listen for user speech input
     */

    public void speakAndListen(String outputSpeech, Boolean firstTIme){
        //listen for results
        ttsFinish = false;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");
        repeatTTS.speak(outputSpeech, TextToSpeech.QUEUE_FLUSH, map);
        setTtsListener();
        while(ttsFinish != true);
        {
            Log.d(TAG, "tts on progress");
        }
        listenToSpeech();
        this.firstTIme = firstTIme;
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

    /**
     * onActivityResults handles:
     *  - retrieving results of speech recognition listening
     *  - retrieving result of TTS data check
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //check speech recognition result
        String word = "haha";
        if (requestCode == VR_REQUEST && resultCode == RESULT_OK)
        {

            //store the returned word list as an ArrayList
            ArrayList<String>  detectedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (firstTIme){
                suggestedWords = detectedWords;
                //set the retrieved list to display in the ListView using an ArrayAdapter
                wordlistAdapter = (ArrayAdapter)wordList.getAdapter();
                wordlistAdapter.clear();
                wordlistAdapter.addAll(suggestedWords);
                wordlistAdapter.notifyDataSetChanged();

                Log.d("onActivityResult", detectedWords.get(0));
                word = suggestedWords.get(0);

                String outputSpeech = "Did you say" + word + "?" + "Please answer yes, next or cancel";
                speakAndListen(outputSpeech, false);
            }
            else{
                suggestedCommand = detectedWords;
                if (suggestedCommand.get(0).equals("yes")) {
                    String tmpCommand = suggestedWords.get(0);
                    suggestedWords.clear();
                    suggestedWords.add(tmpCommand);
                    wordlistAdapter.clear();
                    wordlistAdapter.addAll(suggestedWords);
                    wordlistAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "you choose " + suggestedWords.get(0), Toast.LENGTH_SHORT).show();

                }
                else if (suggestedCommand.get(0).equals("cancel")){
//                    firstTIme = true;
//                    listenToSpeech();

                }
                else if (suggestedCommand.get(0).equals("next")){
                    suggestedWords.remove(0);
                    word = suggestedWords.get(0);
                    String outputSpeech = "Did you say" + word + "?" + "Please answer yes, next or cancel";
                    speakAndListen(outputSpeech, false);
                }
                else{
                    word = suggestedWords.get(0);
                    String outputSpeech = "Sorry. I dont get it. Did you say" + word + " before ?" + " Please answer yes, next or cancel";
                    speakAndListen(outputSpeech, false);
                }


            }
        }
        if (requestCode == MY_DATA_CHECK_CODE)
        {
            //we have the data - create a TTS instance
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
                repeatTTS = new TextToSpeech(this, this);
                //data not installed, prompt the user to install it
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
    public void onInit(int initStatus) {
        //if successful, set locale
        if (initStatus == TextToSpeech.SUCCESS)
            repeatTTS.setLanguage(Locale.UK);//***choose your own locale here***

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

}



