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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.ComboBoxLabelProvider;
import org.jboss.tools.hibernate.internal.core.properties.xpl.EditableListCellEditor;

/**
 * @author alex
 *
 */
public class EditableListPropertyDescriptor extends ListPropertyDescriptor {
	public EditableListPropertyDescriptor(Object id, String displayName,String[] propertyValues){
		super(id,displayName,propertyValues);
	}
	public Object getEditValue(Object propertyValue){
		return propertyValue;
	}
	
	public Object getPropertyValue(Object editValue){
		return editValue;
	}
	
    /**
     * The <code>ListPropertyDescriptor</code> implementation of this 
     * <code>IPropertyDescriptor</code> method creates and returns a new
     * <code>ComboBoxCellEditor</code>.
     * <p>
     * The editor is configured with the current validator if there is one.
     * </p>
     */
    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new EditableListCellEditor(parent, values, SWT.NONE);
        if (getValidator() != null)
            editor.setValidator(getValidator());
        return editor;
    }
    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet())
            return super.getLabelProvider();
        else
            return new LabelProvider();
    }

}
