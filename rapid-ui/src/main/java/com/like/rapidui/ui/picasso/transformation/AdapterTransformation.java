package com.like.rapidui.ui.picasso.transformation;

import android.content.Context;
import android.graphics.Bitmap;

import com.like.rapidui.utils.ViewUtils;
import com.squareup.picasso.Transformation;

/**
 * Created By Like on 2018/4/23.
 */

public class AdapterTransformation implements Transformation {

    private Context context;

    public AdapterTransformation(Context context) {
        this.context = context;
    }

    @Override
    public Bitmap transform(Bitmap bitmap) {
        if (bitmap != null) {
            double ratio = bitmap.getHeight() / bitmap.getWidth();
            int screenHeight = (int) (ViewUtils.getInstance(context).getWidth() * ratio);
            if (screenHeight <= 0) return bitmap;
            Bitmap newBitMap = Bitmap.createScaledBitmap(bitmap, ViewUtils.getInstance(context).getWidth(), screenHeight, true);
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
