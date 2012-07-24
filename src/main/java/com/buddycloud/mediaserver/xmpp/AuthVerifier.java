package com.buddycloud.mediaserver.xmpp;

import org.apache.log4j.Logger;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;
import org.xmpp.packet.Packet;

import com.buddycloud.mediaserver.xmpp.util.HTTPAuthPacket;
import com.buddycloud.mediaserver.xmpp.util.SyncPacketSendUtil;

public class AuthVerifier {
	
	private static Logger LOGGER = Logger.getLogger(AuthVerifier.class);
	private MediaServerComponent component;

	
	public AuthVerifier(MediaServerComponent component) {
		this.component = component;
	}
	
	public boolean verifyUser(String userId, String token, String url) {
		try {
			IQ reply = SyncPacketSendUtil.getReply(component, createVerifyTokenPacket(userId, token, url));
			
			return reply.getType().equals(Type.result);
		} catch (Exception e) {
			LOGGER.warn("Error while verifying user '" + userId + "' request");
		}
		
		return true;
	}
	
	private Packet createVerifyTokenPacket(String userId, String token, String url) {
		HTTPAuthPacket packet = new HTTPAuthPacket(token, url);
		packet.setTo(userId);
		
		return packet;
	}
}
