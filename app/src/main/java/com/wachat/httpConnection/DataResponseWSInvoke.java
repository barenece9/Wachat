package com.wachat.httpConnection;

import java.io.Serializable;

public class DataResponseWSInvoke implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int status_line;

	private String response;

	public int getStatus_line() {
		return status_line;
	}

	public void setStatus_line(int status_line) {
		this.status_line = status_line;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

}
