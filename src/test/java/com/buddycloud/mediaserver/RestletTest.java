package com.buddycloud.mediaserver;

import org.apache.log4j.Logger;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;

import com.buddycloud.mediaserver.web.MediaServerApplication;

public class RestletTest {
	private static Logger LOGGER = Logger.getLogger(RestletTest.class);

	
	public Component start() throws Exception {
	    Component component = new Component();  
	    component.getServers().add(Protocol.HTTP, 8080);
	    
	    Context context = component.getContext().createChildContext();
		component.getDefaultHost().attach(new MediaServerApplication(context));
		
	    component.start(); 
	    
	    LOGGER.debug("Started test HTTP server");
	    
	    return component;
	} 
}
