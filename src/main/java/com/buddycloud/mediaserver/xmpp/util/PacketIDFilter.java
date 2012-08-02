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
