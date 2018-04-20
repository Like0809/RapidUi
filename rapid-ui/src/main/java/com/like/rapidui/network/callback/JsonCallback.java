package com.like.rapidui.network.callback;


import com.google.gson.Gson;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * Created By Like on 2016/8/11.
 */
@SuppressWarnings("ALL")
public abstract class JsonCallback<T> extends Callback<T> {


    private Type mType;

    public JsonCallback() {
        mType = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        if (mType == String.class)
            return (T) string;
        else return new Gson().fromJson(string, mType);
    }

}
