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
package org.hibernate.eclipse.mapper.editors.reveng;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.model.IRevEngColumn;
import org.hibernate.eclipse.console.model.IRevEngGenerator;
import org.hibernate.eclipse.console.model.IRevEngParameter;
import org.hibernate.eclipse.console.model.IRevEngPrimaryKey;
import org.hibernate.eclipse.console.model.IRevEngTable;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class TablePropertiesLabelProvider extends LabelProvider {

	public String getText(Object element) {
		String internalText = getInternalText( element );
		if(internalText==null) {
			return "<n/a>";
		} else {
			return internalText;
		}
	}

	private String getInternalText(Object element) {
		if(element instanceof IRevEngTable) {
			IRevEngTable table = (IRevEngTable)element;
			return getLabel( table );			
		} else if(element instanceof IRevEngColumn) {
			return ((IRevEngColumn)element).getName();
		} else if(element instanceof IRevEngPrimaryKey) {
			return "Primary key";
		} else if(element instanceof IRevEngGenerator) {
			return ((IRevEngGenerator)element).getGeneratorClassName();
		} else if ( element instanceof IRevEngParameter ) {
			IRevEngParameter new_name = (IRevEngParameter) element;
			return new_name.getName();
		}
		return super.getText( element );
	}

	private String getLabel(IRevEngTable table) {
		StringBuffer res = new StringBuffer();
		if(table.getCatalog()!=null) {
			res.append(table.getCatalog());
		}

		if(table.getSchema()!=null) {
			if(res.length()!=0) res.append(".");
			res.append(table.getSchema());
		}
		
		if(table.getName()!=null) {
			if(res.length()!=0) res.append(".");
			res.append(table.getName());
		}
		return res.toString();
	}
	
	public Image getImage(Object element) {
		if(element instanceof IRevEngTable) {
			return EclipseImages.getImage(ImageConstants.TABLE);			
		} else if(element instanceof IRevEngColumn) {
			return EclipseImages.getImage(ImageConstants.COLUMN);
		} else if(element instanceof IRevEngParameter) {
			return EclipseImages.getImage(ImageConstants.PARAMETER);
		}
		return null;
	}
}
