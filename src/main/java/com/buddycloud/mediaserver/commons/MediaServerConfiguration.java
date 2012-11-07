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
package com.buddycloud.mediaserver.commons;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buddycloud.mediaserver.commons.exception.LoadConfigurationException;

public class MediaServerConfiguration {

	// File System
	public static final String MEDIA_STORAGE_ROOT_PROPERTY = "media.storage.root";
	public static final String MEDIA_SIZE_LIMIT_PROPERTY = "media.sizelimit";

	// JDBC
	public static final String JDBC_DRIVER_CLASS_PROPERTY = "jdbc.driver.class";
	public static final String JDBC_DB_URL_PROPERTY = "jdbc.db.url";

	// buddycloud
	public static final String BC_CHANNELS_SERVER = "bc.channels.server";

	// XMPP
	public static final String XMPP_COMPONENT_HOST = "xmpp.component.host";
	public static final String XMPP_COMPONENT_PORT = "xmpp.component.port";
	public static final String XMPP_COMPONENT_SUBDOMAIN = "xmpp.component.subdomain";
	public static final String XMPP_COMPONENT_SECRETKEY = "xmpp.component.secretkey";

	public static final String XMPP_CONNECTION_USERNAME = "xmpp.connection.username";
	public static final String XMPP_CONNECTION_PASSWORD = "xmpp.connection.password";
	public static final String XMPP_CONNECTION_HOST = "xmpp.connection.host";
	public static final String XMPP_CONNECTION_PORT = "xmpp.connection.port";
	public static final String XMPP_CONNECTION_SERVICENAME = "xmpp.connection.servicename";

	// HTTP
	public static final String HTTP_PORT = "http.port";
	public static final String HTTPS_PORT = "https.port";
	public static final String HTTPS_ENABLED = "https.enabled";
	public static final String HTTPS_KEYSTORE_PATH = "https.keystore.path";
	public static final String HTTPS_KEYSTORE_TYPE = "https.keystore.type";
	public static final String HTTPS_KEYSTORE_PASSWORD = "https.keystore.password";
	public static final String HTTPS_KEY_PASSWORD = "https.key.password";

	/*
	 * mediaserver.properties default values
	 */
	// JDBC
	public static final String DEF_JDBC_DRIVER_CLASS_PROPERTY = "org.postgresql.Driver";
	
	// File System
	public static final long DEF_MEDIA_SIZE_LIMIT_PROPERTY = 1048576;

	// HTTP
	public static final int DEF_HTTP_PORT = 8080;
	public static final boolean DEF_HTTPS_ENABLED = false;

	private static MediaServerConfiguration instance = new MediaServerConfiguration();

	private static final String CONFIGURATION_FILE = "mediaserver.properties";
	private static Logger LOGGER = LoggerFactory.getLogger(MediaServerConfiguration.class);

	private Properties configuration;

	private MediaServerConfiguration() {
		this.configuration = new Properties();

		try {
			configuration.load(new FileInputStream(CONFIGURATION_FILE));
			loadDefault();
			validate();
		} catch (IOException e) {
			LOGGER.error("Configuration could not be loaded.", e);
			throw new LoadConfigurationException(e.getMessage(), e);
		}
	}

	public static MediaServerConfiguration getInstance() {
		return instance;
	}

	public Properties getConfiguration() {
		return this.configuration;
	}

	private void loadDefault() {
		if (configuration.get(JDBC_DRIVER_CLASS_PROPERTY) == null) {
			configuration.put(JDBC_DRIVER_CLASS_PROPERTY,
					DEF_JDBC_DRIVER_CLASS_PROPERTY);			
		}
		
		if (configuration.get(MEDIA_SIZE_LIMIT_PROPERTY) == null) {
			configuration.put(MEDIA_SIZE_LIMIT_PROPERTY,
					DEF_MEDIA_SIZE_LIMIT_PROPERTY);
		}

		if (configuration.get(HTTP_PORT) == null) {
			configuration.put(HTTP_PORT, DEF_HTTP_PORT);
		}

		if (configuration.get(HTTPS_ENABLED) == null) {
			configuration.put(HTTPS_ENABLED, DEF_HTTPS_ENABLED);
		}
	}

	private void validate() {
		List<String> missingProperties = new ArrayList<String>();

		if (configuration.get(MEDIA_STORAGE_ROOT_PROPERTY) == null) {
			missingProperties.add(MEDIA_STORAGE_ROOT_PROPERTY);
		}

		if (configuration.get(JDBC_DB_URL_PROPERTY) == null) {
			missingProperties.add(JDBC_DB_URL_PROPERTY);
		}

		if (configuration.get(BC_CHANNELS_SERVER) == null) {
			missingProperties.add(BC_CHANNELS_SERVER);
		}

		if (configuration.get(XMPP_COMPONENT_HOST) == null) {
			missingProperties.add(XMPP_COMPONENT_HOST);
		}

		if (configuration.get(XMPP_COMPONENT_PORT) == null) {
			missingProperties.add(XMPP_COMPONENT_PORT);
		}

		if (configuration.get(XMPP_COMPONENT_SUBDOMAIN) == null) {
			missingProperties.add(XMPP_COMPONENT_SUBDOMAIN);
		}

		if (configuration.get(XMPP_COMPONENT_SECRETKEY) == null) {
			missingProperties.add(XMPP_COMPONENT_SECRETKEY);
		}

		if (configuration.get(XMPP_CONNECTION_USERNAME) == null) {
			missingProperties.add(XMPP_CONNECTION_USERNAME);
		}

		if (configuration.get(XMPP_CONNECTION_PASSWORD) == null) {
			missingProperties.add(XMPP_CONNECTION_PASSWORD);
		}

		if (configuration.get(XMPP_COMPONENT_HOST) == null) {
			missingProperties.add(XMPP_COMPONENT_HOST);
		}

		if (configuration.get(XMPP_CONNECTION_PORT) == null) {
			missingProperties.add(XMPP_CONNECTION_PORT);
		}

		if (configuration.get(XMPP_CONNECTION_SERVICENAME) == null) {
			missingProperties.add(XMPP_CONNECTION_SERVICENAME);
		}

		if (missingProperties.size() > 0) {
			throw new LoadConfigurationException(
					"Media Server configuration could not be loaded. "
							+ "The following mandatory properties are missing: "
							+ missingProperties);
		}
	}
}
