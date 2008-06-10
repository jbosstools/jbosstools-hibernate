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
import org.hibernate.cfg.reveng.JDBCToHibernateTypeHelper;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.mapping.Column;

public class ColumnWorkbenchAdapter extends BasicWorkbenchAdapter {

	public Object[] getChildren(Object o) {		
		return NO_CHILDREN;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return EclipseImages.getImageDescriptor(ImageConstants.COLUMN);
	}

	public String getLabel(Object o) {
		Column c = (Column) o;
		return getColumnLabel( c );
	}

	static String getColumnLabel(Column c) {
		String label = c.getName();
		if(c.getSqlTypeCode()!=null) {
			label += " : " + JDBCToHibernateTypeHelper.getJDBCTypeName(c.getSqlTypeCode().intValue()); //$NON-NLS-1$
		}
		return label;
	}

	public Object getParent(Object o) {
		return null;
	}
	
	public boolean isContainer() {
		return false;
	}

}
