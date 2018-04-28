package com.like.rapidui.base;

public class DataParam {

    private int successCode = 0;

    private String codeParam;

    private String messageParam;

    private String dataParam;

    public DataParam(int successCode, String codeParam, String messageParam, String dataParam) {
        this.successCode = successCode;
        this.codeParam = codeParam;
        this.messageParam = messageParam;
        this.dataParam = dataParam;
    }

    public int getSuccessCode() {
        return successCode;
    }

    public void setSuccessCode(int successCode) {
        this.successCode = successCode;
    }

    public String getCodeParam() {
        return codeParam;
    }

    public void setCodeParam(String codeParam) {
        this.codeParam = codeParam;
    }

    public String getMessageParam() {
        return messageParam;
    }

    public void setMessageParam(String messageParam) {
        this.messageParam = messageParam;
    }

    public String getDataParam() {
        return dataParam;
    }

    public void setDataParam(String dataParam) {
        this.dataParam = dataParam;
    }
}
