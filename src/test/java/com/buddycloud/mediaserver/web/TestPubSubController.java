/*
 * Copyright 2011 buddycloud
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

import org.jivesoftware.smack.Connection;

import com.buddycloud.mediaserver.xmpp.pubsub.PubSubController;
import com.buddycloud.mediaserver.xmpp.pubsub.capabilities.CapabilitiesDecorator;

public class TestPubSubController extends PubSubController {
	
	
	public TestPubSubController(Connection connection) {
		super(connection);
	}
	
	
	public boolean matchUserCapability(String userId, String entityId, CapabilitiesDecorator capability) {
		return true;
	}
}
