package com.like.rapidui.network.callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import okhttp3.Response;

/**
 * Created By Like on 15/12/14.
 */
@SuppressWarnings("unused")
public abstract class BitmapCallback extends Callback<Bitmap> {
    @Override
    public Bitmap parseNetworkResponse(Response response, int id) throws Exception {
        return BitmapFactory.decodeStream(response.body().byteStream());
    }

}
