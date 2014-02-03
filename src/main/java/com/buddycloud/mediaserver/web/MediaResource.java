/*
 * Copyright 2014 Buddycloud
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
import com.buddycloud.mediaserver.commons.exception.*;
import com.buddycloud.mediaserver.web.representation.DynamicFileRepresentation;
import com.buddycloud.mediaserver.xmpp.XMPPToolBox;
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

import java.io.File;
import java.io.IOException;

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

        try {
            String userJID = getUsedJID(request, true);
            MediaDAO mediaDAO = DAOFactory.getInstance().getDAO();

            String entityId = (String) request.getAttributes().get(Constants.ENTITY_ARG);
            String mediaId = (String) request.getAttributes().get(Constants.MEDIA_ARG);

            if (!mediaId.equals(Constants.AVATAR_ARG)) {
                setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
                return new StringRepresentation("Valid only for avatars!", MediaType.APPLICATION_JSON);
            }

            String result;
            if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {
                result = mediaDAO.insertFormDataMedia(userJID, entityId, getRequest(), true);
            } else {
                result = mediaDAO.insertWebFormMedia(userJID, entityId, new Form(entity), true);
            }

            setStatus(Status.SUCCESS_CREATED);
            return new StringRepresentation(result, MediaType.APPLICATION_JSON);
        } catch (MissingAuthenticationException e) {
            setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
            return authenticationResponse();
        } catch (FileUploadException e) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        } catch (UserNotAllowedException e) {
            setStatus(Status.CLIENT_ERROR_FORBIDDEN);
        } catch (XMPPException e) {
            setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
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
        setServerHeader();
        Request request = getRequest();

        try {
            String userJID = getUsedJID(request, true);
            String entityId = (String) request.getAttributes().get(Constants.ENTITY_ARG);
            String mediaId = (String) request.getAttributes().get(Constants.MEDIA_ARG);

            MediaDAO mediaDAO = DAOFactory.getInstance().getDAO();
            mediaDAO.deleteMedia(userJID, entityId, mediaId);
            return new StringRepresentation("Media deleted!", MediaType.APPLICATION_JSON);
        } catch (MetadataSourceException e) {
            setStatus(Status.SERVER_ERROR_INTERNAL);
        } catch (MediaNotFoundException e) {
            setStatus(Status.CLIENT_ERROR_NOT_FOUND);
        } catch (UserNotAllowedException e) {
            setStatus(Status.CLIENT_ERROR_FORBIDDEN);
        } catch (MissingAuthenticationException e) {
            setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
            return authenticationResponse();
        } catch (XMPPException e) {
            setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
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

        try {
            String userJID = getUsedJID(request, true);
            String entityId = (String) request.getAttributes().get(Constants.ENTITY_ARG);
            String mediaId = (String) request.getAttributes().get(Constants.MEDIA_ARG);

            MediaDAO mediaDAO = DAOFactory.getInstance().getDAO();

            return new StringRepresentation(mediaDAO.updateMedia(
                    userJID, entityId, mediaId, new Form(entity)),
                    MediaType.APPLICATION_JSON);
        } catch (MetadataSourceException e) {
            setStatus(Status.SERVER_ERROR_INTERNAL);
        } catch (MediaNotFoundException e) {
            setStatus(Status.CLIENT_ERROR_NOT_FOUND);
        } catch (UserNotAllowedException e) {
            setStatus(Status.CLIENT_ERROR_FORBIDDEN);
        } catch (MissingAuthenticationException e) {
            setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
            return authenticationResponse();
        } catch (XMPPException e) {
            setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
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
        Request request = getRequest();

        Integer maxHeight = getIntegerQueryValue(Constants.MAX_HEIGHT_QUERY);
        Integer maxWidth = getIntegerQueryValue(Constants.MAX_WIDTH_QUERY);

        if (maxHeight != null && maxWidth == null) {
            maxWidth = maxHeight;
        } else if (maxHeight == null && maxWidth != null) {
            maxHeight = maxWidth;
        }

        String entityId = (String) request.getAttributes().get(Constants.ENTITY_ARG);
        String mediaId = (String) request.getAttributes().get(Constants.MEDIA_ARG);

        try {
            if (mediaId.equals(Constants.AVATAR_ARG)) {
                return getAvatar(entityId, maxHeight, maxWidth);
            }

            return getRegularMedia(request, entityId, mediaId, maxHeight, maxWidth);
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
        } catch (MissingAuthenticationException e) {
            setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
            return authenticationResponse();
        } catch (XMPPException e) {
            setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
        } catch (Throwable t) {
            return unexpectedError(t);
        }

        return new EmptyRepresentation();
    }

    private Representation getRegularMedia(Request request, String entityId, String mediaId, Integer maxHeight, Integer maxWidth)
            throws UserNotAllowedException, MissingAuthenticationException, IOException,
            MediaNotFoundException, MetadataSourceException, InvalidPreviewFormatException {

        String userJID = null;
        boolean isChannelPublic = XMPPToolBox.getInstance().getPubSubClient().isChannelPublic(entityId);
        if (!isChannelPublic) {
            userJID = getUsedJID(request, true);
        }
        MediaDAO mediaDAO = DAOFactory.getInstance().getDAO();

        if (maxHeight == null && maxWidth == null) {
            MediaFile<File> mediaFile = mediaDAO.getMedia(userJID, entityId, mediaId);
            MediaType mediaType = new MediaType(mediaFile.getMimeType());
            return new FileRepresentation(mediaFile.getMediaFile(), mediaType);
        }

        MediaFile<byte[]> thumbnail = mediaDAO.getMediaPreview(userJID, entityId, mediaId, maxHeight, maxWidth);
        return new DynamicFileRepresentation(new MediaType(thumbnail.getMimeType()), thumbnail.getMediaFile());
    }

    private Representation getAvatar(String entityId, Integer maxHeight, Integer maxWidth)
            throws MetadataSourceException, UserNotAllowedException, MediaNotFoundException,
            IOException, InvalidPreviewFormatException {

        MediaDAO mediaDAO = DAOFactory.getInstance().getDAO();
        Representation representation;

        if (maxHeight == null && maxWidth == null) {
            MediaFile<File> mediaFile = mediaDAO.getMedia(null, entityId, Constants.AVATAR_ARG);
            MediaType mediaType = new MediaType(mediaFile.getMimeType());
            representation = new FileRepresentation(mediaFile.getMediaFile(), mediaType);
            representation.setModificationDate(mediaFile.getLastModified());
        } else {
            MediaFile<byte[]> thumbnail = mediaDAO.getMediaPreview(null, entityId, Constants.AVATAR_ARG, maxHeight, maxWidth);
            representation = new DynamicFileRepresentation(new MediaType(thumbnail.getMimeType()), thumbnail.getMediaFile());
            representation.setModificationDate(thumbnail.getLastModified());
        }

        return representation;
    }


}
