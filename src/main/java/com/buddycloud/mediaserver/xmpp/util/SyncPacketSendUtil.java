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
