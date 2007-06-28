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


import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.OrmProgressMonitor;
import org.jboss.tools.hibernate.internal.core.properties.EditableListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.ListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.TextPropertyDescriptorWithTemplate;
import org.jboss.tools.hibernate.internal.core.properties.TextPropertyDescriptorWithType;




/**
 * @author alex
 *
 * Class for maintaining orm studio property descriptors 
 */
public class OrmPropertyDescriptorsHolder extends PropertyDescriptorsHolder {
	private static OrmPropertyDescriptorsHolder instance;
	public static OrmPropertyDescriptorsHolder getInstance(OrmProject mod){
		instance=new OrmPropertyDescriptorsHolder(mod);
		return instance;
	}
	private static PropertyDescriptorsHolder springProperties=new PropertyDescriptorsHolder();
	public static PropertyDescriptorsHolder getSpringPropertiesDescriptors(){
		return springProperties;
	}
	
	private static final String TYPES_CATEGORY=Messages.OrmPropertyDescriptorsHolder_TypesCategory;
    private static final String NUMERIC_TYPES_CATEGORY=Messages.OrmPropertyDescriptorsHolder_NumericTypesCategory;
	private static final String DISCRIMINATOR_CATEGORY=Messages.OrmPropertyDescriptorsHolder_DiscriminatorCategory;
	private static final String ID_CATEGORY=Messages.OrmPropertyDescriptorsHolder_IdCategory;
	private static final String LOCKING_CATEGORY=Messages.OrmPropertyDescriptorsHolder_LockingCategory;
	private static final String ASSOCIATION_CATEGORY=Messages.OrmPropertyDescriptorsHolder_AssociationsCategory;
	private static final String AUTOMAPPING_CATEGORY=Messages.OrmPropertyDescriptorsHolder_AutomappingCategory;
	private static final String NAMES=Messages.OrmPropertyDescriptorsHolder_Names;
	// added by Nick 04.07.2005
    private static final String CLASS_MAPPING_CATEGORY=Messages.OrmPropertyDescriptorsHolder_ClassMappingCategory;
	// by Nick
    //add akuzmin 15.09.2005
	private static final String SPRING_FRAMEWORK = Messages.OrmPropertyDescriptorsHolder_SpringFramework;    
    
	private OrmPropertyDescriptorsHolder(OrmProject mod){

        // XXX toAlex should be "XML file per hierarchy" or "XML file per class"? 
		//This is a question. I discussed it with guys who used Hibernate in real life projects.
		//They prefer use XML file per hierarchy strategy so let it be as is.
		PropertyDescriptor pd=new ListPropertyDescriptor(OrmConfiguration.HIBERNATE_STORAGE,Messages.OrmPropertyDescriptorsHolder_HibernateStorageN,OrmConfiguration.STORAGE_STRATEGY_NAMES,OrmConfiguration.STORAGE_STRATEGY_VALUES);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateStorageD);
		addPropertyDescriptor(pd);
		pd.setCategory(AUTOMAPPING_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_STORAGE_STRATEGY);

//		String[] values;
//		Integer[] constants;
		
        // added by Nick 26.09.2005
        pd=new ListPropertyDescriptor(OrmConfiguration.POJO_RENDERER,Messages.OrmPropertyDescriptorsHolder_POJORendererN,ExtensionBinder.getExtensionsParameters(ExtensionBinder.CODE_RENDERER_SERVICE)[0],ExtensionBinder.getExtensionsParameters(ExtensionBinder.CODE_RENDERER_SERVICE)[1]);
        pd.setDescription(Messages.OrmPropertyDescriptorsHolder_POJORendererD);
        addPropertyDescriptor(pd);
        pd.setCategory(AUTOMAPPING_CATEGORY);
        setDefaultPropertyValue(pd.getId(),ExtensionBinder.getDefaultImplementationId(ExtensionBinder.CODE_RENDERER_SERVICE));
        // by Nick
        
