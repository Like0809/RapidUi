package com.like.rapidui.network.utils;

/**
 * Created By zhy on 15/12/14.
 */
public class Exceptions {
    public static void illegalArgument(String msg, Object... params) {
        throw new IllegalArgumentException(String.format(msg, params));
    }


}
