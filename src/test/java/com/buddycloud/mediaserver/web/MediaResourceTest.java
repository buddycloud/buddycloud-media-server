package com.buddycloud.mediaserver.web;

import java.io.File;
import java.io.IOException;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;

import com.buddycloud.mediaserver.business.model.Media;

public abstract class MediaResourceTest {
	
	protected static final String URL = "http://localhost:8080/channel/channel@domain/media/";
	protected static final String TESTFILE_PATH = "resources/tests/testimage.jpg";
	
	
	@Before
	public void setUp() throws Exception {
		Component component = new Component();  
	    component.getServers().add(Protocol.HTTP, 8080);  
	    
	    Context context = component.getContext().createChildContext();
		component.getDefaultHost().attach("/channel/{channelId}", new MediaServerApplication(context));
		
	    component.start();  
	}
	
	public Media buildValidTestMedia() throws IOException {
		Media media = new Media();
		
		media.setId(generateRandomString());
		media.setDescription("a description");
		media.setFileExtension(".jpg");

		File file = new File(TESTFILE_PATH);
		media.setFileSize(file.length());
		
		String md5 = DigestUtils.md5Hex(FileUtils.openInputStream(file));
		media.setMd5Checksum(md5);
		
		media.setMimeType(new MimetypesFileTypeMap().getContentType(file));
		media.setResolution("500x100");
		media.setTitle("testimage.jpg");
		media.setUploader("user@domain.com");
		
		return media;
	}
	
	protected static String generateRandomString() {
		return RandomStringUtils.random(20, true, true);
	}
}
