package com.like.rapidui.network.callback;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

@SuppressWarnings("unused")
public abstract class Callback<T> {

    public void onBefore(Request request, int id) {
    }

    public abstract void onAfter(int id, int status);


    public void inProgress(float progress, long total, int id) {
    }

    public boolean validateResponse(Response response, int id) {
        return response.isSuccessful();
    }

    public abstract T parseNetworkResponse(Response response, int id) throws Exception;

    public abstract void onError(Call call, Exception e, int id);

    public abstract void onResponse(T response, int id);

    public static Callback CALLBACK_DEFAULT = new Callback() {

        @Override
        public void onAfter(int id, int status) {

        }

        @Override
        public Object parseNetworkResponse(Response response, int id) throws Exception {
            return null;
        }

        @Override
        public void onResponse(Object response, int id) {

        }

        @Override
        public void onError(Call call, Exception e, int id) {

        }
    };

}