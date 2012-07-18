package com.buddycloud.mediaserver.xmpp.pubsub.capabilities;


public class ModeratorDecorator extends AbstractCapabilitiesDecorator {

	private static final String TYPE = "moderator";
	
	
	public ModeratorDecorator(AbstractCapabilitiesDecorator decorator) {
		super(decorator);
	}
	
	public ModeratorDecorator() {}
	
	
	@Override
	public String getType() {
		return TYPE;
	}
}
