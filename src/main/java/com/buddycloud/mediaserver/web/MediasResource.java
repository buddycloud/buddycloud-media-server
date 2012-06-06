package com.buddycloud.mediaserver.web;
import org.apache.commons.fileupload.FileUploadException;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.buddycloud.mediaserver.business.dao.DAOFactory;
import com.buddycloud.mediaserver.business.dao.MediasDAO;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.exception.FormMissingFieldException;
import com.buddycloud.mediaserver.commons.exception.MediaMetadataSourceException;


public class MediasResource extends ServerResource {

	@Post
	public Representation postMedia(Representation entity) {
		String entityId = (String) getRequest().getAttributes().get(Constants.ENTITY_ARG);

		if (entity != null) {
			if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {
				
				MediasDAO mediaDAO = DAOFactory.getInstance().getMediaDAO();
				
				try {
					return new StringRepresentation(mediaDAO.insertMedia(entityId, getRequest()), 
									MediaType.APPLICATION_JSON);
				} catch (FileUploadException e) {
					setStatus(Status.SERVER_ERROR_INTERNAL);
					return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
				} catch (MediaMetadataSourceException e) {
					setStatus(Status.SERVER_ERROR_INTERNAL);
					return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
				} catch (FormMissingFieldException e) {
					setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
				}
			}
		}
		
		// POST request with no entity.
		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		return new StringRepresentation("POST request with no entity", MediaType.TEXT_PLAIN);
	}
}
