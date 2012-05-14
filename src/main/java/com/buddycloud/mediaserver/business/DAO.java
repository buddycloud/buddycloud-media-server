package com.buddycloud.mediaserver.business;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.sql.SQLException;

import org.apache.commons.fileupload.FileUploadException;
import org.restlet.Request;

public interface DAO {
	
	public String addFile(String domain, String channel, Request request) 
			throws FileNotFoundException, FileUploadException, SQLException;
	
}
