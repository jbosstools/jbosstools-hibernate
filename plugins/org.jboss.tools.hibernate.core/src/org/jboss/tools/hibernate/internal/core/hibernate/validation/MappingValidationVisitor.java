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
package org.jboss.tools.hibernate.internal.core.hibernate.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IDatabaseTableForeignKey;
import org.jboss.tools.hibernate.core.IDatabaseTablePrimaryKey;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.core.hibernate.ICollectionMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateValueMapping;
import org.jboss.tools.hibernate.core.hibernate.IJoinedSubclassMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToOneMapping;
import org.jboss.tools.hibernate.core.hibernate.IOneToManyMapping;
import org.jboss.tools.hibernate.core.hibernate.IOneToOneMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMappingHolder;
import org.jboss.tools.hibernate.core.hibernate.IRootClassMapping;
import org.jboss.tools.hibernate.core.hibernate.ISimpleValueMapping;
import org.jboss.tools.hibernate.core.hibernate.ISubclassMapping;
import org.jboss.tools.hibernate.core.hibernate.IToOneMapping;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.BaseMappingVisitor;
import org.jboss.tools.hibernate.internal.core.hibernate.CollectionMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ComponentMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ManyToManyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ManyToOneMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.OneToManyMapping;
import org.jboss.tools.hibernate.internal.core.util.ClassUtils;
import org.jboss.tools.hibernate.internal.core.util.ScanProject;
import org.jboss.tools.hibernate.internal.core.util.TypeUtils;



public class MappingValidationVisitor extends BaseMappingVisitor {

	//class mappings
	private final int NO_DISCRIMINATOR_VALUE = 1;	
	private final int NO_DISCRIMINATOR_MAPPING = 2;	
	private final int NO_ID = 3;	
	private final int NO_KEY = 4;
	
	//field mappings
	private final int NO_REFERENCED_ENTITY = 5;
	private final int BAD_COLUMN_NUMBER = 6;
    private final int BAD_COLUMN_TYPE = 7;
    private final int BAD_HIBERNATE_TYPE = 8;
    private final int NO_COLUMNS = 9;
    
    //associations
    private final int INCORRECT_INVERSE_NOT_BIDIR = 10;
    private final int INCORRECT_INVERSE_NOT_INVERSED = 11;
    private final int NO_REFERENCED_PROPERTY = 12;
    private final int REF_PROPERTY_NOT_REFERENCING_US = 13;
    private final int CONSTRAINED_TO_OTO_BAD_STRATEGY = 14;
    
    //collections
    private final int INDEXED_INVERSED_COLLECTION = 15;
    
    //foreign keys
    private final int NO_REFERENCED_TABLE = 16;
    private final int REFERENCED_TABLE_NO_PK = 17;
    private final int PK_COLUMN_INCOMPATIBLE = 18;
    private final int PK_FK_SIZE_CONFLICT = 19;
    
    // #added# by Konstantin Mishin on 30.09.2005 fixed for ESORM-78    
    //  collections
    private final int NOT_IMPLEMENT_COMPARABLE_WARNING = 20;
    // #added# 

    MappingValidation validator;
	IMapping projectMapping;
	IHibernateClassMapping currentClassMapping;
	private String validationPath = null;
    
	public MappingValidationVisitor(MappingValidation validator, IMapping projectMapping){
		this.validator=validator;
		this.projectMapping=projectMapping;
	}
	
	protected void createMarker(IPersistentClassMapping mapping, int id){
		createMarker(mapping,mapping.getName(), null, id);
	}
    // #added# by Konstantin Mishin on 26.08.2005 fixed for ORMIISTUD-659
	protected void createMarker(IPersistentClassMapping mapping, String entity, String field, int id){
		createMarker(mapping, entity, field, id, false);
	}
    // #added#

