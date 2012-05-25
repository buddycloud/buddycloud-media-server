package com.buddycloud.mediaserver.web;
import java.io.File;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.buddycloud.mediaserver.business.MediaDAO;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.exception.MediaMetadataSourceException;
import com.buddycloud.mediaserver.commons.exception.MediaNotFoundException;


public class ChannelAvatarResource extends ServerResource {
	
	@Put
	public Representation putAvatar() {
		String channelId = (String) getRequest().getAttributes().get(Constants.CHANNEL_ARG);

		return new StringRepresentation("PUT /channel/" + channelId + "/media/avatar/1");
	}

	@Get
	public Representation getAvatar() {
		String channelId = (String) getRequest().getAttributes().get(Constants.CHANNEL_ARG);

		try {
			File media = MediaDAO.gestInstance().getChannelAvatar(channelId);
			return new FileRepresentation(media, new MediaType(MediaDAO.gestInstance().getMediaType(Constants.AVATAR_ID)));
		} catch (MediaMetadataSourceException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
		} catch (MediaNotFoundException e) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
		}
	}
}
