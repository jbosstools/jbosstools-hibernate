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
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.springframework.beans.BeanWrapperImpl;

/**
 * @author alex
 *
 * Helper to manage property descriptors for Java bean object
 */
public class BeanPropertySourceBase implements IPropertySource2 {
	private PropertyDescriptorsHolder propertyDescriptors;
	private boolean dirty;
	private boolean refresh;	
	private BeanWrapperImpl bean;
	
	public BeanPropertySourceBase(Object bean){
		this.bean=new BeanWrapperImpl(bean);
	}
	
	protected BeanPropertySourceBase(){
		this.bean=new BeanWrapperImpl(this);
	}
	
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
		IPropertyDescriptor pd=propertyDescriptors.getPropertyDescriptor(id);
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
		
		
		
	}

	/* 
	 * Set property value after editing. Final property value may differ from given.
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		IPropertyDescriptor pd=propertyDescriptors.getPropertyDescriptor(id);
		if(pd instanceof AdaptablePropertyDescriptor){
			value=((AdaptablePropertyDescriptor)pd).getPropertyValue(value);
		}
		setProperty((String)id,value);
	}

	/**
	 * @param propertyDescriptors The propertyDescriptors to set.
	 */
	public void setPropertyDescriptors(
			PropertyDescriptorsHolder propertyDescriptors) {
		this.propertyDescriptors = propertyDescriptors;
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
	public Object getProperty(String name){
		Object value=bean.getPropertyValue(name);
		if(value==null)value = propertyDescriptors.getDefaultPropertyValue(name);
		//akuzmin 12.03.05
		if(value instanceof Boolean) return ((Boolean)value).toString();
		if(value instanceof Integer) return ((Integer)value).toString();		
		return value;
	}
	/**
	 * Set property value by given id. 
	 * */
	public void setProperty(String id, Object value){//value-database ;id=collectiontable
		Object oldValue=bean.getPropertyValue(id);
		Class type=bean.getPropertyType(id); // 20050721 yan
//		akuzmin 01.07.2005
		
		// 20050727 <yan>
		IPropertyDescriptor pd=propertyDescriptors.getPropertyDescriptor(id);		
		if (type.isPrimitive()) {
			
			if (type.isAssignableFrom(Integer.TYPE)) {// 20050721 yan
				
				if (value==null || value.toString().length()==0) {
		      	//akuzmin 
					if(pd instanceof ChangebleIntegerPropertyDescriptor)
						value=new Integer(-1);
					else
						value=oldValue;
		      	
		      } else {
		      	try {
						StringConverter.asInt(value.toString());//Lenghttext.getText().
		      	} catch (DataFormatException e) {
			      	value=oldValue;//.toString();
		      	}
		      	
		      }
				
			}
				
		} else if(type.isAssignableFrom(Integer.class)) {// 20050721 yan
	      
			if (value==null || value.toString().length()==0) {
	      	
	      	value=null;
	      	
	      } else {
	      	try {
					StringConverter.asInt(value.toString());//Lenghttext.getText().
	      	} catch (DataFormatException e) {
		      	value=oldValue;//.toString();
	      	}
	      	
	      }
			
			
			
		}
		// </yan>
		bean.setPropertyValue(id,value);
		
		
		//akuzmin 04.05.2005
		if(pd instanceof ChangebleIntegerPropertyDescriptor)
			((ChangebleIntegerPropertyDescriptor)pd).setTechvalue(value);
		if(pd instanceof IAutoChangebleParentPropertyDescriptor){
			String[] DependentProperties=((IAutoChangebleParentPropertyDescriptor)pd).getDependentProperties();
			for(int i=0;i<DependentProperties.length;i++)
			{
				IPropertyDescriptor deppd=propertyDescriptors.getPropertyDescriptor(DependentProperties[i]);
				if ((deppd!=null) && (deppd instanceof AutoChangeblePropertyDescriptor))
				{
					((AutoChangeblePropertyDescriptor)deppd).autochangeValues(value);
					if (deppd instanceof ChangebleIntegerPropertyDescriptor)
					{
//						if ((value instanceof Integer)&&(((Integer)value).equals(new Integer(-1)))&& (((ChangebleIntegerPropertyDescriptor)deppd).getTechvalue()!=null))
//						{
//								bean.setPropertyValue(deppd.getId().toString(),((ChangebleIntegerPropertyDescriptor)deppd).getTechvalue());
//						}
//						else 
							if ((pd instanceof ChangebleIntegerPropertyDescriptor)&&(((ChangebleIntegerPropertyDescriptor)pd).isResetdependet()))
							{
								if (((ChangebleIntegerPropertyDescriptor)pd).isValidValue(value))
								{
									((ChangebleIntegerPropertyDescriptor)deppd).setNewvalue(((ChangebleIntegerPropertyDescriptor)deppd).getDefvalue());
									bean.setPropertyValue(deppd.getId().toString(),((ChangebleIntegerPropertyDescriptor)deppd).getDefvalue());
								}
//								else if (((ChangebleIntegerPropertyDescriptor)deppd).getTechvalue()!=null)
//										bean.setPropertyValue(deppd.getId().toString(),((ChangebleIntegerPropertyDescriptor)deppd).getTechvalue());

							}
							else
							{
							if (((ChangebleIntegerPropertyDescriptor)deppd).getTechvalue()!=null)
								bean.setPropertyValue(deppd.getId().toString(),((ChangebleIntegerPropertyDescriptor)deppd).getTechvalue());
							if (((ChangebleIntegerPropertyDescriptor)deppd).isResetdependet())
							{
								String[] resetableProperties=((IAutoChangebleParentPropertyDescriptor)deppd).getDependentProperties();
								for(int z=0;z<resetableProperties.length;z++)
								{
									IPropertyDescriptor resprop=propertyDescriptors.getPropertyDescriptor(resetableProperties[i]);
									if ((resprop!=null) && (resprop instanceof ChangebleIntegerPropertyDescriptor))
									{
										((ChangebleIntegerPropertyDescriptor)resprop).setNewvalue(((ChangebleIntegerPropertyDescriptor)resprop).getDefvalue());
										bean.setPropertyValue(resprop.getId().toString(),((ChangebleIntegerPropertyDescriptor)resprop).getDefvalue());
									}
									
								}
							}
							}

					}
					else
						bean.setPropertyValue(deppd.getId().toString(),null);
					((AutoChangeblePropertyDescriptor)deppd).setNewCellEditor(true);//thinking
					setRefresh(true);
				}
					
			}
			if (((IAutoChangebleParentPropertyDescriptor)pd).autoChangeModel())
				setRefresh(true);
		}
		/////////
		
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
	
}
