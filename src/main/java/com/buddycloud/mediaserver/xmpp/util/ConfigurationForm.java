/**
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
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
package com.buddycloud.mediaserver.xmpp.util;

import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.ConfigureNodeFields;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;

/**
 * A decorator for a {@link Form} to easily enable reading and updating
 * of node configuration.  All operations read or update the underlying {@link DataForm}.
 * 
 * <p>Unlike the {@link Form}.setAnswer(XXX)} methods, which throw an exception if the field does not
 * exist, all <b>ConfigureForm.setXXX</b> methods will create the field in the wrapped form
 * if it does not already exist. 
 * 
 * @author Robin Collier
 */
public class ConfigurationForm extends ConfigureForm
{
	/**
	 * Create a decorator from an existing {@link DataForm} that has been
	 * retrieved from parsing a node configuration request.
	 * 
	 * @param configDataForm
	 */
	public ConfigurationForm(DataForm configDataForm)
	{
		super(configDataForm);
	}
	
	/**
	 * Create a decorator from an existing {@link Form} for node configuration.
	 * Typically, this can be used to create a decorator for an answer form
	 * by using the result of {@link #createAnswerForm()} as the input parameter.
	 * 
	 * @param nodeConfigForm
	 */
	public ConfigurationForm(Form nodeConfigForm)
	{
		super(nodeConfigForm.getDataFormToSend());
	}
	
	/**
	 * Create a new form for configuring a node.  This would typically only be used 
	 * when creating and configuring a node at the same time via {@link PubSubManager#createNode(String, Form)}, since 
	 * configuration of an existing node is typically accomplished by calling {@link LeafNode#getNodeConfiguration()} and
	 * using the resulting form to create a answer form.  See {@link #ConfigurationForm(Form)}.
	 * @param formType
	 */
	public ConfigurationForm(FormType formType)
	{
		super(formType);
	}
	
	private String getFieldValue(ConfigureNodeFields field)
	{
		FormField formField = getField(field.getFieldName());

		return formField.getValues().get(0);
	}

	public AccessModel getBuddycloudAccessModel() {
        String value = getFieldValue(ConfigureNodeFields.access_model);
		
		if (value == null) {
			return null;
		}
		return AccessModel.valueOf(value);
	}

}