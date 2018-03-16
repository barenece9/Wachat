package com.wachat;

/**
 * Created by Debopam Sikder on 25-05-2016.
 */
public interface GcmOnCompleteListener {
    void onComplete();

    void onError(String message);

    void onCancel();
}
