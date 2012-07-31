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
			IQ reply = SyncPacketSendUtil.getReply(component, createVerifyIQ(userId, tid, url));
			
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