		//XXX (bela_n)(1) Add all orm properties
		pd=new ListPropertyDescriptor(OrmConfiguration.HIBERNATE_INHERITANCE,Messages.OrmPropertyDescriptorsHolder_HibernateInheritanceN,OrmConfiguration.INHERITANCE_MAP_STRATEGY_NAMES, OrmConfiguration.INHERITANCE_MAP_STRATEGY_VALUES);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateInheritanceD);
		addPropertyDescriptor(pd);
		pd.setCategory(AUTOMAPPING_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_INHERITANCE_MAP_STRATEGY);
		
		pd=new ListPropertyDescriptor(OrmConfiguration.HIBERNATE_OPTIMISTIC,Messages.OrmPropertyDescriptorsHolder_HibernateOptimisticN,OrmConfiguration.OPTIMISTIC_STRATEGY_NAMES,OrmConfiguration.OPTIMISTIC_STRATEGY_VALUES);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateOptimisticD);
		addPropertyDescriptor(pd);
		pd.setCategory(LOCKING_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_OPTIMISTIC_STRATEGY);
		
        // added by Nick 30.08.2005
        pd=new ListPropertyDescriptor(OrmConfiguration.REVERSING_NATIVE_SQL_TYPES,Messages.OrmPropertyDescriptorsHolder_ReversingNativeSQLTypesN,OrmConfiguration.BOOLEAN_VALUES);
        pd.setDescription(Messages.OrmPropertyDescriptorsHolder_ReversingNativeSQLTypesD);
        addPropertyDescriptor(pd);
        pd.setCategory(AUTOMAPPING_CATEGORY);
        setDefaultPropertyValue(pd.getId(),OrmConfiguration.BOOLEAN_VALUES[0]);
        // by Nick
        
		/* Eventually remove commented code
		pd=new ListPropertyDescriptor("hibernate.versioning","Versioning method",OrmConfiguration.VERSIONING_METHOD_NAMES,OrmConfiguration.VERSIONING_METHOD_VALUES);
		pd.setDescription("Defines versioning method to use");
		addPropertyDescriptor(pd);
		pd.setCategory(LOCKING_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_VERSIONING_METHOD);
		*/
		
		pd=new ListPropertyDescriptor(OrmConfiguration.HIBERNATE_VERSION_DATATYPE,Messages.OrmPropertyDescriptorsHolder_HibernateVersionDatatypeN,OrmConfiguration.VERSION_TYPES);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateVersionDatatypeD);
		addPropertyDescriptor(pd);
		pd.setCategory(LOCKING_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_VERSION_TYPE);
		
		//XXX Nick(5) Use ListPropertyDescriptor for this property
		pd=new ListPropertyDescriptor("hibernate.version.unsaved-value",Messages.OrmPropertyDescriptorsHolder_HibernateVersionUnsavedValueN,OrmConfiguration.VERSION_UNSAVED_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateVersionUnsavedValueD);
		addPropertyDescriptor(pd);
		pd.setCategory(LOCKING_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_VERSION_UNSAVED);
		
		//XXX Eventually remove commented code
		/*pd=new ListPropertyDescriptor("hibernate.timestamp.unsaved-value","Timestamp unsaved value",OrmConfiguration.TIMESTAMP_UNSAVED_VALUES);
		pd.setDescription("Indicates timestamp value for newly instantiated instances");
		addPropertyDescriptor(pd);
		pd.setCategory(LOCKING_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_TIMESTAMP_UNSAVED);*/
		
		//XXX Nick(5) Use ListPropertyDescriptor for this property
		pd=new EditableListPropertyDescriptor(OrmConfiguration.HIBERNATE_CASCADE,Messages.OrmPropertyDescriptorsHolder_HibernateCascadeN,OrmConfiguration.CASCADE_VALUES);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateCascadeD);
		addPropertyDescriptor(pd);
		pd.setCategory(ASSOCIATION_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_CASCADE);
		
		//XXX Nick(5) Use ListPropertyDescriptor for this property
		// changed by Nick 09.06.2005 - decided to throw away this property for now
