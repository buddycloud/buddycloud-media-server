package com.buddycloud.mediaserver.web;

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

public class MediaResourceUploadTest extends MediaResourceTest {
	
	@Test
	public void anonymousSuccessfulUpload() throws Exception {
		Media media = buildValidTestMedia();
		ClientResource client = new ClientResource(URL + media.getId());
		
		FormDataSet form = new FormDataSet();
		form.setMultipart(true);
		form.getEntries().add(new FormData(Constants.BODY_FIELD, 
				new JsonRepresentation(media)));
		form.getEntries().add(new FormData(Constants.FILE_FIELD, 
				new FileRepresentation(TESTFILE_PATH, MediaType.IMAGE_JPEG)));
		
		
		Representation result = client.put(form);
		System.out.println(result.getText());
	}

}
