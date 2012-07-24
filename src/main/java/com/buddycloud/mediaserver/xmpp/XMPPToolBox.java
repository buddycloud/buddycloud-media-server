package com.buddycloud.mediaserver.xmpp;

import org.jivesoftware.smack.XMPPConnection;

import com.buddycloud.mediaserver.xmpp.pubsub.PubSubClient;


public class XMPPToolBox {
	
	private AuthVerifier authClient;
	private PubSubClient pubSubClient;
	private boolean started;
	
	
	private static final XMPPToolBox instance = new XMPPToolBox();


	protected XMPPToolBox() {
		started = false;
	}
	
	
	public static XMPPToolBox getInstance() {
		return instance;
	}
	
	
	public void start(MediaServerComponent component, XMPPConnection connection, String[] servers) {
		if (!started) {
			authClient = new AuthVerifier(component);
			pubSubClient = new PubSubClient(connection, servers);
			
			started = true;
		}
	}
	

	public AuthVerifier getAuthClient() {
		return authClient;
	}

	public PubSubClient getPubSubClient() {
		return pubSubClient;
	}
}
