package com.buddycloud.mediaserver.web;
import org.restlet.Context;
import org.restlet.data.ChallengeScheme;
import org.restlet.ext.crypto.DigestAuthenticator;
import org.restlet.security.ChallengeAuthenticator;


public class TestMediaServerApplication extends MediaServerApplication {

	
	public TestMediaServerApplication(Context parentContext) {
		super(parentContext);
	}
	
	
	protected DigestAuthenticator getDigestAuthenticator() {
		DigestAuthenticator auth = new DigestAuthenticator(getContext(), REALM, "secret");
		auth.setOptional(true);
	    auth.setWrappedVerifier(new TestMediaServerVerifier());
	    
	    return auth;
	}
	
	protected ChallengeAuthenticator getBasicAuthenticator() {
		ChallengeAuthenticator auth = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC, REALM);
		auth.setOptional(false);
	    auth.setVerifier(new TestMediaServerVerifier());
	    
	    return auth;
	}
}