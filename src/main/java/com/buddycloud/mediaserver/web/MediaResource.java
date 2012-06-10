package com.buddycloud.mediaserver.web;
import java.io.File;
import java.io.IOException;

import org.apache.commons.fileupload.FileUploadException;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.buddycloud.mediaserver.business.dao.AvatarDAO;
import com.buddycloud.mediaserver.business.dao.DAOFactory;
import com.buddycloud.mediaserver.business.dao.MediaDAO;
import com.buddycloud.mediaserver.commons.Constants;
import com.buddycloud.mediaserver.commons.exception.FormMissingFieldException;
import com.buddycloud.mediaserver.commons.exception.InvalidPreviewFormatException;
import com.buddycloud.mediaserver.commons.exception.MetadataSourceException;
import com.buddycloud.mediaserver.commons.exception.MediaNotFoundException;
import com.buddycloud.mediaserver.web.representation.DynamicFileRepresentation;


public class MediaResource extends ServerResource {
	
	@Post
	public Representation postAvatar(Representation entity) {
		String entityId = (String) getRequest().getAttributes().get(Constants.ENTITY_ARG);

		if (entity != null) {
			if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {
				
				String mediaId = (String) getRequest().getAttributes().get(Constants.MEDIA_ARG);
				
				if (mediaId.equals(Constants.AVATAR_ARG)) {
					AvatarDAO avatarDAO = DAOFactory.getInstance().getAvatarDAO();
					
					try {
						return new StringRepresentation(avatarDAO.insertMedia(entityId, getRequest()), 
								MediaType.APPLICATION_JSON);
					} catch (FileUploadException e) {
						setStatus(Status.SERVER_ERROR_INTERNAL);
						return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
					} catch (MetadataSourceException e) {
						setStatus(Status.SERVER_ERROR_INTERNAL);
						return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
					} catch (FormMissingFieldException e) {
						setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
						return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
					}
				}
			}
		}
		
		// POST request with no entity.
		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		return new StringRepresentation("POST request with no entity", MediaType.TEXT_PLAIN);
	}
	
	@Delete
	public Representation deleteMedia() {
		String entityId = (String) getRequest().getAttributes().get(Constants.ENTITY_ARG);
		String mediaId = (String) getRequest().getAttributes().get(Constants.MEDIA_ARG);

		return new StringRepresentation("DELETE /media/" + entityId + "/" + mediaId);
	}
	
	@Put
	public Representation putMedia() {
		String entityId = (String) getRequest().getAttributes().get(Constants.ENTITY_ARG);
		String mediaId = (String) getRequest().getAttributes().get(Constants.MEDIA_ARG);

		return new StringRepresentation("PUT /media/" + entityId + "/" + mediaId);
	}

	@Get
	public Representation getMedia() {
		String entityId = (String) getRequest().getAttributes().get(Constants.ENTITY_ARG);
		String mediaId = (String) getRequest().getAttributes().get(Constants.MEDIA_ARG);
		
		String maxHeight = getQueryValue(Constants.MAX_HEIGHT_QUERY);
		String maxWidth = getQueryValue(Constants.MAX_WIDTH_QUERY);
		
		if (mediaId.equals(Constants.AVATAR_ARG)) {
			return getAvatar(entityId, maxHeight, maxWidth);
		}

		MediaDAO mediaDAO = DAOFactory.getInstance().getMediaDAO();

		try {
			final MediaType mediaType = new MediaType(mediaDAO.getMediaType(mediaId));
			
			if (maxHeight == null || maxWidth == null) {
				File media = mediaDAO.getMedia(entityId, mediaId);
				return new FileRepresentation(media, mediaType);
			}
				
			byte[] preview = mediaDAO.getMediaPreview(entityId, mediaId, Integer.valueOf(maxHeight), Integer.valueOf(maxWidth));
			return new DynamicFileRepresentation(mediaType, preview);
		} catch (MetadataSourceException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
		} catch (IOException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);	
		} catch (MediaNotFoundException e) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
		} catch (InvalidPreviewFormatException e) {
			setStatus(Status.CLIENT_ERROR_PRECONDITION_FAILED);
			return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
		}
	}

	private Representation getAvatar(String entityId, String maxHeight, String maxWidth) {
		AvatarDAO avatarDAO = DAOFactory.getInstance().getAvatarDAO();

		try {
			final MediaType mediaType = new MediaType(avatarDAO.getAvatarMediaType(entityId));

			if (maxHeight == null || maxWidth == null) {
				File media = avatarDAO.getAvatar(entityId);
				return new FileRepresentation(media, mediaType);
			}
				
			byte[] media = avatarDAO.getAvatarPreview(entityId, Integer.valueOf(maxHeight), Integer.valueOf(maxWidth));
			return new DynamicFileRepresentation(mediaType, media);
		} catch (MetadataSourceException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
		} catch (IOException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);	
		} catch (MediaNotFoundException e) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
		} catch (InvalidPreviewFormatException e) {
			setStatus(Status.CLIENT_ERROR_PRECONDITION_FAILED);
			return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
		}
	}
}
