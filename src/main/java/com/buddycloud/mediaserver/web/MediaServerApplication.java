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
		
		// GET /media/<name@domain.com>/<mediaID>
		router.attach(Constants.GET_MEDIA_URL, DownloadMediaResource.class);
		
		// POST /media/<name@domain.com>
		router.attach(Constants.POST_MEDIA_URL, UploadMediaResource.class);
		
		// GET /media/avatar/<name@domain.com>/<mediaID>
		router.attach(Constants.GET_AVATAR_URL, AvatarResource.class);
		
		// PUT /media/avatar/<name@domain.com>/<mediaID>
		router.attach(Constants.PUT_AVATAR_URL, AvatarResource.class);
		
		return router;
	}
}