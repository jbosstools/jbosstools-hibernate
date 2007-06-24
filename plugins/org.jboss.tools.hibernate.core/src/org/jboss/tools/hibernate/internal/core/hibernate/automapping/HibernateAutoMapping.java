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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.jboss.tools.hibernate.core.IAutoMappingService;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IDatabaseTableColumnSet;
import org.jboss.tools.hibernate.core.IDatabaseTableForeignKey;
import org.jboss.tools.hibernate.core.IOrmConfiguration;
import org.jboss.tools.hibernate.core.IOrmElement;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.OrmProgressMonitor;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.core.hibernate.ICollectionMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToManyMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToOneMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.core.hibernate.IRootClassMapping;
import org.jboss.tools.hibernate.core.hibernate.ISimpleValueMapping;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.PersistentClass;
import org.jboss.tools.hibernate.internal.core.PersistentField;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.data.Table;
import org.jboss.tools.hibernate.internal.core.hibernate.ClassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.CollectionMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ComponentMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.DefaultPersistentClassFiller;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateConfiguration;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.JoinedSubclassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ManyToOneMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.OneToOneMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.PropertyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.RootClassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SubclassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.UnionSubclassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.RunnablesHolder.ReversingCreateCompilationUnitsOperation;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.RunnablesHolder.SourcePropertyCreationRunnable;
import org.jboss.tools.hibernate.internal.core.util.ClassUtils;
import org.jboss.tools.hibernate.internal.core.util.RuleUtils;
import org.jboss.tools.hibernate.internal.core.util.ScanProject;
import org.jboss.tools.hibernate.internal.core.util.SearchUtils;
import org.jboss.tools.hibernate.internal.core.util.StringUtils;
import org.jboss.tools.hibernate.internal.core.util.TypeUtils;

/**
 * @author alex
 * 
 * Hibernate automated mapping service
 */
public class HibernateAutoMapping implements IAutoMappingService {

    private DefaultPersistentClassFiller pcFiller;
    private HibernateMapping mapping;
    private HibernateConfiguration config;
    private HibernateAutoMappingHelper helper;
	private ConfigurationReader hamConfig;
    private SchemaProcessor schemaProcessor;
    private IOrmConfiguration ormConfig;
    private SourcePropertyCreationRunnable sourcePropertyCreation;
    
    private ReversingCreateCompilationUnitsOperation createCUOperation;
    private ReversingCreateCompilationUnitsOperation createReversedCmpIdOperation;
    private PersistentFieldProvider pfProvider;
    
    private MappingsFactory idFactory;
    
	private Set<ClassMapping> cycledIdCatcher = new HashSet<ClassMapping>();

// added by yk 22.07.2005
	private SchemaSynchronizationVisitor	schemaSyncVisitor;
// added by yk 22.07.2005 stop
	
    private void setIdentifierValueDefaults(SimpleValueMapping svm, boolean useDefaultGenerator)
    {
        String nullValue = getOrmProperty(OrmConfiguration.HIBERNATE_ID_UNSAVED_VALUE);
        if (svm instanceof ComponentMapping && "null".equals(nullValue))
            nullValue = "undefined";
        
        svm.setNullValue(nullValue);
        
        if (useDefaultGenerator)
            svm.setIdentifierGeneratorStrategy(ormConfig.getProperty(OrmConfiguration.HIBERNATE_ID_GENERATOR));
        
    }
    
    // edit tau 07.08.2006
    // TODO 07.08.2006 -> configurationReader.get...
    private void generateIdentity(ClassMapping mapping, MappingsFactory valueFactory) throws CoreException
    {
		generateIdentity(mapping,valueFactory,getOrmProperty(OrmConfiguration.IDENTIFIER_QUERY));
    }
    
    private void generateIdentity(ClassMapping mapping, MappingsFactory valueFactory, String identifierQuery) throws CoreException
    {
        PersistentClass pc = (PersistentClass)mapping.getPersistentClass();
        IDatabaseTable table = (Table)mapping.getDatabaseTable();
        IPersistentField[] fields=pc.getFields();
        
        boolean useDefaultGenerator = true;
        
        /* by alex 04/28/05
        if (mapping instanceof UnionSubclassMapping)
            fields = pc.getAllFields();
        else
            fields = pc.getFields();*/
        //map identifier columns first to have proper id before collections
        // mapping
        //XXX Nick Note that class mapping has identifier value mapping
        // and identifier property mapping.
        //It is because class may not have a identifier property mapping
        // but must have identifier value mapping.
        //If identifier property mapping exists that means identifier value
        // mapping == (identifier property mapping).getValue()
        if (mapping.isClass() && mapping.getIdentifier() == null) 
        {
            SimpleValueMapping idMappingValue = null;
            IPropertyMapping idMapping = mapping.getIdentifierProperty();

            if (idMapping == null) 
            {
                boolean resetPrimaryKey = true;
                
                IPersistentField idField = SearchUtils.findField(fields,identifierQuery,hamConfig.isUseFuzzySearch());  //alex 04/13/05
                
                if (idField != null)
                {
                    boolean needNewId = false;
                    IPersistentClass foreignPC = config.getHibernateMapping().findClass(idField.getType());
                    if (foreignPC != null)
                    {
                        if (foreignPC.getPersistentClassMapping() == null)
                        {
                            //foreign PC can be mapped only as a component - that's wrong, so don't use this Id property
                            needNewId = true;
                        }
                        else
                        {
                            ClassMapping foreignMapping = (ClassMapping)foreignPC.getPersistentClassMapping();
                            IHibernateKeyMapping fKey = foreignMapping.getIdentifier();
                            boolean canShareId = true;
                            canShareId = !cycledIdCatcher.contains(foreignMapping) && (foreignMapping != mapping);
                            if (fKey == null && canShareId)
                            {
                                cycledIdCatcher.add(mapping);
                                generateIdentity(foreignMapping,valueFactory,identifierQuery);
                            }
                            
                            if (canShareId)
                            {
                                fKey = foreignMapping.getIdentifier();
                                if (fKey != null && fKey.isSimpleValue())
                                {
                                    OneToOneMapping oto = new OneToOneMapping(table,fKey);
                                    oto.setConstrained(true);
                                    
                                    IPersistentField fKeyField = fKey.getFieldMapping().getPersistentField();
                                    
                                    String sharedIdName = StringUtils.beanDecapitalize(pc.getShortName())+"_"+fKeyField.getName();
                                    IPersistentField sharedIdField = pfProvider.getOrCreatePersistentField(pc,sharedIdName,fKeyField.getType(),hamConfig.isUseFuzzySearch());
                                    helper.createAndBindPropertyMapping(sharedIdField);
                                    helper.createAndBindPropertyMapping(idField);
                                    SimpleValueMapping fkValue = HibernateAutoMappingHelper.createAndBindKey(fKey,table);
                                    HibernateAutoMappingHelper.bindValueMappingToField(sharedIdField,fkValue);
                                    HibernateAutoMappingHelper.bindValueMappingToField(idField,oto);
                                    //sharedIdMapping
                                    
                                    useDefaultGenerator = false;
                                    fkValue.setIdentifierGeneratorStrategy("foreign");
                                    Properties props = new Properties();
                                    props.put("property",idField.getName());
                                    fkValue.setIdentifierGeneratorProperties(props);
                                    idMappingValue = fkValue;
                                    idField = sharedIdField;
                                }
                                else
                                {
                                    needNewId = true;
                                }
                            }
                            else
                            {
                                needNewId = true;
                            }
                            cycledIdCatcher.remove(mapping);
                        }
                    }
                    
                    if (idField.getMapping() != null)
                    {
                        IPersistentValueMapping value = idField.getMapping().getPersistentValueMapping();
                        if (value instanceof SimpleValueMapping) {
                            idMappingValue = (SimpleValueMapping) value;
                        }
                    }
                    
                    if (!needNewId && idMappingValue == null)
                    {
                        idMappingValue = valueFactory.simpleMappingsFactory(idField.getName(),idField.getType(),mapping,table,null);
                    }
                }
//                else
//                {
//                    //XXX try to find PK on the table, then create
//                    // idValueMapping=new SimpleValueMapping to the PK
//                    //if possible create field and idMapping=new
//                    // PropertyMapping
//                    IDatabaseTablePrimaryKey pk = null;
//                    IDatabaseColumn pkColumn;
//                    idMappingValue = null;
//                    
//                    String columnName = null;
//                    
//                    if (table != null)
//                    {
//                        pk = table.getPrimaryKey();
//                        
//                        if ( pk == null || pk.getColumnSpan() == 0) 
//                        {
//                            idMappingValue = new SimpleValueMapping();
//                            
//                            pkColumn = SearchUtils.findColumn(
//                                    table.getColumns(), identifierQuery); //alex 04/13/05
//                            
//                            //PK is null, no Id field - so we have all freedom we can get
//                            if (pkColumn == null) 
//                            {
//                                //no id-like columns, no PK set
//                                idMappingValue.setType(hamConfig.getIdType());
//                                columnName = hamConfig.getIdentifierColumnName();
//                                pkColumn = HibernateAutoMappingHelper.createAndBindColumn(idMappingValue,columnName,table);
//                            } 
//                            else 
//                            {
//                                idMappingValue.setType(TypeUtils.columnTypeToHibTypeDefault(pkColumn));
//                                idMappingValue.addColumn(pkColumn);
//                                columnName = pkColumn.getName();
//                            }
//                            
//                            idMappingValue.setTable(table);
//                            
//                            if (idMappingValue.getType() == null)
//                                idMappingValue = null;
//                            
//                            if (idMappingValue != null)
//                                idField = pfProvider.getOrCreatePersistentField(pc,columnToPropertyName(columnName),idMappingValue.getType(),
//                                        hamConfig.getSettings().canUseHeuristicAlgorithms);
//                            
//                        }
//                        else
//                        {
//                            int colSpan = pk.getColumnSpan();
//                            
//                            //get set earlier PK
//                            Iterator columnsItr = pk.getColumnIterator();
//                            IDatabaseColumn[] columns = new IDatabaseColumn[colSpan];
//                            int i = 0;
//                            while (columnsItr.hasNext()) {
//                                IDatabaseColumn column = (IDatabaseColumn) columnsItr.next();
//                                columns[i] = column;
//                                i++;
//                            }
//                            //idMappingValue = buildReversedIdentifierMapping(mapping,columns);
//                            resetPrimaryKey = false;
//                            
//                            if (idMappingValue != null)
//                            {
//                                idField = idMappingValue.getFieldMapping().getPersistentField();
//                                idField.getMapping().setPersistentValueMapping(idMappingValue);
//                                idMappingValue.setFieldMapping(idField.getMapping());
//                                idMappingValue.setTable(table);
//                            }
//                            else
//                            {
//                                StringBuffer msg = new StringBuffer("Primary key of <");
//                                msg.append(table.getName());
//                                msg.append("> table failed to reverse, probably due to unsupported primary key column SQL type");
//                                OrmCore.log(msg.toString());
//                            }
//                        }
//                    }
//                    
//                    //XXX create field and property mapping
//                }
                if (idMappingValue == null)
                {
                    //Id field type not suitable to be Id at all (is array etc.) so we should create default id here
//                    String prefixName = pc.getShortName()+"_";
                    String idFieldName = columnToPropertyName(hamConfig.getIdentifierColumnName());

                    if (idFieldName.length() == 0)
                    {
                        idFieldName = "id";
                    }
                    
                    idField = pfProvider.getOrCreatePersistentField(pc,StringUtils.beanDecapitalize(idFieldName),hamConfig.getIdType(),
                            hamConfig.isUseFuzzySearch());
                    idMappingValue = valueFactory.simpleMappingsFactory(idField.getName(),idField.getType(),mapping,table,null);
                    resetPrimaryKey = true;
                }

                //Id field found or just created
                if (idField.getMapping() != null) 
                {
                    IPersistentFieldMapping idFieldMapping = idField.getMapping();
                    valueFactory.addPersistentField(idField,null);
                    mapping.setIdentifier((IHibernateKeyMapping) idFieldMapping.getPersistentValueMapping());
                    mapping.setIdentifierProperty((IPropertyMapping) idFieldMapping.getPersistentField().getMapping());
                    if (idFieldMapping.getPersistentValueMapping() != null)
                    {
                        if (idFieldMapping.getPersistentValueMapping() instanceof SimpleValueMapping)
                        {
                            setIdentifierValueDefaults(
                                    (SimpleValueMapping) idFieldMapping.getPersistentValueMapping(),
                                    useDefaultGenerator);
                        }
                    }
                        
                    
                    return; 
                    
                    //XXX Nick mapping exists so set it to
                    // mapping.setIdentifierProperty and
                    // mapping.setIdentifier
                }
                
                if (idField.getType() == null)
                    return; // no type info - skip it
                
                idField.setOwnerClass(pc);
                idMapping = helper.createAndBindPropertyMapping(idField);
                HibernateAutoMappingHelper.bindValueMappingToField(idField,idMappingValue);
                
                if (resetPrimaryKey)
                {
                    if (table.getPrimaryKey() != null) 
                    {
                        //XXX Reset PK on table. Clear old pk and set new
                        // pk
                        table.getPrimaryKey().clear();
                    }
                }
            } 
            else 
            {
                idMappingValue = (SimpleValueMapping) idMapping.getValue();
            }

            if (idMappingValue != null)
                setIdentifierValueDefaults(idMappingValue,useDefaultGenerator);
            
            mapping.setIdentifier(idMappingValue);
            mapping.setIdentifierProperty(idMapping);
        }
        
    }
	
