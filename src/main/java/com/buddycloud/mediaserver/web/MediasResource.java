package com.buddycloud.mediaserver.web;
import org.apache.commons.fileupload.FileUploadException;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.buddycloud.mediaserver.business.dao.DAOFactory;
import com.buddycloud.mediaserver.business.dao.MediaDAO;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.exception.FormFieldException;
import com.buddycloud.mediaserver.commons.exception.MetadataSourceException;
import com.buddycloud.mediaserver.commons.exception.UserNotAllowedException;


public class MediasResource extends ServerResource {
	
	@Post
	public Representation postMedia(Representation entity) {
		String userId = getChallengeResponse().getIdentifier();
		String entityId = getAttribute(Constants.ENTITY_ARG);

		if (entity != null) {
			if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {

				MediaDAO mediaDAO = DAOFactory.getInstance().getDAO();

				try {
					return new StringRepresentation(mediaDAO.insertMedia(userId, entityId, getRequest(), false), 
							MediaType.APPLICATION_JSON);
				} catch (FileUploadException e) {
					setStatus(Status.SERVER_ERROR_INTERNAL);
					return new StringRepresentation(e.getMessage(), MediaType.APPLICATION_JSON);
				} catch (MetadataSourceException e) {
					setStatus(Status.SERVER_ERROR_INTERNAL);
					return new StringRepresentation(e.getMessage(), MediaType.APPLICATION_JSON);
				} catch (FormFieldException e) {
					setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					return new StringRepresentation(e.getMessage(), MediaType.APPLICATION_JSON);
				} catch (UserNotAllowedException e) {
					setStatus(Status.CLIENT_ERROR_FORBIDDEN);
					return new StringRepresentation(e.getMessage(), MediaType.APPLICATION_JSON);
				}
			}
		}

		// POST request with no entity.
		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		return new StringRepresentation("POST request with no entity", MediaType.APPLICATION_JSON);
	}
	
	@Get
	public Representation getMediasInfo() {
		String userId = getChallengeResponse().getIdentifier();
		String entityId = getAttribute(Constants.ENTITY_ARG);
		
		String since = getQueryValue(Constants.MAX_HEIGHT_QUERY);

		MediaDAO mediaDAO = DAOFactory.getInstance().getDAO();

		try {
			return new StringRepresentation(mediaDAO.getMediasInfo(userId, entityId, since), MediaType.APPLICATION_JSON);
		} catch (MetadataSourceException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage(), MediaType.APPLICATION_JSON);
		} catch (UserNotAllowedException e) {
			setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			return new StringRepresentation(e.getMessage(), MediaType.APPLICATION_JSON);
		}
	}
}