    // #changed# by Konstantin Mishin on 26.08.2005 fixed for ORMIISTUD-659
	//protected void createMarker(IPersistentClassMapping mapping, String entity, String field, int id){
	protected void createMarker(IPersistentClassMapping mapping, String entity, String field, int id, boolean isWarning){
    // #changed#
        String errorMessage = null;
		try{
//			IResource res = mapping.getPersistentClass().getSourceCode()
//                    .getUnderlyingResource();
            if (field == null)
                errorMessage = getErrorMessage(id, mapping.getName());
            else
                errorMessage = getErrorMessage(id, entity, field);
            
            if (mapping.getStorage() != null)
            {
                IResource mappingStorage = mapping.getStorage().getResource();
                
                validator.createMarker(
                        mappingStorage,
                        errorMessage, 
                        // #changed# by Konstantin Mishin on 26.08.2005 fixed for ORMIISTUD-659
                        //IMarker.SEVERITY_ERROR,
                        isWarning?IMarker.SEVERITY_WARNING:IMarker.SEVERITY_ERROR, 
                        // #changed#
                        String.valueOf(id), 
                        mapping.getName(),
                        projectMapping,
                        mapping.getStorage());
            }
// 05.07.2005 - mapping storage resources now have validity markers
//
//            if (res != null && mapping.getPersistentClass().getType() != null)
//            {
//                // validator.createMarker(res,
//                // errorMessage,
//                // IMarker.SEVERITY_ERROR,
//                // String.valueOf(id),
//                // mapping.getClassName(),
//                // projectMapping);
//                validator.createMarker(
//                        mapping.getPersistentClass().getType(),
//                        errorMessage, 
//                        IMarker.SEVERITY_ERROR, 
//                        String.valueOf(id), 
//                        mapping.getClassName(),
//                        projectMapping);
//            }
// - by Nick
        } catch (Exception ex) {
// added by yk 14.09.2005
        	ExceptionHandler.logThrowableWarning(ex,ex.getMessage()); // changed by Nick 22.09.2005
// added by yk 14.09.2005.
/* rem by yk 14.09.2005            throw new NestableRuntimeException(ex);		*/
        	}
	}
	
	public Object visitHibernateClassMapping(IHibernateClassMapping mapping, Object argument) {
		currentClassMapping=mapping;
		return super.visitHibernateClassMapping(mapping, argument);
	}

	public Object visitJoinedSubclassMapping(IJoinedSubclassMapping mapping, Object argument) {
	    // added by Nick 01.09.2005
        currentClassMapping=mapping;
        // by Nick
        
        //check key
		if(mapping.getKey() == null)
            createMarker(mapping, NO_KEY);
        // added by Nick 08.07.2005
        else
            checkKeyMapping(mapping.getKey());
        //by Nick
        return super.visitJoinedSubclassMapping(mapping, argument);
	}

	public Object visitRootClassMapping(IRootClassMapping mapping, Object argument) {
		//mapping.getIdentifier()
		if(mapping.getIdentifier() == null) createMarker(mapping, NO_ID);
		return super.visitRootClassMapping(mapping, argument);
	}

	public Object visitSubclassMapping(ISubclassMapping mapping, Object argument) {
        // added by Nick 01.09.2005
        currentClassMapping=mapping;
        // by Nick

        IHibernateClassMapping root=mapping.getRootClass();
		//mapping.getRootClass().getDiscriminator()
		
// added by yk 23.08.2005
		String dv;
		if(root != null){
// added by yk 23.08.2005.
		if(root.getDiscriminator() == null){
			createMarker(root, NO_DISCRIMINATOR_MAPPING);
		}
		dv=root.getDiscriminatorValue();
		if(root.getDiscriminatorValue() == null){
			createMarker(root, NO_DISCRIMINATOR_VALUE);
		}
		}
		//No Discriminator Value
		dv=mapping.getDiscriminatorValue();
		if(dv==null || dv.length()==0){
			createMarker(mapping, NO_DISCRIMINATOR_VALUE);
		}
		return super.visitSubclassMapping(mapping, argument);
	}


	private void checkReference(IHibernateValueMapping mapping, String entity){
		if(entity!=null){
			if(null==projectMapping.findClass(entity)){
				String field="";
				if(mapping.getFieldMapping()!=null) field=mapping.getFieldMapping().getName();
				else field=mapping.getName();
				createMarker(currentClassMapping,entity, field,NO_REFERENCED_ENTITY);
			}
		}
	}
    
