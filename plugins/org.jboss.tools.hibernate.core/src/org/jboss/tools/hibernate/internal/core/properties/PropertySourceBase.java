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

import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.DataFormatException;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;

import java.util.Properties;

/**
 * @author alex
 *
 * Helper to manage property descriptors for a Properties object
 */
public class PropertySourceBase implements IPropertySource2 {

	private Properties properties=new Properties();
	// #added# by Konstantin Mishin on 24.12.2005 fixed for ESORM-440
	private Properties propertiesClone;
	// #added#
	private PropertyDescriptorsHolder propertyDescriptors;
	
	// tau 26.02.2006
	//private boolean dirty;
	private boolean dirty = false;
	
	private boolean refresh;	
	private String description="";
	
	public Object getEditableValue(){
		
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource2#isPropertyResettable(java.lang.Object)
	 */
	public boolean isPropertyResettable(Object id) {
		return false;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors(){
		
		return propertyDescriptors.getPropertyDescriptorArray();
	}
	

	/* 
	 * Returns property value for editing. Returned value may differ from original property value.
	 * @see getProperty
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		IPropertyDescriptor pd = propertyDescriptors.getPropertyDescriptor(id);
		//getDescriptionPD((String)id);
		Object value=getProperty((String)id);
		if(pd instanceof AdaptablePropertyDescriptor){
			return ((AdaptablePropertyDescriptor)pd).getEditValue(value);
		}
		return value;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id) {
	//	getDescriptionPD((String)id);
	}

	/* 
	 * Set property value after editing. Final property value may differ from given.
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		IPropertyDescriptor pd = propertyDescriptors.getPropertyDescriptor(id);
		if(pd instanceof AdaptablePropertyDescriptor){
			value=((AdaptablePropertyDescriptor)pd).getPropertyValue(value);
		}
		if((pd instanceof IDescriptorWithType)&&(!value.equals(""))){
			Object oldvalue=getProperty((String)id);
			try {
				if (((IDescriptorWithType)pd).isIsint())
				{
						int newvalue=StringConverter.asInt(value.toString());
						if ((((IDescriptorWithType)pd).isUseLeftRange())&&(newvalue<((Integer)((IDescriptorWithType)pd).getLeftRange()).intValue()))
						{
							value=oldvalue;
						}
						if ((((IDescriptorWithType)pd).isUseRightRange())&&(newvalue>((Integer)((IDescriptorWithType)pd).getRightRange()).intValue()))
						{
							value=oldvalue;
						}
				}
				else 
				{
					float newvalue=StringConverter.asFloat(value.toString());
					if ((((IDescriptorWithType)pd).isUseLeftRange())&&(newvalue<((Float)((IDescriptorWithType)pd).getLeftRange()).floatValue()))
					{
						value=oldvalue;
					}
					if ((((IDescriptorWithType)pd).isUseRightRange())&&(newvalue>((Float)((IDescriptorWithType)pd).getRightRange()).floatValue()))
					{
						value=oldvalue;
					}
				}
		     } catch (DataFormatException e) {
			   	value=oldvalue;
		     }
		    
		}
		else if (pd instanceof IDescriptorWithTemplates)
		{
			if (!((IDescriptorWithTemplates)pd).isCorrectValue((String) value))
			    value=((IDescriptorWithTemplates)pd).getCorrectedValue((String) value);
		}
		setProperty((String)id,(String)value);
		//akuzmin 21.08.2005
		if(pd instanceof IAutoChangebleParentPropertyDescriptor){
			String[] DependentProperties=((IAutoChangebleParentPropertyDescriptor)pd).getDependentProperties();
			for(int i=0;i<DependentProperties.length;i++)
			{
				IPropertyDescriptor deppd= propertyDescriptors.getPropertyDescriptor(DependentProperties[i]);
				if ((deppd!=null) && (deppd instanceof ChangeblePropertyDescriptor))
				{
					// #changed# by Konstantin Mishin on 18.01.2006 fixed for ESORM-485
					//((AutoChangeblePropertyDescriptor)deppd).autochangeValues(value);
					//setPropertyValue(deppd.getId().toString(),null);
					//((AutoChangeblePropertyDescriptor)deppd).setNewCellEditor(true);//thinking
					setPropertyValue(deppd.getId().toString(),((ChangeblePropertyDescriptor)deppd).getAutochangeValues(value));
					// #changed#
					setRefresh(true);
				}
					
			}
			if (((IAutoChangebleParentPropertyDescriptor)pd).autoChangeModel())
				setRefresh(true);
		}
		/////////
		

	}

	/**
	 * @param propertyDescriptors The propertyDescriptors to set.
	 */
	public void setPropertyDescriptorsHolder(PropertyDescriptorsHolder propertyDescriptors) {
		this.propertyDescriptors = propertyDescriptors;
	}
	
	/**
	 * Returns iterator of property ids that have been set
	 * */
	public Iterator getPropertyIdIterator(){
		return properties.keySet().iterator();
	}
	
	/**
	 * Return property value by given id. Always returns original value of the property.
	 * */
	public String getProperty(String id){
		Object value=properties.get(id);
		if(value==null)value = propertyDescriptors.getDefaultPropertyValue(id);
		return (String)value;
	}
	/**
	 * Set property value by given id. 
	 * */
	public void setProperty(String id, String value){
		Object defValue= propertyDescriptors.getDefaultPropertyValue(id);
		if(defValue!=null && defValue.equals(value)) value=null;
		Object oldValue=null;
		if(value==null)oldValue=properties.remove(id);
		else oldValue=properties.put(id,value);
		if(oldValue==value || (oldValue!=null && oldValue.equals(value)))return;
		dirty=true;
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
	
	/**
	 * Return properties map 
	 * */
	public Properties getProperties(){
		return properties;
	}
	
	protected void replaceProperties(Map newProperties) {
		properties.clear();
		properties.putAll(newProperties);
	}
	// 10.08.2005 queued for deletion - by Nick
//	public void getDescriptionPD(String id){
//		description="";
//		IPropertyDescriptor pd=propertyDescriptors.getPropertyDescriptor(id);
//		description=pd.getDescription();
//		
//	}
	// by Nick
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
	
	
	public String getDescription(){
		return description;
	}
	
	// #added# by Konstantin Mishin on 24.12.2005 fixed for ESORM-440
	public void cloneProperties(){
		propertiesClone = (Properties)properties.clone();
	}

	public void restoreProperties() {
		if(propertiesClone!=null) {
			properties = propertiesClone;
			propertiesClone=null;
		}
	}
	
	public void resetCloneProperties() {
			propertiesClone=null;
	}
	// #added#
	
}
