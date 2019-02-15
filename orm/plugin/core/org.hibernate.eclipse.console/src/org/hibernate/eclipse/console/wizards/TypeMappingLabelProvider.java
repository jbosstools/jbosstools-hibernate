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
package org.hibernate.eclipse.console.wizards;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.hibernate.eclipse.console.model.ITypeMapping;

public class TypeMappingLabelProvider extends LabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		return  null;
	}

	public String getColumnText(Object element, int columnIndex) {
		ITypeMapping tf = (ITypeMapping) element;
		String result = ""; //$NON-NLS-1$
		
		switch (columnIndex) {
		case 0:
			return tf.getJDBCType();
		case 1: 
			return tf.getHibernateType();
		case 2: 
			return safeToString(tf.getLength());
		case 3:
			return safeToString(tf.getScale());
		case 4: 
			return safeToString(tf.getPrecision());
		case 5: 
			return safeToString(tf.getNullable());		
		default:
			return result;
		}			
	}

	private String safeToString(Object length) {
		if(length==null) return ""; //$NON-NLS-1$
		return length.toString();
	}
}