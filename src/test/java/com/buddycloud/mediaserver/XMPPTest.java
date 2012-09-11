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
package com.buddycloud.mediaserver;

import java.util.Properties;

import org.easymock.EasyMock;

import com.buddycloud.mediaserver.xmpp.AuthVerifier;
import com.buddycloud.mediaserver.xmpp.XMPPToolBox;
import com.buddycloud.mediaserver.xmpp.pubsub.PubSubClient;

public class XMPPTest implements TextExtension {
	
	private PubSubClient pubSubClient;
	private AuthVerifier authClient;
	private boolean started;

	public XMPPTest() {
		this.authClient = EasyMock.createMock(AuthVerifier.class);
		this.pubSubClient = EasyMock.createMock(PubSubClient.class);
		this.started = false;
		XMPPToolBox.getInstance().start(authClient, pubSubClient);
	}

	public void start(Properties configuration) throws Exception {
		if (!started) {
			EasyMock.reset(authClient);
			EasyMock.reset(pubSubClient);
			started = true;
		}
	}
	
	@Override
	public void shutdown() throws Exception {
		if (started) {
			EasyMock.verify(authClient);
			EasyMock.verify(pubSubClient);
			started = false;
		}
	}
	
	public PubSubClient getPubSubClient() {
		return this.pubSubClient;
	}
	
	public AuthVerifier getAuthVerifier() {
		return this.authClient;
	}
}
