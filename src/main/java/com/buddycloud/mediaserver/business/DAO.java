package com.buddycloud.mediaserver.business;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.apache.commons.fileupload.FileUploadException;
import org.restlet.Request;

public interface DAO {
	
	public String addFile(String channel, String mediaId, Request request) 
			throws FileNotFoundException, FileUploadException, SQLException;
	
}
