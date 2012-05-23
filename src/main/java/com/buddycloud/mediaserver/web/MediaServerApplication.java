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
		
		// GET /channel/<name@domain.com>/media/<mediaID>
		router.attach("/media/{" + Constants.MEDIA_ARG + "}", DownloadMediaResource.class);
		
		// POST /channel/<name@domain.com>/media
		router.attach("/media", UploadMediaResource.class);
		
		return router;
	}
}