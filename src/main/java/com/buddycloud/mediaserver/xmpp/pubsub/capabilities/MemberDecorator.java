package com.buddycloud.mediaserver.xmpp.pubsub.capabilities;


public class MemberDecorator extends AbstractCapabilitiesDecorator {

	private static final String TYPE = "member";
	
	
	public MemberDecorator(AbstractCapabilitiesDecorator decorator) {
		super(decorator);
	}
	
	public MemberDecorator() {}
	
	
	@Override
	public String getType() {
		return TYPE;
	}
}
