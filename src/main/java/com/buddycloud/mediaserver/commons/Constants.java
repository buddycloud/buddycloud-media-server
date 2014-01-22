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
package com.buddycloud.mediaserver.commons;

public class Constants {

	private Constants() {
	}

	// URL arguments
	public static final String ENTITY_ARG = "entityId";
	public static final String MEDIA_ARG = "mediaId";
	public static final String AVATAR_ARG = "avatar";

	// URL queries
	public static final String MAX_HEIGHT_QUERY = "maxheight";
	public static final String MAX_WIDTH_QUERY = "maxwidth";
	public static final String SINCE_QUERY = "since";
	public static final String AFTER_QUERY = "after";
	public static final String AUTH_QUERY = "auth";
	public static final String MAX_QUERY = "max";

	// URLs
	public static final String MEDIAS_URL = "/{" + ENTITY_ARG + "}";
	public static final String MEDIA_ID_URL = "/{" + ENTITY_ARG + "}/{"
			+ MEDIA_ARG + "}";
    public static final String MEDIA_ID_METADATA_URL = "/{" + ENTITY_ARG + "}/{"
            + MEDIA_ARG + "}/metadata";

	// Storage constants
	public static final String DATA_FIELD = "data";
	public static final String TYPE_FIELD = "content-type";
    public static final String SIZE_FIELD = "content-length";
	public static final String TITLE_FIELD = "title"; // optional
	public static final String NAME_FIELD = "filename";
	public static final String DESC_FIELD = "description"; // optional
}
