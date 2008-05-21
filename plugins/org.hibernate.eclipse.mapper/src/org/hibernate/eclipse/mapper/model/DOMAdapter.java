/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.mapper.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class DOMAdapter implements INodeAdapter {

	private final Node node;
	protected final INodeAdapterFactory factory;
	private final DOMReverseEngineeringDefinition model;

	PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	public DOMAdapter(Node node, DOMReverseEngineeringDefinition revEngDef) {
		this.node=node;
		this.model = revEngDef;		
		this.factory = revEngDef.getNodeFactory();
	}

	
	protected void setAttribute(String attributeName, String value, String defaultValue) {
		org.w3c.dom.Node attribNode = getNode().getAttributes().getNamedItem(attributeName);
		if(attribNode!=null && safeEquals(value, defaultValue)) {
			((Element)getNode()).removeAttribute(attributeName);										
		} else if (attribNode!=null) {
			attribNode.setNodeValue(value);
		} else if (attribNode == null && !safeEquals(value, defaultValue)) {
			attribNode = getNode().getOwnerDocument().createAttribute(attributeName);
			getNode().getAttributes().setNamedItem(attribNode);
			attribNode.setNodeValue(value);
		} 
	}

	private boolean safeEquals(Object value, Object tf) {
		if(value==tf) return true;
		if(value==null) return false;
		return value.equals(tf);
	}

	protected String getNodeValue(String attrib, String nullValue) {
		Node type = getNode().getAttributes().getNamedItem(attrib);
		return type == null ? nullValue : type.getNodeValue();
	}


	protected Node getNode() {
		return node;
	}


	public boolean isAdapterForType(Object type) {
		return type==DOMAdapter.class;
	}


	protected DOMReverseEngineeringDefinition getModel() {
		return model;
	}


	protected List getAdaptedElements(Element n, String elementName) {
		return DOMModelUtil.getAdaptedElements(n, elementName, factory);		
	}


	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener( listener );
	}


	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcs.addPropertyChangeListener( propertyName, listener );
	}


	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener( listener );
	}


	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcs.removePropertyChangeListener( propertyName, listener );
	}


	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		pcs.firePropertyChange( propertyName, oldValue, newValue );
	}

}
