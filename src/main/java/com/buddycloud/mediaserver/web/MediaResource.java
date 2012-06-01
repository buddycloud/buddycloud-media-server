package com.buddycloud.mediaserver.web;
import java.io.File;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.buddycloud.mediaserver.business.MediaDAO;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.exception.MediaMetadataSourceException;
import com.buddycloud.mediaserver.commons.exception.MediaNotFoundException;


public class MediaResource extends ServerResource {
	
	@Delete
	public Representation deleteMedia() {
		//TODO
		return null;
	}
	
	
	@Put
	public Representation putMedia() {
		//TODO
		return null;
	}

	@Get
	public Representation getMedia() {
		String entityId = (String) getRequest().getAttributes().get(Constants.ENTITY_ARG);
		String mediaId = (String) getRequest().getAttributes().get(Constants.MEDIA_ARG);
		
		String maxHeight = getQueryValue(Constants.MAX_HEIGHT_QUERY);
		String maxWidth = getQueryValue(Constants.MAX_WIDTH_QUERY);

		// both or none arguments must be present
		if (maxHeight == null || maxWidth == null) {
			maxHeight = maxWidth = null;
		}

		try {
			File media = MediaDAO.gestInstance().getMedia(entityId, mediaId, maxHeight, maxWidth);
			return new FileRepresentation(media, new MediaType(MediaDAO.gestInstance().getMediaType(mediaId)));
		} catch (MediaMetadataSourceException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
		} catch (MediaNotFoundException e) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
		}
	}
}
