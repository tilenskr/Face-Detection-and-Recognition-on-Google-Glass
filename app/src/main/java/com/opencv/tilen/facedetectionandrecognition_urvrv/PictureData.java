package com.opencv.tilen.facedetectionandrecognition_urvrv;

/**
 * Created by Tilen on 16.6.2015.
 */
public class PictureData {
    private final int resourceId;
    private final String resourceName;

    public PictureData(int resourceId, String resourceName)
    {
        this.resourceId = resourceId;
        this.resourceName = resourceName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public int getResourceId() {
        return resourceId;
    }
}
