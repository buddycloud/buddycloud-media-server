package org.jivesoftware.smackx.packet;

import org.jivesoftware.smack.packet.IQ;

/**
 * A PacketExtension that implements XEP-0070: HTTP Authentication
 * 
 * @author Rodrigo Duarte Sousa, rodrigodsousa@gmail.com
 * @see <a
 *      href="http://xmpp.org/extensions/xep-0070.html">XEP-0070:&nbsp;HTTP Authentication</a>
 */
public class HTTPAuth extends IQ {

    public static final String ELEMENT_NAME = "confirm";
    public static final String NAMESPACE = "http://jabber.org/protocol/http-auth";

    
    private String id;
    
    
    public HTTPAuth(String id) {
    	this.id = id;
    }
    
    public String getElementName() {
        return ELEMENT_NAME;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

	@Override
	public String getChildElementXML() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("<");
        builder.append(getElementName());
        builder.append(" xmlns=\"");
        builder.append(getNamespace() + "\"");
        builder.append(" id=\"");
        builder.append(id + "\"");
        builder.append(" method=\"GET\"");
        builder.append(" url=\"");
        builder.append("\"/>");
        
        return builder.toString();
	}
}
