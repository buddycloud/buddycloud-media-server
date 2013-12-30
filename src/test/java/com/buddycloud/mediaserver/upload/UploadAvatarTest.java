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
package com.buddycloud.mediaserver.upload;

import static junit.framework.Assert.assertEquals;

import java.io.File;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.Test;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Form;
import org.restlet.ext.html.FormDataSet;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.buddycloud.mediaserver.MediaServerTest;
import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.MediaServerConfiguration;
import com.buddycloud.mediaserver.xmpp.AuthVerifier;
import com.buddycloud.mediaserver.xmpp.pubsub.PubSubClient;
import com.buddycloud.mediaserver.xmpp.pubsub.capabilities.CapabilitiesDecorator;

public class UploadAvatarTest extends MediaServerTest {
	
	private static final String URL = BASE_URL + "/"
			+ BASE_CHANNEL + "/avatar";

    private AuthVerifier authClient;
    private PubSubClient pubSubClient;


	public void testTearDown() throws Exception {
		FileUtils.cleanDirectory(new File(configuration
								.getProperty(MediaServerConfiguration.MEDIA_STORAGE_ROOT_PROPERTY)
								+ File.separator + BASE_CHANNEL));

        EasyMock.reset(authClient);
        EasyMock.reset(pubSubClient);
	}

	@Override
	protected void testSetUp() throws Exception {
		authClient = xmppTest.getAuthVerifier();
		EasyMock.expect(authClient.verifyRequest(EasyMock.matches(BASE_USER), EasyMock.matches(BASE_TOKEN), 
				EasyMock.startsWith(URL))).andReturn(true);
		
		pubSubClient = xmppTest.getPubSubClient();
		EasyMock.expect(pubSubClient.matchUserCapability(EasyMock.matches(BASE_USER), 
				EasyMock.matches(BASE_CHANNEL), 
				(CapabilitiesDecorator) EasyMock.notNull())).andReturn(true);
		
		EasyMock.replay(authClient);
		EasyMock.replay(pubSubClient);
	}
	
	@Test
	public void uploadAvatarMultipartFormData() throws Exception {
		// file fields
		String title = "Test Avatar";
		String description = "My Test Avatar";

		ClientResource client = new ClientResource(URL);
		client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER,
				BASE_TOKEN);

		FormDataSet form = createFormData(TEST_AVATAR_NAME, title, 
				description, TEST_FILE_PATH + TEST_AVATAR_NAME,
				TEST_AVATAR_CONTENT_TYPE);

		Representation result = client.put(form);
		Media media = gson.fromJson(result.getText(), Media.class);

		// verify if resultant media has the passed attributes
		assertEquals(TEST_AVATAR_NAME, media.getFileName());
		assertEquals(title, media.getTitle());
		assertEquals(description, media.getDescription());
		assertEquals(BASE_USER, media.getAuthor());

		// delete metadata
		dataSource.deleteEntityAvatar(media.getEntityId());
		dataSource.deleteMedia(media.getId());
	}
	
	@Test
	public void uploadAvatarWebFormData() throws Exception {
		// file fields
		String title = "Test Avatar";
		String description = "My Test Avatar";

		ClientResource client = new ClientResource(URL);
		client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER,
				BASE_TOKEN);

		Form form = createWebForm(TEST_AVATAR_NAME, title, 
				description, TEST_FILE_PATH + TEST_AVATAR_NAME,
				TEST_AVATAR_CONTENT_TYPE);

		Representation result = client.put(form);
		Media media = gson.fromJson(result.getText(), Media.class);

		// verify if resultant media has the passed attributes
		assertEquals(TEST_AVATAR_NAME, media.getFileName());
		assertEquals(title, media.getTitle());
		assertEquals(description, media.getDescription());
		assertEquals(BASE_USER, media.getAuthor());

		// delete metadata
		dataSource.deleteEntityAvatar(media.getEntityId());
		dataSource.deleteMedia(media.getId());
	}

	@Test
	public void uploadAvatarMultipartFormDataParamAuth() throws Exception {
		// file fields
		String title = "Test Avatar";
		String description = "My Test Avatar";

		Base64 encoder = new Base64(true);
		String authStr = BASE_USER + ":" + BASE_TOKEN;

		ClientResource client = new ClientResource(BASE_URL + "/"
				+ BASE_CHANNEL + "/avatar" + "?auth="
				+ new String(encoder.encode(authStr.getBytes())));

		FormDataSet form = createFormData(TEST_AVATAR_NAME, title, 
				description, TEST_FILE_PATH + TEST_AVATAR_NAME,
				TEST_AVATAR_CONTENT_TYPE);

		Representation result = client.put(form);
		Media media = gson.fromJson(result.getText(), Media.class);

		// verify if resultant media has the passed attributes
		assertEquals(TEST_AVATAR_NAME, media.getFileName());
		assertEquals(title, media.getTitle());
		assertEquals(description, media.getDescription());
		assertEquals(BASE_USER, media.getAuthor());

		// delete metadata
		dataSource.deleteEntityAvatar(media.getEntityId());
		dataSource.deleteMedia(media.getId());
	}
	
	@Test
	public void uploadAvatarWebFormDataParamAuth() throws Exception {
		// file fields
		String title = "Test Avatar";
		String description = "My Test Avatar";

		Base64 encoder = new Base64(true);
		String authStr = BASE_USER + ":" + BASE_TOKEN;

		ClientResource client = new ClientResource(BASE_URL + "/"
				+ BASE_CHANNEL + "/avatar" + "?auth="
				+ new String(encoder.encode(authStr.getBytes())));

		Form form = createWebForm(TEST_AVATAR_NAME, title, 
				description, TEST_FILE_PATH + TEST_AVATAR_NAME,
				TEST_AVATAR_CONTENT_TYPE);

		Representation result = client.put(form);
		Media media = gson.fromJson(result.getText(), Media.class);

		// verify if resultant media has the passed attributes
		assertEquals(TEST_AVATAR_NAME, media.getFileName());
		assertEquals(title, media.getTitle());
		assertEquals(description, media.getDescription());
		assertEquals(BASE_USER, media.getAuthor());

		// delete metadata
		dataSource.deleteEntityAvatar(media.getEntityId());
		dataSource.deleteMedia(media.getId());

    }
}