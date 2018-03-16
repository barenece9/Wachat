package com.wachat.interfaces;

import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 21-09-2015.
 */
public interface InterfaceResponseCallback {

    void onResponseObject(Object mObject);
    void onResponseList(ArrayList<?> mList);
    void onResponseFaliure(String responseText);

}
