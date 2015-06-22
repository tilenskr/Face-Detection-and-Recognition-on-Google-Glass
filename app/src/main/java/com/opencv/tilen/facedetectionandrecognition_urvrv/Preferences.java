package com.opencv.tilen.facedetectionandrecognition_urvrv;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Tilen on 22.6.2015.
 */
public class Preferences {
    private final static String STATE_ZOOM = "zoomLevel";

    public static int onResume(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        int zoom = sharedPref.getInt(STATE_ZOOM, 0);
        return zoom;
    }
    public static void onPause(Activity activity, int zoom)
    {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(STATE_ZOOM, zoom);
        editor.commit();
    }

    public static void onDestroy(Activity activity)
    {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(STATE_ZOOM, 0);
        editor.commit();
    }
}
