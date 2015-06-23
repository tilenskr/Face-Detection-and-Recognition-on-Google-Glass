package com.opencv.tilen.facedetectionandrecognition_urvrv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.google.android.glass.widget.Slider;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StaticImagesActivity extends Activity{

    private CardScrollView mCardScroller;
    private CardScrollAdapter mAdapter;

    private List<PictureData> resourcePictures;
    private static final int FACES_NUMBER_REQUEST = 1;

    private FaceDetection faceDetection;

    // slider
    private Slider mSlider;
    private Slider.Indeterminate mIndeterminate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getResourceDrawables();
        mAdapter = new StaticImagesCardAdapter(this, resourcePictures);
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(mAdapter);
        // Set the view for the Slider
        mSlider = Slider.from(mCardScroller);
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(mCardScroller);
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                mAudioManager.playSoundEffect(Sounds.TAP);
                openOptionsMenu();
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
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS ||
                featureId == Window.FEATURE_OPTIONS_PANEL) {
            getMenuInflater().inflate(R.menu.menu_voice_main, menu);
            return true;
        }
        // Pass through to super to setup touch menu.
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if(featureId == WindowUtils.FEATURE_VOICE_COMMANDS ||
                featureId == Window.FEATURE_OPTIONS_PANEL)
        {
            switch (item.getItemId())
            {
                case R.id.itemDetect:
                    faceDetection = FaceDetection.getInstance(this);
                    if(featureId == Window.FEATURE_OPTIONS_PANEL)
                        closeOptionsMenu();
                    checkFacesOnImage();
                    break;
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /** get ResourceID and ResourceName **/
    private void getResourceDrawables()
    {
        // java reflection
        Field[] drawables = R.drawable.class.getFields();
        String drawableName;
        int drawableResourceId;
        PictureData pictureData;
        resourcePictures = new ArrayList<>();
        for (Field f : drawables) {
            try {
                drawableName = f.getName();
                drawableResourceId = f.getInt(null);
                Global.TestDebug("R.drawable." + drawableName + " id: " + drawableResourceId);
                if (drawableName.startsWith("test_image")) // declaration to follow
                {
                    pictureData= new PictureData(drawableResourceId, drawableName);
                    resourcePictures.add(pictureData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkFacesOnImage()
    {
        // slider slows down performance TODO - needs some actual tests
        mIndeterminate = mSlider.startIndeterminate();
        PictureData pictureData = (PictureData) mCardScroller.getSelectedItem();
        /*Bitmap bitmapImage = BitmapFactory.decodeResource(getResources(),
                pictureData.getResourceId());
        Mat matImage= MyUtils.bitmapToMat(bitmapImage);*/
        Mat matImage = null;
        try {
            matImage = Utils.loadResource(this, pictureData.getResourceId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Mat[] faceImages = faceDetection.getFacePictures(matImage);
        if(faceImages != null) {
            MyUtils.saveBitmaps(faceImages, this); // it takes some time (not the best)
            Intent intent = new Intent(this, FacesActivity.class);
            intent.putExtra(FacesActivity.RESOURCENAME, pictureData.getResourceName());
            intent.putExtra(FacesActivity.FACENUMBER, faceImages.length);
            mIndeterminate.hide();
            startActivity(intent);
        }
        else {
            mIndeterminate.hide();
            AlertDialog alertDialog = new AlertDialog(this, R.drawable.ic_warning_150, R.string.no_face, R.string.tap_choose_picture);
            alertDialog.setCancelable(true);
            alertDialog.show();
        }
        //Toast.makeText(this,"No Face Detected", Toast.LENGTH_LONG).show();

    }
}
