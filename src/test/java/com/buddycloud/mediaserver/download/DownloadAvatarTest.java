package com.buddycloud.mediaserver.download;

import java.io.File;
import java.io.FileOutputStream;

import junit.framework.Assert;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;

import com.buddycloud.mediaserver.MediaServerTest;
import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.MediaServerConfiguration;

public class DownloadAvatarTest extends MediaServerTest {
	
	private static final String TEST_OUTPUT_DIR = "test";

	
	public void testTearDown() throws Exception {
		FileUtils.cleanDirectory(new File(configuration.getProperty(MediaServerConfiguration.MEDIA_STORAGE_ROOT_PROPERTY) + 
				File.separator + BASE_CHANNEL));
		
		dataSource.deleteEntityAvatar(BASE_CHANNEL);
		dataSource.deleteMedia(MEDIA_ID);
	}
	
	@Override
	protected void testSetUp() throws Exception {
		File destDir = new File(configuration.getProperty(MediaServerConfiguration.MEDIA_STORAGE_ROOT_PROPERTY) + File.separator + BASE_CHANNEL);
		if (!destDir.mkdir()) {
			FileUtils.cleanDirectory(destDir);
		}
		
		FileUtils.copyFile(new File(TESTFILE_PATH + TESTAVATAR_NAME), new File(destDir + File.separator + MEDIA_ID));
		
		Media media = buildMedia(MEDIA_ID, TESTFILE_PATH + TESTAVATAR_NAME);
		dataSource.storeMedia(media);
		dataSource.storeAvatar(media);
	}
	
	@Test
	public void anonymousSuccessfulDownload() throws Exception {
		ClientResource client = new ClientResource(BASE_URL + "/" + BASE_CHANNEL + "/media/avatar");
		client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER, BASE_TOKEN);
		
		File file = new File(TEST_OUTPUT_DIR + File.separator + "downloadedAvatar.jpg");
		FileOutputStream outputStream = FileUtils.openOutputStream(file);
		client.get().write(outputStream);

		Assert.assertTrue(file.exists());
		
		// Delete downloaded file
		FileUtils.deleteDirectory(new File(TEST_OUTPUT_DIR));
		outputStream.close();
	}
	
	@Test
	public void anonymousSuccessfulDownloadParamAuth() throws Exception {
		Base64 encoder = new Base64(true);
		String authStr = BASE_USER + ":" + BASE_TOKEN;
		
		ClientResource client = new ClientResource(BASE_URL + "/" + BASE_CHANNEL + "/media/avatar" +
				"?auth=" + new String(encoder.encode(authStr.getBytes())));
		
		File file = new File(TEST_OUTPUT_DIR + File.separator + "downloadedAvatar.jpg");
		FileOutputStream outputStream = FileUtils.openOutputStream(file);
		client.get().write(outputStream);

		Assert.assertTrue(file.exists());
		
		// Delete downloaded file
		FileUtils.deleteDirectory(new File(TEST_OUTPUT_DIR));
		outputStream.close();
	}
	
	@Test
	public void anonymousPreviewSuccessfulDownload() throws Exception {
		int height = 50;
		int width = 50;
		String url = BASE_URL + "/" + BASE_CHANNEL + "/media/avatar?maxheight=" + height + "&maxwidth=" + width;
		
		ClientResource client = new ClientResource(url);
		client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER, BASE_TOKEN);
		
		File file = new File(TEST_OUTPUT_DIR + File.separator + "avatarPreview.jpg");
		FileOutputStream outputStream = FileUtils.openOutputStream(file);
		client.get().write(outputStream);

		Assert.assertTrue(file.exists());
		
		// Delete downloaded file
		FileUtils.deleteDirectory(new File(TEST_OUTPUT_DIR));
		outputStream.close();
		
		// Delete previews table row
		final String previewId = dataSource.getPreviewId(MEDIA_ID, height, width);
		dataSource.deletePreview(previewId);
	}
	
	@Test
	public void anonymousPreviewSuccessfulDownloadParamAuth() throws Exception {
		int height = 50;
		int width = 50;
		
		Base64 encoder = new Base64(true);
		String authStr = BASE_USER + ":" + BASE_TOKEN;
		
		String url = BASE_URL + "/" + BASE_CHANNEL + "/media/avatar?maxheight=" + height + "&maxwidth=" + width +
				"?auth=" + new String(encoder.encode(authStr.getBytes()));
		
		ClientResource client = new ClientResource(url);
		client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER, BASE_TOKEN);
		
		File file = new File(TEST_OUTPUT_DIR + File.separator + "avatarPreview.jpg");
		FileOutputStream outputStream = FileUtils.openOutputStream(file);
		client.get().write(outputStream);

		Assert.assertTrue(file.exists());
		
		// Delete downloaded file
		FileUtils.deleteDirectory(new File(TEST_OUTPUT_DIR));
		outputStream.close();
		
		// Delete previews table row
		final String previewId = dataSource.getPreviewId(MEDIA_ID, height, width);
		dataSource.deletePreview(previewId);
	}
}