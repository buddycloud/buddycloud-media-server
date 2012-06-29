package com.buddycloud.mediaserver.commons;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.buddycloud.mediaserver.commons.exception.LoadConfigurationException;

public class ConfigurationContext {
	
	private static ConfigurationContext instance = new ConfigurationContext();

	
	private static final String CONFIGURATION_FILE = "mediaserver.properties";
	private static Logger LOGGER = Logger.getLogger(ConfigurationContext.class);
	
	
	private Properties configuration;

	
	private ConfigurationContext() {
		this.configuration = new Properties();
		
		try {
			configuration.load(new FileInputStream(CONFIGURATION_FILE));
		} catch (IOException e) {
			LOGGER.fatal("Configuration could not be loaded.", e);
			throw new LoadConfigurationException(e.getMessage(), e);
		}
	}
	
	
	public static ConfigurationContext getInstance() {
		return instance;
	}
	
	
	public Properties getConfiguration() {
		return this.configuration;
	}
}
