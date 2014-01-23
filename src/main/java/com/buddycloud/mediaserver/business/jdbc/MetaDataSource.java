/*
 * Copyright 2012 buddycloud
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.buddycloud.mediaserver.business.jdbc;

import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.business.model.Preview;
import com.buddycloud.mediaserver.commons.MediaServerConfiguration;
import com.buddycloud.mediaserver.commons.exception.CreateDataSourceException;
import com.buddycloud.mediaserver.commons.exception.MetadataSourceException;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Responsible for creating and handling database connections
 * and operations.
 *
 * @author Rodrigo Duarte Sousa - rodrigodsousa@gmail.com
 */
public class MetaDataSource {
	private static Logger LOGGER = LoggerFactory.getLogger(MetaDataSource.class);

	private ComboPooledDataSource dataSource;
	private Properties configuration;

	public MetaDataSource() {
		this.configuration = MediaServerConfiguration.getInstance().getConfiguration();

		try {
			createDataSource();
		} catch (PropertyVetoException e) {
			LOGGER.error(
					"Error during data source creation: " + e.getMessage(), e);
			throw new CreateDataSourceException(e.getMessage(), e);
		}
	}

	private PreparedStatement prepareStatement(String sql, Object... args)
			throws SQLException {
		PreparedStatement prepareStatement = dataSource.getConnection().prepareStatement(sql);
		for (int i = 1; i <= args.length; i++) {
			prepareStatement.setObject(i, args[i - 1]);
		}
		return prepareStatement;
	}

	public void close(Statement statement) {
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

	/**
	 * Returns the database connection.
	 * @return the connection.
	 * @throws SQLException if an error occurs while getting the connection object.
	 */
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	private void createDataSource() throws PropertyVetoException {
		this.dataSource = new ComboPooledDataSource();
		dataSource.setDriverClass(configuration
						.getProperty(MediaServerConfiguration.JDBC_DRIVER_CLASS_PROPERTY));
		dataSource.setJdbcUrl(configuration
				.getProperty(MediaServerConfiguration.JDBC_DB_URL_PROPERTY));
	}

	// Medias

	public void storeMedia(Media media) throws MetadataSourceException {
		LOGGER.debug("Store media metadata. Media ID: " + media.getId());

		PreparedStatement statement = null;
		try {
			statement = prepareStatement(Queries.SAVE_MEDIA, media.getId(),
					media.getFileName(), media.getEntityId(),
					media.getAuthor(), media.getTitle(),
					media.getDescription(), media.getMimeType(),
					media.getFileExtension(), media.getShaChecksum(),
					media.getFileSize(), media.getLength(), media.getHeight(),
					media.getWidth());
			
			statement.execute();

			LOGGER.debug("Media metadata successfully stored. Media ID: "
					+ media.getId());
		} catch (SQLException e) {
			LOGGER.error("Error while saving media metadata", e);
			throw new MetadataSourceException(e.getMessage(), e);
		} finally {
			close(statement);
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

	public List<Media> getMediasInfo(String entityId, Integer max, String after)
			throws MetadataSourceException {
		LOGGER.debug("Get medias info from: " + entityId);

		List<Media> medias = new ArrayList<Media>();

		PreparedStatement statement = null;
		try {
			if (max != null) {
				String sql;
				if (after != null) {
					sql = Queries.GET_MEDIAS_INFO_AFTER.replaceAll("#", max.toString());
					statement = prepareStatement(sql, entityId, after);
				} else {
					sql = Queries.GET_MEDIAS_INFO_MAX.replaceAll("#", max.toString());
					statement = prepareStatement(sql, entityId);
				}
			} else {
				statement = prepareStatement(Queries.GET_MEDIAS_INFO, entityId);
			}
			
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				medias.add(resultToMedia(result));
			}

			LOGGER.debug("Medias info sucessfully fetched");
		} catch (SQLException e) {
			LOGGER.error("Error while fetching medias info", e);
			throw new MetadataSourceException(e.getMessage(), e);
		} finally {
			close(statement);
		}

		return medias;
	}

	public Media getMedia(String mediaId) throws MetadataSourceException {
		LOGGER.debug("Getting media. Media ID: " + mediaId);

		Media media = null;

		PreparedStatement statement = null;
		try {
			statement = prepareStatement(Queries.GET_MEDIA, mediaId);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				media = resultToMedia(result);

				LOGGER.debug("Media metadata successfully fetched. Media ID: "
						+ mediaId);
			} else {
				LOGGER.debug("No media with id '" + mediaId + "' found.");
			}
		} catch (SQLException e) {
			LOGGER.error(
					"Error while fetching media metadata: " + e.getMessage(), e);
			throw new MetadataSourceException(e.getMessage(), e);
		} finally {
			close(statement);
		}

		return media;
	}

	public String getMediaUploader(String mediaId)
			throws MetadataSourceException {
		LOGGER.debug("Getting media uploader. Media ID: " + mediaId);

		String uploader = null;

		PreparedStatement statement = null;
		try {
			statement = prepareStatement(Queries.GET_MEDIA_UPLOADER, mediaId);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				uploader = result.getString(1);
				LOGGER.debug("Media metadata successfully fetched. Media ID: "
						+ mediaId);
			} else {
				LOGGER.debug("No media with id '" + mediaId + "' found.");
			}
		} catch (SQLException e) {
			LOGGER.error(
					"Error while fetching media metadata: " + e.getMessage(), e);
			throw new MetadataSourceException(e.getMessage(), e);
		} finally {
			close(statement);
		}

		return uploader;
	}

