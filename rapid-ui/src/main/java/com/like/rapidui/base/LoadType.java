package com.like.rapidui.base;

public enum LoadType {

    INIT(0, "初始化"),

    PULL_UP(1, "上拉加载"),

    PULL_DOWN(2, "下拉刷新");

    LoadType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    int code;

    String name;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
