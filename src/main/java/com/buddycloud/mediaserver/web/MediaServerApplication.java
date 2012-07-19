package com.buddycloud.mediaserver.web;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.ext.crypto.DigestAuthenticator;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;

import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.web.verifier.MediaServerVerifier;


public class MediaServerApplication extends Application {
	
	protected static final String REALM = "xmpp";
	
	
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
		
		ChallengeAuthenticator basicAuth = getBasicAuthenticator();
		basicAuth.setNext(router);
		
		DigestAuthenticator digestAuth = getDigestAuthenticator();
		digestAuth.setNext(router);
		
		return router;
	}
	
	protected DigestAuthenticator getDigestAuthenticator() {
		DigestAuthenticator auth = new DigestAuthenticator(getContext(), REALM, "secret");
		auth.setOptional(true);
	    auth.setWrappedVerifier(new MediaServerVerifier());
	    
	    return auth;
	}
	
	protected ChallengeAuthenticator getBasicAuthenticator() {
		ChallengeAuthenticator auth = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC, REALM);
		auth.setOptional(false);
	    auth.setVerifier(new MediaServerVerifier());
	    
	    return auth;
	}
}