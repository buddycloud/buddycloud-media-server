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
package com.buddycloud.mediaserver.business.dao;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.restlet.Request;
import org.restlet.ext.fileupload.RestletFileUpload;

import com.buddycloud.mediaserver.business.jdbc.MetaDataSource;
import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.business.model.Preview;
import com.buddycloud.mediaserver.business.util.AudioUtils;
import com.buddycloud.mediaserver.business.util.ImageUtils;
import com.buddycloud.mediaserver.business.util.VideoUtils;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.MediaServerConfiguration;
import com.buddycloud.mediaserver.commons.Thumbnail;
import com.buddycloud.mediaserver.commons.exception.FormFieldException;
import com.buddycloud.mediaserver.commons.exception.FormInvalidFieldException;
import com.buddycloud.mediaserver.commons.exception.FormMissingFieldException;
import com.buddycloud.mediaserver.commons.exception.InvalidPreviewFormatException;
import com.buddycloud.mediaserver.commons.exception.MediaNotFoundException;
import com.buddycloud.mediaserver.commons.exception.MetadataSourceException;
import com.buddycloud.mediaserver.commons.exception.UserNotAllowedException;
import com.buddycloud.mediaserver.xmpp.XMPPToolBox;
import com.buddycloud.mediaserver.xmpp.pubsub.PubSubClient;
import com.buddycloud.mediaserver.xmpp.pubsub.capabilities.MemberDecorator;
import com.buddycloud.mediaserver.xmpp.pubsub.capabilities.ModeratorDecorator;
import com.buddycloud.mediaserver.xmpp.pubsub.capabilities.OwnerDecorator;
import com.buddycloud.mediaserver.xmpp.pubsub.capabilities.PublisherDecorator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Provides a Data Access Object to metadata
 * database and media file system.
 *
 * @author Rodrigo Duarte Sousa - rodrigodsousa@gmail.com
 */
public class MediaDAO {

	private static Logger LOGGER = Logger.getLogger(MediaDAO.class);

	protected MetaDataSource dataSource;
	protected PubSubClient pubsub;
	protected Properties configuration;
	protected Gson gson;

	protected MediaDAO() {
		this.dataSource = new MetaDataSource();
		this.pubsub = XMPPToolBox.getInstance().getPubSubClient();
		this.gson = new GsonBuilder().setDateFormat(DateFormat.FULL,
				DateFormat.FULL).create();
		this.configuration = MediaServerConfiguration.getInstance()
				.getConfiguration();
	}

	/**
	 * Deletes a media file ant its metadata.
	 * @param userId the user that is trying to delete media.
	 * @param entityId media channel's id.
	 * @param mediaId media to be deleted.
	 * @throws MetadataSourceException if something goes wrong while retrieving media's metadata.
	 * @throws MediaNotFoundException there is no media with such id.
	 * @throws UserNotAllowedException this {@link userId} is not allowed to perform this operation.
	 */
	public void deleteMedia(String userId, String entityId, String mediaId)
			throws MetadataSourceException, MediaNotFoundException,
			UserNotAllowedException {

		boolean isAvatar = isAvatar(mediaId);
		if (isAvatar) {
			mediaId = dataSource.getEntityAvatarId(entityId);
		}

		boolean isUploader = dataSource.getMediaUploader(mediaId)
				.equals(userId);

		if (!isUploader) {
			boolean isUserAllowed = pubsub.matchUserCapability(userId,
					entityId, new OwnerDecorator(new ModeratorDecorator()));

			if (!isUserAllowed) {
				LOGGER.debug("User '" + userId
						+ "' not allowed to peform delete operation on: "
						+ mediaId);
				throw new UserNotAllowedException(userId);
			}
		}

		String fullDirectoryPath = getDirectory(entityId);
		File media = new File(fullDirectoryPath + File.separator + mediaId);

		if (!media.exists()) {
			throw new MediaNotFoundException(mediaId, entityId);
		}

		LOGGER.debug("Deleting media. Media ID: " + mediaId);

		if (isAvatar) {
			// delete avatars table entry
			dataSource.deleteEntityAvatar(entityId);
		}

		// delete existent previews from media
		deletePreviews(mediaId, fullDirectoryPath);

		// delete file and metadata
		media.delete();
		dataSource.deleteMedia(mediaId);
	}

