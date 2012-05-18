package com.buddycloud.mediaserver.business.model;

public class Preview {
//	{
//		  "id": string,
//		  "downloadUrl": string,
//		  "md5Checksum": string,
//		  "mediaId": string,
//		  "length": long,
//		  "resolution": string
//	}
	
	private String id;
	private String downloadUrl;
	private String md5Checksum;
	private Long length;
	private String resolution;
	private String mediaId;
	
	
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
	
	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
}
