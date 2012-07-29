package com.buddycloud.mediaserver.web;
import org.apache.commons.fileupload.FileUploadException;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import com.buddycloud.mediaserver.business.dao.DAOFactory;
import com.buddycloud.mediaserver.business.dao.MediaDAO;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.exception.FormFieldException;
import com.buddycloud.mediaserver.commons.exception.MetadataSourceException;
import com.buddycloud.mediaserver.commons.exception.UserNotAllowedException;
import com.buddycloud.mediaserver.xmpp.XMPPToolBox;


public class MediasResource extends MediaServerResource {
	
	@Post
	public Representation postMedia(Representation entity) {
		String auth = getQueryValue(Constants.AUTH_QUERY);
		Request request = getRequest();
		
		String userId = null;
		String token = null;
		
		try {
			userId = getUserId(request, auth);
			token = getTransactionId(request, auth);
		} catch (Throwable t) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation(t.getLocalizedMessage(), MediaType.APPLICATION_JSON);
		}

		if (userId == null || token == null) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return authenticationResponse();
		}
		
		if (!verifyRequest(userId, token,request.getResourceRef().getIdentifier())) {
			setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			return new StringRepresentation("User '" + userId + "' not allowed to access resource", MediaType.APPLICATION_JSON);
		}
		
		String entityId = (String) request.getAttributes().get(Constants.ENTITY_ARG);

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
		Request request = getRequest();

		String userId = null;
		String token = null;
		
		String entityId = (String) request.getAttributes().get(Constants.ENTITY_ARG);

		boolean isChannelPublic = XMPPToolBox.getInstance().getPubSubClient().isChannelPublic(entityId); 
		
		if (!isChannelPublic) {
			String auth = getQueryValue(Constants.AUTH_QUERY);

			try {
				userId = getUserId(request, auth);
				token = getTransactionId(request, auth);
			} catch (Throwable t) {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return new StringRepresentation(t.getLocalizedMessage(), MediaType.APPLICATION_JSON);
			}
	
			if (userId == null || token == null) {
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
				return authenticationResponse();
			}
	
			if (!verifyRequest(userId, token,request.getResourceRef().getIdentifier())) {
				setStatus(Status.CLIENT_ERROR_FORBIDDEN);
				return new StringRepresentation("User '" + userId + "' not allowed to access resource", MediaType.APPLICATION_JSON);
			}
		}
		
		DateTime since = null;

		try {
			String queryValue = getQueryValue(Constants.SINCE_QUERY);

			if (queryValue != null) {
				since = ISODateTimeFormat.dateTime().parseDateTime(queryValue);
			}
		} catch (Throwable t) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation(t.getLocalizedMessage(), MediaType.APPLICATION_JSON);
		}
		
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
