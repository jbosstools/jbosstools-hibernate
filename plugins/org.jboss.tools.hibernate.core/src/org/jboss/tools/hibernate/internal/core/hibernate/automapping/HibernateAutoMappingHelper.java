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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;
import org.jboss.tools.hibernate.core.IAutoMappingService;
import org.jboss.tools.hibernate.core.ICodeRendererService;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseSchema;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IDatabaseTableForeignKey;
import org.jboss.tools.hibernate.core.IDatabaseTablePrimaryKey;
import org.jboss.tools.hibernate.core.IOrmElement;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.PropertyInfoStructure;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToManyMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMappingHolder;
import org.jboss.tools.hibernate.core.hibernate.IToOneMapping;
import org.jboss.tools.hibernate.internal.core.AbstractMapping;
import org.jboss.tools.hibernate.internal.core.CodeRendererServiceWrapper;
import org.jboss.tools.hibernate.internal.core.PersistentField;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.data.ForeignKey;
import org.jboss.tools.hibernate.internal.core.data.PrimaryKey;
import org.jboss.tools.hibernate.internal.core.data.Table;
import org.jboss.tools.hibernate.internal.core.hibernate.ClassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ComponentMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.DefaultPropertyMappingFactory;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateConfiguration;
import org.jboss.tools.hibernate.internal.core.hibernate.ManyToManyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ManyToOneMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.OneToManyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.OneToOneMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.PropertyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ToOneMapping;
import org.jboss.tools.hibernate.internal.core.util.ClassUtils;
import org.jboss.tools.hibernate.internal.core.util.TypeUtils;


/**
 * @author Nick
 *
 */
public class HibernateAutoMappingHelper {

