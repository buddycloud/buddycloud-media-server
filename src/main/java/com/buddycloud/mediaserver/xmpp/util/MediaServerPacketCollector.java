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

import java.util.LinkedList;

import org.xmpp.packet.Packet;

import com.buddycloud.mediaserver.xmpp.MediaServerComponent;

public class MediaServerPacketCollector {

	private static final int MAX_PACKETS = 5000;

	private MediaServerComponent component;
	private MediaServerPacketFilter packetFilter;
	private LinkedList<Packet> resultQueue;
	private boolean cancelled = false;

	public MediaServerPacketCollector(MediaServerComponent component,
			MediaServerPacketFilter packetFilter) {
		this.component = component;
		this.packetFilter = packetFilter;
		this.resultQueue = new LinkedList<Packet>();
	}

	public void cancel() {
		// If the packet collector has already been cancelled, do nothing.
		if (!cancelled) {
			cancelled = true;
			component.removePacketCollector(this);
		}
	}

	public MediaServerPacketFilter getPacketFilter() {
		return packetFilter;
	}

	public synchronized Packet pollResult() {
		if (resultQueue.isEmpty()) {
			return null;
		} else {
			return resultQueue.removeLast();
		}
	}

	public synchronized Packet nextResult() {
		// Wait indefinitely until there is a result to return.
		while (resultQueue.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException ie) {
				// Ignore.
			}
		}
		return resultQueue.removeLast();
	}

	public synchronized Packet nextResult(long timeout) {
		// Wait up to the specified amount of time for a result.
		if (resultQueue.isEmpty()) {
			long waitTime = timeout;
			long start = System.currentTimeMillis();
			try {
				// Keep waiting until the specified amount of time has elapsed,
				// or
				// a packet is available to return.
				while (resultQueue.isEmpty()) {
					if (waitTime <= 0) {
						break;
					}
					wait(waitTime);
					long now = System.currentTimeMillis();
					waitTime -= (now - start);
					start = now;
				}
			} catch (InterruptedException ie) {
				// Ignore.
			}
			// Still haven't found a result, so return null.
			if (resultQueue.isEmpty()) {
				return null;
			}
			// Return the packet that was found.
			else {
				return resultQueue.removeLast();
			}
		}
		// There's already a packet waiting, so return it.
		else {
			return resultQueue.removeLast();
		}
	}

	public synchronized void processPacket(Packet packet) {
		if (packet == null) {
			return;
		}
		if (packetFilter == null || packetFilter.accept(packet)) {
			// If the max number of packets has been reached, remove the oldest
			// one.
			if (resultQueue.size() == MAX_PACKETS) {
				resultQueue.removeLast();
			}
			// Add the new packet.
			resultQueue.addFirst(packet);
			// Notify waiting threads a result is available.
			notifyAll();
		}
	}
}
