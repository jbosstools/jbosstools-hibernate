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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.IndexedCollection;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseConstraint;
import org.jboss.tools.hibernate.core.IDatabaseSchema;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IDatabaseTableForeignKey;
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
import org.jboss.tools.hibernate.core.hibernate.IAnyMapping;
import org.jboss.tools.hibernate.core.hibernate.IArrayMapping;
import org.jboss.tools.hibernate.core.hibernate.IBagMapping;
import org.jboss.tools.hibernate.core.hibernate.ICollectionMapping;
import org.jboss.tools.hibernate.core.hibernate.IComponentMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor;
import org.jboss.tools.hibernate.core.hibernate.IIdBagMapping;
import org.jboss.tools.hibernate.core.hibernate.IIndexedCollectionMapping;
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
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.PersistentClass;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.data.ForeignKey;
import org.jboss.tools.hibernate.internal.core.data.Table;
import org.jboss.tools.hibernate.internal.core.hibernate.ClassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.JoinedSubclassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;
import org.jboss.tools.hibernate.internal.core.util.StringUtils;


/** 
 * @author Nick - mailto:n.belaevski@exadel.com
 *      created 20.05.2005
 */

public class ColumnBuilderVisitor implements IHibernateMappingVisitor {
    protected HibernateMapping hibernateMapping;
    protected Stack<IDatabaseTable> tablesStack = new Stack<IDatabaseTable>();

    protected String columnName = null;
    
    protected SharedColumnsCopier sharedColumnsCopier = new SharedColumnsCopier();
    
    private class UniqueConstraintBinderVisitor extends ColumnBuilderVisitor
    {
        public UniqueConstraintBinderVisitor(HibernateMapping mapping)
        {
            super(mapping);
        }

        public Object visitDatabaseColumn(IDatabaseColumn column, Object argument) {
            column.setUnique(true);
            return null;
        }
    }
    
    public ColumnBuilderVisitor(HibernateMapping mapping)
    {
        this.hibernateMapping = mapping;
    }
    
    public SharedColumnsCopier getSharedColumnCopier()
    {
        return sharedColumnsCopier;
    }
    
    protected String safeCastToString(Object argument)
    {
        if (argument instanceof String)
            return (String) argument;
        else
            return null;
    }
    
    /**
     * CAUTION! uses copy of iterator for iteration - modifications to collection elements aren't processed
     * @param itr
     * @param argument
     */
    protected void visitIOrmElementsIterator(Iterator itr, Object argument)
    {
        if (itr != null)
        {
            List<IOrmElement> elements = new ArrayList<IOrmElement>();
            
            while (itr.hasNext())
            {
                Object obj = itr.next();
                if (obj instanceof IOrmElement)
                {
                    elements.add((IOrmElement)obj);
                }
            }
            
            Iterator eltItr = elements.iterator();
            while (eltItr.hasNext())
            {
                IOrmElement elt = (IOrmElement) eltItr.next();
                elt.accept(this,argument);
            }
        }
    }
    
    protected IPersistentField getValuePersistentField(IPersistentValueMapping valueMapping)
    {
        if (valueMapping != null)
        {
            IPersistentFieldMapping fieldMapping = valueMapping.getFieldMapping();
            if (fieldMapping != null)
            {
                return fieldMapping.getPersistentField();
            }
        }
        return null;
    }

