package com.buddycloud.mediaserver;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.whack.ExternalComponentManager;
import org.xmpp.component.ComponentException;

import com.buddycloud.mediaserver.commons.exception.CreateXMPPConnectionException;
import com.buddycloud.mediaserver.xmpp.MediaServer;
import com.buddycloud.mediaserver.xmpp.XMPPToolBox;

public class XMPPTest {
	private static Logger LOGGER = Logger.getLogger(XMPPTest.class);

	
	public void start(Properties configuration) throws Exception {
		//startXMPPComponent(configuration);
		createPubSubController(configuration);
	} 

	private void startXMPPComponent(Properties configuration) throws Exception {
		ExternalComponentManager componentManager = new ExternalComponentManager(
				configuration.getProperty("xmpp.host"),
				Integer.valueOf(configuration.getProperty("xmpp.port")));

		String subdomain = configuration.getProperty("xmpp.subdomain");
		componentManager.setSecretKey(subdomain, 
				configuration.getProperty("xmpp.secretkey"));

		MediaServer mediaServer = XMPPToolBox.getInstance().createMediaServerComponent();

		try {
			componentManager.addComponent(subdomain, mediaServer);
		} catch (ComponentException e) {
			throw e;
		}

		while (true) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				throw e;
			}
		}
	}

	private void createPubSubController(Properties configuration) {
		XMPPConnection connection = createAndStartConnection(configuration);
		addTraceListeners(connection);
		
		String[] servers = configuration.getProperty("bc.channels.server").split(";");
		XMPPToolBox.getInstance().createPubSubController(connection, servers);
	}
	
	private void addTraceListeners(XMPPConnection connection) {
		PacketFilter iqFilter = new PacketFilter() {
			@Override
			public boolean accept(Packet arg0) {
				return arg0 instanceof IQ;
			}
		};

		connection.addPacketSendingListener(new PacketListener() {
			@Override
			public void processPacket(Packet arg0) {
				LOGGER.debug("S: " + arg0.toXML());
			}
		}, iqFilter);

		connection.addPacketListener(new PacketListener() {

			@Override
			public void processPacket(Packet arg0) {
				LOGGER.debug("R: " + arg0.toXML());
			}
		}, iqFilter);
	}

	private XMPPConnection createAndStartConnection(Properties configuration) {
		
		String serviceName = configuration.getProperty("xmpp.connection.servicename");
		String host = configuration.getProperty("xmpp.connection.host");
		String userName = configuration.getProperty("xmpp.connection.username");
		
		ConnectionConfiguration cc = new ConnectionConfiguration(
				host,
				Integer.parseInt(configuration.getProperty("xmpp.connection.port")),
				serviceName);
		cc.setSASLAuthenticationEnabled(false);
		
		XMPPConnection connection = new XMPPConnection(cc);
		try {
			connection.connect();
			connection.login(userName, configuration.getProperty("xmpp.connection.password"));
		} catch (XMPPException e) {
			LOGGER.fatal("XMPP connection coudn't be started", e);
			throw new CreateXMPPConnectionException(e.getMessage(), e);
		}
		
		addTraceListeners(connection);
		
		return connection;
	}
}
