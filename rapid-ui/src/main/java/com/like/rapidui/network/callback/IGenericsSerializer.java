package com.like.rapidui.network.callback;

public interface IGenericsSerializer {
    <T> T transform(String response, Class<T> classOfT);
}
