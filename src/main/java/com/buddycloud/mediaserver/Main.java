package com.buddycloud.mediaserver;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.whack.ExternalComponentManager;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.xmpp.component.ComponentException;

import com.buddycloud.mediaserver.commons.ConfigurationContext;
import com.buddycloud.mediaserver.web.TestMediaServerApplication;
import com.buddycloud.mediaserver.xmpp.MediaServer;

public class Main {
	private static Logger LOGGER = Logger.getLogger(Main.class);
	
	
	public static void main(String[] args) {  
		Properties configuration = ConfigurationContext.getInstance().getConfiguration();
		
		try {
			startRestletComponent(configuration);
			startXMPPComponent(configuration);
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
		component.getDefaultHost().attach(new TestMediaServerApplication(context));
		
	    component.start(); 
	}

	private static void startXMPPComponent(Properties configuration) throws Exception {
		XMPPConnection connection = createConnection(configuration);
		addTraceListeners(connection);
		
		ExternalComponentManager componentManager = new ExternalComponentManager(
				configuration.getProperty("xmpp.host"),
				Integer.valueOf(configuration.getProperty("xmpp.port")));
		
		String subdomain = configuration.getProperty("xmpp.subdomain");
		componentManager.setSecretKey(subdomain, 
				configuration.getProperty("xmpp.secretkey"));
		
		try {
			componentManager.addComponent(subdomain, 
					new MediaServer(configuration));
		} catch (ComponentException e) {
			LOGGER.fatal("Media Server XMPP Component could not be started.", e);
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

	private static XMPPConnection createConnection(Properties configuration)
			throws Exception {
		
		String serviceName = configuration.getProperty("crawler.xmpp.servicename");
		String host = configuration.getProperty("crawler.xmpp.host");
		String userName = configuration.getProperty("crawler.xmpp.username");
		
		ConnectionConfiguration cc = new ConnectionConfiguration(
				host,
				Integer.parseInt(configuration.getProperty("crawler.xmpp.port")),
				serviceName);
		
		XMPPConnection connection = new XMPPConnection(cc);
		connection.connect();
		connection.login(userName, configuration.getProperty("crawler.xmpp.password"));
		
		return connection;
	}
}
