package com.buddycloud.mediaserver.xmpp.pubsub;

import java.util.Properties;

import org.jivesoftware.smack.Connection;
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
		String domain = "mcfly.org";
		
		Mockito.when(
				configuration
						.getProperty(MediaServerConfiguration.XMPP_CONNECTION_SERVICENAME))
				.thenReturn(domain);
		Mockito.when(factory.create(domain)).thenReturn(null);
		
		new PubSubClient(connection, configuration, factory);
		
	}

}
