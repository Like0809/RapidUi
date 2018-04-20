package com.like.rapidui.network;


import com.google.gson.Gson;

/**
 * Created By Like on 2017/3/22.
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class ApiMessage {
    private String message;
    private int code;
    private static Gson gson = new Gson();
    public static final int OK = 0;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static boolean isApiMessage(String json) {
        return json != null && json.matches("\\{\"code\":-?\\d*,\"message\":\"[\\s\\S]*\"\\}");
    }

    public static ApiMessage parseApiMessage(String json) {
        return gson.fromJson(json, ApiMessage.class);
    }
}
