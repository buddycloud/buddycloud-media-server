package com.buddycloud.mediaserver.business.model;

public class Preview {
//	{
//		  "id": string,
//		  "shaChecksum": string,
//		  "mediaId": string,
//		  "fileSize": long,
//		  "height": int,
//		  "width": int
//	}
	
	private String id;
	private String shaChecksum;
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

	public String getShaChecksum() {
		return shaChecksum;
	}

	public void setShaChecksum(String shaChecksum) {
		this.shaChecksum = shaChecksum;
	}
}
