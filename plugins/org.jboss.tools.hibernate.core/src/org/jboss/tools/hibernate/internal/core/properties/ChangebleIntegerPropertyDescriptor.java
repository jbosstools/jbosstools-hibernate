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

import org.eclipse.jface.resource.DataFormatException;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;


/**
 * @author kaa
 * 01.09.2005
 * Used for column properties for fields length,precision,scale
 */
public class ChangebleIntegerPropertyDescriptor extends
		AutoChangeblePropertyDescriptor {
	private int defvalue=-1;
	private String newvalue;
	private Object techvalue;
	private boolean resetdependet;//if true all dependet properties reset to defvalue	
	public ChangebleIntegerPropertyDescriptor(Object id, String displayName, String[] dependentProperties, int defvalue,boolean resetdependet) {
		super(id, displayName, dependentProperties);
		this.defvalue=defvalue;
		this.resetdependet=resetdependet;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.properties.AutoChangeblePropertyDescriptor#autochangeValues(java.lang.Object)
	 */
	public void autochangeValues(Object changedValue) {
		if ((changedValue instanceof String)&&(resetdependet)) 
		{
			newvalue=((String)changedValue);
				if ((techvalue==null)||(!(techvalue instanceof Integer))||(!isValidValue(techvalue)))
					if (isValidValue(newvalue))
						setTechvalue(new Integer(StringConverter.asInt(newvalue)));	
					else if (StringConverter.asInt(newvalue)==defvalue)
					{
						newvalue=(new Integer(defvalue+1)).toString();
						setTechvalue(new Integer(StringConverter.asInt(newvalue)));
					}
				
		}
		else newvalue=null;
	}

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new TextCellEditor(parent);
        if (getValidator() != null)
            editor.setValidator(getValidator());
        return editor;
    }

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.properties.AdaptablePropertyDescriptor#getEditValue(java.lang.Object)
	 */

	public Object getEditValue(Object propertyValue) {
        	if (!isValidValue(propertyValue))
        		if (newvalue!=null)
        			{
        			setTechvalue(new Integer(StringConverter.asInt(newvalue)));
        			if (isValidValue(newvalue))
        				return newvalue;
        			else return "";
        			}
        		else
        		return "";
        	else
        	{
        		if ((techvalue==null)||(!(techvalue instanceof Integer))||(!isValidValue(techvalue)))
    				if (isValidValue(propertyValue))
    					setTechvalue(new Integer(StringConverter.asInt(propertyValue.toString())));	

        		return super.getEditValue(propertyValue);
        	}
	}

	/**
	 * @return Returns the techvalue.
	 */
	public Object getTechvalue() {
		return techvalue;
	}

	/**
	 * @param value The techvalue to set.
	 */
	public void setTechvalue(Object value) {
			techvalue = value;
	}

	/**
	 * @return Returns the resetdependet.
	 */
	public boolean isResetdependet() {
		return resetdependet;
	}

	/**
	 * @return Returns the defvalue.
	 */
	public Object getDefvalue() {
		return new Integer(defvalue);
	}

	/**
	 * @param object The newvalue to set.
	 */
	public void setNewvalue(Object value) {
		if (value instanceof String)
			this.newvalue = (String)value;
		else if (value instanceof Integer)
				this.newvalue = ((Integer)value).toString();
	}

	public boolean isValidValue(Object value) {
      	try {
    	if ((value instanceof String)&&(StringConverter.asInt((String)value.toString())>defvalue))
    		return true;
    	else 
    	if ((value instanceof Integer)&&(((Integer)value).intValue()>defvalue))
    		return true;
    	return false;
      	} catch (DataFormatException e) {
      		return false;//.toString();
      	}    	
	}
	
}