    protected void bindReferenceColumnsAndFK(SimpleValueMapping svm, IPersistentClass toClass, IPersistentField field, String prefix, boolean setClassNamePrefix)
    {
        IDatabaseTable toTable = null;
        if (!tablesStack.isEmpty())
            toTable = (IDatabaseTable)tablesStack.peek();
        else
            return ;
        
        if (toClass == null)
            return ;
        
        String namePrefix;
        
        IPersistentClass ownerClass = null;
        
        if (field != null && field.getName() != null)
        {
            ownerClass = field.getMasterClass();
//          akuzmin 01.08.2005             
//            namePrefix = StringUtils.safeStrCoupling(prefix,field.getName());
            namePrefix = field.getName();            

//          akuzmin 01.08.2005            
//            if (ownerClass != null)
//                namePrefix = StringUtils.safeStrCoupling(ownerClass.getShortName(),namePrefix);
                
        }
        else
        {
            namePrefix = prefix;
        }
        
        if (svm != null && toClass != null)
        {
            if (svm.getColumnSpan() != 0)
                return ;

            if (ownerClass != toClass && setClassNamePrefix)
//akuzmin 01.08.2005                
//                namePrefix = StringUtils.safeStrCoupling(namePrefix,toClass.getShortName());
                namePrefix = toClass.getShortName();            
        
            ForeignKey fk = new ForeignKey();

            IPersistentClassMapping cm = toClass.getPersistentClassMapping();
            IHibernateKeyMapping pKey = null;
            if (cm instanceof JoinedSubclassMapping) {
                pKey = ((JoinedSubclassMapping) cm).getKey();
                
            }
            else if (cm instanceof ClassMapping) {
                pKey = ((ClassMapping) cm).getIdentifier();
                
            }

            if (pKey != null)
            {
                fk.setTable(toTable);
                fk.setReferencedTable(toClass.getPersistentClassMapping().getDatabaseTable());
                fk.setReferencedEntityName(toClass.getName());
                
                Iterator compositeKeyColumns = pKey.getColumnIterator();
                while (compositeKeyColumns.hasNext())
                {
                    IDatabaseColumn column = (IDatabaseColumn)compositeKeyColumns.next();
                    //or'ing with prototype column to provide support for partially nullable keys
                    IDatabaseColumn newColumn = cloneColumn(toTable,column,namePrefix,svm);
                    newColumn.setNullable(true);
                    svm.addColumn(newColumn);
                    newColumn.setPersistentValueMapping(svm);
                    fk.addColumn(newColumn);
                }

                fk.setName(findProperFKName(toTable,StringUtils.safeStrCoupling(namePrefix,pKey.getName()),fk));
                svm.setForeignKeyName(fk.getName());

                ((Table)toTable).addForeignKey(fk.getName(),fk);
            }
        }
    }
    
    protected void bindReferenceColumnsAndFK(SimpleValueMapping svm, IPersistentClass toClass, IPersistentField field, String prefix)
    {
        bindReferenceColumnsAndFK(svm,toClass,field,prefix,true);
    }
    
    protected Object visitCollectionMapping(ICollectionMapping mapping, Object argument)
    {
        IDatabaseTable collectionTable = mapping.getCollectionTable();
        
        if (collectionTable == null)
            return null;
        
        tablesStack.push(collectionTable);
        
        if (mapping.getElement() != null)
        {
            if (mapping.getElement() instanceof SimpleValueMapping) {
                SimpleValueMapping svm = (SimpleValueMapping) mapping.getElement();
                if (svm.isSimpleValue())
                {
                    columnName = Collection.DEFAULT_ELEMENT_COLUMN_NAME;
                }
            }
            
            if (collectionTable.getPersistentClassMappings() == null || 
                    collectionTable.getPersistentClassMappings().length == 0)
                mapping.getElement().accept(this,null);
            else
                mapping.getElement().accept(this,argument);

            columnName = null;
        }
        if (mapping.getKey() != null)
        {
            IHibernateKeyMapping key = mapping.getKey();

            if (collectionTable != null)
            {
                IPersistentField pf = getValuePersistentField(mapping);
                if (pf != null)
                {
                    IPersistentClass collectionOwner = pf.getMasterClass();
                    if (collectionOwner != null)
                    {
                        bindReferenceColumnsAndFK((SimpleValueMapping)key,collectionOwner,pf,safeCastToString(argument));
                        sharedColumnsCopier.process(key);
                    }
                }
            }
            else
            {
                key.accept(this,argument);
            }
        }

        tablesStack.pop();
        
        return null;
    }
    
