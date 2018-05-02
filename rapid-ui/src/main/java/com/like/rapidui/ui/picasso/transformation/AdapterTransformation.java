package com.like.rapidui.ui.picasso.transformation;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Created By Like on 2018/4/23.
 */

public class AdapterTransformation implements Transformation {

    private int mWidth;

    public AdapterTransformation(int width) {
        this.mWidth = width;
    }

    @Override
    public Bitmap transform(Bitmap bitmap) {
        if (bitmap != null) {
            double ratio = bitmap.getHeight() / bitmap.getWidth();
            int screenHeight = (int) (mWidth * ratio);
            if (screenHeight <= 0) return bitmap;
            Bitmap newBitMap = Bitmap.createScaledBitmap(bitmap, mWidth, screenHeight, true);
            bitmap.recycle();
            return newBitMap;
        }
        return null;
    }

    @Override
    public String key() {
        return "AdapterTransformation";
    }

}
