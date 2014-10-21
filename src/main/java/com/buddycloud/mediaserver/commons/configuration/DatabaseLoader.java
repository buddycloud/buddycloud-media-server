package com.buddycloud.mediaserver.commons.configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buddycloud.mediaserver.commons.MediaServerConfiguration;

public class DatabaseLoader implements Loader {

  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseLoader.class);
  private Properties configuration;
  private String connectionString;

  public DatabaseLoader(Properties configuration, String connectionString) {
    this.connectionString = connectionString;
    this.configuration = configuration;
  }

  public void load() throws ConfigurationException {

    Connection connection = null;
    LOGGER.info("Loading configuration from database");
    try {
      connection = DriverManager.getConnection(this.connectionString);
      PreparedStatement statement =
          connection.prepareStatement("SELECT \"key\", \"value\" FROM \"configuration\";");
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        this.configuration.setProperty(rs.getString(1), rs.getString(2));
      }
      this.configuration.setProperty(MediaServerConfiguration.JDBC_DB_URL_PROPERTY, connectionString);
    } catch (SQLException e) {
      LOGGER.error(e.getMessage());
      throw new ConfigurationException("Could not get configuration from database");
    } finally {
      if (null == connection) {
          return;
      }
      try {
        connection.close();
      } catch (SQLException e) {
        LOGGER.error(e.getMessage());
        throw new ConfigurationException("Could not get configuration from database");
      }
    }
  }

}