package com.buddycloud.mediaserver.web;

import java.io.File;
import java.io.FileOutputStream;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;
import org.restlet.resource.ClientResource;

import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.exception.MediaMetadataSourceException;

public class DownloadMediaResourceTest extends MediaResourceTest {
	
	private static final String TEST_OUTPUT_DIR = "test";

	
	@After
	public void tearDown() throws Exception {
		FileUtils.cleanDirectory(new File(configuration.getProperty(Constants.MEDIA_STORAGE_ROOT_PROPERTY) + 
				File.separator + BASE_CHANNEL));
		
		dataSource.deleteMetadata(TESTFILE_ID);
	}
	
	@Override
	protected void testSetUp() throws Exception {
		File destDir = new File(configuration.getProperty(Constants.MEDIA_STORAGE_ROOT_PROPERTY) + File.separator + BASE_CHANNEL);
		if (!destDir.mkdir()) {
			FileUtils.cleanDirectory(destDir);
		}
		
		FileUtils.copyFile(new File(TESTFILE_PATH), new File(destDir + File.separator + TESTFILE_ID));
		
		Media media = buildValidTestMedia();
		
		try {
			dataSource.storeMetadata(media);
		} catch (MediaMetadataSourceException e) {
			//do nothing
		}
	}
	
	@Test
	public void anonymousSuccessfulDownload() throws Exception {
		ClientResource client = new ClientResource(BASE_URL + "/" + TESTFILE_ID);
		
		File file = new File(TEST_OUTPUT_DIR + File.separator + "downloaded.jpg");
		FileOutputStream outputStream = FileUtils.openOutputStream(file);
		client.get().write(outputStream);

		Assert.assertTrue(file.exists());
		
		// Delete downloaded file
		FileUtils.deleteDirectory(new File(TEST_OUTPUT_DIR));
		outputStream.close();
	}
}
