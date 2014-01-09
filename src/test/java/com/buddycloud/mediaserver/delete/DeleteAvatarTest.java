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
package com.buddycloud.mediaserver.delete;

import java.io.File;

import org.junit.Assert;

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

public class DeleteAvatarTest extends MediaServerTest {
	
	private static final String URL = BASE_URL + "/" + BASE_CHANNEL + "/avatar";
	private File fileToDelete;

	
	public void testTearDown() throws Exception {}

	@Override
	protected void testSetUp() throws Exception {
		File destDir = new File(configuration
						.getProperty(MediaServerConfiguration.MEDIA_STORAGE_ROOT_PROPERTY)
						+ File.separator + BASE_CHANNEL);
		if (!destDir.mkdir()) {
			FileUtils.cleanDirectory(destDir);
		}

		fileToDelete = new File(destDir + File.separator + MEDIA_ID);
		FileUtils.copyFile(new File(TEST_FILE_PATH + TEST_AVATAR_NAME),
				fileToDelete);

		Media media = buildMedia(MEDIA_ID, TEST_FILE_PATH + TEST_AVATAR_NAME);
		dataSource.storeMedia(media);
		dataSource.storeAvatar(media);
		
		// mocks
		AuthVerifier authClient = xmppTest.getAuthVerifier();
		EasyMock.expect(authClient.verifyRequest(EasyMock.matches(BASE_USER), EasyMock.matches(BASE_TOKEN), 
				EasyMock.startsWith(URL))).andReturn(true);
		
		PubSubClient pubSubClient = xmppTest.getPubSubClient();
		EasyMock.expect(pubSubClient.matchUserCapability(EasyMock.matches(BASE_USER), 
				EasyMock.matches(BASE_CHANNEL), 
				(CapabilitiesDecorator) EasyMock.notNull())).andReturn(true);
		
		EasyMock.replay(authClient);
		EasyMock.replay(pubSubClient);
	}

	@Test
	public void deleteAvatar() throws Exception {
		ClientResource client = new ClientResource(URL);
		client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER,
				BASE_TOKEN);

		client.delete();

		Assert.assertFalse(fileToDelete.exists());
	}

	@Test
	public void deleteAvatarParamAuth() throws Exception {
		Base64 encoder = new Base64(true);
		String authStr = BASE_USER + ":" + BASE_TOKEN;

		ClientResource client = new ClientResource(URL + "?auth="
				+ new String(encoder.encode(authStr.getBytes())));

		client.delete();

		Assert.assertFalse(fileToDelete.exists());
	}
}