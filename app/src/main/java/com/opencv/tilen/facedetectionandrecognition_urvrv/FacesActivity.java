package com.opencv.tilen.facedetectionandrecognition_urvrv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.IOException;

public class FacesActivity extends Activity {
    public final static String RESOURCEID = "resource_id";
    public final static String RESOURCENAME = "resource_name";
    private CardScrollView mCardScroller;
    private CardScrollAdapter mAdapter;

    Mat originalPicture;
    Mat[] facePictures;
    String pictureName;
    private FaceDetection faceDetection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeFaceImages();
        mAdapter = new FacesImagesCardAdapter(this, pictureName,facePictures);
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(mAdapter);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(mCardScroller);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_faces, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    private void initializeFaceImages()
    {
        Intent intent = getIntent();
        int pictureResourceId = intent.getIntExtra(RESOURCEID,-1);
        pictureName = intent.getStringExtra(RESOURCENAME);
        // you probably don't need to implement BaseLoaderCallback, because you do in Main Activity ?
        try {
            originalPicture = Utils.loadResource(this, pictureResourceId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        faceDetection = FaceDetection.getInstance(this);
        facePictures = faceDetection.getFacePictures(originalPicture);
        if(facePictures != null )
            setResult(RESULT_OK, null);
        else
        {
            setResult(RESULT_CANCELED, null);// don't need Intent data
            finish();
        }
    }
}
