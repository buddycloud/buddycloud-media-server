
package com.buddycloud.mediaserver.xmpp.util;

import org.xmpp.packet.Packet;

public interface MediaServerPacketFilter {

    public boolean accept(Packet packet);
}
