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
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor;
import org.jboss.tools.hibernate.core.hibernate.IIdBagMapping;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.AbstractOrmElement;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.IdBagIdentifireMappingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.IdBagMappingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.IdBagMappingDescriptorsHolderWithTable;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


/**
 * An <tt>IdentifierBag</tt> has a primary key consisting of
 * just the identifier column
 */
public class IdBagMapping extends CollectionMapping implements IIdBagMapping {
	private IHibernateKeyMapping identifier;

	public IdBagMapping(IHibernateClassMapping owner) {
		super(owner);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.CollectionMapping#clear()
	 */
	public void clear() {
		super.clear();
		if(identifier!=null) identifier.clear();
	}

	public IHibernateKeyMapping getIdentifier() {
		return identifier;
	}
	public void setIdentifier(IHibernateKeyMapping identifier) {
		if(identifier instanceof AbstractOrmElement){
			((AbstractOrmElement)identifier).setName( "id");
		}
		this.identifier = identifier;
	}
	public final boolean isIdentified() {
		return true;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		if(visitor instanceof IHibernateMappingVisitor) 
		    return ((IHibernateMappingVisitor)visitor).visitIdBagMapping(this,argument);
		return visitor.visitPersistentValueMapping(this,argument);
	}

//	akuzmin 01/06/2005	
	public IPropertySource2 getIdentifierPropertySource()
	{
		BeanPropertySourceBase bp = new BeanPropertySourceBase(this);
		bp.setPropertyDescriptors(getIdentifierPropertyDescriptorHolder());		
		return bp;
	}
	
//	akuzmin 01/06/2005
	public PropertyDescriptorsHolder getIdentifierPropertyDescriptorHolder() {
		return IdBagIdentifireMappingDescriptorsHolder.getInstance(getCollectionTable());
	}
	
//	akuzmin 01/06/2005
	public void setTypeByString(String type) {
		if (identifier!=null)
			if ((Type.getType(type)!=null)||(identifier.getFieldMapping().getPersistentField().getOwnerClass().getProjectMapping().findClass(type)!=null))
				identifier.setType(Type.getOrCreateType(type));
			if (identifier.getColumnSpan()==1)
			{
			  Column col=(Column)identifier.getColumnIterator().next();
			  col.setSqlTypeCode(identifier.getType().getSqlType());
			}

		}
//	akuzmin 01/06/2005	
	public String getTypeByString() {
		if (identifier!=null)
		{
			if (identifier.getType()==null)
			{
				if ((identifier.getFieldMapping()==null) ||
				(identifier.getFieldMapping().getPersistentField()==null) ||		
				(identifier.getFieldMapping().getPersistentField().getType()==null))
					return null;
				setType(Type.getOrCreateType(identifier.getFieldMapping().getPersistentField().getType()));			
			}
			if (identifier.getType()==null)
				return null;
			else return identifier.getType().getName();
		}
		else return null;
	}
	
//	akuzmin 01/06/2005	
	public String getGeneratorStrategy() {
		if (identifier!=null)
		{
			return ((SimpleValueMapping)identifier).getIdentifierGeneratorStrategy();
		}
		else return null;
	}
	
//	akuzmin 07/07/2005
	public PropertyDescriptorsHolder getPropertyDescriptorHolder() {
		return IdBagMappingDescriptorsHolderWithTable.getInstance(getFieldMapping().getPersistentField());
	}
//  akuzmin 22/09/2005	
	public PropertyDescriptorsHolder getPropertyDescriptorHolderWithOutTable() {
		return IdBagMappingDescriptorsHolder.getInstance();
	}

	
	//akuzmin 12.07.2005
	public String getIdentifierColumn() {
		if ((identifier!=null)&&(identifier.getColumnIterator().hasNext()))
		{
		Column fkcolumn=(Column) identifier.getColumnIterator().next();
		if (identifier.getTable().getColumn(fkcolumn.getName())!=null)
			return fkcolumn.getName();
		}
		return "";
	}
	
	//akuzmin 12.07.2005
	public void setIdentifierColumn(String keyColumn) {
		if (identifier instanceof SimpleValueMapping)
		{
			if ((keyColumn!=null)&&(!keyColumn.equals("")))
			if (identifier.getColumnSpan()==1)
			{
			  Column col=(Column)identifier.getColumnIterator().next();
				if (identifier.getTable().getColumn(keyColumn)==null)
				{
					identifier.getTable().renameColumn(col,keyColumn);
				}	
				else
				{
					((SimpleValueMapping)identifier).removeColumn(col);
					((SimpleValueMapping)identifier).addColumn(identifier.getTable().getColumn(keyColumn));
				}

			}
			else 
				if (identifier.getColumnSpan()==0) ((SimpleValueMapping)identifier).addColumn(identifier.getTable().getOrCreateColumn(keyColumn));
		}
	}

	
	
}
