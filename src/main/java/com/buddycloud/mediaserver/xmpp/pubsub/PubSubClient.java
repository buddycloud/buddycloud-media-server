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
package com.buddycloud.mediaserver.xmpp.pubsub;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.RSMSet;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jivesoftware.smackx.pubsub.AffiliationsExtension;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.Node;
import org.jivesoftware.smackx.pubsub.NodeExtension;
import org.jivesoftware.smackx.pubsub.PubSubElementType;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.packet.PubSub;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;

import com.buddycloud.mediaserver.xmpp.pubsub.capabilities.CapabilitiesDecorator;

public class PubSubClient {

	private static Logger LOGGER = Logger.getLogger(PubSubClient.class);

	private List<PubSubManager> pubSubManagers = new LinkedList<PubSubManager>();

	public PubSubClient(Connection connection, String[] servers) {
		init(connection, servers);
	}

	private void init(Connection connection, String[] servers) {
		for (String server : servers) {
			PubSubManager pubSubManager = new PubSubManager(connection, server);
			pubSubManagers.add(pubSubManager);
		}

		Object affiliationsProvider = ProviderManager.getInstance()
				.getExtensionProvider(
						PubSubElementType.AFFILIATIONS.getElementName(),
						PubSubNamespace.BASIC.getXmlns());
		ProviderManager.getInstance().addExtensionProvider(
				PubSubElementType.AFFILIATIONS.getElementName(),
				PubSubNamespace.OWNER.getXmlns(), affiliationsProvider);

		Object affiliationProvider = ProviderManager.getInstance()
				.getExtensionProvider("affiliation",
						PubSubNamespace.BASIC.getXmlns());
		ProviderManager.getInstance().addExtensionProvider("affiliation",
				PubSubNamespace.OWNER.getXmlns(), affiliationProvider);
	}

	private Node getNode(String entityId) {
		Node node = null;
		for (PubSubManager manager : pubSubManagers) {
			try {
				node = manager.getNode("/user/" + entityId + "/posts");
			} catch (Exception e) {
				// do nothing
				continue;
			}
		}

		return node;
	}

	private Affiliation getAffiliation(Node node, String userId)
			throws XMPPException {

		PubSub request = node.createPubsubPacket(Type.GET, new NodeExtension(
				PubSubElementType.AFFILIATIONS, node.getId()),
				PubSubNamespace.OWNER);

		int itemCount = 0;
		while (true) {

			PubSub reply = (PubSub) node.sendPubsubPacket(Type.GET, request);

			AffiliationsExtension subElem = (AffiliationsExtension) reply
					.getExtension(
							PubSubElementType.AFFILIATIONS.getElementName(),
							PubSubNamespace.BASIC.getXmlns());

			List<Affiliation> affiliations = subElem.getAffiliations();

			for (Affiliation affiliation : affiliations) {
				if (affiliation.getNodeId().equals(userId)) {
					return affiliation;
				}
			}

			itemCount += affiliations.size();

			if (reply.getRsmSet() == null
					|| itemCount == reply.getRsmSet().getCount()) {
				break;
			}

			RSMSet rsmSet = new RSMSet();
			rsmSet.setAfter(reply.getRsmSet().getLast());
			request.setRsmSet(rsmSet);
		}

		return null;
	}

	public boolean matchUserCapability(String userId, String entityId,
			CapabilitiesDecorator capability) {
		Node node = getNode(entityId);

		if (node != null) {
			Affiliation affiliation = null;

			try {
				affiliation = getAffiliation(node, getBareId(userId));
			} catch (XMPPException e) {
				LOGGER.warn("Could not read node '" + node.getId() + "' "
						+ "affiliation for '" + userId + "'", e);

				return false;
			}

			if (affiliation != null) {
				return capability.isUserAllowed(affiliation.getType()
						.toString());
			}
		}

		return false;
	}

	public boolean isChannelPublic(String entityId) {
		Node node = getNode(entityId);

		if (node != null) {
			try {
				ConfigureForm config = node.getNodeConfiguration();

				return config.getAccessModel().equals(AccessModel.open);
			} catch (XMPPException e) {
				LOGGER.warn("Could not get node '" + node.getId() + "' "
						+ "access model", e);
			}
		}

		return false;
	}

	private String getBareId(String userId) {
		return userId.split("/")[0];
	}
}
