package com.buddycloud.mediaserver.download;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.helpers.ISO8601DateFormat;
import org.junit.Test;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.resource.ClientResource;

import com.buddycloud.mediaserver.MediaServerTest;
import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.MediaServerConfiguration;

public class DownloadMediasInfoTest extends MediaServerTest {
	
	public void testTearDown() throws Exception {
		FileUtils.cleanDirectory(new File(configuration.getProperty(MediaServerConfiguration.MEDIA_STORAGE_ROOT_PROPERTY) + 
				File.separator + BASE_CHANNEL));
		
		dataSource.deleteMedia(MEDIA_ID);
	}
	
	@Override
	protected void testSetUp() throws Exception {
		File destDir = new File(configuration.getProperty(MediaServerConfiguration.MEDIA_STORAGE_ROOT_PROPERTY) + File.separator + BASE_CHANNEL);
		if (!destDir.mkdir()) {
			FileUtils.cleanDirectory(destDir);
		}
		
		FileUtils.copyFile(new File(TESTFILE_PATH + TESTIMAGE_NAME), new File(destDir + File.separator + MEDIA_ID));
		
		Media media = buildMedia(MEDIA_ID, TESTFILE_PATH + TESTIMAGE_NAME);
		dataSource.storeMedia(media);
	}
	
	@Test
	public void anonymousSuccessfulDownload() throws Exception {
		ClientResource client = new ClientResource(BASE_URL + "/media/" + BASE_CHANNEL);
		client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER, BASE_TOKEN);
		
		client.get(MediaType.APPLICATION_JSON).write(System.out);
	}
	
	@Test
	public void anonymousSuccessfulDownloadParamAuth() throws Exception {
		Base64 encoder = new Base64(true);
		String authStr = BASE_USER + ":" + BASE_TOKEN;
		
		ClientResource client = new ClientResource(BASE_URL + "/media/" + BASE_CHANNEL +
				"?auth=" + new String(encoder.encode(authStr.getBytes())));
		
		client.get(MediaType.APPLICATION_JSON).write(System.out);
	}
	
	@Test
	public void anonymousSuccessfulDownloadSince() throws Exception {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.add(Calendar.HOUR, -1);
		
		DateFormat dateFormat = ISO8601DateFormat.getInstance();
		
		ClientResource client = new ClientResource(BASE_URL + "/media/" + BASE_CHANNEL + "?since=" + dateFormat.format(calendar.getTime()));
		client.setChallengeResponse(ChallengeScheme.HTTP_BASIC, BASE_USER, BASE_TOKEN);
		
		client.get(MediaType.APPLICATION_JSON).write(System.out);
	}
	
	@Test
	public void anonymousSuccessfulDownloadSinceParamAuth() throws Exception {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.add(Calendar.HOUR, -1);
		
		DateFormat dateFormat = ISO8601DateFormat.getInstance();

		Base64 encoder = new Base64(true);
		String authStr = BASE_USER + ":" + BASE_TOKEN;
		
		ClientResource client = new ClientResource(BASE_URL + "/media/" + BASE_CHANNEL + "?since=" + dateFormat.format(calendar.getTime()) +
				"?auth=" + new String(encoder.encode(authStr.getBytes())));
		
		client.get(MediaType.APPLICATION_JSON).write(System.out);
	}

}