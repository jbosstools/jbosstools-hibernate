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
package org.hibernate.eclipse.mapper.editors;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.hibernate.eclipse.mapper.model.DOMModelUtil;
import org.w3c.dom.Element;

public class SQLTypeContentProvider implements IStructuredContentProvider {

	public Object[] getElements(Object inputElement) {
		IStructuredModel im = (IStructuredModel)inputElement;
		
		if(im instanceof IDOMModel) {
			IDOMModel model = (IDOMModel)im;
			List childNodes = DOMModelUtil.getChildrenByTagName(model.getDocument(), "hibernate-reverse-engineering");
			if(childNodes.size()>=1) {
				Element l = (Element)childNodes.get(0);
				childNodes = DOMModelUtil.getChildrenByTagName(l, "type-mapping");
				if(childNodes.size()>=1) {
					childNodes = DOMModelUtil.getChildrenByTagName(l, "sql-type");
					Object[] o = new Object[childNodes.size()];
					for (int i = 0; i < childNodes.size(); i++) {
						o[i] = childNodes.get(i);					
					}
					return o;	
				}
				
			}		
		}
		return new Object[0];
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

}
