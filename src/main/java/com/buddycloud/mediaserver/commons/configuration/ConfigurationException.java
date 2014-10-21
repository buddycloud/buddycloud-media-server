package com.buddycloud.mediaserver.commons.configuration;

import java.io.FileInputStream;

import com.buddycloud.mediaserver.commons.exception.LoadConfigurationException;

public class ConfigurationException extends Exception {

  public ConfigurationException(String message) {
    super(message);
  }
  
}