package com.buddycloud.mediaserver.web;
import java.io.File;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.buddycloud.mediaserver.business.MediaDAO;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.exception.MediaMetadataSourceException;


public class DownloadMediaResource extends ServerResource {

//	@Get
//	public Representation getMedia(Representation entity) {
//		String channel = (String) getRequest().getAttributes().get(Constants.CHANNEL_ARG);
//		String mediaId = (String) getRequest().getAttributes().get(Constants.MEDIA_ARG);
//
//		return new StringRepresentation("GET: " + channel + "/" + mediaId);
//	}

	@Get
	public Representation getMedia() {
		String channelId = (String) getRequest().getAttributes().get(Constants.CHANNEL_ARG);
		String mediaId = (String) getRequest().getAttributes().get(Constants.MEDIA_ARG);

		MediaDAO dao = MediaDAO.gestInstance();
		
		File media = dao.getMedia(channelId, mediaId);
		
		if (media != null) {
			try {
				return new FileRepresentation(media, new MediaType(dao.getMediaType(mediaId)));
			} catch (MediaMetadataSourceException e) {
				setStatus(Status.SERVER_ERROR_INTERNAL);
				return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
			}
		}
		
		setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		return null;
	}
}
