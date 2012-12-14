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

import org.apache.commons.fileupload.FileUploadException;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import com.buddycloud.mediaserver.business.dao.DAOFactory;
import com.buddycloud.mediaserver.business.dao.MediaDAO;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.exception.MetadataSourceException;
import com.buddycloud.mediaserver.commons.exception.UserNotAllowedException;
import com.buddycloud.mediaserver.xmpp.XMPPToolBox;

/**
 * Resource that represents /<channel> endpoint.
 *
 * @author Rodrigo Duarte Sousa - rodrigodsousa@gmail.com
 */
public class ChannelResource extends MediaServerResource {

	@Post("application/x-www-form-urlencoded|multipart/form-data")
	public Representation postWebFormMedia(Representation entity) {
		setServerHeader();
		
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
			return new StringRepresentation(t.getLocalizedMessage(), MediaType.APPLICATION_JSON);
		}

		Representation verifyRequest = verifyRequest(userId, token, request.getResourceRef().getIdentifier());
		if (verifyRequest != null) {
			return verifyRequest;
		}

		MediaDAO mediaDAO = DAOFactory.getInstance().getDAO();

		String entityId = (String) request.getAttributes().get(Constants.ENTITY_ARG);

		String result = "";
		try {
			if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {
				result = mediaDAO.insertFormDataMedia(userId, entityId, getRequest(), false);
			} else {
				result = mediaDAO.insertWebFormMedia(userId, entityId, new Form(entity), false);
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
	 * Gets media's information list (GET /<channel>) 
	 */
	@Get
	public Representation getMediasInfo() {
		setServerHeader();
		addCORSHeaders();

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
				return new StringRepresentation("Error while getting auth params", MediaType.APPLICATION_JSON);
			}

			Representation verifyRequest = verifyRequest(userId, token, request.getResourceRef().getIdentifier());
			if (verifyRequest != null) {
				return verifyRequest;
			}
		}

		Integer max = null;
		String after = null;

		try {
			String queryValue = getQueryValue(Constants.MAX_QUERY);
			if (queryValue != null) {
				max = Integer.valueOf(queryValue);
			}

			after = getQueryValue(Constants.AFTER_QUERY);
		} catch (Throwable t) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Invalid query value!", MediaType.APPLICATION_JSON);
		}

		MediaDAO mediaDAO = DAOFactory.getInstance().getDAO();

		try {
			return new StringRepresentation(mediaDAO.getMediasInfo(userId,
					entityId, max, after), MediaType.APPLICATION_JSON);
		} catch (MetadataSourceException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
		} catch (UserNotAllowedException e) {
			setStatus(Status.CLIENT_ERROR_FORBIDDEN);
		} catch (Throwable t) {
			return unexpectedError(t);
		}

		return new EmptyRepresentation();
	}
}
