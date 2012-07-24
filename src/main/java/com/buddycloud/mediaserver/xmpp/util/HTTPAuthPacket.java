package com.buddycloud.mediaserver.xmpp.util;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.tree.BaseElement;
import org.dom4j.tree.DefaultAttribute;
import org.xmpp.packet.IQ;


/**
 * A PacketExtension that implements XEP-0070: HTTP Authentication
 * 
 * @author Rodrigo Duarte Sousa, rodrigodsousa@gmail.com
 * @see <a
 *      href="http://xmpp.org/extensions/xep-0070.html">XEP-0070:&nbsp;HTTP Authentication</a>
 */
public class HTTPAuthPacket extends IQ {

    public static final String ELEMENT_NAME = "confirm";
    public static final String NAMESPACE = "http://jabber.org/protocol/http-auth";

    
    private String id;
    private String url;
    
    
    public HTTPAuthPacket(String id, String url) {
    	this.id = id;
    	this.url = url;
    	addChildElement();
    }
    
    private void addChildElement() {
		Element childElement = new BaseElement(getElementName(), new Namespace(null, getNamespace()));
		childElement.add(new DefaultAttribute("id", id));
		childElement.add(new DefaultAttribute("url", url));
		
		setChildElement(childElement);
	}

	public String getElementName() {
        return ELEMENT_NAME;
    }

    public String getNamespace() {
        return NAMESPACE;
    }
}
