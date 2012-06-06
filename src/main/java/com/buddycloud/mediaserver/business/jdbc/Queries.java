package com.buddycloud.mediaserver.business.jdbc;

public class Queries {
	private Queries() {}
	
	// Insert
	public static final String SAVE_MEDIA = "INSERT INTO medias" +
			" (id, fileName, entityId, author, title, description, mimeType, fileExtension, shaChecksum, fileSize, length, height, width)" +
			" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String SAVE_AVATAR = "INSERT INTO avatars" +
			" (mediaId, entityId)" +
			" VALUES(?, ?)";
	public static final String SAVE_PREVIEW = "INSERT INTO avatars" +
			" (id, mediaId, shaChecksum, fileSize, height, width)" +
			" VALUES(?, ?, ?, ?, ?, ?)";
	
	// Select
	public static final String GET_MEDIA = "SELECT * FROM medias WHERE id = ?";
	public static final String GET_MEDIA_MIME_TYPE = "SELECT mimeType FROM medias WHERE id = ?";
	public static final String GET_MEDIA_EXTENSION = "SELECT fileExtension FROM medias WHERE id = ?";
	public static final String GET_ENTITY_AVATAR_ID = "SELECT mediaId FROM avatars WHERE id = ?";
	public static final String GET_MEDIA_PREVIEWS = "SELECT id FROM previews WHERE mediaId = ? AND height = ? AND width = ?";

	// Delete
	public static final String DELETE_MEDIA = "DELETE FROM medias WHERE id = ?";
	
	// Update
	public static final String UPDATE_MEDIA_LAST_VIEWED = "UPDATE medias SET lastViewedDate = ? WHERE id = ?";
	public static final String UPDATE_AVATAR = "UPDATE avatars SET mediaId = ? WHERE id = ?";
}
