package com.buddycloud.mediaserver.xmpp.pubsub;

import static org.junit.Assert.*;

import java.util.Properties;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.buddycloud.mediaserver.business.util.PubSubManagerFactory;
import com.buddycloud.mediaserver.commons.MediaServerConfiguration;

public class PubSubClientTest {

	private Properties configuration;
	private Connection connection;
	private PubSubManagerFactory factory;
	private PubSubManager manager;
	private DiscoverItems items;

	@Before
	public void setUp() throws Exception {
		connection = Mockito.mock(Connection.class);
		configuration = Mockito.mock(Properties.class);
		factory = Mockito.mock(PubSubManagerFactory.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected=NullPointerException.class)
	public void discoverIsAttemptedOnConnection() throws Exception {
		String jid = "marty@mcfly.org";
		String domain = "mcfly.org";
		
		Mockito.when(
				configuration
						.getProperty(MediaServerConfiguration.XMPP_CONNECTION_USERNAME))
				.thenReturn(jid);
		Mockito.when(factory.create(domain)).thenReturn(null);
		
		PubSubClient client = new PubSubClient(connection, configuration, factory);
		
	}

}
