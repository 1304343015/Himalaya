package com.example.himalaya.base;

public interface BasePresenter<T> {
    void registerViewListener(T t);

    void unRegisterViewListener(T t);
}
