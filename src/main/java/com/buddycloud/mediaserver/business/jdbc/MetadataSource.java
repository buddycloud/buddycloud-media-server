package com.buddycloud.mediaserver.business.jdbc;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.buddycloud.mediaserver.business.model.Media;
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
	
	public PreparedStatement prepareStatement(String sql, Object... args) throws SQLException {
		PreparedStatement prepareStatement = dataSource.getConnection().prepareStatement(sql);
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
	
	public void storeMetadata(Media media) throws SQLException {
		PreparedStatement statement = prepareStatement(Queries.SAVE_MEDIA, media.getId(), 
				media.getUploader(), media.getTitle(), media.getMimeType(), media.getDownloadUrl(), 
				media.getFileExtension(), media.getMd5Checksum(), media.getFileSize(), 
				media.getLength(), media.getResolution());
		
		statement.execute();
		statement.close();
	}
}
