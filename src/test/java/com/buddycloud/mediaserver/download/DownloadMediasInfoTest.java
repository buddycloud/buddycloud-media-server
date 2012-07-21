package com.buddycloud.mediaserver.download;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.resource.ClientResource;

import com.buddycloud.mediaserver.MediaServerTest;
import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.Constants;

public class DownloadMediasInfoTest extends MediaServerTest {
	
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
	public void anonymousSuccessfulDownload() throws Exception {
		ClientResource client = new ClientResource(BASE_URL + "/media/" + BASE_CHANNEL);
		client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER, BASE_PASSWORD);
		
		client.get(MediaType.APPLICATION_JSON).write(System.out);
	}
	
	@Test
	public void anonymousSuccessfulDownloadSince() throws Exception {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.add(Calendar.HOUR, -1);
		
		ClientResource client = new ClientResource(BASE_URL + "/media/" + BASE_CHANNEL + "?since=" + calendar.getTimeInMillis());
		client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER, BASE_PASSWORD);
		
		client.get(MediaType.APPLICATION_JSON).write(System.out);
	}

}