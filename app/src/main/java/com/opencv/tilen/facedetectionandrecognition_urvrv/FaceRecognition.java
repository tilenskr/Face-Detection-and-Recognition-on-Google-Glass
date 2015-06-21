package com.opencv.tilen.facedetectionandrecognition_urvrv;

import android.content.Context;
import android.content.SharedPreferences;

import org.opencv.contrib.FaceRecognizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tilen on 21.6.2015.
 */
public class FaceRecognition {
    private Context mContext;
    private SharedPreferences sharedPref;
    private final String SHARE_PREF_NAME = "FaceRecognitionPreferences";
    private final String KEY_PREF_NAME = "name";
    private final String KEY_PREF_COUNT_NAME = "count_name";
    private final String FACERECOGNIZER_XML = "face_recognizer.xml";
    private FaceRecognizer faceRecognizer;

    private static FaceRecognition faceRecognition = null; //singleton instance

    public static FaceRecognition getInstance(Context context)
    {
        //singleton
        if(faceRecognition == null)
        {
            faceRecognition = new FaceRecognition(context);
        }
        faceRecognition.printAllNames();// for informational purposes
        return faceRecognition;
    }

    private FaceRecognition(Context mContext)
    {
        // TODO complete this constructor
        this.mContext = mContext;

        // get sharedPreferences
        sharedPref = mContext.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
    }

    private void setUpFaceRecognizer()
    {
        File file = new File(mContext.getFilesDir(), FACERECOGNIZER_XML);
        if(file.exists())
        {

        }
        else
        {
            // create new instance of FaceRecognizer
            //faceRecognizer = F
        }
    }

    private int getLabel(String name)
    {
        int countNames = sharedPref.getInt(KEY_PREF_COUNT_NAME, 0);
        String tempName;
        int label = -1;
        for(int i = 0; i < countNames; i++)
        {
            tempName = sharedPref.getString(KEY_PREF_NAME + "i",""); //convention
            if(tempName.equals(name)) // if we found label
            {
                label = i;
                return label; // name exists in shared preferences
            }
        }
        // name does not exists in shared preferences
        //opencv_core.Mat mat = new opencv_core.Mat()
        return -1;
    }

    private void printAllNames() // for informational purposes
    {
        int countNames = sharedPref.getInt(KEY_PREF_COUNT_NAME, 0);
        List<String> allNames = new ArrayList<>();
        String tempName;
        for(int i = 0; i < countNames; i++)
        {
            tempName = sharedPref.getString(KEY_PREF_NAME,"");
            allNames.add(tempName);
        }
        Global.InfoDebug("FaceRecognition.printAllNames(): All names: " + Arrays.toString(allNames.toArray()));
    }
}
