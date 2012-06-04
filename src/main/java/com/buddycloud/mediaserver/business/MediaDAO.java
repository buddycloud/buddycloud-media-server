package com.buddycloud.mediaserver.business;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.restlet.Request;
import org.restlet.ext.fileupload.RestletFileUpload;

import com.buddycloud.mediaserver.business.jdbc.MetadataSource;
import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.ConfigurationUtils;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.ImagesUtils;
import com.buddycloud.mediaserver.commons.exception.FormMissingFieldException;
import com.buddycloud.mediaserver.commons.exception.MediaMetadataSourceException;
import com.buddycloud.mediaserver.commons.exception.MediaNotFoundException;
import com.google.gson.Gson;

public class MediaDAO {

	private static Logger LOGGER = Logger.getLogger(MediaDAO.class);
	private MetadataSource dataSource;
	private Properties configuration;
	private Gson gson;


	private static final MediaDAO instance = new MediaDAO();


	private MediaDAO() {
		this.gson = new Gson();
		this.configuration = ConfigurationUtils.loadConfiguration();
		this.dataSource = new MetadataSource(configuration);
	}


	public static MediaDAO gestInstance() {
		return instance;
	}


	public File getAvatar(String entityId, String maxHeight, String maxWidth) throws MediaNotFoundException, MediaMetadataSourceException {
		String mediaId = dataSource.getEntityAvatarId(entityId);

		String fullDirectoryPath = null;

		if (maxHeight != null && maxWidth != null) {
			//TODO return a preview
		} else {
			fullDirectoryPath = getAvatarDirectory(entityId);
		}

		File media = new File(fullDirectoryPath + File.separator + mediaId);

		if (!media.exists()) {
			dataSource.deleteMedia(mediaId);
			throw new MediaNotFoundException(mediaId, entityId);
		}
		
		// Update last viewed date
		dataSource.updateMediaLastViewed(mediaId);
		
		return media;
	}


	public File getMedia(String entityId, String mediaId, String maxHeight, String maxWidth) throws MediaMetadataSourceException, MediaNotFoundException {
		String fullDirectoryPath = null;

		if (maxHeight != null && maxWidth != null) {
			//TODO return a preview
		} else {
			fullDirectoryPath = getMediaDirectory(entityId);
		}

		File media = new File(fullDirectoryPath + File.separator + mediaId);

		if (!media.exists()) {
			throw new MediaNotFoundException(mediaId, entityId);
		}

		// Update last viewed date
		dataSource.updateMediaLastViewed(mediaId);

		return media;
	}

	public String getMediaType(String mediaId) throws MediaMetadataSourceException {
		return dataSource.getMediaMimeType(mediaId);
	}
	

	public String insertMedia(String entityId, Request request, boolean isAvatar) throws FileUploadException, 
		MediaMetadataSourceException, FormMissingFieldException {

		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(Integer.valueOf(configuration.getProperty(Constants.MEDIA_SIZE_LIMIT_PROPERTY)));

		RestletFileUpload upload = new RestletFileUpload(factory);
		List<FileItem> items = upload.parseRequest(request);

		String fileName = getFormField(items, Constants.NAME_FIELD);
		String title = getFormField(items, Constants.TITLE_FIELD);
		String description = getFormField(items, Constants.DESC_FIELD);
		String uploader = getFormField(items, Constants.UPLOADER_FIELD);
		InputStream inputStream = getFileFormField(items);

		Media media = storeMedia(fileName, title, description, uploader, entityId, inputStream, isAvatar);

		LOGGER.debug("Media sucessfully added. Media ID: " + media.getId());

		return gson.toJson(media); 
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
	

	private Media storeMedia(String fileName, String title, String description, String uploader,
			String entityId, InputStream inputStream, boolean isAvatar) throws FileUploadException {
		
		String directory = isAvatar ? getAvatarDirectory(entityId) : getMediaDirectory(entityId);
		
		mkdir(directory);

		// TODO assert id uniqueness
		String mediaId = RandomStringUtils.random(25, true, true);
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
				uploader, entityId, file);
		
		// store media metadata
		new Thread() {
			public void start() {
				try {
					dataSource.storeMedia(media);
				} catch (MediaMetadataSourceException e) {
					// do nothing
				}
			}
		}.start();
		
		return media;
	}


	private Media createMedia(String mediaId, String fileName, String title,
			String description, String uploader, String entityId, File file) {
		Media media = new Media();
		media.setId(mediaId);
		media.setFileName(fileName);
		media.setEntityId(entityId);
		media.setUploader(uploader);
		media.setDescription(description);
		media.setTitle(title);
		media.setFileSize(file.length());
		media.setMd5Checksum(getFileMD5Checksum(file));
		media.setMimeType(new MimetypesFileTypeMap().getContentType(file));
		
		String fileExtension = getFileExtension(fileName);
		media.setFileExtension(fileExtension);
		//TODO if is a video, set length media.setLength(null);
		
		//XXX adds a lot of delay to the server side, maybe the client should send those information
		if (ImagesUtils.isImage(fileExtension)) {
			try {
				BufferedImage img = ImageIO.read(file);
				media.setHeight(img.getHeight());
				media.setWidth(img.getWidth());
			} catch (IOException e) {
				LOGGER.error("Error while getting image height and size", e);
			}
		}
		
		return media;
	}
	
	private String getFileExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf("."));
	}
	
	private String getFileMD5Checksum(File file) {
		try {
			return DigestUtils.md5Hex(FileUtils.openInputStream(file));
		} catch (IOException e) {
			LOGGER.error("Error during media MD5 checksum generation.", e);
		}

		return null;
	}


	private String getMediaDirectory(String entityId) {
		return configuration.getProperty(Constants.MEDIA_STORAGE_ROOT_PROPERTY) +
				File.separator + entityId;
	}
	
	private String getAvatarDirectory(String entityId) {
		return configuration.getProperty(Constants.MEDIA_STORAGE_ROOT_PROPERTY) +
				File.separator + "avatars" + File.separator + entityId;
	}
	
	private boolean mkdir(String fullDirectoryPath) {
		File directory = new File(fullDirectoryPath);
		return directory.mkdir();
	}
}