    private AbstractMapping mapping;
    private HibernateConfiguration config;
    private DefaultPropertyMappingFactory pmFactory;
	private ConfigurationReader hamConfig;

    
    public void createEquals_HashCode(ICodeRendererService svc, IPersistentClass newPC) throws CoreException{
        if ((newPC!=null)&&(newPC.getPersistentClassMapping()!=null))
        {
            IHibernateClassMapping pcmapping = (IHibernateClassMapping) newPC.getPersistentClassMapping();
            String[] compproperties={};
            Iterator iterator;
            if (pcmapping.getIdentifier() instanceof ComponentMapping) { //compositeID
//                String idclassName =
                	pcmapping.getIdentifier().getFieldMapping().getName();
                ComponentMapping cm = (ComponentMapping)pcmapping.getIdentifier();
                IPersistentClass idclass =cm.getComponentClass();
                if (idclass!=null)
                {
                    //ICompilationUnit idunit = (ICompilationUnit)idclass.getSourceCode();

//                  if (idunit == null || !idunit.exists())
//                  {
//                      String simpleName = Signature.getSimpleName(idclassName);
//                      String[] names = simpleName.split("\\$");
//
//                      renderer.createCompilationUnit(names[0],ClassUtils.getPackageFragment(idunit));
//                      idunit.makeConsistent(null);
//                  }
                    IType idType = idclass.getType();
                    if (idType!=null)
                    {
//                      String[] singequals={"Object"};
//                      String[] singhash={};
//                      if (idType.getMethod("equals",singequals).exists())
//                      {
//                          idType.getMethod("equals",singequals).delete(true,monitor);
//                      }
//                      if (idType.getMethod("hashCode",singhash).exists())
//                      {
//                          idType.getMethod("hashCode",singhash).delete(true,monitor);
//                      }

                        compproperties=new String[cm.getPropertySpan()];
                        iterator = cm.getPropertyIterator();
                        int i=0;
                        while(iterator.hasNext())
                        {
                            compproperties[i++]=
                                //pcmapping.getIdentifier().getFieldMapping().getName()+"."+
                                ((PropertyMapping)iterator.next()).getName();   
                        }

                        svc.createEquals(idType,compproperties);                                    
                        svc.createHashCode(idType,compproperties);                                   
                        svc.createToString(idType,compproperties);                                   
                    }
                }
                compproperties=new String[1];
                compproperties[0]=pcmapping.getIdentifier().getFieldMapping().getName();
                
            }
            else
            {
                // #added# by Konstantin Mishin on 30.11.2005 fixed for ESORM-401
                //if ((pcmapping.getIdentifier()!=null)&&(((SimpleValueMapping)pcmapping.getIdentifier()).getIdentifierGeneratorStrategy().equals("assigned")))
                if (hamConfig.isIdGeneratorQuality() || ((pcmapping.getIdentifier()!=null)&&
                		(((SimpleValueMapping)pcmapping.getIdentifier()).getIdentifierGeneratorStrategy().equals("assigned")))) //$NON-NLS-1$
                // #added#
                {
                    compproperties=new String[1];
                    compproperties[0]=((SimpleValueMapping)pcmapping.getIdentifier()).getFieldMapping().getPersistentField().getName();
                } else {
                    PropertyMapping idpm = (PropertyMapping) pcmapping.getIdentifierProperty();
                    ArrayList<String> prop = new ArrayList<String>();
                    iterator = pcmapping.getPropertyIterator();
                    PropertyMapping pm;
                    while(iterator.hasNext()) {
                        pm = (PropertyMapping)iterator.next();
                        
                        if ((pm.getValue()!=null)&&(idpm!=pm)&&(pm.getValue().isSimpleValue())) {
//                          Column col=(Column)pm.getValue().getColumnIterator().next();
//                          if (col.getOwnerTable().getIndexName(col.getName())!=null)
//                          {
//                              prop.add(pm.getName());
//                          }
//                          else
//                          {
                                if (pm.getUnique())
                                    prop.add(pm.getName());
//                          }
                        }
                    }
                    if (prop.size()>0)
                    {
                        compproperties=new String[prop.size()];
                        for(int i=0;i<prop.size();i++)
                            compproperties[i]=(String) prop.get(i);
                    }
                }
            }
            svc.createEquals(newPC.getType(),compproperties);                                   
            svc.createHashCode(newPC.getType(),compproperties);                                   
            svc.createToString(newPC.getType(),compproperties);                                   
            
            List<PropertyInfoStructure> fullProps = new ArrayList<PropertyInfoStructure>();
            List<PropertyInfoStructure> shortProps = new ArrayList<PropertyInfoStructure>();
            Iterator mappings = pcmapping.getFieldMappingIterator();
            
            while (mappings.hasNext())
            {
                IPersistentFieldMapping pfm = (IPersistentFieldMapping) mappings.next();
                
                if (pfm.getPersistentField() == null)
                    continue;
                
                IPersistentField pf = pfm.getPersistentField();
                
		if (pf.getType() == null)
		    continue;

                if (pfm instanceof PropertyMapping) {
                    PropertyMapping pm = (PropertyMapping) pfm;
                    
                    if (pm.getNotNull())
                        shortProps.add(new PropertyInfoStructure(pf.getName(),pf.getType()));
                }
                
                fullProps.add(new PropertyInfoStructure(pf.getName(),pf.getType()));
            }
            
            
            //screw away legacy auto-generated ctors
            IType workType = CodeRendererServiceWrapper.getWorkingCopy(newPC.getType());
            if (workType != null)
            {
                IMethod[] methods = workType.getMethods();
                
                if (methods != null)
                {
                    for (int i = 0; i < methods.length; i++) {
                        IMethod method = methods[i];
                        
                        if (method.getElementName().equals(workType.getElementName()) && ClassUtils.isESGenerated(method))
                        {
                            method.delete(true, null);
                        }
                    }
                }
                CodeRendererServiceWrapper.commitChanges(workType.getCompilationUnit());
                CodeRendererServiceWrapper.saveChanges(workType.getCompilationUnit());
            } else  {
            	OrmCore.getPluginLog().logInfo("Working copy type does not exist!"); //$NON-NLS-1$
            }
            //screwed
            
            if (!fullProps.isEmpty())
                svc.createConstructor(newPC.getType(),(PropertyInfoStructure[]) fullProps.toArray(new PropertyInfoStructure[0]) );
            if (!shortProps.isEmpty() && fullProps.size() != shortProps.size())
                svc.createConstructor(newPC.getType(),(PropertyInfoStructure[]) shortProps.toArray(new PropertyInfoStructure[0]) );
            svc.createConstructor(newPC.getType());
        }
    }
    

