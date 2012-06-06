package com.buddycloud.mediaserver.business.jdbc;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.business.model.Preview;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.exception.CreateDataSourceException;
import com.buddycloud.mediaserver.commons.exception.MetadataSourceException;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class MetadataSource {
	private static Logger LOGGER = Logger.getLogger(MetadataSource.class);
	
	
	private ComboPooledDataSource dataSource;
	private Properties configuration;
	

	public MetadataSource(Properties configuration) {
		this.configuration = configuration;
		
		try {
			createDataSource();
		} catch (PropertyVetoException e) {
			LOGGER.fatal("Error during data source creation: " + e.getMessage(), e);
			throw new CreateDataSourceException(e.getMessage(), e);
		}
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
		dataSource.setDriverClass(configuration.getProperty(Constants.JDBC_DRIVER_CLASS_PROPERTY));
		dataSource.setJdbcUrl(configuration.getProperty(Constants.JDBC_DB_URL_PROPERTY));
	}
	
	// Medias
	
	public void storeMedia(Media media) throws MetadataSourceException {
		LOGGER.debug("Store media metadata. Media ID: " + media.getId());
		
		PreparedStatement statement;
		try {
			statement = prepareStatement(Queries.SAVE_MEDIA, media.getId(), media.getFileName(),
					media.getEntityId(), media.getAuthor(), media.getTitle(), media.getDescription(), media.getMimeType(), 
					media.getFileExtension(), media.getShaChecksum(), media.getFileSize(), 
					media.getLength(), media.getHeight(), media.getWidth());

			statement.execute();
			statement.close();
			
			LOGGER.debug("Media metadata successfully stored. Media ID: " + media.getId());
		} catch (SQLException e) {
			LOGGER.error("Error while saving media metadata", e);
			throw new MetadataSourceException(e.getMessage(), e);
		}
	}
	
	public String getMediaMimeType(String mediaId) throws MetadataSourceException {
		LOGGER.debug("Getting media type. Media ID: " + mediaId);
		
		String mimeType = null;
		
		PreparedStatement statement;
		try {
			statement = prepareStatement(Queries.GET_MEDIA_MIME_TYPE, mediaId);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				mimeType = result.getString(1); 
				LOGGER.debug("Media metadata successfully fetched. Media ID: " + mediaId);
			} else {
				LOGGER.debug("No media with id '" + mediaId + "' found.");
			}

			statement.close();
		} catch (SQLException e) {
			LOGGER.error("Error while fetching media metadata: " + e.getMessage(), e);
			throw new MetadataSourceException(e.getMessage(), e);
		}
		
		return mimeType;
	}
	
	public String getMediaExtension(String mediaId) throws MetadataSourceException {
		LOGGER.debug("Getting media extension. Media ID: " + mediaId);
		
		String mimeType = null;
		
		PreparedStatement statement;
		try {
			statement = prepareStatement(Queries.GET_MEDIA_EXTENSION, mediaId);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				mimeType = result.getString(1); 
				LOGGER.debug("Media metadata successfully fetched. Media ID: " + mediaId);
			} else {
				LOGGER.debug("No media with id '" + mediaId + "' found.");
			}

			statement.close();
		} catch (SQLException e) {
			LOGGER.error("Error while fetching media metadata: " + e.getMessage(), e);
			throw new MetadataSourceException(e.getMessage(), e);
		}
		
		return mimeType;
	}
	
	public void updateMediaLastViewed(String mediaId) throws MetadataSourceException {
		LOGGER.debug("Updating last viewed date. Media ID: " + mediaId);
		
		PreparedStatement statement;
		try {
			Timestamp now = new Timestamp((new Date()).getTime());

			statement = prepareStatement(Queries.UPDATE_MEDIA_LAST_VIEWED, now, mediaId);
			statement.execute();
			statement.close();
			
			LOGGER.debug("Media last viewed date successfully updated. Media ID: " + mediaId);
		} catch (SQLException e) {
			LOGGER.error("Error while updating media last viewed date", e);
			throw new MetadataSourceException(e.getMessage(), e);
		}
	}
	
	public void deleteMedia(String mediaId) throws MetadataSourceException {
		LOGGER.debug("Deleting media metadata. Media ID: " + mediaId);
		
		PreparedStatement statement;
		try {
			statement = prepareStatement(Queries.DELETE_MEDIA, mediaId);
			statement.execute();
			statement.close();
			
			LOGGER.debug("Media metadata successfully deleted. Media ID: " + mediaId);
		} catch (SQLException e) {
			LOGGER.error("Error while deleting media metadata", e);
			throw new MetadataSourceException(e.getMessage(), e);
		}
	}
	
	// Previews
	
	public void storePreview(Preview preview) throws MetadataSourceException {
		LOGGER.debug("Store preview metadata. Preview ID: " + preview.getId());
		
		PreparedStatement statement;
		try {
			statement = prepareStatement(Queries.SAVE_PREVIEW, preview.getId(), preview.getMediaId(), preview.getShaChecksum(),
					preview.getFileSize(), preview.getHeight(), preview.getWidth());

			statement.execute();
			statement.close();
			
			LOGGER.debug("Preview metadata successfully stored. Preview ID: " + preview.getId());
		} catch (SQLException e) {
			LOGGER.error("Error while saving preview metadata", e);
			throw new MetadataSourceException(e.getMessage(), e);
		}
	}
	
	public String getPreview(String mediaId, int height, int width) throws MetadataSourceException {
		LOGGER.debug("Getting preview from media: " + mediaId);
		
		String previewId = null;
		
		PreparedStatement statement;
		try {
			statement = prepareStatement(Queries.GET_MEDIA_PREVIEWS, mediaId, height, width);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				previewId = result.getString(1); 
				LOGGER.debug("Preview successfuly fetched. Preview ID: " + previewId);
			} else {
				LOGGER.debug("No previews for media '" + mediaId + "' found.");
			}

			statement.close();
		} catch (SQLException e) {
			LOGGER.error("Error while fetching entity avatar", e);
			throw new MetadataSourceException(e.getMessage(), e);
		}
		
		return previewId;
	}
	
	public void deletePreview(String previewId) throws MetadataSourceException {
		LOGGER.debug("Deleting preview metadata. Preview ID: " + previewId);
		
		PreparedStatement statement;
		try {
			statement = prepareStatement(Queries.DELETE_PREVIEW, previewId);
			statement.execute();
			statement.close();
			
			LOGGER.debug("Preview metadata successfully deleted. Preview ID: " + previewId);
		} catch (SQLException e) {
			LOGGER.error("Error while deleting preview metadata", e);
			throw new MetadataSourceException(e.getMessage(), e);
		}
	}
	
	// Avatars
	
	public void storeAvatar(String entityId, String mediaId) throws MetadataSourceException {
		LOGGER.debug("Store " + entityId + " avatar. Media ID: " + mediaId);
		
		PreparedStatement statement;
		try {
			statement = prepareStatement(Queries.SAVE_AVATAR, entityId, mediaId);

			statement.execute();
			statement.close();
			
			LOGGER.debug("Avatar successfully stored. Media ID: " + mediaId);
		} catch (SQLException e) {
			LOGGER.error("Error while saving avatar", e);
			throw new MetadataSourceException(e.getMessage(), e);
		}
	}
	
	
	public String getEntityAvatarId(String entityId) throws MetadataSourceException {
		LOGGER.debug("Getting current avatar id from: " + entityId);
		
		String mediaId = null;
		
		PreparedStatement statement;
		try {
			statement = prepareStatement(Queries.GET_ENTITY_AVATAR_ID, entityId);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				mediaId = result.getString(1); 
				LOGGER.debug("Entity avatar id successfully fetched. Media ID: " + mediaId);
			} else {
				LOGGER.debug("No avatar for '" + entityId + "' found.");
			}

			statement.close();
		} catch (SQLException e) {
			LOGGER.error("Error while fetching entity avatar", e);
			throw new MetadataSourceException(e.getMessage(), e);
		}
		
		return mediaId;
	}
	
	public void updateEntityAvatar(String entityId, String mediaId) throws MetadataSourceException {
		LOGGER.debug("Updating " + entityId + " avatar");
		
		PreparedStatement statement;
		try {
			statement = prepareStatement(Queries.UPDATE_AVATAR, mediaId, entityId);
			statement.execute();
			statement.close();
			
			LOGGER.debug("Entity avatar successfully updated. Entity ID: " + entityId + ". Media ID: " + mediaId);
		} catch (SQLException e) {
			LOGGER.error("Error while updating entity avatar", e);
			throw new MetadataSourceException(e.getMessage(), e);
		}
	}
}
