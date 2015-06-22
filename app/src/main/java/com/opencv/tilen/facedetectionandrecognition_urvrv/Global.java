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

    private static final boolean debug = true;
    private static final boolean info = true;
    private static final boolean error = true;
    private static final boolean test = true;

    public static void LogDebug(String message) {
        if (debug)
            Log.d(DEBUGTAG, message);
    }

    public static void InfoDebug(String message) {
        if (info)
            Log.i(INFOTAG, message);
    }

    public static void ErrorDebug(String message) {
        if (error)
            Log.e(ERRORTAG, message);
    }

    public static void TestDebug(String message) {
        if (test)
            Log.d(TESTTAG, message);
    }
}
