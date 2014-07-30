/*
 * Copyright © 2014 Florian Schmaus
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
package org.jivesoftware.smackx.rsm;

import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.rsm.packet.RSMSet;

public class RSMManager extends Manager {

	private static final Map<XMPPConnection, RSMManager> INSTANCES = new WeakHashMap<XMPPConnection, RSMManager>();

	public static synchronized RSMManager getInstanceFor(XMPPConnection connection) {
		RSMManager rsmManager = INSTANCES.get(connection);
		if (rsmManager == null) {
			rsmManager = new RSMManager(connection);
			INSTANCES.put(connection, rsmManager);
		}
		return rsmManager;
	}

	private RSMManager(XMPPConnection connection) {
		super(connection);
		ServiceDiscoveryManager.getInstanceFor(connection).addFeature(RSMSet.NAMESPACE);
	}

	public boolean isSupported(String jid) throws NoResponseException, XMPPErrorException, NotConnectedException {
		return ServiceDiscoveryManager.getInstanceFor(connection()).supportsFeature(jid, RSMSet.NAMESPACE);
	}

	public static RSMSet getRSMSet(Packet packet) {
		return (RSMSet) packet.getExtension(RSMSet.ELEMENT, RSMSet.NAMESPACE);
	}

	public static RSMSet getRSMSet(Collection<PacketExtension> packetExtensions) {
		for (PacketExtension packetExtension : packetExtensions) {
			if (packetExtension.getNamespace().equals(RSMSet.NAMESPACE)
					&& packetExtension.getElementName().equals(RSMSet.ELEMENT)) {
				return (RSMSet) packetExtension;
			}
		}
		return null;
	}
}
