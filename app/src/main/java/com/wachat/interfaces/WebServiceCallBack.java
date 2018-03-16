package com.wachat.interfaces;

/**
 * Created by Ashish on 15-03-2016.
 */
public interface WebServiceCallBack<T> {
    void onSuccess(T result);
    void onFailure(T result);
    void onFailure(Throwable e);
}
