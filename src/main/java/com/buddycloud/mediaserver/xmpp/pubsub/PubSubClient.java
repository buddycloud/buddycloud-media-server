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

import com.buddycloud.mediaserver.business.util.PubSubManagerFactory;
import com.buddycloud.mediaserver.commons.MediaServerConfiguration;
import com.buddycloud.mediaserver.xmpp.pubsub.capabilities.CapabilitiesDecorator;
import com.buddycloud.mediaserver.xmpp.util.AccessModel;
import com.buddycloud.mediaserver.xmpp.util.ConfigurationForm;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.RSMSet;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jivesoftware.smackx.pubsub.AffiliationsExtension;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.Node;
import org.jivesoftware.smackx.pubsub.NodeExtension;
import org.jivesoftware.smackx.pubsub.PubSubElementType;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.packet.PubSub;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * XMPP client that handles PubSub (XEP-0060) operations.
 * 
 * @see <a href="http://xmpp.org/extensions/xep-0060.html">XEP-0060</a>
 * @author Rodrigo Duarte Sousa - rodrigodsousa@gmail.com
 * 
 */
public class PubSubClient {
	private static final String IDENTITY_CATEGORY = "pubsub";
	private static final String IDENTITY_TYPE = "channels";

	private static Logger LOGGER = LoggerFactory.getLogger(PubSubClient.class);

	private Map<String, PubSubManager> pubSubManagersCache = new HashMap<String, PubSubManager>();
	private Map<String, String> serversCache = new HashMap<String, String>();
	private Connection connection;
	private Properties configuration;
	private PubSubManagerFactory pubsubManagerFactory;

	public PubSubClient(Connection connection, Properties configuration,
			PubSubManagerFactory factory) {
		this.connection = connection;
		this.configuration = configuration;
		if (null != factory) {
			setPubSubManagerFactory(factory);
		}
		init();
	}

	public PubSubClient(Connection connection, Properties configuration) {
		this(connection, configuration, null);
	}

	private PubSubManagerFactory getPubSubManagerFactory() {
		if (null == pubsubManagerFactory) {
			pubsubManagerFactory = new PubSubManagerFactory(connection);
		}
		return pubsubManagerFactory;
	}

	public void setPubSubManagerFactory(PubSubManagerFactory factory) {
		this.pubsubManagerFactory = factory;
	}

	private void init() {
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

		getChannelServerAddress(configuration
				.getProperty(MediaServerConfiguration.XMPP_CONNECTION_SERVICENAME));
	}

	private Node getNode(String entityId) {
		JID entityJID = new JID(entityId);
		String serverAddress = getChannelServerAddress(entityJID.getDomain());
		Node node = null;
		if (serverAddress != null) {
			PubSubManager manager = getPubSubManagerFactory().create(
					serverAddress);
			if (manager == null) {
				manager = new PubSubManager(connection, serverAddress);
				pubSubManagersCache.put(serverAddress, manager);
			}

			try {
				LOGGER.debug("Getting " + entityId
						+ " node at channel server [" + serverAddress + "]");
				node = manager.getNode("/user/" + entityId + "/posts");
			} catch (XMPPException e) {
				LOGGER.error("Error while getting " + entityId + "node", e);
			}
		}

		return node;
	}

	private boolean isChannelServerIdentity(DiscoverInfo.Identity identity) {
		return identity.getCategory().equals(IDENTITY_CATEGORY)
				&& identity.getType().equals(IDENTITY_TYPE);
	}

	private String discoverDomainServer(String domain) {
		ServiceDiscoveryManager discoManager = ServiceDiscoveryManager
				.getInstanceFor(connection);
		PubSubManager pubSubManager = getPubSubManagerFactory().create(domain);

		DiscoverItems discoverItems;
		try {
			LOGGER.debug("Discover nodes for domain [" + domain + "]");
			discoverItems = pubSubManager.discoverNodes(null);
		} catch (XMPPException e) {
			LOGGER.error("Error while trying to fetch domain [" + domain
					+ "] node", e);
			return null;
		}

		Iterator<DiscoverItems.Item> items = discoverItems.getItems();
		while (items.hasNext()) {
			String entityID = items.next().getEntityID();
			DiscoverInfo discoverInfo;
			try {
				LOGGER.debug("Discover identities for entity [" + entityID
						+ "]");
				discoverInfo = discoManager.discoverInfo(entityID);
			} catch (XMPPException e) {
				LOGGER.error("Error while trying to fetch [" + entityID
						+ "] identities");
				continue;
			}

			Iterator<DiscoverInfo.Identity> identities = discoverInfo
					.getIdentities();
			while (identities.hasNext()) {
				if (isChannelServerIdentity(identities.next())) {
					return entityID;
				}
			}
		}

		return null;
	}

	private String getChannelServerAddress(String domain) {
		String serverAddress = serversCache.get(domain);
		if (serverAddress == null) {
			LOGGER.debug("Server cache doesn't contains the channel server for domain ["
					+ domain + "]");
			serverAddress = discoverDomainServer(domain);

			if (serverAddress != null) {
				LOGGER.debug("Channel server for domain [" + domain
						+ "] discovered: " + serverAddress);
				serversCache.put(domain, serverAddress);
			}
		}

		return serverAddress;
	}

	private Affiliation getAffiliation(Node node, String userBareJID)
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
				if (affiliation.getNodeId().equals(userBareJID)) {
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
	 * Verifies if an user has the desired capability (member, moderator, owner
	 * or publisher).
	 * 
	 * @param userJID
	 *            the user Jabber Id.
	 * @param entityId
	 *            channel id.
	 * @param capability
	 *            decorator that represents the desired capabilities.
	 * @return if the user has the desired capabilities.
	 */
	public boolean matchUserCapability(String userJID, String entityId,
			CapabilitiesDecorator capability) {
		String userBareJID = new JID(userJID).toBareJID();

		// Workaround for #86 (The channel's owner is sometimes not part of
		// <affiliations/>) buddycloud-server issue
		if (userBareJID.equals(entityId)) {
			return true;
		}

		Node node = getNode(entityId);
		if (node != null) {
			Affiliation affiliation;

			try {
				LOGGER.debug("Getting " + userBareJID
						+ " affiliation for node [" + node.getId() + "]");
				affiliation = getAffiliation(node, userBareJID);
			} catch (XMPPException e) {
				LOGGER.warn("Could not read node '" + node.getId()
						+ " affiliation for '" + userBareJID + "'", e);

				return false;
			}

			if (affiliation != null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(userBareJID + " affiliation: "
							+ affiliation.getType());
				}
				return capability.isUserAllowed(affiliation.getType()
						.toString());
			}
		}

		return false;
	}

	/**
	 * Verifies if a channel is public.
	 * 
	 * @param entityId
	 *            channel to be verified.
	 * @return if
	 * @param entityId
	 *            is public.
	 */
	public boolean isChannelPublic(String entityId) {
		Node node = getNode(entityId);

		if (node != null) {
			try {
				ConfigureForm config = node.getNodeConfiguration();
				ConfigurationForm form = new ConfigurationForm(config);
				return form.getBuddycloudAccessModel().equals(AccessModel.open);
			} catch (XMPPException e) {
				LOGGER.warn("Could not get node '" + node.getId() + "' "
						+ "access model", e);
			}
		}

		return false;
	}
}
