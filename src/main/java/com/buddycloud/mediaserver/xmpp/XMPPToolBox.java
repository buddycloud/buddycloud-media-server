package com.buddycloud.mediaserver.xmpp;

import org.jivesoftware.smack.Connection;

import com.buddycloud.mediaserver.xmpp.pubsub.PubSubController;


public class XMPPToolBox {
	
	private MediaServer mediaServer;
	private PubSubController pubsubController;
	
	
	private static final XMPPToolBox instance = new XMPPToolBox();


	protected XMPPToolBox() {}
	
	
	public static XMPPToolBox getInstance() {
		return instance;
	}
	
	
	public MediaServer createMediaServerComponent() {
		if (mediaServer == null) {
			mediaServer = new MediaServer();
		}
		
		return mediaServer;
	}
	
	public MediaServer getMediaServerComponent() {
		return mediaServer;
	}
	
	public PubSubController createPubSubController(Connection connection) {
		if (pubsubController == null) {
			pubsubController = new PubSubController(connection);
		}
		
		return pubsubController;
	}
	
	public PubSubController getPubSubController() {
		return pubsubController;
	}
}