    public void generateReversingReport(int tablesNumber, int foundCUNumber, int skippedTablesNumber, 
            int linkTablesNumber) throws CoreException
    {
        IProject project = mapping.getProject().getProject();
        IFile rsrc = project.getFile(IAutoMappingService.REVERSING_REPORT_FILENAME);
        if (rsrc == null || rsrc.getType() == IResource.FILE)
        {
        	// edit tau 18.01.2006
			// edit tau 10.02.2006 for ESORM-513 Overwrites and changes our Hibernate mapping files, even if they are marked read-only?        	
            if(!rsrc.exists()) rsrc.create(null, IResource.NONE, null);
        	//if(!rsrc.exists()) rsrc.create(null, IResource.FORCE, null);
        	
            String output=Messages.HibernateAutoMappingHelper_ReportString1+
            Messages.HibernateAutoMappingHelper_ReportString2+
            Messages.HibernateAutoMappingHelper_ReportString3+(new Integer(tablesNumber-skippedTablesNumber-linkTablesNumber)).toString()+Messages.HibernateAutoMappingHelper_ReportString4+
            Messages.HibernateAutoMappingHelper_ReportString5+
            Messages.HibernateAutoMappingHelper_ReportString6+(new Integer(skippedTablesNumber)).toString()+Messages.HibernateAutoMappingHelper_ReportString7+
            Messages.HibernateAutoMappingHelper_ReportString8+(new Integer(linkTablesNumber)).toString()+Messages.HibernateAutoMappingHelper_ReportString9+
            		Messages.HibernateAutoMappingHelper_ReportString10;
            InputStream input = new ByteArrayInputStream(output.getBytes());
            // edit tau 18.01.2006
			// edit tau 10.02.2006 for ESORM-513 Overwrites and changes our Hibernate mapping files, even if they are marked read-only?            
            //rsrc.setContents(input, IResource.FILE, null);
            //rsrc.setContents(input, IResource.FORCE, null);
            rsrc.setContents(input, IResource.NONE, null);            
        }
    }

    public static void removeMapping(IPersistentFieldMapping pfm)
    {
        if (pfm == null)
            return ;
        
        if (pfm instanceof IPropertyMapping) {
            IPropertyMapping pm = (IPropertyMapping) pfm;
            
            if (pm.getPropertyMappingHolder() != null)
                pm.getPropertyMappingHolder().removeProperty(pm);
        }
        if (pfm.getPersistentField() != null)
        {
            pfm.getPersistentField().setMapping(null);
        }
    }
    
    public static boolean tableHasMappings(IDatabaseTable table)
    {
        return (table.getPersistentClassMappings() != null && table.getPersistentClassMappings().length > 0);
    }
    
    private static IDatabaseTable internalGetTableInMappingSchema(IPersistentClassMapping mapping, String shortTableName, AbstractMapping hm, boolean getOrCreate)
    {
        String schemaString = null;
        if (mapping != null)
        {
            IDatabaseTable table = mapping.getDatabaseTable();
            if (table != null)
            {
                IDatabaseSchema schema = table.getSchema();
                if (schema != null)
                {
                    schemaString = schema.getName();
                }
            }
        }
        String fQTableName = Table.toFullyQualifiedName(null,schemaString,shortTableName);

        if (getOrCreate)
            return hm.getOrCreateTable(fQTableName);
        else
            return hm.findTable(fQTableName);
    }
    
