package com.buddycloud.mediaserver.commons.exception;

public class FormMissingFieldException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1501256336773866438L;
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


	public void setMissingField(String missingField) {
		this.missingField = missingField;
	}
	
}
