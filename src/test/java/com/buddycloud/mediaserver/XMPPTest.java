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
package com.buddycloud.mediaserver;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.whack.ExternalComponentManager;
import org.xmpp.component.ComponentException;

import com.buddycloud.mediaserver.commons.exception.CreateXMPPConnectionException;
import com.buddycloud.mediaserver.xmpp.MediaServerComponent;
import com.buddycloud.mediaserver.xmpp.XMPPToolBox;

public class XMPPTest implements TextExtension {
	private static Logger LOGGER = Logger.getLogger(XMPPTest.class);

	private boolean started;
	private String componentSubDomain;
	private ExternalComponentManager componentManager;
	private MediaServerComponent component;

	public XMPPTest() {
		this.started = false;
	}

	public void start(Properties configuration) throws Exception {
		if (!started) {
			XMPPConnection connection = createAndStartConnection(configuration);
			addTraceListeners(connection);

			component = createXMPPComponent(configuration);

			String[] servers = configuration.getProperty("bc.channels.server")
					.split(";");

			XMPPToolBox.getInstance().start(component, connection, servers);

			started = true;
		}
	}

	public void shutdown() throws Exception {
		if (started) {
			component.shutdown();
			componentManager.removeComponent(componentSubDomain);

			started = false;
		}
	}

	private MediaServerComponent createXMPPComponent(Properties configuration)
			throws Exception {
		componentManager = new ExternalComponentManager(
				configuration.getProperty("xmpp.component.host"),
				Integer.valueOf(configuration
						.getProperty("xmpp.component.port")));

		componentSubDomain = configuration
				.getProperty("xmpp.component.subdomain");
		componentManager.setSecretKey(componentSubDomain,
				configuration.getProperty("xmpp.component.secretkey"));

		MediaServerComponent mediaServer = new MediaServerComponent();

		try {
			componentManager.addComponent(componentSubDomain, mediaServer);
		} catch (ComponentException e) {
			LOGGER.fatal("Media Server XMPP Component could not be started.", e);
			throw e;
		}

		return mediaServer;
	}

	private void addTraceListeners(XMPPConnection connection) {
		PacketFilter iqFilter = new PacketFilter() {
			@Override
			public boolean accept(Packet arg0) {
				return arg0 instanceof IQ;
			}
		};

		connection.addPacketSendingListener(new PacketListener() {
			@Override
			public void processPacket(Packet arg0) {
				LOGGER.debug("S: " + arg0.toXML());
			}
		}, iqFilter);

		connection.addPacketListener(new PacketListener() {

			@Override
			public void processPacket(Packet arg0) {
				LOGGER.debug("R: " + arg0.toXML());
			}
		}, iqFilter);
	}

	private XMPPConnection createAndStartConnection(Properties configuration) {

		String serviceName = configuration
				.getProperty("xmpp.connection.servicename");
		String host = configuration.getProperty("xmpp.connection.host");
		String userName = configuration.getProperty("xmpp.connection.username");

		ConnectionConfiguration cc = new ConnectionConfiguration(host,
				Integer.parseInt(configuration
						.getProperty("xmpp.connection.port")), serviceName);

		XMPPConnection connection = new XMPPConnection(cc);
		try {
			connection.connect();
			connection.login(userName,
					configuration.getProperty("xmpp.connection.password"));
		} catch (XMPPException e) {
			LOGGER.fatal("XMPP connection coudn't be started", e);
			throw new CreateXMPPConnectionException(e.getMessage(), e);
		}

		addTraceListeners(connection);

		return connection;
	}
}
