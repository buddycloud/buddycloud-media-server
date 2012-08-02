/*
 * Copyright 2012 buddycloud
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.buddycloud.mediaserver.business.model;

import java.util.Date;

public class Media {
//	{
//		  "id": string,
//		  "fileName": string;
//        "entityId": string,
//		  "author": string,
//		  "title": string,
//		  "description": string,
//		  "mimeType": string,
//		  "uploadedDate": datetime,
//		  "lastUpdatedDate": datetime,
//		  "fileExtension": string,
//		  "shaChecksum": string,
//		  "fileSize": long,
//		  "length": long,
//	  	  "height": int,
//	      "width": int
//	}
	
	private String id;
	private String fileName;
	private String author;
	private String title;
	private String mimeType;
	private String description;
	private Date uploadedDate = null; 
	private Date lastUpdatedDate = null; 
	private String fileExtension;
	private String shaChecksum;
	private Long fileSize;
	private Long length = null; //for audio and videos
	private Integer height = null; //for videos and images
	private Integer width = null; //for videos and images
	private String entityId;
	
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
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
	
	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}
	
	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}
	
	public String getFileExtension() {
		return fileExtension;
	}
	
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
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

	public String getShaChecksum() {
		return shaChecksum;
	}

	public void setShaChecksum(String shaChecksum) {
		this.shaChecksum = shaChecksum;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}
