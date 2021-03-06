package com.wachat.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;

import java.io.Serializable;

/**
 * Created by Priti Chatterjee on 06-10-2015.
 */
public class DataShareImage implements Serializable {


    public boolean isMasked = false;
    public String caption = "";
    public String imgUrl = "";
    public String imgUrlBlur = "";
    public boolean sketchType = false;

    public DataShareImage() {
    }

    public String getImgUrlBlur() {
        return imgUrlBlur;
    }

    public void setImgUrlBlur(String imgUrlBlur) {
        this.imgUrlBlur = imgUrlBlur;
    }

    public boolean isMasked() {
        return isMasked;
    }

    public void setIsMasked(boolean isMasked) {
        this.isMasked = isMasked;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public boolean isSketchType() {
        return sketchType;
    }

    public void setSketchType(boolean sketchType) {
        this.sketchType = sketchType;
    }


}
