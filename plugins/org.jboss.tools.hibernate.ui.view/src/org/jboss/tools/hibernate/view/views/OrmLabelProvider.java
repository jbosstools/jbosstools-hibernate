/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.view.views;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Table;

public class OrmLabelProvider extends LabelProvider implements IColorProvider, IFontProvider {

	private Map imageCache = new HashMap(25);
	private OrmModelImageVisitor ormModelImageVisitor;
	private OrmModelNameVisitor ormModelNameVisitor;

	public OrmLabelProvider(OrmModelImageVisitor imageVisitor, OrmModelNameVisitor nameVisitor) {
		super();
		ormModelImageVisitor = imageVisitor;
		ormModelNameVisitor = nameVisitor;		
	}
	
	public Image getImage(Object element) {
		ImageDescriptor descriptor = null;
		
		if (element instanceof RootClass) {
			descriptor = (ImageDescriptor) ((RootClass) element).accept(ormModelImageVisitor);
		} else if (element instanceof Property) {
			descriptor = (ImageDescriptor)ormModelImageVisitor.visitPersistentField((Property) element, null);
		} else if (element instanceof Table) {
			descriptor = (ImageDescriptor)ormModelImageVisitor.visitDatabaseTable((Table) element);
		} else if (element instanceof Column) {
			descriptor = (ImageDescriptor)ormModelImageVisitor.visitDatabaseColumn((Column) element);
		} else if (element instanceof DependantValue) {
			descriptor = (ImageDescriptor)ormModelImageVisitor.visitComponentKeyMapping((DependantValue) element, null);
		} else if (element instanceof Component) {
			descriptor = (ImageDescriptor)ormModelImageVisitor.visitComponentMapping((Component) element);
		} else {
			//throw unknownElement(element);
			return null;			
		}

		Image image = (Image) imageCache.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			imageCache.put(descriptor, image);
		}
		return image;
	}

	public String getText(Object element) {
		if (element instanceof RootClass) {
			String name = (String)ormModelNameVisitor.visitPersistentClass((RootClass)element, null);
			if (name == null) {
				return "OrmElement";
			} else {
				return name;
			}
		} else if (element instanceof SingleTableSubclass) {
				String name = (String)ormModelNameVisitor.visitPersistentClass((SingleTableSubclass)element, null);
				if (name == null) {
					return "OrmElement";
				} else {
					return name;
				}
		} else if (element instanceof Property) {
			String name = (String)ormModelNameVisitor.visitPersistentField((Property)element, null);
			if (name == null) {
				return "OrmElement";
			} else {
				return name;
			}
		} else if (element instanceof Column) {
			String name = (String)ormModelNameVisitor.visitDatabaseColumn((Column)element, null);
			if (name == null) {
				return "OrmElement";
			} else {
				return name;
			}
		} else if (element instanceof OneToMany || element instanceof ManyToOne) {
			String name = "element";
			if (name == null) {
				return "OrmElement";
			} else {
				return name;
			}
		} else if (element instanceof SimpleValue) {
			if (element instanceof DependantValue) {
				String name = (String)ormModelNameVisitor.visitCollectionKeyMapping((DependantValue)element, null);
				if (name == null) {
					return "OrmElement";
				} else {
					return name;
				}
			} else if (element instanceof Component) {
				String name = (String)ormModelNameVisitor.visitComponentMapping((Component)element, null);
				if (name == null) {
					return "OrmElement";
				} else {
					return name;
				}
			} else {
				return "element";
//				throw unknownElement(element);
			}
		} else if (element instanceof String){
			return (String) element;
		} else {
			throw unknownElement(element);
		}
		
	}

	protected RuntimeException unknownElement(Object element) {
		if (element != null && element.getClass() != null )
			return new RuntimeException("Unknown type of element in tree of type: " + element.getClass().getName());
		else return new RuntimeException("Unknown type of element in tree of type: " + element);
		
	}
	
	public void dispose() {
		for (Iterator i = imageCache.values().iterator(); i.hasNext();) {
			((Image) i.next()).dispose();
		}
		imageCache.clear();
	}

	public Color getForeground(Object element) {
		if (element instanceof RootClass) {
			return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
		} else if (element instanceof Property) {
			return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);
		}
		
		return null;
	}

	public Color getBackground(Object element) {
		return null;
	}

	public Font getFont(Object element) {
/*		if (element instanceof IOrmProject) {
			return JFaceResources.getFontRegistry().getBold(JFaceResources.getTextFont().getFontData()[0].getName());
		}*/
		return null;
	}	

}