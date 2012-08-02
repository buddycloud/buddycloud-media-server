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

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Packet;

import com.buddycloud.mediaserver.xmpp.MediaServerComponent;

public final class SyncPacketSendUtil {
	private SyncPacketSendUtil() {}
	
	public static IQ getReply(MediaServerComponent component, Packet packet, long timeout) throws Exception {
        MediaServerPacketFilter responseFilter = new PacketIDFilter(packet.getID());
        MediaServerPacketCollector response = component.createPacketCollector(responseFilter);
        
        component.sendPacket(packet);

        // Wait up to a certain number of seconds for a reply.
        IQ result = response.nextResult(timeout);

        // Stop queuing results
        response.cancel();

        if (result == null) {
            throw new XMPPException("No response from client '" + packet.getTo() + "'");
        } else if (result.getError() != null) {
            throw new Exception(result.getError().getText());
        }
        return result;
	}

	public static IQ getReply(MediaServerComponent component, Packet packet) throws Exception {
		return getReply(component, packet, SmackConfiguration.getPacketReplyTimeout());
	}
}
