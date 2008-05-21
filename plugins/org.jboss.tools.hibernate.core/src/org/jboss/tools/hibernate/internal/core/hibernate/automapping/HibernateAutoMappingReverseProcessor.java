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

import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IDatabaseTableColumnSet;
import org.jboss.tools.hibernate.core.IDatabaseTableForeignKey;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.hibernate.CollectionMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;
import org.jboss.tools.hibernate.internal.core.util.StringUtils;
import org.jboss.tools.hibernate.internal.core.util.TypeUtils;


/**
 * @author Nick - mailto:n.belaevski@exadel.com
 * created: 15.07.2005
 * 
 */
public class HibernateAutoMappingReverseProcessor {
    
    private PersistentFieldProvider provider;
    private HibernateAutoMappingHelper helper;
    private MappingsFactory factory;
    
    //private List creators = new ArrayList();
    
    private interface IMappingsCreator
    {
        void createMappings(MappingsFactory factory);
    }
 
    private class SimpleCreator implements IMappingsCreator
    {
        private IPersistentField proxy;
        private IDatabaseColumn column;
        
        SimpleCreator(IPersistentField proxy, IDatabaseColumn column)
        {
            this.proxy = proxy;
            this.column = column;
        }

        public void createMappings(MappingsFactory factory)
        {
            IPersistentField field = proxy;
            if (factory.getVisitor() instanceof ColumnMergingVisitor) {
                ColumnMergingVisitor visitor = (ColumnMergingVisitor) factory.getVisitor();
                visitor.setPersistentFieldColumnSet(field,new ColumnSet(column));
            }
            factory.addPersistentField(field,null);
        }
    }
    
    private class ManyToManyRelationCreator implements IMappingsCreator
    {
        private IDatabaseTableForeignKey[] fKeys;
        private IPersistentField[] proxies;
        private IPersistentClass[] clases;
        
        ManyToManyRelationCreator(IDatabaseTableForeignKey[] fKeys, IPersistentField[] proxies, IPersistentClass[] clases)
        {
            this.fKeys = fKeys;
            this.proxies = proxies;
            this.clases = clases;
        }
        
        public void createMappings(MappingsFactory factory)
        {
            for (int i = 0; i < proxies.length; i++) {
                IPersistentField field = proxies[i];
                
                if (factory.getVisitor() instanceof ColumnMergingVisitor) {
                    ColumnMergingVisitor visitor = (ColumnMergingVisitor) factory.getVisitor();
                    visitor.setPersistentFieldColumnSet(field,fKeys[i]);
                }

                factory.addReferringPersistentField(field.getMapping(),clases[i],null);
            }
            factory.process();
        }
    }
    
    
    private class ManyToOneRelationCreator implements IMappingsCreator
    {
        private IDatabaseTableForeignKey fKey;
        private IPersistentField[] proxies;
        private IPersistentClass[] clases;
        
        ManyToOneRelationCreator(IDatabaseTableForeignKey fKey, IPersistentField[] proxies, IPersistentClass[] clases)
        {
            this.fKey = fKey;
            this.proxies = proxies;
            this.clases = clases;
        }
        
        public void createMappings(MappingsFactory factory)
        {
            for (int i = 0; i < proxies.length; i++) {
                IPersistentField field = proxies[i];
                
                if (factory.getVisitor() instanceof ColumnMergingVisitor) {
                    ColumnMergingVisitor visitor = (ColumnMergingVisitor) factory.getVisitor();
                    visitor.setPersistentFieldColumnSet(field,fKey);
                }

                factory.addReferringPersistentField(field.getMapping(),clases[i],null);
            }
            
            factory.process();
        }
    }

    private class OneToManyRelationCreator implements IMappingsCreator
    {
        private IDatabaseTableForeignKey fKey;
        private IPersistentField[] proxies;
        private IPersistentClass[] clases;
        
        OneToManyRelationCreator(IDatabaseTableForeignKey fKey, IPersistentField[] proxies, IPersistentClass[] clases)
        {
            this.fKey = fKey;
            this.proxies = proxies;
            this.clases = clases;
        }
        
