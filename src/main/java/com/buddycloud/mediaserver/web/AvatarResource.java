package com.buddycloud.mediaserver.web;
import java.io.File;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.buddycloud.mediaserver.business.MediaDAO;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.exception.MediaMetadataSourceException;
import com.buddycloud.mediaserver.commons.exception.MediaNotFoundException;


public class AvatarResource extends ServerResource {
	
	@Put
	public Representation putAvatar() {
		String entityId = (String) getRequest().getAttributes().get(Constants.ENTITY_ARG);
		String mediaId = (String) getRequest().getAttributes().get(Constants.ENTITY_ARG);

		return new StringRepresentation("PUT /media/avatar/" + entityId + "/" + mediaId);
	}

	@Get
	public Representation getAvatar() {
		String entityId = (String) getRequest().getAttributes().get(Constants.ENTITY_ARG);
		String mediaId = (String) getRequest().getAttributes().get(Constants.MEDIA_ARG);

		try {
			File media = MediaDAO.gestInstance().getAvatar(entityId, mediaId);
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
