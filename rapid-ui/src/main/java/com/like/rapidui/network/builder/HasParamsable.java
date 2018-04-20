package com.like.rapidui.network.builder;

import java.util.Map;

/**
 * Created By Like on 16/3/1.
 */
public interface HasParamsable {
    OkHttpRequestBuilder params(Map<String, String> params);

    OkHttpRequestBuilder addParams(String key, String val);
}
