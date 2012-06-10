package com.buddycloud.mediaserver.business.dao;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.buddycloud.mediaserver.business.jdbc.MetadataSource;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.exception.InvalidPreviewFormatException;
import com.buddycloud.mediaserver.commons.exception.MetadataSourceException;
import com.buddycloud.mediaserver.commons.exception.MediaNotFoundException;

public class MediaDAO extends AbstractDAO {

	private static Logger LOGGER = Logger.getLogger(MediaDAO.class);


	MediaDAO(MetadataSource dataSource, Properties configuration) {
		super(dataSource, configuration);
	}

	
	public File getMedia(String entityId, String mediaId) 
			throws MetadataSourceException, MediaNotFoundException, IOException, InvalidPreviewFormatException {
		//TODO authentication
		
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

	public byte[] getMediaPreview(String entityId, String mediaId, Integer maxHeight, Integer maxWidth) 
			throws MetadataSourceException, MediaNotFoundException, IOException, InvalidPreviewFormatException {
		//TODO authentication
		
		LOGGER.debug("Getting media preview. Media ID: " + mediaId);
		
		return getPreview(entityId, mediaId, maxHeight, maxWidth, getDirectory(entityId));
	}

	public String getMediaType(String mediaId) throws MetadataSourceException {
		return dataSource.getMediaMimeType(mediaId);
	}
}
