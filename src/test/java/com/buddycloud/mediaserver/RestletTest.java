package com.buddycloud.mediaserver;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;

import com.buddycloud.mediaserver.commons.MediaServerConfiguration;
import com.buddycloud.mediaserver.web.MediaServerApplication;

public class RestletTest implements TextExtension {
	private static Logger LOGGER = Logger.getLogger(RestletTest.class);
	
	private Component component;
	private boolean started;
	
	
	public RestletTest() {
		this.started = false;
	}

	
	public void start(Properties configuration) throws Exception {
		if (!started) {
			component = new Component();  
			component.getServers().add(Protocol.HTTP, 
					Integer.valueOf(configuration.getProperty(MediaServerConfiguration.HTTP_PORT)));
			
			Context context = component.getContext().createChildContext();
			component.getDefaultHost().attach(new MediaServerApplication(context));
			
			component.start(); 
			
			LOGGER.debug("Started test HTTP server");

			started = true;
		}
	} 
	
	public void shutdown() throws Exception {
		if (started) {
			component.stop();
			
			started = false;
		}
	}
}
