package com.buddycloud.mediaserver.xmpp;

import org.apache.log4j.Logger;
import org.xmpp.component.AbstractComponent;
import org.xmpp.packet.Packet;

public class MediaServer extends AbstractComponent {
	
	private static final String DESCRIPTION = "An XMPP Media Server";
	private static final String NAME = "Media Server";
	
	private static Logger LOGGER = Logger.getLogger(MediaServer.class);

	
	MediaServer() {}
	
	
	@Override
	protected void send(Packet arg0) {
		LOGGER.debug("S: " + arg0.toXML());
		super.send(arg0);
	}
	
	@Override
	public void postComponentStart() {
		createHandlers();
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}
	
	private void createHandlers() {

	}
}
