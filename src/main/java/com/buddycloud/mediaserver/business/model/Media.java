package com.buddycloud.mediaserver.business.model;

import java.util.Date;

public class Media {
//	{
//		  "id": string,
//		  "fileName": string;
//        "entityId": string,
//		  "uploader": string,
//		  "title": string,
//		  "description": string,
//		  "mimeType": string,
//		  "uploadedDate": datetime,
//		  "lastViewedDate": datetime,
//		  "fileExtension": string,
//		  "md5Checksum": string,
//		  "fileSize": long,
//		  "length": long,
//	  	  "height": int,
//	      "width": int
//	}
	
	private String id;
	private String fileName;
	private String uploader;
	private String title;
	private String mimeType;
	private String description;
	private Date uploadedDate = null; 
	private Date lastViewedDate = null; 
	private String fileExtension;
	private String md5Checksum;
	private Long fileSize;
	private Long length = null; //for videos
	private Integer height = null; //for videos and images
	private Integer width = null; //for videos and images
	private String entityId;
	
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getUploader() {
		return uploader;
	}
	
	public void setUploader(String uploader) {
		this.uploader = uploader;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getMimeType() {
		return mimeType;
	}
	
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Date getUploadedDate() {
		return uploadedDate;
	}
	
	public void setUploadedDate(Date uploadedDate) {
		this.uploadedDate = uploadedDate;
	}
	
	public Date getLastViewedDate() {
		return lastViewedDate;
	}
	
	public void setLastViewedDate(Date lastViewedDate) {
		this.lastViewedDate = lastViewedDate;
	}
	
	public String getFileExtension() {
		return fileExtension;
	}
	
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}
	
	public String getMd5Checksum() {
		return md5Checksum;
	}
	
	public void setMd5Checksum(String md5Checksum) {
		this.md5Checksum = md5Checksum;
	}
	
	public Long getFileSize() {
		return fileSize;
	}
	
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	
	public Long getLength() {
		return length;
	}
	
	public void setLength(Long length) {
		this.length = length;
	}
	
	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
