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
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.internal.core.properties.xpl.EditableListCellEditor;

/**
 * 
 * @author akuzmin - akuzmin@exadel.com
 * May 25, 2005
 * 
 */
public class PersistentFieldPropertyDescriptor extends AutoChangeblePropertyDescriptor {

	protected IMapping mod;
	protected IPersistentClass pc;
	public PersistentFieldPropertyDescriptor(Object id, String displayName,String[] dependentProperties,IPersistentClass pc){
		super(id,displayName,dependentProperties);
		if (pc==null)
			autochangeValues(null);
		else
		{
		mod=pc.getProjectMapping();
		this.pc=pc;
		autochangeValues(pc.getName());
		}
	}
//	public Object getEditValue(Object propertyValue){
//		for(int i=0;i<getPropertyValues().length;++i)
//			if(getPropertyValues()[i]==propertyValue || (propertyValue!=null && propertyValue.equals(getPropertyValues()[i])))return  new Integer(i); //new Integer(i);getPropertyValues()[i].toString();
//		return new Integer(0);// Integer(0);String("");
//	}
	public Object getEditValue(Object propertyValue){
//		autochangeValues(propertyValue);
		return propertyValue;
	}
	
	public Object getPropertyValue(Object editValue){
		return editValue;
	}

	
//	public Object getPropertyValue(Object editValue){
//		if(editValue instanceof Integer){
//			return getPropertyValues()[((Integer)editValue).intValue()];
//		}
//		return null;
//		
//	}
	
    /**
     * The <code>PersistentFieldPropertyDescriptor</code> implementation of this 
     * <code>IPropertyDescriptor</code> method creates and returns a new
     * <code>ComboBoxCellEditor</code>.
     * <p>
     * The editor is configured with the current validator if there is one.
     * </p>
     */

	public CellEditor createPropertyEditor(Composite parent) {///ww
        CellEditor editor = new EditableListCellEditor(parent, values, SWT.NONE);
        if (getValidator() != null)
            editor.setValidator(getValidator());
        return editor;
    }

    /**
     * The <code>ComboBoxPropertyDescriptor</code> implementation of this 
     * <code>IPropertyDescriptor</code> method returns the value set by
     * the <code>setProvider</code> method or, if no value has been set
     * it returns a <code>ComboBoxLabelProvider</code> created from the 
     * valuesArray of this <code>ComboBoxPropertyDescriptor</code>.
     *
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
		if((changedValue instanceof String)&&(mod!=null)) {
		pc=mod.findClass((String)changedValue);	
		if ((pc!=null)&&(pc.getFields()!=null))
		{
		values=new String[pc.getFields().length+1];
		values[0]="";
		for(int i=0;i<pc.getFields().length;i++)
			values[i+1]=(pc.getFields()[i]).getName();
		Arrays.sort(values);	
		propertyValues=values;
		}
		else
		{
			values=new String[1];
			values[0]="";
			propertyValues=values;			
		}
		//Arrays.sort(values);
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
