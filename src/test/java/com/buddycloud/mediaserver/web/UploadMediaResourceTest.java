package com.buddycloud.mediaserver.web;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.ext.html.FormData;
import org.restlet.ext.html.FormDataSet;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.Constants;

public class UploadMediaResourceTest extends MediaResourceTest {
	
	@After
	public void tearDown() throws Exception {
		FileUtils.cleanDirectory(new File(configuration.getProperty(Constants.MEDIA_STORAGE_ROOT_PROPERTY) + 
				File.separator + BASE_CHANNEL));
		
		dataSource.deleteMedia(TESTFILE_ID);
	}
	
	@Override
	protected void testSetUp() throws Exception {}
	
	@Test
	public void anonymousSuccessfulUpload() throws Exception {
		Media media = buildValidTestMedia();
		ClientResource client = new ClientResource(BASE_URL + Constants.POST_MEDIA_URL);
		
		FormDataSet form = new FormDataSet();
		form.setMultipart(true);
		form.getEntries().add(new FormData(Constants.BODY_FIELD, 
				new JsonRepresentation(media)));
		form.getEntries().add(new FormData(Constants.FILE_FIELD, 
				new FileRepresentation(TESTFILE_PATH, MediaType.IMAGE_JPEG)));
		
		Representation result = client.post(form);
		System.out.println(result.getText());
	}

}
