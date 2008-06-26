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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections.SequencedHashMap;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IDatabaseTableColumnSet;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.hibernate.ICollectionMapping;
import org.jboss.tools.hibernate.core.hibernate.IComponentMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateValueMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToManyMapping;
import org.jboss.tools.hibernate.core.hibernate.IOneToOneMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMappingHolder;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.AbstractMapping;
import org.jboss.tools.hibernate.internal.core.PersistentClass;
import org.jboss.tools.hibernate.internal.core.PersistentField;
import org.jboss.tools.hibernate.internal.core.data.PrimaryKey;
import org.jboss.tools.hibernate.internal.core.hibernate.ArrayMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.BagMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ClassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.CollectionMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ComponentMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateConfiguration;
import org.jboss.tools.hibernate.internal.core.hibernate.ListMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.MapMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.OneToManyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.OneToOneMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.PersistableProperty;
import org.jboss.tools.hibernate.internal.core.hibernate.PrimitiveArrayMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.PropertyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SetMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ToOneMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMapping.DeferredPropertyHolder;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.SharedColumnsCopier.BindToPKPostCopy;
import org.jboss.tools.hibernate.internal.core.util.ClassUtils;
import org.jboss.tools.hibernate.internal.core.util.ScanProject;
import org.jboss.tools.hibernate.internal.core.util.SearchUtils;
import org.jboss.tools.hibernate.internal.core.util.StringUtils;
import org.jboss.tools.hibernate.internal.core.util.TypeAnalyzer;
import org.jboss.tools.hibernate.internal.core.util.TypeUtils;

/**
 * @author Nick - mailto:n.belaevski@exadel.com
 */
public class MappingsFactory {
    
    private HibernateConfiguration config;
    private ConfigurationReader hamConfig;
    private AbstractMapping mapping;
    private Map<IPersistentClass,HashMap<IPersistentClass,Vector<DeferredPropertyHolder>>> collectionsMapping;
    private SequencedHashMap deferredProperties;
    private HibernateAutoMappingHelper helper;

    private ColumnBuilderVisitor visitor;
    
    public static final String BLOB_SIGNATURE = Signature.createArraySignature("byte",1);
    private static final String COLLECTION_TYPE = "[";
    public static final SimpleValueMapping DEFERRED_VALUE = new SimpleValueMapping();

    private int state = 0;
    private static final int STATE_FULL_PROCESSING = 0;
    private static final int STATE_SIMPLE_PROCESSING = 1;
    
    //contains strings describing types forbidden to be created value for now as keys
    //and with strings describing reasons as elements
    private Map<String,String> restrictedTypesMap = new HashMap<String,String>();
    
    public static MappingsFactory getFullFactory(ConfigurationReader hamConfigReader, ColumnBuilderVisitor visitor) {
        MappingsFactory factory = new MappingsFactory(hamConfigReader,visitor);
        factory.state = STATE_FULL_PROCESSING;
        return factory;
    }
    
    public static MappingsFactory getSimpleFactory(ConfigurationReader hamConfigReader, ColumnBuilderVisitor visitor)
    {
        MappingsFactory factory = new MappingsFactory(hamConfigReader,visitor);
        factory.state = STATE_SIMPLE_PROCESSING;
        return factory;
    }
    
    public void addReferringPersistentField(IPersistentFieldMapping pFieldMapping, IPersistentClass toClass, String namePrefix)
    {
        if (pFieldMapping != null && pFieldMapping.getPersistentField() != null && pFieldMapping.getPersistentField().getType() != null)
            deferField(pFieldMapping,pFieldMapping.getPersistentField().getType(),toClass,namePrefix);
    }

    private void traverseUnmappedFields(IPersistentValueMapping value, String namePrefix)
    {
        if (value == null)
            return ;
        
        if (value instanceof ComponentMapping)
        {
            ComponentMapping cmp = (ComponentMapping) value;
            IPersistentClass cmpOwner = cmp.getComponentClass();
            if (cmpOwner != null)
            {
                IPersistentField[] fields = cmpOwner.getFields();
                if (fields != null)
                {
                    boolean removeComponentRestrictionOnTraverse = !restrictedTypesMap.containsKey(cmpOwner.getName());
                    
                    if (removeComponentRestrictionOnTraverse)
                        restrictedTypesMap.put(cmpOwner.getName(),"");
                    
                    for (int i = 0; i < fields.length; i++) {
                        IPersistentField field = fields[i];

                        generatePersistentFieldMapping(field,cmp,namePrefix);
                    }

                    if (removeComponentRestrictionOnTraverse)
                        restrictedTypesMap.remove(cmpOwner.getName());
                }
            }
        }
        if (value instanceof CollectionMapping)
        {
            boolean removeCollectionRestrictionOnTraverse = !restrictedTypesMap.containsKey(COLLECTION_TYPE);
            
            Map<String,String> backupRestrictionMap = new HashMap<String,String>();
            
            if (removeCollectionRestrictionOnTraverse) {
                backupRestrictionMap.putAll(restrictedTypesMap);
                restrictedTypesMap.clear();
                restrictedTypesMap.put(COLLECTION_TYPE,"");
                
                CollectionMapping collection = (CollectionMapping) value;
                if (collection.getElement() != null && collection.getElement() instanceof ComponentMapping)
                    traverseUnmappedFields(collection.getElement(),namePrefix);
                
                restrictedTypesMap.remove(COLLECTION_TYPE);
                restrictedTypesMap.putAll(backupRestrictionMap);
            }
        }
    }
    
    private void generatePersistentFieldMapping(IPersistentField pField, IPropertyMappingHolder mapping, String namePrefix)
    {
        if (pField.getMapping() != null && pField.getMapping().getPersistentValueMapping() != null)
        {
            traverseUnmappedFields(pField.getMapping().getPersistentValueMapping(),StringUtils.safeStrCoupling(namePrefix,pField.getName()));
            return ; //mapping exists
        }
        if (pField.getType() == null)
            return ; // no type info - skip it
        if ((pField.getAccessorMask() & hamConfig.getPropertyAccessorMask()) == 0) //wrong accessor
            return ;
        
        //XXX Nick(5) implement ValueMapping that depends on field
        // type
        // use mappingsFactory method
        //XXX Nick(5) Move the code below into
        // 'getQualifiedNameFromSignature' method so it can be
        // reusable
        //code moved to
        // org.jboss.tools.hibernate.internal.core.util.ClassUtils#getQualifiedNameFromSignature
        
        if (state != STATE_SIMPLE_PROCESSING && hamConfig.isIgnored(pField))
            return ;
        
        if (pField.getMapping() == null)
        {
            helper.createPropertyMapping(pField);
            mapping.addProperty((PropertyMapping)pField.getMapping());
        }
        
        this.addPersistentField(pField,pField.getType(),namePrefix);
//        if (pField.getMapping() != null && pField.getMapping() instanceof PropertyMapping)
//        {
//            mapping.addProperty((PropertyMapping)pField.getMapping());
//        }
    }
    

