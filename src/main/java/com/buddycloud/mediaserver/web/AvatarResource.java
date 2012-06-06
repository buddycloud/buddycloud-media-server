package com.buddycloud.mediaserver.web;
import java.io.File;
import java.io.IOException;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.buddycloud.mediaserver.business.dao.AvatarsDAO;
import com.buddycloud.mediaserver.business.dao.DAOFactory;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.exception.InvalidPreviewFormatException;
import com.buddycloud.mediaserver.commons.exception.MetadataSourceException;
import com.buddycloud.mediaserver.commons.exception.MediaNotFoundException;
import com.buddycloud.mediaserver.web.representation.DynamicFileRepresentation;


public class AvatarResource extends ServerResource {
	
	@Delete
	public Representation postAvatar() {
		String entityId = (String) getRequest().getAttributes().get(Constants.ENTITY_ARG);

		return new StringRepresentation("POST /media/avatar/" + entityId);
	}

	@Delete
	public Representation deleteAvatar() {
		String entityId = (String) getRequest().getAttributes().get(Constants.ENTITY_ARG);

		return new StringRepresentation("DELETE /media/avatar/" + entityId);
	}
	
	@Put
	public Representation putAvatar() {
		String entityId = (String) getRequest().getAttributes().get(Constants.ENTITY_ARG);

		return new StringRepresentation("PUT /media/avatar/" + entityId);
	}
	
	@Get
	public Representation getAvatar() {
		String entityId = (String) getRequest().getAttributes().get(Constants.ENTITY_ARG);
		
		String maxHeight = getQueryValue(Constants.MAX_HEIGHT_QUERY);
		String maxWidth = getQueryValue(Constants.MAX_WIDTH_QUERY);
		
		AvatarsDAO avatarDAO = DAOFactory.getInstance().getAvatarDAO();

		try {
			final MediaType mediaType = new MediaType(avatarDAO.getAvatarMediaType(entityId));

			if (maxHeight == null || maxWidth == null) {
				File media = avatarDAO.getAvatar(entityId);
				return new FileRepresentation(media, mediaType);
			}
				
			byte[] media = avatarDAO.getAvatarPreview(entityId, Integer.valueOf(maxHeight), Integer.valueOf(maxWidth));
			return new DynamicFileRepresentation(mediaType, media);
		} catch (MetadataSourceException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
		} catch (IOException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);	
		} catch (MediaNotFoundException e) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
		} catch (InvalidPreviewFormatException e) {
			setStatus(Status.CLIENT_ERROR_PRECONDITION_FAILED);
			return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
		}
	}
}
