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

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;

/**
 * @author alex
 *
 * Wrapper for property source that can expose subset of property descriptors 
 */
public class PropertySourceWrapper implements IPropertySource2 {

	private IPropertySource2 base;
	private PropertyDescriptorsHolder propertyDescriptors;
	//akuzmin 15.07.2005
	private boolean dirty;
	
	public PropertySourceWrapper(IPropertySource2 base, PropertyDescriptorsHolder propertyDescriptors){
		this.base=base;
		this.propertyDescriptors=propertyDescriptors;
	}
	public PropertySourceWrapper(IPropertySource2 base, String[] propertyNames){
		this.base=base;
		this.propertyDescriptors=new PropertyDescriptorsHolder();
		IPropertyDescriptor pd[]=base.getPropertyDescriptors();
		for(int i=0;i<pd.length;++i){
			for(int j=0;j<propertyNames.length;++j)
				if(pd[i].getId().equals( propertyNames[j]))propertyDescriptors.addPropertyDescriptor(pd[i]); 
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource2#isPropertyResettable(java.lang.Object)
	 */
	public boolean isPropertyResettable(Object id) {
		return base.isPropertyResettable(id);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue() {
		return base.getEditableValue();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors(){
		return propertyDescriptors.getPropertyDescriptorArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		return base.getPropertyValue(id);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id) {
		return base.isPropertySet(id);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id) {
		base.resetPropertyValue(id);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		//akuzmin 15.07.2005
		Object oldValue=getPropertyValue(id);
		
		base.setPropertyValue(id,value);
		
		//akuzmin 15.07.2005
		if(oldValue==value || (oldValue!=null && oldValue.equals(value)))return;
		dirty=true;

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
	 * 	akuzmin 15.07.2005
	 */
	public boolean isDirty() {
		return dirty;
	}
	/**
	 * @param dirty The dirty to set.
	 * 	akuzmin 15.07.2005
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
}
