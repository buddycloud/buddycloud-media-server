package com.buddycloud.mediaserver;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import com.buddycloud.mediaserver.resource.MediaResource;


public class MediaServerApplication extends Application {
	
	public MediaServerApplication(Context parentContext) {
		super(parentContext);
	}
	
	@Override
	public synchronized void start() throws Exception {
		super.start();
		
		//TODO start xmpp component
	}

	/**
	 * Creates a root Restlet that will receive all incoming calls.
	 */
	@Override
	public synchronized Restlet createRoot() {
		Router router = new Router(getContext());
		
		router.attach("/media/{channel}/{id}", MediaResource.class);

		return router;
	}
}