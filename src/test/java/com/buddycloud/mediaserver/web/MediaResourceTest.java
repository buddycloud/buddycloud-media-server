package com.buddycloud.mediaserver.web;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;

import com.buddycloud.mediaserver.business.jdbc.MetadataSource;
import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.ConfigurationUtils;
import com.buddycloud.mediaserver.commons.Constants;

public abstract class MediaResourceTest {
	
	protected static final String TEST_MEDIA_STORAGE_ROOT = "/tmp";
	protected static final String TESTFILE_PATH = "resources/tests/testimage.jpg";
	protected static final String TESTFILE_ID = "testFileId";

	protected static final String BASE_CHANNEL = "channel@topics.domain.com";
	protected static final String BASE_URL = "http://localhost:8080";
	
	
	protected Properties configuration;
	protected MetadataSource dataSource;
	
	@Before
	public void setUp() throws Exception {
		configuration = ConfigurationUtils.loadConfiguration();
		configuration.setProperty(Constants.MEDIA_CHANNEL_ROOT_PROPERTY, TEST_MEDIA_STORAGE_ROOT);
		
		dataSource = new MetadataSource(configuration);
		
		setupComponent();
	    testSetUp();
	}
	
	private void setupComponent() throws Exception {
		Component component = new Component();  
	    component.getServers().add(Protocol.HTTP, 8080);  
	    
	    Context context = component.getContext().createChildContext();
		component.getDefaultHost().attach(new MediaServerApplication(context));
		
	    component.start();  
	}
	
	public Media buildValidTestMedia() throws IOException {
		return buildValidTestMedia(TESTFILE_ID);
	}
	
	public Media buildValidTestMedia(String mediaId) throws IOException {
		Media media = new Media();
		
		media.setId(mediaId);
		media.setDescription("a description");
		media.setFileExtension("jpg");

		File file = new File(TESTFILE_PATH);
		media.setFileSize(file.length());
		
		String md5 = DigestUtils.md5Hex(FileUtils.openInputStream(file));
		media.setMd5Checksum(md5);
		
		media.setMimeType(new MimetypesFileTypeMap().getContentType(file));
		media.setTitle("testimage.jpg");
		media.setUploader("user@domain.com");
		media.setHeight(312);
		media.setWidth(312);
		
		return media;
	}
	
	protected static String generateRandomString() {
		return RandomStringUtils.random(20, true, true);
	}
	
	protected abstract void testSetUp() throws Exception;
}
