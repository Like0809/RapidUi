package com.like.rapidui.network.callback;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import okhttp3.Response;

@SuppressWarnings("unused")
public abstract class GenericsCallback<T> extends Callback<T> {
    IGenericsSerializer iGenericsSerializer;

    public GenericsCallback(IGenericsSerializer iGenericsSerializer) {
        this.iGenericsSerializer = iGenericsSerializer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T parseNetworkResponse(Response response, int id) throws IOException {
        String string = response.body().string();
        Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if (entityClass == String.class) {
            return (T) string;
        }
        return iGenericsSerializer.transform(string, entityClass);
    }

}
