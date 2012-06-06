package com.buddycloud.mediaserver.commons.exception;

public class InvalidPreviewFormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5828983817422968188L;

	
	public InvalidPreviewFormatException(String format) {
		super("Invalid format for requesting preview: " + format + ". Previews are allowed only for images and videos.");
	}
}
