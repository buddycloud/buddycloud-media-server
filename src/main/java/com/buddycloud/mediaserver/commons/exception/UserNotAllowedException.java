package com.buddycloud.mediaserver.commons.exception;

public class UserNotAllowedException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1797702522193347030L;
	private String userId;

	
	public UserNotAllowedException(String userId) {
		super("User '" + userId + "' not allowed to peform this operation'");
		this.userId = userId;
	}
	
	public UserNotAllowedException(String message, Throwable cause) {
		super(message, cause);
	}
	

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
