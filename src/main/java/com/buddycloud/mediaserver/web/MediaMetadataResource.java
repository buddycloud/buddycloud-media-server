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
import com.buddycloud.mediaserver.commons.exception.*;
import com.buddycloud.mediaserver.xmpp.XMPPToolBox;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

/**
 * Resource that represents /<channel>/<mediaId>/metadata endpoint.
 *
 * @author Rodrigo Duarte Sousa - rodrigodsousa@gmail.com
 */
public class MediaMetadataResource extends MediaServerResource {

    /**
     * Gets media's metadata (GET /<channel>/<mediaId>/metadata)
     */
    @Get
    public Representation getMediaMetadata() {
        setServerHeader();
        Request request = getRequest();

        try {
            String userJID = null;
            String entityId = (String) request.getAttributes().get(Constants.ENTITY_ARG);
            String mediaId = (String) request.getAttributes().get(Constants.MEDIA_ARG);

            if (!mediaId.equals(Constants.AVATAR_ARG)) {
                boolean isChannelPublic = XMPPToolBox.getInstance().getPubSubClient().isChannelPublic(entityId);
                if (!isChannelPublic) {
                    userJID = getUsedJID(request, true);
                }
            }

            MediaDAO mediaDAO = DAOFactory.getInstance().getDAO();
            return new StringRepresentation(mediaDAO.getMediaInfo(userJID,
                    entityId, mediaId), MediaType.APPLICATION_JSON);
        } catch (MetadataSourceException e) {
            setStatus(Status.SERVER_ERROR_INTERNAL);
        } catch (UserNotAllowedException e) {
            setStatus(Status.CLIENT_ERROR_FORBIDDEN);
        } catch (MediaNotFoundException e) {
            setStatus(Status.CLIENT_ERROR_NOT_FOUND);
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
}
