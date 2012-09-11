/*
 * Copyright 2012 buddycloud
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.buddycloud.mediaserver.web;

import java.io.File;
import java.io.IOException;

import org.apache.commons.fileupload.FileUploadException;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

import com.buddycloud.mediaserver.business.dao.DAOFactory;
import com.buddycloud.mediaserver.business.dao.MediaDAO;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.Thumbnail;
import com.buddycloud.mediaserver.commons.exception.InvalidPreviewFormatException;
import com.buddycloud.mediaserver.commons.exception.MediaNotFoundException;
import com.buddycloud.mediaserver.commons.exception.MetadataSourceException;
import com.buddycloud.mediaserver.commons.exception.UserNotAllowedException;
import com.buddycloud.mediaserver.web.representation.DynamicFileRepresentation;
import com.buddycloud.mediaserver.xmpp.XMPPToolBox;

/**
 * Resource that represents /<channel>/<mediaId> endpoint.
 *
 * @author Rodrigo Duarte Sousa - rodrigodsousa@gmail.com
 */
public class MediaResource extends MediaServerResource {

	/**
	 * Uploads avatar (PUT /<channel>/avatar) 
	 */
	@Put("application/x-www-form-urlencoded|multipart/form-data")
	public Representation putWebFormAvatar(Representation entity) {
		Request request = getRequest();
		addCORSHeaders(request);

		String auth = getQueryValue(Constants.AUTH_QUERY);

		String userId = null;
		String token = null;
		
		try {
			userId = getUserId(request, auth);
			token = getTransactionId(request, auth);
		} catch (Throwable t) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Error while getting auth params", MediaType.APPLICATION_JSON);
		}

		Representation verifyRequest = verifyRequest(userId, token, request.getResourceRef().getIdentifier());
		if (verifyRequest != null) {
			return verifyRequest;
		}
		
		MediaDAO mediaDAO = DAOFactory.getInstance().getDAO();

		String entityId = (String) request.getAttributes().get(Constants.ENTITY_ARG);
		String mediaId = (String) request.getAttributes().get(Constants.MEDIA_ARG);

		if (!mediaId.equals(Constants.AVATAR_ARG)) {
			setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
			return new StringRepresentation("Valid only for avatars!",	MediaType.APPLICATION_JSON);
		}
		
		try {
			String result = null;
			if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {
				result = mediaDAO.insertFormDataMedia(userId, entityId, getRequest(), true);
			} else {
				result = mediaDAO.insertWebFormMedia(userId, entityId, new Form(entity), true);
			}
			
			setStatus(Status.SUCCESS_CREATED);
			return new StringRepresentation(result, MediaType.APPLICATION_JSON);
		} catch (FileUploadException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
		} catch (UserNotAllowedException e) {
			setStatus(Status.CLIENT_ERROR_FORBIDDEN);
		} catch (Throwable t) {
			return unexpectedError(t);	
		}
		
