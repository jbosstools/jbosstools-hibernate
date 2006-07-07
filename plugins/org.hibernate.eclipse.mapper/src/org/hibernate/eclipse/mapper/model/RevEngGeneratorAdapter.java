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
import org.hibernate.eclipse.console.model.IRevEngGenerator;
import org.hibernate.eclipse.console.model.IRevEngParameter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RevEngGeneratorAdapter extends DOMAdapter implements
		IRevEngGenerator {

	public RevEngGeneratorAdapter(Node node, DOMReverseEngineeringDefinition revEngDef) {
		super( node, revEngDef );
	}

	public String getGeneratorClassName() {
		return getNodeValue("class", "");
	}

	public IRevEngParameter[] getParameters() {
		return (IRevEngParameter[]) getAdaptedElements((Element) getNode(), "param").toArray(new IRevEngParameter[0]);
	}

	public void notifyChanged(INodeNotifier notifier, int eventType,
			Object changedFeature, Object oldValue, Object newValue, int pos) {
		getModel().tablesChanged(notifier);
	}

	public void setGeneratorClassName(String value) {
		setAttribute("class", value, null);		
	}

}
