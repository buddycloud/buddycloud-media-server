package com.buddycloud.mediaserver.commons.exception;

public class FormInvalidFieldException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6036687197655827078L;
	private String invalidField;

	
	public FormInvalidFieldException(String invalidField) {
		super("multipart/form-data invalid field for update media: '" + invalidField + "'");
		this.invalidField = invalidField;
	}
	
	public FormInvalidFieldException(String message, Throwable cause) {
		super(message, cause);
	}

	
	public String getInvalidField() {
		return invalidField;
	}

	public void setInvalidField(String invalidField) {
		this.invalidField = invalidField;
	}
}
