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
package com.buddycloud.mediaserver.xmpp.util;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.tree.BaseElement;
import org.dom4j.tree.DefaultAttribute;
import org.xmpp.packet.IQ;

/**
 * A PacketExtension that implements XEP-0070: HTTP Authentication
 * 
 * @author Rodrigo Duarte Sousa, rodrigodsousa@gmail.com
 * @see <a href="http://xmpp.org/extensions/xep-0070.html">XEP-0070:&nbsp;HTTP
 *      Authentication</a>
 */
public class HTTPAuthIQ extends IQ {

	public static final String ELEMENT_NAME = "confirm";
	public static final String NAMESPACE = "http://jabber.org/protocol/http-auth";

	private String id;
	private String url;

	public HTTPAuthIQ(String id, String url) {
		this.id = id;
		this.url = url;
		addChildElement();
	}

	private void addChildElement() {
		Element childElement = new BaseElement(getElementName(), new Namespace(
				null, getNamespace()));
		childElement.add(new DefaultAttribute("id", id));
		childElement.add(new DefaultAttribute("url", url));

		setChildElement(childElement);
	}

	public String getElementName() {
		return ELEMENT_NAME;
	}

	public String getNamespace() {
		return NAMESPACE;
	}
}
