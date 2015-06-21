package com.opencv.tilen.facedetectionandrecognition_urvrv;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FacesActivity extends Activity {
    public final static String RESOURCENAME = "resource_name";
    public final static String FACENUMBER = "face_number;";

    private CardScrollView mCardScroller;
    private CardScrollAdapter mAdapter;

    Bitmap[] facePictures;
    String pictureName;

    private static final int SPEECH_REQUEST = 0;

    TextToSpeech textToSpeech;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeFaceImages();
        mAdapter = new FacesImagesCardAdapter(this, pictureName,facePictures);
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(mAdapter);
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(mCardScroller);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener(){

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mCardScroller.deactivate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            getMenuInflater().inflate(R.menu.menu_faces, menu);
            return true;
        }
        // Pass through to super to setup touch menu.
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if(featureId == WindowUtils.FEATURE_VOICE_COMMANDS)
        {
            switch (item.getItemId())
            {
                case R.id.itemPredict:
                    // when predicts a face notifies user as sound trough TextToSpeech
                    //TODO change here
                    textToSpeech.speak(String.format(getString(R.string.face_result_format), "John" ),TextToSpeech.QUEUE_FLUSH, // old API level method, since we use 19 is ok (deprecated in 21)
                            null);
                    break;
                case R.id.itemTrain:
                    // speak and say nickname (or just name) that describe a person (can contain more words)
                    // bad side - needs internet to work
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.say_name));
                    startActivityForResult(intent, SPEECH_REQUEST);
                    break;
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
            List<String> spokenText = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            Global.InfoDebug("FacesActivity.onActivityResult(): spokenText: " + Arrays.toString(spokenText.toArray()));
            // TODO Do something with spokenText.
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initializeFaceImages()
    {
        Intent intent = getIntent();
        pictureName = intent.getStringExtra(RESOURCENAME);
        int numberOfFaces = intent.getIntExtra(FACENUMBER, -1);
        facePictures = LocalPicturesDetection.loadBitmaps(numberOfFaces,this);

    }
}
