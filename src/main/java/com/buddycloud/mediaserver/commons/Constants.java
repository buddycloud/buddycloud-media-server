package com.buddycloud.mediaserver.commons;

public class Constants {
	
	private Constants() {}

	//URL arguments
	public static final String ENTITY_ARG = "entityId";
	public static final String MEDIA_ARG = "mediaId";
	
	//URL queries
	public static final String MAX_HEIGHT_QUERY = "maxheight";
	public static final String MAX_WIDTH_QUERY = "maxwidth";

	//URLs
	public static final String POST_MEDIA_URL = "/media/{" + ENTITY_ARG + "}";
	public static final String GET_MEDIA_URL = "/media/{" + ENTITY_ARG + "}/{" + MEDIA_ARG + "}";
	
	public static final String AVATAR_URL = "/media/{" + ENTITY_ARG + "}/avatar";
	
	//Storage constants
	public static final String UPLOADER_FIELD = "uploader";
	public static final String TITLE_FIELD = "title"; //optional
	public static final String FILE_FIELD = "binaryfile";
	public static final String NAME_FIELD = "filename";
	public static final String DESC_FIELD = "description"; //optional
	
	//Properties file constants
	public static final String MEDIA_STORAGE_ROOT_PROPERTY = "media.channel.root";
	public static final String MEDIA_SIZE_LIMIT_PROPERTY = "media.sizelimit";
	
	public static final String JDBC_DRIVER_CLASS_PROPERTY = "jdbc.driver.class";
	public static final String JDBC_DB_URL_PROPERTY = "jdbc.db.url";
}
