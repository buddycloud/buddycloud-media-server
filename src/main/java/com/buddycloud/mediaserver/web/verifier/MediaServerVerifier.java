package com.buddycloud.mediaserver.web.verifier;

import org.restlet.security.SecretVerifier;

public class MediaServerVerifier extends SecretVerifier {
	
	@Override
	public int verify(String identifier, char[] secret) {
		//TODO implement XEP 0070
		
		return RESULT_VALID;
	}

}