    private void addPersistentField(IPersistentField pField, String fieldType, String namePrefix)
    {
        IPersistentClass clazz = pField.getOwnerClass();
        ClassMapping classMapping = (ClassMapping)clazz.getPersistentClassMapping();
        if (classMapping != null) {
            IPersistentValueMapping valueMapping;
            try {
                valueMapping = mappingsFactory(pField, classMapping,
                        classMapping.getDatabaseTable(),namePrefix);
                if (valueMapping != null)
                {
                    if (valueMapping != DEFERRED_VALUE)
                    {
                        HibernateAutoMappingHelper.bindValueMappingToField(pField,
                                valueMapping);
                        pField.accept(visitor,namePrefix);
                    }
                }
                else
                {
                    pField.setMapping(null);
                }
            } catch (CoreException e) {
            	OrmCore.getPluginLog().logError("Exception adding persistent field to mappings factory",e);
                removeDeferredOnCrash(pField);
            } finally {
            }
        }
        
    }
    
    public void addPersistentField(IPersistentField pField, String namePrefix) {
        if (pField.getOwnerClass() != null && pField.getType() != null) {
            ClassMapping classMapping = (ClassMapping)pField.getOwnerClass().getPersistentClassMapping();
            if (classMapping != null) {
                generatePersistentFieldMapping(pField,classMapping,namePrefix);
            }
        }
    }

    public void process()
    {
        processDeferredFields();
    }
    
    private void removeDeferredOnCrash(IPersistentField field)
    {
        DeferredPropertyHolder holder = (DeferredPropertyHolder)deferredProperties.get(field);
        if (holder != null)
        {
            removeHolder(holder);
        }
        deferredProperties.remove(field);
    }

    private void initFactory(ConfigurationReader hamConfig) {
        collectionsMapping = new HashMap<IPersistentClass,HashMap<IPersistentClass,Vector<DeferredPropertyHolder>>>();
        deferredProperties = new SequencedHashMap();

        this.mapping = hamConfig.getMapping();
        this.config = (HibernateConfiguration)this.mapping.getConfiguration();
        this.helper = new HibernateAutoMappingHelper(hamConfig);
        this.hamConfig = hamConfig;
    }
    
    private MappingsFactory(ConfigurationReader hamConfig, ColumnBuilderVisitor visitor)
    {
        super();
        initFactory(hamConfig);
        this.visitor = visitor;
    }
    
/*
    private SimpleValueMapping binaryTypesMappingsFactory(IType field) {
        SimpleValueMapping mapping = new SimpleValueMapping();
        mapping.setType(Type.SERIALIZABLE);
        mapping.setName(HibernateAutoMapping.propertyToColumnName(field.getElementName()));
        return mapping;
    }
*/
    
    /**
     * map simples and components using custom table - suitable for elements of arrays
     * @param fieldName
     * @param fQTypeName
     * @param pcMapping
     * @param ownerTable
     * @return
     * @throws CoreException
     */
    SimpleValueMapping simpleMappingsFactory(String fieldName,
            String fQTypeName, IPersistentClassMapping pcMapping, IDatabaseTable ownerTable, String namePrefix) throws CoreException {
        
        SimpleValueMapping valueMapping = null;
        
        if (fQTypeName == null || pcMapping == null)
            return null;
        
        String fieldMappingName = HibernateAutoMapping.propertyToColumnName(fieldName);
        
        if (fQTypeName.equals(BLOB_SIGNATURE))
        {
            //map array of bytes as blob type
            valueMapping = new SimpleValueMapping();
            ((SimpleValueMapping)valueMapping).setType(Type.BINARY);
        }
        else
        {
            if (Signature.getArrayCount(fQTypeName) != 0)
                return null;
            
            if (TypeUtils.javaTypeToHibType(fQTypeName) != null /*ClassUtils.isSimpleType(fQTypeName)*/) 
            {
                SimpleValueMapping fieldMapping = new SimpleValueMapping();
                //XXX Nick How about java.lang.Integer and others? They should be
                // mapped the same way as primitive types
                // they do so... this part of code is aimed to skip is-component
                // check with project search for simple types
                
                //XXX Nick String, Class, Locale, Date etc. should be mapped here
                // using hibernate types
                //XXX.toAlex (Nick) 7.03.2005 do we need some extra functionality
                // for that other than getOrCreateType()?
                // I suppose that simple java types should also be added to Type as
                // statics. Add them?
                //XXX Nick (5) You do not understand hibernate type system
                // correctly. Hibernate type defines a way how to map a field to a
                // column.
                // There is set of predefined types: see Type.* constants. And new
                // type means that there is a class that maps a field(s) to a
                // column(s).
                //Please, read Hibernate in action book ch 6.1.1
                // ok
                fieldMapping.setType(TypeUtils.javaTypeToHibType(fQTypeName));
                fieldMapping.setName(fieldMappingName);
                //createAndBindColumn(fieldMapping,fieldMappingName,ownerTable);
                
                valueMapping = fieldMapping;
                //XXX Here you should map field that has simple type or type that
                // you can map using standard hibernate type
                //XXX Nick Map fields with primitive types too!
                //XXX.toAlex aren't they already mapped? javaTypeToHibType() maps
                // all 8 primitive types
                // and ClassUtils.isSimpleType() first checks if type is primitive.
                //Ok.Sorry I missed it
            }
            else
            {
                //XXX Nick Implement many-to-one class detection and component
                // types detection
                //XXX Here you should map field that has a persistent type . Why do
                // you use getSuperClass? It is senseless.
                //XXX.toAlex (Nick) class2hbm util does mapping like that. Quote:
                // "If the property's type is any other class, MapGenerator defers
                // the decision on the database
                // representation until all classes have been processed. At this
                // point, if the class was discovered
                // through the superclass search described above, then the property
                // is an many-to-one association"
                //
                // superclass search described above - search for superclass with a
                // property that has a name appearing on a list
                // of candidate UID names
                //XXX I still don't understand. As I understand all types can be
                // divided into the following categories:
                // simple(including primitive), persistent, collections(arrays),
                // components, interfaces or abstract
                // 
                //XXX.toAlex 5.1.9 section Hib3 reference. We can also store
                // serializable and custom types.
                // Class can be serializable and not binary. But not all
                // serializable classes are persistent,
                // they may or may not provide identity. We can just check class for
                // custom type and serializabliness
                //
                // this piece of code is used to detect link from class to one of
                // its superclass
                // or to class itself. We can suppose that if it is that case, the
                // property just contains the link to
                // its parent object. class2hbm works with this assumption. We can
                // throw this away if we don't need it.
                //XXX Nick It's fine. But you should map fields with persistent
                // types. Use model.findPersistentClass(fQTypeName)!=null to detect
                // persistent class
                //You should assume that all references on persistent classes are
                // many-to-one associations. And sometime they may be bidirectional.
                // You should support them too.
                //Be aware that referenced persistent class may be not mapped yet.
                IPersistentClass foreignPC = mapping.findClass(fQTypeName);
                
                //skip copying identifier if classes not mapped yet when invoked from generateIdentity
                if (foreignPC != null && foreignPC.getPersistentClassMapping() != null && 
                        ((ClassMapping)foreignPC.getPersistentClassMapping()).getIdentifier() != null)
                {
                    SimpleValueMapping fk = HibernateAutoMappingHelper.createAndBindFK(((ClassMapping)foreignPC.getPersistentClassMapping()).getIdentifier(),ownerTable,fieldMappingName,true);
                    fk.setName(fieldMappingName);
                    valueMapping = fk;
                }
                else
                {
                    //XXX.toAlex (Nick) here we skip field which types are from
                    // binary files, not from project source, OK?
                    // if we don't skip'em even java.lang.String becomes a component
                    // with "bytes" field
                    //XXX Nick Move the code that map "binary" types to a method
                    // and use SERIALIZABLE hibernate type for such types. Later we
                    // should implement
                    //full auto mapping for such types.
                    // 
                    
                    //XXX Here falls all other types that can be binary or
                    // component. It seems you did it right. Check it again
                    IType fieldType = ScanProject.findClass(fQTypeName, config
                            .getProject());
                    if (fieldType != null)
                    {
                        PersistableProperty[] componentProps;
                        TypeAnalyzer ta = new TypeAnalyzer(fieldType);

                        if ( (componentProps = ta.getPropertiesByMask(hamConfig.getPropertyAccessorMask())) != null && 
                                componentProps.length != 0)
                        {
                            valueMapping = componentMappingFactory(pcMapping,fieldType,componentProps,StringUtils.safeStrCoupling(namePrefix,fieldName),ownerTable,ta);
                            if (valueMapping == null)
                                return null;
                        }
                    }
                    
                    if (valueMapping == null)
                    {
                        SimpleValueMapping fieldMapping = new SimpleValueMapping();
                        // any other classes mapped as CLASS hibernate data type
                        fieldMapping.setType(Type.SERIALIZABLE); //XXX Is it
                        // correct? See
                        // comment above
                        fieldMapping.setName(fieldMappingName);
                        //createAndBindColumn(fieldMapping,fieldMapping.getName(),ownerTable);
                        valueMapping = fieldMapping;
                    }
                }
            }
            
        }
        if (valueMapping != null)
            valueMapping.setTable(ownerTable);
        
        return valueMapping;
    }

