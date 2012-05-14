package com.buddycloud.mediaserver.web;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.buddycloud.mediaserver.commons.Constants;


public class DownloadMediaResource extends ServerResource {

	@Get
	public Representation getMedia(Representation entity) {
		String channel = (String) getRequest().getAttributes().get(Constants.CHANNEL_ARG);
		String id = (String) getRequest().getAttributes().get(Constants.ID_ARG);

		return new StringRepresentation("GET: " + channel + "/" + id);
	}
}
