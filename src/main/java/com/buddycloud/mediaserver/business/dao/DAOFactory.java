package com.buddycloud.mediaserver.business.dao;

import java.util.Properties;

import com.buddycloud.mediaserver.business.jdbc.MetadataSource;
import com.buddycloud.mediaserver.commons.ConfigurationUtils;

public class DAOFactory {
	
	private MediaDAO mediaDAO;
	
	
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
	
	
	public MediaDAO getMediaDAO() {
		if (this.mediaDAO == null) {
			mediaDAO = new MediaDAO(dataSource, configuration);
		}
		
		return mediaDAO;
	}
}
