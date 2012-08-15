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

import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.buddycloud.mediaserver.MediaServerTest;
import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.MediaServerConfiguration;
import com.google.gson.reflect.TypeToken;

public class DownloadMediasInfoTest extends MediaServerTest {

	private static final String MEDIA_ID1 = generateRandomString();
	private static final String MEDIA_ID2 = generateRandomString();


	public void testTearDown() throws Exception {
		deleteFile(MEDIA_ID1);
		deleteFile(MEDIA_ID2);
	}

	@Override
	protected void testSetUp() throws Exception {
		storeFile(MEDIA_ID1);
		storeFile(MEDIA_ID2);
	}

	private void storeFile(String id) throws Exception {
		File destDir = new File(
				configuration
				.getProperty(MediaServerConfiguration.MEDIA_STORAGE_ROOT_PROPERTY)
				+ File.separator + BASE_CHANNEL);
		if (!destDir.mkdir()) {
			FileUtils.cleanDirectory(destDir);
		}

		FileUtils.copyFile(new File(TESTFILE_PATH + TESTIMAGE_NAME), new File(
				destDir + File.separator + id));

		Media media = buildMedia(id, TESTFILE_PATH + TESTIMAGE_NAME);
		dataSource.storeMedia(media);
	}

	private void deleteFile(String id) throws Exception {
		FileUtils.cleanDirectory(new File(configuration
				.getProperty(MediaServerConfiguration.MEDIA_STORAGE_ROOT_PROPERTY)
				+ File.separator + BASE_CHANNEL));

		dataSource.deleteMedia(MEDIA_ID);
	}


	@Test
	public void downloadMediasInfo() throws Exception {
		ClientResource client = new ClientResource(BASE_URL + "/"
				+ BASE_CHANNEL + "");
		client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER,
				BASE_TOKEN);

		Representation result = client.get(MediaType.APPLICATION_JSON);
		List<Media> medias = gson.fromJson(result.getText(),  new TypeToken<List<Media>>(){}.getType());

		assertTrue(medias.size() == 2);
	}

	@Test
	public void downloadMediasInfoParamAuth() throws Exception {
		Base64 encoder = new Base64(true);
		String authStr = BASE_USER + ":" + BASE_TOKEN;

		ClientResource client = new ClientResource(BASE_URL + "/"
				+ BASE_CHANNEL + "" + "?auth="
				+ new String(encoder.encode(authStr.getBytes())));

		Representation result = client.get(MediaType.APPLICATION_JSON);
		List<Media> medias = gson.fromJson(result.getText(),  new TypeToken<List<Media>>(){}.getType());

		assertTrue(medias.size() == 2);
	}

	@Test
	public void downloadMediasInfoMax() throws Exception {
		int max = 1;
		ClientResource client = new ClientResource(BASE_URL + "/"
				+ BASE_CHANNEL + "?max=" + max);
		client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER,
				BASE_TOKEN);

		Representation result = client.get(MediaType.APPLICATION_JSON);
		List<Media> medias = gson.fromJson(result.getText(),  new TypeToken<List<Media>>(){}.getType());

		assertTrue(medias.size() == 1);
	}

	@Test
	public void downloadMediasInfoAfter() throws Exception {
		int max = 1;
		ClientResource client = new ClientResource(BASE_URL + "/"
				+ BASE_CHANNEL + "?max=" + max + "&after=" + MEDIA_ID1);
		client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER,
				BASE_TOKEN);

		Representation result = client.get(MediaType.APPLICATION_JSON);
		List<Media> medias = gson.fromJson(result.getText(),  new TypeToken<List<Media>>(){}.getType());

		assertTrue(medias.size() == 1);
	}

}