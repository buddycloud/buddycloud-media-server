/*
 * Copyright 2012 buddycloud
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.buddycloud.mediaserver.web;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.codec.binary.Base64;
import org.restlet.Message;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeRequest;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.engine.header.Header;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Options;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

import com.buddycloud.mediaserver.xmpp.AuthVerifier;
import com.buddycloud.mediaserver.xmpp.XMPPToolBox;

public abstract class MediaServerResource extends ServerResource {

	protected static final String AUTH_SEPARATOR = ":";
	protected static final String HEADERS_KEY = "org.restlet.http.headers";
	protected static final String CORS_ALLOW_HEADER = "Access-Control-Allow-Headers";
	protected static final String CORS_ORIGIN_HEADER = "Access-Control-Allow-Origin";
	protected static final String CORS_METHODS_HEADER = "Access-Control-Allow-Methods";
	protected static final String CORS_CREDENTIALS_HEADER = "Access-Control-Allow-Credentials";
	protected static final String REQUEST_METHOD_HEADER = "Access-Control-Request-Method";
	protected static final String ORIGIN_HEADER = "Origin";
	
	@Options
	public Representation getOptions() {
		Request request = getRequest();
		
		Series<Header> messageHeaders = getMessageHeaders(request);
		Header requestMethod = messageHeaders.getFirst(REQUEST_METHOD_HEADER);
		
		if (requestMethod != null) {
			if (requestMethod.getValue().toUpperCase().equals("PUT") || 
					requestMethod.getValue().toUpperCase().equals("POST")) {
				addCORSHeaders(request);
			}
		} else {
			addCORSHeaders();
		}
		
		return new EmptyRepresentation();
	}

	protected boolean verifyRequest(String userId, String token, String url) {
		AuthVerifier authClient = XMPPToolBox.getInstance().getAuthClient();

		return authClient.verifyRequest(userId, token, url);
	}
	
	@SuppressWarnings("unchecked")
	protected Series<Header> getMessageHeaders(Message message) {
        ConcurrentMap<String, Object> attrs = message.getAttributes();
        Series<Header> headers = (Series<Header>) attrs.get(HEADERS_KEY);
        
        if (headers == null) {
            headers = new Series<Header>(Header.class);
            Series<Header> prev = (Series<Header>) 
                attrs.putIfAbsent(HEADERS_KEY, headers);
            
            if (prev != null) { 
            	headers = prev; 
            }
        }
        
        return headers;
    }
	
	protected void addCORSHeaders() {
		addCORSHeaders(null);
	}
	
	protected void addCORSHeaders(Request request) {
		String origin = null;
		
		if (request != null) {
			Series<Header> messageHeaders = getMessageHeaders(request);
			Header originHeader = messageHeaders.getFirst(ORIGIN_HEADER);
			
			origin = originHeader != null ? originHeader.getValue() : null;
		}
		
		getMessageHeaders(getResponse()).add(CORS_ALLOW_HEADER, "Authorization, " +
				"Content-Type, X-Requested-With, XMLHttpRequest-specific");
		getMessageHeaders(getResponse()).add(CORS_ORIGIN_HEADER, (origin == null ? "*" : origin));
		getMessageHeaders(getResponse()).add(CORS_METHODS_HEADER, "GET, POST, PUT, DELETE");
		getMessageHeaders(getResponse()).add(CORS_CREDENTIALS_HEADER, "true");
	}

	protected Representation authenticationResponse() {
		List<ChallengeRequest> challengeRequests = new ArrayList<ChallengeRequest>();
		challengeRequests.add(new ChallengeRequest(ChallengeScheme.HTTP_BASIC,
				MediaServerApplication.REALM));

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
