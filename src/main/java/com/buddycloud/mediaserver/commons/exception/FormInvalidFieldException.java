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
package com.buddycloud.mediaserver.commons.exception;

public class FormInvalidFieldException extends FormFieldException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6036687197655827078L;
	private String invalidField;

	public FormInvalidFieldException(String invalidField) {
		super("multipart/form-data invalid field: '" + invalidField + "'");
		this.invalidField = invalidField;
	}

	public String getInvalidField() {
		return invalidField;
	}

	public void setInvalidField(String invalidField) {
		this.invalidField = invalidField;
	}
}
