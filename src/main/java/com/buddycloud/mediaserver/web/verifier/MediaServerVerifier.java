package com.buddycloud.mediaserver.web.verifier;

import org.restlet.security.LocalVerifier;

public class MediaServerVerifier extends LocalVerifier {

	@Override
	public char[] getLocalSecret(String identifier) {
		//TODO get "identifier" password
		
		return null;
	}

}