    public ComponentMapping componentMappingFactory(IPersistentClassMapping pcMapping, IType fieldType, PersistableProperty[] componentProps, String fieldMappingName, IDatabaseTable ownerTable, TypeAnalyzer ta) throws CoreException
    {
        return componentMappingFactory(pcMapping,fieldType,componentProps,fieldMappingName,ownerTable,ta,true);
    }
    
    //akuzmin 11.05.2005
    public ComponentMapping componentMappingFactory(IPersistentClassMapping pcMapping, IType fieldType, PersistableProperty[] componentProps, String fieldMappingName, IDatabaseTable ownerTable, TypeAnalyzer ta, boolean processIgnoredList) throws CoreException
    {
        if (state == STATE_SIMPLE_PROCESSING)
        {
            if (pcMapping != null && pcMapping.getPersistentClass() != null)
            {
                restrictedTypesMap.put(pcMapping.getPersistentClass().getName(),"");
            }
        }
        
        if (fieldType != null)
        {
            if (!restrictedTypesMap.containsKey(fieldType.getFullyQualifiedName()))
            {
                restrictedTypesMap.put(fieldType.getFullyQualifiedName(),"");
            }
            else
            {
                //XXX.toAlex (Nick) 9.03.2005 cycled component mapping.
                // Throw exception or process one end of cycled
                // component as Type.CLASS? Just skip this mapping
                return null;
            }
        }
        
        if (pcMapping == null || componentProps == null)
            return null;
        
        // that's a component cause there are fields
        ComponentMapping cmpMapping = new ComponentMapping((ClassMapping)pcMapping);
        
        if (fieldType != null && ta != null)
        {
            if (!ClassUtils.isImplementing(fieldType,"java.io.Serializable"))
            {
                //TODO Nick uncomment conformity markers
                
                /*                if (!fieldType.isBinary())
                 PersistentClassConformity.classConformity.createMarker(fieldType.getResource(),"Component classes should implement Serializable interface",IMarker.SEVERITY_ERROR,"HAM-W002",null);
                 else
                 return null;
                 */            }
            
            if (!ta.hasEqualsMethod())
            {
                /*                if (!fieldType.isBinary())
                 PersistentClassConformity.classConformity.createMarker(fieldType.getResource(),"Component classes should implement equals() method",IMarker.SEVERITY_ERROR,"HAM-W003",null);
                 else
                 return null;
                 */            }
            
            if (!ta.hasHashCodeMethod())
            {
                /*                if (!fieldType.isBinary())
                 PersistentClassConformity.classConformity.createMarker(fieldType.getResource(),"Component classes should implement hashCode() method",IMarker.SEVERITY_ERROR,"HAM-W004",null);
                 else
                 return null;
                 */            }
        }
        
        if (fieldType != null)
            cmpMapping.setComponentClassName(fieldType.getFullyQualifiedName());
        
        cmpMapping.setName(fieldMappingName);
        cmpMapping.setTable(ownerTable);
        
        IPersistentClass cmpClass = cmpMapping.getComponentClass();
        cmpClass.setPersistentClassMapping(pcMapping);
        
        //XXX Nick Map component fields
        
        if (componentProps != null)
        {
            for (int k = 0; k < componentProps.length; k++) {
                
                if (processIgnoredList && state != STATE_SIMPLE_PROCESSING && hamConfig.isIgnored(componentProps[k]))
                    continue ;
                
                String fQFieldTypeName = componentProps[k].getType();
                String[] generifiedTypes = componentProps[k].getGenerifiedTypes();
                
                String typeName = fQFieldTypeName;
                if (HibernateAutoMappingHelper.getLinkedType(fQFieldTypeName) == HibernateAutoMappingHelper.LT_ARRAY)
                {
                    typeName = Signature.getElementType(typeName);
                }
                
                boolean isInnerCollectionRestrictedField = false;
//                boolean isCollectionType = false;
                
                int type = HibernateAutoMappingHelper.getLinkedType(fQFieldTypeName);
                if (type == HibernateAutoMappingHelper.LT_COLLECTION || type == HibernateAutoMappingHelper.LT_ARRAY)
                {
                    if (restrictedTypesMap.containsKey(COLLECTION_TYPE))
                        isInnerCollectionRestrictedField = true;
                }
                
                String fieldName = componentProps[k].getName();
                PersistentField pf = (PersistentField)HibernateAutoMapping.createPersistentFieldByType(cmpClass,fieldName,fQFieldTypeName,generifiedTypes);
                pf.addAccessorMask(componentProps[k].getAccessorMask());
                
                if (!restrictedTypesMap.containsKey(typeName) && !isInnerCollectionRestrictedField)
                {
                    PropertyMapping pm = helper.createPropertyMapping(pf);
                    
                    IHibernateValueMapping value = mappingsFactory(pf,pcMapping,ownerTable,
                            fieldMappingName);
                    if (value != null && value != DEFERRED_VALUE) 
                    {
                        HibernateAutoMappingHelper.bindValueMappingToField(pf,value);
                        pm.setPersistentClassMapping((ClassMapping)pcMapping);
                    }

                    cmpMapping.addProperty(pm);
                }
            }
        }
        
        if (fieldType != null)
            restrictedTypesMap.remove(fieldType.getFullyQualifiedName());
        
        return cmpMapping;
    }

