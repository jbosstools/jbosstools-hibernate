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
package org.jboss.tools.hibernate.internal.core.hibernate;

import org.eclipse.ui.views.properties.IPropertySource2;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.hibernate.IToOneMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.ToOneMappingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


/**
 * A simple-point association (ie. a reference to another entity).
 */
public class ToOneMapping extends SimpleValueMapping implements IToOneMapping {
	private static final long serialVersionUID = 1L;
	private String fetchMode;
	protected String referencedPropertyName;
	private String referencedEntityName;

	protected ToOneMapping(IDatabaseTable table) {
		super(table);
	}

	public String getFetchMode() {
		return fetchMode;
	}

	public void setFetchMode(String fetchMode) {
		this.fetchMode=fetchMode;
	}

	public String getReferencedPropertyName() {
		return referencedPropertyName;
	}

	public void setReferencedPropertyName(String string) {
		referencedPropertyName = string;
	}

	public String getReferencedEntityName() {
//akuzmin 26.05.05
		if (referencedEntityName==null)
		{
			//akuzmin 21.09.2005
			if ((getFieldMapping()!=null)&&(getFieldMapping().getPersistentField()!=null))
			setReferencedEntityName(getFieldMapping().getPersistentField().getType());	
		}
		return referencedEntityName;
	}

	public void setReferencedEntityName(String referencedEntityName) {
//		if ((referencedEntityName!=null)&&(getFieldMapping().getPersistentField().getOwnerClass().getProjectMapping().findClass(referencedEntityName)!=null))
		this.referencedEntityName = referencedEntityName;
	}
	public boolean isTypeSpecified() {
		return referencedEntityName!=null;
	}
	
//	akuzmin 21/04/2005	
	public IPropertySource2 getPropertySource()
	{
		BeanPropertySourceBase bp = new BeanPropertySourceBase(this);
		bp.setPropertyDescriptors(getPropertyDescriptorHolder());		
		return bp;
	}
	
//	akuzmin 05/05/2005
	public PropertyDescriptorsHolder getPropertyDescriptorHolder() {
		return ToOneMappingDescriptorsHolder.getInstance(getTable());
	}	
//	akuzmin 03/08/2005	
    public boolean isSimpleValue() {
		return false;
	}

}
