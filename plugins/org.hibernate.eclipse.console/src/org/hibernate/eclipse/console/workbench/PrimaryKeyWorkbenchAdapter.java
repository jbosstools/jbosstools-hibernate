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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.jboss.tools.hibernate.spi.IColumn;
import org.jboss.tools.hibernate.spi.IPrimaryKey;

public class PrimaryKeyWorkbenchAdapter implements IWorkbenchAdapter {

	public Object[] getChildren(Object o) {
		IPrimaryKey t = getPrimaryKey( o );

		if(t.getColumnSpan()==1) {
			return new Object[0];
		} else {
			return t.getColumns().toArray(new IColumn[0]);
		}
	}

	private IPrimaryKey getPrimaryKey(Object o) {
		return (IPrimaryKey)o;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return EclipseImages.getImageDescriptor(ImageConstants.COLUMN);
	}

	public String getLabel(Object o) {
		IPrimaryKey table = getPrimaryKey(o);
		if(table.getColumnSpan()==1) {
			return ColumnWorkbenchAdapter.getColumnLabel(table.getColumn(0));
		} else {
			return HibernateConsoleMessages.PrimaryKeyWorkbenchAdapter_composite_primary_key;
		}
	}

	public Object getParent(Object o) {
		return getPrimaryKey(o).getTable();
	}

}
