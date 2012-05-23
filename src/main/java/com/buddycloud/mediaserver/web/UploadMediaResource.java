package com.buddycloud.mediaserver.web;
import org.apache.commons.fileupload.FileUploadException;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.buddycloud.mediaserver.business.MediaDAO;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.exception.FormMissingFieldException;
import com.buddycloud.mediaserver.commons.exception.NotMatchingChecksumException;
import com.buddycloud.mediaserver.commons.exception.MediaMetadataSourceException;


public class UploadMediaResource extends ServerResource {

	@Post
	public Representation putMedia(Representation entity) {
		String channel = (String) getRequest().getAttributes().get(Constants.CHANNEL_ARG);

		if (entity != null) {
			if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {
				
				try {
					return new StringRepresentation(MediaDAO.gestInstance().insertMedia(channel, getRequest()), 
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
				} catch (NotMatchingChecksumException e) {
					setStatus(Status.CLIENT_ERROR_PRECONDITION_FAILED);
					return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
				}
			}
		}
		
		// POST request with no entity.
		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		return new StringRepresentation("POST request with no entity", MediaType.TEXT_PLAIN);
	}
}
