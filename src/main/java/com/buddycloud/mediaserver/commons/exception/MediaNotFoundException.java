package com.buddycloud.mediaserver.commons.exception;

public class MediaNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5309544190868706356L;
	
	
	public MediaNotFoundException(String mediaId, String channelId) {
		super("No media with id '" + mediaId + "' found on the channel '" + channelId + "'.");
	}

}
