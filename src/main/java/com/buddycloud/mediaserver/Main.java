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

import com.buddycloud.mediaserver.commons.MediaServerConfiguration;
import com.buddycloud.mediaserver.commons.exception.CreateXMPPConnectionException;
import com.buddycloud.mediaserver.web.MediaServerApplication;
import com.buddycloud.mediaserver.xmpp.MediaServerComponent;
import com.buddycloud.mediaserver.xmpp.XMPPToolBox;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.whack.ExternalComponentManager;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.component.ComponentException;

import java.util.Properties;

public class Main {
	private static Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		Properties configuration = MediaServerConfiguration.getInstance()
				.getConfiguration();

		try {
			startRestletComponent(configuration);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			System.exit(1);
		}

        while (!startXMPPToolBox(configuration)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.exit(1);
            }
        }

        keepAlive();
    }

    private static void keepAlive() {
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                System.exit(1);
            }
        }
    }

	private static void startRestletComponent(Properties configuration)
			throws Exception {
		Component component = new Component();

		if (Boolean.valueOf(configuration.getProperty(MediaServerConfiguration.HTTPS_ENABLED))) {
			Server server = component.getServers().add(Protocol.HTTPS,
					Integer.valueOf(configuration.getProperty(MediaServerConfiguration.HTTPS_PORT)));

			server.getContext()
					.getParameters()
					.add("sslContextFactory",
							"org.restlet.ext.ssl.PkixSslContextFactory");
			server.getContext()
					.getParameters()
					.add("keystorePath",
							configuration.getProperty(MediaServerConfiguration.HTTPS_KEYSTORE_PATH));
			server.getContext()
					.getParameters()
					.add("keystorePassword",
							configuration
									.getProperty(MediaServerConfiguration.HTTPS_KEYSTORE_PASSWORD));
			server.getContext()
					.getParameters()
					.add("keyPassword",
							configuration.getProperty(MediaServerConfiguration.HTTPS_KEY_PASSWORD));
			server.getContext()
					.getParameters()
					.add("keystoreType",
							configuration.getProperty(MediaServerConfiguration.HTTPS_KEYSTORE_TYPE));
		} else {
			component.getServers().add(Protocol.HTTP,
					Integer.valueOf(configuration.getProperty(MediaServerConfiguration.HTTP_PORT)));
		}

		Context context = component.getContext().createChildContext();
		component.getDefaultHost().attach(new MediaServerApplication(context));

		component.start();
		
		LOGGER.info("Buddycloud Media Server HTTP server started!");
	}

    private static void setXMPPReplyTimeout(Properties configuration) {
        int xmppReplyTimeout = Integer.valueOf(configuration.getProperty(MediaServerConfiguration.XMPP_REPLY_TIMEOUT));
        SmackConfiguration.setPacketReplyTimeout(xmppReplyTimeout);
    }

	private static boolean startXMPPToolBox(Properties configuration) {
        setXMPPReplyTimeout(configuration);

		XMPPConnection connection = createAndStartConnection(configuration);
		addTraceListeners(connection);

        MediaServerComponent component;
        try {
            component = createXMPPComponent(configuration);
        } catch (ComponentException e) {
            return false;
        }

		XMPPToolBox.getInstance().start(component, connection);
		
		LOGGER.info("Buddycloud Media Server XMPP component started!");

        return true;
	}

	private static MediaServerComponent createXMPPComponent(
			Properties configuration) throws ComponentException {
		ExternalComponentManager componentManager = new ExternalComponentManager(
				configuration.getProperty(MediaServerConfiguration.XMPP_COMPONENT_HOST),
				Integer.valueOf(configuration
						.getProperty(MediaServerConfiguration.XMPP_COMPONENT_PORT)));

		String subdomain = configuration
				.getProperty(MediaServerConfiguration.XMPP_COMPONENT_SUBDOMAIN);
		componentManager.setSecretKey(subdomain,
				configuration.getProperty(MediaServerConfiguration.XMPP_COMPONENT_SECRETKEY));

		MediaServerComponent mediaServer = new MediaServerComponent();

		try {
			componentManager.addComponent(subdomain, mediaServer);
		} catch (ComponentException e) {
			LOGGER.error("Media Server XMPP Component could not be started.", e);
			throw e;
		}

		return mediaServer;
	}

	private static void addTraceListeners(XMPPConnection connection) {
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

	private static XMPPConnection createAndStartConnection(
			Properties configuration) {

		String serviceName = configuration
				.getProperty(MediaServerConfiguration.XMPP_CONNECTION_SERVICENAME);
		String host = configuration.getProperty(MediaServerConfiguration.XMPP_CONNECTION_HOST);
		String userName = configuration.getProperty(MediaServerConfiguration.XMPP_CONNECTION_USERNAME);

		ConnectionConfiguration cc = new ConnectionConfiguration(host,
				Integer.parseInt(configuration
						.getProperty(MediaServerConfiguration.XMPP_CONNECTION_PORT)), serviceName);

		XMPPConnection connection = new XMPPConnection(cc);
		try {
			connection.connect();
			connection.login(userName,
					configuration.getProperty(MediaServerConfiguration.XMPP_CONNECTION_PASSWORD));
		} catch (XMPPException e) {
			LOGGER.error("XMPP connection coudn't be started", e);
			throw new CreateXMPPConnectionException(e.getMessage(), e);
		}

		addTraceListeners(connection);

		return connection;
	}
}
