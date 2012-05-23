package com.buddycloud.mediaserver.commons;

public class Constants {
	
	private Constants() {}
	
	
	//URL arguments
	public static final String CHANNEL_ARG = "channelId";
	public static final String MEDIA_ARG = "mediaId";
	
	//Storage constants
	public static final String FILE_FIELD = "binaryFile";
	public static final String BODY_FIELD = "body";
	
	//Properties file constants
	public static final String MEDIA_SIZE_LIMIT_PROPERTY = "media.sizelimit";
	public static final String MEDIA_STORAGE_ROOT_PROPERTY = "media.storage.root";
	
	public static final String JDBC_DRIVER_CLASS_PROPERTY = "jdbc.driver.class";
	public static final String JDBC_DB_URL_PROPERTY = "jdbc.db.url";
}
