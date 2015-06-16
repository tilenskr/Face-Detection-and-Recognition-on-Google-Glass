package com.opencv.tilen.facedetectionandrecognition_urvrv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StaticImagesActivity extends Activity {

    private CardScrollView mCardScroller;
    private CardScrollAdapter mAdapter;
    private List<PictureData> resourcePictures;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getResourceDrawables();
        mAdapter = new StaticImagesCardAdapter(this, resourcePictures);
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(mAdapter);
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(mCardScroller);
       // setCardScrollerListener();
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
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            getMenuInflater().inflate(R.menu.menu_voice_main, menu);
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
                case R.id.itemDetect:
                    Intent intent = new Intent(this, FacesActivity.class);
                    PictureData pictureData = (PictureData) mCardScroller.getSelectedItem();
                    intent.putExtra(FacesActivity.RESOURCEID, pictureData.getResourceId());
                    intent.putExtra(FacesActivity.RESOURCENAME, pictureData.getResourceName());
                    //startActivity(intent);
                    Toast.makeText(this,"No Face Detected", Toast.LENGTH_LONG).show();

                    break;
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }



    private void setCardScrollerListener() {
        mCardScroller.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Global.LogDebug("StaticImagesActivity.setCardScrollerListener(): position: " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
}