//        pd=new EditableListPropertyDescriptor(OrmConfiguration.HIBERNATE_ACCESS,"Property access",OrmConfiguration.ACCESS_VALUES);
//		pd.setDescription("Defines the strategy Hibernate should use for accessing property values");
//		addPropertyDescriptor(pd);
//		pd.setCategory(GENERAL_CATEGORY);
//		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_ACCESS);
		// by Nick
        
		// added by Nick 20.09.2005
        pd=new ListPropertyDescriptor(OrmConfiguration.HIBERNATE_LAZY,Messages.OrmPropertyDescriptorsHolder_HibernateLazyN,OrmConfiguration.BOOLEAN_VALUES);
        pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateLazyD);
        addPropertyDescriptor(pd);
        pd.setCategory(ASSOCIATION_CATEGORY);
        setDefaultPropertyValue(pd.getId(),OrmConfiguration.BOOLEAN_VALUES[1]);
        // by Nick
        
		pd=new ListPropertyDescriptor(OrmConfiguration.HIBERNATE_ASSOCIATIONS_LAZY,Messages.OrmPropertyDescriptorsHolder_HibernateAssociationsLazyN,OrmConfiguration.ASSOCIATIONS_LAZY_VALUES);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateAssociationsLazyD);
		addPropertyDescriptor(pd);
		pd.setCategory(ASSOCIATION_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.ASSOCIATIONS_DEFAULT_LAZY);
		
        pd=new ListPropertyDescriptor(OrmConfiguration.HIBERNATE_COLLECTIONS_LAZY,Messages.OrmPropertyDescriptorsHolder_HibernateCollectionsLazyN,OrmConfiguration.BOOLEAN_VALUES);
        pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateCollectionsLazyD);
        addPropertyDescriptor(pd);
        pd.setCategory(ASSOCIATION_CATEGORY);
        setDefaultPropertyValue(pd.getId(),OrmConfiguration.BOOLEAN_VALUES[1]);
        
		pd=new ListPropertyDescriptor(OrmConfiguration.HIBERNATE_DISCRIMINATOR_DATATYPE,Messages.OrmPropertyDescriptorsHolder_HibernateDiscriminatorDatatypeN,OrmConfiguration.DISCRIMINATOR_DATATYPES);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateDiscriminatorDatatypeD);
		addPropertyDescriptor(pd);
		pd.setCategory(DISCRIMINATOR_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_DISCRIMINATOR_DATATYPE);
		
		pd=new ListPropertyDescriptor("hibernate.discriminator.force",Messages.OrmPropertyDescriptorsHolder_HibernateDiscriminatorForceN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateDiscriminatorForceD);
		addPropertyDescriptor(pd);
		pd.setCategory(DISCRIMINATOR_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_FORCE_DISCRIMINATOR);

		//XXX Nick(5) Add default property value for all descriptors. See examples above.
		pd=new ListPropertyDescriptor("hibernate.discriminator.insert",Messages.OrmPropertyDescriptorsHolder_HibernateDiscriminatorInsertN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateDiscriminatorInsertD);
		addPropertyDescriptor(pd);
		pd.setCategory(DISCRIMINATOR_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_INSERT_DISCRININATOR);
		
		
		//XXX Nick(5) Use  Check for proper <string>,<hibernate datatype> usage, plz
		//Use TextPropertyDescriptor instead of ListPropertyDescriptor for properties that have no predefined list of values
		pd=new TextPropertyDescriptor("hibernate.discriminator.formula",Messages.OrmPropertyDescriptorsHolder_HibernateDiscriminatorFormulaN); //$NON-NLS-1$
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateDiscriminatorFormulaD);
		addPropertyDescriptor(pd);
		pd.setCategory(DISCRIMINATOR_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_DISCRIMINATOR_FORMULA);
		
		pd=new ListPropertyDescriptor(OrmConfiguration.HIBERNATE_ID_DATATYPE,Messages.OrmPropertyDescriptorsHolder_HibernateIdDatatypeN,OrmConfiguration.ID_DATATYPES);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateIdDatatypeD);
		addPropertyDescriptor(pd);
		pd.setCategory(ID_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_ID_DATATYPE);
		
		//XXX Nick(5) Use ListPropertyDescriptor for this property
		pd=new EditableListPropertyDescriptor(OrmConfiguration.HIBERNATE_ID_UNSAVED_VALUE,Messages.OrmPropertyDescriptorsHolder_HibernateIdUnsavedValueN,OrmConfiguration.ID_UNSAVED_VALUES);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateIdUnsavedValueD);
		addPropertyDescriptor(pd);
		pd.setCategory(ID_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_ID_UNSAVED);
		
		//XXX toAlex generator classes can have more than two parameters (see sample for "hilo" generator)
		//This property contains a string with the following formationg: <class name>,param1=value1,param2=value2,...,paramN=valueN
		//XXX Nick(5) Use TextPropertyDescriptor instead of ListPropertyDescriptor for properties that have no predefined list of values
		//Do not use long display names(second parameter in PropertyDescriptors). Please make them shorter.
		
		//XXX provide example for this field "<class>,param1=value,param2=value"
		pd=new TextPropertyDescriptor(OrmConfiguration.HIBERNATE_ID_GENERATOR,Messages.OrmPropertyDescriptorsHolder_HibernateIdGeneratorN);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateIdGeneratorD);
		addPropertyDescriptor(pd);
		pd.setCategory(ID_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_ID_GENERATOR);
		
        // #added# by Konstantin Mishin on 30.11.2005 fixed for ESORM-401
		pd=new ListPropertyDescriptor(OrmConfiguration.HIBERNATE_ID_GENERATOR_QUALITY,Messages.OrmPropertyDescriptorsHolder_HibernateIdGeneratorQualityN,OrmConfiguration.BOOLEAN_VALUES);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateIdGeneratorQualityD);
		addPropertyDescriptor(pd);
		pd.setCategory(ID_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_ID_GENERATOR_QUALITY);
        // #added#

		//XXX Nick(5) Use ListPropertyDescriptor for this property
		pd=new EditableListPropertyDescriptor("hibernate.associations.cascade",Messages.OrmPropertyDescriptorsHolder_HibernateAssociationsCascadeN,OrmConfiguration.ASSOCIATIONS_CASCADE_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateAssociationsCascadeD);
		addPropertyDescriptor(pd);
		pd.setCategory(ASSOCIATION_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_ASSOCIATIONS_CASCADE);
		
		//XXX Nick(5) Use ListPropertyDescriptor for this property
		pd=new ListPropertyDescriptor(OrmConfiguration.HIBERNATE_KEY_ON_DELETE,Messages.OrmPropertyDescriptorsHolder_HibernateKeyOnDeleteN,OrmConfiguration.KEY_ON_DELETE_VALUES);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateKeyOnDeleteD);
		addPropertyDescriptor(pd);
		pd.setCategory(ASSOCIATION_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_KEY_ON_DELETE);
		
		
		//XXX Nick(5) Use ListPropertyDescriptor for this property and below
		pd=new ListPropertyDescriptor("hibernate.type.int",Messages.OrmPropertyDescriptorsHolder_HibernateTypeIntN,OrmConfiguration.TYPE_INT); //$NON-NLS-1$
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateTypeIntD);
		addPropertyDescriptor(pd);
		pd.setCategory(TYPES_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_TYPE_INT);
		
		pd=new ListPropertyDescriptor("hibernate.type.long",Messages.OrmPropertyDescriptorsHolder_HibernateTypeLongN,OrmConfiguration.TYPE_LONG); //$NON-NLS-1$
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateTypeLongD);
		addPropertyDescriptor(pd);
		pd.setCategory(TYPES_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_TYPE_LONG);
		
		pd=new ListPropertyDescriptor("hibernate.type.short",Messages.OrmPropertyDescriptorsHolder_HibernateTypeShortN,OrmConfiguration.TYPE_SHORT); //$NON-NLS-1$
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateTypeShortD);
		addPropertyDescriptor(pd);
		pd.setCategory(TYPES_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_TYPE_SHORT);
		
		pd=new ListPropertyDescriptor("hibernate.type.float",Messages.OrmPropertyDescriptorsHolder_HibernateTypeFloatN,OrmConfiguration.TYPE_FLOAT); //$NON-NLS-1$
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateTypeFloatD);
		addPropertyDescriptor(pd);
		pd.setCategory(TYPES_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_TYPE_FLOAT);
		
		pd=new ListPropertyDescriptor("hibernate.type.double",Messages.OrmPropertyDescriptorsHolder_HibernateTypeDoubleN,OrmConfiguration.TYPE_DOUBLE); //$NON-NLS-1$
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateTypeDoubleD);
		addPropertyDescriptor(pd);
		pd.setCategory(TYPES_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_TYPE_DOUBLE);
		
		pd=new ListPropertyDescriptor("hibernate.type.character",Messages.OrmPropertyDescriptorsHolder_HibernateTypeCharacterN,OrmConfiguration.TYPE_CHAR); //$NON-NLS-1$
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateTypeCharacterD);
		addPropertyDescriptor(pd);
		pd.setCategory(TYPES_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_TYPE_CHAR);
		
		pd=new ListPropertyDescriptor("hibernate.type.byte",Messages.OrmPropertyDescriptorsHolder_HibernateTypeByteN,OrmConfiguration.TYPE_BYTE); //$NON-NLS-1$
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateTypeByteD);
		addPropertyDescriptor(pd);
		pd.setCategory(TYPES_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_TYPE_BYTE);
		
		pd=new ListPropertyDescriptor("hibernate.type.boolean",Messages.OrmPropertyDescriptorsHolder_HibernateTypeBooleanN,OrmConfiguration.TYPE_BOOLEAN); //$NON-NLS-1$
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateTypeBooleanD);
		addPropertyDescriptor(pd);
		pd.setCategory(TYPES_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_TYPE_BOOLEAN);

        pd=new ListPropertyDescriptor("hibernate.type.time",Messages.OrmPropertyDescriptorsHolder_HibernateTypeTimeN,OrmConfiguration.TYPE_TIME); //$NON-NLS-1$
        pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateTypeTimeD);
        addPropertyDescriptor(pd);
        pd.setCategory(TYPES_CATEGORY);
        setDefaultPropertyValue(pd.getId(),OrmConfiguration.TYPE_TIME[0]);
        
        pd=new ListPropertyDescriptor("hibernate.type.date",Messages.OrmPropertyDescriptorsHolder_HibernateTypeDateN,OrmConfiguration.TYPE_DATE); //$NON-NLS-1$
        pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateTypeDateD);
        addPropertyDescriptor(pd);
        pd.setCategory(TYPES_CATEGORY);
        setDefaultPropertyValue(pd.getId(),OrmConfiguration.TYPE_DATE[0]);
        
        pd=new ListPropertyDescriptor("hibernate.type.timestamp",Messages.OrmPropertyDescriptorsHolder_HibernateTypeTimestampN,OrmConfiguration.TYPE_TIMESTAMP); //$NON-NLS-1$
        pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateTypeTimestampD);
        addPropertyDescriptor(pd);
        pd.setCategory(TYPES_CATEGORY);
        setDefaultPropertyValue(pd.getId(),OrmConfiguration.TYPE_TIMESTAMP[0]);
        
        pd=new ListPropertyDescriptor("hibernate.type.calendar",Messages.OrmPropertyDescriptorsHolder_HibernateTypeCalendarN,OrmConfiguration.TYPE_CALENDAR); //$NON-NLS-1$
        pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateTypeCalendarD);
        addPropertyDescriptor(pd);
        pd.setCategory(TYPES_CATEGORY);
        setDefaultPropertyValue(pd.getId(),OrmConfiguration.TYPE_CALENDAR[0]);
        
        pd=new ListPropertyDescriptor("hibernate.type.calendar_date",Messages.OrmPropertyDescriptorsHolder_HibernateTypeCalendarDateN,OrmConfiguration.TYPE_CALENDAR_DATE); //$NON-NLS-1$
        pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateTypeCalendarDateD);
        addPropertyDescriptor(pd);
        pd.setCategory(TYPES_CATEGORY);
        setDefaultPropertyValue(pd.getId(),OrmConfiguration.TYPE_CALENDAR_DATE[0]);
        
        //ORMIISTUD-511 - == !(lockStrategy.equals("none"))
