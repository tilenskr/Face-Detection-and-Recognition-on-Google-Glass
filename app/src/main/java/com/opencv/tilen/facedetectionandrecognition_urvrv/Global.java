package com.opencv.tilen.facedetectionandrecognition_urvrv;

import android.util.Log;

/**
 * Created by Tilen on 4.6.2015.
 */
public class Global {
    private static final String DEBUGTAG = "OCVSeminarska.debug";
    private static final String INFOTAG = "OCVSeminarska.info";
    private static final String ERRORTAG = "OCVSeminarska.error";
    private static final String TESTTAG = "OCVSeminarska.test";
    public static void LogDebug(String message)
    {
        Log.d(DEBUGTAG, message);
    }
    public static void InfoDebug(String message)
    {
        Log.i(INFOTAG, message);
    }
    public static void ErrorDebug(String message) {Log.e(ERRORTAG,message);}
    public static void TestDebug(String message) {Log.d(TESTTAG, message);}
}
