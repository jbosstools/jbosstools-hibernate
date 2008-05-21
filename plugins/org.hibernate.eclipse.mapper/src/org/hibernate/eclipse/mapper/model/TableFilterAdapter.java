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
import org.hibernate.eclipse.console.model.ITableFilter;
import org.w3c.dom.Node;

public class TableFilterAdapter extends DOMAdapter implements ITableFilter {

	public TableFilterAdapter(Node node, DOMReverseEngineeringDefinition revEngDef) {
		super(node, revEngDef);
	}
	
	public void setExclude(Boolean exclude) {
		setAttribute("exclude", exclude==null?"false":exclude.toString(), "false");					
	}

	public void setMatchCatalog(String catalog) {
		setAttribute( "match-catalog", catalog, ".*" );
	}

	public void setMatchSchema(String schema) {
		setAttribute("match-schema", schema, ".*");		
	}

	public void setMatchName(String name) {
		setAttribute("match-name", name, null);		
	}

	public Boolean getExclude() {
		Node type = getNode().getAttributes().getNamedItem("exclude");
		return type == null ? Boolean.FALSE : Boolean.valueOf(type.getNodeValue());
	}

	public String getMatchCatalog() {
		Node type = getNode().getAttributes().getNamedItem("match-catalog");
		return type == null ? ".*" : type.getNodeValue();
	}

	public String getMatchSchema() {
		Node type = getNode().getAttributes().getNamedItem("match-schema");
		return type == null ? ".*" : type.getNodeValue();
	}

	public String getMatchName() {
		Node type = getNode().getAttributes().getNamedItem("match-name");
		return type == null ? ".*" : type.getNodeValue();	
	}
	
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		getModel().tableFilterChanged(notifier);	
	}
}