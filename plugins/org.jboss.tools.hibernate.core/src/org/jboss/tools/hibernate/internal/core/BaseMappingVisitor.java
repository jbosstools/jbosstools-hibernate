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
package org.jboss.tools.hibernate.internal.core;

import java.util.Iterator;

import org.hibernate.mapping.Column;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseConstraint;
import org.jboss.tools.hibernate.core.IDatabaseSchema;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.INamedQueryMapping;
import org.jboss.tools.hibernate.core.IOrmElement;
import org.jboss.tools.hibernate.core.IOrmProject;
import org.jboss.tools.hibernate.core.IPackage;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.hibernate.IAnyMapping;
import org.jboss.tools.hibernate.core.hibernate.IArrayMapping;
import org.jboss.tools.hibernate.core.hibernate.IBagMapping;
import org.jboss.tools.hibernate.core.hibernate.ICollectionMapping;
import org.jboss.tools.hibernate.core.hibernate.IComponentMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor;
import org.jboss.tools.hibernate.core.hibernate.IIdBagMapping;
import org.jboss.tools.hibernate.core.hibernate.IJoinMapping;
import org.jboss.tools.hibernate.core.hibernate.IJoinedSubclassMapping;
import org.jboss.tools.hibernate.core.hibernate.IListMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToAnyMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToManyMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToOneMapping;
import org.jboss.tools.hibernate.core.hibernate.IMapMapping;
import org.jboss.tools.hibernate.core.hibernate.IOneToManyMapping;
import org.jboss.tools.hibernate.core.hibernate.IOneToOneMapping;
import org.jboss.tools.hibernate.core.hibernate.IPrimitiveArrayMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.core.hibernate.IRootClassMapping;
import org.jboss.tools.hibernate.core.hibernate.ISetMapping;
import org.jboss.tools.hibernate.core.hibernate.ISimpleValueMapping;
import org.jboss.tools.hibernate.core.hibernate.ISubclassMapping;
import org.jboss.tools.hibernate.core.hibernate.IUnionSubclassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.SchemaSynchronizationHelper;


public class BaseMappingVisitor implements IHibernateMappingVisitor {

	public Object visitSimpleValueMapping(ISimpleValueMapping simple, Object argument) {
		return null;
	}

	public Object visitAnyMapping(IAnyMapping mapping, Object argument) {
		return null;
	}

	public Object visitComponentMapping(IComponentMapping mapping,
			Object argument) {
		if(mapping == null) return null;
		
		// edit tau 17.111.2005
		//for(Iterator it = mapping.getPropertyIterator(); it.hasNext();)
		for(Iterator it = SchemaSynchronizationHelper.getIteratorCopy(mapping.getPropertyIterator()); it.hasNext();)			
		{
			IPropertyMapping ipm = (IPropertyMapping)it.next();
			if(ipm != null)
				ipm.accept(this, mapping);
		}
		return null;
	}

	public Object visitOneToManyMapping(IOneToManyMapping mapping, Object argument) {
		return null;
	}

	public Object visitManyToManyMapping(IManyToManyMapping mapping, Object argument) {
		visitManyToOneMapping(mapping, argument);	
		return null;
	}

	public Object visitManyToAnyMapping(IManyToAnyMapping mapping, Object argument) {
		visitAnyMapping(mapping, argument);
		return null;
	}

	public Object visitManyToOneMapping(IManyToOneMapping mapping, Object argument) {
		return null;
	}

	public Object visitOneToOneMapping(IOneToOneMapping mapping, Object argument) {
		return null;
	}

	public Object visitJoinMapping(IJoinMapping mapping, Object argument) {
		if(mapping == null) return null;
		Iterator it = mapping.getPropertyIterator();
		while(it.hasNext())
		{
			IOrmElement element = (IOrmElement)it.next();
			if(element != null)
				element.accept(this,mapping);
		}
		return null;
	}

	
	public Object visitHibernateClassMapping(IHibernateClassMapping mapping, Object argument){
		if(mapping == null) return null;
		Iterator it = mapping.getFieldMappingIterator();
		while(it.hasNext())
		{
			IOrmElement element = (IOrmElement)it.next();
			if(element != null)
				element.accept(this,mapping);
		}
		return null;
	}
	public Object visitRootClassMapping(IRootClassMapping mapping,
			Object argument) {
		if(mapping == null) return null;
        IHibernateClassMapping hcm=mapping;
        visitHibernateClassMapping(mapping, argument);
		if(hcm.getDiscriminator()!=null)hcm.getDiscriminator().accept(this, mapping);
		if(hcm.getIdentifierProperty()!=null)hcm.getIdentifierProperty().accept(this, mapping);
		else if(hcm.getIdentifier()!=null) hcm.getIdentifier().accept(this, mapping);
		if(hcm.getVersion()!=null)hcm.getVersion().accept(this, mapping);
		return null;
	}

	public Object visitSubclassMapping(ISubclassMapping mapping, Object argument) {
		visitHibernateClassMapping(mapping, argument);
		return null;
	}

	public Object visitJoinedSubclassMapping(IJoinedSubclassMapping mapping, Object argument) {
		visitHibernateClassMapping(mapping, argument);
		return null;
	}