	private void checkPropertyReference(IToOneMapping mapping, IPersistentClass refClass)
	{
	    IPersistentField refField = refClass.getField(mapping.getReferencedPropertyName());
	    
	    IPersistentField field = mapping.getFieldMapping() == null ?
	            null : mapping.getFieldMapping().getPersistentField();
	    
	    if (refField == null || refField.getMapping() == null)
	    {
	        createMarker(currentClassMapping,
	                field.getName(),mapping.getReferencedPropertyName(),NO_REFERENCED_PROPERTY);
	    }

        //TODO can refactor this to bad relational model warning
//	    else
//	    {
//	        IPersistentValueMapping refValue = refField.getMapping().getPersistentValueMapping();
//	        String refSideRefEntityName = null;
//	        if (refValue instanceof IToOneMapping) {
//	            IToOneMapping tom = (IToOneMapping) refValue;
//	            refSideRefEntityName = tom.getReferencedEntityName();
//	        }
//            if (refSideRefEntityName == null ||
//	                !refSideRefEntityName.equals(field.getOwnerClass().getName()))
//	            createMarker(cm,refField.getName(),refField.getOwnerClass().getName(),REF_PROPERTY_NOT_REFERENCING_US);
//	    }
	}
	
	private void checkForeignKey(IDatabaseTableForeignKey fKey)
	{
	    IDatabaseTable refTable = fKey.getReferencedTable();
	    IDatabaseTable table = fKey.getTable();

	    if (table != null)
	    {            
	        if (refTable != null)
	        {
	            IDatabaseTablePrimaryKey pKey = refTable.getPrimaryKey();
	            if (pKey != null && pKey.getColumnSpan() != 0)
	            {
	                boolean invalidColumns = false;
	                Iterator fKColumns = fKey.getOrderedColumnIterator();
	                Iterator pKColumns = pKey.getColumnIterator();
	                
	                while (fKColumns.hasNext() && pKColumns.hasNext() && !invalidColumns)
	                {
	                    IDatabaseColumn fKColumn = (IDatabaseColumn) fKColumns.next();
	                    IDatabaseColumn pKColumn = (IDatabaseColumn) pKColumns.next();
	                    
                        boolean equivColumns = true;
                        
                        if (fKColumn.isNativeType() && pKColumn.isNativeType())
                        {
                            //check native types
                            if (fKColumn.getSqlTypeName() != null)
                            {
                                equivColumns = fKColumn.getSqlTypeName().equals(pKColumn.getSqlTypeName());
                            }
                        }
                        else
                        {
                            equivColumns = fKColumn.getSqlTypeCode() == pKColumn.getSqlTypeCode();
                        }
	                    
//	                    if (equivColumns)
//	                    {
//	                        equivColumns = (pKColumn.getLength() == fKColumn.getLength()) &&
//	                        (pKColumn.getScale() == fKColumn.getScale()) &&
//	                        (pKColumn.getPrecision() == fKColumn.getPrecision());
//	                    }
	                    
	                    if (!equivColumns)
	                    {
	                        invalidColumns = true;
	                        createMarker(currentClassMapping,fKColumn.getName(),refTable.getName(),PK_COLUMN_INCOMPATIBLE);
	                    }
	                }
	                
	                if (fKey.getColumnSpan() != pKey.getColumnSpan())
	                {
	                    createMarker(currentClassMapping,fKey.getName(),refTable.getName(),PK_FK_SIZE_CONFLICT);
	                }
	            }
	            else
	            {
	                createMarker(currentClassMapping,fKey.getName(),refTable.getName(),REFERENCED_TABLE_NO_PK);
	            }
	        }
	        else
	        {
	            createMarker(currentClassMapping,fKey.getName(),"",NO_REFERENCED_TABLE);
	        }
	    }
	}
    