	protected void deletePreviews(String mediaId, String dirPath)
			throws MetadataSourceException {
		List<String> previews = dataSource.getPreviewsFromMedia(mediaId);

		if (!previews.isEmpty()) {
			for (String previewId : previews) {
				File preview = new File(dirPath + File.separator + previewId);
				preview.delete();
			}

			dataSource.deletePreviewsFromMedia(mediaId);
		}
	}

	/**
	 * Gets an information list from all medias in a channel.
	 * @param userId the user that is trying to request the media lsit.
	 * @param entityId media channel's id.
	 * @throws MetadataSourceException if something goes wrong while retrieving media's metadata.
	 * @throws UserNotAllowedException this {@link userId} is not allowed to perform this operation.
	 */
	public String getMediasInfo(String userId, String entityId, Integer max, String after)
			throws UserNotAllowedException, MetadataSourceException {

		if (userId != null) {
			boolean isUserAllowed = pubsub.matchUserCapability(userId,
					entityId, new OwnerDecorator(new ModeratorDecorator(
							new PublisherDecorator(new MemberDecorator()))));

			if (!isUserAllowed) {
				LOGGER.debug("User '" + userId
						+ "' not allowed to peform get info operation on: "
						+ entityId);
				throw new UserNotAllowedException(userId);
			}
		}

		LOGGER.debug("Getting medias info from: " + entityId);

		List<Media> medias = dataSource.getMediasInfo(entityId, max, after);

		return gson.toJson(medias);
	}

	/**
	 * Gets a media file.
	 * @param userId the user that is trying to get the media.
	 * @param entityId media channel's id.
	 * @param mediaId media to be fetched.
	 * @return media file.
	 * @throws MetadataSourceException if something goes wrong while retrieving media's metadata.
	 * @throws MediaNotFoundException there is no media with such id.
	 * @throws IOException if something goes wrong while getting media file.
	 * @throws UserNotAllowedException this {@link userId} is not allowed to perform this operation.
	 */
	public File getMedia(String userId, String entityId, String mediaId)
			throws MetadataSourceException, MediaNotFoundException,
			IOException, UserNotAllowedException {

		if (isAvatar(mediaId)) {
			return getAvatar(entityId);
		}

		if (userId != null) {
			boolean isUserAllowed = pubsub.matchUserCapability(userId,
					entityId, new OwnerDecorator(new ModeratorDecorator(
							new PublisherDecorator(new MemberDecorator()))));

			if (!isUserAllowed) {
				LOGGER.debug("User '" + userId
						+ "' not allowed to peform get operation on: "
						+ entityId);
				throw new UserNotAllowedException(userId);
			}
		}

		LOGGER.debug("Getting media. Media ID: " + mediaId);

		String fullDirectoryPath = getDirectory(entityId);
		File media = new File(fullDirectoryPath + File.separator + mediaId);

		if (!media.exists()) {
			throw new MediaNotFoundException(mediaId, entityId);
		}

		return media;
	}

	/**
	 * Gets a channel avatar.
	 * @param entityId avatar's channel.
	 * @return avatar file.
	 * @throws MetadataSourceException if something goes wrong while retrieving avatar's metadata.
	 * @throws MediaNotFoundException there is no media representing {@link entityId} avatar.
	 */
	public File getAvatar(String entityId) throws MetadataSourceException,
			MediaNotFoundException, IOException {
		String mediaId = dataSource.getEntityAvatarId(entityId);

		LOGGER.debug("Getting avatar. Entity ID: " + entityId);

		String fullDirectoryPath = getDirectory(entityId);

		File media = new File(fullDirectoryPath + File.separator + mediaId);

		if (!media.exists()) {
			throw new MediaNotFoundException(mediaId, entityId);
		}

		return media;
	}

