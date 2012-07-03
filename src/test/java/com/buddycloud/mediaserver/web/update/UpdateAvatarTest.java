package com.buddycloud.mediaserver.web.update;

import static junit.framework.Assert.assertEquals;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.restlet.ext.html.FormData;
import org.restlet.ext.html.FormDataSet;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.web.MediaResourceTest;

public class UpdateAvatarTest extends MediaResourceTest {
	
	public void testTearDown() throws Exception {
		FileUtils.cleanDirectory(new File(configuration.getProperty(Constants.MEDIA_STORAGE_ROOT_PROPERTY) + 
				File.separator + BASE_CHANNEL));
		
		dataSource.deleteEntityAvatar(BASE_CHANNEL);
		dataSource.deleteMedia(MEDIA_ID);
	}
	
	@Override
	protected void testSetUp() throws Exception {
		File destDir = new File(configuration.getProperty(Constants.MEDIA_STORAGE_ROOT_PROPERTY) + File.separator + BASE_CHANNEL);
		if (!destDir.mkdir()) {
			FileUtils.cleanDirectory(destDir);
		}
		
		FileUtils.copyFile(new File(TESTFILE_PATH + TESTAVATAR_NAME), new File(destDir + File.separator + MEDIA_ID));
		
		Media media = buildMedia(MEDIA_ID, TESTFILE_PATH + TESTAVATAR_NAME);
		dataSource.storeMedia(media);
		dataSource.storeAvatar(media);
	}
	
	@Test
	public void anonymousSuccessfulUpdate() throws Exception {
		// file fields
		String title = "New Avatar";
		String description = "New Avatar Description";

		ClientResource client = new ClientResource(BASE_URL + "/media/" + BASE_CHANNEL + "/avatar");

		FormDataSet form = new FormDataSet();
		form.setMultipart(true);
		form.getEntries().add(new FormData(Constants.TITLE_FIELD,
		new StringRepresentation(title)));	
		form.getEntries().add(new FormData(Constants.DESC_FIELD,
		new StringRepresentation(description)));
		
		Representation result = client.put(form);
		Media media = gson.fromJson(result.getText(), Media.class);

		// verify if resultant media has the passed attributes
		assertEquals(title, media.getTitle());
		assertEquals(description, media.getDescription());
	}

}