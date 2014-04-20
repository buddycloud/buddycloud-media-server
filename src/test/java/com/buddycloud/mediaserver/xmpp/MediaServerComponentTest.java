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

import java.util.Properties;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.junit.Before;
import org.junit.Test;

import com.buddycloud.mediaserver.commons.MediaServerConfiguration;

public abstract class MediaServerComponentTest {
    
	IQ discoRequest = null;
	final String packetId = "12345-67890-09876-54321";
	final String to = "mediaserver.example.com";
	final String from = "me@example.com/media-lover";
	
	@Before
	public void setup() {
		IQ request = new DiscoverInfo();
		request.setTo(to);
		request.setFrom(from);
		request.setPacketID(packetId);
		
		discoRequest = request;
	}
	
	@Test
	public void withoutApiEndPointInConfigurationStandardDiscoResponse() throws Exception {
		
		MediaServerComponent component = new MediaServerComponent(new Properties());
		component.collectPacket(discoRequest);
		
		// Check for standard XMPP library inclusions

		  final Element responseElement = replyPacket.setChildElement("query",
				NAMESPACE_DISCO_INFO);

			// identity
			responseElement.addElement("identity").addAttribute("category",
				discoInfoIdentityCategory()).addAttribute("type",
				discoInfoIdentityCategoryType())
				.addAttribute("name", getName());
		AbstractComponent.NAMESPACE_DISCO_INFO;
		AbstractComponent.NAMESPACE_XMPP_PING;  (feature.var)
		AbstractComponent.NAMESPACE_LAST_ACTIVITY; (feature.var)
		AbstractComponent.NAMESPACE_ENTITY_TIME; (feature.var)

	}
	
	@Test
	public void withApiEndPointInConfigurationServiceDiscoveryExtensionIncluded() throws Exception {
		String endPoint = "https://api.buddycloud.com";
		Properties properties = new Properties();
		properties.put(MediaServerConfiguration.HTTP_ENDPOINT, endPoint);
	
		MediaServerComponent component = new MediaServerComponent(properties);
		
		
		
		component.collectPacket(discoRequest);
		
		// Check for standard XMPP library inclusions + service discovery extensions
		/*
		  final Element responseElement = replyPacket.setChildElement("query",
				NAMESPACE_DISCO_INFO);

			// identity
			responseElement.addElement("identity").addAttribute("category",
				discoInfoIdentityCategory()).addAttribute("type",
				discoInfoIdentityCategoryType())
				.addAttribute("name", getName());
		AbstractComponent.NAMESPACE_DISCO_INFO;
		AbstractComponent.NAMESPACE_XMPP_PING; (feature.var)
		AbstractComponent.NAMESPACE_LAST_ACTIVITY; (feature.var)
		AbstractComponent.NAMESPACE_ENTITY_TIME; (feature.var)
		
		x.FORM_TYPE - MediaServerConfiguration.BUDDYCLOUD_NS_API
		x. MediaServerConfiguration.API_ENDPOINT_FIELD_VAR = endPoint
		*/
	}
	
}