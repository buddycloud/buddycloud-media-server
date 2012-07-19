package com.buddycloud.mediaserver.web;


import org.xmpp.packet.Packet;

import com.buddycloud.mediaserver.xmpp.MediaServer;

public class TestMediaServer extends MediaServer {
	
	protected TestMediaServer() {}
	
	
	@Override
	protected void send(Packet arg0) {}
	
	@Override
	public void postComponentStart() {
		createHandlers();
	}

	private void createHandlers() {

	}
}
