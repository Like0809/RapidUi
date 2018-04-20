package com.like.rapidui.network.builder;


import com.like.rapidui.network.OkHttpUtils;
import com.like.rapidui.network.request.OtherRequest;
import com.like.rapidui.network.request.RequestCall;

/**
 * Created By Like on 16/3/2.
 */
public class HeadBuilder extends GetBuilder {
    @Override
    public RequestCall build() {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers, id).build();
    }
}
