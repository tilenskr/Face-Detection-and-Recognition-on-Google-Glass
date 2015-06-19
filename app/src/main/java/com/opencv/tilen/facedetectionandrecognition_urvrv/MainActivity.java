package com.opencv.tilen.facedetectionandrecognition_urvrv;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardScrollView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.IOException;
import java.util.List;

/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p/>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 *
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */
public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, Gestures.OnGesturesCallback {

    private MyJavaCameraView mOpenCvCameraView;
    private FaceDetection faceDetection;
    private ImageView ivPicture;
    private Gestures mGestureDetector;

    private LocalPicturesDetection localPictures;
    private List<Camera.Size> cameraResolutions;
    private List<int[]> cameraFpsRanges;
    private boolean isSubmenuAdded;
    private int maxIndexResolution;

    // constants for detecting zoom
    private float previousTouch1 = -1;
    private float previousTouch2 = -1;
    private float previousAbsDistance = -1;

    private Mat currentCameraImage;

    private boolean isCaptureFaceDetectionUsed = true;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Global.InfoDebug("MainActivity.mLoaderCallback.onManagerConnected:OpenCV loaded successfully");
                    faceDetection = FaceDetection.getInstance(mAppContext);
                    mOpenCvCameraView.enableView();

                   /* Mat mMat = null;
                    try {
                        mMat = Utils.loadResource(mAppContext, R.drawable.test_image_0);
                    } catch (IOException e) {
                        e.printStackTrace(); */
                }

                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        // to request voice menu on this activity
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mGestureDetector = new Gestures(this, this);
        mOpenCvCameraView = (MyJavaCameraView) findViewById(R.id.tutorial1_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        ivPicture = (ImageView) findViewById(R.id.ivPicture);
        isSubmenuAdded = false;
       /* Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.test_image_0);
        ivPicture.setImageBitmap(bitmap);*/

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if(featureId == WindowUtils.FEATURE_VOICE_COMMANDS)
        {
            switch (item.getItemId())
            {
                case R.id.itemDetect:
                    // TODO open an activity and show user images of faces
                    break;
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // This is called right before the menu is shown, every time it is shown.
            if(isSubmenuAdded == false) {
                SubMenu submenu = menu.addSubMenu(0, -1, 1, getString(R.string.resolutions));
                int index = 0;
                String textToDisplay;
                for(Camera.Size resolution : cameraResolutions)
                {
                        textToDisplay = resolution.width + " x " + resolution.height;
                        submenu.add(0, index, Menu.NONE, textToDisplay);
                        index++;
                }
                maxIndexResolution = index;
                /*
                    submenu = menu.addSubMenu(0, -2,2 , getString(R.string.fps_ranges));
                    for(int[] fpsRange : cameraFpsRanges)
                {
                    textToDisplay = fpsRange[0] + ", " + fpsRange[1];
                    submenu.add(0, index, Menu.NONE, textToDisplay);
                    index++;
                }
                */
                isSubmenuAdded = true;
            }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection. Menu items typically start another
        // activity, start a service, or broadcast another intent.
        int itemId = item.getItemId();
        Global.LogDebug("MainActivity.onOptionsItemSelected(): item name: " + item.getTitle() + " item id: " + itemId);
        switch (itemId) {
            case R.id.itemImageManipulation:
                Global.LogDebug("MainActivity.onOptionsItemSelected(): R.id.itemImage");
                Intent intent = new Intent(this,StaticImagesActivity.class);
                startActivity(intent);
                return true;
            case R.id.itemSubmenuToggleFaceDetection:
                return true;
            case R.id.itemToggleFDOn:
                isCaptureFaceDetectionUsed = true;
                return true;
            case R.id.itemToggleFDOff:
                isCaptureFaceDetectionUsed = false;
                return true;
            default:
                if(itemId < 0) // submenu
                {
                    return super.onOptionsItemSelected(item);
                }
                else if(itemId < maxIndexResolution) // submenu items resolutions
                {
                    // we need to enable CameraView (and show it) hide ImageView to use Camera
                    showCameraView();
                    mOpenCvCameraView.setResolution(cameraResolutions.get(itemId));
                    return true;
                }
                else // submenu items fpsRanges
                {
                    itemId -= maxIndexResolution;
                    showCameraView();
                    mOpenCvCameraView.setFpsRange(cameraFpsRanges.get(itemId));
                    return true;
                }
        }
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) { // called when you select an Item Menu or just cancel Menu
        Global.LogDebug("MainActivity.onOptionsMenuClosed");
        //TODO check if this is proper
         showCameraView();
        super.onOptionsMenuClosed(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Global.InfoDebug("MainActivity.onResume()");
        // mCardScroller.activate();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mCardScroller.deactivate();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) { // needs  Bitmap type: 640*360 (so the same size as camera)
        // if isCaptureFaceDetectionUsed == true we use last image, where faces were detected, else we use last image from camera
        if(isCaptureFaceDetectionUsed == true)
        {
            // it is the same performance with grey and color Image
            Mat outputFDPicture = faceDetection.getFaceDetectionPicture(inputFrame.rgba());
            if(faceDetection.getNumberOfFacesInCurrentImage() != 0) {
                currentCameraImage = outputFDPicture;
            }
            return outputFDPicture;
        }
        else
        {
            Global.TestDebug("isCaptureFaceDetectionUsed == false");
            currentCameraImage = inputFrame.rgba();
            return currentCameraImage;
        }

    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        boolean isGestureHandled = false;
        if (mGestureDetector != null) {
            isGestureHandled = mGestureDetector.onMotionEvent(event);
        }
        if(isGestureHandled == false)
        {
            if(event.getActionMasked() == MotionEvent.ACTION_MOVE)
            {
                Global.LogDebug("MainActivit.onGenericMotionEvent(): Action Move");
                return processMotionEventsZooming(event);
            }
            else
                return false;
        }
        else
            return false;
    }

    @Override
    public void onThreeTap() {
        // mOpenCvCameraView needs to be enabled and we can not do this in onManagerConnected (mCamera is null)
        if(cameraResolutions == null) {
            cameraResolutions = mOpenCvCameraView.getResolutionList();
            removeUnWantedResolutions();
        }
        if(cameraFpsRanges == null)
        {
            cameraFpsRanges = mOpenCvCameraView.getPreviewFpsRangeList();
        }
        // beacuse of lag, we disable when navigation on Menu
        mOpenCvCameraView.disableView();
        openOptionsMenu();
    }

    private void manipulateStaticImage() {
        mOpenCvCameraView.setVisibility(View.GONE);
        try {
            localPictures = new LocalPicturesDetection(this, R.drawable.test_image_0);
        } catch (IOException e) {
            Global.ErrorDebug("MainActivity.manipulateStaticImage(): " + e.toString());
        }
        //TODO change
        Mat outputPicture = faceDetection.getFaceDetectionPicture(localPictures.getlocalPicture());
        Global.TestDebug("manipulateStaticImage():" + outputPicture.cols());
        Bitmap bitmap = LocalPicturesDetection.matToBitmap(outputPicture);
        ivPicture.setVisibility(View.VISIBLE);
        ivPicture.setImageBitmap(bitmap);

    }

    //  /* Select the size that fits surface considering maximum size allowed */ - from OpenCV
    // so we ignore size which are higher than screen size (in our case 640 x 360)
    private void removeUnWantedResolutions()
    {
        // screen dimensions
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;
        int screenWidth = displaymetrics.widthPixels;
        for(int i = 0; i < cameraResolutions.size();)
        {
            Camera.Size resolution = cameraResolutions.get(i);
            if(resolution.width <= screenWidth && resolution.height <= screenHeight) {
                i++;
            }
            else
                cameraResolutions.remove(resolution);
        }
    }

    private void showCameraView()
    {
        ivPicture.setVisibility(View.GONE);
        mOpenCvCameraView.setVisibility(View.VISIBLE);
        mOpenCvCameraView.enableView();
    }

    private Boolean processMotionEventsZooming(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        if (pointerCount == 2)
        {
            float x1 = event.getX(0);
            float x2 = event.getX(1);
            if (previousTouch1 != -1 || previousTouch2 != -1) {
                float differenceDistance =  Math.abs(x1-x2) - previousAbsDistance;
                Global.TestDebug("MainActity.processMotionEventsZooming(): Difference beetween distances: " + differenceDistance);
                if(differenceDistance > 10) // zooming in; threshold - ignore minor moves
                {
                    mOpenCvCameraView.setZoom(true);
                }
                else if(differenceDistance < -10 ) // zooming out; threshold - ignore minor moves
                {
                    mOpenCvCameraView.setZoom(false);
                }
            }
            previousTouch1 = x1;
            previousTouch2 = x2;
            previousAbsDistance = Math.abs(x1-x2);
            return true;
        }
        else
            return false;
    }
}
