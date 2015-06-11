package com.opencv.tilen.facedetectionandrecognition_urvrv;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;

import org.opencv.android.JavaCameraView;

import java.util.List;

/**
 * Created by Tilen on 11.6.2015.
 */
public class MyJavaCameraView extends JavaCameraView  {
    public MyJavaCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setResolution(Camera.Size resolution) {
        disconnectCamera();
        mMaxHeight = resolution.height;
        mMaxWidth = resolution.width;
        connectCamera(getWidth(), getHeight());
    }

    public List<Camera.Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }


}
