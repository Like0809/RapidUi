package com.like.frame.rapiddevframe.entity;

/**
 * Created By Like on 2018/4/23.
 */

public class Banner {

    private int url;
    private String title;

    public Banner(int url, String title) {
        this.url = url;
        this.title = title;
    }

    public int getUrl() {
        return url;
    }

    public void setUrl(int url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