//		pd=new ListPropertyDescriptor(OrmConfiguration.CLASS_DYNAMIC_INSERT,"dynamic insert",OrmConfiguration.BOOLEAN_VALUES);
//		pd.setDescription("Use dynamic insert");
//		addPropertyDescriptor(pd);
//		pd.setCategory(CLASS_MAPPING_CATEGORY);
//		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_DYNAMIC_INSERT);
		
//		pd=new ListPropertyDescriptor(OrmConfiguration.CLASS_DYNAMIC_UPDATE,"dynamic update",OrmConfiguration.BOOLEAN_VALUES);
//		pd.setDescription("Use dynamic update");
//		addPropertyDescriptor(pd);
//		pd.setCategory(CLASS_MAPPING_CATEGORY);
//		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_DYNAMIC_UPDATE);

		pd=new ListPropertyDescriptor(OrmConfiguration.CLASS_SELECT_BEFORE_UPDATE,Messages.OrmPropertyDescriptorsHolder_ClassSelectBeforeUpdateN,OrmConfiguration.BOOLEAN_VALUES);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_ClassSelectBeforeUpdateD);
		addPropertyDescriptor(pd);
		pd.setCategory(CLASS_MAPPING_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_SELECT_BEFORE_UPDATE);
		
//		pd=new TextPropertyDescriptor(OrmConfiguration.CLASS_BATCH_SIZE,"fetching \"batch size\"");
		pd=new TextPropertyDescriptorWithType(OrmConfiguration.CLASS_BATCH_SIZE,Messages.OrmPropertyDescriptorsHolder_ClassBatchSizeN,true,true,false,new Integer(1),null);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_ClassBatchSizeD);
		addPropertyDescriptor(pd);
		pd.setCategory(CLASS_MAPPING_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_CLASS_BATCH_SIZE);

		pd=new ListPropertyDescriptor(OrmConfiguration.HAM_SAFE_COLLECTION,Messages.OrmPropertyDescriptorsHolder_HamSafeCollectionN,OrmConfiguration.HAM_SAFE_COLLECTION_VALUES);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HamSafeCollectionD);
		addPropertyDescriptor(pd);
		pd.setCategory(AUTOMAPPING_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.HAM_SAFE_COLLECTION_DEFAULT);
		
		pd=new ListPropertyDescriptor(OrmConfiguration.HAM_COLLECTION_ELEMENT_TYPE,Messages.OrmPropertyDescriptorsHolder_HamCollectionElementTypeN,OrmConfiguration.HAM_COLLECTION_ELEMENT_TYPE_VALUES);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HamCollectionElementTypeD);
		addPropertyDescriptor(pd);
		pd.setCategory(AUTOMAPPING_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.HAM_COLLECTION_ELEMENT_TYPE_DEFAULT);

        pd=new ListPropertyDescriptor(OrmConfiguration.HAM_REACHABILITY,Messages.OrmPropertyDescriptorsHolder_HamReachabilityN,OrmConfiguration.HAM_REACHABILITY_VALUES);
        pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HamReachabilityD);
        addPropertyDescriptor(pd);
        pd.setCategory(AUTOMAPPING_CATEGORY);
        setDefaultPropertyValue(pd.getId(),OrmConfiguration.HAM_REACHABILITY_VALUES[0]);
        
        pd=new ListPropertyDescriptor(OrmConfiguration.HAM_ACCESSOR,Messages.OrmPropertyDescriptorsHolder_HamAccessorN,OrmConfiguration.HAM_ACCESSOR_VALUES);
        pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HamAccessorD);
        addPropertyDescriptor(pd);
        pd.setCategory(AUTOMAPPING_CATEGORY);
        setDefaultPropertyValue(pd.getId(),OrmConfiguration.HAM_ACCESSOR_DEFAULT);
        
        pd=new ListPropertyDescriptor(OrmConfiguration.HAM_QUERY_FUZZY_ON,Messages.OrmPropertyDescriptorsHolder_HamQueryFuzzyOnN,OrmConfiguration.BOOLEAN_VALUES);
        pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HamQueryFuzzyOnD);
        addPropertyDescriptor(pd);
        pd.setCategory(AUTOMAPPING_CATEGORY);
        setDefaultPropertyValue(pd.getId(),OrmConfiguration.BOOLEAN_VALUES[0]);
        
        pd=new TextPropertyDescriptorWithType(OrmConfiguration.HAM_QUERY_FUZZINESS,Messages.OrmPropertyDescriptorsHolder_HamQueryFuzzinessN,false,true,true,new Float(0.0),new Float(1.0));
        pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HamQueryFuzzinessD);
        addPropertyDescriptor(pd);
        pd.setCategory(AUTOMAPPING_CATEGORY);
        setDefaultPropertyValue(pd.getId(),"0.8"); //$NON-NLS-1$
        
        pd=new ListPropertyDescriptor(OrmConfiguration.HAM_LINK_TABLES,Messages.OrmPropertyDescriptorsHolder_HamLinkTablesN,OrmConfiguration.HAM_LINK_TABLES_VALUES);
        pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HamLinkTablesD);
        addPropertyDescriptor(pd);
        pd.setCategory(AUTOMAPPING_CATEGORY);
        setDefaultPropertyValue(pd.getId(),OrmConfiguration.HAM_LINK_TABLES_DEFAULT);
        
        //REVERSE TYPES
        pd=new ListPropertyDescriptor(OrmConfiguration.REVTYPE_NUMERIC_1_0,Messages.OrmPropertyDescriptorsHolder_RevtypeNumeric10N,OrmConfiguration.REVTYPE_NUMERIC_1_0_VALUES);
        pd.setDescription(Messages.OrmPropertyDescriptorsHolder_RevtypeNumeric10D);
        addPropertyDescriptor(pd);
        pd.setCategory(NUMERIC_TYPES_CATEGORY);
        setDefaultPropertyValue(pd.getId(),OrmConfiguration.REVTYPE_NUMERIC_1_0_VALUES[2]);
        
        pd=new ListPropertyDescriptor(OrmConfiguration.REVTYPE_NUMERIC_CONVERT,Messages.OrmPropertyDescriptorsHolder_RevtypeNumericConvertN,OrmConfiguration.BOOLEAN_VALUES);
        pd.setDescription(Messages.OrmPropertyDescriptorsHolder_RevtypeNumericConvertD);
        addPropertyDescriptor(pd);
        pd.setCategory(NUMERIC_TYPES_CATEGORY);
        setDefaultPropertyValue(pd.getId(),OrmConfiguration.BOOLEAN_VALUES[1]);

        pd=new TextPropertyDescriptorWithTemplate(OrmConfiguration.REVTYPE_NUMERIC_X_Y,Messages.OrmPropertyDescriptorsHolder_RevtypeNumericXYN,";",",",2,"-","0123456789-"); //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        pd.setDescription(Messages.OrmPropertyDescriptorsHolder_RevtypeNumericXYD);
        addPropertyDescriptor(pd);
        pd.setCategory(NUMERIC_TYPES_CATEGORY);
        setDefaultPropertyValue(pd.getId()," - , 1 - ;"); //$NON-NLS-1$
        
        
		//NAMES
		pd=new TextPropertyDescriptor(OrmConfiguration.IDENTIFIER_COLUMN_NAME,Messages.OrmPropertyDescriptorsHolder_IdentifierColumnNameN);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_IdentifierColumnNameD);
		addPropertyDescriptor(pd);
		pd.setCategory(NAMES);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_IDENTIFIER_COLUMN_NAME);
		
		pd=new TextPropertyDescriptor(OrmConfiguration.VERSION_COLUMN_NAME,Messages.OrmPropertyDescriptorsHolder_VersionColumnNameN);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_VersionColumnNameD);
		addPropertyDescriptor(pd);
		pd.setCategory(NAMES);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_VERSION_COLUMN_NAME);
		
		pd=new TextPropertyDescriptor(OrmConfiguration.DISCRIMINATOR_COLUMN_NAME,Messages.OrmPropertyDescriptorsHolder_DiscriminatorColumnNameN);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_DiscriminatorColumnNameD);
		addPropertyDescriptor(pd);
		pd.setCategory(NAMES);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_DISCRIMINATOR_COLUMN_NAME);

		pd=new TextPropertyDescriptor(OrmConfiguration.IDENTIFIER_QUERY,Messages.OrmPropertyDescriptorsHolder_IdentifierQueryN);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_IdentifierQueryD);
		addPropertyDescriptor(pd);
		pd.setCategory(NAMES);
		setDefaultPropertyValue(pd.getId(),"id key pk identifier"); //$NON-NLS-1$

		pd=new TextPropertyDescriptor(OrmConfiguration.VERSION_QUERY,Messages.OrmPropertyDescriptorsHolder_VersionQueryN);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_VersionQueryD);
		addPropertyDescriptor(pd);
		pd.setCategory(NAMES);
		setDefaultPropertyValue(pd.getId(),"version vid"); //$NON-NLS-1$
		
		pd=new TextPropertyDescriptor(OrmConfiguration.DISCRIMINATOR_QUERY,Messages.OrmPropertyDescriptorsHolder_DiscriminatorQueryN);
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_DiscriminatorQueryD);
		addPropertyDescriptor(pd);
		pd.setCategory(NAMES);
		setDefaultPropertyValue(pd.getId(),"class discriminator type"); //$NON-NLS-1$

		//XXX Nick(5) Use TextPropertyDescriptor instead of ListPropertyDescriptor for properties that have no predefined list of values
		//Do not use long display names(second parameter in PropertyDescriptors). Please make them shorter.
		pd=new TextPropertyDescriptor("hibernate.default_schema",Messages.OrmPropertyDescriptorsHolder_HibernateDefaultSchemaN); //$NON-NLS-1$
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateDefaultSchemaD);
		addPropertyDescriptor(pd);
		pd.setCategory(NAMES);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_SCHEMA);
		
		//XXX Nick(5) Use TextPropertyDescriptor instead of ListPropertyDescriptor for properties that have no predefined list of values
		pd=new TextPropertyDescriptor("hibernate.default_package",Messages.OrmPropertyDescriptorsHolder_HibernateDefaultPackageN); //$NON-NLS-1$
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HibernateDefaultPackageD);
		addPropertyDescriptor(pd);
		pd.setCategory(NAMES);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_PACKAGE);
		
        pd=new TextPropertyDescriptor(OrmConfiguration.HAM_IGNORE_LIST,Messages.OrmPropertyDescriptorsHolder_HamIgnoreListN);
        pd.setDescription(Messages.OrmPropertyDescriptorsHolder_HamIgnoreListD);
        addPropertyDescriptor(pd);
        pd.setCategory(NAMES);
        setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
        pd=new TextPropertyDescriptor(OrmConfiguration.TABLE_PREFIX_QUERY,Messages.OrmPropertyDescriptorsHolder_TablePrefixQueryN);
        pd.setDescription(Messages.OrmPropertyDescriptorsHolder_TablePrefixQueryD);
        addPropertyDescriptor(pd);
        pd.setCategory(NAMES);
        setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
		
		//akuzmin 22.07.2005
	    IJavaElement[] obj=null;
	    IJavaElement[] elemobj=null;
	    IJavaElement[] elemfragobj=null;
	    ArrayList<String> objects = new ArrayList<String>();
	    ArrayList<String> readyobjects = new ArrayList<String>();
		IProject project = null;
		
		if (mod != null) {
			project = mod.getProject();
			IMapping[] mappings = mod.getMappings();
			for (int z = 0; z < mappings.length; z++) {
				IPersistentClass[] PertsistentClassesobj = mappings[z].getPertsistentClasses();
				for (int i = 0; i < PertsistentClassesobj.length; i++) {
					readyobjects.add(PertsistentClassesobj[i].getName());
				}
			}
			IJavaProject javaProject = JavaCore.create(project);
			try {
				obj = javaProject.getAllPackageFragmentRoots();
			} catch (JavaModelException e) {
				OrmCore.getPluginLog().logError(e); // tau // 15.09.2005
			}
			
			// add tau 19.03.2006
			// TODO (tau->tau) dodelat
			OrmProgressMonitor monitor = new OrmProgressMonitor(OrmProgressMonitor.getMonitor());
			monitor.setTaskParameters(100, obj.length);			

			for (int i = 0; i < obj.length; i++) {
				if (obj[i] instanceof IPackageFragmentRoot /* ICompilationUnit */) {
					IPackageFragmentRoot elem;
					elem = (IPackageFragmentRoot) obj[i];

					// added by Nick 26.11.2005
					if (!elem.exists())
						continue;
					// by Nick

					try {
						elemobj = elem.getChildren();
						for (int z = 0; z < elemobj.length; z++)
							if (elemobj[z] instanceof IPackageFragment) {
								elemfragobj = ((IPackageFragment) elemobj[z])
										.getChildren();
								for (int m = 0; m < elemfragobj.length; m++)
									if (elemfragobj[m] instanceof ICompilationUnit) {

										// added by Nick 08.10.2005
										ICompilationUnit u = (ICompilationUnit) elemfragobj[m];

										// added by Nick 26.11.2005
										if (!u.exists())
											continue;
										// by Nick

										ICompilationUnit wc = null;
										if (!u.isWorkingCopy())
											wc = u.getWorkingCopy(null);
										// by Nick

										IType pType = u.findPrimaryType();

										// add classes to list
										// changed by Nick 12.10.2005
										// if
										// (((ICompilationUnit)elemfragobj[m]).findPrimaryType()!=null)
										// if
										// ((!readyobjects.contains(elemobj[z].getElementName()+"."+(((ICompilationUnit)elemfragobj[m]).findPrimaryType().getElementName())))//GetTypes
										// &&(!Flags.isFinal(((ICompilationUnit)elemfragobj[m]).findPrimaryType().getFlags())))
										// {
										// objects.add(elemobj[z].getElementName()+"."+(((ICompilationUnit)elemfragobj[m]).findPrimaryType().getElementName()));
										// }
										// by Nick
										if (pType != null)
											if (!readyobjects.contains(pType
													.getFullyQualifiedName())// GetTypes
													&& (!Flags.isFinal(pType
															.getFlags()))) {
												objects
														.add(pType
																.getFullyQualifiedName());
											}

										// added by Nick 08.10.2005
										if (wc != null)
											wc.discardWorkingCopy();
										// by Nick
									}
							}

					} catch (JavaModelException e1) {
						OrmCore.getPluginLog().logError(e1); // tau
						// 15.09.2005
					}
				}
				monitor.worked();				
			}
		}
	 	String classes[];
	 	classes=new String[objects.size()+1];
	 	classes[0]=""; //$NON-NLS-1$
		for(int i=0;i<objects.size();i++)
			classes[i+1]=(String) objects.get(i);
		Arrays.sort(classes);
        pd=new ListPropertyDescriptor(OrmConfiguration.REVERSING_BASE_CLASS,Messages.OrmPropertyDescriptorsHolder_ReversingBaseClassN,classes);
        pd.setDescription(Messages.OrmPropertyDescriptorsHolder_ReversingBaseClassD);
        addPropertyDescriptor(pd);
        pd.setCategory(AUTOMAPPING_CATEGORY);
        setDefaultPropertyValue(pd.getId(),classes[0]);
	
