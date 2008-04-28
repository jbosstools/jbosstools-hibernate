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
package org.jboss.tools.hibernate.internal.core.hibernate.automapping;

import java.util.HashSet;
import java.util.Set;

import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.core.hibernate.IAnyMapping;
import org.jboss.tools.hibernate.core.hibernate.IArrayMapping;
import org.jboss.tools.hibernate.core.hibernate.IBagMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
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
import org.jboss.tools.hibernate.core.hibernate.IRootClassMapping;
import org.jboss.tools.hibernate.core.hibernate.ISetMapping;
import org.jboss.tools.hibernate.core.hibernate.ISimpleValueMapping;
import org.jboss.tools.hibernate.core.hibernate.ISubclassMapping;
import org.jboss.tools.hibernate.core.hibernate.IUnionSubclassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;
import org.jboss.tools.hibernate.internal.core.util.StringUtils;


/** 
 * @author Nick - mailto:n.belaevski@exadel.com
 *
 */
public class IdentifierColumnBuilderVisitor extends ColumnBuilderVisitor {
    
    private boolean processLinked = false;
    
    private Set<IHibernateClassMapping> cycleCatcher = new HashSet<IHibernateClassMapping>();
    
    public void setProcessLinked(boolean flag)
    {
        processLinked = flag;
    }

    public IdentifierColumnBuilderVisitor(HibernateMapping mapping)
    {
        super(mapping);
    }

    public Object visitSimpleValueMapping(ISimpleValueMapping simple, Object argument) {
        if (!processLinked)
            return super.visitSimpleValueMapping(simple,argument);
        return null;
    }

    public Object visitAnyMapping(IAnyMapping mapping, Object argument) {
        return null;
    }

    public Object visitListMapping(IListMapping listMapping, Object argument) {
        return null;
    }

    public Object visitArrayMapping(IArrayMapping mapping, Object argument) {
        return null;
    }

    public Object visitBagMapping(IBagMapping bagMapping, Object argument) {
        return null;
    }

    public Object visitIdBagMapping(IIdBagMapping mapping, Object argument) {
        return null;
    }

    public Object visitPrimitiveArrayMapping(IPrimitiveArrayMapping constraint, Object argument) {
        return null;
    }

    public Object visitMapMapping(IMapMapping mapping, Object argument) {
        return null;
    }

    public Object visitSetMapping(ISetMapping mapping, Object argument) {
        return null;
    }

    public Object visitOneToManyMapping(IOneToManyMapping mapping, Object argument) {
        return null;
    }

    public Object visitManyToManyMapping(IManyToManyMapping mapping, Object argument) {
        return null;
    }

    public Object visitManyToAnyMapping(IManyToAnyMapping mapping, Object argument) {
        if (processLinked)
            return super.visitManyToAnyMapping(mapping,argument);
        return null;
    }

    public Object visitManyToOneMapping(IManyToOneMapping mapping, Object argument) {
        if (processLinked)
            return super.visitManyToOneMapping(mapping,argument);
        return null;
    }

    public Object visitOneToOneMapping(IOneToOneMapping mapping, Object argument) {
        return null;
    }

    public Object visitJoinMapping(IJoinMapping mapping, Object argument) {
        return null;
    }


    public Object visitRootClassMapping(IRootClassMapping mapping, Object argument) {
        if (mapping.getDatabaseTable() != null)
        {
            tablesStack.push(mapping.getDatabaseTable());
            
            boolean remove = cycleCatcher.add(mapping);

            if (mapping.getIdentifier() != null)
                mapping.getIdentifier().accept(this,argument);

            if (remove)
            {
                cycleCatcher.remove(mapping);
            }
            
            tablesStack.pop();
        }
        return null;
    }

    public Object visitSubclassMapping(ISubclassMapping mapping, Object argument) {
        boolean remove = cycleCatcher.add(mapping);

        if (mapping.getIdentifier() != null)
            mapping.getIdentifier().accept(this,argument);

        if (remove)
        {
            cycleCatcher.remove(mapping);
        }
        
        return null;
    }

    public Object visitJoinedSubclassMapping(IJoinedSubclassMapping mapping, Object argument) {

        if (!processLinked)
            return null;
        
        if (cycleCatcher.contains(mapping.getRootClass()))
            return null;
        
//        if (mapping.getIdentifier() != null)
//            mapping.getIdentifier().accept(this,argument);
        
        if (mapping.getKey() != null && mapping.getDatabaseTable() != null)
        {
            tablesStack.push(mapping.getDatabaseTable());

            boolean remove = cycleCatcher.add(mapping);
            
            SimpleValueMapping key = (SimpleValueMapping)mapping.getKey();
            IHibernateClassMapping superMapping = (IHibernateClassMapping)mapping.getSuperclassMapping();
            if (superMapping != null && superMapping.getIdentifier() != null)
            {
                IPersistentClass superClass = superMapping.getPersistentClass();
                bindReferenceColumnsAndFK(key,superClass,null,
                        StringUtils.safeStrCoupling(
                                safeCastToString(argument),
                                mapping.getPersistentClass().getShortName()));
            }

            if (remove)
            {
                cycleCatcher.remove(mapping);
            }
            
            tablesStack.pop();
        }
        
        return null;
    }

    public Object visitUnionSubclassMapping(IUnionSubclassMapping mapping, Object argument) {
        if (mapping.getIdentifier() != null)
            mapping.getIdentifier().accept(this,argument);
        return null;
    }

    protected IDatabaseColumn createAndBindColumn(ISimpleValueMapping valueMapping, String namePrefix) {
        IDatabaseColumn result = super.createAndBindColumn(valueMapping, namePrefix);
        //HibernateAutoMappingHelper.createOrBindPK(result);
        return result;
    }

    protected IDatabaseColumn cloneColumn(IDatabaseTable toTable, IDatabaseColumn originalColumn, String prefixName, IPersistentValueMapping value) {
        IDatabaseColumn result = super.cloneColumn(toTable, originalColumn, prefixName, value);
        //HibernateAutoMappingHelper.createOrBindPK(result);
        return result;
    }

    protected void bindReferenceColumnsAndFK(SimpleValueMapping svm, IPersistentClass toClass, IPersistentField field, String prefix) {
        if (cycleCatcher.contains(toClass.getPersistentClassMapping()) || toClass.getPersistentClassMapping() == null)
            return ;
        
        toClass.getPersistentClassMapping().accept(this,null);
        super.bindReferenceColumnsAndFK(svm, toClass, field, null, false);
    }

}
