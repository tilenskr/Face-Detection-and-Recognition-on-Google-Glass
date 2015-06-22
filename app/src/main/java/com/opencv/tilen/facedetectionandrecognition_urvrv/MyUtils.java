package com.opencv.tilen.facedetectionandrecognition_urvrv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgproc;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Tilen on 10.6.2015.
 */
public class MyUtils {
    public static Bitmap matToBitmap(Mat inputPicture)
    {
        Mat convertedPicture = new Mat();
        Global.TestDebug("test : " +inputPicture.cols());
        Imgproc.cvtColor(inputPicture, convertedPicture, Imgproc.COLOR_RGB2BGRA);
        Bitmap bitmapPicture = Bitmap.createBitmap(inputPicture.cols(), inputPicture.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(convertedPicture, bitmapPicture);
        return bitmapPicture;
    }

    public static Mat bitmapToMat(Bitmap inputPicture)
    {
        Mat matPicture = new Mat();
        Utils.bitmapToMat(inputPicture, matPicture);
        Imgproc.cvtColor(matPicture, matPicture, Imgproc.COLOR_RGB2BGRA);
        return matPicture;
    }

    public static void saveBitmaps(Mat[] faceImages, Context mContext)
    {
        File cacheDir = mContext.getCacheDir();
        File file;
        FileOutputStream out;
        Bitmap bitmapPicture;
        for(int i = 0; i < faceImages.length;i++) {
            file = new File(cacheDir, "faceImage" + i);
            bitmapPicture = MyUtils.matToBitmap(faceImages[i]);
            try {
                out = new FileOutputStream(file);
                bitmapPicture.compress(
                        Bitmap.CompressFormat.PNG,
                        100, out);
                out.flush();
                out.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap[] loadBitmaps(int numberOfImages, Context mContext)
    {
        File cacheDir = mContext.getCacheDir();
        File file;
        FileInputStream fis;
        Bitmap[] faceImages = new Bitmap[numberOfImages];
        for(int i= 0; i < numberOfImages; i++) {
            file = new File(cacheDir, "faceImage" + i);
            fis = null;
            try {
                fis = new FileInputStream(file);
                faceImages[i] = BitmapFactory.decodeStream(fis);
                file.delete();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return faceImages;
    }

    //JavaCV library - testing purpose

    public static Bitmap IplImageToBitmap(opencv_core.IplImage source) {
        opencv_core.IplImage container = opencv_core.IplImage.create(source.width(), source.height(), opencv_core.IPL_DEPTH_8U, 4);
        opencv_imgproc.cvCvtColor(source, container, opencv_imgproc.CV_BGR2RGBA);
        Bitmap bitmap = Bitmap.createBitmap(source.width(), source.height(), Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(container.createBuffer());
        return bitmap;
    }

    public static opencv_core.IplImage BitmapToIplImage(Bitmap source) {
        opencv_core.IplImage container = opencv_core.IplImage.create(source.getWidth(), source.getHeight(), opencv_core.IPL_DEPTH_8U, 4);

        source.copyPixelsToBuffer(container.createBuffer());
        opencv_imgproc.cvCvtColor(container, container, opencv_imgproc.CV_BGR2RGBA); // works now for javacv library
        return container;
    }
}
