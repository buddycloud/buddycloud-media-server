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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buddycloud.mediaserver.commons.exception.LoadConfigurationException;

public class MediaServerConfiguration {

	// File System
	public static final String MEDIA_STORAGE_ROOT_PROPERTY = "media.storage.root";
	public static final String MEDIA_SIZE_LIMIT_PROPERTY = "media.sizelimit";
    public static final String MEDIA_TO_DISK_THRESHOLD_PROPERTY = "media.todisk.threshold";

	// JDBC
	public static final String JDBC_DRIVER_CLASS_PROPERTY = "jdbc.driver.class";
	public static final String JDBC_DB_URL_PROPERTY = "jdbc.db.url";

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
	public static final String XMPP_CONNECTION_SECURITY = "xmpp.connection.securitymode";

    public static final String XMPP_REPLY_TIMEOUT = "xmpp.reply.timeout";

	// HTTP
	public static final String HTTP_PORT = "http.port";
	public static final String HTTP_ENDPOINT = "http.endpoint";
    public static final String HTTP_TESTS_PORT = "http.tests.port";
	public static final String HTTPS_PORT = "https.port";
	public static final String HTTPS_ENABLED = "https.enabled";
	public static final String HTTPS_KEYSTORE_PATH = "https.keystore.path";
	public static final String HTTPS_KEYSTORE_TYPE = "https.keystore.type";
	public static final String HTTPS_KEYSTORE_PASSWORD = "https.keystore.password";
	public static final String HTTPS_KEY_PASSWORD = "https.key.password";

	// CACHE
	public static final String CACHE_MAX_AGE = "cache.max.age";

	/*
	 * mediaserver.properties default values
	 */
    // XMPP
    public static final Integer DEF_XMPP_REPLY_TIMEOUT = 30000; // 30 seconds

	// CACHE
	public static final Integer DEF_CACHE_MAX_AGE = 86400; //1 day;
	
	// JDBC
	public static final String DEF_JDBC_DRIVER_CLASS_PROPERTY = "org.postgresql.Driver";
	
	// File System
    public static final Long DEF_MEDIA_SIZE_LIMIT = 104857600L;
	public static final Long DEF_MEDIA_TO_DISK_THRESHOLD = 1048576L;

	// HTTP
    public static final Integer DEF_HTTP_TESTS_PORT = 9091;
	public static final Integer DEF_HTTP_PORT = 8080;
	public static final Boolean DEF_HTTPS_ENABLED = false;
	
	// XMPP sec
	private static final String DEF_XMPP_CONNECTION_SECURITY = SecurityMode.enabled.toString();

	private static MediaServerConfiguration instance;

	private static final String CONFIGURATION_FILE = "mediaserver.properties";
	public static final String CONFIGURATION_ENV = "mediaserver.configuration";
	
	public static final String BUDDYCLOUD_NS_API = "http://buddycloud.org/v1/api";
	public static final String API_ENDPOINT_FIELD_VAR = "endpoint";
	
	private static Logger LOGGER = LoggerFactory.getLogger(MediaServerConfiguration.class);

	private Properties configuration;

	private MediaServerConfiguration() {
		this.configuration = new Properties();
        try {
			String confPath = System.getProperty(CONFIGURATION_ENV) == null ? CONFIGURATION_FILE
					: System.getProperty(CONFIGURATION_ENV);
        	configuration.load(new FileInputStream(confPath));
		} catch (Exception e) {
			LOGGER.error("Configuration could not be loaded.", e);
			throw new LoadConfigurationException(e.getMessage(), e);
		}
        loadDefault();
        validate();
	}

	public static MediaServerConfiguration getInstance() {
		if (instance == null) {
			instance = new MediaServerConfiguration();
		}
		return instance;
	}

	public Properties getConfiguration() {
		return this.configuration;
	}

	public void loadDefault() {
        if (configuration.get(XMPP_REPLY_TIMEOUT) == null) {
            configuration.put(XMPP_REPLY_TIMEOUT, DEF_XMPP_REPLY_TIMEOUT.toString());
        }

		if (configuration.get(CACHE_MAX_AGE) == null) {
			configuration.put(CACHE_MAX_AGE, DEF_CACHE_MAX_AGE.toString());
		}
		
		if (configuration.get(JDBC_DRIVER_CLASS_PROPERTY) == null) {
			configuration.put(JDBC_DRIVER_CLASS_PROPERTY,
					DEF_JDBC_DRIVER_CLASS_PROPERTY);			
		}
		
		if (configuration.get(MEDIA_TO_DISK_THRESHOLD_PROPERTY) == null) {
			configuration.put(MEDIA_TO_DISK_THRESHOLD_PROPERTY,
					DEF_MEDIA_TO_DISK_THRESHOLD.toString());
		}

        if (configuration.get(MEDIA_SIZE_LIMIT_PROPERTY) == null) {
            configuration.put(MEDIA_SIZE_LIMIT_PROPERTY,
                    DEF_MEDIA_SIZE_LIMIT.toString());
        }

		if (configuration.get(HTTP_PORT) == null) {
			configuration.put(HTTP_PORT, DEF_HTTP_PORT.toString());
		}

        if (configuration.get(HTTP_TESTS_PORT) == null) {
			configuration.put(HTTP_TESTS_PORT, DEF_HTTP_TESTS_PORT.toString());
		}

		if (configuration.get(HTTPS_ENABLED) == null) {
			configuration.put(HTTPS_ENABLED, DEF_HTTPS_ENABLED.toString());
		}
		
		if (configuration.get(XMPP_CONNECTION_SECURITY) == null) {
			configuration.put(XMPP_CONNECTION_SECURITY, DEF_XMPP_CONNECTION_SECURITY);
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