	/**
	 * Gets a media preview.
	 * @param userId user that is requesting the preview.
	 * @param entityId media's channel.
	 * @param mediaId media to be fetched.
	 * @param size preview size limit.
	 * @return media's {@link Thumbnail}
	 * @throws MetadataSourceException if something goes wrong while retrieving media's metadata.
	 * @throws MediaNotFoundException there is no media with such id.
	 * @throws IOException if something goes wrong while getting preview file.
	 * @throws InvalidPreviewFormatException if the client is not requesting media from an image or video.
	 * @throws UserNotAllowedException this {@link userId} is not allowed to perform this operation.
	 */
	public Thumbnail getMediaPreview(String userId, String entityId,
			String mediaId, Integer size) throws MetadataSourceException,
			MediaNotFoundException, IOException, InvalidPreviewFormatException,
			UserNotAllowedException {

		return getMediaPreview(userId, entityId, mediaId, size, size);
	}

	/**
	 * Gets a media preview.
	 * @param userId user that is requesting the preview.
	 * @param entityId media's channel.
	 * @param mediaId media to be fetched.
	 * @param maxHeight preview height limit.
	 * @param maxWidth preview width limit.
	 * @return media's {@link Thumbnail}
	 * @throws MetadataSourceException if something goes wrong while retrieving media's metadata.
	 * @throws MediaNotFoundException there is no media with such id.
	 * @throws IOException if something goes wrong while getting preview file.
	 * @throws InvalidPreviewFormatException if the client is not requesting media from an image or video.
	 * @throws UserNotAllowedException this {@link userId} is not allowed to perform this operation.
	 */	
	public Thumbnail getMediaPreview(String userId, String entityId,
			String mediaId, Integer maxHeight, Integer maxWidth)
			throws MetadataSourceException, MediaNotFoundException,
			IOException, InvalidPreviewFormatException, UserNotAllowedException {

		if (isAvatar(mediaId)) {
			return getAvatarPreview(userId, entityId, maxHeight, maxWidth);
		}

		if (userId != null) {
			boolean isUserAllowed = pubsub.matchUserCapability(userId,
					entityId, new OwnerDecorator(new ModeratorDecorator(
							new PublisherDecorator(new MemberDecorator()))));

			if (!isUserAllowed) {
				LOGGER.debug("User '" + userId
						+ "' not allowed to get media on: " + entityId);
				throw new UserNotAllowedException(userId);
			}
		}

		LOGGER.debug("Getting media preview. Media ID: " + mediaId);

		return getPreview(entityId, mediaId, maxHeight, maxWidth,
				getDirectory(entityId));
	}

	private Thumbnail getAvatarPreview(String userId, String entityId,
			Integer maxHeight, Integer maxWidth)
			throws MetadataSourceException, MediaNotFoundException,
			IOException, InvalidPreviewFormatException {
		String mediaId = dataSource.getEntityAvatarId(entityId);

		LOGGER.debug("Getting avatar preview. Avatar ID: " + entityId);

		return getPreview(entityId, mediaId, maxHeight, maxWidth,
				getDirectory(entityId));
	}

	/**
	 * Gets a media MIME type.
	 * @param entityId media's channel.
	 * @param mediaId media's unique id.
	 * @return media's MIME type.
	 * @throws MetadataSourceException if something goes wrong while retrieving media's metadata.
	 */		
	public String getMediaType(String entityId, String mediaId)
			throws MetadataSourceException {
		if (isAvatar(mediaId)) {
			mediaId = dataSource.getEntityAvatarId(entityId);
		}

		return dataSource.getMediaMimeType(mediaId);
	}

