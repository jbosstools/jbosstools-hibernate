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

import java.util.Collections;
import java.util.Iterator;

import org.eclipse.ui.views.properties.IPropertySource2;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor;
import org.jboss.tools.hibernate.core.hibernate.IHibernateValueMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.core.hibernate.IRootClassMapping;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.RootClassMappingPropertyDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;


/**
 * @author alex
 *
 * The root class of an inheritance hierarchy
 */
public class RootClassMapping extends ClassMapping implements IRootClassMapping {
	private static final long serialVersionUID = 1L;
	private IPropertyMapping identifierProperty; //may be final
	private IHibernateKeyMapping identifier; //may be final
	private IPropertyMapping version; //may be final
	private boolean polymorphic;
	private String cacheConcurrencyStrategy;
	private String cacheRegionName;
	private IHibernateValueMapping discriminator; //may be final
	private boolean mutable = true;
	private boolean embeddedIdentifier = false; // may be final
	private boolean explicitPolymorphism;
	private boolean forceDiscriminator = false;
	private String where;
	private IDatabaseTable table;
	private boolean discriminatorInsertable = true;
	
	private boolean dirtyCacheConcurrencyStrategy = false;	
	
	
	public IDatabaseTable getDatabaseTable() {
		return table;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClassMapping#setDatabaseTable(org.jboss.tools.hibernate.core.IDatabaseTable)
	 */
	public void setDatabaseTable(IDatabaseTable table) {
		this.table=table;

	}
	
	//akuzmin 04.07.2005
	public String getDatabaseTableByName() {
		if (table!=null)
			return table.getName();
		else return "";
	}

	/* 
	 * akuzmin 04.07.2005
	 */
	public void setDatabaseTableByName(String TableName) {
		if ((TableName!=null)&&(!TableName.trim().equals("")))
		{
			if (table!=null)
			{
				if ((table.getSchema()!=null)&&(table.getSchema().getProjectMapping()!=null))
					table.getSchema().getProjectMapping().renameTable(table,TableName);
				else if ((getStorage()!=null)&&(getStorage().getProjectMapping()!=null))
					getStorage().getProjectMapping().renameTable(table,TableName);
			}
			else
			{
				if ((getStorage()!=null)&&(getStorage().getProjectMapping()!=null))
					getStorage().getProjectMapping().getOrCreateTable(TableName);
			}
		}

	}
	
	public IPropertyMapping getIdentifierProperty() {
		return identifierProperty;
	}
	
	public void setIdentifierProperty(IPropertyMapping idMapping) {
		identifierProperty=idMapping;
	}
	
	public void setIdentifier(IHibernateKeyMapping identifier) {
		this.identifier = identifier;
	}

	public IHibernateKeyMapping getIdentifier() {
		return identifier;
	}
	public boolean hasIdentifierProperty() {
		return identifierProperty!=null;
	}

	public IHibernateValueMapping getDiscriminator() {
		return discriminator;
	}

	//akuzmin 03.05.2005
	public String getDiscriminatorColumnName() {
		if ((discriminator!=null)&&(discriminator.getColumnIterator().hasNext()))
		return ((Column)discriminator.getColumnIterator().next()).getName();
		else return "";
	}
	
	
	public boolean isInherited() {
		return false;
	}
	public boolean isPolymorphic() {
		return polymorphic;
	}

	public void setPolymorphic(boolean polymorphic) {
		this.polymorphic = polymorphic;
	}

	public IRootClassMapping getRootClass() {
		return this;
	}

	public Iterator<IPropertyMapping> getPropertyClosureIterator() {
		return getPropertyIterator();
	}
	public Iterator getTableClosureIterator() {
		return Collections.singleton( getDatabaseTable() ).iterator();
	}
	public Iterator getKeyClosureIterator() {
		return Collections.singleton( getKey() ).iterator();
	}

	public void addSubclass(SubclassMapping subclass) {
		super.addSubclass(subclass);
		setPolymorphic(true);
	}

	public boolean isExplicitPolymorphism() {
		return explicitPolymorphism;
	}

	public IPropertyMapping getVersion() {
		return version;
	}
	public void setVersion(IPropertyMapping version) {
		this.version = version;
	}
	//akuzmin 03.05.2005
	public String getVersionColumnName() {
		if ((version!=null)&&(version.getColumnIterator().hasNext()))
		return ((Column)version.getColumnIterator().next()).getName();
		else return "";
	}

	public boolean isVersioned() {
		return version!=null;
	}

	public boolean isMutable() {
		return mutable;
	}
	public boolean hasEmbeddedIdentifier() {
		return embeddedIdentifier;
	}


	public IDatabaseTable getRootTable() {
		return getDatabaseTable();
	}


	public IHibernateClassMapping getSuperclass() {
		return null;
	}

	public IHibernateKeyMapping getKey() {
		return getIdentifier();
	}

	public void setDiscriminator(IHibernateValueMapping discriminator) {
		this.discriminator = discriminator;
	}

	public void setEmbeddedIdentifier(boolean embeddedIdentifier) {
		this.embeddedIdentifier = embeddedIdentifier;
	}

	public void setExplicitPolymorphism(boolean explicitPolymorphism) {
		this.explicitPolymorphism = explicitPolymorphism;
	}


	public void setMutable(boolean mutable) {
		this.mutable = mutable;
	}

	public boolean isDiscriminatorInsertable() {
		return discriminatorInsertable;
	}

	public void setDiscriminatorInsertable(boolean insertable) {
		this.discriminatorInsertable = insertable;
	}

	public boolean isForceDiscriminator() {
		return forceDiscriminator;
	}

	public void setForceDiscriminator(boolean forceDiscriminator) {
		this.forceDiscriminator = forceDiscriminator;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String string) {
		where = string;
	}


	public String getCacheConcurrencyStrategy() {
		return cacheConcurrencyStrategy;
	}

	public void setCacheConcurrencyStrategy(String cacheConcurrencyStrategy) {
		//edit tau 31.03.2006
		if (this.cacheConcurrencyStrategy != null){
			if (!this.cacheConcurrencyStrategy.equals(cacheConcurrencyStrategy)){
				setDirtyCacheConcurrencyStrategy(true);				
			}
		} else if (cacheConcurrencyStrategy != null){
			setDirtyCacheConcurrencyStrategy(true);
		}
		this.cacheConcurrencyStrategy = cacheConcurrencyStrategy;
	}

	public String getCacheRegionName() {
		return cacheRegionName==null ? getEntityName() : cacheRegionName;
	}
	public void setCacheRegionName(String cacheRegionName) {
		//edit tau 31.03.2006
		if (this.cacheRegionName != null){
			if (!this.cacheRegionName.equals(cacheRegionName)){
				setDirtyCacheConcurrencyStrategy(true);				
			}
		} else if (cacheRegionName != null){
			setDirtyCacheConcurrencyStrategy(true);
		}
		this.cacheRegionName = cacheRegionName;
	}

	public boolean isJoinedSubclass() {
		return false;
	}
	public boolean isUnionSubclass(){
		return false;
	}
	public boolean isSubclass(){
		return false;
	}

	public boolean isClass() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		if(visitor instanceof IHibernateMappingVisitor) 
		    return ((IHibernateMappingVisitor)visitor).visitRootClassMapping(this,argument);
		return visitor.visitPersistentClassMapping(this,argument);
	}

	//akuzmin 21/04/2005	
	public IPropertySource2 getPropertySource()
	{
		BeanPropertySourceBase bp = new BeanPropertySourceBase(this);
		bp.setPropertyDescriptors(RootClassMappingPropertyDescriptorsHolder.getInstance(this));
		return bp;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.ClassMapping#copyFrom(org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping)
	 */
	public void copyFrom(IHibernateClassMapping hcm) {
		super.copyFrom(hcm);
		table=hcm.getDatabaseTable();
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClassMapping#isIncludeSuperFields()
	 */
	public boolean isIncludeSuperFields() {
		return true;
	}

	public synchronized boolean isDirtyCacheConcurrencyStrategy() {
		return dirtyCacheConcurrencyStrategy;
	}

	public synchronized void setDirtyCacheConcurrencyStrategy(
			boolean dirtyCacheConcurrencyStrategy) {
		this.dirtyCacheConcurrencyStrategy = dirtyCacheConcurrencyStrategy;
	}	

}
