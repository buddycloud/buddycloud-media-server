
package com.buddycloud.mediaserver.xmpp.util;

import org.xmpp.packet.Packet;

public class PacketIDFilter implements MediaServerPacketFilter {

    private String packetID;
    

    public PacketIDFilter(String packetID) {
        if (packetID == null) {
            throw new IllegalArgumentException("Packet ID cannot be null.");
        }
        this.packetID = packetID;
    }
    

    public boolean accept(Packet packet) {
        return packetID.equals(packet.getID());
    }

    public String toString() {
        return "Packet filter by id: " + packetID;
    }
}
