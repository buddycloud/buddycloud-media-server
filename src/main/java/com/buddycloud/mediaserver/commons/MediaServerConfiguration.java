package com.buddycloud.mediaserver.commons;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.buddycloud.mediaserver.commons.exception.LoadConfigurationException;

public class MediaServerConfiguration {
	
	//Properties file constants
	public static final String MEDIA_STORAGE_ROOT_PROPERTY = "media.storage.root";
	public static final String MEDIA_SIZE_LIMIT_PROPERTY = "media.sizelimit";
	
	public static final String JDBC_DRIVER_CLASS_PROPERTY = "jdbc.driver.class";
	public static final String JDBC_DB_URL_PROPERTY = "jdbc.db.url";
	
	private static MediaServerConfiguration instance = new MediaServerConfiguration();

	
	private static final String CONFIGURATION_FILE = "mediaserver.properties";
	private static Logger LOGGER = Logger.getLogger(MediaServerConfiguration.class);
	
	
	private Properties configuration;

	
	private MediaServerConfiguration() {
		this.configuration = new Properties();
		
		try {
			configuration.load(new FileInputStream(CONFIGURATION_FILE));
		} catch (IOException e) {
			LOGGER.fatal("Configuration could not be loaded.", e);
			throw new LoadConfigurationException(e.getMessage(), e);
		}
	}
	
	
	public static MediaServerConfiguration getInstance() {
		return instance;
	}
	
	
	public Properties getConfiguration() {
		return this.configuration;
	}
}
