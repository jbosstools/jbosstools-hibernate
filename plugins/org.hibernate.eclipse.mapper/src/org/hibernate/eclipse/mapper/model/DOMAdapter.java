package org.hibernate.eclipse.mapper.model;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DOMAdapter {

	protected final Node node;

	public DOMAdapter(Node node) {
		this.node=node;
	}

	public Node getNode() {
		return node;
	}

	protected void setAttribute(String attributeName, String value, String defaultValue) {
		org.w3c.dom.Node attribNode = node.getAttributes().getNamedItem(attributeName);
		if(attribNode!=null && safeEquals(value, defaultValue)) {
			((Element)node).removeAttribute(attributeName);										
		} else if (attribNode!=null) {
			attribNode.setNodeValue(value);
		} else if (attribNode == null && !value.equals(defaultValue)) {
			attribNode = node.getOwnerDocument().createAttribute(attributeName);
			node.getAttributes().setNamedItem(attribNode);
			attribNode.setNodeValue(value);
		} 
	}

	private boolean safeEquals(Object value, Object tf) {
		if(value==tf) return true;
		if(value==null) return false;
		return value.equals(tf);
	}

}
