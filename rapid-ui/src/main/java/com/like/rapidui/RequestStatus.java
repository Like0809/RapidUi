package com.like.rapidui;

/**
 * Created By Like on 2018/4/20.
 */

public enum RequestStatus {

    NET_ERR(-1, "网络错误"),

    API_ERR(1, "逻辑错误"),

    OK(0, "成功");

    int key;
    String value;

    RequestStatus(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}
