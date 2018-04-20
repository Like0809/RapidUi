package com.like.rapidui;

import java.util.Map;

/**
 * Created By Like on 2018/4/19.
 */

public class Request {

    private String url;

    private Map<String, String> params;

    private Map<String, String> headers;

    private int loadType;

    public Request(String url, Map<String, String> params, Map<String, String> headers, int loadType) {
        this.url = url;
        this.params = params;
        this.headers = headers;
        this.loadType = loadType;
    }

    public String getUrl() {
        return url;
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

    public int getLoadType() {
        return loadType;
    }

    public void setLoadType(int loadType) {
        this.loadType = loadType;
    }
}