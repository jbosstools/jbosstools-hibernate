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

import java.util.List;

import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.hibernate.eclipse.console.model.IRevEngColumn;
import org.hibernate.eclipse.console.model.IRevEngGenerator;
import org.hibernate.eclipse.console.model.IRevEngPrimaryKey;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RevEngPrimaryKeyAdapter extends DOMAdapter implements
		IRevEngPrimaryKey {

	public RevEngPrimaryKeyAdapter(Node node, DOMReverseEngineeringDefinition revEngDef) {
		super( node, revEngDef );
	}

	public IRevEngGenerator getGenerator() {
		List adaptedElements = getAdaptedElements((Element) getNode(), "generator");
		if(adaptedElements.isEmpty()) {
			return null;
		} else {
			return (IRevEngGenerator) adaptedElements.get(0);
		}
	}

	public IRevEngColumn[] getColumns() {
		return (IRevEngColumn[]) getAdaptedElements((Element) getNode(), "key-column").toArray(new IRevEngColumn[0]);
	}

	public void notifyChanged(INodeNotifier notifier, int eventType,
			Object changedFeature, Object oldValue, Object newValue, int pos) {
		getModel().tablesChanged(notifier);
	}

	public void addGenerator() {
		RevEngGeneratorAdapter key = (RevEngGeneratorAdapter) getModel().createGenerator();
		key.setGeneratorClassName("assigned");
		getNode().insertBefore(key.getNode(), getNode().getFirstChild());
		DOMModelUtil.formatNode(getNode().getParentNode());
	}

	public void addColumn() {
		RevEngColumnAdapter key = (RevEngColumnAdapter) getModel().createKeyColumn();
		key.setName("column_" + (getColumns().length+1));
		getNode().appendChild(key.getNode());
		DOMModelUtil.formatNode(getNode().getParentNode());
	}

}