	/**
	 * Updates media's metadata.
	 * @param userId the user that is requesting the update.
	 * @param entityId media's channel.
	 * @param mediaId media to be updated.
	 * @param request contains which are the metadata to be updated.
	 * @return media's new metadata.
	 * @throws FileUploadException if something goes wrong during request parsing.
	 * @throws MetadataSourceException if something goes wrong while retrieving media's metadata.
	 * @throws FormFieldException the field to be updated is invalid.
	 * @throws MediaNotFoundException there is no media with such id.
	 * @throws UserNotAllowedException the @{link userId} is not allowed to perform this operation.
	 */
	public String updateMedia(String userId, String entityId, String mediaId,
			Request request) throws FileUploadException,
			MetadataSourceException, FormFieldException,
			MediaNotFoundException, UserNotAllowedException {

		if (isAvatar(mediaId)) {
			mediaId = dataSource.getEntityAvatarId(entityId);
		}

		boolean isUploader = dataSource.getMediaUploader(mediaId)
				.equals(userId);
		boolean isMember = pubsub.matchUserCapability(userId, entityId,
				new MemberDecorator());

		if (!(isUploader && isMember)) {
			boolean isUserAllowed = pubsub.matchUserCapability(userId,
					entityId, new OwnerDecorator(new ModeratorDecorator()));

			if (!isUserAllowed) {
				LOGGER.debug("User '" + userId
						+ "' not allowed to peform delete operation on: "
						+ mediaId);
				throw new UserNotAllowedException(userId);
			}
		}

		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(Integer.valueOf(configuration
				.getProperty(MediaServerConfiguration.MEDIA_SIZE_LIMIT_PROPERTY)));

		RestletFileUpload upload = new RestletFileUpload(factory);
		List<FileItem> items = upload.parseRequest(request);

		Media media = dataSource.getMedia(mediaId);

		if (media == null) {
			throw new MediaNotFoundException(mediaId, entityId);
		}

		for (FileItem item : items) {
			final String fieldName = item.getFieldName();

			if (fieldName.equals(Constants.NAME_FIELD)) {
				media.setFileName(item.getString());
			} else if (fieldName.equals(Constants.TITLE_FIELD)) {
				media.setTitle(item.getString());
			} else if (fieldName.equals(Constants.DESC_FIELD)) {
				media.setDescription(item.getString());
			} else {
				throw new FormInvalidFieldException(fieldName);
			}
		}

		dataSource.updateMediaFields(media);

		// Update last updated date
		dataSource.updateMediaLastUpdated(mediaId);
		LOGGER.debug("Media sucessfully updated. Media ID: " + media.getId());

		return gson.toJson(media);
	}

	/**
	 * Uploads media.
	 * @param userId user that is uploading the media.
	 * @param entityId channel where the media will belong.
	 * @param request media file and required upload fields.
	 * @param isAvatar if the media to be uploaded is an avatar.
	 * @return media's metadata, if the upload ends with success
	 * @throws FileUploadException the is something wrong with the request.
	 * @throws MetadataSourceException if something goes wrong while creating media's metadata.
	 * @throws FormFieldException required upload field is not present.
	 * @throws UserNotAllowedException the user {@link userId} is now allowed to upload media in this channel.
	 */
	public String insertMedia(String userId, String entityId, Request request,
			boolean isAvatar) throws FileUploadException,
			MetadataSourceException, FormFieldException,
			UserNotAllowedException {

		boolean isUserAllowed = pubsub.matchUserCapability(userId, entityId,
				new OwnerDecorator(new ModeratorDecorator(
						new PublisherDecorator())));

		if (!isUserAllowed) {
			LOGGER.debug("User '" + userId
					+ "' not allowed to uploade file on: " + entityId);
			throw new UserNotAllowedException(userId);
		}

		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(Integer.valueOf(configuration
				.getProperty(MediaServerConfiguration.MEDIA_SIZE_LIMIT_PROPERTY)));

		RestletFileUpload upload = new RestletFileUpload(factory);
		List<FileItem> items = upload.parseRequest(request);

		String fileName = getFormFieldContent(items, Constants.NAME_FIELD);
		String title = getFormFieldContent(items, Constants.TITLE_FIELD);
		String description = getFormFieldContent(items, Constants.DESC_FIELD);
		String author = getFormFieldContent(items, Constants.AUTHOR_FIELD);

		if (!author.equals(userId)) {
			new FormFieldException("User '" + userId
					+ "' tried to upload media as '" + author + "'");
		}

		FileItem fileField = getFileFormField(items);

		InputStream inputStream = null;
		try {
			inputStream = fileField.getInputStream();
		} catch (IOException e) {
			throw new FileUploadException("Error to get file input stream");
		}

		Media media = storeMedia(fileName, title, description, author,
				entityId, fileField.getContentType(), inputStream, isAvatar);

		LOGGER.debug("Media sucessfully added. Media ID: " + media.getId());

		return gson.toJson(media);
	}

