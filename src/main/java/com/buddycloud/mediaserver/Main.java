package com.buddycloud.mediaserver;

import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;

import com.buddycloud.mediaserver.web.MediaServerApplication;

public class Main {
	public static void main(String[] args) throws Exception {  
	    // Create a new Restlet component and add a HTTP server connector to it  
	    Component component = new Component();  
	    component.getServers().add(Protocol.HTTP, 8080);  
	    
	    // Then attach it to the local host  
	    Context context = component.getContext().createChildContext();
		component.getDefaultHost().attach("/media", new MediaServerApplication(context));
		
	    // Now, let's start the component!  
	    // Note that the HTTP server connector is also automatically started.  
	    component.start(); 
	} 
}
