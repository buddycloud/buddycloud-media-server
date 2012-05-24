package com.buddycloud.mediaserver.commons;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.buddycloud.mediaserver.commons.exception.LoadConfigurationException;

public class ConfigurationUtils {
	
	private static final String CONFIGURATION_FILE = "mediaserver.properties";
	private static Logger LOGGER = Logger.getLogger(ConfigurationUtils.class);

	
	private ConfigurationUtils() {}

	
	public static Properties loadConfiguration() {
		Properties configuration = new Properties();
		try {
			configuration.load(new FileInputStream(CONFIGURATION_FILE));
		} catch (IOException e) {
			LOGGER.fatal("Configuration could not be loaded.", e);
			throw new LoadConfigurationException(e.getMessage(), e);
		}
		
		return configuration;
	}
}