	protected Media storeMedia(String fileName, String title,
			String description, String author, String entityId,
			String mimeType, InputStream inputStream, final boolean isAvatar)
			throws FileUploadException {

		String directory = getDirectory(entityId);
		mkdir(directory);

		// TODO assert id uniqueness
		String mediaId = RandomStringUtils.randomAlphanumeric(20);
		String filePath = directory + File.separator + mediaId;
		File file = new File(filePath);

		LOGGER.debug("Storing new media: " + file.getAbsolutePath());

		try {
			FileOutputStream out = FileUtils.openOutputStream(file);

			int length = 0;
			byte[] bytes = new byte[1024];

			while ((length = inputStream.read(bytes)) != -1) {
				out.write(bytes, 0, length);
			}

			inputStream.close();
			out.flush();
			out.close();
		} catch (IOException e) {
			LOGGER.error("Error while storing file: " + filePath, e);

			throw new FileUploadException(e.getMessage());
		}

		final Media media = createMedia(mediaId, fileName, title, description,
				author, entityId, mimeType, file);

		// store media's metadata
		new Thread() {
			public void start() {
				try {
					dataSource.storeMedia(media);

					if (isAvatar) {
						if (dataSource.getEntityAvatarId(media.getEntityId()) != null) {
							dataSource.updateEntityAvatar(media.getEntityId(),
									media.getId());
						} else {
							dataSource.storeAvatar(media);
						}
					}
				} catch (MetadataSourceException e) {
					// do nothing
				}
			}
		}.start();

		return media;
	}

	protected Thumbnail getPreview(String entityId, String mediaId,
			Integer maxHeight, Integer maxWidth, String mediaDirectory)
			throws MetadataSourceException, IOException,
			InvalidPreviewFormatException, MediaNotFoundException {
		File media = new File(mediaDirectory + File.separator + mediaId);

		if (!media.exists()) {
			throw new MediaNotFoundException(mediaId, entityId);
		}

		String previewId = dataSource
				.getPreviewId(mediaId, maxHeight, maxWidth);

		if (previewId != null) {
			File preview = new File(mediaDirectory + File.separator + previewId);

			if (!preview.exists()) {
				dataSource.deletePreview(previewId);
			} else {
				return new Thumbnail(dataSource.getPreviewMimeType(mediaId), 
						IOUtils.toByteArray(FileUtils.openInputStream(preview)));
			}
		} else {
			// generate random id
			previewId = RandomStringUtils.randomAlphanumeric(20);
		}
		String extension = dataSource.getMediaExtension(mediaId);

		BufferedImage previewImg = null;
		Thumbnail thumbnail = null;
		
		if (ImageUtils.isImage(extension)) {
			previewImg = ImageUtils.createImagePreview(media, maxWidth,
					maxHeight);
			
			thumbnail = new Thumbnail(dataSource.getMediaMimeType(mediaId), 
					ImageUtils.imageToBytes(previewImg, extension));
		} else if (VideoUtils.isVideo(extension)) {
			previewImg = new VideoUtils(media).createVideoPreview(maxWidth,
					maxHeight);
			
			thumbnail = new Thumbnail(VideoUtils.PREVIEW_MIME_TYPE, 
					ImageUtils.imageToBytes(previewImg, VideoUtils.PREVIEW_TYPE));
		} else {
			throw new InvalidPreviewFormatException(extension);
		}
		
		// store preview in another flow
		new StorePreviewThread(previewId, mediaDirectory, thumbnail.getMimeType(), mediaId, maxHeight,
				maxWidth, extension, previewImg).start();
		
		return thumbnail;
	}

	protected boolean isAvatar(String mediaId) {
		return mediaId.equals(Constants.AVATAR_ARG);
	}

	protected String getFormFieldContent(List<FileItem> items, String fieldName)
			throws FormMissingFieldException {
		String field = null;

		for (int i = 0; i < items.size(); i++) {
			FileItem item = items.get(i);
			if (fieldName.equals(item.getFieldName().toLowerCase())) {
				field = item.getString();
				items.remove(i);

				break;
			}
		}

		if (field == null) {
			throw new FormMissingFieldException(fieldName);
		}

		return field;
	}

