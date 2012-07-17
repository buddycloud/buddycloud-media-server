package com.buddycloud.mediaserver.xmpp.pubsub.capabilities;


public class MemberDecorator extends AbstractCapabilitiesDecorator {

	private static final String TYPE = "member";
	
	
	@Override
	public String getType() {
		return TYPE;
	}
}
