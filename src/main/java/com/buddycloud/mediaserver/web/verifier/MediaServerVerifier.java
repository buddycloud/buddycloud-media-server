package com.buddycloud.mediaserver.web.verifier;

import org.restlet.security.SecretVerifier;

public class MediaServerVerifier extends SecretVerifier {
	
	@Override
	public int verify(String identifier, char[] secret) {
		// TODO XMPP component asks client about the "transaction id"
		return 0;
	}

}
