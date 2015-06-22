package com.opencv.tilen.facedetectionandrecognition_urvrv;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;

import java.util.List;

/**
 * Created by Tilen on 15.6.2015.
 */
public class StaticImagesCardAdapter extends CardScrollAdapter {
    private final List<PictureData> mData;
    private final Context mContext;

    public StaticImagesCardAdapter(Context mContext, List<PictureData> mData)
    {
        this.mData = mData;
        this.mContext = mContext;
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PictureData pictureData = mData.get(position);
        CardBuilder card = new CardBuilder(mContext, CardBuilder.Layout.EMBED_INSIDE)
                .setEmbeddedLayout(R.layout.activity_static_images)
                .setFootnote(pictureData.getResourceName());
        View view = card.getView(convertView, parent);
        ImageView ivPicture = (ImageView) view.findViewById(R.id.ivPicture);
        ivPicture.setImageResource(pictureData.getResourceId());
        // test converting from Bitmap to Mat and inversely
       /*Bitmap testBitmap = BitmapFactory.decodeResource(mContext.getResources(),
               pictureData.getResourceId());
        Mat testMat = MyUtils.bitmapToMat(testBitmap);
        Bitmap testOutputBitmap = MyUtils.matToBitmap(testMat);
        ivPicture.setImageBitmap(testOutputBitmap);
        */
        return view;
    }

    @Override
    public int getPosition(Object item) {
        for (int i = 0; i <  mData.size(); i++) {
            if (getItem(i).equals(item)) {
                return i;
            }
        }
        return AdapterView.INVALID_POSITION;

    }
}