	public Object visitManyToOneMapping(IManyToOneMapping mapping, Object argument) {
		//XXX Check correctness of bidirectional associations: one and only one side should be inverse
		// many-to-one cannot be inversed
        checkKeyMapping(mapping);
        checkReference(mapping, mapping.getReferencedEntityName());
		
        return super.visitManyToOneMapping(mapping, argument);
	}
	
    private ManyToOneMapping iterateFindOneSideAssoc(Iterator mappings, IDatabaseColumn column)
    {
        if (mappings != null)
        {
            while (mappings.hasNext())
            {
                Object object = mappings.next();
                
                IPersistentValueMapping value = null;
                
                if (object instanceof IPersistentFieldMapping) {
                    IPersistentFieldMapping pfm = (IPersistentFieldMapping) object;
                    value = pfm.getPersistentValueMapping();
                }

                if (object instanceof IPersistentValueMapping) {
                    value = (IPersistentValueMapping) object;
                }
                
                if (value instanceof ManyToOneMapping)
                {
                    ManyToOneMapping mto = (ManyToOneMapping) value;
                    if (mto.containsColumn(column))
                        return mto;
                }
                if (value instanceof IPropertyMappingHolder)
                {
                    ManyToOneMapping mto = iterateFindOneSideAssoc(
                        ((IPropertyMappingHolder)value).getPropertyIterator(),    
                        column);
                    if (mto != null)
                        return mto;
                }
                
            }
        }
        
        return null;
    }
    
    private ManyToOneMapping findOneSideAssoc(IPersistentClassMapping cm, IDatabaseColumn column) {
        ManyToOneMapping mto = iterateFindOneSideAssoc(cm.getFieldMappingIterator(),column);
        if (mto == null && cm instanceof IHibernateClassMapping && ((IHibernateClassMapping)cm).getIdentifierProperty() == null) {
            if (cm.getIdentifier() != null) {
                ArrayList<IHibernateKeyMapping> list = new ArrayList<IHibernateKeyMapping>();
                list.add(cm.getIdentifier());
                mto = iterateFindOneSideAssoc(list.iterator(),column);
            }
        }
        return mto;
    }
    
	public Object visitOneToManyMapping(IOneToManyMapping mapping, Object argument) {
	    if (argument instanceof CollectionMapping) {
	        CollectionMapping collectionValue = (CollectionMapping) argument;
	        IPersistentValueMapping collectionKey = collectionValue.getKey();
	        IPersistentValueMapping key = null;
	        
            IDatabaseColumn column = null;
	        if (collectionKey != null)
	        {
	            Iterator itr = collectionKey.getColumnIterator();
	            if (itr.hasNext())
	            {
	                column = (IDatabaseColumn) itr.next();
	                key = column.getPersistentValueMapping();
	            }
	        }

	        if (!(key instanceof ManyToOneMapping))
	        {
	            IPersistentClassMapping cm = mapping.getAssociatedClass();
	            if (cm != null)
                {
	                key = findOneSideAssoc(cm,column);
                }
	        }

            
	        IPersistentField field = collectionValue.getFieldMapping().getPersistentField();
	        if (field != null)
	        {
	            if (key instanceof ManyToOneMapping) {
	                
                    if (key.getFieldMapping() instanceof IPropertyMapping) {
                        IPropertyMapping pm = (IPropertyMapping) key.getFieldMapping();
                        
                        if (pm.isInsertable() || pm.isUpdateable())
                        {
                            //inversed end
                            if (!collectionValue.isInverse() )
                            {
                                // #changed# by Konstantin Mishin on 26.08.2005 fixed for ORMIISTUD-659
                                //createMarker(currentClassMapping,field.getName(),"",INCORRECT_INVERSE_NOT_INVERSED);
                                createMarker(currentClassMapping,field.getName(),"",INCORRECT_INVERSE_NOT_INVERSED, true);
                                // #changed#
                            }
                        }
                    }
	            }
	            else
	            {
	                //key can be null here!
                    if (collectionValue.isInverse())
	                    createMarker(currentClassMapping,field.getName(),"",INCORRECT_INVERSE_NOT_BIDIR);
	            }
	        }
	    }
        //XXX Check correctness of bidirectional associations: one and only one side should be inverse
		checkReference(mapping, mapping.getReferencedEntityName());
		return super.visitOneToManyMapping(mapping, argument);
	}

