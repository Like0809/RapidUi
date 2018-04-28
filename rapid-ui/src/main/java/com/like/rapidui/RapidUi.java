package com.like.rapidui;

import com.like.rapidui.base.DataLoader;
import com.like.rapidui.base.DataParam;
import com.like.rapidui.base.PagingParam;
import com.like.rapidui.base.Request;
import com.like.rapidui.base.RequestListener;
import com.like.rapidui.network.OkHttpUtils;
import com.like.rapidui.network.callback.StringCallback;
import com.like.rapidui.network.https.HttpsUtils;
import com.like.rapidui.network.log.LoggerInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * Created By Like on 2018/4/19.
 */

@SuppressWarnings("ALL")
public class RapidUi {

    private static volatile RapidUi mInstance;
    private DataLoader mDataLoader;
    private PagingParam pagingParam = new PagingParam("pageSize", "pageNum");
    private DataParam dataParam = new DataParam(0, "code", "message", "data");

    private RapidUi(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            HttpsUtils.SSLParams sslParams;
            sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(2000L, TimeUnit.MILLISECONDS)
                    .readTimeout(2000L, TimeUnit.MILLISECONDS)
                    .writeTimeout(8000L, TimeUnit.MILLISECONDS)
                    .addInterceptor(new LoggerInterceptor("RapidUi", true))
                    .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                    .hostnameVerifier((hostname, session) -> true)
                    .build();
        }
        OkHttpUtils.initClient(okHttpClient);
        mDataLoader = new DataLoader() {
            @Override
            public void load(final Request request, final RequestListener requestListener) {
                OkHttpUtils.post().url(request.getUrl())
                        .params(request.getParams())
                        .headers(request.getHeaders())
                        .build()
                        .execute(new StringCallback() {

                            @Override
                            public void onError(Call call, Exception e, int id) {
                                requestListener.onError(request, -1, e.getMessage());
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                requestListener.onResponse(request, response);
                            }

                            @Override
                            public void onAfter(int id, int status) {
                            }
                        });
            }
        };
    }


    public static RapidUi getInstance(OkHttpClient okHttpClient) {
        if (mInstance == null) {
            synchronized (RapidUi.class) {
                if (mInstance == null) {
                    mInstance = new RapidUi(okHttpClient);
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


    public DataParam getDataParam() {
        return dataParam;
    }

    public void setDataParam(DataParam dataParam) {
        this.dataParam = dataParam;
    }

    public PagingParam getPagingParam() {
        return pagingParam;
    }

    public void setPagingParam(PagingParam pagingParam) {
        this.pagingParam = pagingParam;
    }

}
