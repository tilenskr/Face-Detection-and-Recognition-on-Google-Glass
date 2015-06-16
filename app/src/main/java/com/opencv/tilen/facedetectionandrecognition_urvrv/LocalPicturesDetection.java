package com.opencv.tilen.facedetectionandrecognition_urvrv;

import android.content.Context;
import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

/**
 * Created by Tilen on 10.6.2015.
 */
public class LocalPicturesDetection {
    private Mat localPicture;
    private Context mContext;
    public LocalPicturesDetection(Context context,int resourceId) throws IOException {
        this.mContext = context;
       /* localPicture = new Mat();
        Bitmap bMap= BitmapFactory.decodeResource(mContext.getResources(), R.drawable.test_image_0);
        Utils.bitmapToMat(bMap, localPicture);*/

        localPicture = Utils.loadResource(context, resourceId);
        Global.TestDebug("LocalPicturesDetection.LocalPicturesDetection: localPicture " + localPicture.cols());


    }

    public Mat getlocalPicture() {
        return localPicture;
    }
    public static Bitmap matToBitmap(Mat inputPicture)
    {
        Mat convertedPicture = new Mat();
        Global.TestDebug("test : " +inputPicture.cols());
        Imgproc.cvtColor(inputPicture, convertedPicture, Imgproc.COLOR_RGB2BGRA);
        Bitmap bitmapPicture = Bitmap.createBitmap(inputPicture.cols(), inputPicture.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(convertedPicture, bitmapPicture);
        return bitmapPicture;
    }
}
