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
package org.hibernate.eclipse.mapper.views.contentoutline;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xml.ui.views.contentoutline.XMLContentOutlineConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.w3c.dom.Node;

public class HibernateXMLContentOutlineConfiguration extends
		XMLContentOutlineConfiguration {

	/*
	private JFaceNodeAdapterFactoryForXML myFactory;

	protected IJFaceNodeAdapterFactory getFactory() {
		
		if(myFactory==null) {
			IJFaceNodeAdapterFactory realFactory = super.getFactory();
			
			myFactory = new JFaceNodeAdapterFactoryForXML(realFactory);
		}
		return myFactory;
	}*/
	
	
	private HibernateToolsLabelProvider labelProvider;

	public ILabelProvider getLabelProvider(TreeViewer viewer) {
		if(labelProvider==null) {
			labelProvider = new HibernateToolsLabelProvider(super.getLabelProvider(viewer));
		} 
		return labelProvider;
	}
	
	static class HibernateToolsLabelProvider implements ILabelProvider {
		ILabelProvider delegate;

		public HibernateToolsLabelProvider(ILabelProvider labelProvider) {
			delegate = labelProvider;
		}

		public void addListener(ILabelProviderListener listener) {
			delegate.addListener( listener );
		}

		public void dispose() {
			delegate.dispose();
		}

		static Map nameToMap = new HashMap();
		static {
			//TODO: this will affect any xml provided by hibernatetools...should be configured by contenttype instead.
			nameToMap.put("many-to-one", ImageConstants.MANYTOONE);
			nameToMap.put("one-to-many", ImageConstants.ONETOMANY);
			nameToMap.put("property", ImageConstants.PROPERTY);	
			nameToMap.put("class", ImageConstants.MAPPEDCLASS);
			nameToMap.put("subclass", ImageConstants.MAPPEDCLASS);
			nameToMap.put("joined-subclass", ImageConstants.MAPPEDCLASS);
			nameToMap.put("union-subclass", ImageConstants.MAPPEDCLASS);
			nameToMap.put("id", ImageConstants.IDPROPERTY);
			nameToMap.put("one-to-one", ImageConstants.ONETOONE);
			nameToMap.put("component", ImageConstants.ONETOONE);
		}
		
		public Image getImage(Object element) {
			Node node = (Node) element;
			if(node.getNodeType()==Node.ELEMENT_NODE) {
				String key = (String) nameToMap.get( node.getNodeName() );
				if(key!=null) {
					return EclipseImages.getImage(key);
				}
			}
			return delegate.getImage( element );
		}

		public String getText(Object element) {
			if(element instanceof Node) {
				Node node = (Node) element;
				String nodeName = node.getNodeName();
				if(node.getNodeType()==Node.PROCESSING_INSTRUCTION_NODE && "xml".equals(nodeName)) {
					return "xml (Hibernate Tools)";
				}
			}
			return delegate.getText( element );
		}

		public boolean isLabelProperty(Object element, String property) {
			return delegate.isLabelProperty( element, property );
		}

		public void removeListener(ILabelProviderListener listener) {
			delegate.removeListener( listener );
		}
	}
	
}
