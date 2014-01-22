package com.buddycloud.mediaserver.commons;

import java.util.Date;

/**
 * MediaFile bean class.
 *
 * @author Rodrigo Duarte Sousa - rodrigodsousa@gmail.com
 */
public class MediaFile<T> {
	private T mediaFile;
	private String mimeType;
	private Date lastModified;

	
	public MediaFile(String mimeType, T mediaFile, Date lastModified) {
		this.mediaFile = mediaFile;
		this.mimeType = mimeType;
        this.lastModified = lastModified;
	}


	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public T getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(T mediaFile) {
        this.mediaFile = mediaFile;
    }
}
