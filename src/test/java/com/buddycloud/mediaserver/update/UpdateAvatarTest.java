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
package com.buddycloud.mediaserver.update;

import static junit.framework.Assert.assertEquals;

import java.io.File;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.restlet.data.ChallengeScheme;
import org.restlet.ext.html.FormData;
import org.restlet.ext.html.FormDataSet;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import com.buddycloud.mediaserver.MediaServerTest;
import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.MediaServerConfiguration;

public class UpdateAvatarTest extends MediaServerTest {

	public void testTearDown() throws Exception {
		FileUtils
				.cleanDirectory(new File(
						configuration
								.getProperty(MediaServerConfiguration.MEDIA_STORAGE_ROOT_PROPERTY)
								+ File.separator + BASE_CHANNEL));

		dataSource.deleteEntityAvatar(BASE_CHANNEL);
		dataSource.deleteMedia(MEDIA_ID);
	}

	@Override
	protected void testSetUp() throws Exception {
		File destDir = new File(
				configuration
						.getProperty(MediaServerConfiguration.MEDIA_STORAGE_ROOT_PROPERTY)
						+ File.separator + BASE_CHANNEL);
		if (!destDir.mkdir()) {
			FileUtils.cleanDirectory(destDir);
		}

		FileUtils.copyFile(new File(TESTFILE_PATH + TESTAVATAR_NAME), new File(
				destDir + File.separator + MEDIA_ID));

		Media media = buildMedia(MEDIA_ID, TESTFILE_PATH + TESTAVATAR_NAME);
		dataSource.storeMedia(media);
		dataSource.storeAvatar(media);
	}

	@Test
	public void anonymousSuccessfulUpdate() throws Exception {
		// file fields
		String title = "New Avatar";
		String description = "New Avatar Description";

		ClientResource client = new ClientResource(BASE_URL + "/"
				+ BASE_CHANNEL + "/avatar");
		client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER,
				BASE_TOKEN);

		FormDataSet form = new FormDataSet();
		form.setMultipart(true);
		form.getEntries().add(
				new FormData(Constants.TITLE_FIELD, new StringRepresentation(
						title)));
		form.getEntries().add(
				new FormData(Constants.DESC_FIELD, new StringRepresentation(
						description)));

		Representation result = client.post(form);
		Media media = gson.fromJson(result.getText(), Media.class);

		// verify if resultant media has the passed attributes
		assertEquals(title, media.getTitle());
		assertEquals(description, media.getDescription());
	}

	@Test
	public void anonymousSuccessfulUpdateParamAuth() throws Exception {
		// file fields
		String title = "New Avatar";
		String description = "New Avatar Description";

		Base64 encoder = new Base64(true);
		String authStr = BASE_USER + ":" + BASE_TOKEN;

		ClientResource client = new ClientResource(BASE_URL + "/"
				+ BASE_CHANNEL + "/avatar" + "?auth="
				+ new String(encoder.encode(authStr.getBytes())));

		FormDataSet form = new FormDataSet();
		form.setMultipart(true);
		form.getEntries().add(
				new FormData(Constants.TITLE_FIELD, new StringRepresentation(
						title)));
		form.getEntries().add(
				new FormData(Constants.DESC_FIELD, new StringRepresentation(
						description)));

		Representation result = client.post(form);
		Media media = gson.fromJson(result.getText(), Media.class);

		// verify if resultant media has the passed attributes
		assertEquals(title, media.getTitle());
		assertEquals(description, media.getDescription());
	}

}