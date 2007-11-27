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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.internal.core.properties.xpl.EditableListCellEditor;

/**
 * 
 * @author akuzmin - akuzmin@exadel.com May 25, 2005
 * 
 */
public class PersistentClassPropertyDescriptor extends
		AutoChangeblePropertyDescriptor {

	protected IMapping mod;

	private boolean isChangeModel;

	public PersistentClassPropertyDescriptor(Object id, String displayName,
			String[] dependentProperties, IMapping mod, boolean isChangeModel) {
		super(id, displayName, dependentProperties);
		this.mod = mod;
		this.isChangeModel = isChangeModel;
		autochangeValues(mod);
	}

	public Object getEditValue(Object propertyValue) {
		return propertyValue;
	}

	public Object getPropertyValue(Object editValue) {
		return editValue;
	}

	public CellEditor createPropertyEditor(Composite parent) {// /ww
		CellEditor editor = new EditableListCellEditor(parent, values, SWT.NONE);
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
			return new LabelProvider();
	}

	public void autochangeValues(Object changedValue) {
		String values[];
		Object[] propertyValues;
		if (changedValue instanceof IMapping) {
			mod = (IMapping) changedValue;
			if (mod.getPertsistentClasses() != null) {
				values = new String[mod.getPertsistentClasses().length];
				for (int i = 0; i < mod.getPertsistentClasses().length; i++)
					values[i] = (mod.getPertsistentClasses()[i]).getName();
				Arrays.sort(values);
				propertyValues = values;
			} else {
				values = new String[1];
				values[0] = "";
				propertyValues = values;
			}
			setValues(values, propertyValues);
		} else if (changedValue == null) {
			values = new String[1];
			values[0] = "";
			propertyValues = values;
			setValues(values, propertyValues);
		}
	}

	public boolean autoChangeModel() {
		return isChangeModel;
	}

}
