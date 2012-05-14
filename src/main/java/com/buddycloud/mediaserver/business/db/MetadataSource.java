package com.buddycloud.mediaserver.business.db;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class MetadataSource {
	private static Logger LOGGER = Logger.getLogger(MetadataSource.class);
	
	
	private ComboPooledDataSource dataSource;
	private Properties configuration;
	

	public MetadataSource(Properties configuration) throws PropertyVetoException {
		this.configuration = configuration;
		createDataSource();
	}

	public Statement createStatement() throws SQLException {
		return dataSource.getConnection().createStatement();
	}
	
	public PreparedStatement prepareStatement(String statement, Object... args) throws SQLException {
		PreparedStatement prepareStatement = dataSource.getConnection().prepareStatement(statement);
		for (int i = 1; i <= args.length; i++) {
			prepareStatement.setObject(i, args[i-1]);
		}
		return prepareStatement;
	}
	
	public static void close(Statement statement) {
		if (statement == null) {
			return;
		}
		
		try {
			Connection connection = statement.getConnection();
			statement.close();
			connection.close();
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
	
	public ComboPooledDataSource getDataSource() {
		return dataSource;
	}

	private void createDataSource() throws PropertyVetoException {
		this.dataSource = new ComboPooledDataSource();
		dataSource.setDriverClass("org.postgresql.Driver");
		dataSource.setJdbcUrl(configuration.getProperty("postgres.jdbc.url"));
	}
}