	private String getOrmProperty(String name){
		return config.getOrmProject().getOrmConfiguration().getProperty(name);
	}

    private void hamInit(Settings settings)
    {
        config = (HibernateConfiguration) mapping.getConfiguration();
        hamConfig = ConfigurationReader.getAutomappingConfigurationInstance(mapping,settings);
        helper = new HibernateAutoMappingHelper(hamConfig);
        idFactory = MappingsFactory.getSimpleFactory(hamConfig, new IdentifierColumnBuilderVisitor(mapping));
        sourcePropertyCreation = new SourcePropertyCreationRunnable(hamConfig.getPojoRenderer());
        ormConfig = config.getOrmProject().getOrmConfiguration();
        // $changed$ by Konstantin Mishin on 2005/08/10 fixed fo ORMIISTUD-607
        //pcFiller = new DefaultPersistentClassFiller(ormConfig);
        pcFiller = new DefaultPersistentClassFiller(config.getOrmProject());
        // $changed$ 
        pfProvider = new PersistentFieldProvider(sourcePropertyCreation,hamConfig);
        SearchUtils.setFuzzinessLevel(hamConfig.getFuzzinessSimilarityLevel());
    }
    
    public HibernateAutoMapping(HibernateMapping mapping) {
        this.mapping = mapping;
        hamInit(Settings.DEFAULT_SETTINGS);
    }

    public static void clearLegacyColumnMapping(IHibernateKeyMapping svm, IDatabaseColumn column, boolean cleanFieldMapping)
    {
        if (svm == null)
            return ;
        
        if (column != null)
        {
            if (svm instanceof ISimpleValueMapping/* && column.getPersistentValueMapping() == svm*/) {
                ((ISimpleValueMapping) svm).removeColumn(column);

                column.setPersistentValueMapping(null);
            }
        } else {
            List<IDatabaseColumn> columns = new ArrayList<IDatabaseColumn>();
            Iterator itr = svm.getColumnIterator();
            while (itr.hasNext()) {
                Object o = itr.next();
                if (o instanceof IDatabaseColumn) {
                    IDatabaseColumn column1 = (IDatabaseColumn) o;
                    columns.add(column1);
                }
            }
            
            itr = columns.iterator();
            while (itr.hasNext())
            {
                Object o = itr.next();
                if (o instanceof IDatabaseColumn) {
                    IDatabaseColumn column1 = (IDatabaseColumn) o;
                    clearLegacyColumnMapping(svm,column1, cleanFieldMapping);
                }
            }
        }
        
        if (cleanFieldMapping && svm.getColumnSpan() == 0)
        {
            HibernateAutoMappingHelper.removeMapping(
                    svm.getFieldMapping());
        }

    }
    
    private IPersistentFieldMapping buildReversedIdentifierMapping(HibernateAutoMappingReverseProcessor binder, IPersistentClassMapping classMapping, IDatabaseTable table, IDatabaseTableColumnSet idColumns) throws CoreException
    {
        IPersistentFieldMapping fieldMapping = null;
        
        if (classMapping instanceof ClassMapping) {
            ClassMapping mapping = (ClassMapping) classMapping;
            
            IPersistentClass clazz = mapping.getPersistentClass();
            IDatabaseTableColumnSet pKey = idColumns;//table.getPrimaryKey();
            
            Iterator columns = pKey.getColumnIterator();
            
            List<IOrmElement> columnSets = new ArrayList<IOrmElement>();
            
            //composite Id
            boolean createNewCmpId = true;
            String cmpIdName = hamConfig.getIdentifierColumnName();
            String cmpIdType = null;
             
            IHibernateKeyMapping id = mapping.getIdentifier();
            
            if (id != null)
            {
                ColumnSet idSet = new ColumnSet(id.getColumnIterator());
                if (idSet.equals(pKey))
                {
                    boolean identical = true;
                    Iterator idItr = id.getColumnIterator();
                    
                    while (idItr.hasNext() && identical)
                    {
                        IDatabaseColumn idc = (IDatabaseColumn) idItr.next();
                        
                        if (idc.getOwnerTable() != null)
                        {
                            identical = !( (idc.getPersistentValueMapping() instanceof ManyToOneMapping) ^ idc.getOwnerTable().isForeignKey(idc.getName()) );
                        }
                    }

                    if (identical)
                        return null;
                }
            }
            
            ComponentMapping cmpMapping = null;

            boolean clearOldId = true;
            
            if (id instanceof ComponentMapping) {
                cmpMapping = (ComponentMapping) id;
                
                cmpIdType = cmpMapping.getComponentClassName();
                
                createNewCmpId = false;
                clearOldId = false;
                
                if (cmpMapping.getFieldMapping() != null)
                {
                    //remove persistent field mapping to reuse
                    //but save ComponentMapping for future use
                    cmpMapping.getFieldMapping().setPersistentValueMapping(null);
                    HibernateAutoMappingHelper.removeMapping(cmpMapping.getFieldMapping());
                }
            }
            else
            {
                cmpMapping = new ComponentMapping(mapping);
            }

            HibernateAutoMapping.clearLegacyColumnMapping(id,null,clearOldId);
            
            boolean isComponent = !createNewCmpId;
            //!createNewCmpId - means we have existing composite id
            
            Set<IDatabaseColumn> columnsSkipSet = new HashSet<IDatabaseColumn>();
            
            while (columns.hasNext())
            {
                IDatabaseColumn column = (IDatabaseColumn) columns.next();
                
                if (columnsSkipSet.contains(column))
                    continue;
                
                IDatabaseTableForeignKey referencingFKey = null;
                if (schemaProcessor != null)
                {

                	// edit 29.01.2006 tau for ESORM-286 
                    IDatabaseTableForeignKey[] keys = new IDatabaseTableForeignKey[0]; 
                    if (column != null && column.getOwnerTable() != null) {
                        keys = column.getOwnerTable().getForeignKeys(column.getName());                    	
                    }
                    
                    for (int i = 0; i < keys.length && referencingFKey == null; i++) {
                        IDatabaseTableForeignKey key = keys[i];
                        
                        if (key.getReferencedTable() != null &&
                                HibernateAutoMappingHelper.tableHasMappings(key.getReferencedTable())
                                && pKey.includes(key))
                        {
                            referencingFKey = key;
                        
//                            boolean isValidKey = true;
//                            Iterator itr = referencingFKey.getColumnIterator();
//                            
//                            while (itr.hasNext() && isValidKey)
//                            {
//                                IDatabaseColumn fKeyColumn = (IDatabaseColumn)itr.next();
//                                //column shouldn't be already mapped in Id    
//                                isValidKey = (!cmpMapping.containsColumn(fKeyColumn) || (column.getPersistentValueMapping() instanceof ManyToOneMapping && (column.getPersistentValueMapping() == fKeyColumn.getPersistentValueMapping())));
//                            }
//                            
//                            if (!isValidKey)
//                                referencingFKey = null;
                        }
                    }

                    if (referencingFKey != null)
                    {
                        isComponent = true;
                        columnSets.add(referencingFKey);
                        
                        //skip m2o columns
                        Iterator<IDatabaseColumn> m2oColumns = referencingFKey.getColumnIterator();
                        while (m2oColumns.hasNext())
                        {
                            columnsSkipSet.add(m2oColumns.next());
                        }
                    }
                    else /*if (!hamConfig.isIgnored(column))*/
                        columnSets.add(column);
                }
            }
            
            isComponent = (isComponent || (columnSets.size() > 1)); 
            
            Iterator itr = columnSets.iterator();
            
            IPersistentClass idMappingHolder = clazz;
            
            mapping.setIdentifier(null);
            mapping.setIdentifierProperty(null);
            
            if (isComponent)
            {
                cmpMapping.setOwner(mapping);
                cmpMapping.setTable(table);
                
                if (cmpIdType == null)
                {
                    String typeName = clazz.getName()+"$Id";
                    cmpIdType = findUnMappedTypeName(typeName);
                }
                
                cmpMapping.setComponentClassName(cmpIdType);
                PersistentClass cmpClass = (PersistentClass)cmpMapping.getComponentClass();
                
                if (createNewCmpId)
                {
                    ICompilationUnit unit = createReversedCmpIdOperation.addNameToQueue(ClassUtils.getPackageFragment(clazz.getSourceCode()),cmpClass.getShortName());
                    cmpClass.setSourceCode(unit);
                }
                cmpClass.refresh();
                idMappingHolder = cmpClass;

                IPersistentField field = pfProvider.getOrCreatePersistentField(mapping.getPersistentClass(),cmpIdName,cmpClass.getName(),
                        helper.getConfiguration().isUseFuzzySearch());
                
                fieldMapping = helper.createAndBindPropertyMapping(field);

                cmpMapping.setFieldMapping(fieldMapping);
                fieldMapping.setPersistentValueMapping(cmpMapping);
            
                while (itr.hasNext()) {
                    Object obj = itr.next();
                    
                    if (obj instanceof IDatabaseTableForeignKey) {
                        IDatabaseTableForeignKey fKey = (IDatabaseTableForeignKey) obj;
                        
                        Iterator<IDatabaseColumn> fkColumns = fKey.getColumnIterator();
                        while (fkColumns.hasNext()) {
                            Object o = fkColumns.next();
                            if (o instanceof IDatabaseColumn) {
                                IDatabaseColumn column = (IDatabaseColumn) o;
                                if (column.getPersistentValueMapping() instanceof SimpleValueMapping) {
                                    SimpleValueMapping svm = (SimpleValueMapping) column.getPersistentValueMapping();
                                    clearLegacyColumnMapping(svm,column, true);
                                }
                            }
                        }
                        
                        
                        IPersistentClass refTo = fKey.getReferencedTable().getPersistentClassMappings()[0].getPersistentClass();
                        
                        IPersistentFieldMapping mtoMapping = binder.bindManyToOne(fKey,idMappingHolder,refTo,false);
                        
                        if (mtoMapping instanceof PropertyMapping && isComponent) {
                            cmpMapping.addProperty( (PropertyMapping) mtoMapping);
                        }
                    }
                    
                    if (obj instanceof IDatabaseColumn) {
                        IDatabaseColumn column = (IDatabaseColumn) obj;
                        
                        if (column.getPersistentValueMapping() != null)
                        {
                            if (column.getPersistentValueMapping() instanceof SimpleValueMapping) {
                                SimpleValueMapping svm = (SimpleValueMapping) column.getPersistentValueMapping();
                                //remove legacy many-to-one in PK
                                clearLegacyColumnMapping(svm,column, true);
                            }
                            else
                            {
                                //this column is outside composite id
                                HibernateAutoMappingHelper.removeMapping(
                                        column.getPersistentValueMapping().getFieldMapping());
                                column.setPersistentValueMapping(null);
                            }
                        }
                        
                        IPersistentFieldMapping pfm = binder.bindSimpleValue(column,idMappingHolder);
                        if (pfm instanceof PropertyMapping && isComponent) {
                            cmpMapping.addProperty((PropertyMapping) pfm);
                        }
                        
                        if (!isComponent)
                        {
                            fieldMapping = pfm;
                        }
                    }
                }
            }
            else
            {
                if (!columnSets.isEmpty())
                {
                    IDatabaseColumn column = (IDatabaseColumn) columnSets.get(0);
                    Type valueType = TypeUtils.columnTypeToHibTypeDefault(column, hamConfig);
                    if (valueType != null)
                    {
                        if (column.getPersistentValueMapping() != null &&
                                column.getPersistentValueMapping().getFieldMapping() != null)
                        {
                            fieldMapping = column.getPersistentValueMapping().getFieldMapping();
                        }
                        else
                        {
                            fieldMapping = binder.bindSimpleValue(column,clazz);
                        }
                    }
                }
            }
            
            if (fieldMapping == null)
            {
                generateIdentity(mapping,MappingsFactory.getSimpleFactory(hamConfig,new ColumnMergingVisitor(this.mapping)));
            }
        }
        
        
        return fieldMapping;
    }
    
    
    private void findCompilationUnits(IDatabaseTable[] persistentTables, List<ICompilationUnit> accessibleUnits, ICompilationUnit[] units, boolean isFuzzy)
    {
        for (int i = 0; i < persistentTables.length; i++) 
        {
            if (units[i] != null)
                continue ; 
            
            IDatabaseTable table = persistentTables[i];
            
            if (HibernateAutoMappingHelper.tableHasMappings(table))
                continue ;
            
            String tableName = schemaProcessor.getUnPrefixedTableName(table).replace('$','_');
            
            ICompilationUnit unit = SearchUtils.findCompilationUnit(tableToClassName(tableName),(ICompilationUnit[])accessibleUnits.toArray(new ICompilationUnit[0]),isFuzzy);

            if (unit != null)
            {
                accessibleUnits.remove(unit);
                units[i] = unit;
            }
        }
    }
    
