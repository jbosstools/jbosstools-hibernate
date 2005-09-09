package org.hibernate.eclipse.mapper.model;

import org.eclipse.wst.xml.core.internal.provisional.format.ElementNodeFormatter;
import org.w3c.dom.Node;

public class DOMModelUtil {

	private DOMModelUtil() {
		
	}

	static public Node getNextNamedSibling(Node node, String nodeName) {
		Node nextSibling = node.getNextSibling();
		while(nextSibling!=null && !nextSibling.getNodeName().equals(nodeName)) {
			nextSibling = nextSibling.getNextSibling();
		}
		return nextSibling;
	}

	static Node getPreviousNamedSibling(Node node, String nodeName) {
		Node nextSibling = node.getPreviousSibling();
		while(nextSibling!=null && !nextSibling.getNodeName().equals(nodeName)) {
			nextSibling = nextSibling.getPreviousSibling();
		}
		return nextSibling;
	}

	static public void addElementBefore(Node parentNode, Node element, Node before) {
		if (before == null) {
			parentNode.appendChild(element);
		} else {
			parentNode.insertBefore(element, before);
		}
		DOMModelUtil.formatNode(parentNode);
	}

	static void formatNode(Node node) {
		ElementNodeFormatter formatter = new ElementNodeFormatter();
	    formatter.format(node);		
	}

	static public boolean isWhiteSpace(Node node) {
		return node != null && node.getNodeType() == Node.TEXT_NODE && node.getNodeValue().trim().length() == 0;	
	}
}
