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
import org.jboss.tools.hibernate.internal.core.properties.xpl.EditableListCellEditor;
import org.jboss.tools.hibernate.internal.core.util.StringUtils;

/**
 * 
 * @author eskimo
 *
 */
public class DBColumnPropertyDescriptor extends AutoChangeblePropertyDescriptor {

	protected IDatabaseTable db;
	public DBColumnPropertyDescriptor(Object id, String displayName,String[] dependentProperties,IDatabaseTable db){
		super(id,displayName,dependentProperties);
		this.db=db;
		autochangeValues(db);
	}
	
	public Object getEditValue(Object propertyValue){
		return propertyValue;
	}
	
	public Object getPropertyValue(Object editValue){
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
		if((db!=null)&&((changedValue instanceof IDatabaseTable)||(changedValue instanceof String))){
		if (changedValue instanceof String) db=db.getSchema().getProjectMapping().findTable(StringUtils.hibernateEscapeName((String)changedValue));//added 27/9
		else
		db=(IDatabaseTable)changedValue;	
		if ((db!=null)&&(db.getColumns()!=null))
		{
		propertyValues=new Object[db.getColumns().length+1];;
		values=new String[db.getColumns().length+1];
		values[0]="";
		for(int i=1;i<values.length;i++)
			values[i]=(db.getColumns()[i-1]).getName();
		Arrays.sort(values);
		propertyValues=values;
		}
		else
		{
			values=new String[1];
			values[0]="";
			propertyValues=values;
		}

		setValues(values,propertyValues);
		}
		else if (changedValue==null)
		{
			values=new String[1];
			values[0]="";
			propertyValues=values;			
			setValues(values,propertyValues);			
		}
			
	}
	
}
