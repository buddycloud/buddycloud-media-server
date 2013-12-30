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
package com.buddycloud.mediaserver;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.engine.util.Base64;
import org.restlet.ext.html.FormData;
import org.restlet.ext.html.FormDataSet;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.StringRepresentation;

import com.buddycloud.mediaserver.business.jdbc.MetaDataSource;
import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.business.util.AudioUtils;
import com.buddycloud.mediaserver.business.util.ImageUtils;
import com.buddycloud.mediaserver.business.util.MimeTypeMapping;
import com.buddycloud.mediaserver.business.util.VideoUtils;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.MediaServerConfiguration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class MediaServerTest {

    protected static final String TEST_JDBC_DRIVER_CLASS = "org.hsqldb.jdbcDriver";
    protected static final String TEST_JDBC_DB_URL = "jdbc:hsqldb:mem:test;user=sa;sql.syntax_pgs=true";
	protected static final String TEST_MEDIA_STORAGE_ROOT = "/tmp";
	protected static final String TEST_IMAGE_NAME = "testimage.jpg";
	protected static final String TEST_IMAGE_CONTENT_TYPE = "image/jpeg";
	protected static final String TEST_VIDEO_NAME = "testvideo.avi";
	protected static final String TEST_VIDEO_CONTENT_TYPE = "video/avi";
	protected static final String TEST_AVATAR_NAME = "testavatar.jpg";
	protected static final String TEST_AVATAR_CONTENT_TYPE = "image/jpeg";
	protected static final String TEST_FILE_PATH = "resources/tests/";

	protected static final String MEDIA_ID = generateRandomString();

    protected static final String SCHEMA_SCRIPTS_PATH = "resources/schema/";

	protected static final String BASE_TOKEN = "secret";
	protected static final String BASE_CHANNEL = "testreg123@buddycloud.org";
	protected static final String BASE_USER = "testreg123@buddycloud.org";
	protected static final String BASE_URL = "http://localhost:8080";

	protected static RestletTest restletTest;
	protected static XMPPTest xmppTest;
	protected static Properties configuration;
	protected static MetaDataSource dataSource;
	protected static Gson gson;


    @BeforeClass
    public static void prepareEnv() throws Exception {
        configuration = MediaServerConfiguration.getInstance()
                .getConfiguration();
        configuration.setProperty(
                MediaServerConfiguration.MEDIA_STORAGE_ROOT_PROPERTY,
                TEST_MEDIA_STORAGE_ROOT);
        configuration.setProperty(MediaServerConfiguration.JDBC_DRIVER_CLASS_PROPERTY,
                TEST_JDBC_DRIVER_CLASS);
        configuration.setProperty(MediaServerConfiguration.JDBC_DB_URL_PROPERTY,
                TEST_JDBC_DB_URL);

        dataSource = new MetaDataSource();
        gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();

        start();
        createSchema();
    }

    @AfterClass
    public static void destroyEnv() throws Exception {
        restletTest.shutdown();
        xmppTest.shutdown();
        dropSchema();
    }

    @Before
    public void setUp() throws Exception {
        testSetUp();
        Thread.sleep(1000);
    }

    @After
    public void tearDown() throws Exception {
        testTearDown();
    }

    private static void createSchema() throws IOException, SQLException {
        load("create_schema");
    }

    private static void dropSchema() throws IOException, SQLException {
        load("drop_schema");
    }

    public static void load(final String scriptName) throws SQLException, IOException {
        FileInputStream fileInputStream = new FileInputStream(SCHEMA_SCRIPTS_PATH + scriptName + ".sql");
        runScript(new InputStreamReader(fileInputStream));
    }

    public static void runScript(final Reader inputStream) throws SQLException, IOException {
        // Now read line bye line
        BufferedReader reader = new BufferedReader(inputStream);
        String currLine;
        StringBuilder query = new StringBuilder();
        Statement statement = dataSource.getConnection().createStatement();

        while ((currLine = reader.readLine()) != null) {
            // Skip comments and empty lines
            if (currLine.length() > 0 && currLine.charAt(0) == '-' || currLine.length() == 0) {
                continue;
            }

            query.append(" " + currLine);

            // If one command complete
            if (query.charAt(query.length() - 1) == ';') {
                query = query.replace(query.length() - 1, query.length(), " ");

                // Execute
                statement.execute(query.toString());
                query = new StringBuilder();
            }
        }

        dataSource.close(statement);
    }

	protected static void start() throws Exception {
        restletTest = new RestletTest();
		restletTest.start(configuration);

		xmppTest = new XMPPTest();
		xmppTest.start(configuration);
	}

	protected Media buildMedia(String mediaId, String filePath)
			throws Exception {
		File file = new File(filePath);
		String fileName = file.getName();
		String extension = fileName.substring(fileName.indexOf(".") + 1);

		Media media = new Media();
		media.setId(mediaId);
		media.setFileName(fileName);
		media.setEntityId(BASE_CHANNEL);
		media.setAuthor(BASE_USER);
		media.setDescription("A description");
		media.setTitle("A title");
		media.setFileSize(file.length());
		media.setShaChecksum(getFileShaChecksum(file));
		media.setFileExtension(extension);
		media.setMimeType(MimeTypeMapping.lookupMimeType(extension));

		if (ImageUtils.isImage(extension)) {
			BufferedImage img = ImageIO.read(file);
			media.setHeight(img.getHeight());
			media.setWidth(img.getWidth());
		} else if (VideoUtils.isVideo(extension)) {
			VideoUtils videoUtils = new VideoUtils(file);
			media.setLength(videoUtils.getVideoLength());
			media.setHeight(videoUtils.getVideoHeight());
			media.setWidth(videoUtils.getVideoWidth());
		} else if (AudioUtils.isAudio(extension)) {
			media.setLength(AudioUtils.getAudioLength(file));
		}

		return media;
	}

	private String getFileShaChecksum(File file) throws IOException {
		return DigestUtils.shaHex(FileUtils.openInputStream(file));
	}

	protected static String generateRandomString() {
		return RandomStringUtils.random(20, true, true);
	}
	
	protected Form createWebForm(String name, String title, 
			String description, String filePath, String contentType) {
		Form form = new Form();
		
		if (name != null) {
			form.set(Constants.NAME_FIELD, name);
		}
		
		if (title != null) {
			form.set(Constants.TITLE_FIELD, title);
		}
		
		if (description != null) {
			form.set(Constants.DESC_FIELD, description);
		}
		
		if (contentType != null) {
			form.set(Constants.TYPE_FIELD, contentType);
		}
		
		if (filePath != null) {
			try {
				File file = new File(filePath);
				byte[] byteArray = FileUtils.readFileToByteArray(file);

				form.set(Constants.DATA_FIELD, Base64.encode(byteArray, false));
			} catch (IOException e) {}
		}
		
		return form;
	}
	
	protected FormDataSet createFormData(String name, String title, 
			String description, String filePath, String contentType) {
		FormDataSet form = new FormDataSet();
		form.setMultipart(true);

		if (name != null) {
			form.getEntries().add(new FormData(Constants.NAME_FIELD, new StringRepresentation(name)));
		}
		
		if (title != null) {
			form.getEntries().add(new FormData(Constants.TITLE_FIELD, new StringRepresentation(title)));
		}
		
		if (description != null) {
			form.getEntries().add(new FormData(Constants.DESC_FIELD, new StringRepresentation(description)));
		}
		
		if (filePath != null) {
			form.getEntries().add(new FormData(Constants.DATA_FIELD, new FileRepresentation(
					filePath, new MediaType(contentType))));
		}
		
		return form;
	}

	protected abstract void testSetUp() throws Exception;

	protected abstract void testTearDown() throws Exception;
}
