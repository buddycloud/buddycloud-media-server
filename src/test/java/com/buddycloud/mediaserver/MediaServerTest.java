package com.buddycloud.mediaserver;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Properties;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.restlet.Component;

import com.buddycloud.mediaserver.business.jdbc.MetaDataSource;
import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.MediaServerConfiguration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class MediaServerTest {
	
	protected static final String TEST_MEDIA_STORAGE_ROOT = "/tmp";
	protected static final String TESTMEDIA_NAME = "testimage.jpg";
	protected static final String TESTAVATAR_NAME = "testavatar.jpg";
	protected static final String TESTFILE_PATH = "resources/tests/";
	
	protected static final String TEST_OUTPUT_DIR = "test";
	protected static final String MEDIA_ID = generateRandomString();

	protected static final String BASE_TOKEN = "secret";
	protected static final String BASE_CHANNEL = "mediaservertest@topics.buddycloud.org";
	protected static final String BASE_USER = "rodrigods@buddycloud.org";
	protected static final String BASE_URL = "http://localhost:8080";
	

	protected Component component;
	protected Properties configuration;
	protected MetaDataSource dataSource;
	protected Gson gson;
	
	
	@Before
	public void setUp() throws Exception {
		configuration = MediaServerConfiguration.getInstance().getConfiguration();
		configuration.setProperty(MediaServerConfiguration.MEDIA_STORAGE_ROOT_PROPERTY, TEST_MEDIA_STORAGE_ROOT);
		
		dataSource = new MetaDataSource();
		gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
		
		start();
	    testSetUp();
	    
	    Thread.sleep(1000);
	}
	
	@After
	public void tearDown() throws Exception {
		component.stop();
		
		testTearDown();
	}
	
	protected void start() throws Exception {
		component = new RestletTest().start();
		new XMPPTest().start(configuration);
	}
	
	protected Media buildMedia(String mediaId, String filePath) throws Exception {
		File file = new File(filePath);
		
		Media media = new Media();
		media.setId(mediaId);
		media.setFileName(TESTMEDIA_NAME);
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
