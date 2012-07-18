package com.buddycloud.mediaserver.xmpp.pubsub.capabilities;


public class OwnerDecorator extends AbstractCapabilitiesDecorator {

	private static final String TYPE = "owner";
	
	
	public OwnerDecorator(AbstractCapabilitiesDecorator decorator) {
		super(decorator);
	}
	
	public OwnerDecorator() {}
	
	
	@Override
	public String getType() {
		return TYPE;
	}
}