        public void createMappings(MappingsFactory factory)
        {
            IPersistentField field = null;
            for (int i = 0; i < proxies.length; i++) {
                field = proxies[i];
                if (factory.getVisitor() instanceof ColumnMergingVisitor) {
                    ColumnMergingVisitor visitor = (ColumnMergingVisitor) factory.getVisitor();
                    visitor.setPersistentFieldColumnSet(field,fKey);
                }

                factory.addReferringPersistentField(field.getMapping(),clases[i],null);
            }
            
            factory.process();
            
            IPersistentValueMapping value = field.getMapping().getPersistentValueMapping();
            if (value instanceof CollectionMapping) {
                CollectionMapping collection = (CollectionMapping) value;
                
                collection.setInverse(true);
                Iterator columns = fKey.getColumnIterator();
                while (columns.hasNext())
                {
                    IDatabaseColumn column = (IDatabaseColumn) columns.next();
                    
                    if (collection.getKey() instanceof SimpleValueMapping) {
                        SimpleValueMapping key = (SimpleValueMapping) collection.getKey();
                        
                        key.addColumn(column);
                    }
                }
            }
        }
    }
    
    
    private IDatabaseColumn getFirstColumn(IDatabaseTableColumnSet constraint)
    {
        IDatabaseColumn result = null;
        if (constraint != null)
        {
            Iterator itr = constraint.getColumnIterator();
            if (itr.hasNext())
            {
                result = (IDatabaseColumn)itr.next();
            }
        }
        return result;
    }
    
    HibernateAutoMappingReverseProcessor(MappingsFactory factory, PersistentFieldProvider provider, HibernateAutoMappingHelper helper)
    {
        this.provider = provider;
        this.helper = helper;
        this.factory = factory;
    }
    
    IPersistentFieldMapping bindOneToMany(IDatabaseTableForeignKey fKey, IPersistentClass fromClass, IPersistentClass toClass) throws CoreException
    {
        //maybe that can be one-to-one?
        IPersistentField inverseField = null;
        //XXX Nick add 2phase search
        inverseField = provider.getOrCreatePersistentField(toClass,StringUtils.beanDecapitalize(fromClass.getShortName()),
                TypeUtils.UNINDEXED_COLLECTIONS,helper.getConfiguration().getUnindexedCollectionName(),
                helper.getConfiguration().isUseFuzzySearch());
        helper.createAndBindPropertyMapping(inverseField);
        
        OneToManyRelationCreator struct = new OneToManyRelationCreator(fKey,
                new IPersistentField[] {inverseField},
                new IPersistentClass[] {fromClass}
        );
        struct.createMappings(factory);
        
        return inverseField.getMapping();
    }
    
    IPersistentFieldMapping bindManyToOne(IDatabaseTableForeignKey fKey, IPersistentClass fromClass, IPersistentClass toClass, boolean bidirectional) throws CoreException
    {
        IPersistentField field = null;
        IDatabaseColumn firstFKeyColumn = getFirstColumn(fKey);
        
        IDatabaseTable refTable = fKey.getReferencedTable();
        if (refTable != null)
        {
            String fieldName;
            if (fKey.getColumnSpan() == 1 && firstFKeyColumn != null)
            {
                String columnName = null;
                
                if (refTable.getPrimaryKey() != null || toClass.getPersistentClassMapping().getIdentifier() != null)
                {
                    IDatabaseColumn pKeyColumn = getFirstColumn( ((refTable.getPrimaryKey() != null) ? 
                            refTable.getPrimaryKey() : 
                        (IDatabaseTableColumnSet) new ColumnSet(toClass.getPersistentClassMapping().getIdentifier().getColumnIterator())));
                    
                    columnName = StringUtils.getUnPrefixedName(firstFKeyColumn,pKeyColumn);
                }
                
                if (columnName == null)
                    columnName = firstFKeyColumn.getName();
                
                fieldName = HibernateAutoMapping.columnToPropertyName(columnName);
            }
            else
            {
                fieldName = StringUtils.beanDecapitalize(toClass.getShortName());
            }
            
            
            //XXX Nick add 2phase search
            field = provider.getOrCreatePersistentField(fromClass,fieldName,toClass.getName(),helper.getConfiguration().isUseFuzzySearch());
            helper.createAndBindPropertyMapping(field);
            
            if (bidirectional)
            {
                //maybe that can be one-to-one?
                IPersistentField inverseField = null;
                if (fKey.isUnique())
                {
                    //XXX Nick add 2phase search
                    inverseField = provider.getOrCreatePersistentField(toClass,StringUtils.beanDecapitalize(fromClass.getShortName()),
                            fromClass.getName(),
                            helper.getConfiguration().isUseFuzzySearch());
                }
                else
                {
                    //XXX Nick add 2phase search
                    inverseField = provider.getOrCreatePersistentField(toClass,StringUtils.beanDecapitalize(fromClass.getShortName()),
                            TypeUtils.UNINDEXED_COLLECTIONS,helper.getConfiguration().getUnindexedCollectionName(),
                            helper.getConfiguration().isUseFuzzySearch());
                }
                helper.createAndBindPropertyMapping(inverseField);
                
                ManyToOneRelationCreator struct = new ManyToOneRelationCreator(fKey,
                        new IPersistentField[] {field,inverseField},
                        new IPersistentClass[] {toClass,fromClass}
                );
                struct.createMappings(factory);
            }
            else
            {
                ManyToOneRelationCreator struct = new ManyToOneRelationCreator(fKey,
                        new IPersistentField[] {field},
                        new IPersistentClass[] {toClass}
                );
                struct.createMappings(factory);
            }
        }
        
        if (field == null)
            return null;
            
        return field.getMapping();
    }
    