    public static IDatabaseTable getTableInMappingSchema(IPersistentClassMapping mapping, String shortTableName, AbstractMapping hm)
    {
        return internalGetTableInMappingSchema(mapping,shortTableName,hm,false);
    }
    
    public static IDatabaseTable getOrCreateTableInMappingSchema(IPersistentClassMapping mapping, String shortTableName, AbstractMapping hm)
    {
        return internalGetTableInMappingSchema(mapping,shortTableName,hm,true);
    }
    
    //akuzmin 13.04.2005
    public HibernateAutoMappingHelper(ConfigurationReader hamConfig)
    {
        this.mapping = hamConfig.getMapping();
		this.config = (HibernateConfiguration)this.mapping.getConfiguration();
        pmFactory = new DefaultPropertyMappingFactory(this.config.getOrmProject().getOrmConfiguration());
		this.hamConfig = hamConfig;
   }

    public ConfigurationReader getConfiguration()
    {
        return hamConfig;
    }
    
    static Column createAndBindColumn(IPersistentValueMapping valueMapping,
            String name, int typeCode, IDatabaseTable ownerTable) {
        
        Column column;
        if (ownerTable.getColumn(name) != null)
        {
            column = (Column)ownerTable.getColumn(name);
            column.setPersistentValueMapping(valueMapping);
        }
        else
        {
            column = new Column();
            column.setName(name);
            column.setOwnerTable(ownerTable);
            column.setSqlTypeCode(typeCode);
            ownerTable.addColumn(column);
        }
        return column;
    }

//	akuzmin 13.04.2005
	public static Column createAndBindColumn(SimpleValueMapping valueMapping, String name,
            IDatabaseTable ownerTable) {

        Column column;
        if (ownerTable.getColumn(name) != null)
        {
            column = (Column)ownerTable.getColumn(name);
        }
        column = createAndBindColumn(valueMapping, name,
                valueMapping.getType().getSqlType(), ownerTable);
        
        valueMapping.addColumn(column);
        valueMapping.setTable(ownerTable);
        return column;
    }

    SimpleValueMapping createAndBindSimpleValueMapping(IDatabaseColumn column) {
        SimpleValueMapping svm = new SimpleValueMapping();
        svm.setTable((Table) column.getOwnerTable());
        svm.setName(column.getName());
        svm.setType(TypeUtils.columnTypeToHibTypeDefault(column,hamConfig));
        svm.addColumn(column);
        column.setPersistentValueMapping(svm);
        return svm;
    }

    static IDatabaseTablePrimaryKey createOrBindPK(IDatabaseColumn column) {
        if (column == null || column.getOwnerTable() == null)     
            return null;

        IDatabaseTable table = column.getOwnerTable();
        
        PrimaryKey pk = (PrimaryKey) table.getPrimaryKey();
        
        if (pk == null)
            pk = new PrimaryKey();
        
        String keyName = "PK_"; //$NON-NLS-1$

        if (!column.isPrimaryKey())
        {
            pk.addColumn(column);
        }
        
        Iterator itr = pk.getColumnIterator();
        while (itr.hasNext())
        {
            Column col = (Column)itr.next();
            if (keyName.length() != 0)
                keyName += "_"; //$NON-NLS-1$
            keyName += col.getName();
        }
        
        pk.setName(keyName);
        
        if (pk.getTable() == null)
            pk.setTable(table);
        if (table.getPrimaryKey() == null)
            table.setPrimaryKey(pk);

        return pk;
    }

    public static IDatabaseTable getPrivateTable(IPersistentClassMapping mapping)
    {
        return mapping.getDatabaseTable();
    }
    
