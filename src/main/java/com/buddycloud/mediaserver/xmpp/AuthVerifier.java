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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;
import org.xmpp.packet.Packet;

import com.buddycloud.mediaserver.xmpp.util.HTTPAuthIQ;
import com.buddycloud.mediaserver.xmpp.util.SyncPacketSendUtil;

/**
 * Authentication class that uses an XMPP component to 
 * handle the XEP-0070 implementation.
 * @see <a href="http://xmpp.org/extensions/xep-0070.html">XEP-0070</a>
 * @author Rodrigo Duarte Sousa - rodrigodsousa@gmail.com
 *
 */
public class AuthVerifier {

	private static Logger LOGGER = LoggerFactory.getLogger(AuthVerifier.class);
	private MediaServerComponent component;

	public AuthVerifier(MediaServerComponent component) {
		this.component = component;
	}

	/**
	 * Verifies if the client has really sent this request.
	 * @param userId client's user id.
	 * @param tid transaction id provided by the client.
	 * @param url URL that the client is trying to access.
	 * @return if the client has really sent the request.
	 */
	public boolean verifyRequest(String userId, String tid, String url) {
		try {
			IQ reply = SyncPacketSendUtil.getReply(component,
					createVerifyIQ(userId, tid, url));

			return reply.getType().equals(Type.result);
		} catch (Exception e) {
			LOGGER.warn("Error while verifying user '" + userId + "' request", e);
		}

		return false;
	}

	private Packet createVerifyIQ(String userId, String tid, String url) {
		HTTPAuthIQ packet = new HTTPAuthIQ(tid, url);
		packet.setTo(userId);

		return packet;
	}
}
