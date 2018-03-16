package com.wachat.data;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Priti Chatterjee on 06-10-2015.
 */
public class DataClick  {

    public RecyclerView.ViewHolder mRecyclerView;
    public boolean status = false;

    public RecyclerView.ViewHolder getmRecyclerView() {
        return mRecyclerView;
    }

    public void setmRecyclerView(RecyclerView.ViewHolder mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
