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
package org.hibernate.eclipse.console.workbench;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySheetEntry;

public class BeanPropertyDescriptor implements IPropertyDescriptor {

	private final PropertyDescriptor descriptor;

	public BeanPropertyDescriptor(PropertyDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public CellEditor createPropertyEditor(Composite parent) {
		return null;
	}

	public String getCategory() {
		return null;
	}

	public String getDescription() {
		return descriptor.getShortDescription();
	}

	public String getDisplayName() {
		return descriptor.getDisplayName();
	}

	public String[] getFilterFlags() {
		if(descriptor.isExpert()) {
			return new String[] { IPropertySheetEntry.FILTER_ID_EXPERT };
		} else {
			return null;
		}
	}

	public Object getHelpContextIds() {
		return null;
	}

	public Object getId() {
		return descriptor.getName();
	}

	public ILabelProvider getLabelProvider() {
		return new ILabelProvider() {
		
			public void removeListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub
		
			}
		
			public boolean isLabelProperty(Object element, String property) {
				// TODO Auto-generated method stub
				return false;
			}
		
			public void dispose() {
				// TODO Auto-generated method stub
		
			}
		
			public void addListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub
		
			}
		
			public String getText(Object element) {
				return "" + element;  //$NON-NLS-1$
			}
		
			public Image getImage(Object element) {
				return null;//EclipseImages.getImage(ImageConstants.TABLE);
			}
		
		};
	}

	public boolean isCompatibleWith(IPropertyDescriptor anotherProperty) {
		return false;
	}

	public Object getValue(Object real) {
		try {
			Method readMethod = descriptor.getReadMethod();
			if (readMethod!=null) {
				return readMethod.invoke(real, new Object[0]);
			} else {
				return null;
			}
		}
		catch (Exception e) {
			return null;
		}
	}

}