    protected Object visitIndexedCollectionMapping(IIndexedCollectionMapping mapping, Object argument)
    {
        IDatabaseTable collectionTable = mapping.getCollectionTable();
        
        if (collectionTable == null)
            return null;
        
        tablesStack.push(collectionTable);
        
        visitCollectionMapping(mapping,argument);
        if (mapping.getIndex() != null)
        {
            String namePrefix = null;
            IPersistentValueMapping index = (IPersistentValueMapping)mapping.getIndex();
            
            if (index instanceof SimpleValueMapping) {
                SimpleValueMapping svm = (SimpleValueMapping) index;
                if (svm.isSimpleValue())
                {
                    columnName = IndexedCollection.DEFAULT_INDEX_COLUMN_NAME;
                }
            }
            
            if (collectionTable != null)
            {
                if (collectionTable.getPersistentClassMappings() != null && collectionTable.getPersistentClassMappings().length != 0)
                {
                    IPersistentField fld =  getValuePersistentField(mapping);
                    if (fld != null)
                    {
                        namePrefix = HibernateAutoMapping.propertyToColumnName(fld.getName());
                    }
                }
            }
            index.accept(this,StringUtils.safeStrCoupling(safeCastToString(argument),namePrefix));
        
            columnName = null;
        }
        
        tablesStack.pop();
        
        return null;
    }
    
    private String findUnMappedColumnName(IDatabaseTable table, String name, IPersistentValueMapping value)
    {
        String result = name;
        NamingProvider np = new NamingProvider();
        while (table.getColumn(result) != null && table.getColumn(result).getPersistentValueMapping() != null &&
                table.getColumn(result).getPersistentValueMapping() != value)
        {
            result = np.computeName(name);
        }
        
        return result;
    }
    
    private String findProperFKName(IDatabaseTable table, String name, IDatabaseTableForeignKey fk)
    {
        String result = name;
        NamingProvider np = new NamingProvider();
        while (table.getForeignKey(result) != null && !table.getForeignKey(result).equals(fk))
        {
            result = np.computeName(name);
        }
        
        return result;
    }

    protected IDatabaseColumn createAndBindColumn(ISimpleValueMapping valueMapping, String namePrefix) 
    {
        IDatabaseTable ownerTable = null;
        if (!tablesStack.isEmpty())
            ownerTable = (IDatabaseTable)tablesStack.peek();
        else
            return null;
        
        String name;

        String fieldName = null;
        if (columnName == null)
        {
            fieldName = valueMapping.getName();
        }
        else
        {
            fieldName = columnName;
        }
        
        if (fieldName == null || fieldName.length() == 0)
        {
            IPersistentField fld = getValuePersistentField(valueMapping);
            if (fld != null)
            {
                fieldName = fld.getName();
            }
        }
        
        if (fieldName == null)
            return null;
        
        if (namePrefix != null)
        {
            name = HibernateAutoMapping.propertyToTableName(namePrefix,fieldName);
        }
        else
        {
            name = HibernateAutoMapping.propertyToColumnName(fieldName);
        }
        
        name = findUnMappedColumnName(ownerTable,name,valueMapping);
        
        Type type = valueMapping.getType();
        if (type == null)
            return null;
        
        int columnTypeCode;
        if (type.isCustom())
            columnTypeCode = Type.BLOB.getSqlType();
        else
            columnTypeCode = type.getSqlType();
        
        Column column;
        if (ownerTable.getColumn(name) != null)
        {
            column = (Column)ownerTable.getColumn(name);
        }
        else
        {
            column = new Column();
            column.setName(name);
            column.setOwnerTable(ownerTable);
            column.setSqlTypeCode(columnTypeCode);
            ownerTable.addColumn(column);
            
            // added by Nick 16.06.2005
            if (type == Type.CHARACTER)
                column.setLength(1);
            // by Nick
        }
        column.setPersistentValueMapping(valueMapping);
        ((SimpleValueMapping)valueMapping).addColumn(column);
        return column;
    }
    
