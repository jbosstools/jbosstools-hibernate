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

import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.hibernate.eclipse.console.model.IRevEngColumn;
import org.w3c.dom.Node;

public class RevEngColumnAdapter extends DOMAdapter implements IRevEngColumn {
	
	public RevEngColumnAdapter(Node node, DOMReverseEngineeringDefinition model) {
		super( node, model );
	}

	public String getJDBCType() {
		return getNodeValue("jdbc-type", null);
	}

	public String getType() {
		return getNodeValue("type", null);
	}

	public String getPropertyName() {
		return getNodeValue("property", null);
	}

	public boolean getExclude() {
		String nodeValue = getNodeValue("exclude", "false");
		return nodeValue==null ? false : Boolean.valueOf(nodeValue).booleanValue();
	}

	public String getName() {
		return getNodeValue("name", null);
	}

	public void setName(String value) {
		setAttribute("name", value, "");		
	}

	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		getModel().tablesChanged(notifier);
		firePropertyChange(((Node)changedFeature).getNodeName(), oldValue, newValue);
	}

	public void setPropertyName(String value) {
		setAttribute("property", value, "");		
	}

	public void setJDBCType(String value) {
		setAttribute("jdbc-type", value, "");		
	}

	public void setType(String value) {
		setAttribute("type", value, "");		
	}

	public void setExcluded(boolean selection) {
		setAttribute("exclude", ""+selection, "false");		
	}

}
