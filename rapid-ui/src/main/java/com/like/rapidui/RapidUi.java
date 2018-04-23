package com.like.rapidui;

import android.annotation.SuppressLint;
import android.content.Context;

import com.google.gson.Gson;
import com.like.rapidui.callback.DataLoader;
import com.like.rapidui.network.ApiMessage;
import com.like.rapidui.network.OkHttpUtils;
import com.like.rapidui.network.callback.Callback;
import com.like.rapidui.network.callback.StringCallback;
import com.like.rapidui.network.https.HttpsUtils;
import com.like.rapidui.network.log.LoggerInterceptor;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.OkHttpClient;

import static com.like.rapidui.RequestListener.FAILED;
import static com.like.rapidui.RequestListener.SUCCESS;

/**
 * Created By Like on 2018/4/19.
 */

public class RapidUi {

    private static volatile RapidUi mInstance;

    private DataLoader mDataLoader;
    private Callback mDataTransfer;
    private PagingParam pagingParam = new PagingParam("pageSize", "pageNum");


    private RapidUi(Context context) {
//        if (context == null) {
//            throw new RuntimeException("Please init RapidUi with context at first time!");
//        }
        HttpsUtils.SSLParams sslParams;
        sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(3000L, TimeUnit.MILLISECONDS)
                .readTimeout(5000L, TimeUnit.MILLISECONDS)
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
        mDataLoader = new DataLoader() {
            @Override
            public void load(final Request request, final RequestListener requestListener) {
                Callback callback = RapidUi.getInstance().getDataTransfer();
                if (callback == null) {
                    callback = new StringCallback() {

                        @Override
                        public void onError(Call call, Exception e, int id) {
                            requestListener.onError(RequestStatus.NET_ERR, request, 0, e.getMessage());
                            requestListener.onComplete(request, FAILED);
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            if (ApiMessage.isApiMessage(response)) {
                                ApiMessage message = new Gson().fromJson(response, ApiMessage.class);
                                requestListener.onError(RequestStatus.API_ERR, request, message.getCode(), message.getMessage());
                                requestListener.onComplete(request, FAILED);
                            } else {
                                requestListener.onResponse(request, response);
                                requestListener.onComplete(request, SUCCESS);
                            }
                        }

                        @Override
                        public void onAfter(int id, int status) {
                        }
                    };
                }
                OkHttpUtils.post().url(request.getUrl())
                        .params(request.getParams())
                        .headers(request.getHeaders())
                        .build()
                        .execute(callback);
            }
        };
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

    public PagingParam getPagingParam() {
        return pagingParam;
    }

    public void setPagingParam(PagingParam pagingParam) {
        this.pagingParam = pagingParam;
    }
}
