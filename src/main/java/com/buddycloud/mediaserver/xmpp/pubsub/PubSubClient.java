/*
 * Copyright 2012-2014 buddycloud
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
import com.buddycloud.mediaserver.xmpp.util.AccessModel;
import com.buddycloud.mediaserver.xmpp.util.ConfigurationForm;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.smackx.rsm.RSMManager;
import org.jivesoftware.smackx.rsm.packet.RSMSet;
import org.jivesoftware.smackx.rsm.provider.RSMProvider;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.Node;
import org.jivesoftware.smackx.pubsub.PubSubElementType;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * XMPP client that handles PubSub (XEP-0060) operations.
 * @see <a href="http://xmpp.org/extensions/xep-0060.html">XEP-0060</a>
 * @author Rodrigo Duarte Sousa - rodrigodsousa@gmail.com
 *
 */
public class PubSubClient {
	static {
		RSMProvider.registerWithProviderManager();
	}

    private static final String IDENTITY_CATEGORY = "pubsub";
    private static final String IDENTITY_TYPE = "channels";

	private static Logger LOGGER = LoggerFactory.getLogger(PubSubClient.class);

    private Map<String, PubSubManager> pubSubManagersCache = new HashMap<String, PubSubManager>();
    private Map<String, String> serversCache = new HashMap<String, String>();
    private XMPPConnection connection;


	public PubSubClient(XMPPConnection connection) {
        this.connection = connection;
		init();
	}

	private void init() {
		// TODO This appears to be a workaround for a bug of Smack's PubSub
		// implementation and should be reported upstream
		Object affiliationsProvider = ProviderManager
				.getExtensionProvider(
						PubSubElementType.AFFILIATIONS.getElementName(),
						PubSubNamespace.BASIC.getXmlns());
		ProviderManager.addExtensionProvider(
				PubSubElementType.AFFILIATIONS.getElementName(),
				PubSubNamespace.OWNER.getXmlns(), affiliationsProvider);

		Object affiliationProvider = ProviderManager
				.getExtensionProvider("affiliation",
						PubSubNamespace.BASIC.getXmlns());
		ProviderManager.addExtensionProvider("affiliation",
				PubSubNamespace.OWNER.getXmlns(), affiliationProvider);
	}

	private Node getNode(String entityId) {
        JID entityJID = new JID(entityId);
        String serverAddress = getChannelServerAddress(entityJID.getDomain());
        Node node = null;
        if (serverAddress != null) {
            PubSubManager manager = pubSubManagersCache.get(serverAddress);
            if (manager == null) {
                manager = new PubSubManager(connection, serverAddress);
                pubSubManagersCache.put(serverAddress, manager);
            }

            try {
                LOGGER.debug("Getting " + entityId + " node at channel server [" + serverAddress + "]");
                node = manager.getNode("/user/" + entityId + "/posts");
            } catch (Exception e) {
                LOGGER.error("Error while getting " + entityId + "node", e);
            }
        }

        return node;
	}

    private boolean isChannelServerIdentity(DiscoverInfo.Identity identity) {
        return identity.getCategory().equals(IDENTITY_CATEGORY) && identity.getType().equals(IDENTITY_TYPE);
    }

    private String discoverDomainServer(String domain) {
        ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(connection);
        PubSubManager pubSubManager = new PubSubManager(connection, domain);

        DiscoverItems discoverItems;
        try {
            LOGGER.debug("Discover nodes for domain [" + domain + "]");
            discoverItems = pubSubManager.discoverNodes(null);
        } catch (Exception e) {
            LOGGER.error("Error while trying to fetch domain [" + domain + "] node", e);
            return null;
        }

        for (DiscoverItems.Item item : discoverItems.getItems()) {
            String entityID = item.getEntityID();
            DiscoverInfo discoverInfo;
            try {
                LOGGER.debug("Discover identities for entity [" + entityID + "]");
                discoverInfo = discoManager.discoverInfo(entityID);
            } catch (Exception e) {
                LOGGER.error("Error while trying to fetch [" + entityID + "] identities");
                continue;
            }

            for (DiscoverInfo.Identity identity : discoverInfo.getIdentities()) {
                if (isChannelServerIdentity(identity)) {
                    return entityID;
                }
            }
        }

        return null;
    }

    private String getChannelServerAddress(String domain) {
        String serverAddress = serversCache.get(domain);
        if (serverAddress == null) {
            LOGGER.debug("Server cache doesn't contains the channel server for domain [" + domain + "]");
            serverAddress = discoverDomainServer(domain);

            if (serverAddress != null) {
                LOGGER.debug("Channel server for domain [" + domain + "] discovered: " + serverAddress);
                serversCache.put(domain, serverAddress);
            }
        }

        return serverAddress;
    }

	private Affiliation getAffiliation(Node node, String userBareJID)
			throws XMPPException, NoResponseException, NotConnectedException {

        int itemCount = 0;
        // Limit to 10 results per query if possible
        RSMSet rsmSet = new RSMSet(10);
        List<PacketExtension> additionalExtensions = Collections.singletonList((PacketExtension) rsmSet);
        while (true) {
            List<PacketExtension> returnedExtensions = new LinkedList<PacketExtension>();
            List<Affiliation> affiliations = node.getAffiliations(additionalExtensions, returnedExtensions);

            for (Affiliation affiliation : affiliations) {
                if (affiliation.getNodeId().equals(userBareJID)) {
                    return affiliation;
                }
            }

            itemCount += affiliations.size();

            RSMSet replyRSMSet = RSMManager.getRSMSet(returnedExtensions);
            if (replyRSMSet == null
                    || itemCount == replyRSMSet.getCount()) {
				// If there was no rsmSet extension in the reply, then the
				// server does not support RSM and returned all affiliations and
				// we are done here.
                break;
            }

            rsmSet.setAfter(replyRSMSet.getLast());
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
        String userBareJID = new JID(userJID).toBareJID();

		// Workaround for #86 (The channel's owner is sometimes not part of <affiliations/>) buddycloud-server issue
		if (userBareJID.equals(entityId)) {
			return true;
		}
		
		Node node = getNode(entityId);
		if (node != null) {
			Affiliation affiliation;

			try {
                LOGGER.debug("Getting " + userBareJID + " affiliation for node [" + node.getId() + "]");
				affiliation = getAffiliation(node, userBareJID);
			} catch (Exception e) {
				LOGGER.warn("Could not read node '" + node.getId()
						+ " affiliation for '" + userBareJID + "'", e);

				return false;
			}

            LOGGER.debug(userBareJID + " affiliation: " + affiliation.getType().toString());

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
	 * @return if {@code entityId} is public.
	 */
	public boolean isChannelPublic(String entityId) {
		Node node = getNode(entityId);

		if (node != null) {
			try {
				ConfigureForm config = node.getNodeConfiguration();
				ConfigurationForm form = new ConfigurationForm(config);
				return form.getBuddycloudAccessModel().equals(AccessModel.open);
			} catch (Exception e) {
				LOGGER.warn("Could not get node '" + node.getId() + "' "
						+ "access model", e);
			}
		}

		return false;
	}
}
