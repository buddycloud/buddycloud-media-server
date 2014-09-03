package com.buddycloud.mediaserver.business.util;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smackx.pubsub.PubSubManager;

public class PubSubManagerFactory {

	private Connection connection;

	public PubSubManagerFactory(Connection connection) {
		this.connection = connection;
	}
	
	public PubSubManager create(String domain) {
		return new PubSubManager(connection, domain);
	}
}