    /**
     * returns appropriate mapping for field discovered by field's FQ name - creates mapping only for
     * simple types and valued collections
     * 
     * @param fieldName -
     *            the name of the field we map
     * @param fQTypeName -
     *            fully qualified type name of the field
     * @param pcMapping -
     *            owning persistent class mapping
     * @return value mapping for that field or null if field cannot be mapped
     * @throws CoreException
     */
    IHibernateValueMapping mappingsFactory(IPersistentField field, IPersistentClassMapping pcMapping, IDatabaseTable ownerTable, String namePrefix) throws CoreException {

        String fQTypeName = field.getType();
        String fieldName = field.getName();

        if (fQTypeName == null || fieldName == null)
            return null;
        
        String toClassName = null;
        
        if (HibernateAutoMappingHelper.getLinkedType(fQTypeName) != HibernateAutoMappingHelper.LT_NONE)
        {
            toClassName = toClassCollectionFinder(field,fQTypeName,false);
        }
            
        if (!fQTypeName.equals(BLOB_SIGNATURE))
        {
            if (state != STATE_FULL_PROCESSING)
            {
                //processing identifiers
                PersistentClass pc = (PersistentClass)mapping.findClass(fQTypeName);
                if (pc != null)
                {
                    return helper.createManyToOneMapping(field,pc);
                }
            }
            else
            {
                IPersistentClass clazz = null;
                if (toClassName != null)
                {
                    clazz = mapping.findClass(toClassName);
                }
                //try to discover if this field belongs to PC's linkage 
                DeferredPropertyHolder deferred = deferField(field.getMapping(),fQTypeName,clazz,namePrefix);
                if (deferred != null)
                {
                    //this field is collection or array of PC's
                    //put it into queue for further processing
                    return DEFERRED_VALUE;
                }
            }
        }

        IHibernateValueMapping value = null;
        
        if (HibernateAutoMappingHelper.getLinkedType(fQTypeName) == HibernateAutoMappingHelper.LT_NONE)
            value = simpleMappingsFactory(fieldName, fQTypeName, pcMapping, ownerTable,namePrefix);
        else
            if (state == STATE_FULL_PROCESSING)
            {
                value = linkedValuesMappingsFactory(fieldName,fQTypeName,pcMapping,namePrefix,
                        toClassName);
            }

        return value;
    }

    final static int DF_NONE = 0;
    final static int DF_ONE = 1;
    final static int DF_MANY = 2;
    
    private SequencedHashMap typesMap = null;
    
    private class TypeSearchHolder implements SearchUtils.IClassSearchInfoHolder
    {
        String packageName;
        String stemmedTypeName;
        String typeName;
        
        TypeSearchHolder(String typeName, String packageName)
        {
            this.typeName = typeName;
            this.packageName = packageName;
        }
        
        public String getFullyQualifiedName()
        {
            return Signature.toQualifiedName(new String[]{packageName,typeName});
        }
        
        public String getShortName()
        {
            if (!hamConfig.isUseFuzzySearch())
            {
                if (stemmedTypeName == null)
                    stemmedTypeName = SearchUtils.getStemmedString(typeName);
                return stemmedTypeName;
            }
            else
                return typeName;
        }
    }
    
    private void initTypesMap()
    {
        if (typesMap == null)
        {
            typesMap = new SequencedHashMap();
            IJavaProject jProject = JavaCore.create(config.getProject());
            try {
                IPackageFragmentRoot[] root = jProject.getAllPackageFragmentRoots();
                {
                    for (int k = 0; k < root.length; k++) {
                        IJavaElement[] fragments = root[k].getChildren();

                        for (int i = 0; i < fragments.length; i++) {
                            IJavaElement fragment = fragments[i];
                            if (!(fragment instanceof IPackageFragment))
                                continue ;
                            
                            String[] types = ScanProject.getProjectSourceTypesNames((IPackageFragment)fragment);
                            String packageName = fragment.getElementName();
                            
                            if (types.length != 0) {
                                Vector typesVector = null;
                                if (typesMap.containsKey(packageName)) {
                                    typesVector = (Vector)typesMap.get(fragment.getElementName());
                                } else {
                                    typesVector = new Vector();
                                    typesMap.put(packageName,typesVector);
                                }
                                
                                for (int j = 0; j < types.length; j++) {
                                    String string = types[j];
                                    typesVector.add(new TypeSearchHolder(string,packageName));
                                }
                            }
                        }
                    
                    }
                }
            } catch (JavaModelException e) {
            	OrmCore.getPluginLog().logError(e.getMessage(),e);
            }
        }
    }
    
    public String toClassCollectionFinder(IPersistentField field,String fQTypeName, boolean searchOnlyPersistent)
    {
        if (field == null || fQTypeName == null)
            return null;
        
        String collectionPCName = null;
        
        boolean isArray = false;
        if (HibernateAutoMappingHelper.getLinkedType(fQTypeName) == HibernateAutoMappingHelper.LT_ARRAY)
            isArray = true;
        
        if (isArray)
        {
            String aType = Signature.getElementType(fQTypeName);

            if (aType != null)
            {
                if (searchOnlyPersistent)
                {
                    if (config.getHibernateMapping().findClass(aType) != null)
                        collectionPCName = aType;
                }
                else
                {
                    collectionPCName = aType;
                }
            }
        } else {
            IPersistentClass ownerClass = field.getOwnerClass();
            
            Vector<TypeSearchHolder> packageMappingsVector = new Vector<TypeSearchHolder>();
            Vector<TypeSearchHolder> allOtherMappingsVector = new Vector<TypeSearchHolder>();
            
            boolean tryForPersistence = searchOnlyPersistent;
            
            if (ownerClass != null)
            {
                String packageName = null;
                if (ownerClass.getPackage() != null)
                    packageName = ownerClass.getPackage().getName();

                initTypesMap();

                Iterator stringKeysItr = typesMap.iterator();
                while (stringKeysItr.hasNext())
                {
                    Vector typesVector;
                    
                    String thePackageName = (String) stringKeysItr.next();
                    tryForPersistence &= (config.getHibernateMapping().getPackage(thePackageName) != null);
                    
                    if (thePackageName.equals(packageName))
                    {
                        typesVector = packageMappingsVector;
                    }
                    else
                    {
                        typesVector = allOtherMappingsVector;
                    }
                    
                    Vector packageTypes = (Vector) typesMap.get(thePackageName);
                    if (packageTypes != null)
                    {
                        Iterator itr = packageTypes.iterator();
                        while (itr.hasNext())
                        {
                            TypeSearchHolder typeName = (TypeSearchHolder) itr.next();
                            if (!tryForPersistence || (config.getHibernateMapping().findClass(typeName.getFullyQualifiedName()) != null))
                                typesVector.add(typeName);
                        }
                    }
                }
                
                TypeSearchHolder[] classes = new TypeSearchHolder[0];
                if (packageMappingsVector.size() != 0)
                {
                    classes = (TypeSearchHolder[])packageMappingsVector.toArray(new TypeSearchHolder[0]);
                    TypeSearchHolder tsh = (TypeSearchHolder)SearchUtils.findClass(field.getName(),classes,hamConfig.isUseFuzzySearch());
                    if (tsh != null)
                        collectionPCName = tsh.getFullyQualifiedName();
                }
                if (collectionPCName == null && allOtherMappingsVector.size() != 0)
                {
                    classes = (TypeSearchHolder[])allOtherMappingsVector.toArray(new TypeSearchHolder[0]);
                    TypeSearchHolder tsh = (TypeSearchHolder)SearchUtils.findClass(field.getName(),classes,hamConfig.isUseFuzzySearch());
                    if (tsh != null)
                        collectionPCName = tsh.getFullyQualifiedName();
                }
            }
        }
        
        return collectionPCName;
    }
    
