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
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.model.ITableFilter;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class TableFilterLabelProvider extends LabelProvider implements ITableLabelProvider {

	//		 Names of images used to represent checkboxes	
	public static final String CHECKED_IMAGE 	= "checked"; //$NON-NLS-1$
	public static final String UNCHECKED_IMAGE  = "unchecked"; //$NON-NLS-1$
	
	
	public Image getColumnImage(Object element, int columnIndex) {
		if(columnIndex==0) {
			ITableFilter tf = (ITableFilter)element;
			if(tf.getExclude()!=null) {
				String key = tf.getExclude().booleanValue() ? ImageConstants.CLOSE : null ; // TODO: find a better image
				return EclipseImages.getImage(key);
			} else {
				return null;
			}
			
		}
		return  null;
	}

	
	public String getColumnText(Object element, int columnIndex) {
		ITableFilter tf = (ITableFilter) element;
		String result = ""; //$NON-NLS-1$
		
		switch (columnIndex) {
		case 0:
			return result;
		case 1:
			return tf.getMatchCatalog();
		case 2: 
			return tf.getMatchSchema();
		case 3: 
			return tf.getMatchName();
		default:
			return result;
		}			
	}
}