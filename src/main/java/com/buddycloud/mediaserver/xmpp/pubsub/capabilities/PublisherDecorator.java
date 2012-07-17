package com.buddycloud.mediaserver.xmpp.pubsub.capabilities;


public class PublisherDecorator extends AbstractCapabilitiesDecorator {

	private static final String TYPE = "publisher";
	
	
	@Override
	public String getType() {
		return TYPE;
	}
}
