package com.buddycloud.mediaserver.commons.exception;

public class FormInvalidFieldException extends FormFieldException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6036687197655827078L;
	private String invalidField;

	
	public FormInvalidFieldException(String invalidField) {
		super("multipart/form-data invalid field: '" + invalidField + "'");
		this.invalidField = invalidField;
	}
	
	
	public String getInvalidField() {
		return invalidField;
	}

	public void setInvalidField(String invalidField) {
		this.invalidField = invalidField;
	}
}
