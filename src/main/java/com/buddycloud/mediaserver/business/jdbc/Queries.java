package com.buddycloud.mediaserver.business.jdbc;

public class Queries {
	private Queries() {}
	
	public static final String SAVE_MEDIA = "INSERT INTO media" +
			" (id, uploader, title, mimeType, downloadUrl, fileExtension, md5Checksum, fileSize, length, resolution)" +
			" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
}