		return new EmptyRepresentation();
	}

	/**
	 * Deletes media (DELETE /<channel>/<mediaId>) 
	 */
	@Delete
	public Representation deleteMedia() {
		Request request = getRequest();
		addCORSHeaders(request);

		String auth = getQueryValue(Constants.AUTH_QUERY);
		String userId = null;
		String token = null;

		try {
			userId = getUserId(request, auth);
			token = getTransactionId(request, auth);
		} catch (Throwable t) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Error while getting auth params", MediaType.APPLICATION_JSON);
		}

		Representation verifyRequest = verifyRequest(userId, token, request.getResourceRef().getIdentifier());
		if (verifyRequest != null) {
			return verifyRequest;
		}

		String entityId = (String) request.getAttributes().get(Constants.ENTITY_ARG);
		String mediaId = (String) request.getAttributes().get(Constants.MEDIA_ARG);

		MediaDAO mediaDAO = DAOFactory.getInstance().getDAO();

		try {
			mediaDAO.deleteMedia(userId, entityId, mediaId);
			return new StringRepresentation("Media deleted!", MediaType.APPLICATION_JSON);
		} catch (MetadataSourceException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
		} catch (MediaNotFoundException e) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		} catch (UserNotAllowedException e) {
			setStatus(Status.CLIENT_ERROR_FORBIDDEN);
		} catch (Throwable t) {
			return unexpectedError(t);
		}
		
		return new EmptyRepresentation();
	}

	/**
	 * Updates media (POST /<channel>/<mediaId>) 
	 */
	@Post("application/x-www-form-urlencoded")
	public Representation updateMedia(Representation entity) {
		Request request = getRequest();
		addCORSHeaders(request);

		String auth = getQueryValue(Constants.AUTH_QUERY);
		String userId = null;
		String token = null;

		try {
			userId = getUserId(request, auth);
			token = getTransactionId(request, auth);
		} catch (Throwable t) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Error while getting auth params", MediaType.APPLICATION_JSON);
		}

		Representation verifyRequest = verifyRequest(userId, token, request.getResourceRef().getIdentifier());
		if (verifyRequest != null) {
			return verifyRequest;
		}

		String entityId = (String) request.getAttributes().get(Constants.ENTITY_ARG);
		String mediaId = (String) request.getAttributes().get(Constants.MEDIA_ARG);

		MediaDAO mediaDAO = DAOFactory.getInstance().getDAO();
		
		try {
			return new StringRepresentation(mediaDAO.updateMedia(
					userId, entityId, mediaId,  new Form(entity)),
					MediaType.APPLICATION_JSON);
		} catch (MetadataSourceException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
		} catch (MediaNotFoundException e) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		} catch (UserNotAllowedException e) {
			setStatus(Status.CLIENT_ERROR_FORBIDDEN);
		} catch (Throwable t) {
			return unexpectedError(t);
		}
		
		return new EmptyRepresentation();
	}

	/**
	 * Downloads media (GET /<channel>/<mediaId>) 
	 */
	@Get
	public Representation getMedia() {
		addCORSHeaders(null);

		Request request = getRequest();

		String userId = null;
		String token = null;

		String entityId = (String) request.getAttributes().get(Constants.ENTITY_ARG);
		String mediaId = (String) request.getAttributes().get(Constants.MEDIA_ARG);

		boolean isChannelPublic = XMPPToolBox.getInstance().getPubSubClient().isChannelPublic(entityId);

		if (!isChannelPublic && !mediaId.equals(Constants.AVATAR_ARG)) {
			String auth = getQueryValue(Constants.AUTH_QUERY);

			try {
				userId = getUserId(request, auth);
				token = getTransactionId(request, auth);
			} catch (Throwable t) {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return new StringRepresentation("Error while getting auth params", MediaType.APPLICATION_JSON);
			}

			Representation verifyRequest = verifyRequest(userId, token, request.getResourceRef().getIdentifier());
			if (verifyRequest != null) {
				return verifyRequest;
			}
		}

		Integer maxHeight = null;
		Integer maxWidth = null;

		try {
			String queryValue = getQueryValue(Constants.MAX_HEIGHT_QUERY);
			if (queryValue != null) {
				maxHeight = Integer.valueOf(queryValue);
			}

			queryValue = getQueryValue(Constants.MAX_HEIGHT_QUERY);
			if (queryValue != null) {
				maxWidth = Integer.valueOf(queryValue);
			}
		} catch (Throwable t) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Invalid query value!", MediaType.APPLICATION_JSON);
		}

		try {
			MediaDAO mediaDAO = DAOFactory.getInstance().getDAO();

			if (maxHeight == null && maxWidth == null) {
				MediaType mediaType = new MediaType(mediaDAO.getMediaType(entityId, mediaId));

				File media = mediaDAO.getMedia(userId, entityId, mediaId);
				return new FileRepresentation(media, mediaType);
			}

			Thumbnail thumbnail = null;

			if (maxHeight != null && maxWidth == null) {
				thumbnail = mediaDAO.getMediaPreview(userId, entityId, mediaId, maxHeight);
			} else if (maxHeight == null && maxWidth != null) {
				thumbnail = mediaDAO.getMediaPreview(userId, entityId, mediaId, maxWidth);
			} else {
				thumbnail = mediaDAO.getMediaPreview(userId, entityId, mediaId, maxHeight, maxWidth);
			}

			return new DynamicFileRepresentation(new MediaType(thumbnail.getMimeType()), thumbnail.getImg());
		} catch (MetadataSourceException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
		} catch (IOException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
		} catch (MediaNotFoundException e) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		} catch (InvalidPreviewFormatException e) {
			setStatus(Status.CLIENT_ERROR_PRECONDITION_FAILED);
		} catch (UserNotAllowedException e) {
			setStatus(Status.CLIENT_ERROR_FORBIDDEN);
		} catch (Throwable t) {
			return unexpectedError(t);
		}
		
		return new EmptyRepresentation();
	}
}
