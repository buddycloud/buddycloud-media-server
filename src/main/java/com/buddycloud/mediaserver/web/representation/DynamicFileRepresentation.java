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