    private String findUnMappedTypeName(String name) throws CoreException
    {
        String newName = name;
        String result = null;
        
        int counter = 1;

        while (result == null)
        {
            if (mapping.findClass(name) == null)
            {
                IType type = ScanProject.findClass(newName,config.getProject());
                
                if (type == null || !type.isBinary())
                {
                    result = newName;
                }
            }

            newName = name + Integer.toString(counter);
            counter++;
        }

        return result;
    }
    
    private String findUnMappedCUName(String name, IPackageFragment[] fragments) throws CoreException
    {
        if (fragments == null || fragments.length == 0)
            return name;
        
        String result = null;
        String newUnitName = name;
        String packageName = fragments[0].getElementName();

        int counter = 1;
        
        while (result == null)
        {
            String fqName = Signature.toQualifiedName(new String[]{packageName,newUnitName});
            if (mapping.findClass(fqName) == null)
            {
                result = newUnitName;
                
                // check other-cased Java Unit existence
                boolean found = false;
                
                if (fragments != null)
                {
                    
                    for (int i = 0; i < fragments.length && !found; i++) {
                        IPackageFragment fragment = fragments[i];
                        
                        IJavaElement[] elements = fragment.getChildren();
                        for (int j = 0; j < elements.length; j++) {
                            IJavaElement element = elements[j];
                            
                            if ( !(element instanceof ICompilationUnit || element instanceof IClassFile) )
                                continue;
                            
                            String elementName = element.getElementName();
                            
                            String simpleName = Signature.getSimpleName(fqName);
                            
                            if ((simpleName+".java").equalsIgnoreCase(elementName))
                                found = true;
                        }
                    }
                }
                
                if (found)
                    //if other-cased Java Unit exists skip to next name
                    result = null;
                else
                {
                    IType type = ScanProject.findClass(fqName,config.getProject());
                    
                    if (type != null && type.isBinary())
                    {
                        result = null;
                    }
                }
                
            }
            
            newUnitName = name + Integer.toString(counter);
            counter++;
        }
       
        return result;
    }
    
