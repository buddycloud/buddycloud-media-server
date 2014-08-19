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
package com.buddycloud.mediaserver.download;

import com.buddycloud.mediaserver.MediaServerTest;
import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.MediaServerConfiguration;
import com.buddycloud.mediaserver.xmpp.AuthVerifier;
import com.buddycloud.mediaserver.xmpp.pubsub.PubSubClient;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Status;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DownloadImageTest extends MediaServerTest {
	private static final String URL = BASE_URL + "/" + BASE_CHANNEL + "/" + MEDIA_ID;
	private static final String TEST_OUTPUT_DIR = "test";

    private AuthVerifier authClient;
    private PubSubClient pubSubClient;


    public void testTearDown() throws Exception {
        clearFilesAndDB();

        // Verify mocks
        EasyMock.verify(authClient);
        EasyMock.verify(pubSubClient);

        // Reset mocks
        EasyMock.reset(authClient);
        EasyMock.reset(pubSubClient);
    }

    private void clearFilesAndDB() throws Exception {
        FileUtils.cleanDirectory(new File(configuration
                .getProperty(MediaServerConfiguration.MEDIA_STORAGE_ROOT_PROPERTY)
                + File.separator + BASE_CHANNEL));

        dataSource.deleteMedia(MEDIA_ID);
    }

	@Override
    protected void testSetUp() throws Exception {
		setupFilesAndDB();
		setupMocks();
    }

    private void setupFilesAndDB() throws Exception {
        setupFiles();
        Media media = buildMedia(MEDIA_ID, TEST_FILE_PATH + TEST_IMAGE_NAME);
        dataSource.storeMedia(media);
    }
    
    private void setupFiles() throws Exception {
        File destDir = new File(configuration
                .getProperty(MediaServerConfiguration.MEDIA_STORAGE_ROOT_PROPERTY)
                + File.separator + BASE_CHANNEL);
        if (!destDir.mkdir()) {
            FileUtils.cleanDirectory(destDir);
        }

        FileUtils.copyFile(new File(TEST_FILE_PATH + TEST_IMAGE_NAME), new File(
                destDir + File.separator + MEDIA_ID));
    }

    private void setupMocks() {
        authClient = xmppTest.getAuthVerifier();

        pubSubClient = xmppTest.getPubSubClient();
        EasyMock.expect(pubSubClient.isChannelPublic(EasyMock.matches(BASE_CHANNEL))).andReturn(true);

        EasyMock.replay(authClient);
        EasyMock.replay(pubSubClient);
    }

	@Test
	public void downloadImage() throws Exception {
		ClientResource client = new ClientResource(URL);
		client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER,
				BASE_TOKEN);

		File file = new File(TEST_OUTPUT_DIR + File.separator
				+ "downloaded.jpg");
		FileOutputStream outputStream = FileUtils.openOutputStream(file);
		client.get().write(outputStream);
        outputStream.close();

        assertTrue(file.exists());

        // Delete downloaded file
        FileUtils.deleteDirectory(new File(TEST_OUTPUT_DIR));
	}

	@Test
	public void downloadImageParamAuth() throws Exception {
		Base64 encoder = new Base64(true);
		String authStr = BASE_USER + ":" + BASE_TOKEN;

		ClientResource client = new ClientResource(URL + "?auth="
				+ new String(encoder.encode(authStr.getBytes())));

		File file = new File(TEST_OUTPUT_DIR + File.separator
				+ "downloaded.jpg");
		FileOutputStream outputStream = FileUtils.openOutputStream(file);
		client.get().write(outputStream);
        outputStream.close();

        assertTrue(file.exists());

        // Delete downloaded file
        FileUtils.deleteDirectory(new File(TEST_OUTPUT_DIR));
	}

    @Test
    public void downloadImagePreview() throws Exception {
        int height = 50;
        int width = 50;
        String completeUrl = URL + "?maxheight=" + height + "&maxwidth=" + width;

        ClientResource client = new ClientResource(completeUrl);
        client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER,
                BASE_TOKEN);

        File file = new File(TEST_OUTPUT_DIR + File.separator + "preview.jpg");
        FileOutputStream outputStream = FileUtils.openOutputStream(file);
        client.get().write(outputStream);
        outputStream.close();

        assertTrue(file.exists());

        // Delete downloaded file
        FileUtils.deleteDirectory(new File(TEST_OUTPUT_DIR));

        // Delete previews table row
        final String previewId = dataSource.getPreviewId(MEDIA_ID, height,
                width);
        dataSource.deletePreview(previewId);
    }

    @Test
    public void downloadPreviewBiggerThanImage() throws Exception {
        BufferedImage originalImage = ImageIO.read(new File(TEST_FILE_PATH + TEST_IMAGE_NAME));
        int height = originalImage.getHeight() + 1;
        int width = originalImage.getWidth() + 1;
        String completeUrl = URL + "?maxheight=" + height + "&maxwidth=" + width;

        ClientResource client = new ClientResource(completeUrl);
        client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER,
                BASE_TOKEN);

        File file = new File(TEST_OUTPUT_DIR + File.separator + "preview.jpg");
        FileOutputStream outputStream = FileUtils.openOutputStream(file);
        client.get().write(outputStream);
        outputStream.close();

        assertTrue(file.exists());

        // Downloads the original image
        BufferedImage downloadedImage = ImageIO.read(file);
        assertEquals(downloadedImage.getHeight(), originalImage.getHeight());
        assertEquals(downloadedImage.getWidth(), originalImage.getWidth());

        // Delete downloaded file
        FileUtils.deleteDirectory(new File(TEST_OUTPUT_DIR));

        // Delete previews table row
        final String previewId = dataSource.getPreviewId(MEDIA_ID, height,
                width);
        dataSource.deletePreview(previewId);
    }

	@Test
	public void downloadImagePreviewParamAuth() throws Exception {
		int height = 50;
		int width = 50;
		Base64 encoder = new Base64(true);
		String authStr = BASE_USER + ":" + BASE_TOKEN;

		String completeUrl = URL + "?maxheight=" + height + "&maxwidth=" + width + "&auth="
				+ new String(encoder.encode(authStr.getBytes()));
		
		ClientResource client = new ClientResource(completeUrl);

		File file = new File(TEST_OUTPUT_DIR + File.separator + "preview.jpg");
		FileOutputStream outputStream = FileUtils.openOutputStream(file);
		client.get().write(outputStream);
        outputStream.close();

        assertTrue(file.exists());

        // Delete downloaded file
        FileUtils.deleteDirectory(new File(TEST_OUTPUT_DIR));

		// Delete previews table row
		final String previewId = dataSource.getPreviewId(MEDIA_ID, height,
				width);
		dataSource.deletePreview(previewId);
	}
	
	@Test
	public void downloadFileExistsMediaNull() throws Exception {
		dataSource.deleteMedia(MEDIA_ID);
		ClientResource client = new ClientResource(URL);
		client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER,
				BASE_TOKEN);

		File file = new File(TEST_OUTPUT_DIR + File.separator
				+ "downloaded.jpg");
		FileOutputStream outputStream = FileUtils.openOutputStream(file);
		try {
			client.get().write(outputStream);
			Assert.fail();
		} catch (Exception e) {
			Assert.assertTrue(e instanceof ResourceException);
			ResourceException re = (ResourceException) e;
			Assert.assertEquals(Status.CLIENT_ERROR_NOT_FOUND, re.getStatus());
		} finally {
			outputStream.close();
		}

        assertTrue(file.exists());

        // Delete downloaded file
        FileUtils.deleteDirectory(new File(TEST_OUTPUT_DIR));
	}
}