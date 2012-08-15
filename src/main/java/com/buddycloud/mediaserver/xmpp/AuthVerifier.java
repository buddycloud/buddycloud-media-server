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

import org.apache.log4j.Logger;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;
import org.xmpp.packet.Packet;

import com.buddycloud.mediaserver.xmpp.util.HTTPAuthIQ;
import com.buddycloud.mediaserver.xmpp.util.SyncPacketSendUtil;

public class AuthVerifier {

	private static Logger LOGGER = Logger.getLogger(AuthVerifier.class);
	private MediaServerComponent component;

	public AuthVerifier(MediaServerComponent component) {
		this.component = component;
	}

	public boolean verifyRequest(String userId, String tid, String url) {
		try {
			IQ reply = SyncPacketSendUtil.getReply(component,
					createVerifyIQ(userId, tid, url));

			return reply.getType().equals(Type.result);
		} catch (Exception e) {
			LOGGER.warn("Error while verifying user '" + userId + "' request");
		}

		return false;
	}

	private Packet createVerifyIQ(String userId, String tid, String url) {
		HTTPAuthIQ packet = new HTTPAuthIQ(tid, url);
		packet.setTo(userId);

		return packet;
	}
}
