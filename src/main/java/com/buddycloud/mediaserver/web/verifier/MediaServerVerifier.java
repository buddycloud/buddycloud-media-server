package com.buddycloud.mediaserver.web.verifier;

import org.restlet.engine.Engine;
import org.restlet.resource.Resource;
import org.restlet.security.LocalVerifier;

import com.buddycloud.mediaserver.xmpp.AuthVerifierClient;
import com.buddycloud.mediaserver.xmpp.XMPPToolBox;

public class MediaServerVerifier extends LocalVerifier {
	
	@Override
	public int verify(String identifier, char[] secret) {
		AuthVerifierClient authClient = XMPPToolBox.getInstance().getAuthClient();
		
		return authClient.verifyUser(identifier, new String(secret)) ? RESULT_VALID : RESULT_INVALID;
	}

	@Override
	public char[] getLocalSecret(String identifier) {
		// not needed
		return null;
	}

}
