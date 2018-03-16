package com.wachat.dataClasses;

import java.io.Serializable;

public class ViewGroupImageSetGet implements Serializable {
	private static final long serialVersionUID = 1L;

	public String image_URL = "";
	public boolean isSelected = false;

	public String getImage_URL() {
		return image_URL;
	}

	public void setImage_URL(String image_URL) {
		this.image_URL = image_URL;
	}



}
