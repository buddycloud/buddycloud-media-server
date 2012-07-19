package com.buddycloud.mediaserver.web.verifier;

import org.restlet.security.LocalVerifier;

public class MediaServerVerifier extends LocalVerifier {
	
	@Override
	public int verify(String identifier, char[] secret) {
		//TODO implement XEP 0070
		
		return RESULT_VALID;
	}

	@Override
	public char[] getLocalSecret(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}

}
