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

import junit.framework.Assert;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;

import com.buddycloud.mediaserver.MediaServerTest;
import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.MediaServerConfiguration;

public class DeleteAvatarTest extends MediaServerTest {

	private File fileToDelete;

	public void testTearDown() throws Exception {
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

		fileToDelete = new File(destDir + File.separator + MEDIA_ID);
		FileUtils.copyFile(new File(TESTFILE_PATH + TESTAVATAR_NAME),
				fileToDelete);

		Media media = buildMedia(MEDIA_ID, TESTFILE_PATH + TESTAVATAR_NAME);
		dataSource.storeMedia(media);
		dataSource.storeAvatar(media);
	}

	@Test
	public void anonymousSuccessfulDelete() throws Exception {
		ClientResource client = new ClientResource(BASE_URL + BASE_CHANNEL
				+ "/media/avatar");
		client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER,
				BASE_TOKEN);

		client.delete();

		Assert.assertFalse(fileToDelete.exists());
	}

	@Test
	public void anonymousSuccessfulDeleteParamAuth() throws Exception {
		Base64 encoder = new Base64(true);
		String authStr = BASE_USER + ":" + BASE_TOKEN;

		ClientResource client = new ClientResource(BASE_URL + BASE_CHANNEL
				+ "/media/avatar" + "?auth="
				+ new String(encoder.encode(authStr.getBytes())));

		client.delete();

		Assert.assertFalse(fileToDelete.exists());
	}
}