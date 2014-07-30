/*
 * Copyright 2011 buddycloud
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
package org.jivesoftware.smackx.rsm.provider;

import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.rsm.packet.RSMSet;
import org.xmlpull.v1.XmlPullParser;

public class RSMProvider implements PacketExtensionProvider {

	public static void registerWithProviderManager() {
		ProviderManager.addExtensionProvider(RSMSet.ELEMENT, RSMSet.NAMESPACE, new RSMProvider());
	}

	@Override
	public RSMSet parseExtension(XmlPullParser parser) throws Exception {
		RSMSet rsmSet = new RSMSet();

		boolean readingFirst = false;
		boolean readingLast = false;
		boolean readingCount = false;

		while (true) {
			int eventType = parser.next();

			if (eventType == XmlPullParser.END_TAG
					&& "set".equals(parser.getName())) {
				break;
			} else if (eventType == XmlPullParser.START_TAG) {
				if ("first".equals(parser.getName())) {
					String indexValue = parser.getAttributeValue("", "index");
					if (indexValue != null) {
						rsmSet.setIndex(Integer.parseInt(indexValue));
					}
					rsmSet.setFirst(parser.nextText());
					readingFirst = true;
				} else if ("last".equals(parser.getName())) {
					rsmSet.setLast(parser.nextText());
					readingLast = true;
				} else if ("count".equals(parser.getName())) {
					rsmSet.setCount(Integer.valueOf(parser.nextText()));
					readingCount = true;
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if ("first".equals(parser.getName())) {
					readingFirst = false;
				} else if ("last".equals(parser.getName())) {
					readingLast = false;
				} else if ("count".equals(parser.getName())) {
					readingCount = false;
				}
			} else if (eventType == XmlPullParser.TEXT) {
				if (readingFirst) {
					rsmSet.setFirst(parser.getText());
				} else if (readingLast) {
					rsmSet.setLast(parser.getText());
				} else if (readingCount) {
					rsmSet.setCount(Integer.parseInt(parser.getText()));
				}
			}

		}
		return rsmSet;
	}

}
