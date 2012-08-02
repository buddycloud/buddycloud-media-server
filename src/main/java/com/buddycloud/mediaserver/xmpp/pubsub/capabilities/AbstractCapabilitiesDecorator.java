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
package com.buddycloud.mediaserver.xmpp.pubsub.capabilities;


public abstract class AbstractCapabilitiesDecorator implements CapabilitiesDecorator {
	
	private AbstractCapabilitiesDecorator decorator;
	
	
	public AbstractCapabilitiesDecorator(AbstractCapabilitiesDecorator decorator) {
		this.decorator = decorator;
	}
	
	public AbstractCapabilitiesDecorator() {}
	
	
	public boolean isUserAllowed(String affiliationType) {
		if (this.decorator == null) {
			return isAllowed(affiliationType);
		}
		
		return this.decorator.isUserAllowed(affiliationType) || isAllowed(affiliationType);
	}
	
	public boolean isAllowed(String affiliationType) {
		return getType().equals(affiliationType);
	}
}