    /**
     * discovers linked to PC's fields and put them into queue for second-stage processing
     * @param field
     * @param fQTypeName
     * @return
     */
    private DeferredPropertyHolder deferField(IPersistentFieldMapping fieldMapping,
            String fQTypeName, IPersistentClass toClass, String namePrefix)
    {
        if (fieldMapping == null || fQTypeName == null)
            return null;
        
        IPersistentField field = fieldMapping.getPersistentField();
        DeferredPropertyHolder result = null;
        PersistentClass pc = (PersistentClass)config.getHibernateMapping().findClass(fQTypeName);
        if (pc != null)
        {
            result = new DeferredPropertyHolder(fieldMapping,pc,DF_ONE,namePrefix);
            registerDeferredMapping(pc, field.getMasterClass(), result);
            return result;
        }
        
        if (HibernateAutoMappingHelper.getLinkedType(fQTypeName) == HibernateAutoMappingHelper.LT_NONE)
        {
            return null;
        }
        

        if (toClass == null)
            return null;
        
        result = new DeferredPropertyHolder(fieldMapping,toClass,DF_MANY,namePrefix);
        registerDeferredMapping(toClass, field.getMasterClass(), result);
        return result;
    }
    
    private void registerDeferredMapping(IPersistentClass element, IPersistentClass fieldOwner, DeferredPropertyHolder holder) {
        if (!collectionsMapping.containsKey(element))
            collectionsMapping.put(element,new HashMap<IPersistentClass,Vector<DeferredPropertyHolder>>());
        
        Map<IPersistentClass,Vector<DeferredPropertyHolder>> map = collectionsMapping.get(element);
        if (!map.containsKey(fieldOwner))
            map.put(fieldOwner,new Vector<DeferredPropertyHolder>());
        
        map.get(fieldOwner).add(holder);

        deferredProperties.put(holder.fieldMapping,holder);
    }

    //removes processed holder from queue
    private void removeHolder(DeferredPropertyHolder holder)
    {
        boolean removed = false;
        Iterator itr = collectionsMapping.values().iterator();
        while (itr.hasNext() && !removed)
        {
            Map map = (Map)itr.next();
            Iterator innerItr = map.values().iterator();
            while (innerItr.hasNext() && !removed)
            {
                Vector vec = (Vector)innerItr.next();
                removed = vec.remove(holder);
            }
        }
        deferredProperties.remove(holder);
    }

/*
    private void createInformationMarker(final IResource resource, final String message) throws CoreException {
        IWorkspaceRunnable runnable = new IWorkspaceRunnable(){
            public void run(IProgressMonitor monitor) throws CoreException {
                IMarker marker=resource.createMarker(IMarker.TASK);
                marker.setAttribute(IMarker.MESSAGE,message);
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
                marker.setAttribute(IMarker.LINE_NUMBER,3);
            }
        };
        // edit tau 16.03.2006 -> add ISchedulingRule (MARKER_RULE) for resource
        //config.getProject().getWorkspace().run(runnable,null);
        config.getProject().getWorkspace().run(runnable, RuleUtils.modifyRule(new IResource[]{resource}, RuleUtils.MARKER_RULE ), IResource.NONE, new NullProgressMonitor());        
    }
*/    
//    private void makeFieldConformToUnIndexedType(IPersistentField field) throws CoreException
//    {
//        if (HibernateAutoMappingHelper.isIndexedCollection(field.getType()))
//        {
//            String message = "Indexed mappings can't be used here. Field \""+field.getName() + "\" should be refactored to "+hamConfig.getUnindexedCollectionName()+" for mapping conformity";
//            IMappingStorage storage = null;
//            
//            if (field.getMasterClass().getPersistentClassMapping() instanceof IPersistentClassMapping) {
//                IPersistentClassMapping pcm = (IPersistentClassMapping) field.getMasterClass().getPersistentClassMapping();
//                storage = pcm.getStorage();
//            }
//        }
//    }
    
    private void createAndBindOneToOne(IPersistentField field1, IPersistentField field2, SharedColumnsCopier sharedColumnsCopier) throws CoreException
    {
        IPersistentField field;
        IPersistentField foreignField;
        
        if (((IPropertyMapping)field1.getMapping()).getPropertyMappingHolder() instanceof IComponentMapping)
        {
            field = field1;
            foreignField = field2;
        }
        else
        {
            field = field2;
            foreignField = field1;
        }
        
        ClassMapping oneMapping = (ClassMapping)field.getOwnerClass().getPersistentClassMapping();
        ClassMapping otherMapping = (ClassMapping)foreignField.getOwnerClass().getPersistentClassMapping();
        
        OneToOneMapping oto = helper.createOneToOneMapping(field,otherMapping.getPersistentClass());
        oto.setReferencedPropertyName(foreignField.getName());
        ToOneMapping mto = helper.createManyToOneMapping(foreignField,oneMapping.getPersistentClass());
        
        oto.setName(HibernateAutoMapping.propertyToTableName(foreignField.getName(),field.getName()));
        
        //TODO Nick - setAlternateUniqueKey?
        mto.setAlternateUniqueKey(true);
        
        sharedColumnsCopier.addSharingValueMappings(mto,oto);
        
        HibernateAutoMappingHelper.bindValueMappingToField(field,oto);
        HibernateAutoMappingHelper.bindValueMappingToField(foreignField,mto);
    }
    
    private void createAndBindBiOneToMany(IPersistentField holderEnd, IPersistentField collectionEnd, SharedColumnsCopier sharedColumnsCopier) throws CoreException
    {
        ClassMapping holderClsMapping = (ClassMapping)holderEnd.getOwnerClass().getPersistentClassMapping();
        ClassMapping collectionClsMapping = (ClassMapping)collectionEnd.getOwnerClass().getPersistentClassMapping();
        
        String collectionTypeName = holderEnd.getType();
        
        ToOneMapping collectionValue = helper.createManyToOneMapping(collectionEnd,holderClsMapping.getPersistentClass());
        
//        String collectionName = 
        	HibernateAutoMapping.propertyToTableName(
                holderClsMapping.getPersistentClass().getShortName(),
                holderEnd.getName());
        
        CollectionMapping collection = createCollectionMapping(
                holderClsMapping.getPersistentClass().getShortName(),
                holderEnd.getName(),
                collectionTypeName,
                holderClsMapping,
                collectionClsMapping.getDatabaseTable(),
                holderClsMapping.getIdentifier(),
                false);
        
        collection.setCollectionElementClassName(collectionClsMapping.getPersistentClass().getName());
        
        OneToManyMapping collectionElement = new OneToManyMapping(holderClsMapping);
        collectionElement.setAssociatedClass(collectionClsMapping);
        collectionElement.setReferencedEntityName(collection.getCollectionElementClassName());
        
        sharedColumnsCopier.addSharingValueMappings(collection.getKey(),collectionValue);
        
        collection.setElement(collectionElement);
        collection.setCascade(hamConfig.getAssociationsCascade());
        collection.setLazy(hamConfig.isCollectionLazy());
        HibernateAutoMappingHelper.bindValueMappingToField(collectionEnd,collectionValue);
        HibernateAutoMappingHelper.bindValueMappingToField(holderEnd,collection);
                      
        if (HibernateAutoMappingHelper.isIndexedCollection(holderEnd.getType()) && collectionEnd.getMapping() instanceof IPropertyMapping)
        {
            ((IPropertyMapping)collectionEnd.getMapping()).setInsertable(false);
            ((IPropertyMapping)collectionEnd.getMapping()).setUpdateable(false);

            collection.setInverse(false);
        }
        else
        {
            collection.setInverse(true);
        }
    }
    
