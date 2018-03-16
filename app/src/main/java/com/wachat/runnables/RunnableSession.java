package com.wachat.runnables;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.wachat.util.Constants;

/**
 * Created by Priti Chatterjee on 12-10-2015.
 */
public class RunnableSession implements Runnable {

    private Handler uiHandler;

    public RunnableSession(Handler uiHandler) {
        this.uiHandler = uiHandler;
    }

    @Override
    public void run() {


        // Http Connection to Validate Uesr Session
        // Check AsyncTask Response If Success Then Wait For the Specified amount of time
    }
}