	public Object visitOneToOneMapping(IOneToOneMapping mapping, Object argument) {
        
        checkReference(mapping, mapping.getReferencedEntityName());

        IPersistentClass refClass = null;
        if (mapping.getReferencedEntityName() != null && 
                (refClass = projectMapping.findClass(mapping.getReferencedEntityName())) != null)
        {
            if (mapping.getReferencedPropertyName() != null)
            {
                //XXX Check correctness of bidirectional associations: one and only one side should be inverse
                //check property-ref attribute for correctness
                checkPropertyReference(mapping,refClass);
            }
            
            //XXX Check when persistent class mapped as one-to-one association 
            // that one side have an id generator and constrained property is set on other side
            if (mapping.isConstrained())
            {
                if (refClass.getPersistentClassMapping() instanceof IHibernateClassMapping) {
                    if (currentClassMapping.getIdentifier() instanceof ISimpleValueMapping) {
                        ISimpleValueMapping simpleId = (ISimpleValueMapping) currentClassMapping.getIdentifier();
                        
                        if (simpleId.getIdentifierGeneratorStrategy() == null
                                || !simpleId.getIdentifierGeneratorStrategy().equals("foreign"))
                        {
                            IPersistentField field = null;
                            if (mapping.getFieldMapping() != null)
                                field = mapping.getFieldMapping().getPersistentField();
                            
                            if (field != null)
                                createMarker(currentClassMapping,field.getName(),refClass.getName(),CONSTRAINED_TO_OTO_BAD_STRATEGY);
                        }
                    }
                }
            }
        }
        return super.visitOneToOneMapping(mapping, argument);
	}
    
    private void checkKeyMapping(IHibernateKeyMapping key)
    {
        if (key == null)
            return ;
            
        IDatabaseTableForeignKey fKey = null;
        
        if (key.getTable() != null)
        {
            fKey = key.getTable().getForeignKey(key.getForeignKeyName());
        }
        
        if (fKey != null)
        {
            //XXX key mapping verification through DB structures visitor
            checkForeignKey(fKey);
        }
    }
    
	public Object visitSimpleValueMapping(ISimpleValueMapping mapping, Object argument) {
		// XXX Check possibility of mapping from a field type to a column type   
	    
        String fieldName = null;
        String fieldType = null;
        IPersistentFieldMapping fieldMapping = mapping.getFieldMapping();
        if (fieldMapping != null)
        {
            IPersistentField field = fieldMapping.getPersistentField();
            if (field != null)
            {
                fieldType = field.getType();
                fieldName = field.getName();
            }
        }
   
        if (mapping.getForeignKeyName() != null)
            checkKeyMapping(mapping);
        else
        if (Type.isHibernateType(mapping.getType()))
        {
            if (mapping.getColumnSpan() != 1)
            {
                if (mapping.getColumnSpan() == 0)
                {
                    if (mapping.getFormula() == null)
                    {
                        createMarker(currentClassMapping,fieldName,"",NO_COLUMNS);
                    }
                }
                else
                {
                    // XXX Only if type== hibernate type
                    createMarker(currentClassMapping,fieldName,"1",BAD_COLUMN_NUMBER);
                }
            } else {
            	//XXX That check produces many false errors - should be corrected  
//            	Type[] types = 
            		TypeUtils.javaTypeToCompatibleHibTypes(fieldType);
                
                Type mappingType = mapping.getType();
                
                boolean isMappedToBlob = (mappingType == Type.BINARY || mappingType == Type.BLOB ||
                    mappingType == Type.CLOB || mappingType == Type.SERIALIZABLE);
                
                boolean validMappingType = isMappedToBlob;

                if (!isMappedToBlob)
                {
                    validMappingType = TypeUtils.isFieldCompatibleValidator(fieldType,mappingType);
                }
                
                //do not check field type or field type is valid
                if (fieldType == null || validMappingType)
                {
/* rem by yk 05.09.2005 ORMIISTUD-728                	
                    Iterator columns = mapping.getColumnIterator();
                    IDatabaseColumn column = null;
                    while (columns.hasNext() && column == null)
                    {
                        column = (IDatabaseColumn) columns.next();
                    }
                    Type hibType = mapping.getType();
                    
                    if (hibType != null && !TypeUtils.isColumnTypeCompatible(column,hibType))
                    {
                        if (column != null)
                            createMarker(currentClassMapping,column.getName(),TypeUtils.SQLTypeToName(hibType.getSqlType()),BAD_COLUMN_TYPE);;
                    }
*/                   
                }
                else
                {
                    createMarker(currentClassMapping,fieldName,mapping.getType().getName(),BAD_HIBERNATE_TYPE);
                }
            }
        }
        
        return super.visitSimpleValueMapping(mapping, argument);
	}

	
	
