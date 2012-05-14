package com.buddycloud.mediaserver.web;
import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.apache.commons.fileupload.FileUploadException;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.buddycloud.mediaserver.business.MediaDAO;
import com.buddycloud.mediaserver.commons.Constants;


public class UploadMediaResource extends ServerResource {

	@Post
	public Representation postMedia(Representation entity) {
		String channel = (String) getRequest().getAttributes().get(Constants.CHANNEL_ARG);
		String domain = (String) getRequest().getAttributes().get(Constants.DOMAIN_ARG);

		if (entity != null) {
			if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {
				try {
					return new StringRepresentation(MediaDAO.gestInstance().addFile(domain, channel, getRequest()), 
								MediaType.APPLICATION_JSON);
				} catch (FileNotFoundException e) {
					setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
				} catch (FileUploadException e) {
					setStatus(Status.SERVER_ERROR_INTERNAL);
					return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
				} catch (SQLException e) {
					setStatus(Status.SERVER_ERROR_INTERNAL);
					return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
				}
			}
		}
		
		// POST request with no entity.
		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		return new StringRepresentation("POST request with no entity", MediaType.TEXT_PLAIN);
	}


}
