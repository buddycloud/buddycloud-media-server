/*
 * Copyright 2012 buddycloud
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.buddycloud.mediaserver.business.jdbc;

public class Queries {
	private Queries() {
	}

	// Insert
	public static final String SAVE_MEDIA = "INSERT INTO medias"
			+ " (id, fileName, entityId, author, title, description, mimeType, fileExtension, shaChecksum, fileSize, length, height, width)"
			+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String SAVE_AVATAR = "INSERT INTO avatars"
			+ " (mediaId, entityId)" + " VALUES(?, ?)";
	public static final String SAVE_PREVIEW = "INSERT INTO previews"
			+ " (id, mediaId, shaChecksum, fileSize, height, width)"
			+ " VALUES(?, ?, ?, ?, ?, ?)";

	// Select
	public static final String GET_MEDIA = "SELECT * FROM medias WHERE id = ?";
	public static final String GET_MEDIAS_INFO = "SELECT * FROM medias WHERE entityId = ?";
	public static final String GET_MEDIAS_INFO_SINCE = "SELECT * FROM medias WHERE entityId = ? AND lastUpdatedDate >= ? ";
	public static final String GET_MEDIA_UPLOADER = "SELECT author FROM medias WHERE id = ?";
	public static final String GET_MEDIA_MIME_TYPE = "SELECT mimeType FROM medias WHERE id = ?";
	public static final String GET_MEDIA_EXTENSION = "SELECT fileExtension FROM medias WHERE id = ?";
	public static final String GET_ENTITY_AVATAR_ID = "SELECT mediaId FROM avatars WHERE entityId = ?";
	public static final String GET_MEDIA_PREVIEW = "SELECT id FROM previews WHERE mediaId = ? AND height = ? AND width = ?";
	public static final String GET_MEDIA_PREVIEWS = "SELECT id FROM previews WHERE mediaId = ?";

	// Delete
	public static final String DELETE_MEDIA = "DELETE FROM medias WHERE id = ?";
	public static final String DELETE_PREVIEW = "DELETE FROM previews WHERE id = ?";
	public static final String DELETE_ENTITY_AVATAR = "DELETE FROM avatars WHERE entityId = ?";
	public static final String DELETE_PREVIEWS_FROM_MEDIA = "DELETE FROM previews WHERE mediaId = ?";

	// Update
	public static final String UPDATE_MEDIA_LAST_UPDATED = "UPDATE medias SET lastUpdatedDate = ? WHERE id = ?";
	public static final String UPDATE_MEDIA_FIELDS = "UPDATE medias SET fileName = ?, title = ?, description = ? WHERE id = ?";
	public static final String UPDATE_AVATAR = "UPDATE avatars SET mediaId = ? WHERE entityId = ?";
}
