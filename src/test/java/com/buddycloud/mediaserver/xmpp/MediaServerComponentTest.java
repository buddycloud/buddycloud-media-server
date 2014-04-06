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
import com.buddycloud.mediaserver.xmpp.Element;
import com.buddycloud.mediaserver.xmpp.MediaServerComponent;

import org.dom4j.io.Document;
import org.dom4j.io.DocumentFactory;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.packet.DataForm;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;

public abstract class MediaServerComponentTest {
    
	IQ discoRequest = null;
	String packetId = "12345";
	String to = "mediaserver.example.com";
	String from = "me@example.com/media-lover";
	
	@Before
	public void setup() {
		// TODO generate a disco info request
		discoRequest = (IQ) new Packet() {
			
			@Override
			public String toXML() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		discoRequest.setTo(to);
		discoRequest.setFrom(from);
		discoRequest.setPacketID(packetId);
	}
	
	@Test
	public void withoutApiEndPointInConfigurationStandardDiscoResponse() throws Exception {
		
		MediaServerComponent component = new MediaServerComponent(new Properties());
		component.collectPacket(discoRequest);
		
		// Check for standard XMPP library inclusions
		
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
		*/
	}
	
	@Test
	public void withApiEndPointInConfigurationServiceDiscoveryExtensionIncluded() throws Exception {
		String endPoint = "https://api.buddycloud.com";
		Properties properties = new Properties();
		properties.put(MediaServerConfiguration.API_ENDPOINT, endPoit);
	}
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