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

import org.eclipse.jface.resource.DataFormatException;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.springframework.beans.BeanWrapperImpl;

public class CombinedBeanPropertySourceBase implements IPropertySource2 {

	private PropertyDescriptorsHolder propertyDescriptors[];
	private boolean dirty;
	private BeanWrapperImpl beans[];
	private static IPropertyDescriptor[] DESCRIPTORS ={};
	//akuzmin 26.05.05
	private boolean refresh;
	
	public CombinedBeanPropertySourceBase(Object[] beans, PropertyDescriptorsHolder propertyDescriptors[]){
		this.propertyDescriptors=propertyDescriptors;
		this.beans=new BeanWrapperImpl[beans.length];
		for(int i=0;i<beans.length;++i) this.beans[i]=new BeanWrapperImpl(beans[i]);
	}
	
	public boolean isPropertyResettable(Object id) {
		return false;
	}

	public Object getEditableValue() {
		return this;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		ArrayList list=new ArrayList();
		for(int i=0;i<propertyDescriptors.length;++i) {
			IPropertyDescriptor[] descs=propertyDescriptors[i].getPropertyDescriptorArray();
			for(int j=0;j<descs.length;++j) list.add(descs[j]);
		}
		return (IPropertyDescriptor[])list.toArray(DESCRIPTORS);
	}
	
	private int getObjectIndexByPropertyId(Object id){
		for(int i=0;i<propertyDescriptors.length;++i) {
			IPropertyDescriptor pd=propertyDescriptors[i].getPropertyDescriptor(id);
			if(pd!=null) return i;
		}
		return -1;
	}

	public Object getPropertyValue(Object id) {
		int index=getObjectIndexByPropertyId(id);
		if(index<0) return null;
		
		IPropertyDescriptor pd=propertyDescriptors[index].getPropertyDescriptor(id);
		Object value=getProperty(index, (String)id);
		if(pd instanceof AdaptablePropertyDescriptor){
			return ((AdaptablePropertyDescriptor)pd).getEditValue(value);
		}
		return value;
	}
	
	private Object getProperty(int index, String name){
		Object value=beans[index].getPropertyValue(name);
		if(value==null)value = propertyDescriptors[index].getDefaultPropertyValue(name);
		//akuzmin 12.03.05
		if(value instanceof Boolean) return ((Boolean)value).toString();
		if(value instanceof Integer) return ((Integer)value).toString();		
		return value;
	}
	

	public boolean isPropertySet(Object id) {
		return true;
	}

	public void resetPropertyValue(Object id) {

	}

	public void setPropertyValue(Object id, Object value) {
		int index=getObjectIndexByPropertyId(id);
		if(index<0) return; //no such property found
		
		IPropertyDescriptor pd=propertyDescriptors[index].getPropertyDescriptor(id);
		if(pd instanceof AdaptablePropertyDescriptor){
			value=((AdaptablePropertyDescriptor)pd).getPropertyValue(value);
		}
		setProperty(index, (String)id, value);

	}
	private void setProperty(int index, String id, Object value){
		Object oldValue=beans[index].getPropertyValue(id);
//		akuzmin 01.07.2005
		if(oldValue instanceof Integer)
		{
	        try {
				StringConverter.asInt(value.toString());//Lenghttext.getText().
	        } catch (DataFormatException e) {
	        	value=oldValue.toString();
	        }
			
		}
		beans[index].setPropertyValue(id,value);
		
		//akuzmin 04.05.2005
		IPropertyDescriptor pd=propertyDescriptors[index].getPropertyDescriptor(id);
		if(pd instanceof IAutoChangebleParentPropertyDescriptor){
			String[] DependentProperties=((IAutoChangebleParentPropertyDescriptor)pd).getDependentProperties();
			for(int i=0;i<DependentProperties.length;i++)
			{
				IPropertyDescriptor deppd=propertyDescriptors[index].getPropertyDescriptor(DependentProperties[i]);
				if ((deppd!=null) && (deppd instanceof AutoChangeblePropertyDescriptor))
				{
					((AutoChangeblePropertyDescriptor)deppd).autochangeValues(value);
					beans[index].setPropertyValue(deppd.getId().toString(),null);
					((AutoChangeblePropertyDescriptor)deppd).setNewCellEditor(true);//thinking
					setRefresh(true);
				}
					
			}
			if (((IAutoChangebleParentPropertyDescriptor)pd).autoChangeModel())
				setRefresh(true);
		}
		//////////////////////
		
		if(oldValue==value || (oldValue!=null && oldValue.equals(value)))return;
		dirty=true;
	}

	/**
	 * @return Returns the refresh.
	 */
	public boolean isRefresh() {
		return refresh;
	}

	/**
	 * @param refresh The refresh to set.
	 */
	public void setRefresh(boolean refresh) {
		this.refresh = refresh;
	}

	/**
	 * @return Returns the dirty.
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * @param dirty The dirty to set.
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

}
