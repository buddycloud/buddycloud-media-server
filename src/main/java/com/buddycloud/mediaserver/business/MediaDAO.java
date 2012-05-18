package com.buddycloud.mediaserver.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.log4j.Logger;
import org.restlet.Request;
import org.restlet.ext.fileupload.RestletFileUpload;

import com.buddycloud.mediaserver.business.jdbc.MetadataSource;
import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.ConfigurationUtils;
import com.buddycloud.mediaserver.commons.Constants;
import com.google.gson.Gson;

public class MediaDAO implements DAO {

	private static Logger LOGGER = Logger.getLogger(MediaDAO.class);
	private MetadataSource dataSource;
	private Properties configuration;
	private Gson gson;

	
	private static final MediaDAO instance = new MediaDAO();


	private MediaDAO() {
		try {
			this.configuration = ConfigurationUtils.loadConfiguration();
			this.dataSource = new MetadataSource(configuration);
			this.gson = new Gson();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}


	public static MediaDAO gestInstance() {
		return instance;
	}


	private Media getMedia(List<FileItem> items) {
		Media media = null;

		for (int i = 0; i < items.size(); i++) {
			FileItem item = items.get(i);
			if (item.getFieldName().equals(Constants.BODY_FIELD)) {
				media = gson.fromJson(item.getString(), Media.class);
				items.remove(i);
				
				break;
			}
		}

		return media;
	}

	private boolean mkdir(String fullDirectoryPath) {
		File directory = new File(fullDirectoryPath);
		return directory.mkdir();
	}
	
	private void storeMetadata(Media media) throws SQLException {
		//TODO
		//media.setDownloadUrl();
		
		dataSource.storeMetadata(media);
	}

	public String addFile(String channel, String mediaId, Request request) 
			throws FileNotFoundException, FileUploadException, SQLException {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(Constants.SIZE_THRESHOLD);

		RestletFileUpload upload = new RestletFileUpload(factory);
		List<FileItem> items = upload.parseRequest(request);

		Media media = getMedia(items);
		
		if (media == null) {
			//TODO throw Exception();
		}
		
		boolean found = false;

		for (FileItem item : items) {
			if (item.getFieldName().equals(Constants.FILE_FIELD)) {
				found = true;

				String fullDirectoryPath = configuration.getProperty("media.storage.root") +
						File.separator + channel;
				mkdir(fullDirectoryPath);

				File file = new File(fullDirectoryPath + File.separator + mediaId);

				try {
					item.write(file);
				} catch (Exception e) {
					throw new FileUploadException("Error while writing the file");
				}
				
				//verify md5

				// Only stores if the file were successfully saved				
				storeMetadata(media);

				break;
			}
		}

		if (!found) {
			throw new FileNotFoundException();
		}
		
		return gson.toJson(media); 
	}
}
