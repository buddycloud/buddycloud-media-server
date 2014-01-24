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
package com.buddycloud.mediaserver.xmpp;

import com.buddycloud.mediaserver.xmpp.pubsub.PubSubClient;
import org.jivesoftware.smack.XMPPConnection;

public class XMPPToolBox {

	private AuthVerifier authClient;
	private PubSubClient pubSubClient;
	private boolean started;

	private static final XMPPToolBox instance = new XMPPToolBox();

	protected XMPPToolBox() {
		started = false;
	}

	public static XMPPToolBox getInstance() {
		return instance;
	}

	public void start(MediaServerComponent component,
			XMPPConnection connection) {
		if (!started) {
			authClient = new AuthVerifier(component);
			pubSubClient = new PubSubClient(connection);

			started = true;
		}
	}
	
	public void start(AuthVerifier authClient, PubSubClient pubSubClient) {
		if (!started) {
			this.authClient = authClient;
			this.pubSubClient = pubSubClient;

            started = true;
		}
	}

    public void stop() {
        if (started) {
            started = false;
        }
    }

	public AuthVerifier getAuthClient() {
		return authClient;
	}

	public PubSubClient getPubSubClient() {
		return pubSubClient;
	}
}