	private String getErrorMessage(int errorID, String entityName) {
		switch (errorID) {
		case NO_DISCRIMINATOR_VALUE:
			return MessageFormat.format(HibernateValidationProblem.BUNDLE.getString("PERSISTENT_CLASS_NO_DISCRIMINATOR_VALUE"),new Object[]{entityName});
		case NO_DISCRIMINATOR_MAPPING:
			return MessageFormat.format(HibernateValidationProblem.BUNDLE.getString("PERSISTENT_CLASS_NO_DISCRIMINATOR_MAPPING"),new Object[]{entityName});
		case NO_KEY:
			return MessageFormat.format(HibernateValidationProblem.BUNDLE.getString("PERSISTENT_CLASS_NO_KEY"),new Object[]{entityName});
		case NO_ID:
			return MessageFormat.format(HibernateValidationProblem.BUNDLE.getString("PERSISTENT_CLASS_NO_ID"),new Object[]{entityName});
        default:
			return "";
		}

	}
	private String getErrorMessage(int errorID, String entityName, String fieldName) {
		switch (errorID) {
		case NO_REFERENCED_ENTITY:
			return MessageFormat.format(HibernateValidationProblem.BUNDLE.getString("PERSISTENT_FIELD_NO_REFERENCED_ENTITY"),new Object[]{entityName, fieldName});

        // added by Nick 16.06.2005
        case BAD_COLUMN_NUMBER:
            return MessageFormat.format(HibernateValidationProblem.BUNDLE.getString("BAD_COLUMN_NUMBER"),new Object[]{entityName, fieldName});
        case BAD_COLUMN_TYPE:
		    return MessageFormat.format(HibernateValidationProblem.BUNDLE.getString("BAD_COLUMN_TYPE"),new Object[]{entityName, fieldName});
        case BAD_HIBERNATE_TYPE:
            return MessageFormat.format(HibernateValidationProblem.BUNDLE.getString("BAD_HIBERNATE_TYPE"),new Object[]{entityName, fieldName});
        case NO_COLUMNS:
            return MessageFormat.format(HibernateValidationProblem.BUNDLE.getString("NO_COLUMNS"),new Object[]{entityName});
        // by Nick
        case INCORRECT_INVERSE_NOT_BIDIR:
            return MessageFormat.format(HibernateValidationProblem.BUNDLE.getString("INCORRECT_INVERSE_NOT_BIDIR"),new Object[]{entityName});
        case INCORRECT_INVERSE_NOT_INVERSED:
            return MessageFormat.format(HibernateValidationProblem.BUNDLE.getString("INCORRECT_INVERSE_NOT_INVERSED"),new Object[]{entityName});
        case INDEXED_INVERSED_COLLECTION:
            return MessageFormat.format(HibernateValidationProblem.BUNDLE.getString("INDEXED_INVERSED_COLLECTION"),new Object[]{entityName});
        case NO_REFERENCED_PROPERTY:
            return MessageFormat.format(HibernateValidationProblem.BUNDLE.getString("NO_REFERENCED_PROPERTY"),new Object[]{entityName,fieldName});
        case REF_PROPERTY_NOT_REFERENCING_US:
            return MessageFormat.format(HibernateValidationProblem.BUNDLE.getString("REF_PROPERTY_NOT_REFERENCING_US"),new Object[]{entityName,fieldName});
        case CONSTRAINED_TO_OTO_BAD_STRATEGY:
            return MessageFormat.format(HibernateValidationProblem.BUNDLE.getString("CONSTRAINED_TO_OTO_BAD_STRATEGY"),new Object[]{entityName,fieldName});

        case NO_REFERENCED_TABLE:
            return MessageFormat.format(HibernateValidationProblem.BUNDLE.getString("NO_REFERENCED_TABLE"),new Object[]{entityName});
        case REFERENCED_TABLE_NO_PK:
            return MessageFormat.format(HibernateValidationProblem.BUNDLE.getString("REFERENCED_TABLE_NO_PK"),new Object[]{entityName,fieldName});
        case PK_COLUMN_INCOMPATIBLE:
            return MessageFormat.format(HibernateValidationProblem.BUNDLE.getString("PK_COLUMN_INCOMPATIBLE"),new Object[]{entityName,fieldName});
            
        case PK_FK_SIZE_CONFLICT:    
            return MessageFormat.format(HibernateValidationProblem.BUNDLE.getString("PK_FK_SIZE_CONFLICT"),new Object[]{entityName,fieldName});
        // #added# by Konstantin Mishin on 30.09.2005 fixed for ESORM-78    
		case NOT_IMPLEMENT_COMPARABLE_WARNING:
			return MessageFormat.format(HibernateValidationProblem.BUNDLE.getString("NOT_IMPLEMENT_COMPARABLE_WARNING"),new Object[]{entityName});
	   // #added# 
   
        default:
			return "";
		}
		
	}