	public Object visitUnionSubclassMapping(IUnionSubclassMapping mapping, Object argument) {
		visitHibernateClassMapping(mapping, argument);
		return null;
	}

	public Object visitPropertyMapping(IPropertyMapping mapping, Object argument) {
		if(mapping == null) return null;
		if(mapping.getValue()!=null)
		{
			mapping.getValue().accept(this, mapping);
		}
		return null;
	}

	public Object visitOrmProject(IOrmProject project, Object argument) {
		return null;
	}

	public Object visitDatabaseSchema(IDatabaseSchema schema, Object argument) {
		return null;
	}

	public Object visitDatabaseTable(IDatabaseTable table, Object argument) {
		return null;
	}

	public Object visitDatabaseColumn(IDatabaseColumn column, Object argument) {
		return null;
	}

	public Object visitDatabaseConstraint(IDatabaseConstraint constraint, Object argument) {
		return null;
	}

	public Object visitPackage(IPackage pakage, Object argument) {
		// added by Nick 25.07.2005
		if(pakage == null)	return null;
        IPersistentClass[] pcs = pakage.getPersistentClasses();
        for (int i = 0; i < pcs.length; i++) {
            IPersistentClass class1 = pcs[i];
            if(class1 == null) continue;
            if (class1.getPersistentClassMapping() != null)
            {
                try {
                    class1.getPersistentClassMapping().accept(this,pakage);
                } catch (Exception ex) {
                	OrmCore.getPluginLog().logError(ex.getMessage(),ex);
                }
            }
        }
        // by Nick
        return null;
	}

	public Object visitMapping(IMapping mapping, Object argument) {
		if(mapping == null) return null;
		IPersistentClassMapping maps[]=mapping.getPersistentClassMappings();
		for(int i=0;i<maps.length;++i){
			try{
				if(maps[i] != null)
					maps[i].accept(this, mapping);
			} catch(Exception ex){
				OrmCore.getPluginLog().logError(ex);
			}
		}
		return null;
	}

	public Object visitMappingStorage(IMappingStorage storage, Object argument) {
		return null;
	}

	public Object visitPersistentClass(IPersistentClass clazz, Object argument) {
		return null;
	}

	public Object visitPersistentField(IPersistentField field, Object argument) {
		return null;
	}

	public Object visitPersistentClassMapping(IPersistentClassMapping mapping, Object argument) {
		return null;
	}

	public Object visitPersistentFieldMapping(IPersistentFieldMapping mapping, Object argument) {
		return null;
	}

	public Object visitPersistentValueMapping(IPersistentValueMapping mapping, Object argument) {
		return null;
	}
//	----------------------------- collections ---------------------------
	public Object visitIdBagMapping(IIdBagMapping mapping, Object argument) {
		if(mapping == null) return null;
		visitCollectionMapping(mapping, argument);
		if(mapping != null && mapping.getIdentifier() != null)
			mapping.getIdentifier().accept(this, mapping);
		return null;
	}
	
	public Object visitListMapping(IListMapping mapping, Object argument) {
		if(mapping == null) return null;
		visitCollectionMapping(mapping, argument);
		if(mapping != null && mapping.getIndex() != null)
			mapping.getIndex().accept(this, mapping);
		return null;
	}
	public Object visitMapMapping(IMapMapping mapping, Object argument) {
		if(mapping == null) return null;
		visitCollectionMapping(mapping, argument);
		if(mapping != null && mapping.getIndex() != null)
			mapping.getIndex().accept(this, mapping);
		return null;
	}
	public Object visitPrimitiveArrayMapping(IPrimitiveArrayMapping mapping, Object argument) {
		if(mapping == null) return null;
		visitCollectionMapping(mapping, argument);
		if(mapping != null && mapping.getIndex() != null)
			mapping.getIndex().accept(this, mapping);
		return null;
	}
	public Object visitSetMapping(ISetMapping mapping, Object argument) {
		if(mapping == null) return null;
		visitCollectionMapping(mapping, argument);
		return null;
	}
	public Object visitArrayMapping(IArrayMapping mapping, Object argument) {
		if(mapping == null) return null;
		visitCollectionMapping(mapping, argument);
		if(mapping != null && mapping.getIndex() != null)
			mapping.getIndex().accept(this, mapping);
		return null;
	}
	public Object visitBagMapping(IBagMapping mapping, Object argument) {
		if(mapping == null) return null;
		visitCollectionMapping(mapping, argument);
		return null;
	}
	
	public Object visitCollectionMapping(ICollectionMapping mapping, Object argument) 
	{
		if(mapping == null) return null;
		if(mapping.getElement() != null)
			mapping.getElement().accept(this, mapping);
		if(mapping.getKey() != null)
			mapping.getKey().accept(this, mapping);
		return null;
	}

	// add tau 27.07.2005
	public Object visitNamedQueryMapping(INamedQueryMapping mapping, Object argument) {
		return null;
	}

	public Object visitDatabaseColumn(Column column, Object argument) {
		// TODO Auto-generated method stub
		return null;
	}

}
