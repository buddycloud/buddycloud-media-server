package com.buddycloud.mediaserver.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.log4j.Logger;
import org.restlet.Request;
import org.restlet.ext.fileupload.RestletFileUpload;

import com.buddycloud.mediaserver.business.db.MetadataSource;
import com.buddycloud.mediaserver.commons.ConfigurationUtils;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.JsonUtil;

public class MediaDAO implements DAO {

	private static Logger LOGGER = Logger.getLogger(MediaDAO.class);
	private MetadataSource dataSource;


	private static final MediaDAO instance = new MediaDAO();


	private MediaDAO() {
		try {
			this.dataSource = new MetadataSource(ConfigurationUtils.loadConfiguration());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}


	public static MediaDAO gestInstance() {
		return instance;
	}


	private String getFileName(List<FileItem> items) {
		String fileName = null;

		for (int i = 0; i < items.size(); i++) {
			FileItem item = items.get(i);
			if (item.getFieldName().equals(Constants.NAME_FIELD)) {
				fileName = item.getString();
				items.remove(i);
				break;
			}
		}

		return fileName;
	}

	private void insertMetadata(String uuid, String fileName, File file) throws SQLException {
		Statement statement = dataSource.createStatement();
		statement.execute("INSERT INTO media(media_id, media_path, media_name, media_type) " +
				" VALUES('" + uuid + "', '" + file.getParentFile().getAbsolutePath() + "', '" +
				fileName + "', '" + getMediaType(file) + "');");
		
		MetadataSource.close(statement);
	}

	private boolean mkdir(String fullDirectoryPath) {
		File directory = new File(fullDirectoryPath);
		return directory.mkdir();
	}

	public String addFile(String channel, String mediaId, Request request) 
			throws FileNotFoundException, FileUploadException, SQLException {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(Constants.SIZE_THRESHOLD);

		RestletFileUpload upload = new RestletFileUpload(factory);
		List<FileItem> items = upload.parseRequest(request);

		String fileName = getFileName(items);
		if (fileName == null) {

		}

		boolean found = false;

		for (FileItem item : items) {
			if (item.getFieldName().equals(Constants.FILE_FIELD)) {
				found = true;

				String fullDirectoryPath = Constants.FILE_ROOT + File.separator + channel;
				mkdir(fullDirectoryPath);

				File file = new File(fullDirectoryPath + File.separator + mediaId);

				try {
					item.write(file);
				} catch (Exception e) {
					throw new FileUploadException("Error while writing the file");
				}

				insertMetadata(mediaId, fileName, file);

				break;
			}
		}

		if (!found) {
			throw new FileNotFoundException();
		}

		return JsonUtil.toJson(mediaId); 
	}

	private String getMediaType(File file) {
		return new MimetypesFileTypeMap().getContentType(file);
	}

}
