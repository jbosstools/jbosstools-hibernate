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
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.internal.core.properties.xpl.EditableListCellEditor;

/**
 * @author kaa - akuzmin@exadel.com Jul 4, 2005
 */
public class DBTableEditablePropertyDescriptor extends
		AutoChangeblePropertyDescriptor {
	protected IPersistentClassMapping pcm;

	protected IPersistentField field;

	public DBTableEditablePropertyDescriptor(Object id, String displayName,
			String[] dependentProperties, IPersistentClassMapping pcm) {
		super(id, displayName, dependentProperties);
		this.pcm = pcm;
		autochangeValues(pcm);
	}

	public DBTableEditablePropertyDescriptor(Object id, String displayName,
			String[] dependentProperties, IPersistentClassMapping pcm,
			IPersistentField field) {
		super(id, displayName, dependentProperties);
		this.pcm = pcm;
		autochangeValues(pcm);
		this.field = field;
	}

	public Object getEditValue(Object propertyValue) {
		return propertyValue;
	}

	public Object getPropertyValue(Object editValue) {
		return editValue;

	}

	/**
	 * 
	 */
	public CellEditor createPropertyEditor(Composite parent) {
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
		Object[] tempobj;

		if (changedValue instanceof IPersistentClassMapping) {
			pcm = (IPersistentClassMapping) changedValue;
			if (pcm.getStorage() != null)
				tempobj = pcm.getStorage().getProjectMapping()
						.getDatabaseTables();
			else if (pcm.getDatabaseTable() != null)
				tempobj = pcm.getDatabaseTable().getSchema()
						.getDatabaseTables();
			else {
				values = new String[1];
				values[0] = "";
				propertyValues = values;
				setValues(values, propertyValues);
				return;
			}
			values = new String[tempobj.length + 1];
			values[0] = "";
			for (int i = 1; i <= tempobj.length; i++)
				values[i] = ((IDatabaseTable) tempobj[i - 1]).getName();
			Arrays.sort(values);
			propertyValues = values;
			setValues(values, propertyValues);
		}
	}

	public boolean autoChangeModel() {
		return field != null;
	}

}
