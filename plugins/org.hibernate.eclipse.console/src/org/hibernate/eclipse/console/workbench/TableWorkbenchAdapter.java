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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;

public class TableWorkbenchAdapter extends BasicWorkbenchAdapter {

	public Object[] getChildren(Object o) {
		Table t = getTable( o );
		
		List<Object> items = new ArrayList<Object>();
		
		PrimaryKey primaryKey = t.getPrimaryKey();
		if(primaryKey!=null) {
			items.add(primaryKey);			
		}
		
		Iterator<?> columnIterator = t.getColumnIterator();
		while ( columnIterator.hasNext() ) {
			Column col = (Column) columnIterator.next();
			if(primaryKey==null || !primaryKey.containsColumn(col)) {
				items.add(col); // only add non-pk columns here
			}			
		}
		
		return items.toArray(new Object[items.size()]);
	}

	private Table getTable(Object o) {
		return (Table) o;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return EclipseImages.getImageDescriptor(ImageConstants.TABLE);
	}

	public String getLabel(Object o) {
		Table table = getTable(o);
		//return Table.qualify(table.getCatalog(), table.getSchema(), table.getName(), '.');
		return table.getName();
	}

	public Object getParent(Object o) {
		return null;
	}

}