    private void createAndBindManyToMany(IPersistentField field, IPersistentField foreignField, IDatabaseTable linkTable, SharedColumnsCopier sharedColumnsCopier) throws CoreException
    {
        IPersistentClass clazz = field.getMasterClass();
        ClassMapping mapping = (ClassMapping)field.getOwnerClass().getPersistentClassMapping();
        
        IPersistentClass foreignClazz = foreignField.getMasterClass();
        ClassMapping foreignMapping = (ClassMapping)foreignField.getOwnerClass().getPersistentClassMapping();
        
        IHibernateKeyMapping key1 = mapping.getIdentifier();
        IHibernateKeyMapping key2 = foreignMapping.getIdentifier();
        
        //fieldName set to null for simpler field name
        CollectionMapping collection1 = createCollectionMapping(
                clazz.getShortName(),
                null,
                field.getType(),
                mapping,
                linkTable,
                key1,
                false);
        collection1.setCollectionElementClassName(foreignClazz.getName());
        
        //now process inverse side
        CollectionMapping collection2 = createCollectionMapping(
                foreignClazz.getShortName(),
                null,
                foreignField.getType(),
                foreignMapping,
                linkTable,
                key2,
                false);
        collection2.setCollectionElementClassName(clazz.getName());
        
        collection1.setName(linkTable.getName());
        collection2.setName(linkTable.getName());
        
        //collection2 is always inverse
        //collection2.setInverse(true);
        
        IManyToManyMapping m2m1 = HibernateAutoMappingHelper.createManyToMapping(foreignMapping,clazz,clazz.getName(),linkTable);
        IManyToManyMapping m2m2 = HibernateAutoMappingHelper.createManyToMapping(mapping,foreignClazz,foreignClazz.getName(),linkTable);
        
        if (collection2.getKey() instanceof SimpleValueMapping)
            sharedColumnsCopier.addSharingValueMappings(collection2.getKey(),m2m2);
        if (collection1.getKey() instanceof SimpleValueMapping)
            sharedColumnsCopier.addSharingValueMappings(collection1.getKey(),m2m1);

        if (linkTable.getPrimaryKey() != null)
        {
            if (! (new ColumnSet(m2m1.getColumnIterator())).intersects(linkTable.getPrimaryKey()))
            {
                sharedColumnsCopier.addPostCopyOperation(m2m1,new BindToPKPostCopy(linkTable.getPrimaryKey()));
            }
            if (! (new ColumnSet(m2m2.getColumnIterator())).intersects(linkTable.getPrimaryKey()))
            {
                sharedColumnsCopier.addPostCopyOperation(m2m2,new BindToPKPostCopy(linkTable.getPrimaryKey()));
            }
        }
        
        if (!HibernateAutoMappingHelper.isIndexedCollection(field.getType()) )
        {
                collection1.setInverse(true);
        }
        else
        {
            if (!HibernateAutoMappingHelper.isIndexedCollection(foreignField.getType()) )
            {
                collection2.setInverse(true);
            }
        }
        
        collection1.setElement(m2m2);
        collection2.setElement(m2m1);
        collection1.setCascade(hamConfig.getAssociationsCascade());
        collection2.setCascade(hamConfig.getAssociationsCascade());
        if (hamConfig.getLazy() != null)
        {
            collection1.setLazy(hamConfig.getLazy().booleanValue());
            collection2.setLazy(hamConfig.getLazy().booleanValue());
        }
        
        HibernateAutoMappingHelper.bindValueMappingToField(field,collection1);
        HibernateAutoMappingHelper.bindValueMappingToField(foreignField,collection2);
    }
    
    private void createAndBindUniOneToMany(IPersistentField field, IPersistentClass collectionElements) throws CoreException
    {
        ClassMapping collectionOwnerMapping = (ClassMapping)field.getOwnerClass().getPersistentClassMapping();
        ClassMapping collectionElementMapping = (ClassMapping)collectionElements.getPersistentClassMapping();
        
        CollectionMapping collection = createCollectionMapping(
                collectionOwnerMapping.getPersistentClass().getShortName(),
                field.getName(),
                field.getType(),
                collectionElementMapping,
                collectionElementMapping.getDatabaseTable(),
                collectionOwnerMapping.getIdentifier(),
                true);
        collection.setCollectionElementClassName(collectionElementMapping.getPersistentClass().getName());
        
        OneToManyMapping otm = HibernateAutoMappingHelper.createOneToManyMapping(collectionOwnerMapping,collectionElementMapping);
        collection.setElement(otm);
        collection.setCascade(hamConfig.getAssociationsCascade());
        collection.setLazy(hamConfig.isCollectionLazy());

        HibernateAutoMappingHelper.bindValueMappingToField(field,collection);
    }
    
    private void createAndBindUniManyToOne(IPersistentField field, IPersistentClass pointsTo) throws CoreException
    {
        ToOneMapping mto  = helper.createManyToOneMapping(field,pointsTo);
        HibernateAutoMappingHelper.bindValueMappingToField(field,mto);
    }
    
