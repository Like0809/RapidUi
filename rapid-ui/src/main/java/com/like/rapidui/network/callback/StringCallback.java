package com.like.rapidui.network.callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created By Like on 15/12/14.
 */
@SuppressWarnings("unused")
public abstract class StringCallback extends Callback<String> {
    @Override
    public String parseNetworkResponse(Response response, int id) throws IOException {
        return response.body().string();
    }
}
