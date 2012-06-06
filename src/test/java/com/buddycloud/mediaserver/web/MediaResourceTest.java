package com.buddycloud.mediaserver.web;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;

import com.buddycloud.mediaserver.business.jdbc.MetadataSource;
import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.ConfigurationUtils;
import com.buddycloud.mediaserver.commons.Constants;
import com.google.gson.Gson;

public abstract class MediaResourceTest {
	
	protected static final String TEST_MEDIA_STORAGE_ROOT = "/tmp";
	protected static final String TESTFILE_PATH = "resources/tests/testimage.jpg";
	protected static final String TESTFILE_NAME = "testimage.jpg";

	protected static final String BASE_CHANNEL = "channel@topics.domain.com";
	protected static final String BASE_USER = "user@domain.com";
	protected static final String BASE_URL = "http://localhost:8080";
	

	protected Component component;
	protected Properties configuration;
	protected MetadataSource dataSource;
	protected Gson gson;
	
	
	@Before
	public void setUp() throws Exception {
		configuration = ConfigurationUtils.loadConfiguration();
		configuration.setProperty(Constants.MEDIA_STORAGE_ROOT_PROPERTY, TEST_MEDIA_STORAGE_ROOT);
		
		dataSource = new MetadataSource(configuration);
		gson = new Gson();
		
		setupComponent();
	    testSetUp();
	}
	
	@After
	public void tearDown() throws Exception {
		component.stop();
		
		testTearDown();
	}
	
	private void setupComponent() throws Exception {
		component = new Component();  
	    component.getServers().add(Protocol.HTTP, 8080);  
	    
	    Context context = component.getContext().createChildContext();
		component.getDefaultHost().attach(new MediaServerApplication(context));
		
	    component.start();  
	}
	
	protected Media buildMedia(String mediaId) throws Exception {
		File file = new File(TESTFILE_PATH);
		
		Media media = new Media();
		media.setId(mediaId);
		media.setFileName(TESTFILE_NAME);
		media.setEntityId(BASE_CHANNEL);
		media.setAuthor(BASE_USER);
		media.setDescription("A description");
		media.setTitle("A title");
		media.setFileSize(file.length());
		media.setShaChecksum(getFileShaChecksum(file));
		media.setFileExtension("jpg");
		media.setMimeType(new MimetypesFileTypeMap().getContentType(file));
		
		BufferedImage img = ImageIO.read(file);
		media.setHeight(img.getHeight());
		media.setWidth(img.getWidth());
		
		return media;
	}
	
	private String getFileShaChecksum(File file) throws IOException {
		return DigestUtils.shaHex(FileUtils.openInputStream(file));
	}
	
	protected static String generateRandomString() {
		return RandomStringUtils.random(20, true, true);
	}
	
	protected abstract void testSetUp() throws Exception;
	
	protected abstract void testTearDown() throws Exception;
}