    static void setPrivateTable(IPersistentClassMapping mapping, IDatabaseTable table)
    {
       	mapping.setDatabaseTable(table);
    }

//  akuzmin 23.05.05    
    public static SimpleValueMapping createAndBindKey(IHibernateKeyMapping pKey, IDatabaseTable toTable) throws CoreException
    {
        if (pKey == null || toTable == null)
            return null;

        SimpleValueMapping result = new SimpleValueMapping(toTable);
        result.setUpdateable(true);
        result.setType(pKey.getType());
        return result;
    }
    
    public static SimpleValueMapping bindKey(IDatabaseTableForeignKey fk, IHibernateKeyMapping pKey, IDatabaseTable toTable) throws CoreException
    {
        if (pKey == null || toTable == null || fk == null)
            return null;
        
        SimpleValueMapping result = createAndBindKey(pKey,toTable);

        result.setForeignKeyName(fk.getName());
        return result;
    }
    
    public static SimpleValueMapping createAndBindFK(IHibernateKeyMapping pKey, IDatabaseTable toTable, String prefixName, boolean isNullable) throws CoreException
    {
        if (pKey == null || toTable == null)
            return null;
        
        SimpleValueMapping result = createAndBindKey(pKey,toTable);
        
        String fkMappingName;
        if (prefixName != null)
            fkMappingName = HibernateAutoMapping.propertyToTableName(prefixName,pKey.getName());
        else
            fkMappingName = pKey.getName();
        
        ForeignKey fk = new ForeignKey();
        fk.setTable(toTable);
        fk.setReferencedTable(pKey.getTable());
        ((Table)toTable).addForeignKey(pKey.getName(),fk);
        fk.setReferencedEntityName(pKey.getName());
        fk.setName(fkMappingName);
        
        Iterator compositeKeyColumns = pKey.getColumnIterator();
        while (compositeKeyColumns.hasNext())
        {
            Column column = (Column)compositeKeyColumns.next();
            //or'ing with prototype column to provide support for partially nullable keys
            String columnName;
            if (prefixName != null)
                columnName = HibernateAutoMapping.propertyToTableName(prefixName,column.getName());
            else
                columnName = column.getName();
            Column newColumn = createAndBindColumn(result,columnName,column.getSqlTypeCode(),toTable);
            newColumn.setNullable(isNullable || column.isNullable());
            result.addColumn(newColumn);
            fk.addColumn(newColumn);
            newColumn.setPersistentValueMapping(result);
            toTable.addColumn(newColumn);
        }

        result.setForeignKeyName(fk.getName());
        return result;
    }

    //constants of getLinkedType() method
    final static int LT_NONE = 0;
    final static int LT_ARRAY = 1;
    final static int LT_COLLECTION = 2;
    
    
    /**
     * returns collection type according to collection's name
     * @param fQTypeName
     * @return
     */
//akuzmin 06.05.2005	
    public static int getLinkedType(String fQTypeName)
    {

		if (fQTypeName == null || fQTypeName.equals(MappingsFactory.BLOB_SIGNATURE))
			return LT_NONE;
		
		// add tau 28.01.2006 / fix ESORM-499 for [][]...
		if (Signature.getArrayCount(fQTypeName) != 0 && ClassUtils.isPrimitiveType(Signature.getElementType(fQTypeName))){
			return LT_NONE;			
		}
		// end add
		
        if (Signature.getArrayCount(fQTypeName) != 0)
        {
			return LT_ARRAY;
        }
        
        if (TypeUtils.COLLECTIONS.contains(fQTypeName))
            return LT_COLLECTION;
        return LT_NONE;        
    }

    public static void setPropertyMappingAccessor(PropertyMapping pm)
    {
        if (pm.getPersistentField() == null && !(pm instanceof PropertyMapping))
            return ;

        IPersistentField field = pm.getPersistentField();
        String accessorName = null;
        if ( (field.getAccessorMask() & PersistentField.ACCESSOR_PROPERTY) != 0)
        {
            accessorName = "property"; //$NON-NLS-1$
        }
        else
        {
            accessorName = "field"; //$NON-NLS-1$
        }
        pm.setPropertyAccessorName(accessorName);

    }
    
