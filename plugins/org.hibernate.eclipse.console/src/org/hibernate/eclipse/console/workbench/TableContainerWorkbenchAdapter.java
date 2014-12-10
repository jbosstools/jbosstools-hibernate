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

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.jboss.tools.hibernate.spi.ITable;

public class TableContainerWorkbenchAdapter implements IWorkbenchAdapter {

	public Object[] getChildren(Object o) {
		TableContainer tc = getTableContainer( o );
		List<ITable> list = tc.getTables();
		Object[] children = BasicWorkbenchAdapter.toArray(list.iterator(), Object.class, new Comparator<Object>() {
			public int compare(Object arg0, Object arg1) {
				return getName(arg0).compareTo(getName(arg1));
			}
			private String getName(Object o) {
				String result = null;
				try {
					Method m = o.getClass().getMethod("getName", new Class[] {}); //$NON-NLS-1$
					result = (String)m.invoke(o, new Object[] {});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return result;
			}
		});
		return children;
	}

	private TableContainer getTableContainer(Object o) {
		return (TableContainer) o;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return EclipseImages.getImageDescriptor(ImageConstants.SCHEMA);
	}

	public String getLabel(Object o) {
		String name = getTableContainer(o).getName();
		return "".equals(name)?HibernateConsoleMessages.TableContainerWorkbenchAdapter_default:name; //$NON-NLS-1$
	}

	public Object getParent(Object o) {
		return null;
	}

}
