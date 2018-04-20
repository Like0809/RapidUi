package com.like.rapidui.network.builder;

import com.like.rapidui.network.request.PostStringRequest;
import com.like.rapidui.network.request.RequestCall;

import okhttp3.MediaType;

/**
 * Created By Like on 15/12/14.
 */
@SuppressWarnings("unused")
public class PostStringBuilder extends OkHttpRequestBuilder<PostStringBuilder> {
    private String content;
    private MediaType mediaType;


    public PostStringBuilder content(String content) {
        this.content = content;
        return this;
    }

    public PostStringBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public RequestCall build() {
        return new PostStringRequest(url, tag, params, headers, content, mediaType, id).build();
    }


}
