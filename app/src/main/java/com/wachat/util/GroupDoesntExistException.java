package com.wachat.util;

public class GroupDoesntExistException extends Exception{

	public GroupDoesntExistException(String message) {
		super(message);
	}

	public GroupDoesntExistException() {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
