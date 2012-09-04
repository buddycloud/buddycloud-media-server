package com.buddycloud.mediaserver.business.util;

public class XMPPUtils {
	private XMPPUtils() {}
	
	public static String getBareId(String userId) {
		return userId.split("/")[0];
	}
}
