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
import org.junit.Test;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.ext.html.FormData;
import org.restlet.ext.html.FormDataSet;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import com.buddycloud.mediaserver.MediaServerTest;
import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.MediaServerConfiguration;

public class UploadMediaTest extends MediaServerTest {

	public void testTearDown() throws Exception {
		FileUtils
				.cleanDirectory(new File(
						configuration
								.getProperty(MediaServerConfiguration.MEDIA_STORAGE_ROOT_PROPERTY)
								+ File.separator + BASE_CHANNEL));
	}

	@Override
	protected void testSetUp() throws Exception {
	}

	@Test
	public void uploadImage() throws Exception {
		// file fields
		String title = "Test Image";
		String description = "My Test Image";

		ClientResource client = new ClientResource(BASE_URL + "/"
				+ BASE_CHANNEL);
		client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER,
				BASE_TOKEN);

		FormDataSet form = new FormDataSet();
		form.setMultipart(true);
		form.getEntries().add(
				new FormData(Constants.NAME_FIELD, new StringRepresentation(
						TESTIMAGE_NAME)));
		form.getEntries().add(
				new FormData(Constants.TITLE_FIELD, new StringRepresentation(
						title)));
		form.getEntries().add(
				new FormData(Constants.DESC_FIELD, new StringRepresentation(
						description)));
		form.getEntries().add(
				new FormData(Constants.AUTHOR_FIELD, new StringRepresentation(
						BASE_USER)));

		form.getEntries().add(
				new FormData(Constants.FILE_FIELD, new FileRepresentation(
						TESTFILE_PATH + TESTIMAGE_NAME, MediaType.IMAGE_JPEG)));

		Representation result = client.post(form);
		Media media = gson.fromJson(result.getText(), Media.class);

		// verify if resultant media has the passed attributes
		assertEquals(TESTIMAGE_NAME, media.getFileName());
		assertEquals(title, media.getTitle());
		assertEquals(description, media.getDescription());
		assertEquals(BASE_USER, media.getAuthor());

		// delete metadata
		dataSource.deleteMedia(media.getId());
	}

	@Test
	public void uploadImageParamAuth() throws Exception {
		// file fields
		String title = "Test Image";
		String description = "My Test Image";

		Base64 encoder = new Base64(true);
		String authStr = BASE_USER + ":" + BASE_TOKEN;

		ClientResource client = new ClientResource(BASE_URL + "/"
				+ BASE_CHANNEL + "?auth="
				+ new String(encoder.encode(authStr.getBytes())));

		FormDataSet form = new FormDataSet();
		form.setMultipart(true);
		form.getEntries().add(
				new FormData(Constants.NAME_FIELD, new StringRepresentation(
						TESTIMAGE_NAME)));
		form.getEntries().add(
				new FormData(Constants.TITLE_FIELD, new StringRepresentation(
						title)));
		form.getEntries().add(
				new FormData(Constants.DESC_FIELD, new StringRepresentation(
						description)));
		form.getEntries().add(
				new FormData(Constants.AUTHOR_FIELD, new StringRepresentation(
						BASE_USER)));

		form.getEntries().add(
				new FormData(Constants.FILE_FIELD, new FileRepresentation(
						TESTFILE_PATH + TESTIMAGE_NAME, MediaType.IMAGE_JPEG)));

		Representation result = client.post(form);
		Media media = gson.fromJson(result.getText(), Media.class);

		// verify if resultant media has the passed attributes
		assertEquals(TESTIMAGE_NAME, media.getFileName());
		assertEquals(title, media.getTitle());
		assertEquals(description, media.getDescription());
		assertEquals(BASE_USER, media.getAuthor());

		// delete metadata
		dataSource.deleteMedia(media.getId());
	}

	@Test
	public void uploadVideo() throws Exception {
		// file fields
		String title = "Test Video";
		String description = "My Test Video";

		ClientResource client = new ClientResource(BASE_URL + "/"
				+ BASE_CHANNEL);
		client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER,
				BASE_TOKEN);

		FormDataSet form = new FormDataSet();
		form.setMultipart(true);
		form.getEntries().add(
				new FormData(Constants.NAME_FIELD, new StringRepresentation(
						TESTVIDEO_NAME)));
		form.getEntries().add(
				new FormData(Constants.TITLE_FIELD, new StringRepresentation(
						title)));
		form.getEntries().add(
				new FormData(Constants.DESC_FIELD, new StringRepresentation(
						description)));
		form.getEntries().add(
				new FormData(Constants.AUTHOR_FIELD, new StringRepresentation(
						BASE_USER)));

		form.getEntries().add(
				new FormData(Constants.FILE_FIELD, new FileRepresentation(
						TESTFILE_PATH + TESTVIDEO_NAME, MediaType.VIDEO_AVI)));

		Representation result = client.post(form);
		Media media = gson.fromJson(result.getText(), Media.class);

		// verify if resultant media has the passed attributes
		assertEquals(TESTVIDEO_NAME, media.getFileName());
		assertEquals(title, media.getTitle());
		assertEquals(description, media.getDescription());
		assertEquals(BASE_USER, media.getAuthor());

		// delete metadata
		dataSource.deleteMedia(media.getId());
	}
}