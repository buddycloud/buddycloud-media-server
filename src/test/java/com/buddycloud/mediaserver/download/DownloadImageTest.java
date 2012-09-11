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

import java.io.File;
import java.io.FileOutputStream;

import junit.framework.Assert;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.Test;
import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;

import com.buddycloud.mediaserver.MediaServerTest;
import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.MediaServerConfiguration;
import com.buddycloud.mediaserver.xmpp.AuthVerifier;
import com.buddycloud.mediaserver.xmpp.pubsub.PubSubClient;
import com.buddycloud.mediaserver.xmpp.pubsub.capabilities.CapabilitiesDecorator;

public class DownloadImageTest extends MediaServerTest {
	private static final String URL = BASE_URL + "/" + BASE_CHANNEL + "/" + MEDIA_ID;
	private static final String TEST_OUTPUT_DIR = "test";

	
	public void testTearDown() throws Exception {
		FileUtils.cleanDirectory(new File(configuration
				.getProperty(MediaServerConfiguration.MEDIA_STORAGE_ROOT_PROPERTY)
				+ File.separator + BASE_CHANNEL));

		dataSource.deleteMedia(MEDIA_ID);
	}

	@Override
	protected void testSetUp() throws Exception {
		File destDir = new File(configuration
						.getProperty(MediaServerConfiguration.MEDIA_STORAGE_ROOT_PROPERTY)
						+ File.separator + BASE_CHANNEL);
		if (!destDir.mkdir()) {
			FileUtils.cleanDirectory(destDir);
		}

		FileUtils.copyFile(new File(TEST_FILE_PATH + TEST_IMAGE_NAME), new File(
				destDir + File.separator + MEDIA_ID));

		Media media = buildMedia(MEDIA_ID, TEST_FILE_PATH + TEST_IMAGE_NAME);
		dataSource.storeMedia(media);
		
		// mocks
		AuthVerifier authClient = xmppTest.getAuthVerifier();
		EasyMock.expect(authClient.verifyRequest(BASE_USER, BASE_TOKEN, 
				URL)).andReturn(true);
		
		PubSubClient pubSubClient = xmppTest.getPubSubClient();
		EasyMock.expect(pubSubClient.matchUserCapability(EasyMock.contains(BASE_USER), 
				EasyMock.contains(BASE_CHANNEL), 
				(CapabilitiesDecorator) EasyMock.notNull())).andReturn(true);
		
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

		Assert.assertTrue(file.exists());

		// Delete downloaded file
		FileUtils.deleteDirectory(new File(TEST_OUTPUT_DIR));
		outputStream.close();
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

		Assert.assertTrue(file.exists());

		// Delete downloaded file
		FileUtils.deleteDirectory(new File(TEST_OUTPUT_DIR));
		outputStream.close();
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

		Assert.assertTrue(file.exists());

		// Delete downloaded file
		FileUtils.deleteDirectory(new File(TEST_OUTPUT_DIR));
		outputStream.close();

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

		String completeUrl = URL + "?maxheight=" + height + "&maxwidth=" + width + "?auth="
				+ new String(encoder.encode(authStr.getBytes()));
		
		ClientResource client = new ClientResource(completeUrl);

		File file = new File(TEST_OUTPUT_DIR + File.separator + "preview.jpg");
		FileOutputStream outputStream = FileUtils.openOutputStream(file);
		client.get().write(outputStream);

		Assert.assertTrue(file.exists());

		// Delete downloaded file
		FileUtils.deleteDirectory(new File(TEST_OUTPUT_DIR));
		outputStream.close();

		// Delete previews table row
		final String previewId = dataSource.getPreviewId(MEDIA_ID, height,
				width);
		dataSource.deletePreview(previewId);
	}
}