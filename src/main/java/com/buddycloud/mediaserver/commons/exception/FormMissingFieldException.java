package com.buddycloud.mediaserver.commons.exception;

public class FormMissingFieldException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3440806143900550595L;
	private String missingField;

	
	public FormMissingFieldException(String missingField) {
		super("multipart/form-data missing field: '" + missingField + "'");
		this.missingField = missingField;
	}
	
	public FormMissingFieldException(String message, Throwable cause) {
		super(message, cause);
	}

	
	public String getMissingField() {
		return missingField;
	}

	public void setSetMissingField(String missingField) {
		this.missingField = missingField;
	}
}
