package com.buddycloud.mediaserver.business.dao;


public class DAOFactory {
	
	private MediaDAO mediaDAO;
	
	
	private static final DAOFactory instance = new DAOFactory();


	private DAOFactory() {}
	
	
	public static DAOFactory getInstance() {
		return instance;
	}
	
	
	public MediaDAO getDAO() {
		if (mediaDAO == null) {
			mediaDAO = new MediaDAO();
		}
		
		return mediaDAO;
	}
}
