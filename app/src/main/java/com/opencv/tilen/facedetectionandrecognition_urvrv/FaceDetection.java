package com.opencv.tilen.facedetectionandrecognition_urvrv;

import android.content.Context;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.objdetect.CascadeClassifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    private CascadeClassifier faceDetectorCascadeClassifier;
    private static FaceDetection faceDetector = null;


    public static FaceDetection getInstance(Context context)
    {
        //singleton
        if(faceDetector == null)
        {
            faceDetector = new FaceDetection(context);
        }
        return faceDetector;
    }

    private FaceDetection(Context context) {
        mContext = context;
        getClassifierFilename();
        loadClassifierString(frontalFaceId);
        setUpCascadeClassifier();
    }

    private void loadClassifierString(int resourceId)
    {
        StringBuilder stringBuilder  = new StringBuilder();
        try {
            InputStream inputStream = mContext.getResources().openRawResource(resourceId);

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

    // we need to copy it from resources to temporary directory
    private void getClassifierFilename()
    {
        try {
            InputStream inputStream = mContext.getResources().openRawResource(R.raw.lbpcascade_frontalface);
           // File cascadeDirectory = mContext.getDir("cascade", Context.MODE_PRIVATE);
            //File cascadeFile = new File(cascadeDirectory, "lbpcascade_frontalface.xml");
            File cascadeFile = File.createTempFile("lbpcascade_frontalface", ".xml");
            FileOutputStream outputStream = new FileOutputStream(cascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();

            frontalFaceClassifierFilename = cascadeFile.getAbsolutePath();
            Global.LogDebug("FaceDetection.getClassifierName(): Path of file: " + frontalFaceClassifierFilename);
        }
        catch(Exception e)
        {
            Global.ErrorDebug("FaceDetection.getClassifierName(): Error loading file from raw to temporary location: " + e.toString());
        }


        /* doesn't work
        String path = "android.resource://"+ mContext.getPackageName() + "/raw/lbpcascade_frontalface.xml";// + R.raw.lbpcascade_frontalface;
        Uri uri = Uri.parse(path);
        frontalFaceClassifierFilename = uri.toString();
        */
    }

    private void setUpCascadeClassifier()
    {
        faceDetectorCascadeClassifier = new CascadeClassifier(frontalFaceClassifierFilename);
        if(faceDetectorCascadeClassifier.empty() == true)
            Global.ErrorDebug("FaceDetection.getFaceDetectionPicture(): Classifier has not been loaded. ClassifierFilePath: " + frontalFaceClassifierFilename);
    }
    public Mat getFaceDetectionPicture(Mat inputPicture)
    {
        //inputPicture = localPictures.getlocalPicture();
        //Global.TestDebug("FaceDetection.getFaceDetectionPicture: inputPicture " + inputPicture.cols());

        // MatOfRect is a special container class for Rect. Probably such as vector in c++ (MatOf...)
        MatOfRect faceDetectionRectangles = new MatOfRect();
        faceDetectorCascadeClassifier.detectMultiScale(inputPicture, faceDetectionRectangles);
        Rect[] rectangles = faceDetectionRectangles.toArray();
        Global.LogDebug("FaceDetection.getFaceDetectionPicture() Number of faces: " + rectangles.length);
        for(Rect rect : rectangles)
        {
            Core.rectangle(inputPicture, rect.tl(), rect.br(), new Scalar(154,250,0));
        }
        return inputPicture;
    }
}
