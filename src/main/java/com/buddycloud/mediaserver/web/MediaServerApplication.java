package com.buddycloud.mediaserver.web;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import com.buddycloud.mediaserver.commons.Constants;


public class MediaServerApplication extends Application {
	
	public MediaServerApplication(Context parentContext) {
		super(parentContext);
	}
	
	/**
	 * Creates a root Restlet that will receive all incoming calls.
	 */
	@Override
	public synchronized Restlet createInboundRoot() {
		Router router = new Router(getContext());
		
		// GET/PUT/DELETE /media/<name@domain.com>/<mediaID>
		router.attach(Constants.MEDIA_ID_URL, MediaResource.class);
		
		// POST /media/<name@domain.com>
		router.attach(Constants.MEDIAS_URL, MediasResource.class);
		
		return router;
	}
}