package com.buddycloud.mediaserver.xmpp;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.packet.HTTPAuth;
import org.jivesoftware.smackx.pubsub.packet.SyncPacketSend;

public class AuthVerifierClient {
	
	private static Logger LOGGER = Logger.getLogger(AuthVerifierClient.class);
	private XMPPConnection connection;

	
	public AuthVerifierClient(XMPPConnection connection) {
		this.connection = connection;
		createHandlers();
	}
	
	public boolean verifyUser(String userId, String token) {
		/*try {
			Packet reply = SyncPacketSend.getReply(connection, createVerifyTokenPacket(userId, token));
		} catch (XMPPException e) {
			LOGGER.warn("Error while verifying user '" + userId + "' request");
		}*/
		
		System.out.println(createVerifyTokenPacket(userId, token).toXML());
		
		return true;
	}
	
	private Packet createVerifyTokenPacket(String userId, String token) {
		HTTPAuth packet = new HTTPAuth(token);
		packet.setTo(userId);
		
		return packet;
	}

	private void createHandlers() {

	}
}
