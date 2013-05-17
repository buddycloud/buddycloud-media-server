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

import java.util.UUID;

import org.dom4j.Element;
import org.xmpp.packet.Message;
import org.xmpp.packet.Message.Type;

/**
 * A PacketExtension that implements XEP-0070: HTTP Authentication
 * 
 * @author Rodrigo Duarte Sousa, rodrigodsousa@gmail.com
 * @see <a href="http://xmpp.org/extensions/xep-0070.html">XEP-0070:&nbsp;HTTP
 *      Authentication</a>
 */
public class HTTPAuthMessageBuilder {

	public static final String ELEMENT_NAME = "confirm";
	public static final String NAMESPACE = "http://jabber.org/protocol/http-auth";

	private String id;
	private String url;
	private String thread;

	public HTTPAuthMessageBuilder(String id, String url) {
		this.id = id;
		this.url = url;
		this.thread = UUID.randomUUID().toString().replace("-", "");
	}

	public Message createPacket() {
		Message m = new Message();
		m.setType(Type.normal);
		m.setThread(thread);
		m.setBody("Confirmation message for transaction " + id);
		
		Element rootEl = m.getElement();
		rootEl.addAttribute("xmlns:stream", "http://etherx.jabber.org/streams");
		
		Element authEl = rootEl.addElement(ELEMENT_NAME, NAMESPACE);
		authEl.addAttribute("id", id);
		authEl.addAttribute("url", url);
		authEl.addAttribute("method", "GET");
		
		return m;
	}
}
