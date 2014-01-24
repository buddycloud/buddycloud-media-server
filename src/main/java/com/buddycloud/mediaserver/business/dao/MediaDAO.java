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

import com.buddycloud.mediaserver.business.jdbc.MetaDataSource;
import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.business.model.Preview;
import com.buddycloud.mediaserver.business.util.AudioUtils;
import com.buddycloud.mediaserver.business.util.ImageUtils;
import com.buddycloud.mediaserver.business.util.MimeTypeMapping;
import com.buddycloud.mediaserver.business.util.VideoUtils;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.MediaFile;
import com.buddycloud.mediaserver.commons.MediaServerConfiguration;
import com.buddycloud.mediaserver.commons.exception.InvalidPreviewFormatException;
import com.buddycloud.mediaserver.commons.exception.MediaNotFoundException;
import com.buddycloud.mediaserver.commons.exception.MetadataSourceException;
import com.buddycloud.mediaserver.commons.exception.UserNotAllowedException;
import com.buddycloud.mediaserver.xmpp.XMPPToolBox;
import com.buddycloud.mediaserver.xmpp.pubsub.PubSubClient;
import com.buddycloud.mediaserver.xmpp.pubsub.capabilities.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.engine.util.Base64;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.List;
import java.util.Properties;

/**
 * Provides a Data Access Object to metadata
 * database and media file system.
 *
 * @author Rodrigo Duarte Sousa - rodrigodsousa@gmail.com
 */
public class MediaDAO {

	private static Logger LOGGER = LoggerFactory.getLogger(MediaDAO.class);

	protected MetaDataSource dataSource;
	protected Properties configuration;
	protected Gson gson;

	
	protected MediaDAO() {
		this.dataSource = new MetaDataSource();
		this.gson = new GsonBuilder().setDateFormat(DateFormat.FULL,
				DateFormat.FULL).create();
		this.configuration = MediaServerConfiguration.getInstance()
				.getConfiguration();
	}

