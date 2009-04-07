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
import org.hibernate.eclipse.console.model.ITypeMapping;
import org.w3c.dom.Node;

public class TypeMappingAdapter extends DOMAdapter implements ITypeMapping {
	
	public TypeMappingAdapter(Node node, DOMReverseEngineeringDefinition model) {
		super( node, model );
	}

	public String getJDBCType() {
		Node type = getNode().getAttributes().getNamedItem("jdbc-type"); //$NON-NLS-1$
		return type == null ? null : type.getNodeValue();
	}

	public String getHibernateType() {
		Node type = getNode().getAttributes().getNamedItem("hibernate-type"); //$NON-NLS-1$
		return type == null ? null : type.getNodeValue();
	}

	public Integer getLength() {
		String name = "length"; //$NON-NLS-1$
		return getInteger( name );
	}

	public Boolean getNullable() {
		return getBoolean("not-null"); //$NON-NLS-1$
	}
	
	private Integer getInteger(String name) {
		Node type = getNode().getAttributes().getNamedItem(name);
		if(type == null) {
			return null;
		} else {
			try {
				Integer decode = Integer.decode(type.getNodeValue());
				return decode;
			} catch (NumberFormatException nfe) {
				return null;
			}
		}		
	}
	
	private Boolean getBoolean(String name) {
		Node type = getNode().getAttributes().getNamedItem(name);
		Boolean decode = null;
		if(type != null) {
			decode = Boolean.valueOf(type.getNodeValue());
		}		
		return decode;
	}

	public Integer getPrecision() {
		return getInteger( "precision" ); //$NON-NLS-1$
	}

	public Integer getScale() {
		return getInteger( "scale" ); //$NON-NLS-1$
	}

	public void setJDBCType(String string) {
		setAttribute("jdbc-type", string, null); //$NON-NLS-1$
	}

	public void setLength(Integer length) {
		setAttribute("length", length==null?null:length.toString(), null); //$NON-NLS-1$
	}

	public void setHibernateType(String type) {
		setAttribute("hibernate-type", type, ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void setPrecision(Integer precision) {
		setAttribute("precision", precision==null?null:precision.toString(), null); //$NON-NLS-1$
	}

	public void setScale(Integer scale) {
		setAttribute("scale", scale==null?null:scale.toString(), null); //$NON-NLS-1$
	}

	public void setNullable(Boolean value) {
		setAttribute("not-null", value==null?null:value.toString(), null);		 //$NON-NLS-1$
	}
	
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		getModel().sqlTypeChanged(notifier);		
	}

	

	
}
