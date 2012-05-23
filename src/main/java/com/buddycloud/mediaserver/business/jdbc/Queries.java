package com.buddycloud.mediaserver.business.jdbc;

public class Queries {
	private Queries() {}
	
	// Insert
	public static final String SAVE_MEDIA = "INSERT INTO media" +
			" (id, uploader, title, mimeType, downloadUrl, fileExtension, md5Checksum, fileSize, length, resolution)" +
			" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	// Select
	public static final String GET_MEDIA = "SELECT * FROM media WHERE id = ?";
	
	public static final String GET_MEDIA_MIME_TYPE = "SELECT mimeType FROM media WHERE id = ?";

	// Delete
	public static final String DELETE_MEDIA = "DELETE FROM media WHERE id = ?";
}
