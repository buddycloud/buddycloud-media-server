package com.buddycloud.mediaserver.xmpp.pubsub.capabilities;


public class PublisherDecorator extends AbstractCapabilitiesDecorator {

	private static final String TYPE = "publisher";
	
	
	public PublisherDecorator(AbstractCapabilitiesDecorator decorator) {
		super(decorator);
	}
	
	public PublisherDecorator() {}
	
	
	@Override
	public String getType() {
		return TYPE;
	}
}
