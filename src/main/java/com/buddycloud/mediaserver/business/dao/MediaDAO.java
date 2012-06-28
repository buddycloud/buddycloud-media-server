package com.buddycloud.mediaserver.business.dao;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.List;
import java.util.Properties;

import javax.activation.MimetypesFileTypeMap;
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
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.ImageUtils;
import com.buddycloud.mediaserver.commons.VideoUtils;
import com.buddycloud.mediaserver.commons.exception.FormInvalidFieldException;
import com.buddycloud.mediaserver.commons.exception.FormMissingFieldException;
import com.buddycloud.mediaserver.commons.exception.InvalidPreviewFormatException;
import com.buddycloud.mediaserver.commons.exception.MediaNotFoundException;
import com.buddycloud.mediaserver.commons.exception.MetadataSourceException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MediaDAO {

	private static Logger LOGGER = Logger.getLogger(MediaDAO.class);
	private MetaDataSource dataSource;
	private Properties configuration;
	private Gson gson;


	MediaDAO(MetaDataSource dataSource, Properties configuration) {
		this.configuration = configuration;
		this.dataSource = dataSource;
		this.gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
	}
	
	
	public void deleteMedia(String entityId, String mediaId) 
			throws MetadataSourceException, MediaNotFoundException {

		boolean isAvatar = isAvatar(mediaId);
		if (isAvatar) {
			mediaId = dataSource.getEntityAvatarId(entityId);
		}

		//TODO authentication
/*
 * if (!isChannelPublic)
 * 	if (!isProducer && !isFollower && !isFollower+Post)
 * 		return
 */

		LOGGER.debug("Deleting media. Media ID: " + mediaId);

		String fullDirectoryPath = getDirectory(entityId);
		File media = new File(fullDirectoryPath + File.separator + mediaId);

		if (!media.exists()) {
			throw new MediaNotFoundException(mediaId, entityId);
		}
		
		if (isAvatar) {
			// delete avatars table entry
			dataSource.deleteEntityAvatar(entityId);
		}
		
		// delete existent previews from media
		deletePreviews(mediaId, fullDirectoryPath);

		// delete file and metadata
		media.delete();
		dataSource.deleteMedia(mediaId);

		//TODO delete previews
	}
	
	private void deletePreviews(String mediaId, String dirPath) throws MetadataSourceException {
		List<String> previews = dataSource.getPreviewsFromMedia(mediaId);
		
		if (!previews.isEmpty()) {
			for (String previewId : previews) {
				File preview = new File(dirPath + File.separator + previewId);
				preview.delete();
			}
			
			dataSource.deletePreviewsFromMedia(mediaId);
		}
	}

	public File getMedia(String entityId, String mediaId) 
			throws MetadataSourceException, MediaNotFoundException, IOException, InvalidPreviewFormatException {

		if (isAvatar(mediaId)) {
			return getAvatar(entityId);
		}

		//TODO authentication
/*
 * if (!isChannelPublic)
 * 	if (!isProducer && !isFollower && !isFollower+Post)
 * 		return
 */

		LOGGER.debug("Getting media. Media ID: " + mediaId);

		String fullDirectoryPath = getDirectory(entityId);
		File media = new File(fullDirectoryPath + File.separator + mediaId);

		if (!media.exists()) {
			throw new MediaNotFoundException(mediaId, entityId);
		}

		// Update last viewed date
		dataSource.updateMediaLastViewed(mediaId);

		return media;
	}

	public File getAvatar(String entityId)
			throws MetadataSourceException, MediaNotFoundException, IOException, InvalidPreviewFormatException {
		String mediaId = dataSource.getEntityAvatarId(entityId);

		LOGGER.debug("Getting avatar. Entity ID: " + entityId);

		String fullDirectoryPath = getDirectory(entityId);

		File media = new File(fullDirectoryPath + File.separator + mediaId);

		if (!media.exists()) {
			throw new MediaNotFoundException(mediaId, entityId);
		}

		// Update last viewed date
		dataSource.updateMediaLastViewed(mediaId);

		return media;
	}
	
	public byte[] getMediaPreview(String entityId, String mediaId, Integer size) 
			throws MetadataSourceException, MediaNotFoundException, IOException, InvalidPreviewFormatException {

		return getMediaPreview(entityId, mediaId, size, size);
	}
	
	public byte[] getMediaPreview(String entityId, String mediaId, Integer maxHeight, Integer maxWidth) 
			throws MetadataSourceException, MediaNotFoundException, IOException, InvalidPreviewFormatException {

		if (isAvatar(mediaId)) {
			return getAvatarPreview(entityId, maxHeight, maxWidth);
		}

		//TODO authentication

		LOGGER.debug("Getting media preview. Media ID: " + mediaId);

		return getPreview(entityId, mediaId, maxHeight, maxWidth, getDirectory(entityId));
	}

	public byte[] getAvatarPreview(String entityId, Integer maxHeight, Integer maxWidth)
			throws MetadataSourceException, MediaNotFoundException, IOException, InvalidPreviewFormatException {
		String mediaId = dataSource.getEntityAvatarId(entityId);

		LOGGER.debug("Getting avatar preview. Avatar ID: " + entityId);

		return getPreview(entityId, mediaId, maxHeight, maxWidth, getDirectory(entityId));
	}

	public String getMediaType(String entityId, String mediaId) throws MetadataSourceException {
		if (isAvatar(mediaId)) {
			mediaId = dataSource.getEntityAvatarId(entityId);
		}
		
		return dataSource.getMediaMimeType(mediaId);
	}
	
	public String updateMedia(String entityId, String mediaId, Request request) 
			throws FileUploadException, MetadataSourceException, FormInvalidFieldException, MediaNotFoundException {
		
		if (isAvatar(mediaId)) {
			mediaId = dataSource.getEntityAvatarId(entityId);
		}
		
		//TODO authentication
		/*
		 * if (!isChannelPublic)
		 * 	if (!isProducer && !isFollower && !isFollower+Post)
		 * 		return
		 */
		
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(Integer.valueOf(configuration.getProperty(Constants.MEDIA_SIZE_LIMIT_PROPERTY)));

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
		LOGGER.debug("Media sucessfully updated. Media ID: " + media.getId());

		return gson.toJson(media); 
	}
	
	
	public String insertMedia(String entityId, Request request, boolean isAvatar) 
			throws FileUploadException, MetadataSourceException, FormMissingFieldException {
		//TODO authentication
		/*
		 * if (!isChannelPublic)
		 * 	if (!isProducer && !isFollower && !isFollower+Post)
		 * 		return
		 */
		
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(Integer.valueOf(configuration.getProperty(Constants.MEDIA_SIZE_LIMIT_PROPERTY)));

		RestletFileUpload upload = new RestletFileUpload(factory);
		List<FileItem> items = upload.parseRequest(request);

		String fileName = getFormField(items, Constants.NAME_FIELD);
		String title = getFormField(items, Constants.TITLE_FIELD);
		String description = getFormField(items, Constants.DESC_FIELD);
		String author = getFormField(items, Constants.AUTHOR_FIELD);
		InputStream inputStream = getFileFormField(items);

		Media media = storeMedia(fileName, title, description, author, entityId, inputStream, isAvatar);

		LOGGER.debug("Media sucessfully added. Media ID: " + media.getId());

		return gson.toJson(media); 
	}
	
	private Media storeMedia(String fileName, String title, String description, String author,
			String entityId, InputStream inputStream, final boolean isAvatar) throws FileUploadException {

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
				author, entityId, file);
		
		// store media metadata
		new Thread() {
			public void start() {
				try {
					if (isAvatar) {
						if (dataSource.getEntityAvatarId(media.getEntityId()) != null) {
							dataSource.updateEntityAvatar(media.getEntityId(), media.getId());
						} else {
							dataSource.storeAvatar(media);
						}
					}
					dataSource.storeMedia(media);
				} catch (MetadataSourceException e) {
					// do nothing
				}
			}
		}.start();

		return media;
	}
	
	private byte[] getPreview(String entityId, String mediaId, Integer maxHeight, Integer maxWidth, String mediaDirectory)  
			throws MetadataSourceException, IOException, InvalidPreviewFormatException, MediaNotFoundException {
		File media = new File(mediaDirectory + File.separator + mediaId);

		if (!media.exists()) {
			throw new MediaNotFoundException(mediaId, entityId);
		}

		String previewId = dataSource.getPreviewId(mediaId, maxHeight, maxWidth);

		if (previewId != null) {
			File preview = new File(mediaDirectory + File.separator + previewId);

			if (!preview.exists()) {
				dataSource.deletePreview(previewId);
			} else {
				// Update last viewed date
				dataSource.updateMediaLastViewed(mediaId);

				return IOUtils.toByteArray(FileUtils.openInputStream(preview));
			}
		} else {
			// generate random id
			previewId = RandomStringUtils.randomAlphanumeric(20);
		}

		String extension = dataSource.getMediaExtension(mediaId);
		BufferedImage previewImg = null;

		if (ImageUtils.isImage(extension)) {
			previewImg = ImageUtils.createImagePreview(media, maxWidth, maxHeight);
		} else if (VideoUtils.isVideo(extension)){
			previewImg = VideoUtils.createVideoPreview(media, maxWidth, maxHeight);
		} else {
			throw new InvalidPreviewFormatException(extension);
		}

		// store preview in another flow
		new StorePreviewThread(previewId, mediaDirectory, mediaId, maxHeight, maxWidth, extension, previewImg).start();

		dataSource.updateMediaLastViewed(mediaId);

		return ImageUtils.imageToBytes(previewImg, extension);
	}
	
	private boolean isAvatar(String mediaId) {
		return mediaId.equals(Constants.AVATAR_ARG);
	}

	private String getFormField(List<FileItem> items, String fieldName) throws FormMissingFieldException {
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

	private InputStream getFileFormField(List<FileItem> items) throws FormMissingFieldException, FileUploadException {
		InputStream field = null;

		for (int i = 0; i < items.size(); i++) {
			FileItem item = items.get(i);
			if (Constants.FILE_FIELD.equals(item.getFieldName().toLowerCase())) {
				try {
					field = item.getInputStream();
				} catch (IOException e) {
					throw new FileUploadException("Error to get file input stream");
				}

				break;
			}
		}

		if (field == null) {
			throw new FormMissingFieldException(Constants.FILE_FIELD);
		}

		return field;
	}

	private Media createMedia(String mediaId, String fileName, String title,
			String description, String author, String entityId, File file) {
		Media media = new Media();
		media.setId(mediaId);
		media.setFileName(fileName);
		media.setEntityId(entityId);
		media.setAuthor(author);
		media.setDescription(description);
		media.setTitle(title);
		media.setFileSize(file.length());
		media.setShaChecksum(getFileShaChecksum(file));
		media.setMimeType(new MimetypesFileTypeMap().getContentType(file));

		String fileExtension = getFileExtension(fileName);
		media.setFileExtension(fileExtension);

		//XXX adds a lot of delay to the server side, maybe the client should send those information
		if (ImageUtils.isImage(fileExtension)) {
			try {
				BufferedImage img = ImageIO.read(file);
				media.setHeight(img.getHeight());
				media.setWidth(img.getWidth());
			} catch (IOException e) {
				LOGGER.error("Error while getting image height and size", e);
			}
		} else if (VideoUtils.isVideo(fileExtension)) {
			media.setLength(VideoUtils.getVideoLength(file));
		} //TODO else if AUDIO media.setLength()

		return media;
	}

	private Preview createPreview(String previewId, String mediaId, Integer height, Integer width, File file) {
		Preview preview = new Preview();
		preview.setFileSize(file.length());
		preview.setHeight(height);
		preview.setWidth(width);
		preview.setId(previewId);
		preview.setMediaId(mediaId);
		preview.setShaChecksum(getFileShaChecksum(file));

		return preview;
	}

	private String getFileExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	private String getFileShaChecksum(File file) {
		try {
			return DigestUtils.shaHex(FileUtils.openInputStream(file));
		} catch (IOException e) {
			LOGGER.error("Error during media SHA1 checksum generation.", e);
		}

		return null;
	}

	private boolean mkdir(String fullDirectoryPath) {
		File directory = new File(fullDirectoryPath);
		return directory.mkdir();
	}

	public String getDirectory(String entityId) {
		return configuration.getProperty(Constants.MEDIA_STORAGE_ROOT_PROPERTY) +
				File.separator + entityId;
	}

	private class StorePreviewThread extends Thread {
		private String previewId;
		private String mediaId;
		private Integer height;
		private Integer width;
		private String directory;
		private String extension;
		private BufferedImage img;


		StorePreviewThread(String previewId, String directory, String mediaId, Integer height, Integer width, 
				String extension, BufferedImage img) {
			this.previewId = previewId;
			this.directory = directory;
			this.mediaId = mediaId;
			this.height = height;
			this.width = width;
			this.extension = extension;
			this.img = img;
		}

		
		public void start() {
			try {
				File previewFile = ImageUtils.storeImageIntoFile(img, extension, 
						directory + File.separator + previewId);

				Preview preview = createPreview(previewId, mediaId, height, width, previewFile);

				dataSource.storePreview(preview);
			} catch (Exception e) {
				LOGGER.error("Error while storing preview: " + previewId + ". Media ID: " + mediaId, e);
			}
		}
	}
}