	public String getMediaMimeType(String mediaId)
			throws MetadataSourceException {
		LOGGER.debug("Getting media type. Media ID: " + mediaId);

		String mimeType = null;

		PreparedStatement statement = null;
		try {
			statement = prepareStatement(Queries.GET_MEDIA_MIME_TYPE, mediaId);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				mimeType = result.getString(1);
				LOGGER.debug("Media metadata successfully fetched. Media ID: "
						+ mediaId);
			} else {
				LOGGER.debug("No media with id '" + mediaId + "' found.");
			}
		} catch (SQLException e) {
			LOGGER.error(
					"Error while fetching media metadata: " + e.getMessage(), e);
			throw new MetadataSourceException(e.getMessage(), e);
		} finally {
			close(statement);
		}

		return mimeType;
	}

	public String getMediaExtension(String mediaId)
			throws MetadataSourceException {
		LOGGER.debug("Getting media extension. Media ID: " + mediaId);

		String mimeType = null;

		PreparedStatement statement = null;
		try {
			statement = prepareStatement(Queries.GET_MEDIA_EXTENSION, mediaId);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				mimeType = result.getString(1);
				LOGGER.debug("Media metadata successfully fetched. Media ID: "
						+ mediaId);
			} else {
				LOGGER.debug("No media with id '" + mediaId + "' found.");
			}
		} catch (SQLException e) {
			LOGGER.error(
					"Error while fetching media metadata: " + e.getMessage(), e);
			throw new MetadataSourceException(e.getMessage(), e);
		} finally {
			close(statement);
		}

		return mimeType;
	}

	public void updateMediaLastUpdated(String mediaId)
			throws MetadataSourceException {
		LOGGER.debug("Updating last updated date. Media ID: " + mediaId);

		PreparedStatement statement = null;
		try {
			Timestamp now = new Timestamp((new Date()).getTime());

			statement = prepareStatement(Queries.UPDATE_MEDIA_LAST_UPDATED,
					now, mediaId);
			statement.execute();

			LOGGER.debug("Media last updated date successfully updated. Media ID: "
					+ mediaId);
		} catch (SQLException e) {
			LOGGER.error("Error while updating media last updated date", e);
			throw new MetadataSourceException(e.getMessage(), e);
		} finally {
			close(statement);
		}
	}

	public void updateMediaFields(Media media) throws MetadataSourceException {
		LOGGER.debug("Updating media fields. Media ID: " + media.getId());

		PreparedStatement statement = null;
		try {
			statement = prepareStatement(Queries.UPDATE_MEDIA_FIELDS,
					media.getFileName(), media.getTitle(),
					media.getDescription(), media.getId());
			statement.execute();

			LOGGER.debug("Media fields updated. Media ID: " + media.getId());
		} catch (SQLException e) {
			LOGGER.error("Error while updating media fields", e);
			throw new MetadataSourceException(e.getMessage(), e);
		} finally {
			close(statement);
		}
	}

	public void deleteMedia(String mediaId) throws MetadataSourceException {
		LOGGER.debug("Deleting media metadata. Media ID: " + mediaId);

		PreparedStatement statement = null;
		try {
			statement = prepareStatement(Queries.DELETE_MEDIA, mediaId);
			statement.execute();

			LOGGER.debug("Media metadata successfully deleted. Media ID: "
					+ mediaId);
		} catch (SQLException e) {
			LOGGER.error("Error while deleting media metadata", e);
			throw new MetadataSourceException(e.getMessage(), e);
		} finally {
			close(statement);
		}
	}

	// Previews

	public void storePreview(Preview preview) throws MetadataSourceException {
		LOGGER.debug("Store preview metadata. Preview ID: " + preview.getId());

		PreparedStatement statement = null;
		try {
			statement = prepareStatement(Queries.SAVE_PREVIEW, preview.getId(),
					preview.getMediaId(), preview.getShaChecksum(),
					preview.getFileSize(), preview.getHeight(),
					preview.getWidth(), preview.getMimeType());

			statement.execute();

			LOGGER.debug("Preview metadata successfully stored. Preview ID: "
					+ preview.getId());
		} catch (SQLException e) {
			LOGGER.error("Error while saving preview metadata", e);
			throw new MetadataSourceException(e.getMessage(), e);
		} finally {
			close(statement);
		}
	}

	public List<String> getPreviewsFromMedia(String mediaId)
			throws MetadataSourceException {
		LOGGER.debug("Getting all previews from media: " + mediaId);

		List<String> previews = new LinkedList<String>();

		PreparedStatement statement = null;
		try {
			statement = prepareStatement(Queries.GET_MEDIA_PREVIEWS, mediaId);

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				String previewId = result.getString(1);

				previews.add(previewId);
				LOGGER.debug("Preview successfuly fetched. Preview ID: "
						+ previewId);
			}
		} catch (SQLException e) {
			LOGGER.error("Error while fetching media previews", e);
			throw new MetadataSourceException(e.getMessage(), e);
		} finally {
			close(statement);
		}

		return previews;
	}

	public String getPreviewId(String mediaId, int height, int width)
			throws MetadataSourceException {
		LOGGER.debug("Getting preview from media: " + mediaId);

		String previewId = null;

		PreparedStatement statement = null;
		try {
			statement = prepareStatement(Queries.GET_MEDIA_PREVIEW, mediaId,
					height, width);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				previewId = result.getString(1);
				LOGGER.debug("Preview successfuly fetched. Preview ID: "
						+ previewId);
			} else {
				LOGGER.debug("No previews for media '" + mediaId + "' found.");
			}
		} catch (SQLException e) {
			LOGGER.error("Error while fetching media preview", e);
			throw new MetadataSourceException(e.getMessage(), e);
		} finally {
			close(statement);
		}

		return previewId;
	}
	
	public String getPreviewMimeType(String previewId)
			throws MetadataSourceException {
		LOGGER.debug("Getting previw mime type. Preview ID: " + previewId);

		String mimeType = null;

		PreparedStatement statement = null;
		try {
			statement = prepareStatement(Queries.GET_PREVIEW_MIME_TYPE, previewId);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				mimeType = result.getString(1);
				LOGGER.debug("Preview metadata successfully fetched. Preview ID: "
						+ previewId);
			} else {
				LOGGER.debug("No preview with id '" + previewId + "' found.");
			}
		} catch (SQLException e) {
			LOGGER.error(
					"Error while fetching preview metadata: " + e.getMessage(), e);
			throw new MetadataSourceException(e.getMessage(), e);
		} finally {
			close(statement);
		}

		return mimeType;
	}

	public void deletePreview(String previewId) throws MetadataSourceException {
		LOGGER.debug("Deleting preview metadata. Preview ID: " + previewId);

		PreparedStatement statement = null;
		try {
			statement = prepareStatement(Queries.DELETE_PREVIEW, previewId);
			statement.execute();

			LOGGER.debug("Preview metadata successfully deleted. Preview ID: "
					+ previewId);
		} catch (SQLException e) {
			LOGGER.error("Error while deleting preview metadata", e);
			throw new MetadataSourceException(e.getMessage(), e);
		} finally {
			close(statement);
		}
	}

	public void deletePreviewsFromMedia(String mediaId)
			throws MetadataSourceException {
		LOGGER.debug("Deleting previews. Media ID: " + mediaId);

		PreparedStatement statement = null;
		try {
			statement = prepareStatement(Queries.DELETE_PREVIEWS_FROM_MEDIA,
					mediaId);
			statement.execute();

			LOGGER.debug("Previews metadata successfully deleted. Media ID: "
					+ mediaId);
		} catch (SQLException e) {
			LOGGER.error("Error while deleting previews metadata", e);
			throw new MetadataSourceException(e.getMessage(), e);
		} finally {
			close(statement);
		}
	}

	// Avatars

	public void storeAvatar(Media media) throws MetadataSourceException {
		LOGGER.debug("Store " + media.getEntityId() + " avatar. Media ID: "
				+ media.getId());

		PreparedStatement statement = null;
		try {
			statement = prepareStatement(Queries.SAVE_AVATAR, media.getId(),
					media.getEntityId());
			statement.execute();

			LOGGER.debug("Avatar successfully stored. Media ID: "
					+ media.getId());
		} catch (SQLException e) {
			LOGGER.error("Error while saving avatar", e);
			throw new MetadataSourceException(e.getMessage(), e);
		} finally {
			close(statement);
		}
	}

	public String getEntityAvatarId(String entityId)
			throws MetadataSourceException {
		LOGGER.debug("Getting current avatar id from: " + entityId);

		String mediaId = null;

		PreparedStatement statement = null;
		try {
			statement = prepareStatement(Queries.GET_ENTITY_AVATAR_ID, entityId);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				mediaId = result.getString(1);
				LOGGER.debug("Entity avatar id successfully fetched. Media ID: "
						+ mediaId);
			} else {
				LOGGER.debug("No avatar for '" + entityId + "' found.");
			}
		} catch (SQLException e) {
			LOGGER.error("Error while fetching entity avatar", e);
			throw new MetadataSourceException(e.getMessage(), e);
		} finally {
			close(statement);
		}

		return mediaId;
	}

	public void updateEntityAvatar(String entityId, String mediaId)
			throws MetadataSourceException {
		LOGGER.debug("Updating " + entityId + " avatar");

		PreparedStatement statement = null;
		try {
			statement = prepareStatement(Queries.UPDATE_AVATAR, mediaId,
					entityId);
			statement.execute();

			LOGGER.debug("Entity avatar successfully updated. Entity ID: "
					+ entityId + ". Media ID: " + mediaId);
		} catch (SQLException e) {
			LOGGER.error("Error while updating entity avatar", e);
			throw new MetadataSourceException(e.getMessage(), e);
		} finally {
			close(statement);
		}
	}

	public void deleteEntityAvatar(String entityId)
			throws MetadataSourceException {
		LOGGER.debug("Deleting avatar from '" + entityId + "' avatar");

		PreparedStatement statement = null;
		try {
			statement = prepareStatement(Queries.DELETE_ENTITY_AVATAR, entityId);
			statement.execute();

			LOGGER.debug("Avatar from '" + entityId + "' successfully deleted");
		} catch (SQLException e) {
			LOGGER.error("Error while deleting avatar", e);
			throw new MetadataSourceException(e.getMessage(), e);
		} finally {
			close(statement);
		}
	}
}
