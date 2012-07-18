/*
 * Copyright 2011 buddycloud
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;
import org.jivesoftware.smackx.packet.RSMSet;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jivesoftware.smackx.pubsub.AffiliationsExtension;
import org.jivesoftware.smackx.pubsub.Node;
import org.jivesoftware.smackx.pubsub.NodeExtension;
import org.jivesoftware.smackx.pubsub.PubSubElementType;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.packet.PubSub;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;

import com.buddycloud.mediaserver.xmpp.pubsub.capabilities.CapabilitiesDecorator;

/**
 * Simply maintain a collection of {@link PubSubManager}, 
 * so different crawling strategies can use the same
 * node cache.
 * 
 */
public class PubSubController {
	
	private static Logger LOGGER = Logger.getLogger(PubSubController.class);
	//private static final String IDENTITY_CATEGORY = "pubsub";
	//private static final String IDENTITY_TYPE = "channels";

	private Map<String, PubSubManager> pubSubManagers = new HashMap<String, PubSubManager>();
	private Connection connection;
	
	
	public PubSubController(Connection connection) {
		this.connection = connection;
	}

	
	private PubSubManager getPubSubManager(String server) {
		PubSubManager pubSubManager = pubSubManagers.get(server);
		if (pubSubManager == null) {
			pubSubManager = discoverChannelServer(server);
			pubSubManagers.put(server, pubSubManager);
		}
		
		return pubSubManager;
	}

	private PubSubManager discoverChannelServer(String server) {
		/* TODO server discovery
		 * ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(connection);
		while (true) {
			DiscoverInfo discoInfo = discoManager.discoverInfo(server);
			
			Iterator<Identity> identities = discoInfo.getIdentities();
			while (identities.hasNext()) {
				Identity identity = identities.next();
				
				if (identity.getCategory().equals(IDENTITY_CATEGORY) 
						&& identity.getType().equals(IDENTITY_TYPE)) {
					
				}
				
			}
		}
		 */

		return new PubSubManager(connection, server);
	}
	
	
	
	private Node resolveNode(String entityId) {
		String server = entityId.split("@")[1];
		PubSubManager manager = getPubSubManager(server);
		
		DiscoverItems discoverNodes;
		
		try {
			discoverNodes = manager.discoverNodes(entityId);
		} catch (XMPPException e) {
			LOGGER.warn("Could not discover nodes from server [" + server + "]", e);
			
			return null;
		}

		final Iterator<Item> items = discoverNodes.getItems();
		
		while (items.hasNext()) {
			Item item = items.next();
			
			Node node = null;
			
			try {
				node = manager.getNode(item.getNode());
			} catch (Exception e) {
				LOGGER.warn("Could not read node [" + item.getNode() + "] "
						+ "from server [" + server + "]", e);
				
				continue;
			}
			
			if (node.getId().endsWith("/posts")) {
				return node;
			}
		}
		
		
		return null;
	}
	
	private List<Affiliation> getAffiliations(Node node) throws XMPPException {
		
		List<Affiliation> affiliations = new LinkedList<Affiliation>();
		
		PubSub request = node.createPubsubPacket(Type.GET, 
				new NodeExtension(PubSubElementType.AFFILIATIONS, node.getId()), 
				PubSubNamespace.OWNER);
		
		while (true) {
			
			PubSub reply = (PubSub) node.sendPubsubPacket(Type.GET, request);
			
			AffiliationsExtension subElem = (AffiliationsExtension) reply.getExtension(
					PubSubElementType.AFFILIATIONS.getElementName(), PubSubNamespace.BASIC.getXmlns());
			
			affiliations.addAll(subElem.getAffiliations());
			
			if (reply.getRsmSet() == null || 
					affiliations.size() == reply.getRsmSet().getCount()) {
				break;
			}
			
			RSMSet rsmSet = new RSMSet();
			rsmSet.setAfter(reply.getRsmSet().getLast());
			request.setRsmSet(rsmSet);
		}
		
		return affiliations;
	}
	
	public boolean matchUserCapability(String userId, String entityId, CapabilitiesDecorator capability) {
		Node node = resolveNode(entityId);
		
		if (node != null) {
			List<Affiliation> affiliations;

			try {
				affiliations = getAffiliations(node);
			} catch (XMPPException e) {
				LOGGER.warn("Could not read node [" + node.getId() + "] "
						+ "affiliations", e);
				
				return false;
			}
			
			for (Affiliation affiliation : affiliations) {
				if (affiliation.getNodeId().equals(userId)) {
					return capability.isUserAllowed(affiliation.getType().toString());
				}
			}
		}
		
		return false;
	}
}
