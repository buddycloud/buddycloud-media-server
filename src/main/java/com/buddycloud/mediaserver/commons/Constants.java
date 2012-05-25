package com.buddycloud.mediaserver.commons;

public class Constants {
	
	private Constants() {}

	//Avatar id
	public static final String AVATAR_ID = "1"; 
	
	//URL arguments
	public static final String CHANNEL_ARG = "channelId";
	public static final String MEDIA_ARG = "mediaId";
	public static final String USER_ARG = "userId";

	//URLs
	public static final String CHANNEL_URL_PREFIX = "/channel/{" + CHANNEL_ARG + "}";
	public static final String USER_URL_PREFIX = "/user/{" + USER_ARG + "}";
	
	public static final String GET_MEDIA_URL = CHANNEL_URL_PREFIX + "/media/{" + MEDIA_ARG;
	public static final String POST_MEDIA_URL = CHANNEL_URL_PREFIX + "/media";
	
	public static final String GET_CHANNEL_AVATAR_URL = CHANNEL_URL_PREFIX + "/media/avatar/" + AVATAR_ID;
	public static final String PUT_CHANNEL_AVATAR_URL = CHANNEL_URL_PREFIX + "/media/avatar/" + AVATAR_ID;
	
	public static final String GET_USER_AVATAR_URL = USER_URL_PREFIX + "/media/avatar/" + AVATAR_ID;
	public static final String PUT_USER_AVATAR_URL = USER_URL_PREFIX + "/media/avatar/" + AVATAR_ID;
	
	//Storage constants
	public static final String FILE_FIELD = "binaryFile";
	public static final String BODY_FIELD = "body";
	
	//Properties file constants
	public static final String MEDIA_SIZE_LIMIT_PROPERTY = "media.sizelimit";
	public static final String MEDIA_CHANNEL_ROOT_PROPERTY = "media.channel.root";
	public static final String MEDIA_USER_ROOT_PROPERTY = "media.user.root";
	
	public static final String JDBC_DRIVER_CLASS_PROPERTY = "jdbc.driver.class";
	public static final String JDBC_DB_URL_PROPERTY = "jdbc.db.url";
}