    public PropertyMapping createPropertyMapping(IPersistentField field)
    {
        if (field == null)
            return null;
        
        if (field.getMapping() != null)
            return (PropertyMapping) field.getMapping();
        
        //XXX set default properties (accessor etc.)
        PropertyMapping propertyMapping = pmFactory.createDefaultPropertyMapping();
        
        ((PropertyMapping)propertyMapping).setName(field.getName());
        ((PropertyMapping)propertyMapping).setPersistentField(field);
        
        field.setMapping(propertyMapping);

        if (field.getOwnerClass() != null)
        {    ClassMapping mapping = (ClassMapping)field.getOwnerClass().getPersistentClassMapping();
            ((PropertyMapping)propertyMapping).setPersistentClassMapping(mapping);
        }
        
        setPropertyMappingAccessor(propertyMapping);
        
        return propertyMapping;
        
    }
    //akuzmin 17.04.2005    
    public PropertyMapping createAndBindPropertyMapping(IPersistentField field)
    {
    	PropertyMapping pm = createPropertyMapping(field);
    	if (field.getOwnerClass() != null)
        {
    	    ClassMapping mapping = (ClassMapping)field.getOwnerClass().getPersistentClassMapping();
    	    if (pm != null && mapping != null && field.getOwnerClass() == field.getMasterClass())
    	        mapping.addProperty(pm);
        }
        return pm;
    }

    public PropertyMapping createAndBindPropertyMapping(IPersistentField field, 
			IOrmElement parentElement)
	{
		PropertyMapping pm = createPropertyMapping(field);
		if(parentElement instanceof PersistentField)
		{
			if( ((PersistentField)parentElement).getMapping().getPersistentValueMapping() instanceof ComponentMapping)
			{
				((IPropertyMappingHolder)((PersistentField)parentElement).getMapping().getPersistentValueMapping()).addProperty(pm);
			}
		}
		// added by Nick 10.06.2005
        if (parentElement instanceof IPersistentClass)
        {
            IPersistentClass clazz = (IPersistentClass)parentElement;
            if (clazz.getPersistentClassMapping() != null)
            {
                IPropertyMappingHolder holder = (IPropertyMappingHolder)clazz.getPersistentClassMapping();
                holder.addProperty(pm);
            }
            return pm;
        }
		// by Nick
        if (parentElement instanceof IPropertyMappingHolder)
		{
			((IPropertyMappingHolder)parentElement).addProperty(pm);
			return pm;
		}
		return null;
}
    
    
	void createDiscriminatorValue(ClassMapping mapping)
	{
		if (mapping.getDiscriminatorValue() == null)
			mapping.setDiscriminatorValue(mapping.getPersistentClass().getName());
	}

    public static void bindValueMappingToField(IPersistentField field, IPersistentValueMapping valueMapping)
    {
        if (valueMapping == MappingsFactory.DEFERRED_VALUE)
            return ;
        
        if (field != null)
        {
            if (field.getMapping() != null)
                field.getMapping().setPersistentValueMapping(valueMapping);
        }
        if (valueMapping != null)
        {
            if (field != null)
                valueMapping.setFieldMapping(field.getMapping());
            else
                valueMapping.setFieldMapping(null);
        }
    }
    
    //checks if collection is of indexed type 
    //in - field fully qualified type string, e.g. "[java.lang.Byte" or "java.util.Map"
    public static boolean isIndexedCollection(String fQTypeName)
    {
        if (Signature.getArrayCount(fQTypeName) != 0 && !MappingsFactory.BLOB_SIGNATURE.equals(fQTypeName))
            return true;
        if (TypeUtils.INDEXED_COLLECTIONS.contains(fQTypeName))
            return true;
        
        return false;        
    }
    
