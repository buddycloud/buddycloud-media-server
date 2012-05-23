package com.buddycloud.mediaserver.commons.exception;

public class NotMatchingChecksumException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7098323937854879516L;
	
	
	public NotMatchingChecksumException(String expectedMD5, String generatedMD5) {
		super("Media MD5 checksum hasn't matched. Expected: " + expectedMD5 + ". Generated: " + generatedMD5);
	}
}