    public Object visitCollectionMapping(ICollectionMapping mapping, Object argument) {
    	
//  	if (mapping.isInverse() && mapping instanceof IIndexedCollectionMapping)
//  	createMarker(currentClassMapping,mapping.getFieldMapping().getPersistentField().getName(),"",INDEXED_INVERSED_COLLECTION,true);
        // #added# by Konstantin Mishin on 30.09.2005 fixed for ESORM-78    
    	if("natural".equals(mapping.getSort())) {
    		IType type = null;
    		String referencedEntityName = null;
    		IHibernateValueMapping hibernateValueMapping = mapping.getElement();
    		if (hibernateValueMapping instanceof ComponentMapping) {
    			IPersistentClass persistentClass = ((ComponentMapping)hibernateValueMapping).getComponentClass();
    			if(persistentClass!=null) {
    				type = persistentClass.getType();
    				referencedEntityName = persistentClass.getName();
    			}
    			
    		}
    		else if (hibernateValueMapping instanceof ManyToManyMapping) {
    			referencedEntityName = ((ManyToOneMapping)hibernateValueMapping).getReferencedEntityName();
    			
    		}
    		else if (hibernateValueMapping instanceof OneToManyMapping) {
    			referencedEntityName = ((OneToManyMapping)hibernateValueMapping).getReferencedEntityName();
    			
    		}
    		if (type == null && referencedEntityName != null){
    			try {
    				type = ScanProject.findClass(referencedEntityName, projectMapping.getProject().getProject());					
    			} catch (CoreException e) {
    				ExceptionHandler.logThrowableWarning(e,"validateMapping for "+mapping.getName());	
    			}
    		}
       		if (type != null){
       			try {
       				if (!ClassUtils.isImplementing(type,"java.lang.Comparable"))
       					createMarker(currentClassMapping,referencedEntityName, "", NOT_IMPLEMENT_COMPARABLE_WARNING);
      			} catch (CoreException e) {
       				ExceptionHandler.logThrowableWarning(e,"validateMapping for "+mapping.getName());	
       			}
       		}
    	}
        // #added#   
    	checkKeyMapping(mapping.getKey());
    	
    	return super.visitCollectionMapping(mapping, argument);
    }
    
    void setValidationPath(String validationPath) {
        this.validationPath = validationPath;
    }

}
