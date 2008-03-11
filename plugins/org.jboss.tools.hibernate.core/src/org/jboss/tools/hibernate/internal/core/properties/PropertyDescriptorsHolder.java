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


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.SequencedHashMap;
import org.eclipse.ui.views.properties.IPropertyDescriptor;


/**
 * @author alex
 *
 * Holder for property descriptor list
 */
public class PropertyDescriptorsHolder {
	private SequencedHashMap propertyDescriptors=new SequencedHashMap();
	private Map defaultProperties=new HashMap();
	private static final IPropertyDescriptor[] PROPS={};
	
	public  IPropertyDescriptor[] getPropertyDescriptorArray(){
		return (IPropertyDescriptor[])propertyDescriptors.values().toArray(PROPS);
	}
	
	public IPropertyDescriptor getPropertyDescriptor(Object id){
		return (IPropertyDescriptor)propertyDescriptors.get(id);
	}
	public void addPropertyDescriptor(IPropertyDescriptor pd){
		propertyDescriptors.put(pd.getId(),pd);
	}
	public Object getDefaultPropertyValue(Object id){
		return defaultProperties.get(id);
	}
	public Object setDefaultPropertyValue(Object id, Object value){
		return defaultProperties.put(id,value);
	}
}
