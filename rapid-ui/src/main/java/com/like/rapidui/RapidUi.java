package com.like.rapidui;

import android.annotation.SuppressLint;
import android.content.Context;

import com.like.rapidui.callback.DataLoader;
import com.like.rapidui.network.OkHttpUtils;
import com.like.rapidui.network.callback.Callback;
import com.like.rapidui.network.https.HttpsUtils;
import com.like.rapidui.network.log.LoggerInterceptor;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;

/**
 * Created By Like on 2018/4/19.
 */

public class RapidUi {

    private static volatile RapidUi mInstance;

    private DataLoader mDataLoader;
    private Callback mDataTransfer;

    private RapidUi(Context context) {
        if (context == null) {
            throw new RuntimeException("Please init RapidUi with context at first time!");
        }
        HttpsUtils.SSLParams sslParams;
        sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5000L, TimeUnit.MILLISECONDS)
                .readTimeout(8000L, TimeUnit.MILLISECONDS)
                .writeTimeout(8000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggerInterceptor("NetWork", true))
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .hostnameVerifier(new HostnameVerifier() {
                    @SuppressLint("BadHostnameVerifier")
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    public static RapidUi getInstance(Context context) {
        if (mInstance == null) {
            synchronized (RapidUi.class) {
                if (mInstance == null) {
                    mInstance = new RapidUi(context);
                }
            }
        }
        return mInstance;
    }

    public static RapidUi getInstance() {
        return getInstance(null);
    }

    public DataLoader getDataLoader() {
        return mDataLoader;
    }

    public void setDataLoader(DataLoader dataLoader) {
        this.mDataLoader = dataLoader;
    }

    public Callback getDataTransfer() {
        return mDataTransfer;
    }

    public void setDataTransfer(Callback dataTransfer) {
        this.mDataTransfer = dataTransfer;
    }
}
