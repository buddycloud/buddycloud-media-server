package com.buddycloud.mediaserver.web;
import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.apache.commons.fileupload.FileUploadException;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.buddycloud.mediaserver.business.MediaDAO;
import com.buddycloud.mediaserver.commons.Constants;


public class MediaResource extends ServerResource {

	@Put
	public Representation postMedia(Representation entity) {
		String channel = (String) getRequest().getAttributes().get(Constants.CHANNEL_ARG);
		String mediaId = (String) getRequest().getAttributes().get(Constants.MEDIA_ARG);

		if (entity != null) {
			if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {
				try {
					return new StringRepresentation(MediaDAO.gestInstance().addFile(channel, mediaId, getRequest()), 
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
		
		// PUT request with no entity.
		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		return new StringRepresentation("PUT request with no entity", MediaType.TEXT_PLAIN);
	}
	
	@Get
	public Representation getMedia(Representation entity) {
		String channel = (String) getRequest().getAttributes().get(Constants.CHANNEL_ARG);
		String mediaId = (String) getRequest().getAttributes().get(Constants.MEDIA_ARG);

		return new StringRepresentation("GET: " + channel + "/" + mediaId);
	}


}
