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

import java.util.List;
import java.util.Properties;

import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmpp.component.AbstractComponent;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

import com.buddycloud.mediaserver.commons.MediaServerConfiguration;

public class MediaServerComponentTest {
    
	private static final String DISCO_INFO_NS = "http://jabber.org/protocol/disco#info";
	
	IQ discoRequest = null;
	final String packetId = "12345-67890-09876-54321";
	final String to = "mediaserver.example.com";
	final String from = "me@example.com/media-lover";
	
	@Before
	public void setup() {
//		<query xmlns='http://jabber.org/protocol/disco#info'/>
		IQ request = new IQ();
		request.setTo(to);
		request.setFrom(from);
		request.setID(packetId);
		request.getElement().addElement("query", DISCO_INFO_NS);
		discoRequest = request;
	}
	
	@Test
	public void withoutApiEndPointInConfigurationStandardDiscoResponse() throws Exception {
		
		ComponentManagerMock cm = new ComponentManagerMock();
		
		MediaServerComponent component = new MediaServerComponent(new Properties());
		component.initialize(new JID(to), cm);
		component.processPacket(discoRequest);
		
		Packet replyPacket = cm.collectPacket();
		
		Element queryEl = replyPacket.getElement().element("query");
		Assert.assertEquals(AbstractComponent.NAMESPACE_DISCO_INFO, queryEl.getNamespaceURI());
		
		Element identityEl = queryEl.element("identity");
		Assert.assertEquals("component", identityEl.attributeValue("category"));
		Assert.assertEquals("Media Server", identityEl.attributeValue("name"));
		Assert.assertEquals("generic", identityEl.attributeValue("type"));
		
		Assert.assertTrue(hasFeature(AbstractComponent.NAMESPACE_DISCO_INFO, queryEl));
		Assert.assertTrue(hasFeature(AbstractComponent.NAMESPACE_XMPP_PING, queryEl));
		Assert.assertTrue(hasFeature(AbstractComponent.NAMESPACE_LAST_ACTIVITY, queryEl));
		Assert.assertTrue(hasFeature(AbstractComponent.NAMESPACE_ENTITY_TIME, queryEl));
		
		Assert.assertNull(queryEl.element("x"));
	}
	
	@SuppressWarnings("unchecked")
	private static boolean hasFeature(String var, Element queryEl) {
		List<Element> features = queryEl.elements("feature");
		for (Element feature : features) {
			if (feature.attributeValue("var").equals(var)) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private static String getFieldValue(String var, Element xEl) {
		List<Element> fields = xEl.elements("field");
		for (Element field : fields) {
			if (field.attributeValue("var").equals(var)) {
				return field.elementText("value");
			}
		}
		return null;
	}

	@Test
	public void withApiEndPointInConfigurationServiceDiscoveryExtensionIncluded() throws Exception {
		String endPoint = "https://api.buddycloud.com";
		Properties properties = new Properties();
		properties.put(MediaServerConfiguration.HTTP_ENDPOINT, endPoint);
	
		ComponentManagerMock cm = new ComponentManagerMock();
		
		MediaServerComponent component = new MediaServerComponent(properties);
		component.initialize(new JID(to), cm);
		component.processPacket(discoRequest);
		
		Packet replyPacket = cm.collectPacket();
		
		Element queryEl = replyPacket.getElement().element("query");
		Assert.assertEquals(AbstractComponent.NAMESPACE_DISCO_INFO, queryEl.getNamespaceURI());
		
		Element identityEl = queryEl.element("identity");
		Assert.assertEquals("component", identityEl.attributeValue("category"));
		Assert.assertEquals("Media Server", identityEl.attributeValue("name"));
		Assert.assertEquals("generic", identityEl.attributeValue("type"));
		
		Assert.assertTrue(hasFeature(AbstractComponent.NAMESPACE_DISCO_INFO, queryEl));
		Assert.assertTrue(hasFeature(AbstractComponent.NAMESPACE_XMPP_PING, queryEl));
		Assert.assertTrue(hasFeature(AbstractComponent.NAMESPACE_LAST_ACTIVITY, queryEl));
		Assert.assertTrue(hasFeature(AbstractComponent.NAMESPACE_ENTITY_TIME, queryEl));

		Element xEl = queryEl.element("x");
		Assert.assertNotNull(xEl);
		Assert.assertEquals("jabber:x:data", xEl.getNamespaceURI());
		Assert.assertEquals("http://buddycloud.org/v1/api", getFieldValue("FORM_TYPE", xEl));
		Assert.assertEquals("https://api.buddycloud.com", getFieldValue("endpoint", xEl));
	}
	
}