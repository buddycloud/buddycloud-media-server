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
		router.attach(Constants.GET_MEDIA_URL, DownloadMediaResource.class);
		
		// POST /channel/<name@domain.com>/media
		router.attach(Constants.POST_MEDIA_URL, UploadMediaResource.class);
		
		// GET /channel/<name@domain.com>/media/avatar/1
		router.attach(Constants.GET_CHANNEL_AVATAR_URL, ChannelAvatarResource.class);
		
		// PUT /channel/<name@domain.com>/media/avatar/1
		router.attach(Constants.PUT_CHANNEL_AVATAR_URL, ChannelAvatarResource.class);
		
		// GET /user/<name@domain.com>/media/avatar/1
		router.attach(Constants.GET_USER_AVATAR_URL, UserAvatarResource.class);
		
		// PUT /user/<name@domain.com>/media/avatar/1
		router.attach(Constants.PUT_USER_AVATAR_URL, UserAvatarResource.class);
		
		return router;
	}
}