package com.buddycloud.mediaserver.web;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.ext.html.FormData;
import org.restlet.ext.html.FormDataSet;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import com.buddycloud.mediaserver.commons.Constants;

public class MediaResourceUploadTest {
	
	private static final String URL = "http://localhost:8080/media/channel@domain/" + UUID.randomUUID();
	private static final String TESTFILE_PATH = "resources/tests/testimage.jpg";
	private static final String TESTFILE_NAME = "testimage.jpg";
	
	
	@Before
	public void setUp() throws Exception {
		Component component = new Component();  
	    component.getServers().add(Protocol.HTTP, 8080);  
	    
	    Context context = component.getContext().createChildContext();
		component.getDefaultHost().attach("/media", new MediaServerApplication(context));
		
	    component.start();  
	}
	
	@Test
	public void anonymousSuccessfulUpload() {
		ClientResource client = new ClientResource(URL);
		
		FormDataSet form = new FormDataSet();
		form.setMultipart(true);
		form.getEntries().add(new FormData(Constants.NAME_FIELD, 
				new StringRepresentation(TESTFILE_NAME)));
		form.getEntries().add(new FormData(Constants.FILE_FIELD, 
				new FileRepresentation(TESTFILE_PATH, MediaType.IMAGE_JPEG)));
		
		client.put(form);
	}

}