    private void processDeferredFields()
    {
        if (state != STATE_FULL_PROCESSING)
            return ;
        
        SharedColumnsCopier sharedColumnsCopier = visitor.getSharedColumnCopier();
        
        Iterator props = deferredProperties.values().iterator();
        while (props.hasNext())
        {
            try {
                DeferredPropertyHolder holder = (DeferredPropertyHolder)props.next();
                IPersistentField field = holder.getField();
                
                PropertyMapping pm = (PropertyMapping)holder.fieldMapping;
                //already mapped
                if (pm != null && pm.getValue() != null)
                    continue;
                
                Map referencedClassFieldsMap = (Map)collectionsMapping.get(field.getMasterClass());
                Vector foreignFields = null;
                if (referencedClassFieldsMap != null)
                    foreignFields = (Vector)referencedClassFieldsMap.get(holder.fieldType);
                
                
                //indicates the absence of field referring owner class of the "field" variable
                boolean noForeignField = (foreignFields == null || foreignFields.size() == 0);
                
                DeferredPropertyHolder foreignFieldHolder = null;
                
                if (!noForeignField)
                {
                    foreignFieldHolder = (DeferredPropertyHolder)foreignFields.firstElement();
                    if (foreignFieldHolder == holder)
                    {
                        if (foreignFields.size() > 1)
                        {
                            foreignFieldHolder = (DeferredPropertyHolder)foreignFields.elementAt(1);
                        }
                        else
                            noForeignField = true;
                    }
                }
                
                if (holder.multiplicity == DF_ONE && !noForeignField
                        && foreignFieldHolder.multiplicity == DF_ONE)
                {
                    if (field.getOwnerClass() != field.getMasterClass() &&
                            ((IPropertyMapping)foreignFieldHolder.fieldMapping).getPropertyMappingHolder() instanceof IComponentMapping)
                    {
                        //fields are in component
                        noForeignField = true;
                    }
                }
                
                if (noForeignField)
                {
                    //nobody is referring our class
                    if (holder.multiplicity == DF_ONE)
                    {
                        //many-to-one mapping
                        createAndBindUniManyToOne(field,holder.fieldType);
                    }
                    else
                    {
                        //one-to-many mapping (collection without link to holder(parent) )
                        createAndBindUniOneToMany(field,holder.fieldType);
                    }

                    field.accept(visitor,holder.namePrefix);
                    removeHolder(holder);
                    
                }
                else
                {
                    //all bidirectional
                    IPersistentField foreignField = foreignFieldHolder.getField();
                    
                    if (holder.multiplicity == DF_MANY && 
                            foreignFieldHolder.multiplicity == DF_MANY)
                    {

                        //many-to-many bidirectional
                        IDatabaseTable linkTable = null;
                        
                        if (visitor instanceof ColumnMergingVisitor) {
                            ColumnMergingVisitor mergingVisitor = (ColumnMergingVisitor) visitor;

                            IDatabaseTableColumnSet fKey = mergingVisitor.getPersistentFieldColumnSet(foreignField);
                            IDatabaseTableColumnSet foreignFKey = mergingVisitor.getPersistentFieldColumnSet(field);
                            if (fKey != null && foreignFKey != null
                                    && fKey.getTable() == foreignFKey.getTable())
                                linkTable = fKey.getTable();
                        }
                        
                        if (linkTable == null)
                        {
//                            String linkTableName1 = HibernateAutoMapping.propertyToColumnName(StringUtils.safeStrCoupling(field.getMasterClass().getShortName(),holder.namePrefix));
//                            String linkTableName2 = HibernateAutoMapping.propertyToColumnName(StringUtils.safeStrCoupling(foreignFieldHolder.namePrefix,foreignField.getMasterClass().getShortName()));
                            String linkTableName1 = HibernateAutoMapping.propertyToColumnName(field.getMasterClass().getShortName());
                            String linkTableName2 = HibernateAutoMapping.propertyToColumnName(foreignField.getMasterClass().getShortName());
                            
                            String linkTableName = HibernateAutoMapping.propertyToTableName(linkTableName1,linkTableName2);
                            String name = linkTableName;
                            int counter = 0;
                            
                            do
                            {
                                linkTable = HibernateAutoMappingHelper.getTableInMappingSchema(field.getOwnerClass().getPersistentClassMapping(),
                                        name,mapping);

                                if (linkTable != null)
                                {
                                    counter++;
                                    name = linkTableName + "_" + counter;
                                }
                            }
                            while (linkTable != null);

                            linkTable = HibernateAutoMappingHelper.getOrCreateTableInMappingSchema(field.getOwnerClass().getPersistentClassMapping(),
                                    name,mapping);

                            PrimaryKey key = new PrimaryKey();
                            key.setTable(linkTable);
                            linkTable.setPrimaryKey(key);
                            
                            key.setName("PK_"+linkTable.getName());
                        }

                        //makeFieldConformToUnIndexedType(field);
                        //makeFieldConformToUnIndexedType(foreignField);
                        
                        createAndBindManyToMany(field,foreignField,linkTable,sharedColumnsCopier);
                        
                        holder.namePrefix = null;
                        foreignFieldHolder.namePrefix = null;
                    }
                    else
                        //one-to-one bidirectional
                        if (holder.multiplicity == DF_ONE && 
                                foreignFieldHolder.multiplicity == DF_ONE){
                            createAndBindOneToOne(field,foreignField,sharedColumnsCopier);

                            if (field.getMapping().getPersistentValueMapping() instanceof IOneToOneMapping)
                            {
                                foreignField.accept(visitor,foreignFieldHolder.namePrefix);
                            }
                            else
                            {
                                field.accept(visitor,holder.namePrefix);
                            }
                        }
                        else
                        {
                            //many-to-one (or one-to-many)
                            DeferredPropertyHolder collectionEnd;
                            DeferredPropertyHolder holderEnd;
                            if (holder.multiplicity == DF_ONE)
                            {
                                collectionEnd = holder;
                                holderEnd = foreignFieldHolder;
                            }
                            else
                            {
                                collectionEnd = foreignFieldHolder;
                                holderEnd = holder;
                            }
                            
                            createAndBindBiOneToMany(holderEnd.getField(),collectionEnd.getField(),sharedColumnsCopier);
                            
                            //accept to provide predictable result
                            collectionEnd.getField().accept(visitor,collectionEnd.namePrefix);
                        }
                    
                    field.accept(visitor,holder.namePrefix);
                    foreignField.accept(visitor,foreignFieldHolder.namePrefix);

                    //elements processed
                    removeHolder(holder);
                    removeHolder(foreignFieldHolder);
                    // #added# by Konstantin Mishin on 15.02.2006 fixed for ESORM-455 cascade on collection value is replaced with default one
                    if (foreignField.getMapping() instanceof PropertyMapping && foreignField.getMapping().getPersistentValueMapping() instanceof ICollectionMapping) 
                    	((PropertyMapping)(foreignField.getMapping())).setCascade(hamConfig.getAssociationsCascade());
                    // #added#
                }
            
                // #added# by Konstantin Mishin on 15.02.2006 fixed for ESORM-455 cascade on collection value is replaced with default one
                if (pm.getPersistentValueMapping() instanceof ICollectionMapping) 
                	pm.setCascade(hamConfig.getAssociationsCascade());
                // #added#
           }
            catch (CoreException e) {
            	OrmCore.getPluginLog().logError("Exception processing deferred mappings",e);
            }
        }
        deferredProperties.clear();
        collectionsMapping.clear();
    }
    
    
    private CollectionMapping createCollectionMapping(
            String elementsType, 
            String fieldName, 
            String fQTypeName, 
            IPersistentClassMapping pcMapping, 
            IDatabaseTable collectionTable, 
            IHibernateKeyMapping key, 
            boolean hasNullableKeys) throws CoreException
    {
        CollectionMapping valueMapping = null;
        IHibernateKeyMapping fKey = HibernateAutoMappingHelper.createAndBindKey(key,collectionTable);
        
        if (fKey != null)
            fKey.setCascadeDeleteEnabled(hamConfig.isCascadeDeleteEnabled());

        
        boolean isArray = false;

        switch (HibernateAutoMappingHelper.getLinkedType(fQTypeName))
        {
            case (HibernateAutoMappingHelper.LT_NONE):
                return null;
            case (HibernateAutoMappingHelper.LT_ARRAY):
                isArray = true;
        }
        
/*        String prefix;
        
        //provides simple notations for columnm names, e.g. index instead of <elementsType>_<fieldName>_index
        boolean simpleNotation = (fieldName == null);
        
        if (!simpleNotation)
        {
            prefix = HibernateAutoMapping.propertyToTableName(elementsType,fieldName);
            prefix += "_";
        }
        else
        {
            prefix = "";
            fKey = HibernateAutoMappingHelper.createAndBindFK(key,collectionTable,HibernateAutoMapping.propertyToColumnName(elementsType),hasNullableKeys);
        }
*/
        if (isArray)
        {
            String aType;
            aType = Signature.getElementType(fQTypeName);
            ArrayMapping arrayMapping;
            if (ClassUtils.isPrimitiveType(aType))
                arrayMapping = new PrimitiveArrayMapping((ClassMapping)pcMapping);
            else
                arrayMapping = new ArrayMapping((ClassMapping)pcMapping);
            
            //array index column
            SimpleValueMapping indexMapping = new SimpleValueMapping();
            indexMapping.setTable(collectionTable);
            indexMapping.setType(Type.INTEGER);
            indexMapping.setName("index");
            arrayMapping.setIndex(indexMapping);
            arrayMapping.setElementClassName(aType);
            valueMapping = arrayMapping;
            //XXX Nick Map elements and indexes of arrays. You should
            // create value mappings for element and index and call
            // ArrayMapping.setElement and ArrayMapping.setIndex
        }
        else
        {
            CollectionMapping collectionMapping = null;
            
            boolean sorted = false;
            
            if (fQTypeName.equals("java.util.List")) {
                //PK(FK, "position")
                ListMapping listMapping = new ListMapping((ClassMapping)pcMapping);

                SimpleValueMapping positionMapping = new SimpleValueMapping();
                positionMapping.setType(Type.INTEGER);
                positionMapping.setTable(collectionTable);
                listMapping.setIndex(positionMapping);

                collectionMapping = listMapping;

            } else if (fQTypeName.equals("java.util.Map") || (sorted = fQTypeName.equals("java.util.SortedMap"))) {
                //PK(FK,propName+"_name")
                MapMapping mapMapping = new MapMapping((ClassMapping)pcMapping);
                
                
                SimpleValueMapping keyMapping = new SimpleValueMapping();
                //XXX.toAlex (Nick) index column can be of any basic type, what
                // type can we use here?
                //Use String . We have two choises: String or don't map a key
                keyMapping.setType(Type.STRING);
                keyMapping.setTable(collectionTable);
                mapMapping.setIndex(keyMapping);

                collectionMapping = mapMapping;

            } else if (fQTypeName.equals("java.util.Set") || (sorted = fQTypeName.equals("java.util.SortedSet"))) {
                //PK(FK,*)
                SetMapping setMapping = new SetMapping((ClassMapping)pcMapping);
                collectionMapping = setMapping;

            } else if (fQTypeName.equals("java.util.Collection")) {
                //FK, maybe one surrogate PK, data not unique
                BagMapping bagMapping = new BagMapping((ClassMapping)pcMapping);
                collectionMapping = bagMapping;
            }
            
            collectionMapping.setSort(sorted ? "natural" : null);
            valueMapping = collectionMapping;
        }

        valueMapping.setKey(fKey);
        valueMapping.setCollectionTable(collectionTable);
        //akuzmin 24.08.2005 ORMIISTUD-679        
        valueMapping.setLazy(hamConfig.isCollectionLazy());
        return valueMapping;
    }
    
    
    
