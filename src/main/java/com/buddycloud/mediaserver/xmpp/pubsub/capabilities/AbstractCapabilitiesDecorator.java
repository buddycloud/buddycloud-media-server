package com.buddycloud.mediaserver.xmpp.pubsub.capabilities;


public abstract class AbstractCapabilitiesDecorator implements CapabilitiesDecorator {
	
	private AbstractCapabilitiesDecorator decorator;
	
	
	public AbstractCapabilitiesDecorator(AbstractCapabilitiesDecorator decorator) {
		this.decorator = decorator;
	}
	
	public AbstractCapabilitiesDecorator() {}
	
	
	public boolean isUserAllowed(String affiliationType) {
		if (this.decorator == null) {
			return isAllowed(affiliationType);
		}
		
		return this.decorator.isUserAllowed(affiliationType) || isAllowed(affiliationType);
	}
	
	public boolean isAllowed(String affiliationType) {
		return getType().equals(affiliationType);
	}
}
