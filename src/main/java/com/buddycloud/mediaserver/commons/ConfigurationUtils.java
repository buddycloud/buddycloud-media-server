package com.buddycloud.mediaserver.commons;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigurationUtils {
	
	private static final String CONFIGURATION_FILE = "mediaserver.properties";
	private static Logger LOGGER = Logger.getLogger(ConfigurationUtils.class);
	
	public static Properties loadConfiguration() throws IOException {
		Properties configuration = new Properties();
		try {
			configuration.load(new FileInputStream(CONFIGURATION_FILE));
		} catch (IOException e) {
			LOGGER.fatal("Configuration could not be loaded.", e);
			throw e;
		}
		
		return configuration;
	}
}
