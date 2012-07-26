package com.buddycloud.mediaserver.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeRequest;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.representation.Representation;
import org.restlet.resource.ServerResource;

import com.buddycloud.mediaserver.xmpp.AuthVerifier;
import com.buddycloud.mediaserver.xmpp.XMPPToolBox;

public abstract class MediaServerResource extends ServerResource {
	
	protected static final String AUTH_SEPARATOR = ":";
	
	
	protected boolean verifyRequest(String userId, String token, String url) {
		AuthVerifier authClient = XMPPToolBox.getInstance().getAuthClient();
			
		return authClient.verifyRequest(userId, token, url);
	}
	
	protected Representation authenticationResponse() {
		List<ChallengeRequest> challengeRequests = new ArrayList<ChallengeRequest>();
		challengeRequests.add(new ChallengeRequest(ChallengeScheme.HTTP_BASIC, MediaServerApplication.REALM));

		Response response = getResponse();
		response.setChallengeRequests(challengeRequests);
		
		return response.getEntity();
	}
	
	protected String decodeAuth(String auth) {
		Base64 decoder = new Base64(true);
		
		return new String(decoder.decode(auth.getBytes()));
	}
	
	protected String getUserId(Request request, String auth) {
		String userId = null;

		if (auth == null) {
			ChallengeResponse challenge = request.getChallengeResponse();
			if (challenge != null) {
				userId = challenge.getIdentifier();
			}
		} else {
			String[] split = decodeAuth(auth).split(AUTH_SEPARATOR);
			userId = split[0];
		}
		
		return userId;
	}
	
	protected String getTransactionId(Request request, String auth) {
		String tid = null;

		if (auth == null) {
			ChallengeResponse challenge = request.getChallengeResponse();
			if (challenge != null) {
				tid = new String(challenge.getSecret());
			}
		} else {
			String[] split = decodeAuth(auth).split(AUTH_SEPARATOR);
			tid = split[1];
		}
		
		return tid;
	}
}
