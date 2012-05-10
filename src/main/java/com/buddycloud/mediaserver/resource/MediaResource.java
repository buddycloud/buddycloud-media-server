package com.buddycloud.mediaserver.resource;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;


public class MediaResource extends ServerResource {
	
	private static final String CHANNEL_ARG = "channel";
	private static final String ID_ARG = "id";

	
	@Post
	public Representation postMedia(Representation entity) {
		String channel = (String) getRequest().getAttributes().get(CHANNEL_ARG);
		String id = (String) getRequest().getAttributes().get(ID_ARG);

		return new StringRepresentation("POST: " + channel + "/" + id);
	}
	
	@Get
	public Representation getMedia(Representation entity) {
		String channel = (String) getRequest().getAttributes().get(CHANNEL_ARG);
		String id = (String) getRequest().getAttributes().get(ID_ARG);

		return new StringRepresentation("GET: " + channel + "/" + id);
	}
}
