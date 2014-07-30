/*
 * Copyright 2011 buddycloud, Copyright 2014 Florian Schmaus
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
package org.jivesoftware.smackx.rsm.packet;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.util.XmlStringBuilder;

/**
 * @author Abmar
 * @author Florian Schmaus
 *
 */
public class RSMSet implements PacketExtension {

	public static final String NAMESPACE = "http://jabber.org/protocol/rsm";
	public static final String ELEMENT = "set";

	private Integer index = 0;
	private String first;
	private String last;
	private Integer count;
	private Integer max;

	private String before;
	private String after;

	public RSMSet() {
	}

	public RSMSet(int max) {
		this.max = max;
	}

	@Override
	public String getElementName() {
		return ELEMENT;
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	/**
	 * @return the index
	 */
	public Integer getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(Integer index) {
		this.index = index;
	}

	/**
	 * @return the first
	 */
	public String getFirst() {
		return first;
	}

	/**
	 * @param first
	 *            the first to set
	 */
	public void setFirst(String first) {
		this.first = first;
	}

	/**
	 * @return the last
	 */
	public String getLast() {
		return last;
	}

	/**
	 * @param last
	 *            the last to set
	 */
	public void setLast(String last) {
		this.last = last;
	}

	/**
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(Integer count) {
		this.count = count;
	}

	@Override
	public XmlStringBuilder toXML() {
		XmlStringBuilder xml = new XmlStringBuilder();
		xml.halfOpenElement(ELEMENT).xmlnsAttribute(NAMESPACE);
		xml.rightAngelBracket();

		if (first != null) {
			xml.halfOpenElement("first")
					.attribute("index", Integer.toString(index))
					.rightAngelBracket();
			xml.escape(first);
			xml.closeElement("first");
		}

		if (max != null) {
			xml.element("max", Integer.toString(max));
		}

		xml.optElement("last", last);

		if (count != null) {
			xml.element("count", Integer.toString(count));
		}

		xml.optElement("before", before);
		xml.optElement("after", after);
		xml.closeElement(ELEMENT);

		return xml;
	}

	public String getBefore() {
		return before;
	}

	public void setBefore(String before) {
		this.before = before;
	}

	public String getAfter() {
		return after;
	}

	public void setAfter(String after) {
		this.after = after;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}
}