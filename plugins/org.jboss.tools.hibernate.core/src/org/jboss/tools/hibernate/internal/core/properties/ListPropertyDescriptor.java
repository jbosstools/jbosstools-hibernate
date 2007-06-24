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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.ComboBoxLabelProvider;

/**
 * @author alex
 *
 */
public class ListPropertyDescriptor extends AdaptablePropertyDescriptor {
	protected String values[];
	protected Object[] propertyValues;
	
	public ListPropertyDescriptor(Object id, String displayName,String viewValues[],Object[] propertyValues){
		super(id,displayName);
		values=viewValues;
		this.propertyValues=propertyValues;
	}
	public ListPropertyDescriptor(Object id, String displayName,String[] propertyValues){
		super(id,displayName);
		values=propertyValues;
		this.propertyValues=propertyValues;
	}
	
	public Object getEditValue(Object propertyValue){
		for(int i=0;i<propertyValues.length;++i)
			if(propertyValues[i]==propertyValue || (propertyValue!=null && propertyValue.equals(propertyValues[i])))return new Integer(i);
		return new Integer(0);
	}
	
	public Object getPropertyValue(Object editValue){
		if(editValue instanceof Integer){
			return propertyValues[((Integer)editValue).intValue()];
		}
		return editValue;
	}
	
    /**
     */
    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new ComboBoxCellEditor(parent, values,
                SWT.READ_ONLY);
        if (getValidator() != null)
            editor.setValidator(getValidator());
        return editor;
    }

    /**
     * @see #setLabelProvider
     */
    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet())
            return super.getLabelProvider();
        else
            return new ComboBoxLabelProvider(values);
    }

}
