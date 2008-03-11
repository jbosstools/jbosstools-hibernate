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
import org.jboss.tools.hibernate.core.hibernate.ISubclassMapping;
import org.jboss.tools.hibernate.internal.core.CompoundIterator;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.SubclassMappingPropertyDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;

/**
 * A sublass in a table-per-class-hierarchy mapping
 */
public class SubclassMapping extends ClassMapping implements ISubclassMapping {
	private static final long serialVersionUID = 1L;

	private IHibernateClassMapping superclass;
	
	public SubclassMapping(IHibernateClassMapping superclass) {
		this.superclass = superclass;
	}

	
	public String getCacheConcurrencyStrategy() {
// added by yk 30.06.2005
		String strategy = null;
		if(getSuperclass() != null)
			strategy = getSuperclass().getCacheConcurrencyStrategy();
		return strategy;
// added by yk 30.06.2005 stop

/* rem by yk 30.06.2005		return getSuperclass().getCacheConcurrencyStrategy(); */
	}

	public IRootClassMapping getRootClass() {
//		 added by yk 30.06.2005
		IRootClassMapping irc = null;
		if(getSuperclass() != null)
			irc = getSuperclass().getRootClass();
		return irc;
// added by yk 30.06.2005 stop
/* rem by yk 30.06.2005  return getSuperclass().getRootClass(); */
	}

	public IHibernateClassMapping getSuperclass() {
		return superclass;
	}

	public IPropertyMapping getIdentifierProperty() {
// added by yk 30.06.2005
		IPropertyMapping ipm = null;
		if(getSuperclass() != null)
			ipm = getSuperclass().getIdentifierProperty();
		return ipm;
// added by yk 30.06.2005 stop
		
/* rem by yk 30.06.2005			return getSuperclass().getIdentifierProperty(); */
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping#setIdentifier(org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping)
	 */
	public void setIdentifier(IHibernateKeyMapping id) {
		throw new RuntimeException("Illegal operation for the class mapping");
	}


	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping#setIdentifierProperty(org.jboss.tools.hibernate.core.hibernate.IPropertyMapping)
	 */
	public void setIdentifierProperty(IPropertyMapping idMapping) {
		throw new RuntimeException("Illegal operation for the class mapping");
	}


	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClassMapping#setDatabaseTable(org.jboss.tools.hibernate.core.IDatabaseTable)
	 */
	public void setDatabaseTable(IDatabaseTable table) {
		throw new RuntimeException("Illegal operation for the class mapping");
	}
	
	public IHibernateKeyMapping getIdentifier() {
//		 added by yk 30.06.2005
		IHibernateKeyMapping idnt = null;
		if(getSuperclass() != null)
			idnt = getSuperclass().getIdentifier();
		return idnt;
// added by yk 30.06.2005 stop

/* rem by yk 30.06.2005		return getSuperclass().getIdentifier(); */
	}
	public boolean hasIdentifierProperty() {
		return getSuperclass().hasIdentifierProperty();
	}
	public IHibernateValueMapping getDiscriminator() {
// added by yk 30.06.2005
		IHibernateValueMapping descr = null;
		if(getSuperclass() != null)
			descr = getSuperclass().getDiscriminator();
		return descr;
// added by yk 30.06.2005 stop
/* rem by yk 30.06.2005		return getSuperclass().getDiscriminator(); */
	}
	
	//akuzmin 03.05.2005
	public String getDiscriminatorColumnName() {
		if ((getDiscriminator()!=null)&& (getDiscriminator().getColumnIterator().hasNext()))
		return ((Column)getDiscriminator().getColumnIterator().next()).getName();
		else return "";
	}
	
	
	public boolean isMutable() {
		return getSuperclass().isMutable();
	}
	public boolean isInherited() {
		return true;
	}
	public boolean isPolymorphic() {
		return true;
	}

	public void addProperty(PropertyMapping p) {
		super.addProperty(p);
	}
	
	public void addJoin(JoinMapping j) {
		super.addJoin(j);
	}


	public boolean isVersioned() {
		return getSuperclass().isVersioned();
	}
	public IPropertyMapping getVersion() {
// added by yk 30.06.2005
		IPropertyMapping version = null;
		if(getSuperclass() != null)
			version = getSuperclass().getVersion();
		return version;
// added by yk 30.06.2005 stop
/* rem by yk 30.06.2005		return getSuperclass().getVersion(); */
	}

	//akuzmin 03.05.2005
	public String getVersionColumnName() {
		if ((getVersion()!=null)&& (getVersion().getColumnIterator().hasNext()))
		return ((Column)getVersion().getColumnIterator().next()).getName();
		else return "";
	}	
	
	public boolean hasEmbeddedIdentifier() {
		return getSuperclass().hasEmbeddedIdentifier();
	}
	

	public IDatabaseTable getRootTable() {
		return getSuperclass().getRootTable();
	}

	public IHibernateKeyMapping getKey() {
		return getSuperclass().getIdentifier();
	}

	public boolean isExplicitPolymorphism() {
		return getSuperclass().isExplicitPolymorphism();
	}

	public void setSuperclass(IHibernateClassMapping superclass) {
		this.superclass = superclass;
	}

	public String getWhere() {
		return getSuperclass().getWhere();
	}

	public boolean isJoinedSubclass() {
		return false;
	}
	
	public boolean isUnionSubclass(){
		return false;
	}
	public boolean isSubclass(){
		return true;
	}
	public boolean isClass() {
		return false;
	}


	public IDatabaseTable getDatabaseTable() {
		if(getSuperclass()==null) return null;
		return getSuperclass().getDatabaseTable();
	}

	public boolean isForceDiscriminator() {
		return getSuperclass().isForceDiscriminator();
	}

	public boolean isDiscriminatorInsertable() {
		return getSuperclass().isDiscriminatorInsertable();
	}
	
	public Iterator<IPropertyMapping> getPropertyClosureIterator() {
		return new CompoundIterator<IPropertyMapping>(
			getSuperclass().getPropertyClosureIterator(),
			getPropertyIterator()
		);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		if(visitor instanceof IHibernateMappingVisitor) 
		    return ((IHibernateMappingVisitor)visitor).visitSubclassMapping(this,argument);
		return visitor.visitPersistentClassMapping(this,argument);
	}
	public void setDiscriminator(IHibernateValueMapping mapping){
		throw new RuntimeException("Illegal operation for the class mapping");
	}


	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping#setVersion(org.jboss.tools.hibernate.core.hibernate.IPropertyMapping)
	 */
	public void setVersion(IPropertyMapping version) {
		throw new RuntimeException("Illegal operation for the class mapping");
	}

	public IPropertySource2 getPropertySource()
	{
		BeanPropertySourceBase bp = new BeanPropertySourceBase(this);
		bp.setPropertyDescriptors(SubclassMappingPropertyDescriptorsHolder.getInstance(this));
		return bp;
	}


	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClassMapping#isIncludeSuperFields()
	 */
	public boolean isIncludeSuperFields() {
		return false;
	}


	protected void copyFieldMappings() {
		super.copyFieldMappings();
		IDatabaseTable table=this.getDatabaseTable();
		Iterator it=this.getUnjoinedPropertyIterator();
		while(it.hasNext()){
			PropertyMapping pm=(PropertyMapping)it.next();
			Iterator cit=pm.getColumnIterator();
			while(cit.hasNext()){
				Column col=(Column)cit.next();
				IDatabaseTable colTable=col.getOwnerTable();
				if(table!=colTable){
					if(colTable!=null)colTable.removeColumn(col.getName());
					table.addColumn(col);
				}
			}
		}
	}

}
