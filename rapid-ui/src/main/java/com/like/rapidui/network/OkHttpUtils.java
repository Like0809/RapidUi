package com.like.rapidui.network;


import android.util.Log;

import com.like.rapidui.network.builder.GetBuilder;
import com.like.rapidui.network.builder.HeadBuilder;
import com.like.rapidui.network.builder.OtherRequestBuilder;
import com.like.rapidui.network.builder.PostFileBuilder;
import com.like.rapidui.network.builder.PostFormBuilder;
import com.like.rapidui.network.builder.PostStringBuilder;
import com.like.rapidui.network.callback.Callback;
import com.like.rapidui.network.request.RequestCall;
import com.like.rapidui.network.utils.Platform;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

@SuppressWarnings("unused")
public class OkHttpUtils {
    public static final long DEFAULT_MILLISECONDS = 10_000L;
    private volatile static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private Platform mPlatform;

    private OkHttpUtils(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
        } else {
            mOkHttpClient = okHttpClient;
        }

        mPlatform = Platform.get();
    }


    public static OkHttpUtils initClient(OkHttpClient okHttpClient) {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils(okHttpClient);
                }
            }
        }
        return mInstance;
    }

    public static OkHttpUtils getInstance() {
        return initClient(null);
    }


    public Executor getDelivery() {
        return mPlatform.defaultCallbackExecutor();
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public static GetBuilder get() {
        return new GetBuilder();
    }

    public static PostStringBuilder postString() {
        return new PostStringBuilder();
    }

    public static PostFileBuilder postFile() {
        return new PostFileBuilder();
    }

    public static PostFormBuilder post() {
        return new PostFormBuilder();
    }

    public static OtherRequestBuilder put() {
        return new OtherRequestBuilder(METHOD.PUT);
    }

    public static HeadBuilder head() {
        return new HeadBuilder();
    }

    public static OtherRequestBuilder delete() {
        return new OtherRequestBuilder(METHOD.DELETE);
    }

    public static OtherRequestBuilder patch() {
        return new OtherRequestBuilder(METHOD.PATCH);
    }


    public void execute(final RequestCall requestCall, Callback callback) {
        if (callback == null)
            callback = Callback.CALLBACK_DEFAULT;
        final Callback finalCallback = callback;
        final int id = requestCall.getOkHttpRequest().getId();

        requestCall.getCall().enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (e.getMessage() != null && e.getMessage().toLowerCase().contains("permission")) {
                    sendFailResultCallback(call, new IOException("获取文件权限失败，请在手机设置-权限管理中设置允许"), finalCallback, id);
                } else
                    sendFailResultCallback(call, new IOException("网络异常"), finalCallback, id);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onResponse(final Call call, final Response response) {
                if (call.isCanceled()) {
                    sendFailResultCallback(call, new IOException("请求退出"), finalCallback, id);
                    return;
                }
                if (response.isSuccessful()) {
                    Object o = null;
                    try {
                        o = finalCallback.parseNetworkResponse(response, id);
                        sendSuccessResultCallback(o, finalCallback, id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        sendFailResultCallback(call, new Exception("数据解析出错"), finalCallback, id);
                    } finally {
                        if (response.body() != null) {
                            response.body().close();
                        }
                    }
                } else {
                    Log.e("log", String.format("网络连接失败{code = %d , message = %s }", response.code(), response.message()));
                    sendFailResultCallback(call, new RuntimeException(response.message()), finalCallback, id);
                }
            }
        });
    }


    public void sendFailResultCallback(final Call call, final Exception e, final Callback callback, final int id) {
        if (callback == null) return;

        mPlatform.execute(new Runnable() {
            @Override
            public void run() {
                callback.onError(call, e, id);
                callback.onAfter(id, 1);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void sendSuccessResultCallback(final Object object, final Callback callback, final int id) {
        if (callback == null) return;
        mPlatform.execute(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(object, id);
                callback.onAfter(id, 0);
            }
        });
    }

    public void cancelTag(Object tag) {
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    public static class METHOD {
        public static final String HEAD = "HEAD";
        public static final String DELETE = "DELETE";
        public static final String PUT = "PUT";
        public static final String PATCH = "PATCH";
    }
}