    private void setDefaultPersistentLinkageProperties(IPersistentFieldMapping mapping, IToOneMapping mto)
    {
        if (mapping != null)
        {
            PropertyMapping pm = (PropertyMapping)mapping;
            pm.setCascade(hamConfig.getCascade());
            if (hamConfig.getLazy() != null)
            {
                pm.setLazy(hamConfig.getLazy().booleanValue());

                if (mto instanceof OneToOneMapping) {
                    OneToOneMapping oto = (OneToOneMapping) mto;
                    oto.setProxied(false);
                }
                else if (mto instanceof ManyToOneMapping && !(mto instanceof ManyToManyMapping)) {
                    ManyToOneMapping mto_casted = (ManyToOneMapping) mto;
                    mto_casted.setProxied(false);
                }

            }
        }
    }
    
    public ToOneMapping createManyToOneMapping(IPersistentField field, IPersistentClass toClass) throws CoreException
    {
        Table table = (Table)field.getOwnerClass().getPersistentClassMapping().getDatabaseTable();
        
        ToOneMapping mto = null;
        
        if (toClass != null)
        {
            IHibernateKeyMapping pkey = ((ClassMapping)toClass.getPersistentClassMapping()).getIdentifier();
            String prefixName = HibernateAutoMapping.propertyToTableName(toClass.getShortName(),field.getName());

            if (table != null && prefixName != null)
            {
// added by yk Jun 22, 2005
            	String fkMappingName = null;
            	String stub			 = ""; //$NON-NLS-1$
            	fkMappingName 		 = HibernateAutoMapping.propertyToTableName(prefixName,((pkey != null) ? pkey.getName() : stub) ); 
// added by yk Jun 22, 2005 stop
                /* rem by yk Jun 22, 2005fkMappingName = HibernateAutoMapping.propertyToTableName(prefixName,pkey.getName()); */
            	
                mto = new ManyToOneMapping(table);
                mto.setReferencedEntityName(toClass.getName());
                
// added by yk Jun 22, 2005
                if(pkey != null)
                { mto.setType(pkey.getType()); }
// added by yk Jun 22, 2005 stop
                /* rem by yk Jun 22, 2005 mto.setType(pkey.getType()); */
                
                mto.setName(fkMappingName);
            }
        }
        
        setDefaultPersistentLinkageProperties(field.getMapping(),mto);
        return mto;
    }
    
    public static OneToManyMapping createOneToManyMapping(IHibernateClassMapping owner, IHibernateClassMapping elementsClass)
    {
        OneToManyMapping otm = new OneToManyMapping(owner);
        otm.setAssociatedClass(elementsClass);
        otm.setReferencedEntityName(elementsClass.getName());
        return otm;
    }
    
    public static IManyToManyMapping createManyToMapping(IHibernateClassMapping owner, IPersistentClass associatedClass, String referencedEntity, IDatabaseTable linkTable)
    {
        IManyToManyMapping mtm;
        //if (mapping.isPolymorphic() && !mapping.isExplicitPolymorphism())
        //    mtm = new ManyToAnyMapping();
        //else
            mtm = new ManyToManyMapping(linkTable);

            //mtm.setAssociatedClass(owner.getPersistentClass());
            //mtm.setClassMapping(associatedClass.getPersistentClassMapping().getIdentifier());
            mtm.setReferencedEntityName(referencedEntity);
            //mtm.setAssociatedClass(associatedClass);
            
            return mtm;
    }
    
    public OneToOneMapping createOneToOneMapping(IPersistentField field, IPersistentClass toClass/*IDatabaseTable table, IHibernateKeyMapping otherClassKeyMapping*/)
    {
        Table table = (Table)field.getOwnerClass().getPersistentClassMapping().getDatabaseTable();
        OneToOneMapping oto = null;
        
        if (table != null)
        {
            oto = new OneToOneMapping(table,((ClassMapping)toClass.getPersistentClassMapping()).getIdentifier());
            oto.setReferencedEntityName(toClass.getName());
        }
        
        setDefaultPersistentLinkageProperties(field.getMapping(),oto);
        return oto;
    }
    
}

