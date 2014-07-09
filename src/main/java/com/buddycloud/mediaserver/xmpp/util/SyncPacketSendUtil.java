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
package com.buddycloud.mediaserver.xmpp.util;

import org.dom4j.Element;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.xmpp.packet.Packet;

import com.buddycloud.mediaserver.xmpp.MediaServerComponent;

public final class SyncPacketSendUtil {
	private SyncPacketSendUtil() {
	}

	public static Packet getReply(MediaServerComponent component, Packet packet,
			long timeout) throws Exception {
		MediaServerPacketFilter responseFilter = new TransactionIDFilter(
				getTransactionId(packet));
		MediaServerPacketCollector response = component
				.createPacketCollector(responseFilter);

		component.sendPacket(packet);

		// Wait up to a certain number of seconds for a reply.
		Packet result = response.nextResult(timeout);

		// Stop queuing results
		response.cancel();

		if (result == null) {
			throw new SmackException.NoResponseException();
		} else if (result.getError() != null) {
			throw new Exception(result.getError().getText());
		}
		return result;
	}

	private static String getTransactionId(Packet packet) {
		Element confirmEl = packet.getElement().element("confirm");
		return confirmEl.attributeValue("id");
	}

	public static Packet getReply(MediaServerComponent component, Packet packet)
			throws Exception {
		return getReply(component, packet,
				SmackConfiguration.getDefaultPacketReplyTimeout());
	}
}