    /* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IAutoMappingService#generateMapping(org.jboss.tools.hibernate.core.IDatabaseTable[], org.jboss.tools.hibernate.core.IAutoMappingService.Settings)
	 */
	public void generateMapping(IDatabaseTable[] tables, Settings settings,ReversStatistic info) throws CoreException {
		//XXX Nick Find or create POJO classes for the tables.
		
        OrmProgressMonitor monitor = new OrmProgressMonitor(OrmProgressMonitor.getMonitor());
        
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("Starting reversing job. Have "+tables.length+" tables to reverse");
        
        hamInit(settings);
		
        //process package, create if there is none, set proper thePackage variable
        //compute accessible CUs
        IPackageFragment[] thePackage = null;
        
        String defaultPackageName = ormConfig.getProperty("hibernate.default_package");
        
        if (defaultPackageName == null)
            return;

        try {
            thePackage = hamConfig.getPojoRenderer().getOrCreatePackage(config.getProject(),defaultPackageName);
        } catch (CoreException e) {
            ExceptionHandler.logThrowableError(e,"Exception creating package");
            return;
        }
 
        if (thePackage == null || thePackage.length == 0)
            return;
        
        List<ICompilationUnit> accessibleUnits;

        List packages = ClassUtils.getUnderlyingPackageFragments(thePackage);

        class FilterUnMappedUnits implements ScanProject.IFilterInterface
        {
            /* (non-Javadoc)
             * @see org.jboss.tools.hibernate.internal.core.util.ScanProject.IFilterInterface#isCompatible(java.lang.Object)
             */
            public boolean isCompatible(Object o) {
                if (o instanceof ICompilationUnit) {
                    ICompilationUnit unit = (ICompilationUnit) o;
                    
                    // added by Nick 08.10.2005
                    ICompilationUnit wc = null;
                    if (!unit.isWorkingCopy())
                        try {
                            wc = unit.getWorkingCopy(null);
                        } catch (JavaModelException e) {
                            ExceptionHandler.logThrowableError(e,e.getMessage());
                        }
                    //by Nick
                    
                    IType type = unit.findPrimaryType();
                    //here we use only primary types - not inner! so #getFullyQualifiedName() is enough
                    
                    if (wc != null)
                    {
                        try
                        {
                            wc.discardWorkingCopy();
                        }
                        catch (JavaModelException e)
                        {
                            ExceptionHandler.logThrowableError(e,e.getMessage());
                        }
                    }
                    
                    if (type != null && mapping.findClass(type.getFullyQualifiedName()) == null)
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                    return false;
            }
        }
        
        accessibleUnits = ScanProject.getAllCompilationUnits(packages,new FilterUnMappedUnits());
        //

        if (monitor != null)
            monitor.worked(5);
        
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logObjectPlugin("Package analysis finished. "+accessibleUnits.size()+" acceptable CU's found",
                OrmCore.getDefault().getBundle().getSymbolicName(), this);
        
        IPackageFragment defaultPackage = thePackage[0];
        
        SchemaSynchronizationHelper syncContext = new SchemaSynchronizationHelper();
        schemaSyncVisitor = new SchemaSynchronizationVisitor(mapping,syncContext);
        for (int i = 0; i < tables.length; i++) {
            IDatabaseTable table = tables[i];
            if (table.getPersistentClassMappings() != null)
            {
                for (int j = 0; j < table.getPersistentClassMappings().length; j++) {
                    IPersistentClassMapping classMapping = table.getPersistentClassMappings()[j];
                    
                    classMapping.accept(schemaSyncVisitor, null);
                }
            }
        }
        
        schemaProcessor = new SchemaProcessor(tables,hamConfig);
		schemaProcessor.processTables();
		IDatabaseTable[] persistentTables = schemaProcessor.getPersistentTables();
		final IPersistentClass[] pcArray = new IPersistentClass[persistentTables.length];

        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logObjectPlugin("Tables processed. Found "+persistentTables.length+" persistent classes",
                OrmCore.getDefault().getBundle().getSymbolicName(), this);

        
        //process found persistent tables
		createCUOperation = new ReversingCreateCompilationUnitsOperation(hamConfig.getPojoRenderer(), hamConfig.getReversingBaseClass(),mapping);
        createReversedCmpIdOperation = new ReversingCreateCompilationUnitsOperation(hamConfig.getPojoRenderer(), ScanProject.findClass(Serializable.class.getName(),mapping.getProject().getProject()),
                mapping);
        
        ICompilationUnit[] units = new ICompilationUnit[persistentTables.length];
        
        findCompilationUnits(persistentTables,accessibleUnits,units,false);
        
        if (hamConfig.isUseFuzzySearch())
            findCompilationUnits(persistentTables,accessibleUnits,units,true);
            
        int createdCUNumber = 0;
        
        SchemaSynchronizationVisitor2 mappingSchemaSyncVisitor = new SchemaSynchronizationVisitor2(mapping,syncContext,tables,schemaProcessor.getLinkingTables());
        mapping.accept(mappingSchemaSyncVisitor,null);
        mappingSchemaSyncVisitor.process();
        
        for (int i = 0; i < persistentTables.length; i++) 
        {
            IDatabaseTable table = persistentTables[i];
            
            IPersistentClass clazz;
            
            if (!HibernateAutoMappingHelper.tableHasMappings(table))
            {
                String tableName = schemaProcessor.getUnPrefixedTableName(table).replace('$','_');
                String shortClassName = tableToClassName(tableName);
                if (units[i] == null)
                {
                    units[i] = createCUOperation.addNameToQueue(defaultPackage,findUnMappedCUName(shortClassName,thePackage));
                    createdCUNumber++;
                }    
                shortClassName = units[i].getElementName();
                int index = shortClassName.lastIndexOf(".java");
                if (index != -1)
                    shortClassName = shortClassName.substring(0,index);

                String packageName = null;
                IPackageFragment fragment = ClassUtils.getPackageFragment(units[i]);
                if (fragment != null)
                {
                    packageName = fragment.getElementName();
                }
                else
                {
                    packageName = defaultPackageName;
                }

                clazz = pcArray[i] = mapping.getOrCreatePersistentClass(Signature.toQualifiedName(new String[]{packageName,shortClassName}));
                clazz.setSourceCode(units[i]);
                config.getOrCreateClassMappingForTable(pcArray[i],persistentTables[i],null);

                ClassMapping rootMapping = (ClassMapping)clazz.getPersistentClassMapping();
                pcFiller.fillClassMapping(rootMapping);
            }
            else
            {
                clazz = pcArray[i] = table.getPersistentClassMappings()[0].getPersistentClass();
            }

            mapping.refreshClass(clazz);
        }
        
        if (monitor != null)
            monitor.worked(5);
        
        //create new compilation units
        createCUOperation.setJobPart(5);
        createCUOperation.setOrmMonitor(monitor);
        sourcePropertyCreation.setJobPart(30);
        sourcePropertyCreation.setOrmMonitor(monitor);
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logObjectPlugin("New CU's created",
                OrmCore.getDefault().getBundle().getSymbolicName(), this);

        IProgressMonitor theMonitor = OrmProgressMonitor.getMonitor();
        theMonitor.setTaskName("Creating source files");

        config.getProject().getWorkspace().run(createCUOperation,new SubProgressMonitor(theMonitor,2));

        //do not need base class for collections of components
        createCUOperation = new ReversingCreateCompilationUnitsOperation(hamConfig.getPojoRenderer(), null,mapping);

        if (monitor != null)
            monitor.setTaskParameters(5,pcArray.length);
        
//        for (int i = 0; i < pcArray.length; i++) {
//            
//            PersistentClass clazz = pcArray[i];
//            //process primary keys
//            //generateIdentity(rootMapping,MappingsFactory.getSimpleFactory(hamConfig, new ColumnBuilderVisitor(mapping)),null);
//        
//            if (monitor != null)
//                monitor.worked();
//        } 
		//
        

        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logObjectPlugin("Persistent classes refreshed and have their identity filled",
                OrmCore.getDefault().getBundle().getSymbolicName(), this);

        
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logObjectPlugin("Starting reversing/automapping",
                OrmCore.getDefault().getBundle().getSymbolicName(), this);
        
        ColumnMergingVisitor visitor = new ColumnMergingVisitor(mapping);
        MappingsFactory valueFactory = MappingsFactory.getFullFactory(hamConfig,visitor);
        MappingsFactory idValueFactory = MappingsFactory.getSimpleFactory(hamConfig,visitor);
        
        if (monitor != null)
            monitor.setTaskParameters(5,pcArray.length);
        
        HibernateAutoMappingReverseProcessor binder = new HibernateAutoMappingReverseProcessor(valueFactory,pfProvider,helper);
        HibernateAutoMappingReverseProcessor idBinder = new HibernateAutoMappingReverseProcessor(idValueFactory,pfProvider,helper);
        
        Map<IPersistentClass,IPersistentFieldMapping> identifierMappings = new HashMap<IPersistentClass,IPersistentFieldMapping>();
        
        //do the reversing       
		for (int i = 0; i < pcArray.length; i++) 
		{
            IDatabaseTable table = persistentTables[i];
            
            IDatabaseTableColumnSet idColumns = schemaProcessor.getIdentifier(table);
            
            if (idColumns == null)
                continue;

            IPersistentClass clazz = pcArray[i];
            if (clazz == null)
                continue;
        
//            clazz.accept(visitor,null);
            
            IPersistentFieldMapping idMapping = buildReversedIdentifierMapping(idBinder,clazz.getPersistentClassMapping(),table,idColumns);
            if (idMapping != null)
                identifierMappings.put(clazz,idMapping);
        }

        //pfProvider.fieldsProcess(settings.canUseHeuristicAlgorithms);

        Iterator classes = identifierMappings.keySet().iterator();
        while (classes.hasNext())
        {
            IPersistentClass clazz = (IPersistentClass) classes.next();
            IPersistentFieldMapping fm = (IPersistentFieldMapping) identifierMappings.get(clazz);
            generateIdentity(
                    (ClassMapping)clazz.getPersistentClassMapping(),
                    idValueFactory,
                    fm.getPersistentField().getName());
        
            idValueFactory.addPersistentField(fm.getPersistentField(),null);
        }

        
        for (int i = 0; i < pcArray.length; i++) 
        {
            IDatabaseTable table = persistentTables[i];
            
            IDatabaseTableColumnSet pKey = schemaProcessor.getIdentifier(table);
            if (pKey == null)
                continue;

            IPersistentClass clazz = pcArray[i];
            if (clazz == null)
                continue;
        
            Iterator fKeys = table.getForeignKeyIterator();
			if (fKeys != null)
			{
				while (fKeys.hasNext())
				{
					IDatabaseTableForeignKey fKey = (IDatabaseTableForeignKey)fKeys.next();

                    if (pKey.intersects(fKey) || fKey.getColumnSpan() == 0)
                        continue;
                    
                    
                    IDatabaseTable refTable = fKey.getReferencedTable();
                    if (refTable != null)
                    {
                        IPersistentClassMapping[] mappings = refTable.getPersistentClassMappings();
                        if (mappings != null && mappings.length > 0)
                        {
                            ClassMapping referencedClassMapping = (ClassMapping)mappings[0];
                            PersistentClass referencedPC = (PersistentClass)referencedClassMapping.getPersistentClass();

                            LinkedFieldsSearcher lfs = new LinkedFieldsSearcher(mapping);
                            
                            ArrayList oppSideList = lfs.findOppositeSide(fKey.getColumnIterator());
                            if (oppSideList.size() >= 2)
                                continue ;
                            
                            
                            boolean mappedSides = (clazz.getPersistentClassMapping() != null && clazz.getPersistentClassMapping().getIdentifier() != null);
                            // #changed# by Konstantin Mishin on 14.01.2006 fixed for ESORM-458
                            //mappedSides &= (referencedPC.getPersistentClassMapping() != null && referencedPC.getPersistentClassMapping().getIdentifier() != null);
                            mappedSides &= (referencedPC != null && referencedPC.getPersistentClassMapping() != null && referencedPC.getPersistentClassMapping().getIdentifier() != null);
                            // #changed#                           
                            
                            if (oppSideList.size() == 1 && mappedSides)
                            {
                                Object oppSide = oppSideList.get(0);
                                
                                if (oppSide instanceof ICollectionMapping)
                                {
                                    ICollectionMapping coll = (ICollectionMapping) oppSide;

                                    IPersistentFieldMapping mtoMapping = binder.bindManyToOne(fKey,clazz,referencedPC,false);
                                    
                                    if (mtoMapping instanceof IPropertyMapping)
                                    {
                                        IPropertyMapping pm = (IPropertyMapping) mtoMapping;
                                        
                                        if (mtoMapping.getPersistentValueMapping() instanceof SimpleValueMapping) {
                                            SimpleValueMapping svm = (SimpleValueMapping) mtoMapping.getPersistentValueMapping();
                                            
                                            if (coll.isIndexed())
                                            {
                                                svm.setUpdateable(coll.isInverse());
                                                pm.setInsertable(coll.isInverse());
                                            }
                                            else
                                            {
                                                coll.setInverse(true);
                                            }
                                        }
                                    }
                                }
                                
                                if (oppSide instanceof IManyToOneMapping)
                                {
                                    IManyToOneMapping mto = (IManyToOneMapping) oppSide;
                                    if (mto.getFieldMapping() instanceof IPropertyMapping) {
                                        IPropertyMapping pm = (IPropertyMapping) mto.getFieldMapping();
                                        
                                        pm.setInsertable(true);
                                        mto.setUpdateable(true);
                                    }
                                    
                                    binder.bindOneToMany(fKey,clazz,referencedPC);
                                }
                                
                            }
                            else
                            if (!fKey.containsMappedColumns())
                                binder.bindManyToOne(fKey,clazz,referencedPC,true);
                        }
                    }
                }
            }
			
            Iterator columnIterator = table.getColumnIterator();
            while (columnIterator.hasNext())
            {
                IDatabaseColumn column = (IDatabaseColumn)columnIterator.next();
                
                if (hamConfig.isIgnored(column))
                    continue;
                
                if (table.isForeignKey(column.getName()) || pKey.includes(new ColumnSet(column)))
                    continue;
                
                if (column.getPersistentValueMapping() != null)
                    continue;

                binder.bindSimpleValue(column,clazz);
            }
            
            if (monitor != null)
                monitor.worked();
        }
        
        IDatabaseTable[] linkingTables = schemaProcessor.getLinkingTables();
        
        for (int j = 0; j < linkingTables.length; j++) {
            IDatabaseTable linkingTable = linkingTables[j];
            
            IDatabaseTableForeignKey[] linkingKeys = schemaProcessor.getLinkedTables(linkingTable);                
            if (linkingKeys != null && linkingKeys.length == 2 && !linkingKeys[0].containsMappedColumns()
                    && !linkingKeys[1].containsMappedColumns())
            {
                IDatabaseTable oneSideTbl = linkingKeys[0].getReferencedTable();
                IDatabaseTable otherSideTbl = linkingKeys[1].getReferencedTable();

                ClassMapping oneSide = null;
                if (oneSideTbl.getPersistentClassMappings() != null && oneSideTbl.getPersistentClassMappings().length > 0)
                    oneSide = (ClassMapping)oneSideTbl.getPersistentClassMappings()[0];

                ClassMapping otherSide = null;
                if (otherSideTbl.getPersistentClassMappings() != null && otherSideTbl.getPersistentClassMappings().length > 0)
                    otherSide = (ClassMapping)otherSideTbl.getPersistentClassMappings()[0];
            
                if (oneSide == null || otherSide == null)
                    continue;
                
                if (linkingKeys[0].containsMappedColumns() || linkingKeys[1].containsMappedColumns())
                    continue;
                
                IPersistentFieldMapping[] m2mMappings = binder.bindManyToMany(linkingKeys[1],linkingKeys[0],oneSide.getPersistentClass(),otherSide.getPersistentClass());
            
                IDatabaseColumn[] linkColumns = linkingTable.getColumns();
                if (linkColumns != null)
                {
                    int suitableColumnsNumber = linkColumns.length;
                    for (int i = 0; i < linkColumns.length; i++) {
                        IDatabaseColumn column = linkColumns[i];
                        
                        if (linkingTable.isForeignKey(column.getName()))
                        {
                            linkColumns[i] = null;
                            suitableColumnsNumber-- ;
                        }
                    }
                    
                    if (suitableColumnsNumber > 0)
                    {
                        units = new ICompilationUnit[1];
                        
                        findCompilationUnits(new IDatabaseTable[]{linkingTable},accessibleUnits,units,false);
                        
                        if (hamConfig.isUseFuzzySearch())
                            findCompilationUnits(new IDatabaseTable[]{linkingTable},accessibleUnits,units,true);

                        if (units[0] == null)
                        {
                            String tableName = schemaProcessor.getUnPrefixedTableName(linkingTable).replace('$','_');
                            String shortClassName = tableToClassName(tableName);
                            units[0] = createCUOperation.addNameToQueue(defaultPackage,findUnMappedCUName(shortClassName,thePackage));
                        }
                        
                        String packageName = null;
                        IPackageFragment fragment = ClassUtils.getPackageFragment(units[0]);
                        if (fragment != null)
                        {
                            packageName = fragment.getElementName();
                        }
                        else
                        {
                            packageName = defaultPackageName;
                        }
                        String shortClassName = units[0].getElementName();
                        int index = shortClassName.lastIndexOf(".java");
                        if (index != -1)
                            shortClassName = shortClassName.substring(0,index);

                        String ternaryClassName = Signature.toQualifiedName(new String[]{packageName,shortClassName});
                        
                        //drop many-to-many and create ternary association
                        convertM2MToTernary(m2mMappings,linkingKeys,linkColumns,binder,ternaryClassName,units[0]);
                    }
                }
            }
        }

        //pfProvider.fieldsProcess(settings.canUseHeuristicAlgorithms);
       
        //valueFactory.process();

        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logObjectPlugin("Creating source properties in CU's",
                OrmCore.getDefault().getBundle().getSymbolicName(), this);
        
        //create composite id compilation units

        for (int i = 0; i < pcArray.length; i++) {
            IPersistentClassMapping ipcm = pcArray[i].getPersistentClassMapping();
            
            if (ipcm instanceof RootClassMapping ||
                    ipcm instanceof UnionSubclassMapping) {
                ClassMapping cm = (ClassMapping) ipcm;
                queuedCreateVersionMapping(cm,config,false,true);
            }
        }
        
        config.getProject().getWorkspace().run(createCUOperation,new SubProgressMonitor(theMonitor,2));

        config.getProject().getWorkspace().run(createReversedCmpIdOperation,new SubProgressMonitor(theMonitor,1));
        
        OrmProgressMonitor.getMonitor().setTaskName("Creating source code");//7/12

        IWorkspaceRunnable methodsRunnable = new IWorkspaceRunnable()
        {
            public void run(IProgressMonitor monitor) throws CoreException {

                OrmProgressMonitor o_monitor = new OrmProgressMonitor(monitor);
                o_monitor.setTaskParameters(100,pcArray.length);
                
                for (int i = 0; i < pcArray.length; i++) {
//                    if (pcArray[i].getSourceCode() != null)
//                        pcArray[i].getSourceCode().makeConsistent(null);
                    ICompilationUnit wc = null;

                    // edit tau 14.11.2005 - resolve ESORM-375
                    //if (!pcArray[i].getSourceCode().isWorkingCopy())
                    if (pcArray[i].getSourceCode() != null && !pcArray[i].getSourceCode().isWorkingCopy())                    
                        wc = pcArray[i].getSourceCode().getWorkingCopy(null);

                    helper.createEquals_HashCode(hamConfig.getPojoRenderer(), pcArray[i]);

                    if (wc != null)
                    {
                        wc.discardWorkingCopy();
                    }
                    
                    o_monitor.worked();
                } 
            }
        };

       config.getProject().getWorkspace().run(methodsRunnable,new SubProgressMonitor(theMonitor,8));

       config.getProject().getWorkspace().run(sourcePropertyCreation,new SubProgressMonitor(theMonitor,22));
        
       
       helper.generateReversingReport(tables.length,persistentTables.length - createdCUNumber,tables.length - (persistentTables.length + linkingTables.length),linkingTables.length);
        info.setFoundCUNumber(persistentTables.length - createdCUNumber);
        info.setLinkTablesNumber(linkingTables.length);
        info.setSkippedTablesNumber(tables.length - (persistentTables.length + linkingTables.length));
        info.setTablesNumber(tables.length);
        //by Nick 31.05.2005 - save is done in invoking method
        /*        try 
		{
		    mapping.save();
		} 
		catch (CoreException e) 
		{
		    ExceptionHandler.log(e,"Error writing mappings");
		}
*/       
        //by Nick
        
	}

    private void convertM2MToTernary(IPersistentFieldMapping[] holders, IDatabaseTableForeignKey[] linkKeys, IDatabaseColumn[] columns, HibernateAutoMappingReverseProcessor binder, String ternaryClazzName,ICompilationUnit source) throws CoreException
    {
        if (holders[0].getPersistentValueMapping() instanceof CollectionMapping &&
                holders[1].getPersistentValueMapping() instanceof CollectionMapping) {
            CollectionMapping collection = (CollectionMapping) holders[0].getPersistentValueMapping();
            CollectionMapping collection1 = (CollectionMapping) holders[1].getPersistentValueMapping();
            if (collection.getElement() instanceof IManyToManyMapping && 
                    collection1.getElement() instanceof IManyToManyMapping) {

                ComponentMapping[] ternaryMappings = new ComponentMapping[2];
                ternaryMappings[0] = new ComponentMapping(collection);
                ternaryMappings[1] = new ComponentMapping(collection1);

                ternaryMappings[0].setComponentClassName(ternaryClazzName);
                ternaryMappings[1].setComponentClassName(ternaryClazzName);

                
                for (int i = 0; i < linkKeys.length; i++) {
                    IPersistentClass ternaryClazz = ternaryMappings[i].getComponentClass();
                    ternaryClazz.setSourceCode(source);
                    ternaryClazz.refresh();
                    
                    IDatabaseTableForeignKey key = linkKeys[i == 0 ? 1 : 0];
                    
                    IPersistentClassMapping[] clsMappings = key.getReferencedTable().getPersistentClassMappings();
                    
                    if (clsMappings == null || clsMappings.length == 0)
                        continue;
                    
                    IPersistentFieldMapping mto = binder.bindManyToOne(key,ternaryClazz,clsMappings[0].getPersistentClass(),false);
                    if (mto instanceof IPropertyMapping) {
                        IPropertyMapping pm = (IPropertyMapping) mto;
                        
                        ternaryMappings[i].addProperty(pm);
                    }
                    
                    if (mto != null)
                    {
                        ternaryMappings[i == 0 ? 1 : 0].setParentProperty(mto.getPersistentField().getName());
                    }
                    
//                mto = binder.bindManyToOne(key,ternaryClazz1,clsMappings[0].getPersistentClass(),false);
//                if (mto instanceof IPropertyMapping) {
//                IPropertyMapping pm = (IPropertyMapping) mto;
//                
//                ternaryMapping1.addProperty(pm);
//                }
                
                }

                for (int i = 0; i < columns.length; i++) {
                    IDatabaseColumn column = columns[i];
                    
                    for (int j = 0; j < ternaryMappings.length; j++) {
                        ComponentMapping mapping = ternaryMappings[j];
                        
                        if (column == null)
                            continue;
                        
                        IPersistentFieldMapping pfm = binder.bindSimpleValue(column,mapping.getComponentClass());
                        if (pfm instanceof IPropertyMapping) {
                            IPropertyMapping pm = (IPropertyMapping) pfm;
                            
                            mapping.addProperty(pm);
                        }
                    }
                    
                    
//                    pfm = binder.bindSimpleValue(column,ternaryClazz1);
//                    if (pfm instanceof IPropertyMapping) {
//                        IPropertyMapping pm = (IPropertyMapping) pfm;
//                        
//                        ternaryMapping1.addProperty(pm);
//                    }

                }
                
//                collection.setInverse(false);
//                collection1.setInverse(true);
                collection.setElement(ternaryMappings[0]);
                collection1.setElement(ternaryMappings[1]);
            }
        }
    }
    
    private void generateIdentifierMapping(IPersistentClass[] classes, Settings settings) throws CoreException
    {
        IPersistentClass[] sortedClasses = new IPersistentClass[classes.length];
        System.arraycopy(classes,0,sortedClasses,0,classes.length);
        Arrays.sort(sortedClasses,pcComparator);
        
        for (int i = 0; i < sortedClasses.length; i++) 
        {
            IPersistentClass pc = sortedClasses[i];
            ClassMapping classMapping = (ClassMapping)pc.getPersistentClassMapping();
            //table name
            
            IPersistentClassMapping pcm = classMapping.getSuperclassMapping();
            if (pcm instanceof ClassMapping) {
                ClassMapping cm = (ClassMapping) pcm;
                if (cm.getIdentifier() == null)
                {
                    generateIdentifierMapping(new IPersistentClass[]{cm.getPersistentClass()},settings);
                }
            }
            
            if (HibernateAutoMappingHelper.getPrivateTable(classMapping) == null) {
                if (classMapping instanceof RootClassMapping || 
                        classMapping instanceof UnionSubclassMapping)
                {
                    IPersistentClass rootClass = 
                        pc.getRootClass();
                    if (rootClass != null)
                    {
                        HibernateAutoMappingHelper.getOrCreateTableInMappingSchema(
                                (ClassMapping)rootClass.getPersistentClassMapping(),
                                classToTableName(pc.getShortName()),
                                mapping);
                    }
                }
            }
            
            if (classMapping instanceof RootClassMapping ||
                    classMapping instanceof UnionSubclassMapping) {
                //generateIdentity and create version for table-per-class
                // strategy and root class of other strategies
                generateIdentity(classMapping,idFactory);
                if (classMapping.getIdentifierProperty() != null &&
                        classMapping.getIdentifierProperty().getPersistentField() != null)
                {
                    idFactory.addPersistentField(classMapping.getIdentifierProperty().getPersistentField(),null);
                }
            }
            
            if (classMapping instanceof JoinedSubclassMapping &&
                    ((JoinedSubclassMapping)classMapping).getKey() == null)
            {
                //not a root class in table-per-subclass strategy. create FK
                // referring root PK
                SimpleValueMapping fk = HibernateAutoMappingHelper.createAndBindFK(classMapping.getRootClass()
                        .getIdentifier(), (Table) HibernateAutoMappingHelper
                        .getPrivateTable(classMapping), classMapping.getRootClass()
                        .getPersistentClass().getShortName(), false);
                    JoinedSubclassMapping class_mapping = (JoinedSubclassMapping) classMapping;
                    class_mapping.setKey(fk);
            }
        }
    }
    
	private void generateBasicMapping(IPersistentClass[] classes, Settings settings) throws CoreException
	{
	    for (int i = 0; i < classes.length; i++) {
            IPersistentClass clazz = classes[i];
            ClassMapping classMapping = (ClassMapping)clazz.getPersistentClassMapping();
            
            if (classMapping instanceof RootClassMapping ||
                    classMapping instanceof UnionSubclassMapping) {
                //generateIdentity and create version for table-per-class
                // strategy and root class of other strategies
                queuedCreateVersionMapping(classMapping, config, true, false);
            }

            
            if (classMapping.getClass() == SubclassMapping.class)
            {
                if (classMapping.getDiscriminator() == null)
                {
                    IRootClassMapping rootMapping = classMapping.getRootClass();
                    createDiscriminatorMapping((ClassMapping)rootMapping,config);
                }
                if (classMapping.getDiscriminator() != null)
                    helper.createDiscriminatorValue(classMapping);
            }

        }
    }
    
	/**
	 * @param classes
	 * @param settings
	 * @throws CoreException
	 * 
	 * creates class mapping with default filled property values;
	 * generates identity, version and discriminator (only when needed)
	 */
	private void bindEmptyClassMapping(IPersistentClass[] classes, Settings settings) throws CoreException
    {
        for (int i = 0; i < classes.length; i++) 
		{
            //first loop: create mappings and identities for all persistent
            // classes
            ClassMapping newMapping = null;
            PersistentClass pc = (PersistentClass) classes[i];

            //mapping already exists. I think we should delete that condition.
            // Instead we should check existence of field mappings

            //XXX.toAlex shall we add super classes here using
            // generateMapping() method? You are right.
            // adding superclasses is necessary
            //traverse all superclasses to add their properties at first
            if (pc.getSuperClass() != null)
				this.bindEmptyClassMapping(new IPersistentClass[] { pc
                        .getSuperClass() }, settings);
            
            //XXX Alex class cannot be persisted... Is exception needed here or
            // any other processing?
            //Now we have techique to report problems to user:
            // for any group of problems we should create a class derived from
            // HibernateValidationProblem that will create a problem
            //marker for the resource and provide possible fixes etc.
            //Here an example how to use PersistentClassConformity problem:

            // It is not responsibilities of auto mapping to check conformity:
            //if
            // (!HibernateValidationProblem.classConformity.checkConformity(pc))
            // continue;

            if (classes[i].getPersistentClassMapping() == null)
            {
                if (config.getClassMapping(pc.getName()) instanceof ClassMapping) {
                    newMapping = (ClassMapping) config
                            .getClassMapping(pc.getName());
                }
                if (newMapping == null) {
                    newMapping = (ClassMapping) config.getOrCreateClassMapping(pc);
                }

                pc.setPersistentClassMapping(newMapping);
                
            }

            //various properties
            if (classes[i].getPersistentClassMapping() != null)
            {
                pcFiller.fillClassMapping((ClassMapping)classes[i].getPersistentClassMapping());
            }
        }
    }
    
    private Set<PersistentClass> processedPCs = null;
    
    private void processPCs(IPersistentClass[] classes, Settings settings, MappingsFactory valueFactory, OrmProgressMonitor monitor, boolean isFirstPass) throws CoreException
    {
        if (classes == null || classes.length == 0)
            return ;
        
        //first add empty mappings for all classes to indicate their persistance
        //for collection mappings to be handled properly
        bindEmptyClassMapping(classes,settings);

        if (monitor != null)
            monitor.worked(5);

        generateIdentifierMapping(classes,settings);
        
        if (isFirstPass || hamConfig.isReachabilityReachEverything())
        {
            generateBasicMapping(classes,settings);
        }
        
        if (monitor != null)
            monitor.worked(10);
        

        if (monitor != null)
            monitor.setTaskParameters(65,classes.length);
        
        IPersistentClass[] sortedClasses = new IPersistentClass[classes.length];
        System.arraycopy(classes,0,sortedClasses,0,classes.length);
        Arrays.sort(sortedClasses,pcComparator);

        IdentifierColumnBuilderVisitor idVisitor = new IdentifierColumnBuilderVisitor(mapping);

        for (int i = 0; i < sortedClasses.length; i++) {
            PersistentClass pc = (PersistentClass) sortedClasses[i];
            ClassMapping newMapping = (ClassMapping) pc
            .getPersistentClassMapping();
            
            processedPCs.add(pc);
            
            newMapping.accept(idVisitor,null);
        }
        
        for (int i = 0; i < sortedClasses.length; i++) {
            
            //loop: map properties, using identities created in first loop
            PersistentClass pc = (PersistentClass) sortedClasses[i];
            ClassMapping newMapping = (ClassMapping) pc
            .getPersistentClassMapping();
            
            processedPCs.add(pc);
            
            idVisitor.setProcessLinked(true);
            newMapping.accept(idVisitor,null);
            
            if (newMapping.getIdentifier() != null)
            {
                IHibernateKeyMapping id = newMapping.getIdentifier();
                Iterator itr = id.getColumnIterator();
                while (itr.hasNext())
                {
                    IDatabaseColumn column = (IDatabaseColumn)itr.next();
                    HibernateAutoMappingHelper.createOrBindPK(column);
                }
            }

            
            if (isFirstPass || hamConfig.isReachabilityReachEverything())
            {
                IPersistentField[] fields=pc.getFields(); //returns all or only decalred fields that depends on class mapping
                /* by alex 04/28/05
                 if (hamConfig.getInheritance() == HibernateAutoMappingConfiguration.TABLE_PER_CLASS)
                 fields = pc.getAllFields();
                 else
                 fields = pc.getFields();
                 */
                
                //XXX Nick Create identity mapping, discriminator, optimistic
                // locking columns etc for the ClassMapping
                
                //go on and map fields
                for (int f = 0; f < fields.length; f++) {
                    //Create persistent field mapping for each field
                    PersistentField pField = (PersistentField) fields[f];
                    if (newMapping.getIdentifierProperty() != pField.getMapping())
                        valueFactory.addPersistentField(pField,null);
                }

                if (monitor != null)
                    monitor.worked();
                
                if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logObjectPlugin("Class "+pc.getName()+" processed",
                        OrmCore.getDefault().getBundle().getSymbolicName(), this);
            }
        }
        
        Set<IPersistentClass> resultSet = new HashSet<IPersistentClass>();
        resultSet.addAll(valueFactory.getDeferredClasses());
        resultSet.removeAll(processedPCs);
        processPCs(resultSet.toArray(new IPersistentClass[0]),settings,valueFactory,OrmProgressMonitor.NULL_MONITOR,false);

    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.tools.hibernate.core.IAutoMappingService#generateMapping(org.jboss.tools.hibernate.core.IPersistentClass[],
     *      org.jboss.tools.hibernate.core.IAutoMappingService.Settings)
     */
    public void generateMapping(IPersistentClass[] classes, Settings settings)
            throws CoreException {
        // XXX Nick(5) Implement this method
        //For each pc class
        // - cast IPersistentClass to PersistentClass
        // - create empty pc mapping using
        // HibernateConfiguration.createEmptyClassMapping
        // - for each field of the class
        // -- skip transient and non mappable types
        // -- create PersistentField using PersistentClass.getOrCreateField
        // -- create PropertyMapping
        // -- create ValueMapping that depends on field type and set it to
        // PropertyMapping
        //As example see net.sf.hibernate.tool.class2hbm.MapGenerator from
        // Hibernate extensions project
        //That class does pretty much the same things.
        // If you have questions put them here or send on alex@exadel.com

        OrmProgressMonitor monitor = new OrmProgressMonitor(OrmProgressMonitor.getMonitor());

        hamInit(settings);
        processedPCs = new HashSet<PersistentClass>();
        
        MappingsFactory valueFactory = MappingsFactory.getFullFactory(hamConfig, new ColumnBuilderVisitor(mapping));

        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logObjectPlugin("Starting automapping... Have "+classes.length+" to process",
                OrmCore.getDefault().getBundle().getSymbolicName(), this);

        processPCs(classes,settings,valueFactory,monitor,true);
        
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logObjectPlugin("Persistent fields analyzed. Now go and process them",
                OrmCore.getDefault().getBundle().getSymbolicName(), this);
        
        valueFactory.process();

        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logObjectPlugin("Persistent fields fully processed. Running finalizing jobs",
                OrmCore.getDefault().getBundle().getSymbolicName(), this);
        
        if (sourcePropertyCreation != null)
        {
            sourcePropertyCreation.setJobPart(5);
            
            //edit tau 04.05.2006
            //config.getProject().getWorkspace().run(sourcePropertyCreation,new SubProgressMonitor(OrmProgressMonitor.getMonitor(),5));
            config.getProject().getWorkspace().run(sourcePropertyCreation,
            		RuleUtils.getOrmProjectRule(OrmCore.getDefault().create(config.getProject())),
            		IWorkspace.AVOID_UPDATE,            		
            		new SubProgressMonitor(OrmProgressMonitor.getMonitor(),5));            
        }
        
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logObjectPlugin("Automapping finished",
                OrmCore.getDefault().getBundle().getSymbolicName(), this);

        processedPCs = null;
    }
    
    //utils method
    public void createDiscriminatorMapping(ClassMapping newMapping,
            HibernateConfiguration ormConfig) {
        createDiscriminatorMapping(newMapping, ormConfig, 
				getOrmProperty(OrmConfiguration.DISCRIMINATOR_QUERY) //alex 04/13/05
                );
    }
	
	public static PersistentField createPersistentFieldByType(IPersistentClass clazz, String name, String type, String[] generifiedTypes)
    {
        PersistentField pf;
		if (clazz != null)
        {
            pf = ((PersistentClass)clazz).getOrCreateField(name);
            pf.setOwnerClass(clazz);
        }
        else
        {
            pf = new PersistentField();
            pf.setName(name);
        }
        pf.setType(type);
        pf.setGenerifiedTypes(generifiedTypes);
		return pf;
    }

	
    public void createDiscriminatorMapping(ClassMapping newMapping,
            HibernateConfiguration ormConfig, String candidates) {
        //XXX.toAlex (Nick) I can generate properties by means of Eclipse.
        // Should this technique be used here? What about overlapping properties
        // names?
        // There is also a problem - code is not well formatted and I don't know
        // now how can I format it =(
        // A call like this generates field
        //IType pcType =
        // ScanProject.findClass(pc.getName(),config.getProject());
        //pcType.createField("Integer id;"+"\n"+"public Integer
        // getId()....",null,true,null);

        SimpleValueMapping discriminatorMapping;

        //discriminator mapping only for classes with root class mapping
        if (newMapping.isClass() && newMapping.getDiscriminator() == null) 
        {
            //XXX Nick Get or create column for discriminator value and
            // discriminatorMapping.addColumn()
            //but first try to find existing column:
            // SearchUtils.findColumn(newMapping.getDatabaseTable().getColumns(),"class
            // discriminator type");
            IDatabaseColumn[] mappingColumns = newMapping.getDatabaseTable().getColumns();
            IDatabaseColumn i_column = SearchUtils.findColumn(mappingColumns, candidates);
            
            Column column;
            String columnName = null;
            
            if (i_column != null && i_column instanceof Column) {

                column = (Column) i_column;
                discriminatorMapping = helper.createAndBindSimpleValueMapping(column);
            } 
            else {
                //XXX Nick check property name

                discriminatorMapping = new SimpleValueMapping();
                //XXX default value from IOrmConfiguration
                discriminatorMapping.setType(hamConfig.getDiscriminatorType());
                //XXX By Hibernate reference default name for discriminator column is 'class'
                columnName = hamConfig.getDiscriminatorColumnName(); 
                HibernateAutoMappingHelper.createAndBindColumn(discriminatorMapping,columnName,newMapping.getDatabaseTable());
            }

            //column.setNullable(false);

            //XXX.toAlex (Nick) 9.03.2005 what value should we assign when
            // discriminator column type is not string?
            //discriminator value for all class mappings
            //Leave the class short name for awhile
            newMapping.setDiscriminator(discriminatorMapping);
            discriminatorMapping.setFormula(hamConfig.getDiscriminatorFormula());
        }
        helper.createDiscriminatorValue(newMapping);
    }

    private void queuedCreateVersionMapping(ClassMapping newMapping,
            HibernateConfiguration ormConfig, boolean createIfNotFound, boolean useOnlyMappedFields) throws CoreException {
        queuedCreateVersionMapping(newMapping, ormConfig,
				getOrmProperty(OrmConfiguration.VERSION_QUERY),createIfNotFound, false,useOnlyMappedFields); //alex 04/13/05
    }

    public void createVersionMapping(ClassMapping newMapping,
            HibernateConfiguration ormConfig, String candidates)
            throws CoreException {
        
        queuedCreateVersionMapping(newMapping,ormConfig,candidates,true, true, false);
        ormConfig.getProject().getWorkspace().run(sourcePropertyCreation,null);
    }
        
    private void queuedCreateVersionMapping(ClassMapping newMapping,
            HibernateConfiguration ormConfig, String candidates, boolean createIfNotFound, boolean createAlways, boolean useOnlyMappedFields)
            throws CoreException {

        //XXX Nick Check if default optimistic strategy require version field
        // and only then create the field
        if (newMapping.isClass() && newMapping.getVersion() == null && 
                (createAlways || OrmConfiguration.CHECK_VERSION.equals(newMapping.getOptimisticLockMode()))) 
        {
            IPersistentClass pc = newMapping.getPersistentClass();
            IPersistentField[] fields = pc.getFields();

            List<IPersistentField> fieldsList = new ArrayList<IPersistentField>();
            for (int i = 0; i < fields.length; i++) {
                IPersistentField field = fields[i];
                if (field.getMapping() != null) {
                    IPersistentValueMapping value = field.getMapping().getPersistentValueMapping();
                    
                    boolean suitableField = false;
                    
                    if (newMapping.getIdentifier() != value &&
                            newMapping.getIdentifierProperty() != field.getMapping())
                    {
                        if (value != null)
                        {
                            if (value instanceof SimpleValueMapping) {
                                SimpleValueMapping svm = (SimpleValueMapping) value;
                                if (svm.isSimpleValue())
                                    suitableField = true;
                            }
                        }
                        else
                        {
                            suitableField = true;
                        }
                    }
                    if (suitableField)
                        fieldsList.add(field);
                } else if (!useOnlyMappedFields) {
                    fieldsList.add(field);
                }
            }
            fields = (IPersistentField[]) fieldsList.toArray(new IPersistentField[0]);

            IPersistentField versionField = SearchUtils.findField(fields,
                    candidates,hamConfig.isUseFuzzySearch());
            if (versionField == null && createIfNotFound) 
            {
                versionField = pfProvider.getOrCreatePersistentField(newMapping.getPersistentClass(),columnToPropertyName(hamConfig.getVersionColumnName()),hamConfig.getVersionType(),
                        hamConfig.isUseFuzzySearch());
            }
            if (versionField != null)
			{
                if (newMapping.getVersion() == null)
                {
                    if (versionField.getMapping() == null)
                        helper.createAndBindPropertyMapping(versionField);
                    
                    newMapping.setVersion((IPropertyMapping) versionField
                            .getMapping());
                }
				if (newMapping.getVersion().getValue() == null)
				{
                    ColumnBuilderVisitor visitor = new ColumnBuilderVisitor(mapping);
					MappingsFactory versionFactory = MappingsFactory.getSimpleFactory(hamConfig, visitor);
                    SimpleValueMapping valueMapping = versionFactory.simpleMappingsFactory(versionField.getName(),versionField.getType(),newMapping,(Table) HibernateAutoMappingHelper.getPrivateTable(newMapping),null);
                    HibernateAutoMappingHelper.bindValueMappingToField(versionField,valueMapping);
					if (valueMapping != null)
                    {
                        valueMapping.setNullValue(getOrmProperty(OrmConfiguration.VERSION_UNSAVED_QUERY));
                        versionField.accept(visitor,null);
                    }
				}
			}
        }
    }

   

    //****************** Candidate to utils or to interfaced class ****************************

    /* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IAutoMappingService#getJavaType(org.jboss.tools.hibernate.core.IPersistentField)
	 */
	public String getJavaType(IPersistentField pf) {
		PropertyMapping pm=(PropertyMapping)pf.getMapping();
		Type type = pm.getValue().getType();
		if(type==null) return "String";
		Class clazz=type.getJavaType();
		if(clazz==null) return type.getName();
		else return clazz.getName();
	}

	//XXX.toAlex (Nick) method unqualify is not needed, we can use Signature.getQualifier(), Signature.getSimpleName() = unqualify(),
    // 	Signature.toQualifiedName(). We need this method for other purposes.
    public static String unqualify(String qualifiedName) {
        return qualifiedName == null ? null : qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
        // changed by Nick 19.11.2005 - ESORM-251
    }

    /**
     * Return the unqualified class name, mixed case converted to
     * underscores
     */
    public static String classToTableName(String className) {
        return addUnderscores(unqualify(className));
    }

    /**
     * Return the full property path with underscore seperators, mixed
     * case converted to underscores
     */
    public static String propertyToColumnName(String propertyName) {
        return addUnderscores(propertyName);
    }

	//by Nick 6.05.2005
	public static String tableToClassName(String columnName)
	{
		return StringUtils.capitalize(getValidJavaName(removeUnderscores(columnName),"SafeNameTbl_"));
	}
	//by Nick
	
	//by Nick 22.04.2005
	public static String columnToPropertyName(String columnName)
	{
		return StringUtils.decapitalize(getValidJavaName(removeUnderscores(columnName),"safeNameColumn_"));
	}
	//by Nick
	
    public static String getValidJavaName(String str, String prefix)
    {
        if (str == null)
            return prefix;
        
        if(!str.matches(StringUtils.JAVA_IDENTIFIER_REGEXP))
            return prefix+str;
        
        return str;
    }
    
     /**
     * Return the full property path prefixed by the unqualified class
     * name, with underscore seperators, mixed case converted to underscores
     */
    public static String propertyToTableName(String className,
            String propertyName) {
        return classToTableName(className) + '_'
                + propertyToColumnName(propertyName);
    }

	//by Nick 22.04.2005
    private static String removeUnderscores(String name) {
        char[] chars = name.replaceAll("[^\\w\\d]","").replace(' ','_').toCharArray();
        char[] processedChars = new char[chars.length];
        for (int index = 1; index < chars.length ; index++ )
        {
            if (Character.isUpperCase(chars[index-1]) && Character.isUpperCase(chars[index]))
                processedChars[index] = Character.toLowerCase(chars[index]);
            else
                processedChars[index] = chars[index];
        }
        if (chars.length != 0)
            processedChars[0] = chars[0];
        
        
        StringBuffer buf = new StringBuffer(new String(processedChars));
        for (int i = 1; i < buf.length() + 1; i++) {
            if ('_' == buf.charAt(i - 1))
			{
                if (i < buf.length())
                    buf.replace(i-1,i+1,new String(new char[]{Character.toUpperCase(buf.charAt(i))}));
                else
                    //remove trailing "_"
                    buf.deleteCharAt(i-1);
            }
        }
        return buf.toString();
    }
	//by Nick
	
    private static String addUnderscores(String name) {
        StringBuffer buf = new StringBuffer(name.replace('.', '_'));
        for (int i = 1; i < buf.length() - 1; i++) {
            if (
    				Character.isLowerCase( buf.charAt(i-1) ) &&
    				Character.isUpperCase( buf.charAt(i) ) &&
    				Character.isLowerCase( buf.charAt(i+1) )
    			)
            {
                buf.insert(i++, '_');
            }
        }
        return buf.toString().toLowerCase();
    }
    //************************************** end utils *********************************

    //helper classes
/*
    private interface IOrmElementOperation {
        Object process(IOrmElement element);
    }
*/   
/*
    private class UnNullableColumnSetterOperation implements IOrmElementOperation {
        public Object process(IOrmElement element) {
            if (element instanceof IPersistentField) {
                IPersistentField theField = (IPersistentField) element;
                IPersistentFieldMapping fieldMapping = theField.getMapping();
                if (fieldMapping != null)
                {
                    IPersistentValueMapping valueMapping = fieldMapping.getPersistentValueMapping();
                    Iterator itr = valueMapping.getColumnIterator();
                    if (itr != null)
                    {
                        while (itr.hasNext())
                        {
                            IDatabaseColumn column = (IDatabaseColumn)itr.next();
                            column.setNullable(false);
                        }
                    }
                }
            }
            return null;
        }
    }
*/

    static class DeferredPropertyHolder {
        DeferredPropertyHolder(IPersistentFieldMapping field, IPersistentClass fieldType, int multiplicity, String namePrefix) {
            this.fieldMapping = field;
            this.fieldType = fieldType;
            this.multiplicity = multiplicity;
            this.namePrefix = namePrefix;
        }
        IPersistentFieldMapping fieldMapping;
        IPersistentClass fieldType;
        int multiplicity;
        String namePrefix;
        
        IPersistentField getField()
        {
            if (this.fieldMapping != null)
                return fieldMapping.getPersistentField();
            else
                return null;
        }
    }
    
    //persistent classes comparator to sort in superclasses-first order
    private final static Comparator<IPersistentClass> pcComparator = new Comparator<IPersistentClass>() {
        public int compare(IPersistentClass o1, IPersistentClass o2) {
            if (o1 == null || o2 == null)
                return 0;
            IPersistentClass class1 = o1;
            IPersistentClass class2 = o2;
            if (class1 == class2)
                return 0;

            int depth1 = 0;
            int depth2 = 0;
            while ((class1 = class1.getSuperClass()) != null)
                depth1++;
            while ((class2 = class2.getSuperClass()) != null)
                depth2++;
            
            return depth1-depth2;
        }
    };
    
// added by yk 08.09.2005
    public final static Comparator<IPersistentClassMapping> mappingComparator = new Comparator<IPersistentClassMapping>()
    {
        public int compare(IPersistentClassMapping o1, IPersistentClassMapping o2) {
            if (o1 == null || o2 == null)
                return 0;
            
            IPersistentClass class1 = o1.getPersistentClass();
            IPersistentClass class2 = o2.getPersistentClass();
            if (class1 == class2)
                return 0;

            int depth1 = 0;
            int depth2 = 0;
            while ((class1 = class1.getSuperClass()) != null)
                depth1++;
            while ((class2 = class2.getSuperClass()) != null)
                depth2++;
            
            return depth1-depth2;
        }
    };
// added by yk 08.09.2005.

    
    //by Nick - queued for deletion
/*    public CollectionMapping buildReversedManyToManyMapping(IDatabaseTable linkTable, ClassMapping oneSide, ClassMapping otherSide, String mappingName, String fkName) throws CoreException
    {
        ManyToManyMapping mtm = new ManyToManyMapping(linkTable);

        PersistentClass oneSidePc = (PersistentClass)oneSide.getPersistentClass();
        PersistentClass otherSidePc = (PersistentClass)otherSide.getPersistentClass();
       
        mtm.setAssociatedClass(otherSidePc);
        mtm.setClassMapping(oneSide.getIdentifier());
        
        String propertyName = StringUtils.decapitalize(otherSidePc.getShortName())+"Many";
        
        IPersistentField pf = internalGetOrCreateReversedField(oneSide,propertyName,hamConfig.getUnindexedCollectionName());
        PropertyMapping pm = helper.createAndBindPropertyMapping(pf);

        mtm.setTable(linkTable);
        mtm.setTypeName(pf.getType());
        mtm.setName(mappingName);
        mtm.setForeignKeyName(fkName);

        if (otherSide.getIdentifier() != null)
        {
            Iterator itr = otherSide.getIdentifier().getColumnIterator();
            if (itr != null)
            {
                while (itr.hasNext())
                {
                    IDatabaseColumn column = (IDatabaseColumn)itr.next();
                    column.setPersistentValueMapping(mtm);
                    mtm.addColumn(column);
                }
            }
        }
        
        CollectionMapping cm = new BagMapping(oneSide);
        cm.setKey(oneSide.getIdentifier());
        cm.setCollectionTable(linkTable);
        
        cm.setElement(mtm);
        cm.setCollectionElementClassName(otherSidePc.getName());
        
        cm.setFieldMapping(pm);
        pm.setValue(cm);
        
        return cm;
    }

//akuzmin 17.04.2005
    public PersistentField createPersistentFieldByType(ClassMapping mapping, Type type, String name)
    {
        return createPersistentFieldByType(mapping,type.getJavaType().getName(),name);
    }
*/
    /**
     * Convert mixed case to underscores
     */
/*    public static String tableName(String tableName) {
        return addUnderscores(tableName);
    }
*/
    /**
     * Convert mixed case to underscores
     */
/*    public static String columnName(String columnName) {
        return addUnderscores(columnName);
    }
*/

/*    public CollectionMapping buildReversedManyToManyMapping(IDatabaseTable linkTable, ClassMapping oneSide, ClassMapping otherSide, String mappingName, String fkName) throws CoreException
    {
        ManyToManyMapping mtm = new ManyToManyMapping(linkTable);

        PersistentClass oneSidePc = (PersistentClass)oneSide.getPersistentClass();
        PersistentClass otherSidePc = (PersistentClass)otherSide.getPersistentClass();
       
        mtm.setAssociatedClass(otherSidePc);
        mtm.setClassMapping(oneSide.getIdentifier());
        
        String propertyName = StringUtils.decapitalize(otherSidePc.getShortName())+"Many";
        
        IPersistentField pf = internalGetOrCreateReversedField(oneSide,propertyName,hamConfig.getUnindexedCollectionName());
        PropertyMapping pm = helper.createAndBindPropertyMapping(pf);

        mtm.setTable(linkTable);
        mtm.setTypeName(pf.getType());
        mtm.setName(mappingName);
        mtm.setForeignKeyName(fkName);

        if (otherSide.getIdentifier() != null)
        {
            Iterator itr = otherSide.getIdentifier().getColumnIterator();
            if (itr != null)
            {
                while (itr.hasNext())
                {
                    IDatabaseColumn column = (IDatabaseColumn)itr.next();
                    column.setPersistentValueMapping(mtm);
                    mtm.addColumn(column);
                }
            }
        }
        
        CollectionMapping cm = new BagMapping(oneSide);
        cm.setKey(oneSide.getIdentifier());
        cm.setCollectionTable(linkTable);
        
        cm.setElement(mtm);
        cm.setCollectionElementClassName(otherSidePc.getName());
        
        cm.setFieldMapping(pm);
        pm.setValue(cm);
        
        return cm;
    }
*/    
/*    private ManyToOneMapping buildReversedReferencedMapping(IPersistentClass clazz, IDatabaseTableForeignKey fKey) throws CoreException
    {
        IDatabaseTable table = fKey.getTable();
        Iterator colsIterator = fKey.getColumnIterator();
        
        ManyToOneMapping mto = null;
        
        if (colsIterator != null)
        {
            IDatabaseTable refTable = fKey.getReferencedTable();
            if (refTable != null)
            {
                IPersistentClassMapping[] mappings = refTable.getPersistentClassMappings();
                if (mappings != null && mappings.length > 0)
                {
                    ClassMapping referencedClassMapping = (ClassMapping)mappings[0];
                    PersistentClass referencedClass = (PersistentClass)referencedClassMapping.getPersistentClass();
                    
                    mto = new ManyToOneMapping(table);
                    
                    String candidateName = null;
                    
                    while (colsIterator.hasNext())
                    {
                        Column column = (Column)colsIterator.next();
                        String columnName = column.getName();
                        
                        if (column.getPersistentValueMapping() != null)
                        {
                            continue;
                        }
                        column.setPersistentValueMapping(mto);
                        mto.addColumn(column);
                        
                        
                        //TODO uncomment
                        IDatabaseColumn pkColumn = null;//getFirstConstraintColumn(refTable.getPrimaryKey());
                        if (pkColumn != null && columnName.endsWith(pkColumn.getName()))
                        {
                            candidateName = columnToPropertyName(columnName.substring(0,columnName.length() - pkColumn.getName().length()));
                        }
                        if (candidateName == null || candidateName.equals(""))
                        {
                            candidateName = columnToPropertyName(columnName);
                        }
                    }
                    mto.setForeignKeyName(fKey.getName());
                    mto.setReferencedEntityName(referencedClass.getName());

                    if (fKey.getColumnSpan() != 1 || candidateName == null)
                    {
                        candidateName = referencedClass.getShortName()+"Id";
                    }
                    
                    if (mto.getColumnSpan() == 0)
                        return null;

                    candidateName = StringUtils.beanDecapitalize(candidateName);
                    
                    IPersistentField field;

                    field = pfProvider.getOrCreatePersistentField(clazz,candidateName,referencedClass.getName(),hamConfig.getSettings().canUseHeuristicAlgorithms);
                    ((PersistentField)field).setOwnerClass(clazz);
                    helper.createPropertyMapping(field);

                    HibernateAutoMappingHelper.bindValueMappingToField(field,mto);
                }
            }
        }
        return mto;
    }

    private void buildReversedSimpleColumnsMapping(IPropertyMappingHolder mapping, IPersistentClass clazz, Collection columns) throws CoreException
    {
        Iterator itr = columns.iterator();
        Map columnsMap = new HashMap();

        final PropertyMapping USED_FIELD = new PropertyMapping();
        
        pfProvider.setSearchOnly(hamConfig.getSettings().canUseHeuristicAlgorithms);
        while (itr.hasNext())
        {
            IDatabaseColumn column = (IDatabaseColumn)itr.next();
            SimpleValueMapping svm = HibernateAutoMappingHelper.createAndBindSimpleValueMapping((Column)column);

            IPersistentField pf = null;

            if (svm.getType() != null)
            {
                pf = pfProvider.getOrCreatePersistentField(clazz,columnToPropertyName(column.getName()),svm.getType(),false);
                if (pf != null)
                    pf.setMapping(USED_FIELD);
                
                columnsMap.put(column,pf);
            }
        }
        pfProvider.setSearchOnly(false);
        
        
        itr = columns.iterator();
        if (hamConfig.getSettings().canUseHeuristicAlgorithms)
            while (itr.hasNext())
            {
                IDatabaseColumn column = (IDatabaseColumn)itr.next();
                if (column.getPersistentValueMapping() instanceof SimpleValueMapping)
                if (columnsMap.containsKey(column))
                {
                    if (columnsMap.get(column) == null)
                    {
                        IPersistentField pf = pfProvider.getOrCreatePersistentField(clazz,columnToPropertyName(column.getName()),
                                ((SimpleValueMapping)column.getPersistentValueMapping()).getType(),true);
                        if (pf != null)
                        {
                            pf.setMapping(USED_FIELD);
                            columnsMap.put(column,pf);
                        }
                    }
                }
            }
        
        
        itr = columns.iterator();
        while (itr.hasNext())
        {
            IDatabaseColumn column = (IDatabaseColumn)itr.next();
            if (column.getPersistentValueMapping() == null)
                continue ;
            
            IPersistentField pf = (IPersistentField) columnsMap.get(column);
            if (pf == null)
                continue;
            
            pf.setOwnerClass(clazz);
            
            if (pf.getMapping() == USED_FIELD)
                pf.setMapping(null);
            
            PropertyMapping pm = helper.createAndBindPropertyMapping(pf);

            mapping.addProperty(pm);

            HibernateAutoMappingHelper.bindValueMappingToField(pf,column.getPersistentValueMapping());
        }
    }
    
*/
}