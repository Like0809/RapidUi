package com.like.rapidui;

/**
 * Created By Like on 2018/4/19.
 */

public interface RequestListener {

    int SUCCESS = 0;

    int FAILED = 1;

    /**
     * @param json List<实体>对应的json
     */
    void onResponse(Request request, String json);

    void onError(Request request, int code, String message);

}
