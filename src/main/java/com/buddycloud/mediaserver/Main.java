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
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.xmpp.component.ComponentException;

import com.buddycloud.mediaserver.commons.MediaServerConfiguration;
import com.buddycloud.mediaserver.commons.exception.CreateXMPPConnectionException;
import com.buddycloud.mediaserver.web.MediaServerApplication;
import com.buddycloud.mediaserver.xmpp.MediaServerComponent;
import com.buddycloud.mediaserver.xmpp.XMPPToolBox;

public class Main {
	private static Logger LOGGER = Logger.getLogger(Main.class);
	
	
	public static void main(String[] args) {  
		Properties configuration = MediaServerConfiguration.getInstance().getConfiguration();
		
		try {
			startRestletComponent(configuration);
			startXMPPToolBox(configuration);
		} catch (Exception e) {
			LOGGER.fatal(e.getMessage(), e);
			System.exit(1);
		}
	} 
	
	
	private static void startRestletComponent(Properties configuration) throws Exception {
	    Component component = new Component();  
	    
	    Server server = component.getServers().add(Protocol.HTTPS, 8443);
	    server.getContext().getParameters().add("sslContextFactory","org.restlet.ext.ssl.PkixSslContextFactory");
	    server.getContext().getParameters().add("keystorePath", configuration.getProperty("https.keystore.path"));
	    server.getContext().getParameters().add("keystorePassword", configuration.getProperty("https.keystore.password"));
	    server.getContext().getParameters().add("keyPassword", configuration.getProperty("https.key.password"));
	    server.getContext().getParameters().add("keystoreType", configuration.getProperty("https.keystore.type"));
	    
	    Context context = component.getContext().createChildContext();
		component.getDefaultHost().attach(new MediaServerApplication(context));
		
	    component.start(); 
	}


	private static void startXMPPToolBox(Properties configuration) throws Exception {
		XMPPConnection connection = createAndStartConnection(configuration);
		addTraceListeners(connection);
		
		MediaServerComponent component = createXMPPComponent(configuration);

		String[] servers = configuration.getProperty("bc.channels.server").split(";");
		
		XMPPToolBox.getInstance().start(component, connection, servers);
		
		while (true) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				throw e;
			}
		}
	}
	
	private static MediaServerComponent createXMPPComponent(Properties configuration) throws Exception {
		ExternalComponentManager componentManager = new ExternalComponentManager(
				configuration.getProperty("xmpp.component.host"),
				Integer.valueOf(configuration.getProperty("xmpp.component.port")));

		String subdomain = configuration.getProperty("xmpp.component.subdomain");
		componentManager.setSecretKey(subdomain, 
				configuration.getProperty("xmpp.component.secretkey"));

		MediaServerComponent mediaServer = new MediaServerComponent();

		try {
			componentManager.addComponent(subdomain, mediaServer);
		} catch (ComponentException e) {
			LOGGER.fatal("Media Server XMPP Component could not be started.", e);
			throw e;
		}
		
		return mediaServer;
	}
	
	private static void addTraceListeners(XMPPConnection connection) {
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

	private static XMPPConnection createAndStartConnection(Properties configuration) {
		
		String serviceName = configuration.getProperty("xmpp.connection.servicename");
		String host = configuration.getProperty("xmpp.connection.host");
		String userName = configuration.getProperty("xmpp.connection.username");
		
		ConnectionConfiguration cc = new ConnectionConfiguration(
				host,
				Integer.parseInt(configuration.getProperty("xmpp.connection.port")),
				serviceName);
		
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