	/**
	 * Deletes a media file ant its metadata.
	 * @param userJID the user that is trying to delete media.
	 * @param entityId media channel's id.
	 * @param mediaId media to be deleted.
	 * @throws MetadataSourceException if something goes wrong while retrieving media's metadata.
	 * @throws MediaNotFoundException there is no media with such id.
	 * @throws UserNotAllowedException this {@param userJID} is not allowed to perform this operation.
	 */
	public void deleteMedia(String userJID, String entityId, String mediaId)
			throws MetadataSourceException, MediaNotFoundException,
			UserNotAllowedException {

		boolean isAvatar = isAvatar(mediaId);
		if (isAvatar) {
			mediaId = dataSource.getEntityAvatarId(entityId);
		}

		boolean isUploader = dataSource.getMediaUploader(mediaId)
				.equals(userJID);

		if (!isUploader) {
			if (!isUserAllowed(userJID, entityId, new OwnerDecorator(new ModeratorDecorator()))) {
				LOGGER.debug("User '" + userJID
						+ "' not allowed to peform delete operation on: "
						+ mediaId);
				throw new UserNotAllowedException(userJID);
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

		// delete file and metadata. Best effort
		media.delete();
		dataSource.deleteMedia(mediaId);
	}

	protected void deletePreviews(String mediaId, String dirPath)
			throws MetadataSourceException {
		List<String> previews = dataSource.getPreviewsFromMedia(mediaId);

		if (!previews.isEmpty()) {
			for (String previewId : previews) {
				File preview = new File(dirPath + File.separator + previewId);
                // Best effort
				preview.delete();
			}

			dataSource.deletePreviewsFromMedia(mediaId);
		}
	}

    /**
     * Gets the media metadata.
     * @param userJID the user that is trying to request the media lsit.
     * @param entityId media channel's id.
     * @param mediaId the media's id
     * @throws MetadataSourceException if something goes wrong while retrieving media's metadata.
     * @throws UserNotAllowedException this {@param userJID} is not allowed to perform this operation.
     * @throws MediaNotFoundException if there is not such media metadata
     */
    public String getMediaInfo(String userJID, String entityId, String mediaId)
            throws UserNotAllowedException, MetadataSourceException, MediaNotFoundException {
        if (isAvatar(mediaId)) {
            LOGGER.debug("Getting media info for " + entityId + " avatar");
            mediaId = dataSource.getEntityAvatarId(entityId);
            if (mediaId == null) {
                throw new MediaNotFoundException("avatar", entityId);
            }

            return gson.toJson(dataSource.getMedia(mediaId));
        }

        if (userJID != null) {
            if (!isUserAllowed(userJID, entityId, new OwnerDecorator(new ModeratorDecorator(
                    new PublisherDecorator(new MemberDecorator()))))) {
                LOGGER.debug("User '" + userJID
                        + "' not allowed to peform get info operation on: "
                        + entityId);
                throw new UserNotAllowedException(userJID);
            }
        }

        LOGGER.debug("Getting media info: " + mediaId);

        Media media = dataSource.getMedia(mediaId);
        if (media == null) {
            throw new MediaNotFoundException(mediaId, entityId);
        }

        return gson.toJson(media);
    }

	/**
	 * Gets an information list from all medias in a channel.
	 * @param userJID the user that is trying to request the media lsit.
	 * @param entityId media channel's id.
	 * @throws MetadataSourceException if something goes wrong while retrieving media's metadata.
	 * @throws UserNotAllowedException this {@param userJID} is not allowed to perform this operation.
	 */
	public String getMediasInfo(String userJID, String entityId, Integer max, String after)
			throws UserNotAllowedException, MetadataSourceException {

		if (userJID != null) {
			if (!isUserAllowed(userJID, entityId, new OwnerDecorator(new ModeratorDecorator(
                    new PublisherDecorator(new MemberDecorator()))))) {
				LOGGER.debug("User '" + userJID
						+ "' not allowed to peform get info operation on: "
						+ entityId);
				throw new UserNotAllowedException(userJID);
			}
		}

		LOGGER.debug("Getting medias info from: " + entityId);

		List<Media> medias = dataSource.getMediasInfo(entityId, max, after);

		return gson.toJson(medias);
	}

	/**
	 * Gets a media file.
	 * @param userJID the user that is trying to get the media.
	 * @param entityId media channel's id.
	 * @param mediaId media to be fetched.
	 * @return media file.
	 * @throws MetadataSourceException if something goes wrong while retrieving media's metadata.
	 * @throws MediaNotFoundException there is no media with such id.
	 * @throws IOException if something goes wrong while getting media file.
	 * @throws UserNotAllowedException this {@param userJID} is not allowed to perform this operation.
	 */
	public MediaFile<File> getMedia(String userJID, String entityId, String mediaId)
			throws MetadataSourceException, MediaNotFoundException,
			IOException, UserNotAllowedException {

		if (isAvatar(mediaId)) {
			return getAvatar(entityId);
		}

		if (userJID != null) {
            if (!isUserAllowed(userJID, entityId, new OwnerDecorator(new ModeratorDecorator(
                    new PublisherDecorator(new MemberDecorator()))))) {
				LOGGER.debug("User '" + userJID
						+ "' not allowed to peform get operation on: "
						+ entityId);
				throw new UserNotAllowedException(userJID);
			}
		}

		LOGGER.debug("Getting media. Media ID: " + mediaId);

		String fullDirectoryPath = getDirectory(entityId);
		File file = new File(fullDirectoryPath + File.separator + mediaId);

		if (!file.exists()) {
			throw new MediaNotFoundException(mediaId, entityId);
		}

        Media media = dataSource.getMedia(mediaId);

        return new MediaFile<File>(media.getMimeType(), file, media.getLastUpdatedDate());
	}

	/**
	 * Gets a channel avatar.
	 * @param entityId avatar's channel.
	 * @return avatar file.
	 * @throws MetadataSourceException if something goes wrong while retrieving avatar's metadata.
	 * @throws MediaNotFoundException there is no media representing {@param entityId} avatar.
	 */
	public MediaFile<File> getAvatar(String entityId) throws MetadataSourceException,
            MediaNotFoundException, IOException {
		String mediaId = dataSource.getEntityAvatarId(entityId);
        if (mediaId == null) {
            throw new MediaNotFoundException("avatar", entityId);
        }

		LOGGER.debug("Getting avatar. Entity ID: " + entityId);

		String fullDirectoryPath = getDirectory(entityId);

		File file = new File(fullDirectoryPath + File.separator + mediaId);

		if (!file.exists()) {
			throw new MediaNotFoundException(mediaId, entityId);
		}

        Media media = dataSource.getMedia(mediaId);

		return new MediaFile<File>(media.getMimeType(), file, media.getLastUpdatedDate());
	}

	/**
	 * Gets a media preview.
	 * @param userJID user that is requesting the preview.
	 * @param entityId media's channel.
	 * @param mediaId media to be fetched.
	 * @param maxHeight preview height limit.
	 * @param maxWidth preview width limit.
	 * @return media's {@link MediaFile}
	 * @throws MetadataSourceException if something goes wrong while retrieving media's metadata.
	 * @throws MediaNotFoundException there is no media with such id.
	 * @throws IOException if something goes wrong while getting preview file.
	 * @throws InvalidPreviewFormatException if the client is not requesting media from an image or video.
	 * @throws UserNotAllowedException this {@param userJID} is not allowed to perform this operation.
	 */	
	public MediaFile<byte[]> getMediaPreview(String userJID, String entityId,
			String mediaId, Integer maxHeight, Integer maxWidth)
					throws MetadataSourceException, MediaNotFoundException,
					IOException, InvalidPreviewFormatException, UserNotAllowedException {

		if (isAvatar(mediaId)) {
			return getAvatarPreview(entityId, maxHeight, maxWidth);
		}

		if (userJID != null) {
            if (!isUserAllowed(userJID, entityId, new OwnerDecorator(new ModeratorDecorator(
                    new PublisherDecorator(new MemberDecorator()))))) {
				LOGGER.debug("User '" + userJID
						+ "' not allowed to get media on: " + entityId);
				throw new UserNotAllowedException(userJID);
			}
		}

		LOGGER.debug("Getting media preview. Media ID: " + mediaId);

		return getPreview(entityId, mediaId, maxHeight, maxWidth,
				getDirectory(entityId));
	}

	private MediaFile<byte[]> getAvatarPreview(String entityId,
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
	 * @throws MediaNotFoundException if there is no media for the entity's avatar.
	 */		
	public String getMediaType(String entityId, String mediaId)
			throws MetadataSourceException, MediaNotFoundException {
		if (isAvatar(mediaId)) {
			mediaId = dataSource.getEntityAvatarId(entityId);

			if (mediaId == null) {
				throw new MediaNotFoundException("avatar", entityId);
			}
		}

		return dataSource.getMediaMimeType(mediaId);
	}

	/**
	 * Updates media's metadata.
	 * @param userJID the user that is requesting the update.
	 * @param entityId media's channel.
	 * @param mediaId media to be updated.
	 * @param form contains which are the metadata to be updated.
	 * @return media's new metadata.
	 * @throws MetadataSourceException if something goes wrong while retrieving media's metadata.
	 * @throws MediaNotFoundException there is no media with such id.
	 * @throws UserNotAllowedException the @{link userJID} is not allowed to perform this operation.
	 */
	public String updateMedia(String userJID, String entityId, String mediaId,
			Form form) throws MetadataSourceException, MediaNotFoundException, 
			UserNotAllowedException {

		if (isAvatar(mediaId)) {
			mediaId = dataSource.getEntityAvatarId(entityId);
		}

		boolean isUploader = dataSource.getMediaUploader(mediaId)
				.equals(userJID);

		if (!isUploader) {
            if (!isUserAllowed(userJID, entityId, new OwnerDecorator(new ModeratorDecorator()))) {
				LOGGER.debug("User '" + userJID
						+ "' not allowed to peform delete operation on: "
						+ mediaId);
				throw new UserNotAllowedException(userJID);
			}
		}

		Media media = dataSource.getMedia(mediaId);

		if (media == null) {
			throw new MediaNotFoundException(mediaId, entityId);
		}

		// get form fields
		String fileName = form.getFirstValue(Constants.NAME_FIELD);
		if (fileName != null) {
			media.setFileName(fileName);
		}

		String title = form.getFirstValue(Constants.TITLE_FIELD);
		if (title != null) {
			media.setTitle(title);
		}

		String description = form.getFirstValue(Constants.DESC_FIELD);
		if (description != null) {
			media.setDescription(description);
		}

		dataSource.updateMediaFields(media);

		// Update last updated date
		dataSource.updateMediaLastUpdated(mediaId);
		LOGGER.debug("Media sucessfully updated. Media ID: " + media.getId());

		return gson.toJson(media);
	}

	/**
	 * Uploads media from web form.
	 * @param userJID user that is uploading the media.
	 * @param entityId channel where the media will belong.
	 * @param form web form containing the media.
	 * @param isAvatar if the media to be uploaded is an avatar.
	 * @return media's metadata, if the upload ends with success
	 * @throws FileUploadException the is something wrong with the request.
	 * @throws UserNotAllowedException the user {@param userJID} is now allowed to upload media in this channel.
	 */
	public String insertWebFormMedia(String userJID, String entityId, Form form,
			boolean isAvatar) throws FileUploadException, UserNotAllowedException {

		LOGGER.debug("User '" + userJID
				+ "' trying to upload web-form media on: " + entityId);

        if (!isUserAllowed(userJID, entityId, new OwnerDecorator(new ModeratorDecorator(
                new PublisherDecorator())))) {
			LOGGER.debug("User '" + userJID
					+ "' not allowed to uploade file on: " + entityId);
			throw new UserNotAllowedException(userJID);
		}

        int fileSizeLimit = Integer.valueOf(configuration
                .getProperty(MediaServerConfiguration.MEDIA_SIZE_LIMIT_PROPERTY));

		// get form fields
		String fileName = form.getFirstValue(Constants.NAME_FIELD);
		String title = form.getFirstValue(Constants.TITLE_FIELD);
		String description = form.getFirstValue(Constants.DESC_FIELD);
        String fileSizeStr = form.getFirstValue(Constants.SIZE_FIELD);

        if (fileSizeStr != null) {
            int fileSize = Integer.valueOf(fileSizeStr);

            // First size checking
            if (fileSize > fileSizeLimit) {
                throw new FileUploadException("File content size bigger than: " + fileSizeLimit);
            }
        }

		String data = form.getFirstValue(Constants.DATA_FIELD);

		if (data == null) {
			throw new FileUploadException("Must provide the file data.");
		}

		byte[] dataArray = Base64.decode(data);

        // Second size checking
        int fileSize = dataArray.length;
        if (fileSize > fileSizeLimit) {
            throw new FileUploadException("File content size bigger than: " + fileSizeLimit);
        }

		String contentType = form.getFirstValue(Constants.TYPE_FIELD);
		if (contentType == null) {
			throw new FileUploadException("Must provide a " + Constants.TYPE_FIELD + " for the uploaded file.");
		}

		// storing
		Media media = storeMedia(fileName, title, description, new JID(userJID).toBareJID(),
				entityId, contentType, dataArray, isAvatar);

		LOGGER.debug("Media sucessfully added. Media ID: " + media.getId());

		return gson.toJson(media);
	}

	/**
	 * Uploads media.
	 * @param userJID user that is uploading the media.
	 * @param entityId channel where the media will belong.
	 * @param request media file and required upload fields.
	 * @param isAvatar if the media to be uploaded is an avatar.
	 * @return media's metadata, if the upload ends with success
	 * @throws FileUploadException the is something wrong with the request.
	 * @throws UserNotAllowedException the user {@param userJID} is now allowed to upload media in this channel.
	 */
	public String insertFormDataMedia(String userJID, String entityId, Request request,
			boolean isAvatar) throws FileUploadException, UserNotAllowedException {

		LOGGER.debug("User '" + userJID
				+ "' trying to upload form-data media in: " + entityId);

        if (!isUserAllowed(userJID, entityId, new OwnerDecorator(new ModeratorDecorator(
                new PublisherDecorator())))) {
			LOGGER.debug("User '" + userJID
					+ "' not allowed to upload file in: " + entityId);
			throw new UserNotAllowedException(userJID);
		}

		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(Integer.valueOf(configuration
				.getProperty(MediaServerConfiguration.MEDIA_TO_DISK_THRESHOLD_PROPERTY)));

		List<FileItem> items;
		
		try {
			RestletFileUpload upload = new RestletFileUpload(factory);
			items = upload.parseRequest(request);
		} catch (Throwable e) {
			throw new FileUploadException("Invalid request data.");
		}

        int fileSizeLimit = Integer.valueOf(configuration
                .getProperty(MediaServerConfiguration.MEDIA_SIZE_LIMIT_PROPERTY));

		// get form fields
		String fileName = getFormFieldContent(items, Constants.NAME_FIELD);
		String title = getFormFieldContent(items, Constants.TITLE_FIELD);
		String description = getFormFieldContent(items, Constants.DESC_FIELD);
		String contentType = getFormFieldContent(items, Constants.TYPE_FIELD);
        String fileSizeStr = getFormFieldContent(items, Constants.SIZE_FIELD);

        if (fileSizeStr != null) {
            int fileSize = Integer.valueOf(fileSizeStr);

            // First size checking
            if (fileSize > fileSizeLimit) {
                throw new FileUploadException("File content size bigger than: " + fileSizeLimit);
            }
        }

		FileItem fileField = getFileFormField(items);

		if (fileField == null) {
			throw new FileUploadException("Must provide the file data.");
		}

        // Second size checking
        if (fileField.getSize() > fileSizeLimit) {
            throw new FileUploadException("File content size bigger than: " + fileSizeLimit);
        }

        byte[] dataArray = fileField.get();

		if (contentType == null) {
			if (fileField.getContentType() != null) {
				contentType = fileField.getContentType();
			} else {
				throw new FileUploadException("Must provide a " + Constants.TYPE_FIELD + 
						" for the uploaded file.");
			}
		}

		// storing
		Media media = storeMedia(fileName, title, description, new JID(userJID).toBareJID(),
				entityId, contentType, dataArray, isAvatar);

		LOGGER.debug("Media sucessfully added. Media ID: " + media.getId());

		return gson.toJson(media);
	}

	protected Media storeMedia(String fileName, String title,
			String description, String author, String entityId,
			String mimeType, byte[] data, final boolean isAvatar)
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

			out.write(data);
			out.close();
		} catch (IOException e) {
			LOGGER.error("Error while storing file: " + filePath, e);
			throw new FileUploadException(e.getMessage());
		}

		final Media media = createMedia(mediaId, fileName, title, description,
				author, entityId, mimeType, file, isAvatar);
		
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
					LOGGER.error("Database error", e);
				}
			}
		}.start();

		return media;
	}

	protected MediaFile<byte[]> getPreview(String entityId, String mediaId, Integer maxHeight,
			Integer maxWidth, String mediaDirectory) throws MetadataSourceException, IOException,
            InvalidPreviewFormatException, MediaNotFoundException {
		File file = new File(mediaDirectory + File.separator + mediaId);

		if (!file.exists()) {
			throw new MediaNotFoundException(mediaId, entityId);
		}

        Media media = dataSource.getMedia(mediaId);
        String previewId = dataSource
				.getPreviewId(mediaId, maxHeight, maxWidth);

        if (previewId != null) {
            File preview = new File(mediaDirectory + File.separator + previewId);

            if (!preview.exists()) {
                dataSource.deletePreview(previewId);
            } else {
                return new MediaFile<byte[]>(dataSource.getPreviewMimeType(previewId),
						IOUtils.toByteArray(FileUtils.openInputStream(preview)),
                        media.getLastUpdatedDate());
			}
		} else {
			// generate random id
			previewId = RandomStringUtils.randomAlphanumeric(20);
		}

		return buildNewPreview(file, media, previewId, mediaDirectory, maxHeight, maxWidth);
	}

	private MediaFile<byte[]> buildNewPreview(File file, Media media, String previewId, String mediaDirectory,
			Integer maxHeight, Integer maxWidth) throws MetadataSourceException, IOException, InvalidPreviewFormatException {
		String extension = media.getFileExtension();

		BufferedImage previewImg;
        MediaFile<byte[]> thumbnail;

        String previewExtension;
		if (ImageUtils.isImage(extension)) {
			previewImg = ImageUtils.createImagePreview(file, maxWidth, maxHeight);

			thumbnail = new MediaFile<byte[]>(media.getMimeType(),
					ImageUtils.imageToBytes(previewImg, extension),
                    media.getLastUpdatedDate());
            previewExtension = extension;
		} else if (VideoUtils.isVideo(extension)) {
			previewImg = new VideoUtils(file).createVideoPreview(maxWidth, maxHeight);

			thumbnail = new MediaFile<byte[]>(VideoUtils.PREVIEW_MIME_TYPE,
					ImageUtils.imageToBytes(previewImg, VideoUtils.PREVIEW_TYPE),
                    media.getLastUpdatedDate());
            previewExtension = VideoUtils.PREVIEW_TYPE;
		} else {
			throw new InvalidPreviewFormatException(extension);
		}

		// store preview in another flow
		new StorePreviewThread(previewId, mediaDirectory, media.getId(), thumbnail.getMimeType(), maxHeight,
				maxWidth, previewExtension, previewImg).start();

		return thumbnail;
	}

	public boolean isAvatar(String mediaId) {
		return mediaId.equals(Constants.AVATAR_ARG);
	}

	protected String getFormFieldContent(List<FileItem> items, String fieldName) {
		String field = null;

		for (int i = 0; i < items.size(); i++) {
			FileItem item = items.get(i);
			if (fieldName.equals(item.getFieldName().toLowerCase())) {
				field = item.getString();
				items.remove(i);

				break;
			}
		}

		return field;
	}

	protected FileItem getFileFormField(List<FileItem> items) {
		FileItem field = null;

		for (FileItem item : items) {
			if (Constants.DATA_FIELD.equals(item.getFieldName().toLowerCase())) {
				field = item;
				break;
			}
		}

		return field;
	}

	protected Media createMedia(String mediaId, String fileName, String title,
			String description, String author, String entityId,
			String mimeType, File file, boolean isAvatar) {
		Media media = new Media();
		media.setId(mediaId);
		media.setFileName(fileName);
		media.setEntityId(entityId);
		media.setAuthor(author);
		media.setDescription(description);
		media.setTitle(title);
		media.setMimeType(mimeType);

		String fileExtension = getFileExtension(fileName, mimeType);
		media.setFileExtension(fileExtension);

		try {
			if (ImageUtils.isImage(fileExtension)) {
				BufferedImage img = ImageIO.read(file);

				if (isAvatar && !ImageUtils.isSquare(img)) {
					img = ImageUtils.cropMaximumSquare(img);

					// update image file
					file = ImageUtils.storeImageIntoFile(img, img.getWidth(),
							img.getHeight(), fileExtension, file.getAbsolutePath());
				}

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
		
		// set after possible cropping
		media.setFileSize(file.length());
		media.setShaChecksum(getFileShaChecksum(file));

		return media;
	}

	protected String getFileExtension(String fileName, String mimeType) {
		if (fileName != null) {
			String[] dotSplit = fileName.split("\\.");
			if (dotSplit.length > 1) {
				return dotSplit[dotSplit.length - 1];
			}
		}

		return MimeTypeMapping.lookupExtension(mimeType);
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

	protected String getFileShaChecksum(File file) {
		try {
            return DigestUtils.sha1Hex(FileUtils.openInputStream(file));
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

    private boolean isUserAllowed(String userJID, String entityId, CapabilitiesDecorator capabilities) {
        PubSubClient pubSubClient = XMPPToolBox.getInstance().getPubSubClient();
        return pubSubClient.matchUserCapability(userJID, entityId, capabilities);
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
				File previewFile = ImageUtils.storeImageIntoFile(img, width,
						height, extension, directory + File.separator + previewId);

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
