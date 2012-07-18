package com.buddycloud.mediaserver.commons.exception;

public class FormMissingFieldException extends FormFieldException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3440806143900550595L;
	private String field;

	
	public FormMissingFieldException(String field) {
		super("multipart/form-data missing field: '" + field + "'");
		this.field = field;
	}
	
	
	public String getField() {
		return field;
	}

	public void setSetField(String missingField) {
		this.field = missingField;
	}
}
