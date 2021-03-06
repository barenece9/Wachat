package com.wachat.dataClasses;

import java.io.Serializable;

public class GallerySetGet implements Serializable, Comparable<GallerySetGet> {

    @Override
    public int compareTo(GallerySetGet o) {
        return image_URL.equals(o.getImage_URL()) ? 0 : 1;
    }

    private static final long serialVersionUID = 1L;

    public String image_URL = "";
    public boolean isSelected = false;
    public boolean isMasked = false;
    public int state = 0;

    public String getImage_URL() {
        return image_URL;
    }

    public void setImage_URL(String image_URL) {
        this.image_URL = image_URL;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isMasked() {
        return isMasked;
    }

    public void setIsMasked(boolean isMasked) {
        this.isMasked = isMasked;
    }
}
