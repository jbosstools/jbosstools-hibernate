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

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.ComboBoxLabelProvider;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IPersistentField;

public class DBTablePropertyDescriptor extends AutoChangeblePropertyDescriptor {
	protected IPersistentClassMapping pcm;
	protected IPersistentField field;
	public DBTablePropertyDescriptor(Object id, String displayName,String[] dependentProperties,IPersistentClassMapping pcm){
		super(id,displayName,dependentProperties);
		this.pcm=pcm;
		autochangeValues(pcm);
	}

	public DBTablePropertyDescriptor(Object id, String displayName,String[] dependentProperties,IPersistentClassMapping pcm,IPersistentField field){
		super(id,displayName,dependentProperties);
		this.pcm=pcm;
		autochangeValues(pcm);
		this.field=field;
	}
	
	public Object getEditValue(Object propertyValue){
		for(int i=0;i<getPropertyValues().length;++i)
			if(getPropertyValues()[i]==propertyValue || (propertyValue!=null && propertyValue.equals(getPropertyValues()[i])))return new Integer(i);
		return new Integer(0);
	}
	
	public Object getPropertyValue(Object editValue){
		if(editValue instanceof Integer){
			return getPropertyValues()[((Integer)editValue).intValue()];
		}
		return null;
	}
	
	/**
	 * 
	 */
	public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new ComboBoxCellEditor(parent, getValues(), 
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
            return new ComboBoxLabelProvider(getValues());
    }

	public void autochangeValues(Object changedValue) {
		String values[];
		Object[] propertyValues;
		Object[] tempobj;

		if (changedValue instanceof IPersistentClassMapping)
		{
			pcm=(IPersistentClassMapping)changedValue;
			if (pcm.getStorage()!=null)
				tempobj=pcm.getStorage().getProjectMapping().getDatabaseTables();
			else if (pcm.getDatabaseTable()!=null)
				   tempobj=pcm.getDatabaseTable().getSchema().getDatabaseTables();
				else
				{
					values=new String[1];
					values[0]="";
					propertyValues=new Object[1];
					propertyValues[0]=null;
					setValues(values,propertyValues);			
					return;
				}
			ArrayList<String> tempindexlist=new ArrayList<String>();
			values=new String[tempobj.length+1];
			values[0]="";
			tempindexlist.add(values[0]);
			for(int i=1;i<=tempobj.length;tempindexlist.add(values[i]),i++)
				values[i]=((IDatabaseTable)tempobj[i-1]).getName();
			Arrays.sort(values);
			propertyValues=new Object[tempobj.length+1];
			propertyValues[0]=null;
			for(int i=1;i<values.length;i++)
				propertyValues[i]=tempobj[tempindexlist.indexOf(values[i])-1];
			setValues(values,propertyValues);
		}
	}

	public boolean autoChangeModel() {
		return field!=null;
	}

}
