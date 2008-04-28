/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.internal.core.properties;

import java.util.Arrays;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * @author kaa
 * akuzmin@exadel.com
 * Aug 21, 2005
 */
public class ChangeblePropertyDescriptor extends
		AutoChangeblePropertyDescriptor {
	private String[] parentList;
	private String[] valueList;
//	private int valueindex=-1;
	public ChangeblePropertyDescriptor(Object id, String displayName, String[] dependentProperties,String[] parentList,String parentvalue,String[] valueList) {
		super(id, displayName, dependentProperties);
		this.parentList=parentList;
		this.valueList=valueList;
		if (parentvalue!=null)
			autochangeValues(parentvalue);
	}

	// #changed# by Konstantin Mishin on 18.01.2006 fixed for ESORM-485
	public void autochangeValues(Object changedValue) {}
	
	public Object getAutochangeValues(Object changedValue) {
       	return valueList[Arrays.asList(parentList).indexOf(changedValue)-1];        	
	}
	
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.internal.core.properties.AutoChangeblePropertyDescriptor#autochangeValues(java.lang.Object)
//	 */
//	public void autochangeValues(Object changedValue) {
//		if (changedValue instanceof String) 
//		{
//			valueindex=Arrays.asList(parentList).indexOf(changedValue);
//		} 
//	}
//
//	
//
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.internal.core.properties.AdaptablePropertyDescriptor#getEditValue(java.lang.Object)
//	 */
//	public Object getEditValue(Object propertyValue) {
//        if (("".equals(propertyValue) || propertyValue == null) && valueindex>0)
//        {
//        	return valueList[valueindex-1];
//        }
//        else return super.getEditValue(propertyValue);
//	}
	// #changed#
	
	public CellEditor createPropertyEditor(Composite parent) {
		CellEditor editor = new TextCellEditor(parent);
		if (getValidator() != null)
			editor.setValidator(getValidator());
		return editor;
	}
}
