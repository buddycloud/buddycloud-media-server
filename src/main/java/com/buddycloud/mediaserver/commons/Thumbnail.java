package com.buddycloud.mediaserver.commons;

public class Thumbnail {
	private byte[] img;
	private String mimeType;
	
	
	public Thumbnail(String mimeType, byte[] img) {
		this.img = img;
		this.mimeType = mimeType;
	}

	
	public byte[] getImg() {
		return img;
	}

	public void setImg(byte[] img) {
		this.img = img;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
}
