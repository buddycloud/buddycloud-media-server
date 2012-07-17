package com.buddycloud.mediaserver.xmpp.pubsub.capabilities;


public interface CapabilitiesDecorator {
	public boolean isUserAllowed(String affiliationType);
	
	public String getType();
}
