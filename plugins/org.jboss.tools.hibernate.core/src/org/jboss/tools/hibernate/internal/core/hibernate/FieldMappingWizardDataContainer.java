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

/**
 * 
 * @author yk
 *
 */

import org.eclipse.core.runtime.CoreException;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.hibernate.core.*;
import org.jboss.tools.hibernate.core.hibernate.ICollectionMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.data.Table;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.ColumnBuilderVisitor;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.ConfigurationReader;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMappingHelper;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.MappingsFactory;
import org.jboss.tools.hibernate.internal.core.util.ScanProject;
import org.jboss.tools.hibernate.internal.core.util.TypeAnalyzer;
import org.jboss.tools.hibernate.internal.core.util.TypeUtils;

public class FieldMappingWizardDataContainer {
    private IPersistentField theField;

    private MappingsFactory factory;
    
    private IMapping 	theMapping;
    private String 		persistClassName;
    private IOrmElement	parent;
    // added by Nick 15.06.2005
    private HibernateAutoMappingHelper helper;
    // by Nick
    
    public FieldMappingWizardDataContainer(IMapping mapping,
           IPersistentField field, IOrmElement parentElement,String pcname) throws CoreException
    {
        theMapping = mapping;
        theField = field;
        //10.06.05 akuzmin
        persistClassName = pcname;
        parent = parentElement;
        helper = new HibernateAutoMappingHelper(
                ConfigurationReader
                        .getAutomappingConfigurationInstance((HibernateMapping) mapping));

        factory = MappingsFactory.getFullFactory(helper.getConfiguration(),new ColumnBuilderVisitor((HibernateMapping)theMapping));
        
        if (field.getMapping() == null)
        {
//            helper.createAndBindPropertyMapping(field, mapping, ownerName, parentElement);
            helper.createAndBindPropertyMapping(field, parentElement);
        }
    }

    private ClassMapping createClassMapping()
    {
    	return (ClassMapping)theMapping.findClass(persistClassName).getPersistentClassMapping();
    }

    // added by Nick 15.06.2005
    private void createAndBindCollectionTable(CollectionMapping mapping)
    {
        if (mapping == null)
            return ;
            
        IPersistentClass ownerClass = theField.getOwnerClass();
        IPersistentClassMapping ownerMapping = null;
        String className = null;
        
        if (ownerClass != null)
        {
            ownerMapping = ownerClass.getPersistentClassMapping();
            className = ownerClass.getShortName();
        }
        
        String tableName = null;
        
        if (className != null)
            tableName = HibernateAutoMapping.propertyToTableName(className,theField.getName());
        else
            tableName = HibernateAutoMapping.propertyToColumnName(theField.getName());
        
        IDatabaseTable collTable = HibernateAutoMappingHelper.getOrCreateTableInMappingSchema(ownerMapping,tableName,(HibernateMapping)theMapping);
        mapping.setCollectionTable(collTable);
    }
    
    private HibernateAutoMappingHelper createAutoMappingHelper()
    {
        HibernateAutoMappingHelper helper = new HibernateAutoMappingHelper(
                ConfigurationReader
                        .getAutomappingConfigurationInstance((HibernateMapping) theMapping));

        if (theField.getMapping() == null)
        {
            helper.createAndBindPropertyMapping(theField);
        }
        return helper;
    }

    public IPersistentValueMapping createOneToManyMapping()
    {
        OneToManyMapping value = new OneToManyMapping(
                createClassMapping());
        value.setFieldMapping(theField.getMapping());
        
        String clazzName = factory.toClassCollectionFinder(theField,theField.getType(),true);
        IPersistentClass clazz = null;
        if (clazzName != null)
            clazz = theMapping.findClass(clazzName);
        String refEntity = null;
        if (clazz == null)
        {
            clazz = theField.getMasterClass();
            refEntity = clazz.getName();
        }
        else
        {
            refEntity = clazz.getName();
        }
        
        value.setReferencedEntityName(refEntity);
        
        return value;
    }

    public IPersistentValueMapping createSimpleValueMapping()
    {
    	// add by yk 08.06.2005.
    	SimpleValueMapping svm = null;
    	ClassMapping cm = createClassMapping();
    	if(cm == null){		return null;	}
   
    	IDatabaseTable table = null;
    	if(theField.getMapping().getPersistentValueMapping() instanceof ICollectionMapping)
    	{
    		table = ( (ICollectionMapping)theField.getMapping().getPersistentValueMapping() ).getCollectionTable();
    	}
    	else
    	{
    		table = cm.getDatabaseTable();
    	}
    	if(table == null){	return null;	}
		svm = new SimpleValueMapping(table); 
    	// add by yk 08.06.2005 stop.

        if (theField != null)
        {
            Type type = TypeUtils.javaTypeToHibType(theField.getType());
            if (type == null)
                type = Type.getOrCreateType(theField.getType());
            svm.setType(type);
        }
        svm.setFieldMapping(theField.getMapping());
        return svm;
    }

    public IPersistentValueMapping createManyToManyMapping()
    {
        IPersistentValueMapping value = new ManyToManyMapping(
                createClassMapping().getDatabaseTable());
        value.setFieldMapping(theField.getMapping());
        return value;
    }

