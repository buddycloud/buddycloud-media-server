package com.buddycloud.mediaserver.xmpp.pubsub.capabilities;


public class ModeratorDecorator extends AbstractCapabilitiesDecorator {

	private static final String TYPE = "moderator";
	
	
	@Override
	public String getType() {
		return TYPE;
	}
}
