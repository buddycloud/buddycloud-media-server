package com.buddycloud.mediaserver.business.jdbc;

public class Queries {
	private Queries() {}
	
	// Insert
	public static final String SAVE_MEDIA = "INSERT INTO media" +
			" (id, fileName, entityId, uploader, title, description, mimeType, fileExtension, shaChecksum, fileSize, length, height, width)" +
			" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String SAVE_AVATAR = "INSERT INTO avatar" +
			" (mediaId, entityId)" +
			" VALUES(?, ?)";
	public static final String SAVE_PREVIEW = "INSERT INTO avatar" +
			" (mediaId, md5Checksum, fileSize, height, width)" +
			" VALUES(?, ?, ?, ?, ?)";
	
	// Select
	public static final String GET_MEDIA = "SELECT * FROM media WHERE id = ?";
	public static final String GET_MEDIA_MIME_TYPE = "SELECT mimeType FROM media WHERE id = ?";
	public static final String GET_ENTITY_AVATAR_ID = "SELECT mediaId FROM avatar WHERE id = ?";
	public static final String GET_MEDIA_PREVIEWS = "SELECT id FROM previews WHERE mediaId = ? AND height <= ? AND width <= ?";

	// Delete
	public static final String DELETE_MEDIA = "DELETE FROM media WHERE id = ?";
	
	// Update
	public static final String UPDATE_MEDIA_LAST_VIEWED = "UPDATE media SET lastViewedDate = ? WHERE id = ?";
	public static final String UPDATE_AVATAR = "UPDATE avatar SET mediaId = ? WHERE id = ?";
}