    IPersistentFieldMapping[] bindManyToMany(IDatabaseTableForeignKey key1, IDatabaseTableForeignKey key2, IPersistentClass class1, IPersistentClass class2) throws CoreException
    {
        String propertyName = StringUtils.beanDecapitalize(class2.getShortName())+"Many";
        //XXX Nick add 2phase search
//        PersistentFieldProxy pf1 = provider.createPersistentFieldProxy(class1,propertyName,
//                TypeUtils.UNINDEXED_COLLECTIONS,helper.getConfiguration().getUnindexedCollectionName());
        IPersistentField pf1 = provider.getOrCreatePersistentField(class1,propertyName,
                TypeUtils.UNINDEXED_COLLECTIONS,helper.getConfiguration().getUnindexedCollectionName(),
                helper.getConfiguration().isUseFuzzySearch());

        helper.createAndBindPropertyMapping(pf1);

        //visitor.addPersistentFieldConstraints(pf,linkingKeys[0]);
        //valueFactory.addReferringPersistentField(pf,otherSidePc,null);

        propertyName = StringUtils.beanDecapitalize(class1.getShortName())+"Many";
        //XXX Nick add 2phase search
//        PersistentFieldProxy pf2 = provider.createPersistentFieldProxy(class2,propertyName,
//                TypeUtils.UNINDEXED_COLLECTIONS,helper.getConfiguration().getUnindexedCollectionName());
        IPersistentField pf2 = provider.getOrCreatePersistentField(class2,propertyName,
                TypeUtils.UNINDEXED_COLLECTIONS,helper.getConfiguration().getUnindexedCollectionName(),
                helper.getConfiguration().isUseFuzzySearch());
        helper.createAndBindPropertyMapping(pf2);

        IMappingsCreator creator = new ManyToManyRelationCreator(
                new IDatabaseTableForeignKey[] {key2,key1},
                new IPersistentField[] {pf1,pf2},
                new IPersistentClass[] {class2,class1});
        
        creator.createMappings(factory);
        
        return new IPersistentFieldMapping[]{pf1.getMapping(),pf2.getMapping()};
    }
    
    IPersistentFieldMapping bindSimpleValue(IDatabaseColumn column, IPersistentClass clazz) throws CoreException
    {
        IPersistentFieldMapping fieldMapping = null;
        Type fieldType = TypeUtils.columnTypeToHibTypeDefault(column,helper.getConfiguration());
        
        if (fieldType != null)
        {
//            PersistentFieldProxy pf = provider.createPersistentFieldProxy(clazz,
//                    HibernateAutoMapping.columnToPropertyName(column.getName()),fieldType);
            IPersistentField pf = provider.getOrCreatePersistentField(clazz,
                    HibernateAutoMapping.columnToPropertyName(column.getName()),fieldType,
                    helper.getConfiguration().isUseFuzzySearch());
            fieldMapping = helper.createAndBindPropertyMapping(pf);
            
            new SimpleCreator(pf,column).createMappings(factory);
        
            if (pf.getMapping() != null && pf.getMapping().getPersistentValueMapping() instanceof SimpleValueMapping)
            {
                ((SimpleValueMapping)pf.getMapping().getPersistentValueMapping()).setType(fieldType);
            }
        }
        
        return fieldMapping;
    }
    
/*    void createMappings(MappingsFactory factory)
    {
        Iterator itr = creators.iterator();
        while (itr.hasNext())
        {
            Object o = itr.next();
            if (o instanceof IMappingsCreator) {
                ((IMappingsCreator) o).createMappings(factory);
            }
        }
    }
*/
    }
