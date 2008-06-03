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

import org.eclipse.ui.views.properties.PropertyDescriptor;


/**
 * @author alex
 *
 * Property Descriptor for properties that have different values and editor values
 */
public class AdaptablePropertyDescriptor extends PropertyDescriptor  {

	public AdaptablePropertyDescriptor(Object id, String displayName){
		super(id,displayName);
	}
	public Object getEditValue(Object propertyValue){
		return propertyValue;
	}
	public Object getPropertyValue(Object editValue){
		return editValue;
	}
}
