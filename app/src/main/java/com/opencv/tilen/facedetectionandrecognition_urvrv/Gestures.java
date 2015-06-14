package com.opencv.tilen.facedetectionandrecognition_urvrv;

import android.content.Context;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

/**
 * Created by Tilen on 11.6.2015.
 */
public class Gestures extends GestureDetector implements GestureDetector.BaseListener{
    OnGesturesCallback mCallback;


    public interface OnGesturesCallback
    {
        void onThreeTap();
    }
    public Gestures(Context context, OnGesturesCallback callback)
    {
        super(context);
        mCallback = callback;
        setBaseListener(this);
    }

    @Override
    public boolean onGesture(Gesture gesture) {
        if(gesture == Gesture.THREE_TAP)
        {
            Global.LogDebug("Gestures.onGesture(): THREE_TAP");
            mCallback.onThreeTap();
            return true;
        }
        return false;
    }
}