    public IPersistentValueMapping createManyToOneMapping()
    {
        IPersistentValueMapping value =  new ManyToOneMapping(
                createClassMapping().getDatabaseTable());
        value.setFieldMapping(theField.getMapping());
        return value;
    }

    public IPersistentValueMapping createOneToOneMapping()
    {
        ClassMapping cm = createClassMapping();

        IPersistentValueMapping value =  new OneToOneMapping(cm
                .getDatabaseTable(), cm.getIdentifier());
        value.setFieldMapping(theField.getMapping());
        return value;
    }

    public IPersistentValueMapping createComponentMapping()
            throws CoreException
    {
        IPersistentValueMapping value =  DoComponentMapping();
        if (value != null)
            value.setFieldMapping(theField.getMapping());
        return value;
    }

    public IPersistentValueMapping createAnyMapping()
    {
        IPersistentValueMapping value =  new AnyMapping(createClassMapping()
                .getDatabaseTable());
        value.setFieldMapping(theField.getMapping());
        return value;
    }

    public IPersistentValueMapping createManyToAnyMapping()
    {
        IPersistentValueMapping value =  new ManyToAnyMapping(createClassMapping()
                .getDatabaseTable());
        value.setFieldMapping(theField.getMapping());
        return value;
    }
    
    public CollectionMapping createBagMapping(boolean isCreateTable)
    {
        CollectionMapping value = new BagMapping(createClassMapping());
        value.setFieldMapping(theField.getMapping());
        if (isCreateTable)  createAndBindCollectionTable(value);
        return value;
    }

    public CollectionMapping createIdBagMapping(boolean isCreateTable)
    {
        CollectionMapping value = new IdBagMapping(createClassMapping());
        value.setFieldMapping(theField.getMapping());
        if (isCreateTable)  createAndBindCollectionTable(value);
        return value;
    }

    public CollectionMapping createListMapping(boolean isCreateTable)
    {
        CollectionMapping value = new ListMapping(createClassMapping());
        value.setFieldMapping(theField.getMapping());
        if (isCreateTable)  createAndBindCollectionTable(value);
        return value;
    }

    public CollectionMapping createSetMapping(boolean isCreateTable)
    {
        CollectionMapping value = new SetMapping(createClassMapping());
        value.setFieldMapping(theField.getMapping());
        if (isCreateTable)  createAndBindCollectionTable(value);
        return value;
    }
    
    public CollectionMapping createMapMapping(boolean isCreateTable)
    {
        CollectionMapping value = new MapMapping(createClassMapping());
        value.setFieldMapping(theField.getMapping());
        if (isCreateTable)  createAndBindCollectionTable(value);
        return value;
    }

    public CollectionMapping createArrayMapping(boolean isCreateTable)
    {
        CollectionMapping value = new ArrayMapping(createClassMapping());
        value.setFieldMapping(theField.getMapping());
        if (isCreateTable)  createAndBindCollectionTable(value);
        return value;
    }
	
    private IPersistentValueMapping DoComponentMapping() throws CoreException
    {// code from alexey.
        IType fieldType = null;

        IDatabaseTable ownerTable;

        IHibernateClassMapping hcm = (IHibernateClassMapping) createClassMapping();

        if ((HibernateAutoMappingHelper.getLinkedType(theField.getType()) != 0)
                && (theField.getMapping().getPersistentValueMapping() instanceof CollectionMapping))
        {

            ownerTable = ((CollectionMapping) theField.getMapping()
                    .getPersistentValueMapping()).getCollectionTable();
// added by yk Jun 21, 2005
            if(theField.getMapping().getPersistentValueMapping() instanceof ArrayMapping)
            {
            	String name = org.eclipse.jdt.core.Signature.getElementType(theField.getType());
                fieldType = ScanProject.findClass(name,
                        ((HibernateConfiguration) theMapping.getConfiguration())
                                .getProject());
            }
// added by yk Jun 21, 2005 stop

        }
        else
        {
            ownerTable = hcm.getDatabaseTable();

            fieldType = ScanProject.findClass(theField.getType(),
                    ((HibernateConfiguration) theMapping.getConfiguration())
                            .getProject());
        }

        TypeAnalyzer ta = null;

        PersistableProperty[] componentProps = new PersistableProperty[0];

        if (fieldType != null)
        {
            ta = new TypeAnalyzer(fieldType);

            componentProps = ta.getPropertiesByMask(
                        ConfigurationReader.getAutomappingConfigurationInstance((HibernateMapping) theMapping).getPropertyAccessorMask()        
                );
        }

        ComponentMapping value = null;
        
        if (componentProps != null)
        {
            HibernateAutoMappingHelper helper = createAutoMappingHelper();
            MappingsFactory factory = MappingsFactory.getFullFactory(helper.getConfiguration(), new ColumnBuilderVisitor((HibernateMapping)theMapping));
            value = factory.componentMappingFactory(
                            (ClassMapping) hcm,
                            fieldType,
                            componentProps,
                            HibernateAutoMapping.propertyToColumnName(theField
                                    .getName()), (Table) ownerTable, ta, false);
            factory.process();
            if (value != null)
                value.setFieldMapping(theField.getMapping());
        }
        
        return value;
    }

}