    protected IDatabaseColumn cloneColumn(IDatabaseTable toTable, IDatabaseColumn originalColumn, String prefixName, IPersistentValueMapping value)
    {
        IDatabaseColumn column = null;
        
        String columnName;
        String originalColumnName = originalColumn.getName();
        if (prefixName != null)
        {
            columnName = HibernateAutoMapping.propertyToTableName(prefixName,originalColumnName);
        }
        else
        {
            columnName = originalColumnName;
        }
        
        columnName = findUnMappedColumnName(toTable,columnName,value);

        if (toTable.getColumn(columnName) != null)
        {
            column = toTable.getColumn(columnName);
        }
        else
        {
            column = toTable.getOrCreateColumn(columnName);
            column.setLength(originalColumn.getLength());
            column.setOwnerTable(toTable);
            column.setPrecision(originalColumn.getPrecision());
            column.setScale(originalColumn.getScale());
            if (column.isNativeType())
            {
                column.setSqlTypeName(originalColumn.getSqlTypeName());
            }
            column.setSqlTypeCode(originalColumn.getSqlTypeCode());
        }
        
        return column;
    }

    public Object visitSimpleValueMapping(ISimpleValueMapping simple, Object argument) {
        if (simple.getColumnSpan() == 0)
        {
            createAndBindColumn(simple,safeCastToString(argument));
            sharedColumnsCopier.process(simple);
        }
        return null;
    }

    public Object visitAnyMapping(IAnyMapping mapping, Object argument) {
        return null;
    }

    public Object visitListMapping(IListMapping listMapping, Object argument) {
        return visitIndexedCollectionMapping(listMapping,argument);
    }

    public Object visitArrayMapping(IArrayMapping mapping, Object argument) {
        return visitIndexedCollectionMapping(mapping,argument);
    }

    public Object visitComponentMapping(IComponentMapping mapping, Object argument) {
        String prefix = null;

        IPersistentField field = getValuePersistentField(mapping);
        
        if (field != null)
        {
            prefix = StringUtils.safeStrCoupling(safeCastToString(argument),field.getName());
        }
        this.visitIOrmElementsIterator(mapping.getPropertyIterator(),prefix);
        return null;
    }

    public Object visitBagMapping(IBagMapping bagMapping, Object argument) {
        return visitCollectionMapping(bagMapping,argument);
    }

    public Object visitIdBagMapping(IIdBagMapping mapping, Object argument) {
        visitCollectionMapping(mapping,argument);
        if (mapping.getIdentifier() != null)
            mapping.getIdentifier().accept(this,argument);
        return null;
    }

    public Object visitPrimitiveArrayMapping(IPrimitiveArrayMapping constraint, Object argument) {
        return visitIndexedCollectionMapping(constraint,argument);
    }

    public Object visitMapMapping(IMapMapping mapping, Object argument) {
        return visitIndexedCollectionMapping(mapping,argument);
    }

    public Object visitSetMapping(ISetMapping mapping, Object argument) {
        return visitCollectionMapping(mapping,argument);
    }

    public Object visitOneToManyMapping(IOneToManyMapping mapping, Object argument) {
        return null;
    }

    public Object visitManyToManyMapping(IManyToManyMapping mapping, Object argument) {
        return null;
    }

    public Object visitManyToAnyMapping(IManyToAnyMapping mapping, Object argument) {
        return null;
    }

    public Object visitManyToOneMapping(IManyToOneMapping mapping, Object argument) {
        if (mapping.getColumnSpan() == 0)
        {
            PersistentClass referencedPc = (PersistentClass)hibernateMapping.findClass(mapping.getReferencedEntityName());
            bindReferenceColumnsAndFK((SimpleValueMapping)mapping,referencedPc,getValuePersistentField(mapping),safeCastToString(argument));    
        }
        sharedColumnsCopier.process(mapping);
        return null;
    }

