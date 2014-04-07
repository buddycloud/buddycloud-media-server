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
package com.buddycloud.mediaserver.xmpp;

import com.buddycloud.mediaserver.commons.MediaServerConfiguration;
import com.buddycloud.mediaserver.xmpp.util.MediaServerPacketCollector;
import com.buddycloud.mediaserver.xmpp.util.MediaServerPacketFilter;

import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.packet.DataForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xmpp.component.AbstractComponent;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Media Server XMPP Component Follows the XEP-0114
 * @see (http://xmpp.org/extensions/xep-0114.html)
 * 
 * @author Rodrigo Duarte Sousa - rodrigodsousa@gmail.com
 */
public class MediaServerComponent extends AbstractComponent {

	public static final String DESCRIPTION = "An XMPP Media Server";
	public static final String NAME = "Media Server";
	private static final Logger LOGGER = LoggerFactory.getLogger(MediaServerComponent.class);

	public static final String NS_DATAFORM = "jabber:x:data";

	protected final Collection<MediaServerPacketCollector> collectors = new ConcurrentLinkedQueue<MediaServerPacketCollector>();
	private Properties configuration;

	public MediaServerComponent(Properties configuration) {
		this.configuration = configuration;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	public void sendPacket(Packet arg0) {
		arg0.setFrom(getJID());
		LOGGER.debug("S: " + arg0.toXML());
		super.send(arg0);
	}

	@Override
	protected void handleIQResult(IQ iq) {
		collectPacket(iq);
	}

	@Override
	protected void handleIQError(IQ iq) {
		collectPacket(iq);
	}

	private void collectPacket(Packet packet) {
		LOGGER.debug("R: " + packet.toXML());
		for (MediaServerPacketCollector packetCollector : collectors) {
			packetCollector.processPacket(packet);
		}
	}
	
	@Override
	protected void handleMessage(Message message) {
		collectPacket(message);
	}

	public void removePacketCollector(MediaServerPacketCollector packetCollector) {
		collectors.remove(packetCollector);
	}

	public MediaServerPacketCollector createPacketCollector(
			MediaServerPacketFilter filter) {
		MediaServerPacketCollector collector = new MediaServerPacketCollector(
				this, filter);
		// Add the collector to the list of active collectors.
		collectors.add(collector);
		return collector;
	}

    @Override
	protected IQ handleDiscoInfo(IQ iq) {
		final IQ replyPacket = IQ.createResultIQ(iq);

		final Element responseElement = replyPacket.setChildElement("query",
				NAMESPACE_DISCO_INFO);

		// identity
		responseElement.addElement("identity").addAttribute("category",
				discoInfoIdentityCategory()).addAttribute("type",
				discoInfoIdentityCategoryType())
				.addAttribute("name", getName());
		// features
		responseElement.addElement("feature").addAttribute("var",
				NAMESPACE_DISCO_INFO);
		responseElement.addElement("feature").addAttribute("var",
				NAMESPACE_XMPP_PING);
		responseElement.addElement("feature").addAttribute("var",
				NAMESPACE_LAST_ACTIVITY);
		responseElement.addElement("feature").addAttribute("var",
				NAMESPACE_ENTITY_TIME);
		for (final String feature : discoInfoFeatureNamespaces()) {
			responseElement.addElement("feature").addAttribute("var", feature);
		}
		
		addHttpEndPointDetails(responseElement);
		return replyPacket;
	}
        
    protected void addHttpEndPointDetails(Element responseElement) {
    	String endPoint = configuration.getProperty(MediaServerConfiguration.HTTP_ENDPOINT);
		if (endPoint == null) {
			return;
		}

		Element x = responseElement.addElement("x");			
		x.setAttribute("xmlns", NS_DATAFORM);
		x.setAttribute("type", "result");

		Element formTypeField = x.addElement("field");
		formTypeField.setAttribute("var", "FORM_TYPE");
		formTypeField.setAttribute("type", FormField.TYPE_HIDDEN);
		formTypeField.addElement("value").addText(MediaServerConfiguration.BUDDYCLOUD_NS_API);

		Element endPointField = x.addElement("field");
		endPointField.setAttribute("var", MediaServerConfiguration.API_ENDPOINT_FIELD_VAR);
		endPointField.setAttribute("type", FormField.TYPE_TEXT_SINGLE);
		endPointField.addElement("value").addText(endPoint);

    }

}