	protected FileItem getFileFormField(List<FileItem> items)
			throws FormMissingFieldException, FileUploadException {
		FileItem field = null;

		for (int i = 0; i < items.size(); i++) {
			FileItem item = items.get(i);
			if (Constants.FILE_FIELD.equals(item.getFieldName().toLowerCase())) {
				field = item;
				break;
			}
		}

		if (field == null) {
			throw new FormMissingFieldException(Constants.FILE_FIELD);
		}

		return field;
	}

	protected Media createMedia(String mediaId, String fileName, String title,
			String description, String author, String entityId,
			String mimeType, File file) {
		Media media = new Media();
		media.setId(mediaId);
		media.setFileName(fileName);
		media.setEntityId(entityId);
		media.setAuthor(author);
		media.setDescription(description);
		media.setTitle(title);
		media.setFileSize(file.length());
		media.setShaChecksum(getFileShaChecksum(file));
		media.setMimeType(mimeType);

		String fileExtension = getFileExtension(fileName);
		media.setFileExtension(fileExtension);

		// XXX adds a lot of delay to the server side, maybe the client should
		// send those information
		try {
			if (ImageUtils.isImage(fileExtension)) {
				BufferedImage img = ImageIO.read(file);
				media.setHeight(img.getHeight());
				media.setWidth(img.getWidth());
			} else if (VideoUtils.isVideo(fileExtension)) {
				VideoUtils videoUtils = new VideoUtils(file);
				media.setLength(videoUtils.getVideoLength());
				media.setHeight(videoUtils.getVideoHeight());
				media.setWidth(videoUtils.getVideoWidth());
			} else if (AudioUtils.isAudio(fileExtension)) {
				media.setLength(AudioUtils.getAudioLength(file));
			}
		} catch (Throwable t) {
			LOGGER.error("Error while resolving media format properties", t);
		}

		return media;
	}

	protected Preview createPreview(String previewId, String mediaId,
			String mimeType, Integer height, Integer width, File file) {
		Preview preview = new Preview();
		preview.setFileSize(file.length());
		preview.setHeight(height);
		preview.setWidth(width);
		preview.setId(previewId);
		preview.setMediaId(mediaId);
		preview.setShaChecksum(getFileShaChecksum(file));
		preview.setMimeType(mimeType);

		return preview;
	}

	protected String getFileExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	protected String getFileShaChecksum(File file) {
		try {
			return DigestUtils.shaHex(FileUtils.openInputStream(file));
		} catch (IOException e) {
			LOGGER.error("Error during media SHA1 checksum generation.", e);
		}

		return null;
	}

	protected boolean mkdir(String fullDirectoryPath) {
		File directory = new File(fullDirectoryPath);
		return directory.mkdir();
	}

	private String getDirectory(String entityId) {
		return configuration
				.getProperty(MediaServerConfiguration.MEDIA_STORAGE_ROOT_PROPERTY)
				+ File.separator + entityId;
	}

	// Thread responsible to store preview's file and metadata
	private class StorePreviewThread extends Thread {
		private String previewId;
		private String mediaId;
		private Integer height;
		private Integer width;
		private String directory;
		private String extension;
		private String mimeType;
		private BufferedImage img;

		StorePreviewThread(String previewId, String directory, String mediaId,
				String mimeType, Integer height, Integer width, String extension,
				BufferedImage img) {
			this.previewId = previewId;
			this.directory = directory;
			this.mediaId = mediaId;
			this.height = height;
			this.width = width;
			this.extension = extension;
			this.mimeType = mimeType;
			this.img = img;
		}

		public void start() {
			try {
				File previewFile = ImageUtils.storeImageIntoFile(img,
						extension, directory + File.separator + previewId);

				Preview preview = createPreview(previewId, mediaId, mimeType,
						height, width, previewFile);

				dataSource.storePreview(preview);
			} catch (Exception e) {
				LOGGER.error("Error while storing preview: " + previewId
						+ ". Media ID: " + mediaId, e);
			}
		}
	}
}
