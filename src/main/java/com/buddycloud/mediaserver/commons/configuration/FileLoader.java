package com.buddycloud.mediaserver.commons.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileLoader implements Loader {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileLoader.class);

  public static final String CONFIGURATION_ENV = "mediaserver.configuration";
  private static final String CONFIGURATION_FILE = "mediaserver.properties";
  private Properties configuration;

  public FileLoader(Properties configuration) {
    this.configuration = configuration;
  }

  public void load() throws ConfigurationException {
    
    try {
      InputStream confFile = this.getClass().getClassLoader().getResourceAsStream(CONFIGURATION_FILE);
      if (null == confFile) {
        
        String confPath = (System.getProperty(CONFIGURATION_ENV) == null) ? CONFIGURATION_FILE
                : System.getProperty(CONFIGURATION_ENV);
        confFile = new FileInputStream(confPath);
      }
      readFile(confFile);
    } catch (IOException e) {
      LOGGER.error("Configuration could not be loaded.", e);
      throw new ConfigurationException(e.getMessage());
    }

  }

  private void readFile(InputStream inputStream) throws IOException {
    this.configuration.load(inputStream);
  }

}
