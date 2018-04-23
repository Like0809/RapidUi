package com.like.rapidui;

/**
 * Created By Like on 2018/4/23.
 */

public class PagingParam {

    private String pageSizeParam;
    private String pageNumParam;

    public PagingParam(String pageSizeParam, String pageNumParam) {
        this.pageSizeParam = pageSizeParam;
        this.pageNumParam = pageNumParam;
    }

    public String getPageSizeParam() {
        return pageSizeParam;
    }

    public void setPageSizeParam(String pageSizeParam) {
        this.pageSizeParam = pageSizeParam;
    }

    public String getPageNumParam() {
        return pageNumParam;
    }

    public void setPageNumParam(String pageNumParam) {
        this.pageNumParam = pageNumParam;
    }
}
