package com.buddycloud.mediaserver.business.dao;

import java.util.Properties;

import com.buddycloud.mediaserver.business.jdbc.MetadataSource;
import com.buddycloud.mediaserver.commons.ConfigurationUtils;

public class DAOFactory {
	
	private MediasDAO mediaDAO;
	private AvatarsDAO avatarDAO;
	
	
	private MetadataSource dataSource;
	private Properties configuration;


	private static final DAOFactory instance = new DAOFactory();


	private DAOFactory() {
		this.configuration = ConfigurationUtils.loadConfiguration();
		this.dataSource = new MetadataSource(configuration);
	}
	
	
	public static DAOFactory getInstance() {
		return instance;
	}
	
	
	public MediasDAO getMediaDAO() {
		if (this.mediaDAO == null) {
			mediaDAO = new MediasDAO(dataSource, configuration);
		}
		
		return mediaDAO;
	}
	
	public AvatarsDAO getAvatarDAO() {
		if (this.avatarDAO == null) {
			avatarDAO = new AvatarsDAO(dataSource, configuration);
		}
		
		return avatarDAO;
	}
	
}
