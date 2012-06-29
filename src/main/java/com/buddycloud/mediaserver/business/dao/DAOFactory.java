package com.buddycloud.mediaserver.business.dao;

import com.buddycloud.mediaserver.business.jdbc.MetaDataSource;

public class DAOFactory {
	
	private MediaDAO mediaDAO;
	
	
	private MetaDataSource dataSource;


	private static final DAOFactory instance = new DAOFactory();


	private DAOFactory() {
		this.dataSource = new MetaDataSource();
	}
	
	
	public static DAOFactory getInstance() {
		return instance;
	}
	
	
	public MediaDAO getMediaDAO() {
		if (this.mediaDAO == null) {
			mediaDAO = new MediaDAO(dataSource);
		}
		
		return mediaDAO;
	}
}
