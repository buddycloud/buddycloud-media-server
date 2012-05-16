package com.buddycloud.mediaserver.web;

import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.ext.html.FormData;
import org.restlet.ext.html.FormDataSet;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import com.buddycloud.mediaserver.commons.Constants;

public class UploadMediaResourceTest {
	
	private static final String URL = "http://localhost:8080/media/testdomain/testchannel";
	private static final String TESTFILE = "resources/tests/testimage.jpg";
	
	
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
				new StringRepresentation("testfile")));
		form.getEntries().add(new FormData(Constants.FILE_FIELD, 
				new FileRepresentation(TESTFILE, MediaType.APPLICATION_ALL)));
		
		Representation result = client.post(form);
	}

}
