package com.penta.rnhotupdatelibrary.http;

import java.lang.reflect.ParameterizedType;

public abstract class HttpCallBack<T> {

    private Class<T> clazz;

    public HttpCallBack() {
        this.clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public abstract void onSuccess(T t);

    public Class<T> getClazz() {
        return clazz;
    }
}