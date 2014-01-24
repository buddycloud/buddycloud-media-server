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

import com.buddycloud.mediaserver.xmpp.util.MediaServerPacketCollector;
import com.buddycloud.mediaserver.xmpp.util.MediaServerPacketFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.component.AbstractComponent;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Media Server XMPP Component Follows the XEP-0114
 * @see (http://xmpp.org/extensions/xep-0114.html)
 * 
 * @author Rodrigo Duarte Sousa - rodrigodsousa@gmail.com
 */
public class MediaServerComponent extends AbstractComponent {

	private static final String DESCRIPTION = "An XMPP Media Server";
	private static final String NAME = "Media Server";
	private static final Logger LOGGER = LoggerFactory.getLogger(MediaServerComponent.class);

	protected final Collection<MediaServerPacketCollector> collectors = new ConcurrentLinkedQueue<MediaServerPacketCollector>();

	public MediaServerComponent() {
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
}
