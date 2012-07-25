package com.buddycloud.mediaserver.commons;

public class Constants {
	
	private Constants() {}

	//URL arguments
	public static final String ENTITY_ARG = "entityId";
	public static final String MEDIA_ARG = "mediaId";
	public static final String AVATAR_ARG = "avatar";
	
	//URL queries
	public static final String MAX_HEIGHT_QUERY = "maxheight";
	public static final String MAX_WIDTH_QUERY = "maxwidth";
	public static final String SINCE_QUERY = "since";
	public static final String AUTH_QUERY = "auth";

	//URLs
	public static final String MEDIAS_URL = "/media/{" + ENTITY_ARG + "}";
	public static final String MEDIA_ID_URL = "/media/{" + ENTITY_ARG + "}/{" + MEDIA_ARG + "}";
	
	//Storage constants
	public static final String AUTHOR_FIELD = "author";
	public static final String TITLE_FIELD = "title"; //optional
	public static final String FILE_FIELD = "binaryfile";
	public static final String NAME_FIELD = "filename";
	public static final String DESC_FIELD = "description"; //optional
}