    //map linked types - collections and arrays
    /**
     * creates collections and arrays mappings; puts collection's elements to separate table, 
     * so should be used only for collections with value-semantic elements, 
     * but not for PC's collections!
     * @param fieldName
     * @param fQTypeName
     * @param pcMapping
     * @return
     * @throws CoreException
     */
    private CollectionMapping linkedValuesMappingsFactory(String fieldName,
            String fQTypeName, IPersistentClassMapping pcMapping, String namePrefix, String elementClassName) throws CoreException {

        boolean isArray = false;

        switch (HibernateAutoMappingHelper.getLinkedType(fQTypeName))
        {
            case (HibernateAutoMappingHelper.LT_NONE):
                return null;
            case (HibernateAutoMappingHelper.LT_ARRAY):
            {
                isArray = true;
                
                //we should not handle array of array
                if (Signature.getArrayCount(fQTypeName) != 1)
                    return null;
            }
        }
        
        String aType = "";
//        PersistentClass collectionPC = null;
        IDatabaseTable collectionTable = null;
        
        if (isArray)
        {
            aType = Signature.getElementType(fQTypeName);
        }
        
        Map<String,String> backupRestrictionMap = new HashMap<String,String>();
        
        backupRestrictionMap.putAll(restrictedTypesMap);
        restrictedTypesMap.clear();
        restrictedTypesMap.put(COLLECTION_TYPE,"Inner collection restricted here");
        
        String className = pcMapping.getPersistentClass().getShortName();
        //create new table
        collectionTable = HibernateAutoMappingHelper.getOrCreateTableInMappingSchema(
                pcMapping,
                HibernateAutoMapping.propertyToTableName(className, StringUtils.safeStrCoupling(namePrefix,fieldName)),
                config.getHibernateMapping());

        CollectionMapping collectionMapping = createCollectionMapping(pcMapping.getPersistentClass().getShortName(),null,fQTypeName,
                pcMapping,collectionTable,((ClassMapping)pcMapping).getIdentifier(),false);
        
        if (isArray)
        {
            //can now handle arrays of components
            IHibernateValueMapping arrayElementMapping = simpleMappingsFactory(
                    fieldName, aType, pcMapping, collectionTable,namePrefix);
            collectionMapping.setElement(arrayElementMapping);
            //XXX Nick Map elements and indexes of arrays. You should
            // create value mappings for element and index and call
            // ArrayMapping.setElement and ArrayMapping.setIndex
        }
        else
        {
            String elementMappingName = HibernateAutoMapping.propertyToColumnName(fieldName);
            
            IHibernateValueMapping elementMapping = null;
            
            if (elementClassName != null)
            {
                elementMapping = simpleMappingsFactory(fieldName,elementClassName,pcMapping,collectionTable,null);
                collectionMapping.setCollectionElementClassName(elementClassName);
            }

            if (elementMapping == null)
            {
                SimpleValueMapping element= new SimpleValueMapping();
                element.setName(elementMappingName);
                element.setType(Type.getType(hamConfig.getCollectionElementsType()));
                elementMapping = element;
                collectionMapping.setCollectionElementClassName(hamConfig.getJavaTypeName(elementMapping.getType()));
            }
            //createAndBindColumn(elementMapping,elementMappingName,collectionTable);
            
            elementMapping.setTable(collectionTable);
            collectionMapping.setElement(elementMapping);

            collectionMapping.setCollectionTable(collectionTable);
            
        }
/*        collectionMapping.setKey(HibernateAutoMappingHelper.createAndBindFK(
                ((ClassMapping)pcMapping).getIdentifier(),
                collectionTable,
                pcMapping.getPersistentClass().getShortName(),
                false));
*/        restrictedTypesMap.remove(COLLECTION_TYPE);
          restrictedTypesMap.putAll(backupRestrictionMap);

        return collectionMapping;
    }

    /**
     * @return Set of persistent classes, which are actually deferred
     */
    public Set<IPersistentClass> getDeferredClasses()
    {
        return collectionsMapping.keySet();
    }

    ColumnBuilderVisitor getVisitor() {
        return visitor;
    }
    
 }
