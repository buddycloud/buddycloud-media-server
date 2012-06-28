package com.buddycloud.mediaserver;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.jivesoftware.whack.ExternalComponentManager;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;
import org.xmpp.component.ComponentException;

import com.buddycloud.mediaserver.commons.ConfigurationUtils;
import com.buddycloud.mediaserver.web.MediaServerApplication;
import com.buddycloud.mediaserver.xmpp.MediaServer;

public class Main {
	private static Logger LOGGER = Logger.getLogger(Main.class);
	
	
	public static void main(String[] args) {  
		try {
			startRestletComponent();
			startXMPPComponent();
		} catch (Exception e) {
			LOGGER.fatal(e.getMessage(), e);
			System.exit(1);
		}
	} 
	
	
	private static void startRestletComponent() throws Exception {
	    Component component = new Component();  
	    component.getServers().add(Protocol.HTTP, 8080);  
	    
	    Context context = component.getContext().createChildContext();
		component.getDefaultHost().attach(new MediaServerApplication(context));
		
	    component.start(); 
	}

	private static void startXMPPComponent() throws Exception {
		Properties configuration = ConfigurationUtils.loadConfiguration();
//		XMPPConnection connection = createConnection(configuration);
//		addTraceListeners(connection);
		
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
				LOGGER.fatal("Main loop.", e);
				throw e;
			}
		}
	}
}
