package com.buddycloud.mediaserver.business.dao;

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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.restlet.Request;
import org.restlet.ext.fileupload.RestletFileUpload;

import com.buddycloud.mediaserver.business.jdbc.MetadataSource;
import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.business.model.Preview;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.ImageUtils;
import com.buddycloud.mediaserver.commons.VideoUtils;
import com.buddycloud.mediaserver.commons.exception.FormMissingFieldException;
import com.buddycloud.mediaserver.commons.exception.InvalidPreviewFormatException;
import com.buddycloud.mediaserver.commons.exception.MetadataSourceException;
import com.buddycloud.mediaserver.commons.exception.MediaNotFoundException;
import com.google.gson.Gson;

public abstract class DAO {

	private static Logger LOGGER = Logger.getLogger(DAO.class);
	protected MetadataSource dataSource;
	protected Properties configuration;
	protected Gson gson;


	DAO(MetadataSource dataSource, Properties configuration) {
		this.configuration = configuration;
		this.dataSource = dataSource;
		this.gson = new Gson();
	}

	
	public String insertMedia(String entityId, Request request) 
			throws FileUploadException, MetadataSourceException, FormMissingFieldException {
		//TODO authentication

		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(Integer.valueOf(configuration.getProperty(Constants.MEDIA_SIZE_LIMIT_PROPERTY)));

		RestletFileUpload upload = new RestletFileUpload(factory);
		List<FileItem> items = upload.parseRequest(request);

		String fileName = getFormField(items, Constants.NAME_FIELD);
		String title = getFormField(items, Constants.TITLE_FIELD);
		String description = getFormField(items, Constants.DESC_FIELD);
		String author = getFormField(items, Constants.AUTHOR_FIELD);
		InputStream inputStream = getFileFormField(items);

		Media media = storeMedia(fileName, title, description, author, entityId, inputStream);

		LOGGER.debug("Media sucessfully added. Media ID: " + media.getId());

		return gson.toJson(media); 
	}
	
	protected abstract String getDirectory(String entityId);

	protected Media storeMedia(String fileName, String title, String description, String author,
			String entityId, InputStream inputStream) throws FileUploadException {
		
		String directory = getDirectory(entityId);
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
				author, entityId, file);
		
		// store media metadata
		new Thread() {
			public void start() {
				try {
					dataSource.storeMedia(media);
				} catch (MetadataSourceException e) {
					// do nothing
				}
			}
		}.start();
		
		return media;
	}

	protected byte[] getPreview(String entityId, String mediaId, Integer maxHeight, Integer maxWidth, String mediaDirectory)  
			throws MetadataSourceException, IOException, InvalidPreviewFormatException, MediaNotFoundException {
		File media = new File(mediaDirectory + File.separator + mediaId);

		if (!media.exists()) {
			throw new MediaNotFoundException(mediaId, entityId);
		}

		String previewId = dataSource.getPreview(mediaId, maxHeight, maxWidth);
		
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
			previewId = RandomStringUtils.random(25, true, true);
		}
		
		String extension = dataSource.getMediaExtension(mediaId);
		BufferedImage previewImg = null;
		
		//TODO else if isVideo
		if (ImageUtils.isImage(extension)) {
			previewImg = ImageUtils.createImagePreview(media, maxWidth, maxHeight);
		} else if (VideoUtils.isVideo(extension)){
			previewImg = VideoUtils.createVideoPreview(media, maxWidth, maxHeight);
		} else {
			throw new InvalidPreviewFormatException(extension);
		}
		
		// store preview in another flow
		new StorePreviewThread(previewId, mediaId, maxHeight, maxWidth, extension, previewImg).start();
		
		dataSource.updateMediaLastViewed(mediaId);
		
		return ImageUtils.imageToBytes(previewImg, extension);
	}

	protected String getFormField(List<FileItem> items, String fieldName) throws FormMissingFieldException {
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
	
	protected InputStream getFileFormField(List<FileItem> items) throws FormMissingFieldException, FileUploadException {
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
	
	protected Media createMedia(String mediaId, String fileName, String title,
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
	
	protected Preview createPreview(String previewId, String mediaId, Integer height, Integer width, File file) {
		Preview preview = new Preview();
		preview.setFileSize(file.length());
		preview.setHeight(height);
		preview.setWidth(width);
		preview.setId(previewId);
		preview.setMediaId(mediaId);
		preview.setShaChecksum(getFileShaChecksum(file));

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
	
	class StorePreviewThread extends Thread {
		private String previewId;
		private String mediaId;
		private Integer height;
		private Integer width;
		private String directory;
		private String extension;
		private BufferedImage img;
		

		StorePreviewThread(String previewId, String mediaId, Integer height, Integer width, 
				String extension, BufferedImage img) {
			this.previewId = previewId;
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
