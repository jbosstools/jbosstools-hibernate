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

public abstract class AutoChangeblePropertyDescriptor extends
		AdaptablePropertyDescriptor implements IAutoChangebleParentPropertyDescriptor{
	protected String values[];
	private String dependentProperties[]={};	
	private Object[] propertyValues;
	private boolean newCellEditor;

/*	To use descriptor wich will chaging you'll have  
 *  to use this listner
 *  BeanPropertySourceBase bp;
 *  ..........
 * page.getControl().addMouseListener(
			new MouseAdapter(){

		public void mouseDown(MouseEvent e) {
			if (bp.isRefresh())
			{
				FormList();//reforming bean and put it into page
				//by page.selectionChanged(null, new StructuredSelection(bp)); 
				bp.setRefresh(false);
			}
		}

});*/	
	public AutoChangeblePropertyDescriptor(Object id, String displayName, String[] dependentProperties) {
		super(id, displayName);
		if (dependentProperties!=null)
		this.dependentProperties=dependentProperties;
	}
	public void setValues(String[] values, Object[] propertyValues)
	{
		this.values=values;
		this.propertyValues=propertyValues;		
	}

	public Object[] getPropertyValues()
	{
		return propertyValues;		
	}

	public String[] getValues()
	{
		return values;		
	}

	public String[] getDependentProperties()
	{
		return dependentProperties;		
	}
	
	public abstract void autochangeValues(Object changedValue);
	public boolean isNewCellEditor() {
		return newCellEditor;
	}
	public void setNewCellEditor(boolean newCellEditor) {
		this.newCellEditor = newCellEditor;
	}
	/**
	 * Do something in model if need
	 */
	public boolean autoChangeModel() {
		return false;
	}
	
}
