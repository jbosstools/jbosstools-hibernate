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
package org.jboss.tools.hibernate.internal.core.hibernate.descriptors;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.properties.ListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


/**
 * @author alex
 *
 * Hibernare class mapping property descriptors
 */
public class ClassMappingPropertyDescriptorsHolder extends PropertyDescriptorsHolder {
	private static PropertyDescriptorsHolder instance; 
	protected static final String GENERAL_CATEGORY=Messages.ClassMappingPropertyDescriptorsHolder_GeneralCategory;
	protected static final String ADVANCED_CATEGORY=Messages.ClassMappingPropertyDescriptorsHolder_AdvancedlCategory;	
	public static PropertyDescriptorsHolder getInstance(IPersistentClassMapping pcm){
		instance=new ClassMappingPropertyDescriptorsHolder(pcm);
		return instance;
	}
	
	protected ClassMappingPropertyDescriptorsHolder(IPersistentClassMapping pcm){
		PropertyDescriptor pd=new PropertyDescriptor("name",Messages.ClassMappingPropertyDescriptorsHolder_NameN); //$NON-NLS-1$
		pd.setDescription(Messages.ClassMappingPropertyDescriptorsHolder_NameD);
		addPropertyDescriptor(pd);
		pd.setCategory(GENERAL_CATEGORY);

		pd=new TextPropertyDescriptor("entityName",Messages.ClassMappingPropertyDescriptorsHolder_EntityNameN); //$NON-NLS-1$
		pd.setDescription(Messages.ClassMappingPropertyDescriptorsHolder_EntityNameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$

		
		//Spec property descriptor: select database
//		pd=new DBTablePropertyDescriptor("databaseTable","Database table",pcm);
//		pd.setDescription("Database tabl");
//		pd.setCategory(GENERAL_CATEGORY);
//		addPropertyDescriptor(pd);
//		setDefaultPropertyValue(pd.getId(),"databaseTable");
	
		/*
		pd=new ListPropertyDescriptor("dynamic","dynamic",OrmConfiguration.BOOLEAN_VALUES);
		pd.setDescription("dynamic");
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_FORCE_DISCRIMINATOR);
		*/
		
		pd=new TextPropertyDescriptor("proxyInterfaceName",Messages.ClassMappingPropertyDescriptorsHolder_ProxyInterfaceNameN); //$NON-NLS-1$
		pd.setDescription(Messages.ClassMappingPropertyDescriptorsHolder_ProxyInterfaceNameD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$
		

		pd=new ListPropertyDescriptor("dynamicInsert",Messages.ClassMappingPropertyDescriptorsHolder_DynamicInsertN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.ClassMappingPropertyDescriptorsHolder_DynamicInsertD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		
		pd=new ListPropertyDescriptor("dynamicUpdate",Messages.ClassMappingPropertyDescriptorsHolder_DynamicUpdateN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.ClassMappingPropertyDescriptorsHolder_DynamicUpdateD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		
		pd=new TextPropertyDescriptor("batchSize",Messages.ClassMappingPropertyDescriptorsHolder_BatchSizeN); //$NON-NLS-1$
		pd.setDescription(Messages.ClassMappingPropertyDescriptorsHolder_BatchSizeD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"1");		 //$NON-NLS-1$

		pd=new ListPropertyDescriptor("selectBeforeUpdate",Messages.ClassMappingPropertyDescriptorsHolder_SelectBeforeUpdateN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.ClassMappingPropertyDescriptorsHolder_SelectBeforeUpdateD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);

		pd=new TextPropertyDescriptor("loaderName",Messages.ClassMappingPropertyDescriptorsHolder_LoaderNameN); //$NON-NLS-1$
		pd.setDescription(Messages.ClassMappingPropertyDescriptorsHolder_LoaderNameD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$
		
		
		pd=new TextPropertyDescriptor("persisterClassName",Messages.ClassMappingPropertyDescriptorsHolder_PersisterClassNameN); //$NON-NLS-1$
		pd.setDescription(Messages.ClassMappingPropertyDescriptorsHolder_PersisterClassNameD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$

		pd=new ListPropertyDescriptor("isAbstract",Messages.ClassMappingPropertyDescriptorsHolder_IsAbstractN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.ClassMappingPropertyDescriptorsHolder_IsAbstractD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);

		pd=new ListPropertyDescriptor("lazy",Messages.ClassMappingPropertyDescriptorsHolder_LazyN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.ClassMappingPropertyDescriptorsHolder_LazyD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);

		pd=new TextPropertyDescriptor("synchronizedTables",Messages.ClassMappingPropertyDescriptorsHolder_SynchronizedTablesN); //$NON-NLS-1$
		pd.setDescription(Messages.ClassMappingPropertyDescriptorsHolder_SynchronizedTablesD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$
		
	}
	
	/*
	 * 
	private String entityName;
	private boolean dynamic = false;
	private String discriminatorValue;
	private ArrayList properties = new ArrayList();
	private String proxyInterfaceName;
	private final ArrayList subclasses = new ArrayList();
	private boolean dynamicInsert;
	private boolean dynamicUpdate;
	private int batchSize=-1;
	private boolean selectBeforeUpdate;
	private String optimisticLockMode;
	private java.util.Map metaAttributes;
	private ArrayList joins = new ArrayList();
	private final java.util.Map filters = new HashMap();
	private String loaderName;
	private boolean isAbstract;
	private boolean lazy;
	private String check;
	//private List checkConstraints = new ArrayList();
	private String rowId;
	private String subselect;
	private String synchronizedTables;
	

	// Custom SQL
	private String customSQLInsert;
	private String customSQLUpdate;
	private String customSQLDelete;
	 * 
	 * 
	 * */

}
