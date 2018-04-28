package com.like.rapidui.base;

import java.util.Map;

/**
 * Created By Like on 2018/4/19.
 */

public class Request {

    private String url;

    private LoadType loadType;

    private Map<String, String> params;

    private Map<String, String> headers;

    public Request(String url, Map<String, String> params, Map<String, String> headers, LoadType loadType) {
        this.url = url;
        this.loadType = loadType;
        this.params = params;
        this.headers = headers;
    }

    public String getUrl() {
        return url;
    }

    public LoadType getLoadType() {
        return loadType;
    }

    public void setLoadType(LoadType loadType) {
        this.loadType = loadType;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

}
