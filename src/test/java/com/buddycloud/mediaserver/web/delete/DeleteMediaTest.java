package com.buddycloud.mediaserver.web.delete;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.restlet.resource.ClientResource;

import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.web.MediaResourceTest;

public class DeleteMediaTest extends MediaResourceTest {
	
	public void testTearDown() throws Exception {
		FileUtils.cleanDirectory(new File(configuration.getProperty(Constants.MEDIA_STORAGE_ROOT_PROPERTY) + 
				File.separator + BASE_CHANNEL));
		
		dataSource.deleteMedia(MEDIA_ID);
	}
	
	@Override
	protected void testSetUp() throws Exception {
		File destDir = new File(configuration.getProperty(Constants.MEDIA_STORAGE_ROOT_PROPERTY) + File.separator + BASE_CHANNEL);
		if (!destDir.mkdir()) {
			FileUtils.cleanDirectory(destDir);
		}
		
		FileUtils.copyFile(new File(TESTFILE_PATH + TESTMEDIA_NAME), new File(destDir + File.separator + MEDIA_ID));
		
		Media media = buildMedia(MEDIA_ID, TESTFILE_PATH + TESTMEDIA_NAME);
		dataSource.storeMedia(media);
	}
	
	@Test
	public void anonymousSuccessfulDelete() throws Exception {
		ClientResource client = new ClientResource(BASE_URL + "/media/" + BASE_CHANNEL + "/" + MEDIA_ID);
		
		client.delete();
	}
}