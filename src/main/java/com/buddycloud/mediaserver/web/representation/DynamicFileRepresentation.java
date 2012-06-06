package com.buddycloud.mediaserver.web.representation;

import java.io.IOException;
import java.io.OutputStream;

import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;

public class DynamicFileRepresentation extends OutputRepresentation {

    private byte[] fileData;

    public DynamicFileRepresentation(MediaType mediaType, byte[] fileData) {
        super(mediaType);
        this.fileData = fileData;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        outputStream.write(fileData);
    }

}