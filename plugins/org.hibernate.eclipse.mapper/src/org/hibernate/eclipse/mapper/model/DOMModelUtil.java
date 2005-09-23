package org.hibernate.eclipse.mapper.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.provisional.format.ElementNodeFormatter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

	public static List getAdaptedElements(Element n, String elementName, INodeAdapterFactory factory) {
		List result = new ArrayList();		
		NodeList list = n.getElementsByTagName(elementName);
		for (int i = 0; i < list.getLength(); i++) {
			Node item = list.item(i);
			result.add(factory.adapt((INodeNotifier) item));			
		}
		return result;
	}
}
