package com.buddycloud.mediaserver.business.model;

public class Preview {
//	{
//		  "id": string,
//		  "downloadUrl": string,
//		  "md5Checksum": string,
//		  "mediaId": string,
//		  "fileSize": long,
//		  "height": int,
//		  "width": int
//	}
	
	private String id;
	private String downloadUrl;
	private String md5Checksum;
	private Long fileSize;
	private String mediaId;
	private Integer height;
	private Integer width;
	
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getDownloadUrl() {
		return downloadUrl;
	}
	
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	
	public String getMd5Checksum() {
		return md5Checksum;
	}
	
	public void setMd5Checksum(String md5Checksum) {
		this.md5Checksum = md5Checksum;
	}
	
	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
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
}
