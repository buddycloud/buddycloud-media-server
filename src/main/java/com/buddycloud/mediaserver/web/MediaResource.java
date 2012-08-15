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
import org.restlet.data.MediaType;
import org.restlet.data.Status;
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
import com.buddycloud.mediaserver.commons.exception.FormFieldException;
import com.buddycloud.mediaserver.commons.exception.InvalidPreviewFormatException;
import com.buddycloud.mediaserver.commons.exception.MediaNotFoundException;
import com.buddycloud.mediaserver.commons.exception.MetadataSourceException;
import com.buddycloud.mediaserver.commons.exception.UserNotAllowedException;
import com.buddycloud.mediaserver.web.representation.DynamicFileRepresentation;
import com.buddycloud.mediaserver.xmpp.XMPPToolBox;

public class MediaResource extends MediaServerResource {

	@Put
	public Representation putAvatar(Representation entity) {
		addCORSHeader();
		
		String auth = getQueryValue(Constants.AUTH_QUERY);
		Request request = getRequest();

		String userId = null;
		String token = null;

		try {
			userId = getUserId(request, auth);
			token = getTransactionId(request, auth);
		} catch (Throwable t) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);

			return new StringRepresentation(t.getLocalizedMessage(),
					MediaType.APPLICATION_JSON);
		}

		if (userId == null || token == null) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return authenticationResponse();
		}

		if (!verifyRequest(userId, token, request.getResourceRef()
				.getIdentifier())) {
			setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			return new StringRepresentation("User '" + userId
					+ "' not allowed to access resource",
					MediaType.APPLICATION_JSON);
		}

		if (entity != null) {
			if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),
					true)) {
				String entityId = (String) request.getAttributes().get(
						Constants.ENTITY_ARG);
				String mediaId = (String) request.getAttributes().get(
						Constants.MEDIA_ARG);

				if (!mediaId.equals(Constants.AVATAR_ARG)) {
					setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
				}

				MediaDAO mediaDAO = DAOFactory.getInstance().getDAO();

				try {
					return new StringRepresentation(mediaDAO.insertMedia(
							userId, entityId, getRequest(), true),
							MediaType.APPLICATION_JSON);
				} catch (FileUploadException e) {
					setStatus(Status.SERVER_ERROR_INTERNAL);
					return new StringRepresentation(e.getMessage(),
							MediaType.APPLICATION_JSON);
				} catch (MetadataSourceException e) {
					setStatus(Status.SERVER_ERROR_INTERNAL);
					return new StringRepresentation(e.getMessage(),
							MediaType.APPLICATION_JSON);
				} catch (FormFieldException e) {
					setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					return new StringRepresentation(e.getMessage(),
							MediaType.APPLICATION_JSON);
				} catch (UserNotAllowedException e) {
					setStatus(Status.CLIENT_ERROR_FORBIDDEN);
					return new StringRepresentation(e.getMessage(),
							MediaType.APPLICATION_JSON);
				}
			}
		}

		// POST request with no entity.
		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		return new StringRepresentation("POST request with no entity",
				MediaType.APPLICATION_JSON);
	}

	@Delete
	public Representation deleteMedia() {
		addCORSHeader();
		
		String auth = getQueryValue(Constants.AUTH_QUERY);
		Request request = getRequest();

		String userId = null;
		String token = null;

		try {
			userId = getUserId(request, auth);
			token = getTransactionId(request, auth);
		} catch (Throwable t) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation(t.getLocalizedMessage(),
					MediaType.APPLICATION_JSON);
		}

		if (userId == null || token == null) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return authenticationResponse();
		}

		if (!verifyRequest(userId, token, request.getResourceRef()
				.getIdentifier())) {
			setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			return new StringRepresentation("User '" + userId
					+ "' not allowed to access resource",
					MediaType.APPLICATION_JSON);
		}

		String entityId = (String) request.getAttributes().get(
				Constants.ENTITY_ARG);
		String mediaId = (String) request.getAttributes().get(
				Constants.MEDIA_ARG);

		MediaDAO mediaDAO = DAOFactory.getInstance().getDAO();

		try {
			mediaDAO.deleteMedia(userId, entityId, mediaId);
			return new StringRepresentation("Media deleted",
					MediaType.APPLICATION_JSON);
		} catch (MetadataSourceException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage(),
					MediaType.APPLICATION_JSON);
		} catch (MediaNotFoundException e) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return new StringRepresentation(e.getMessage(),
					MediaType.APPLICATION_JSON);
		} catch (UserNotAllowedException e) {
			setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			return new StringRepresentation(e.getMessage(),
					MediaType.APPLICATION_JSON);
		}
	}

	@Post
	public Representation updateMedia(Representation entity) {
		addCORSHeader();
		
		String auth = getQueryValue(Constants.AUTH_QUERY);
		Request request = getRequest();

		String userId = null;
		String token = null;

		try {
			userId = getUserId(request, auth);
			token = getTransactionId(request, auth);
		} catch (Throwable t) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation(t.getLocalizedMessage(),
					MediaType.APPLICATION_JSON);
		}

		if (userId == null || token == null) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return authenticationResponse();
		}

		if (!verifyRequest(userId, token, request.getResourceRef()
				.getIdentifier())) {
			setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			return new StringRepresentation("User '" + userId
					+ "' not allowed to access resource",
					MediaType.APPLICATION_JSON);
		}

		String entityId = (String) request.getAttributes().get(
				Constants.ENTITY_ARG);
		String mediaId = (String) request.getAttributes().get(
				Constants.MEDIA_ARG);

		if (entity != null) {
			if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),
					true)) {

				MediaDAO mediaDAO = DAOFactory.getInstance().getDAO();

				try {
					return new StringRepresentation(mediaDAO.updateMedia(
							userId, entityId, mediaId, getRequest()),
							MediaType.APPLICATION_JSON);
				} catch (FileUploadException e) {
					setStatus(Status.SERVER_ERROR_INTERNAL);
					return new StringRepresentation(e.getMessage(),
							MediaType.APPLICATION_JSON);
				} catch (MetadataSourceException e) {
					setStatus(Status.SERVER_ERROR_INTERNAL);
					return new StringRepresentation(e.getMessage(),
							MediaType.APPLICATION_JSON);
				} catch (FormFieldException e) {
					setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					return new StringRepresentation(e.getMessage(),
							MediaType.APPLICATION_JSON);
				} catch (MediaNotFoundException e) {
					setStatus(Status.CLIENT_ERROR_NOT_FOUND);
					return new StringRepresentation(e.getMessage(),
							MediaType.APPLICATION_JSON);
				} catch (UserNotAllowedException e) {
					setStatus(Status.CLIENT_ERROR_FORBIDDEN);
					return new StringRepresentation(e.getMessage(),
							MediaType.APPLICATION_JSON);
				}
			}
		}

		// POST request with no entity.
		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		return new StringRepresentation("POST request with no entity",
				MediaType.APPLICATION_JSON);
	}

	@Get
	public Representation getMedia() {
		addCORSHeader();
		
		Request request = getRequest();

		String userId = null;
		String token = null;

		String entityId = (String) request.getAttributes().get(
				Constants.ENTITY_ARG);
		String mediaId = (String) request.getAttributes().get(
				Constants.MEDIA_ARG);

		boolean isChannelPublic = XMPPToolBox.getInstance().getPubSubClient()
				.isChannelPublic(entityId);

		if (!isChannelPublic) {
			String auth = getQueryValue(Constants.AUTH_QUERY);

			try {
				userId = getUserId(request, auth);
				token = getTransactionId(request, auth);
			} catch (Throwable t) {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return new StringRepresentation(t.getLocalizedMessage(),
						MediaType.APPLICATION_JSON);
			}

			if (userId == null || token == null) {
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
				return authenticationResponse();
			}

			if (!verifyRequest(userId, token, request.getResourceRef()
					.getIdentifier())) {
				setStatus(Status.CLIENT_ERROR_FORBIDDEN);
				return new StringRepresentation("User '" + userId
						+ "' not allowed to access resource",
						MediaType.APPLICATION_JSON);
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
			return new StringRepresentation(t.getLocalizedMessage(),
					MediaType.APPLICATION_JSON);
		}

		MediaDAO mediaDAO = DAOFactory.getInstance().getDAO();

		try {
			MediaType mediaType = new MediaType(mediaDAO.getMediaType(entityId,
					mediaId));

			if (maxHeight == null && maxWidth == null) {
				File media = mediaDAO.getMedia(userId, entityId, mediaId);
				return new FileRepresentation(media, mediaType);
			}

			byte[] preview = null;

			if (maxHeight != null && maxWidth == null) {
				preview = mediaDAO.getMediaPreview(userId, entityId, mediaId,
						maxHeight);
			} else if (maxHeight == null && maxWidth != null) {
				preview = mediaDAO.getMediaPreview(userId, entityId, mediaId,
						maxWidth);
			} else {
				preview = mediaDAO.getMediaPreview(userId, entityId, mediaId,
						maxHeight, maxWidth);
			}

			return new DynamicFileRepresentation(mediaType, preview);
		} catch (MetadataSourceException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage(),
					MediaType.APPLICATION_JSON);
		} catch (IOException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage(),
					MediaType.APPLICATION_JSON);
		} catch (MediaNotFoundException e) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return new StringRepresentation(e.getMessage(),
					MediaType.APPLICATION_JSON);
		} catch (InvalidPreviewFormatException e) {
			setStatus(Status.CLIENT_ERROR_PRECONDITION_FAILED);
			return new StringRepresentation(e.getMessage(),
					MediaType.APPLICATION_JSON);
		} catch (UserNotAllowedException e) {
			setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			return new StringRepresentation(e.getMessage(),
					MediaType.APPLICATION_JSON);
		}
	}
}
