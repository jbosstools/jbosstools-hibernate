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

import java.util.Map;

import org.eclipse.ui.views.properties.IPropertySource2;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.hibernate.IAnyMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.AnyMappingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.PropertyMappingForAnyMapingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;



/**
 * A Hibernate "any" type (ie. polymorphic association to
 * one-of-several tables).
 */
public class AnyMapping extends SimpleValueMapping implements IAnyMapping {
	private String identifierTypeName;
	private String metaTypeName = "string";
	private Map metaValues;

	public AnyMapping(IDatabaseTable table) {
		super(table);
	}

	public String getIdentifierType() {
		//akuzmin 02.06.2005
		if (identifierTypeName==null)
			identifierTypeName="string";
		return identifierTypeName;
	}

	public void setIdentifierType(String identifierType) {
		this.identifierTypeName = identifierType;
		//akuzmin 22.07.2005
		if (Type.getType(identifierType)!=null)
		{
			if ((getColumnSpan()>0)&&(getColumnSpan()<=2))
			{
				Column col=(Column)getConstraintColumns().get(0);
				if (getTable().getColumn(col.getName())!=null)
					col.setSqlTypeCode(Type.getOrCreateType(identifierType).getSqlType());
			}
			
		}
	}

	public String getMetaType() {
		return metaTypeName;
	}

	public void setMetaType(String type) {
		metaTypeName = type;
		//akuzmin 22.07.2005		
		if (Type.getType(type)!=null)
		{
			if (getColumnSpan()==2)
			{
				Column col=(Column)getConstraintColumns().get(1);
				if (getTable().getColumn(col.getName())!=null)
					col.setSqlTypeCode(Type.getOrCreateType(type).getSqlType());
			}
			
		}
		
	}

	public Map getMetaValues() {
		return metaValues;
	}

	public void setMetaValues(Map metaValues) {
		this.metaValues = metaValues;
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
		return AnyMappingDescriptorsHolder.getInstance(getTable());
	}	
//	akuzmin 02/06/2005
	public PropertyDescriptorsHolder getPropertyMappingDescriptorHolder() {
		return PropertyMappingForAnyMapingDescriptorsHolder.getInstance();
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		if(visitor instanceof IHibernateMappingVisitor) 
		    return ((IHibernateMappingVisitor)visitor).visitAnyMapping(this,argument);
		return visitor.visitPersistentValueMapping(this,argument);
	}
	
	/**
	 * @return	the secomd Column for mapping
	 * 			akuzmin 02/06/2005
	 */
	public String getMetaTypeColumn()
	{
		if (getColumnSpan()==2)
		{
			Column col=(Column)getConstraintColumns().get(1);
			if (getTable().getColumn(col.getName())!=null)
			return col.getName();
		}
			return "";	
	}
	
	/**
	 * @param metaTypeColumn - the secomd Column for mapping
	 * 		  akuzmin 02/06/2005
	 */
	public void setMetaTypeColumn(String metaTypeColumn)
	{
		if ((getColumnSpan()>0)&&(getColumnSpan()<=2)&&(!getConstraintColumns().contains(metaTypeColumn)))
		{
			if (getColumnSpan()==2)
			{
				if (getTable().getColumn(metaTypeColumn)==null)
				{
					getTable().renameColumn(getTable().getColumn(getMetaTypeColumn()),metaTypeColumn);
				}
				else
				{
					removeColumn(getTable().getColumn(getMetaTypeColumn()));
					addColumn(getTable().getOrCreateColumn(metaTypeColumn));
				}
			}
			else
			addColumn(getTable().getOrCreateColumn(metaTypeColumn));
		}
		
	}
	//akuzmin 02.06.2005	
	public String getMappingColumn() {
		if ((getColumnSpan()>0)&&(getColumnSpan()<=2))
		{
			Column col=(Column)getConstraintColumns().get(0);
			if (getTable().getColumn(col.getName())!=null)
			return col.getName();
		}
		return "";
	}
	
	//akuzmin 02.06.2005
	public void setMappingColumn(String keyColumn) {
		if ((keyColumn!=null)&&(!getConstraintColumns().contains(keyColumn)))
		{
			if (getColumnSpan()>2) return;
			if (getColumnSpan()!=0)
			{
			if (getTable().getColumn(keyColumn)==null)
			{
				if (!keyColumn.trim().equals(""))
					getTable().renameColumn(getTable().getColumn(getMappingColumn()),keyColumn);
				else
					removeColumn(getTable().getColumn(getMappingColumn()));
			}
			else
			{
				removeColumn(getTable().getColumn(getMappingColumn()));
				if (!keyColumn.trim().equals("")) 
					addColumn(getTable().getOrCreateColumn(keyColumn),0);
			}
			}
			else
				if (!keyColumn.trim().equals(""))
					addColumn(getTable().getOrCreateColumn(keyColumn));
		}
	}					
}
						