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

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.ext.crypto.DigestAuthenticator;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;

import com.buddycloud.mediaserver.commons.Constants;

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

        // GET /<name@domain.com>/<mediaID>/metadata
        router.attach(Constants.MEDIA_ID_METADATA_URL, MediaMetadataResource.class);

		// GET/PUT/DELETE /<name@domain.com>/<mediaID>
		router.attach(Constants.MEDIA_ID_URL, MediaResource.class);

		// GET/POST /<name@domain.com>
		router.attach(Constants.MEDIAS_URL, ChannelResource.class);

		ChallengeAuthenticator basicAuth = getBasicAuthenticator();
		basicAuth.setNext(router);

		DigestAuthenticator digestAuth = getDigestAuthenticator();
		digestAuth.setNext(basicAuth);

		return router;
	}

	protected DigestAuthenticator getDigestAuthenticator() {
		DigestAuthenticator auth = new DigestAuthenticator(getContext(), REALM,
				"secret");
		auth.setOptional(true);

		return auth;
	}

	protected ChallengeAuthenticator getBasicAuthenticator() {
		ChallengeAuthenticator auth = new ChallengeAuthenticator(getContext(),
				ChallengeScheme.HTTP_BASIC, REALM);
		auth.setOptional(false);

		return auth;
	}
}