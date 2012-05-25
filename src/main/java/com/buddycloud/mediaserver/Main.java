package com.buddycloud.mediaserver;

import org.apache.log4j.Logger;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;

import com.buddycloud.mediaserver.web.MediaServerApplication;

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

	private static void startXMPPComponent() {
		// TODO Auto-generated method stub
		
	}
}