///////////spring add akuzmin 23.08.2005
		pd=new TextPropertyDescriptor("spring.sessionfactory",Messages.OrmPropertyDescriptorsHolder_SpringSessionfactoryN); //$NON-NLS-1$
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_SpringSessionfactoryD);
//		addPropertyDescriptor(pd);
		springProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"sessionFactory"); //$NON-NLS-1$
		pd.setCategory(SPRING_FRAMEWORK);
		
		pd=new TextPropertyDescriptor("spring.data_source",Messages.OrmPropertyDescriptorsHolder_SpringDataSourceN); //$NON-NLS-1$
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_SpringDataSourceD);
//		addPropertyDescriptor(pd);
		springProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"dataSource"); //$NON-NLS-1$
		pd.setCategory(SPRING_FRAMEWORK);
		
		pd=new TextPropertyDescriptor("spring.transaction_manager",Messages.OrmPropertyDescriptorsHolder_SpringTransactionManagerN); //$NON-NLS-1$
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_SpringTransactionManagerD);
//		addPropertyDescriptor(pd);
		springProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"transactionManager"); //$NON-NLS-1$
		pd.setCategory(SPRING_FRAMEWORK);

		pd=new TextPropertyDescriptor("spring.transaction_interceptor",Messages.OrmPropertyDescriptorsHolder_SpringTransactionInterceptorN); //$NON-NLS-1$
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_SpringTransactionInterceptorD);
//		addPropertyDescriptor(pd);
		springProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"daoTransactionInterceptor"); //$NON-NLS-1$
		pd.setCategory(SPRING_FRAMEWORK);
		
		pd=new TextPropertyDescriptor("spring.hibernate_interceptor",Messages.OrmPropertyDescriptorsHolder_SpringHibernateInterceptorN); //$NON-NLS-1$
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_SpringHibernateInterceptorD);
//		addPropertyDescriptor(pd);
		springProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"hibernateInterceptor"); //$NON-NLS-1$
		pd.setCategory(SPRING_FRAMEWORK);

		pd=new TextPropertyDescriptor("spring.transaction_attributes",Messages.OrmPropertyDescriptorsHolder_SpringTransactionAttributesN); //$NON-NLS-1$
		pd.setDescription(Messages.OrmPropertyDescriptorsHolder_SpringTransactionAttributesD);
