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

import com.buddycloud.mediaserver.business.dao.DAOFactory;
import com.buddycloud.mediaserver.business.dao.MediaDAO;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.MediaFile;
import com.buddycloud.mediaserver.commons.exception.InvalidPreviewFormatException;
import com.buddycloud.mediaserver.commons.exception.MediaNotFoundException;
import com.buddycloud.mediaserver.commons.exception.MetadataSourceException;
import com.buddycloud.mediaserver.commons.exception.UserNotAllowedException;
import com.buddycloud.mediaserver.web.representation.DynamicFileRepresentation;
import com.buddycloud.mediaserver.xmpp.XMPPToolBox;
import org.apache.commons.fileupload.FileUploadException;
import org.restlet.Request;
import org.restlet.data.CacheDirective;
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

import java.io.File;
import java.io.IOException;
import java.util.List;

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
	public Representation putAvatar(Representation entity) {
		setServerHeader();
		
		Request request = getRequest();
//		The HTTP API sets the headers 
//		addCORSHeaders(request);

		String auth = getQueryValue(Constants.AUTH_QUERY);

		String userJID = null;
		String token = null;
		
		try {
			userJID = getUserId(request, auth);
			token = getTransactionId(request, auth);
		} catch (Throwable t) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Error while getting auth params", MediaType.APPLICATION_JSON);
		}

		Representation verifyRequest = checkRequest(userJID, token, request.getResourceRef().getIdentifier());
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
		
		String result = "";
		try {
			if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {
				result = mediaDAO.insertFormDataMedia(userJID, entityId, getRequest(), true);
			} else {
				result = mediaDAO.insertWebFormMedia(userJID, entityId, new Form(entity), true);
			}
			
			setStatus(Status.SUCCESS_CREATED);
		} catch (FileUploadException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (UserNotAllowedException e) {
			setStatus(Status.CLIENT_ERROR_FORBIDDEN);
		} catch (Throwable t) {
			return unexpectedError(t);	
		}
		
		return new StringRepresentation(result, MediaType.APPLICATION_JSON);
	}

	/**
	 * Deletes media (DELETE /<channel>/<mediaId>) 
	 */
	@Delete
	public Representation deleteMedia() {
		setServerHeader();
		
		Request request = getRequest();
//		The HTTP API sets the headers 
//		addCORSHeaders(request);

		String auth = getQueryValue(Constants.AUTH_QUERY);
		String userJID = null;
		String token = null;

		try {
			userJID = getUserId(request, auth);
			token = getTransactionId(request, auth);
		} catch (Throwable t) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Error while getting auth params", MediaType.APPLICATION_JSON);
		}

		Representation verifyRequest = checkRequest(userJID, token, request.getResourceRef().getIdentifier());
		if (verifyRequest != null) {
			return verifyRequest;
		}

		String entityId = (String) request.getAttributes().get(Constants.ENTITY_ARG);
		String mediaId = (String) request.getAttributes().get(Constants.MEDIA_ARG);

		MediaDAO mediaDAO = DAOFactory.getInstance().getDAO();

		try {
			mediaDAO.deleteMedia(userJID, entityId, mediaId);
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
		setServerHeader();
		Request request = getRequest();
//		The HTTP API sets the headers 
//		addCORSHeaders(request);

		String auth = getQueryValue(Constants.AUTH_QUERY);
		String userJID = null;
		String token = null;

		try {
			userJID = getUserId(request, auth);
			token = getTransactionId(request, auth);
		} catch (Throwable t) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Error while getting auth params", MediaType.APPLICATION_JSON);
		}

		Representation verifyRequest = checkRequest(userJID, token, request.getResourceRef().getIdentifier());
		if (verifyRequest != null) {
			return verifyRequest;
		}

		String entityId = (String) request.getAttributes().get(Constants.ENTITY_ARG);
		String mediaId = (String) request.getAttributes().get(Constants.MEDIA_ARG);

		MediaDAO mediaDAO = DAOFactory.getInstance().getDAO();
		
		try {
			return new StringRepresentation(mediaDAO.updateMedia(
					userJID, entityId, mediaId,  new Form(entity)),
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
		setServerHeader();
//		The HTTP API sets the headers 
//		addCORSHeaders();

		Request request = getRequest();

		String userJID = null;
		String token;

		String entityId = (String) request.getAttributes().get(Constants.ENTITY_ARG);
		String mediaId = (String) request.getAttributes().get(Constants.MEDIA_ARG);

        boolean isChannelPublic = true;
		if (!mediaId.equals(Constants.AVATAR_ARG)) {
            isChannelPublic = XMPPToolBox.getInstance().getPubSubClient().isChannelPublic(entityId);

            if (!isChannelPublic) {
                String auth = getQueryValue(Constants.AUTH_QUERY);

                try {
                    userJID = getUserId(request, auth);
                    token = getTransactionId(request, auth);
                } catch (Throwable t) {
                    setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                    return new StringRepresentation("Error while getting auth params", MediaType.APPLICATION_JSON);
                }

                Representation verifyRequest = checkRequest(userJID, token, request.getResourceRef().getIdentifier());
                if (verifyRequest != null) {
                    return verifyRequest;
                }
            }
		}

		Integer maxHeight = null;
		Integer maxWidth = null;

		try {
			String queryValue = getQueryValue(Constants.MAX_HEIGHT_QUERY);
			if (queryValue != null) {
				maxHeight = Integer.valueOf(queryValue);
			}

			queryValue = getQueryValue(Constants.MAX_WIDTH_QUERY);
			if (queryValue != null) {
				maxWidth = Integer.valueOf(queryValue);
			}
		} catch (Throwable t) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Invalid query value!", MediaType.APPLICATION_JSON);
		}

		try {
            // Cache headers
            addCacheHeaders(isChannelPublic);

			MediaDAO mediaDAO = DAOFactory.getInstance().getDAO();

			if (maxHeight == null && maxWidth == null) {
				MediaType mediaType = new MediaType(mediaDAO.getMediaType(entityId, mediaId));

                MediaFile<File> mediaFile = mediaDAO.getMedia(userJID, entityId, mediaId);
                FileRepresentation fileRepresentation = new FileRepresentation(mediaFile.getMediaFile(), mediaType);
                fileRepresentation.setModificationDate(mediaFile.getLastModified());
                return fileRepresentation;
			}

			MediaFile<byte[]> thumbnail;

			if (maxHeight != null && maxWidth == null) {
				thumbnail = mediaDAO.getMediaPreview(userJID, entityId, mediaId, maxHeight);
			} else if (maxHeight == null && maxWidth != null) {
				thumbnail = mediaDAO.getMediaPreview(userJID, entityId, mediaId, maxWidth);
			} else {
				thumbnail = mediaDAO.getMediaPreview(userJID, entityId, mediaId, maxHeight, maxWidth);
			}

            DynamicFileRepresentation dynamicFileRepresentation = new DynamicFileRepresentation(
                    new MediaType(thumbnail.getMimeType()), thumbnail.getMediaFile());
            dynamicFileRepresentation.setModificationDate(thumbnail.getLastModified());
            return dynamicFileRepresentation;
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

	private void addCacheHeaders(boolean publicInfo) {
		List<CacheDirective> cacheDirectives = getResponse().getCacheDirectives();
		// Clear old directives
		cacheDirectives.clear();

		// Add max-age and public/private
//		cacheDirectives.add(CacheDirective.maxAge(maxAge));
		cacheDirectives.add(publicInfo ? CacheDirective.publicInfo() : CacheDirective.privateInfo());
	}
}
