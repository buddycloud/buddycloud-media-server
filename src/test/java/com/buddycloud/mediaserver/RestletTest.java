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
package com.buddycloud.mediaserver;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;

import com.buddycloud.mediaserver.commons.MediaServerConfiguration;
import com.buddycloud.mediaserver.web.MediaServerApplication;

public class RestletTest implements TextExtension {
	private static Logger LOGGER = Logger.getLogger(RestletTest.class);
	
	private Component component;
	private boolean started;
	
	
	public RestletTest() {
		this.started = false;
	}

	
	public void start(Properties configuration) throws Exception {
		if (!started) {
			component = new Component();  
			component.getServers().add(Protocol.HTTP, 
					Integer.valueOf(configuration.getProperty(MediaServerConfiguration.HTTP_PORT)));
			
			Context context = component.getContext().createChildContext();
			component.getDefaultHost().attach(new MediaServerApplication(context));
			
			component.start(); 
			
			LOGGER.debug("Started test HTTP server");

			started = true;
		}
	} 
	
	public void shutdown() throws Exception {
		if (started) {
			component.stop();
			
			started = false;
		}
	}
}