    public Object visitOneToOneMapping(IOneToOneMapping mapping, Object argument) {
//        if (mapping.getColumnSpan() == 0)
//        {
//            PersistentClass referencedPc = (PersistentClass)hibernateMapping.findClass(mapping.getReferencedEntityName());
//            bindReferenceColumnsAndFK((SimpleValueMapping)mapping,referencedPc,getValuePersistentField(mapping),safeCastToString(argument));    
//        }
//        sharedColumnsCopier.process(mapping);

        //set columns to be unique
        UniqueConstraintBinderVisitor binderVisitor = new UniqueConstraintBinderVisitor(hibernateMapping);
        binderVisitor.visitIOrmElementsIterator(mapping.getColumnIterator(),null);

        return null;
    }

    public Object visitJoinMapping(IJoinMapping mapping, Object argument) {
        return null;
    }

    public Object visitRootClassMapping(IRootClassMapping mapping, Object argument) {
        return null;
    }

    public Object visitSubclassMapping(ISubclassMapping mapping, Object argument) {
        return null;
    }

    public Object visitJoinedSubclassMapping(IJoinedSubclassMapping mapping, Object argument) {
        return null;
    }

    public Object visitUnionSubclassMapping(IUnionSubclassMapping mapping, Object argument) {
        return null;
    }

    public Object visitPropertyMapping(IPropertyMapping mapping, Object argument) {
        if (mapping.getValue() != null)
            return mapping.getValue().accept(this,argument);
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
        return null;
    }

    public Object visitMapping(IMapping mapping, Object argument) {
        IPersistentClassMapping[] mappings = mapping.getPersistentClassMappings();
        for (int i = 0; i < mappings.length; i++) {
            if (mappings[i] != null)
            {
                mappings[i].accept(this,argument);
            }
        }
        return null;
    }

    public Object visitMappingStorage(IMappingStorage storage, Object argument) {
        return null;
    }

    public Object visitPersistentClass(IPersistentClass clazz, Object argument) {
        if (clazz.getPersistentClassMapping() != null)
        {
            IPersistentField[] fields = clazz.getFields();
            for (int i = 0; i < fields.length; i++) {
                IPersistentField field = fields[i];
                field.accept(this,null);
            }
            
            return clazz.getPersistentClassMapping().accept(this,argument);
        }
        return null;
    }

    public Object visitPersistentField(IPersistentField field, Object argument) {

        Object result = null;
        
        if (field.getOwnerClass() != null)
        {
            IPersistentClassMapping mapping = field.getOwnerClass().getPersistentClassMapping();
            if (mapping != null && mapping.getDatabaseTable() != null)
            {
                tablesStack.push(mapping.getDatabaseTable());
                if (field.getMapping() != null)
                    result = field.getMapping().accept(this,argument);
                tablesStack.pop();
            }
        }
        return result;
    }

    public Object visitPersistentClassMapping(IPersistentClassMapping mapping, Object argument) {
        if (mapping != null)
        {
            visitIOrmElementsIterator(mapping.getFieldMappingIterator(),mapping.getDatabaseTable());
        }
        return null;
    }

    public Object visitPersistentFieldMapping(IPersistentFieldMapping mapping, Object argument) {
        if (mapping.getPersistentValueMapping() != null)
        {
            if (mapping.getPersistentField() != null
                    && mapping.getPersistentField().getName() != null)
            {
                return mapping.getPersistentValueMapping().accept(this,
                        StringUtils.safeStrCoupling(
                                mapping.getPersistentField().getName(),safeCastToString(argument)
                        ));
            }
            else
                return mapping.getPersistentValueMapping().accept(this,argument);
        }
        return null;
    }

    public Object visitPersistentValueMapping(IPersistentValueMapping mapping, Object argument) {
        return null;
    }

    // add tau 27.07.2005
	public Object visitNamedQueryMapping(INamedQueryMapping mapping, Object argument) {
		return null;
	}

	public Object visitDatabaseColumn(org.hibernate.mapping.Column column, Object argument) {
		// TODO Auto-generated method stub
		return null;
	}

}
