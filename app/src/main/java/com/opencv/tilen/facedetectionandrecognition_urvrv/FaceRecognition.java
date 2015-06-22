package com.opencv.tilen.facedetectionandrecognition_urvrv;

import android.content.Context;
import android.content.SharedPreferences;

import org.bytedeco.javacpp.opencv_contrib;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgproc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.IntBuffer;
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

    private String faceRecognizerFileName;
    private String faceRecognizerFileString;



    //JavaCV API
    private opencv_contrib.FaceRecognizer faceRecognizer;

    private static FaceRecognition faceRecognition = null; //singleton instance

    public static FaceRecognition getInstance(Context context)
    {
        //singleton
        if(faceRecognition == null)
        {
            faceRecognition = new FaceRecognition(context);
        }
        else
        {
            faceRecognition.loadFaceRecognizerXmlString();
        }
        faceRecognition.printAllNames();// for informational purposes
        return faceRecognition;
    }

    private FaceRecognition(Context mContext)
    {
        this.mContext = mContext;
        setUpFaceRecognizer();
        loadFaceRecognizerXmlString();
        // get sharedPreferences
        sharedPref = mContext.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
    }

    private void setUpFaceRecognizer()
    {
        File file = new File(mContext.getFilesDir(), FACERECOGNIZER_XML);
        faceRecognizerFileName = file.getAbsolutePath();
        Global.LogDebug("FaceRecognition.setUpFaceRecognizer(): Path of file: " + faceRecognizerFileName);
        faceRecognizer = opencv_contrib.createLBPHFaceRecognizer();
        if(file.exists())
        {
            faceRecognizer.load(faceRecognizerFileName);
        }
        else
        {
            // create new instance of FaceRecognizer
            saveFaceRecognizer();
        }
    }

    private void saveFaceRecognizer()
    {
        faceRecognizer.save(faceRecognizerFileName);
    }

    private void loadFaceRecognizerXmlString()
    {
        StringBuilder stringBuilder  = new StringBuilder();
        File file = new File(mContext.getFilesDir(), FACERECOGNIZER_XML);
        try {
            FileInputStream inputFileStream = new FileInputStream(file);
            BufferedReader input =  new BufferedReader(new InputStreamReader(inputFileStream), 1024*8);
            try {
                String line = null;
                while (( line = input.readLine()) != null){
                    stringBuilder.append(line);
                    stringBuilder.append("\n");
                }
            }
            finally {
                input.close();
            }
        }
        catch (FileNotFoundException ex) {
            Global.ErrorDebug("FaceRecognition.faceRecognizerXmlString(): Couldn't find the file "
                    + file.getName() + " " + ex);
            return;
        }
        catch (IOException ex){
            Global.ErrorDebug("FaceRecognition.faceRecognizerXmlString(): Error reading file "
                    + file.getName() + " " + ex);
            return;
        }
        faceRecognizerFileString = stringBuilder.toString();
        Global.InfoDebug("FaceRecognition.faceRecognizerXmlString(): faceRecognizerFileString text: " + faceRecognizerFileString);
    }

    public void train(opencv_core.IplImage image, String name)
    {
        opencv_core.IplImage grayImage = opencv_core.IplImage.create(image.width(), image.height(), opencv_core.IPL_DEPTH_8U, 1);
        // weird code
        opencv_imgproc.cvCvtColor(image, grayImage, opencv_imgproc.CV_RGB2GRAY); // works now for javacv library
        opencv_core.Mat matImage = new opencv_core.Mat(grayImage);
        opencv_core.MatVector images = new opencv_core.MatVector(1); // need to pass number of images
        images.put(0,matImage);

        int label = getLabel(name);

        opencv_core.Mat trainLabels =  new opencv_core.Mat(1,1, opencv_core.CV_32SC1); //CvMat.create(keys.size() * NUM_IMAGES_PER_PERSON, 1, CV_32SC1);
        IntBuffer labelsBuf =  trainLabels.getIntBuffer();//trainLabels.createBuffer(opencv_core.IPL_DEPTH_32S);
        Global.TestDebug("ješločez");
        labelsBuf.put(0,label);
        Global.TestDebug("ješločez1");

        faceRecognizer.update(images, trainLabels);
        saveFaceRecognizer();
        Global.TestDebug("ješločez2");

    }


    private int getLabel(String name)
    {
        int countNames = sharedPref.getInt(KEY_PREF_COUNT_NAME, 0);
        String tempName;
        int label = -1;
        for(int i = 0; i < countNames; i++)
        {
            tempName = sharedPref.getString(KEY_PREF_NAME + i,""); //convention
            if(tempName.equals(name)) // if we found label
            {
                label = i;
                return label; // name exists in shared preferences
            }
        }
        // name does not exists in shared preferences
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_PREF_NAME + countNames, name);
        editor.putInt(KEY_PREF_COUNT_NAME, (countNames+1));
        editor.commit();
        return countNames;
    }

    public String predict(opencv_core.IplImage image) {
        opencv_core.IplImage grayImage = opencv_core.IplImage.create(image.width(), image.height(), opencv_core.IPL_DEPTH_8U, 1);
        opencv_imgproc.cvCvtColor(image, grayImage, opencv_imgproc.CV_RGB2GRAY); // works now for javacv library
        opencv_core.Mat matImage = new opencv_core.Mat(grayImage);
        int countNames = sharedPref.getInt(KEY_PREF_COUNT_NAME, 0);
        if (countNames != 0) {
            int predictLabel = faceRecognizer.predict(matImage);
            Global.LogDebug("FaceRecognition.predict(): predictLabel:" + predictLabel);
            String name = getNameFromLabel(predictLabel);
            return name;
        }
        else
            return "-1";
    }

    private String getNameFromLabel(int label)
    {
        String name = sharedPref.getString(KEY_PREF_NAME + label,"Get Name From Label Method in Face Recognition is currently not working.");
        return name;
    }

    public List<String> printAllNames() // for informational purposes
    {
        int countNames = sharedPref.getInt(KEY_PREF_COUNT_NAME, 0);
        List<String> allNames = new ArrayList<>();
        String tempName;
        for(int i = 0; i < countNames; i++)
        {
            tempName = sharedPref.getString(KEY_PREF_NAME+i,"");
            allNames.add(tempName);
        }
        Global.InfoDebug("FaceRecognition.printAllNames(): All names: " + Arrays.toString(allNames.toArray()));
        return allNames;
    }

    public void clearDatabase() // delete saved names and clear saved xml file
    {
        File file = new File(mContext.getFilesDir(), FACERECOGNIZER_XML);
        if(file.exists())
        {
            // clear xml file
            file.delete();
            faceRecognizer = opencv_contrib.createLBPHFaceRecognizer();
            saveFaceRecognizer();
            // delete saved names
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.commit();
        }
        else
        {
            return;
        }
    }
}
