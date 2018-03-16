package com.wachat.data;

import java.io.Serializable;

/**
 * Created by Gourav Kundu on 24-08-2015.
 */
public class DataChat implements Serializable {

    public String msg ="";

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