//		addPropertyDescriptor(pd);
		springProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"daoTxAttributes"); //$NON-NLS-1$
		pd.setCategory(SPRING_FRAMEWORK);
		
		
}
	/* 
	+hibernate.storage=XML file per class | XML file per package | XML file per project | XDoclet tags | Java 5 annotations
	hibernate.inheritance=Table per hierarchy | Table per subclass | Table per concrete class
	hibernate.optimistic=Check version | Check all fields | Check dirty fields | None
	hibernate.versioning=version | timestamp
	hibernate.version.datatype=integer, long, short, timestamp, calendar.
	hibernate.version.unsaved-value=null|negative|undefined
	hibernate.timestamp.unsaved-value=null|undefined
	hibernate.cascade=None | Save-Update
	hibernate.access=property | field | ClassName
	hibernate.lazy=true | false
	hibernate.discriminator.datatype=string | character | integer | byte | short | boolean | yes_no | true_false
	hibernate.discriminator.force=false | true
	hibernate.discriminator.insert=true | false
	hibernate.discriminator.formula=<string>
	hibernate.id.datatype=<hibernate datatype>
	hibernate.id.unsaved-value=null|any|none|undefined|id_value
	hibernate.id.generator=<class>,param1=value,param2=value
	hibernate.associations.cascade=all|none|save-update|delete
	hibernate.key.on-delete=noaction|cascade
	hibernate.schema=
	hibernate.catalog=
	hibernate.type.int=int | Integer
	hibernate.type.long=long | Long
	hibernate.type.short=short | Short
	hibernate.type.float=float | Float
	hibernate.type.double=double | Double
	hibernate.type.character=char | Character
	hibernate.type.byte=byte | Byte
	hibernate.type.boolean=boolean | Boolean
*/

}
