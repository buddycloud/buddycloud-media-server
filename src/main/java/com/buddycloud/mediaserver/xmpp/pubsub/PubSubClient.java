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

import com.buddycloud.mediaserver.xmpp.pubsub.capabilities.CapabilitiesDecorator;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.RSMSet;
import org.jivesoftware.smackx.pubsub.*;
import org.jivesoftware.smackx.pubsub.packet.PubSub;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;

import java.util.LinkedList;
import java.util.List;

/**
 * XMPP client that handles PubSub (XEP-0060) operations.
 * @see <a href="http://xmpp.org/extensions/xep-0060.html">XEP-0060</a>
 * @author Rodrigo Duarte Sousa - rodrigodsousa@gmail.com
 *
 */
public class PubSubClient {

	private static Logger LOGGER = LoggerFactory.getLogger(PubSubClient.class);

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

	private Affiliation getAffiliation(Node node, String userJID)
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
				if (affiliation.getNodeId().equals(userJID)) {
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

	/**
	 * Verifies if an user has the desired capability (member, moderator,
	 * owner or publisher).
	 * @param userJID the user Jabber Id.
	 * @param entityId channel id.
	 * @param capability decorator that represents the desired capabilities.
	 * @return if the user has the desired capabilities.
	 */
	public boolean matchUserCapability(String userJID, String entityId,
			CapabilitiesDecorator capability) {
		String bareId = new JID(userJID).toBareJID();
		
		//workaround for #86 (The channel's owner is sometimes not part of <affiliations/>) buddycloud-server issue
		if (bareId.equals(entityId)) {
			return true;
		}
		
		Node node = getNode(entityId);
		if (node != null) {
			Affiliation affiliation;

			try {
				affiliation = getAffiliation(node, bareId);
			} catch (XMPPException e) {
				LOGGER.warn("Could not read node '" + node.getId() + "' "
						+ "affiliation for '" + userJID + "'", e);

				return false;
			}

			if (affiliation != null) {
				return capability.isUserAllowed(affiliation.getType()
						.toString());
			}
		}

		return false;
	}

	/**
	 * Verifies if a channel is public.
	 * @param entityId channel to be verified.
	 * @return if {@param entityId} is public.
	 */
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
}
