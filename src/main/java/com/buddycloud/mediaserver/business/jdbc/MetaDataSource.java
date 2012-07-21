package com.buddycloud.mediaserver.business.jdbc;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.business.model.Preview;
import com.buddycloud.mediaserver.commons.ConfigurationContext;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.exception.CreateDataSourceException;
import com.buddycloud.mediaserver.commons.exception.MetadataSourceException;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class MetaDataSource {
	private static Logger LOGGER = Logger.getLogger(MetaDataSource.class);
	
	
	private ComboPooledDataSource dataSource;
	private Properties configuration;
	

	public MetaDataSource() {
		this.configuration = ConfigurationContext.getInstance().getConfiguration();
		
		try {
			createDataSource();
		} catch (PropertyVetoException e) {
			LOGGER.fatal("Error during data source creation: " + e.getMessage(), e);
			throw new CreateDataSourceException(e.getMessage(), e);
		}
	}

	private PreparedStatement prepareStatement(String sql, Object... args) throws SQLException {
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
	
	private Media resultToMedia(ResultSet result) throws SQLException {
		Media media = new Media();
		
		media.setId(result.getString(1));
		media.setFileName(result.getString(2));
		media.setEntityId(result.getString(3));
		media.setAuthor(result.getString(4));
		media.setTitle(result.getString(5));
		media.setDescription(result.getString(6));
		media.setMimeType(result.getString(7));
		media.setUploadedDate(result.getTimestamp(8));
		media.setLastUpdatedDate(result.getTimestamp(9));
		media.setFileExtension(result.getString(10));
		media.setShaChecksum(result.getString(11));
		media.setFileSize(result.getLong(12));
		media.setLength(result.getLong(13));
		media.setHeight(result.getInt(14));
		media.setWidth(result.getInt(15));
		
		return media;
	}
	
	public List<Media> getMediasInfo(String entityId, String since) throws MetadataSourceException {
		LOGGER.debug("Get medias info from: " + entityId);
		
		List<Media> medias = new ArrayList<Media>();
		
		PreparedStatement statement;
		try {
			if (since != null) {
				DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
				Timestamp timestamp = new Timestamp(formatter.parseDateTime(since).getMillis());
				
				statement = prepareStatement(Queries.GET_MEDIAS_INFO_SINCE, entityId, timestamp);
			} else {
				statement = prepareStatement(Queries.GET_MEDIAS_INFO, entityId);
			}
			
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				medias.add(resultToMedia(result));
			}
			
			statement.close();

			LOGGER.debug("Medias info sucessfully fetched");
		} catch (SQLException e) {
			LOGGER.error("Error while fetching medias info", e);
			throw new MetadataSourceException(e.getMessage(), e);
		}
		
		return medias;
	}
	
	public Media getMedia(String mediaId) throws MetadataSourceException {
		LOGGER.debug("Getting media. Media ID: " + mediaId);
		
		Media media = null;
		
		PreparedStatement statement;
		try {
			statement = prepareStatement(Queries.GET_MEDIA, mediaId);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				media = resultToMedia(result);
				
				LOGGER.debug("Media metadata successfully fetched. Media ID: " + mediaId);
			} else {
				LOGGER.debug("No media with id '" + mediaId + "' found.");
			}

			statement.close();
		} catch (SQLException e) {
			LOGGER.error("Error while fetching media metadata: " + e.getMessage(), e);
			throw new MetadataSourceException(e.getMessage(), e);
		}
		
		return media;
	}
	
	public String getMediaUploader(String mediaId) throws MetadataSourceException {
		LOGGER.debug("Getting media uploader. Media ID: " + mediaId);
		
		String uploader = null;
		
		PreparedStatement statement;
		try {
			statement = prepareStatement(Queries.GET_MEDIA_UPLOADER, mediaId);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				uploader = result.getString(1); 
				LOGGER.debug("Media metadata successfully fetched. Media ID: " + mediaId);
			} else {
				LOGGER.debug("No media with id '" + mediaId + "' found.");
			}

			statement.close();
		} catch (SQLException e) {
			LOGGER.error("Error while fetching media metadata: " + e.getMessage(), e);
			throw new MetadataSourceException(e.getMessage(), e);
		}
		
		return uploader;
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
	
	public void updateMediaLastUpdated(String mediaId) throws MetadataSourceException {
		LOGGER.debug("Updating last updated date. Media ID: " + mediaId);
		
		PreparedStatement statement;
		try {
			Timestamp now = new Timestamp((new Date()).getTime());

			statement = prepareStatement(Queries.UPDATE_MEDIA_LAST_UPDATED, now, mediaId);
			statement.execute();
			statement.close();
			
			LOGGER.debug("Media last updated date successfully updated. Media ID: " + mediaId);
		} catch (SQLException e) {
			LOGGER.error("Error while updating media last updated date", e);
			throw new MetadataSourceException(e.getMessage(), e);
		}
	}
	
	public void updateMediaFields(Media media) throws MetadataSourceException {
		LOGGER.debug("Updating media fields. Media ID: " + media.getId());
		
		PreparedStatement statement;
		try {
			statement = prepareStatement(Queries.UPDATE_MEDIA_FIELDS, media.getFileName(), media.getTitle(), media.getDescription(), media.getId());
			statement.execute();
			statement.close();
			
			LOGGER.debug("Media fields updated. Media ID: " + media.getId());
		} catch (SQLException e) {
			LOGGER.error("Error while updating media fields", e);
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
	
	public List<String> getPreviewsFromMedia(String mediaId) throws MetadataSourceException {
		LOGGER.debug("Getting all previews from media: " + mediaId);
		
		List<String> previews = new LinkedList<String>();
		
		PreparedStatement statement;
		try {
			statement = prepareStatement(Queries.GET_MEDIA_PREVIEWS, mediaId);

			ResultSet result = statement.executeQuery();
			
			while (result.next()) {
				String previewId = result.getString(1);
				
				previews.add(previewId);
				LOGGER.debug("Preview successfuly fetched. Preview ID: " + previewId);
			}

			statement.close();
		} catch (SQLException e) {
			LOGGER.error("Error while fetching media previews", e);
			throw new MetadataSourceException(e.getMessage(), e);
		}
		
		return previews;
	}
	
	public String getPreviewId(String mediaId, int height, int width) throws MetadataSourceException {
		LOGGER.debug("Getting preview from media: " + mediaId);
		
		String previewId = null;
		
		PreparedStatement statement;
		try {
			statement = prepareStatement(Queries.GET_MEDIA_PREVIEW, mediaId, height, width);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				previewId = result.getString(1); 
				LOGGER.debug("Preview successfuly fetched. Preview ID: " + previewId);
			} else {
				LOGGER.debug("No previews for media '" + mediaId + "' found.");
			}

			statement.close();
		} catch (SQLException e) {
			LOGGER.error("Error while fetching media preview", e);
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
	
	public void deletePreviewsFromMedia(String mediaId) throws MetadataSourceException {
		LOGGER.debug("Deleting previews. Media ID: " + mediaId);
		
		PreparedStatement statement;
		try {
			statement = prepareStatement(Queries.DELETE_PREVIEWS_FROM_MEDIA, mediaId);
			statement.execute();
			statement.close();
			
			LOGGER.debug("Previews metadata successfully deleted. Media ID: " + mediaId);
		} catch (SQLException e) {
			LOGGER.error("Error while deleting previews metadata", e);
			throw new MetadataSourceException(e.getMessage(), e);
		}
	}
	
	// Avatars
	
	public void storeAvatar(Media media) throws MetadataSourceException {
		LOGGER.debug("Store " + media.getEntityId() + " avatar. Media ID: " + media.getId());
		
		PreparedStatement statement;
		try {
			statement = prepareStatement(Queries.SAVE_AVATAR, media.getId(), media.getEntityId());

			statement.execute();
			statement.close();
			
			LOGGER.debug("Avatar successfully stored. Media ID: " + media.getId());
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
	
	public void deleteEntityAvatar(String entityId) throws MetadataSourceException {
		LOGGER.debug("Deleting avatar from '" + entityId + "' avatar");
		
		PreparedStatement statement;
		try {
			statement = prepareStatement(Queries.DELETE_ENTITY_AVATAR, entityId);
			statement.execute();
			statement.close();
			
			LOGGER.debug("Avatar from '" + entityId + "' successfully deleted");
		} catch (SQLException e) {
			LOGGER.error("Error while deleting avatar", e);
			throw new MetadataSourceException(e.getMessage(), e);
		}
	}
}
