package com.buddycloud.mediaserver.business.model;

import java.util.Date;

public class Media {
//	{
//		  "id": string,
//		  "uploader": string,
//		  "title": string,
//		  "mimeType": string,
//		  "description": string,
//		  "uploadedDate": datetime,
//		  "lastViewedDate": datetime,
//		  "downloadUrl": string,
//		  "fileExtension": string,
//		  "md5Checksum": string,
//		  "fileSize": long,
//		  "length": long,
//		  "resolution": string
//	}
	
	private String id;
	private String uploader;
	private String title;
	private String mimeType;
	private String description;
	private Date uploadedDate = null; 
	private Date lastViewedDate = null; 
	private String downloadUrl = null; 
	private String fileExtension;
	private String md5Checksum;
	private Long fileSize;
	private Long length = null; //for videos
	private String resolution = null; //for photos and videos
	
	
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
	
	public String getDownloadUrl() {
		return downloadUrl;
	}
	
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
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
	
	public String getResolution() {
		return resolution;
	}
	
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
}
