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
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;

public class PrimaryKeyWorkbenchAdapter extends BasicWorkbenchAdapter {

	public Object[] getChildren(Object o) {
		PrimaryKey t = getPrimaryKey( o );

		if(t.getColumnSpan()==1) {
			return NO_CHILDREN;
		} else {
			return t.getColumns().toArray(new Column[0]);
		}
	}

	private PrimaryKey getPrimaryKey(Object o) {
		return (PrimaryKey)o;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return EclipseImages.getImageDescriptor(ImageConstants.COLUMN);
	}

	public String getLabel(Object o) {
		PrimaryKey table = getPrimaryKey(o);
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
