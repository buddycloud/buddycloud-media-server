package com.buddycloud.mediaserver.delete;

import java.io.File;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;

import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.MediaServerTest;

public class DeleteMediaTest extends MediaServerTest {
	
	private File fileToDelete;
	
	
	public void testTearDown() throws Exception {}
	
	@Override
	protected void testSetUp() throws Exception {
		File destDir = new File(configuration.getProperty(Constants.MEDIA_STORAGE_ROOT_PROPERTY) + File.separator + BASE_CHANNEL);
		if (!destDir.mkdir()) {
			FileUtils.cleanDirectory(destDir);
		}
		
		fileToDelete = new File(destDir + File.separator + MEDIA_ID);
		FileUtils.copyFile(new File(TESTFILE_PATH + TESTMEDIA_NAME), fileToDelete);
		
		Media media = buildMedia(MEDIA_ID, TESTFILE_PATH + TESTMEDIA_NAME);
		dataSource.storeMedia(media);
	}
	
	@Test
	public void anonymousSuccessfulDelete() throws Exception {
		ClientResource client = new ClientResource(BASE_URL + "/media/" + BASE_CHANNEL + "/" + MEDIA_ID);
		client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER, BASE_PASSWORD);
		
		client.delete();
		
		Assert.assertFalse(fileToDelete.exists());
	}
}