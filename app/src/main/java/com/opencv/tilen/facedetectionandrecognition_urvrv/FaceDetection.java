package com.opencv.tilen.facedetectionandrecognition_urvrv;

import android.content.Context;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Tilen on 10.6.2015.
 */
public class FaceDetection {

    private final int  frontalFaceId = R.raw.lbpcascade_frontalface;
    private Context mContext;
    private String frontalFaceClassifier;
    private String frontalFaceClassifierFilename;

    public FaceDetection(Context context) {
        mContext = context;
        loadClassifierString(mContext, frontalFaceId);
    }

    private void loadClassifierString(Context context, int resourceId)
    {
        StringBuilder stringBuilder  = new StringBuilder();
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);

            BufferedReader input =  new BufferedReader(new InputStreamReader(inputStream), 1024*8);
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
            Global.ErrorDebug("FaceDetection.loadClassifierString(): Couldn't find the file " +
                    + resourceId + " " + ex);
            return;
        }
        catch (IOException ex){
            Global.ErrorDebug("FaceDetection.loadClassifierString(): Error reading file " +
                    + resourceId + " " + ex);
            return;
        }
        frontalFaceClassifier = stringBuilder.toString();
        Global.InfoDebug("FaceDetection.loadClassifierString(): frontalFaceClassifier text: " + frontalFaceClassifier);
    }

    public Mat getFaceDetectionPicture(Mat inputPicture)
    {
        //inputPicture = localPictures.getlocalPicture();
        //Global.TestDebug("FaceDetection.getFaceDetectionPicture: inputPicture " + inputPicture.cols());

        CascadeClassifier faceDetector = new CascadeClassifier();
        faceDetector.load(frontalFaceClassifier);
        if(faceDetector.empty() == true)
            Global.ErrorDebug("FaceDetection.getFaceDetectionPicture(): Classifier has not been loaded.");
        // MatOfRect is a special container class for Rect. Probably such as vector in c++ (MatOf...)
        MatOfRect faceDetectionRectangles = new MatOfRect();
        faceDetector.detectMultiScale(inputPicture, faceDetectionRectangles);
        Rect[] rectangles = faceDetectionRectangles.toArray();
        Global.LogDebug("FaceDetection.getFaceDetectionPicture() Number of faces: " + rectangles.length);

        return inputPicture;
    }
}
