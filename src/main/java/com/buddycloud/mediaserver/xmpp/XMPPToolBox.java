package com.buddycloud.mediaserver.xmpp;

import org.jivesoftware.smack.XMPPConnection;

import com.buddycloud.mediaserver.xmpp.pubsub.PubSubClient;


public class XMPPToolBox {
	
	private AuthVerifierClient authClient;
	private PubSubClient pubSubClient;
	private boolean started;
	
	
	private static final XMPPToolBox instance = new XMPPToolBox();


	protected XMPPToolBox() {
		started = false;
	}
	
	
	public static XMPPToolBox getInstance() {
		return instance;
	}
	
	
	public void start(XMPPConnection connection, String[] servers) {
		if (!started) {
			authClient = new AuthVerifierClient(connection);
			pubSubClient = new PubSubClient(connection, servers);
			
			started = true;
		}
	}
	

	public AuthVerifierClient getAuthClient() {
		return authClient;
	}

	public PubSubClient getPubSubClient() {
		return pubSubClient;
	}
}
