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
package org.jboss.tools.hibernate.wizard.fieldmapping;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.TreeItem;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IOrmElement;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.core.exception.NestableRuntimeException;
import org.jboss.tools.hibernate.core.hibernate.IAnyMapping;
import org.jboss.tools.hibernate.core.hibernate.ICollectionMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateValueMapping;
import org.jboss.tools.hibernate.core.hibernate.IIdBagMapping;
import org.jboss.tools.hibernate.core.hibernate.IIndexedCollectionMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMappingHolder;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.CorrectMappingVisitor;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.PersistentField;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.hibernate.ClassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.CollectionMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ComponentMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.FieldMappingWizardDataContainer;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.IdBagMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.IndexedCollectionMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.JoinMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ManyToManyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.OneToManyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.PropertyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.ColumnBuilderVisitor;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.ConfigurationReader;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMappingHelper;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.IdentifierColumnBuilderVisitor;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.MappingsFactory;
import org.jboss.tools.hibernate.internal.core.util.ClassUtils;
import org.jboss.tools.hibernate.internal.core.util.TypeUtils;
import org.jboss.tools.hibernate.view.ViewPlugin;
import org.jboss.tools.hibernate.view.views.ReadOnlyWizard;



/**
 * @author kaa
 * edit tau 15.02.2006 + ReadOnlyWizard
 * 
 */
public class FieldMappingWizard extends ReadOnlyWizard implements IWizard, IRunnableContext{
	public static final String BUNDLE_NAME = "fieldmapping"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(FieldMappingWizardPage1.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private ResourceBundle BUNDLE_IMAGE = ViewPlugin.BUNDLE_IMAGE;
	
	private FieldMappingWizardPage1 page1;//collection page
	private FieldMappingWizardPage2 page2;//index element page
	private FieldMappingWizardPage3 page3;//element page
	private FieldMappingWizardPage4 page4;//join page
	private FieldMappingWizardPage5 page5;//properties page	
	private IMapping mod;
	private String persistentClassName;
	private IPersistentField field;
	private String FieldType;
	private boolean isBindColumn=false;
	private boolean isClearType=false;	
	private int selectedElement=0;
	private boolean dirty=false;
	private int CollectionSelectedMaping=S_NoMap;
	private IOrmElement parentElement;//it may be one of mappings 
									  //IPersistentField,IpersistentClass,IComponentMapping
	private IWizardPage currentpage;//current page of the wizard
	
    public static final int S_NoMap = 0;	
    public static final int S_BagMapping = 1;
    public static final int S_IdBagMapping = 2;
    public static final int S_ListMapping = 3;
	public static final int S_SetMapping = 4;
	public static final int S_MapMapping = 5;
	public static final int S_ArrayMapping = 6;	
	
    public static final int S_OneToManyMapping = 11;
    public static final int S_SimpleValueMapping = 12;
    public static final int S_ManyToManyMapping = 13;
	public static final int S_ManyToOneMapping = 14;
	public static final int S_OneToOneMapping = 15;	
    public static final int S_ComponentMapping = 16;
    public static final int S_AnyMapping = 17;
    public static final int S_ManyToAnyMapping = 18;
    
	private FieldMappingWizardDataContainer				DataContainer 		= null;
	private boolean isfinish;
	private boolean isinsidecollection=false;
	private boolean isindexcollection=false;
	private boolean isOneToMany=false;
	private boolean isNewMapping;	
	public void addPages() {

		if (mod==null)
		{
			goException("Cant work with null project");
		}
	
		if (persistentClassName==null)
		{
			goException("Cant work with null persistent class name");
		}
		if ((mod.findClass(persistentClassName)==null)||(mod.findClass(persistentClassName).getPersistentClassMapping()==null))
		{
			goException("Cant work with null persistent class mapping");
		}
		
		if (field==null)
		{
			goException("Cant work with null field name");
		}
		FieldType=field.getType();
		if (FieldType==null) {
			if (field.getMapping()!=null)
				{
				if (field.getMapping().getPersistentValueMapping()!=null)
				{
					if (field.getMapping().getPersistentValueMapping() instanceof CollectionMapping) 
					{
						((PersistentField)field).setType("java.util.Collection");
						
					}
					else ((PersistentField)field).setType("java.lang.String");
					isClearType=true;
					setDirty(true);
					FieldType=field.getType();
				}
				else
				{
					CorrectMappingVisitor visitor= new CorrectMappingVisitor();
					field.getMapping().accept(visitor,null);
	            	//TODO (tau-tau) for Exception					
					try {
						// edit tau 29.03.2006
						//mod.findClass(persistentClassName).getPersistentClassMapping().getStorage().save();
						mod.findClass(persistentClassName).getPersistentClassMapping().getStorage().save(true);
					} catch (IOException e) {
						ExceptionHandler.handle(new InvocationTargetException(e), getShell(), null,
								null);
					} catch (CoreException e) {
						ExceptionHandler.handle(e, getShell(), null, null);
					} catch (Throwable e) { 
						ExceptionHandler.logThrowableWarning(e,null); // tau 15.09.2005
					}
					goException("Field "+field.getName()+" in class "+persistentClassName+" has a null type");					
				}
				}
			else 
			{
				goException("Field "+field.getName()+" in class "+persistentClassName+" has a null type");					
			}
		}
		if (parentElement==null) {
			goException("parent element for "+field.getName()+" in class "+persistentClassName+" not exists");					
		}
		
		try
		{
			DataContainer = new FieldMappingWizardDataContainer(mod,field,parentElement,persistentClassName);
		}
		catch(CoreException e)
		{
        	//TODO (tau-tau) for Exception			
			ExceptionHandler.handle(e, getShell(), "Field Mapping Wizard","Error of mapping initialization!");
		}
		if ((parentElement instanceof PersistentField)||(parentElement instanceof ComponentMapping))
		{
			//if parent link
			String ownername=null;
			if (parentElement instanceof PersistentField)
			{
				if ((((PersistentField)parentElement).getMapping()!=null)&&(((PersistentField)parentElement).getMapping().getPersistentValueMapping() instanceof ComponentMapping))
					ownername=((ComponentMapping)((PersistentField)parentElement).getMapping().getPersistentValueMapping()).getParentProperty();
			}
			if (parentElement instanceof ComponentMapping)
			{
				ownername=((ComponentMapping)parentElement).getParentProperty();
			}
			if ((ownername!=null)&&(ownername.equals(field.getName())))
			{
				goException("Field "+field.getName()+" is a parent field and it can't be edit.");					
			}
			//if collection in component
			IPropertyMappingHolder owner = ((PropertyMapping)((PersistentField)field).getMapping()).getPropertyMappingHolder();
			while (owner instanceof ComponentMapping)
			{
				if (((ComponentMapping)owner).getFieldMapping()!=null)
				{
					if (((ComponentMapping)owner).getFieldMapping().getPersistentValueMapping() instanceof ICollectionMapping)
					{
						if (HibernateAutoMappingHelper.getLinkedType(FieldType)!=0)
						{
						goException("One of parent elements for "+field.getName()+" in class "+persistentClassName+" is Collection, so you can't map collection inside colection.");					
						}
						else 
							{
								if (((ComponentMapping)owner).getFieldMapping().getPersistentValueMapping() instanceof IIndexedCollectionMapping)
								{
									if (((ComponentMapping)owner).equals(((IIndexedCollectionMapping)((ComponentMapping)owner).getFieldMapping().getPersistentValueMapping()).getIndex()))
									setIsindexcollection(true);	
								}
								setIsinsidecollection(true);
							}
					}
					owner=((PropertyMapping)((ComponentMapping)owner).getFieldMapping()).getPropertyMappingHolder();	
				}
				else break;
//			else ExceptionHandler.handle(e, getShell(), "Field Mapping Wizard","Error of mapping initialization!");
			}
		}
		if (FieldType.equals("java.util.Collection"))
		{
				page1 = new FieldMappingWizardPage1(mod,persistentClassName,field);		
				addPage(page1);
				
//				page2 = new FieldMappingWizardPage2(mod,persistentClassName,field);
//				addPage(page2);				
		}
		
		page3 = new FieldMappingWizardPage3(mod,persistentClassName,field);
		addPage(page3);

		// edit tau 28.01.2006 / fix ESORM-499
		//if ((FieldType.equals("java.util.Map"))||(FieldType.equals("java.util.SortedMap"))||(FieldType.equals("java.util.List"))||(Signature.getArrayCount(FieldType) != 0)||(FieldType.equals("java.util.Collection")))
				
		if ((FieldType.equals("java.util.Map"))
				||(FieldType.equals("java.util.SortedMap"))
				||(FieldType.equals("java.util.List"))
					||(Signature.getArrayCount(FieldType) != 0 && !ClassUtils.isPrimitiveType(Signature.getElementType(FieldType)))
				||(FieldType.equals("java.util.Collection"))) {
			page2 = new FieldMappingWizardPage2(mod,persistentClassName,field);
			addPage(page2);
		} else {
			page4 = new FieldMappingWizardPage4(mod,persistentClassName,field);
			addPage(page4);
		}
		
		page5 = new FieldMappingWizardPage5(mod,persistentClassName,field);
		addPage(page5);

		ImageDescriptor descriptor = null;
		descriptor = ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("Wizard.Title"));
		setDefaultPageImageDescriptor(descriptor);
	}

	   	public boolean performFinish()
	   	{
	   		page5.SetSQLResaults();
	   		if (!isCorrectMapping()) return false;
	   		if (isClearType) ((PersistentField)field).setType(null);
	   		
        	//TODO (tau-tau) for Exception	   		
			try {
				//edit tau 29.03.2006 
				//mod.findClass(persistentClassName).getPersistentClassMapping().getStorage().save();
				mod.findClass(persistentClassName).getPersistentClassMapping().getStorage().save(true);
				
			} catch (IOException e) {
				ExceptionHandler.handle(new InvocationTargetException(e), getShell(), null,
						null);
			} catch (CoreException e) {
				ExceptionHandler.handle(e, getShell(), null, null);
			} catch (Throwable e) { 
				ExceptionHandler.logThrowableWarning(e,null); // tau 15.09.2005
			}
				return true;				
	   	}
	   	
       private boolean isCorrectMapping() {
	   		if (field.getMapping().getPersistentValueMapping() instanceof ICollectionMapping)
	   		{
	   			if (((ICollectionMapping)field.getMapping().getPersistentValueMapping()).getElement() instanceof IAnyMapping)
	   			{
	   				if (((IAnyMapping)((ICollectionMapping)field.getMapping().getPersistentValueMapping()).getElement()).getColumnSpan()!=2)
	   				{
	   					MessageDialog.openError(ViewPlugin.getActiveWorkbenchShell(), BUNDLE.getString("FieldMappingWizardPage1.title"),BUNDLE.getString("FieldMappingWizardPage.errorManyToAny") );
	   					return false;
	   				}
	   			}
	   		}
	   		else if (field.getMapping().getPersistentValueMapping() instanceof IAnyMapping)
	   			 {
	   				if (((IAnyMapping)field.getMapping().getPersistentValueMapping()).getColumnSpan()!=2)
	   				{
	   					MessageDialog.openError(ViewPlugin.getActiveWorkbenchShell(), BUNDLE.getString("FieldMappingWizardPage1.title"),BUNDLE.getString("FieldMappingWizardPage.errorAny") );
	   					return false;
	   				}
	   			 }
			return true;
		}

       public FieldMappingWizard(IMapping mod, IPersistentClass persistentClass,
			IPersistentField field, TreeItem parentItem, boolean isNewMapping, TreeViewer viewer) {
    	
    	// edit tau 15.02.2066
		super(field, viewer);
		
		setNeedsProgressMonitor(true);
		if (parentItem.getData() instanceof IOrmElement)
			parentElement = (IOrmElement)parentItem.getData();
		else
			parentElement = null;
		this.mod = mod;
		if (persistentClass!=null)
		{
		this.persistentClassName = persistentClass.getName();
		}
		else 
		if ((field!=null) && (field.getOwnerClass()!=null))
		{
			this.persistentClassName=field.getOwnerClass().getName();	
		}
		else this.persistentClassName=null;
		this.field = field;
		this.isNewMapping=isNewMapping;
		this.setWindowTitle(BUNDLE.getString("FieldMappingWizard.title"));
	}  
  
		public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable) throws InvocationTargetException, InterruptedException {
			
		}
		
		public IWizardPage getNextPage(IWizardPage page)
		{
		IWizardPage nextPage = super.getNextPage(page);
		if (page instanceof FieldMappingWizardPage1)
		{
//			try
//			{
//				DoCollectionMapping(page1.getMappingDo());
				CollectionSelectedMaping=page1.getMappingDo();
//			}
//			catch (InvocationTargetException e)
//			{
//				ExceptionHandler.logThrowableInfo(e,null); // tau 15.09.2005
//			}
			}
		else
		if (page instanceof FieldMappingWizardPage2)
		{
        	//TODO (tau-tau) for Exception			
					try {
						DoKeyMapping(page2.getMappingDo());
					} catch (CoreException e) {
						ExceptionHandler.logThrowableWarning(e,null); // tau 15.09.2005
					} catch (InvocationTargetException e) {
						ExceptionHandler.logThrowableWarning(e,null); // tau 15.09.2005
					}
		}
		else			
		if (page instanceof FieldMappingWizardPage3)
		{
			try {
				selectedElement=page3.getMappingDo();				
				DoMapping(selectedElement);
			} catch (InvocationTargetException e) {
				ExceptionHandler.handle(e, getShell(), null, null);
			}
			if ((field.getMapping()!=null) && 
					((field.getMapping().getPersistentValueMapping() instanceof ICollectionMapping)) &&					
					(!(field.getMapping().getPersistentValueMapping() instanceof IIndexedCollectionMapping)) &&
					(!(field.getMapping().getPersistentValueMapping() instanceof IIdBagMapping)))					
			{
				nextPage=super.getNextPage(page2);
			}
			
			if ((!page3.isJoinedTable())&&(page2==null))
				nextPage = super.getNextPage(page4);
			page3.Refresh();
		}
		
			if (isfinish)
			{
				currentpage=nextPage;
				if (page3.isJoinedTable()) isfinish=false;
			}
			else 
				{
				currentpage=null;
				if (!page3.isJoinedTable()) isfinish=false;
				}
		
				if (nextPage instanceof FieldMappingWizardPage5)
				{
					isfinish=true;
					if (isBindColumn) doBindcolumn();					
				}
				else if (nextPage==null)
					{
//						isfinish=false;
						page5.changeTabs();
						page5.FormList();					
					 }
//				else isfinish=false;
		return nextPage;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.wizard.Wizard#canFinish()
		 */
		public boolean canFinish() {
			// edit tau 15.02.2006
 			boolean result = true;			
			
			if (currentpage instanceof FieldMappingWizardPage5)
				result = super.canFinish();
			else result = false;
			
 			// add tau 13.02.2006
 			// for ESORM-513 Overwrites and changes our Hibernate mapping files, even if they are marked read-only?		
 			if (this.readOnly) result = false;
			
			return result;			
		}

		private void DoMapping(int SelectedMaping) throws InvocationTargetException
		{
			if (SelectedMaping==S_OneToManyMapping)
				isOneToMany=true;
			else isOneToMany=false;
			if (SelectedMaping==S_NoMap) return;
			try
			{
				setDirty(true);
			if (HibernateAutoMappingHelper.getLinkedType(FieldType)!=0)
				{
					if (CollectionSelectedMaping!=S_NoMap)
						DoCollectionMapping(CollectionSelectedMaping);
					else
					if (field.getMapping().getPersistentValueMapping()==null)
					{
						if (Signature.getArrayCount(FieldType) != 0) 
								DoCollectionMapping(S_ArrayMapping);
						else
						if (FieldType.equals("java.util.List"))
								DoCollectionMapping(S_ListMapping);
						else
						if (FieldType.equals("java.util.SortedSet") || FieldType.equals("java.util.Set"))
								DoCollectionMapping(S_SetMapping);						
							
						else
								DoCollectionMapping(S_MapMapping);	
					}
					
					if (field.getMapping().getPersistentValueMapping() instanceof CollectionMapping)
					{

					((CollectionMapping)field.getMapping().getPersistentValueMapping()).setElement((IHibernateValueMapping) AttemptDoMapping(SelectedMaping));
					if (isOneToMany)
						{
							if ((((OneToManyMapping)((CollectionMapping)field.getMapping().getPersistentValueMapping()).getElement()).getReferencedEntityName()!=null)
								&&(mod.findClass(((OneToManyMapping)((CollectionMapping)field.getMapping().getPersistentValueMapping()).getElement()).getReferencedEntityName())!=null)
								&&(mod.findClass(((OneToManyMapping)((CollectionMapping)field.getMapping().getPersistentValueMapping()).getElement()).getReferencedEntityName()).getPersistentClassMapping()!=null)
								&&(mod.findClass(((OneToManyMapping)((CollectionMapping)field.getMapping().getPersistentValueMapping()).getElement()).getReferencedEntityName()).getPersistentClassMapping().getDatabaseTable()!=null))
									((CollectionMapping)field.getMapping().getPersistentValueMapping()).setCollectionTable(mod.findClass(((OneToManyMapping)((CollectionMapping)field.getMapping().getPersistentValueMapping()).getElement()).getReferencedEntityName()).getPersistentClassMapping().getDatabaseTable());
							else
									((CollectionMapping)field.getMapping().getPersistentValueMapping()).setCollectionTable(((CollectionMapping)field.getMapping().getPersistentValueMapping()).getElement().getTable());
						}
					((CollectionMapping)field.getMapping().getPersistentValueMapping()).setKey((SimpleValueMapping)AttemptDoMapping(S_SimpleValueMapping));
//					08.09.2005 ORMIISTUD-744
					((CollectionMapping)field.getMapping().getPersistentValueMapping()).getElement().setFieldMapping(null);
					((CollectionMapping)field.getMapping().getPersistentValueMapping()).getKey().setFieldMapping(null);
					((CollectionMapping)field.getMapping().getPersistentValueMapping()).getElement().setType(Type.getOrCreateType(ConfigurationReader.getAutomappingConfigurationInstance((HibernateMapping)mod).getCollectionElementsType()));
					((CollectionMapping)field.getMapping().getPersistentValueMapping()).getKey().setType(null);
					}
//TODO akuzmin: do something if this is not collection or if it null					
				}
			else field.getMapping().setPersistentValueMapping(AttemptDoMapping(SelectedMaping));
			if ((Signature.getArrayCount(FieldType) != 0)&&(!FieldType.equals(MappingsFactory.BLOB_SIGNATURE))) 
			{
				// edit tau 28.01.2066
				//if (((IndexedCollectionMapping)field.getMapping().getPersistentValueMapping()).getElement()!=null)
				if ((field.getMapping().getPersistentValueMapping() instanceof IndexedCollectionMapping) &&
						((IndexedCollectionMapping)field.getMapping().getPersistentValueMapping()).getElement()!=null){
					((IndexedCollectionMapping)field.getMapping().getPersistentValueMapping()).getElement().setType(TypeUtils.javaTypeToHibType(Signature.getElementType(FieldType)));
				}
	
			}

			page3.Refresh();
			}
			catch (CoreException e)
			{
				throw new InvocationTargetException(e);
			}
		}
		
		private IPersistentValueMapping AttemptDoMapping(int mapping_id) throws CoreException
		{
			IPersistentValueMapping mapping = null;

	        switch (mapping_id)
	        {
	        	case (S_OneToManyMapping):
					mapping = DataContainer.createOneToManyMapping();
					break;
	        	case (S_SimpleValueMapping):
					mapping = DataContainer.createSimpleValueMapping();
					break;
	        	case (S_ManyToManyMapping):
					mapping = DataContainer.createManyToManyMapping();
					break;
	        	case (S_ManyToOneMapping):
					mapping = DataContainer.createManyToOneMapping();
					break;
	        	case (S_OneToOneMapping):
					mapping = DataContainer.createOneToOneMapping();
					break;
	        	case (S_ComponentMapping):
					mapping = DataContainer.createComponentMapping();
					break;
	        	case (S_AnyMapping):
					mapping = DataContainer.createAnyMapping();
					break;
	        	case (S_ManyToAnyMapping):
					mapping = DataContainer.createManyToAnyMapping();
					break;
	        	case (S_BagMapping):
					mapping = DataContainer.createBagMapping(!isOneToMany);
					break;
	        	case (S_IdBagMapping):
					mapping = DataContainer.createIdBagMapping(!isOneToMany);
					break;
	        	case (S_ListMapping):
					mapping = DataContainer.createListMapping(!isOneToMany);
					break;
	        	case (S_SetMapping):
					mapping = DataContainer.createSetMapping(!isOneToMany);
					break;
	        	case (S_MapMapping):
					mapping = DataContainer.createMapMapping(!isOneToMany);
					break;
	        	case (S_ArrayMapping):
					mapping = DataContainer.createArrayMapping(!isOneToMany);
					break;					
				default: ;
	        }
	        isBindColumn=true;
	        
	        // #added# by Konstantin Mishin on 23.12.2005 fixed for ESORM-411
	        if (mapping instanceof ManyToManyMapping) {
	        	SimpleValueMapping simpleValueMapping = (SimpleValueMapping)mapping;
	        	if (simpleValueMapping.getColumnSpan() == 0) {
	        		Column col = new Column();
	        		col.setPersistentValueMapping(simpleValueMapping);
	        		col.setName(OrmConfiguration.DEFAULT_ELEMENT_COLUMN_NAME);
	        		col.setNullable(false);
	        		col.setOwnerTable(simpleValueMapping.getTable());
	        		if(simpleValueMapping.getTable() != null)
	        			simpleValueMapping.getTable().addColumn(col);
	        		simpleValueMapping.addColumn(col);
	        	}
	        } else if (mapping instanceof OneToManyMapping) {
	        	OneToManyMapping oneToManyMapping = (OneToManyMapping)mapping;
	        	IPersistentClass persClass = mod.findClass(oneToManyMapping.getReferencedEntityName());
	        	if ( persClass != null )
	        		oneToManyMapping.setAssociatedClass((IHibernateClassMapping) persClass.getPersistentClassMapping());
	        }
	        // #added#
	        
	        return mapping;
		}
		
		private void DoCollectionMapping(int SelectedMaping) throws InvocationTargetException 
		{
			CollectionSelectedMaping=S_NoMap;
			if (SelectedMaping==S_NoMap) return;
			try
			{
			setDirty(true);
		field.getMapping().setPersistentValueMapping(AttemptDoMapping(SelectedMaping));
		if (page1!=null)
		page1.Refresh();
		if (page2!=null)
		page2.Refresh();
//		if (page3.getMappingDo()!=S_NoMap)
		page3.Refresh();		
			
            // added by Nick 29.08.2005
		    if (TypeUtils.SORTED_COLLECTIONS.contains(field.getType()) && field.getMapping().getPersistentValueMapping() instanceof CollectionMapping)
            {
		        CollectionMapping collection = (CollectionMapping) field.getMapping().getPersistentValueMapping();
                if (collection.getSort() == null || collection.getSort().equals("unsorted"))
                {
                    collection.setSort("natural");
                }
            }
            // by Nick
            
            }
			catch(CoreException e)
			{
				throw new InvocationTargetException(e);
			}
		}

		private void DoKeyMapping(int SelectedMaping) throws CoreException, InvocationTargetException {
			if (SelectedMaping==S_NoMap) return;
			setDirty(true);
			if (field.getMapping()==null)
			{
				HibernateAutoMappingHelper helper =new HibernateAutoMappingHelper(ConfigurationReader.getAutomappingConfigurationInstance((HibernateMapping)mod));
				helper.createAndBindPropertyMapping(field);
			}
			
			if (field.getMapping().getPersistentValueMapping() instanceof IIndexedCollectionMapping)
			{
							((IndexedCollectionMapping)field.getMapping().getPersistentValueMapping()).setIndex((IHibernateKeyMapping) AttemptDoMapping(SelectedMaping));
							//08.09.2005 ORMIISTUD-744
							((IndexedCollectionMapping)field.getMapping().getPersistentValueMapping()).getIndex().setFieldMapping(null);
							((IndexedCollectionMapping)field.getMapping().getPersistentValueMapping()).getIndex().setType(Type.getType(OrmConfiguration.DEFAULT_ID_DATATYPE));
							
//							if (Signature.getArrayCount(FieldType) != 0) 
//							{
//								((IndexedCollectionMapping)field.getMapping().getPersistentValueMapping()).getIndex().setType(Type.getType(OrmConfiguration.DEFAULT_ID_DATATYPE));	
//							}
			}
			else 
			if (field.getMapping().getPersistentValueMapping() instanceof IIdBagMapping)
			{
				((IIdBagMapping)field.getMapping().getPersistentValueMapping()).setIdentifier((IHibernateKeyMapping) AttemptDoMapping(SelectedMaping));
				//08.09.2005 ORMIISTUD-744
				((IIdBagMapping)field.getMapping().getPersistentValueMapping()).getIdentifier().setFieldMapping(null);
				((IdBagMapping)field.getMapping().getPersistentValueMapping()).getIdentifier().setType(Type.getType(OrmConfiguration.DEFAULT_ID_DATATYPE));
				
			}
			page2.Refresh();			
		}

	    public IWizardPage getStartingPage() {
		   if ((field.getMapping()!=null)&&(!isNewMapping))
		   {
	       if ((TypeUtils.javaTypeToHibType(FieldType)!=null) || (mod.findClass(FieldType)!=null))//primitive or PersistentClass
		   {
			   if (field.getMapping().getPersistentValueMapping()!=null)
			   {
				   currentpage=page4.getNextPage();
				   return page4.getNextPage();
			   }
		   }
	       else
		   if (field.getMapping().getPersistentValueMapping()!=null)
		   {
				   if (field.getMapping().getPersistentValueMapping() instanceof ICollectionMapping)
				   {   
					   if (((ICollectionMapping)field.getMapping().getPersistentValueMapping()).getElement()!=null)
					   {
						   if  (field.getMapping().getPersistentValueMapping() instanceof IIndexedCollectionMapping)
						   {
							   if (((IIndexedCollectionMapping)field.getMapping().getPersistentValueMapping()).getIndex()!=null)
							   {
								   currentpage=page2.getNextPage();
								   return page2.getNextPage();
							   }

						   }
						   else
						   if  (field.getMapping().getPersistentValueMapping() instanceof IIdBagMapping)
						   {
							   if (((IIdBagMapping)field.getMapping().getPersistentValueMapping()).getIdentifier()!=null)
							   {
								   currentpage=page2.getNextPage();
								   return page2.getNextPage();
							   }

						   }
						   else 
						   {
							   currentpage=page3.getNextPage();
							   return page3.getNextPage();
						   }

					   }
				   }
				   else 
				   {
					   currentpage=page4.getNextPage();
					   return page4.getNextPage();
				   }

		   }

		   }
		   if (!page3.isJoinedTable()) isfinish=true;
		   return super.getStartingPage();
	    }		

	    public IWizardPage getPreviousPage(IWizardPage page) {
			currentpage=null;
			if (page3.isJoinedTable()) isfinish=false;
			
//	    		if (page instanceof FieldMappingWizardPage4)
//	    		{
//
//	    		}
//				if (page instanceof FieldMappingWizardPage3)
//				{
//
//					if ((field.getMapping()!=null) && 
//							(!(field.getMapping().getPersistentValueMapping() instanceof IIndexedCollectionMapping)) &&
//							(!(field.getMapping().getPersistentValueMapping() instanceof IIdBagMapping)))
//						
//						return super.getPreviousPage(super.getPreviousPage(page)); 
//				}
				if (page instanceof FieldMappingWizardPage5)
				{
					if (field.getMapping()!=null)
					{
							if	(((field.getMapping().getPersistentValueMapping() instanceof IIndexedCollectionMapping)) ||
							    ((field.getMapping().getPersistentValueMapping() instanceof IIdBagMapping)))
								return super.getPreviousPage(page);
					if ((field.getMapping().getPersistentValueMapping() instanceof ICollectionMapping))
								return super.getPreviousPage(super.getPreviousPage(page));					
					}
					if (!page3.isJoinedTable())
						return super.getPreviousPage(super.getPreviousPage(page));
				}
	            return super.getPreviousPage(page);
	    }
		
		private void doBindcolumn()
		{
			
			ClassMapping ownerclass=null;
			SimpleValueMapping candidate=null;
			if (field.getOwnerClass().getPersistentClassMapping() instanceof ClassMapping)
			  ownerclass=(ClassMapping) field.getOwnerClass().getPersistentClassMapping();
			if (((parentElement instanceof PersistentField))&&((PersistentField)parentElement).getMapping().getPersistentValueMapping()instanceof SimpleValueMapping)
				candidate=(((SimpleValueMapping)((PersistentField)parentElement).getMapping().getPersistentValueMapping()));
			if ((parentElement instanceof PersistentField)&&(ownerclass!=null)&&(ownerclass.getIdentifier()!=null)&&(((SimpleValueMapping)ownerclass.getIdentifier()).equals(candidate)))
			{
				setDirty(true);
				IdentifierColumnBuilderVisitor visitor=new IdentifierColumnBuilderVisitor((HibernateMapping)mod);
				field.accept(visitor,null);
			}
			else
			{
				setDirty(true);
				ColumnBuilderVisitor visitor = new ColumnBuilderVisitor((HibernateMapping)mod);
				field.accept(visitor,null);
			}
//			if (field.getMapping().getPersistentValueMapping() instanceof CollectionMapping)
//					{
//						if (((CollectionMapping)field.getMapping().getPersistentValueMapping()).getCollectionTable()!=null)
//						{
//							field.accept(visitor,null);
//						}
//					}
//			else
//			{
			
//			}
			isBindColumn=false;			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.wizard.Wizard#performCancel()
		 */
		public boolean performCancel() {
			
        	//TODO (tau-tau) for Exception			
			try {
				if (isDirty())
				{
					mod.findClass(persistentClassName).getPersistentClassMapping().getStorage().reload();
					mod.refresh(false, true);  // edit tau 17.11.2005
				}
				else if ((field.getMapping()!=null)&&(field.getMapping().getPersistentValueMapping()==null))
				{
					field.setMapping(null);
				}
			} catch (IOException e) {
				ExceptionHandler.logThrowableWarning(e,null); // tau 15.09.2005
			} catch (CoreException e) {
				ExceptionHandler.logThrowableWarning(e,null); // tau 15.09.2005
			} catch (Throwable e) { 
				ExceptionHandler.logThrowableWarning(e,null); // tau 15.09.2005
			}
				return super.performCancel();				
		}

		public void buildNonJoinMapping() {
	        HibernateAutoMappingHelper helper = new HibernateAutoMappingHelper(
	                ConfigurationReader
	                        .getAutomappingConfigurationInstance((HibernateMapping) mod));
            helper.createAndBindPropertyMapping(field,parentElement);			
			try {
				if (selectedElement==0) selectedElement=S_SimpleValueMapping;
				DoMapping(selectedElement);
			} catch (InvocationTargetException e) {
            	//TODO (tau-tau) for Exception				
				ExceptionHandler.handle(e, getShell(), null, null);
			}
		}

		public SimpleValueMapping buildJoinMappingKey() throws CoreException {
			return (SimpleValueMapping)AttemptDoMapping(S_SimpleValueMapping);			
		}

		public JoinMapping buildJoinMapping(IDatabaseTable table, String[] columnName,SimpleValueMapping key) {
			IPersistentFieldMapping old_mapping=field.getMapping();
			old_mapping.setPersistentValueMapping(null);
			JoinMapping join = new JoinMapping();
			join.setTable(table);
			try {
				DoMapping(S_SimpleValueMapping);
				join.addProperty((IPropertyMapping) old_mapping);
				join.setKey(key);
			} catch (InvocationTargetException e) {
				ExceptionHandler.handle(e, getShell(), null, null);
			}
			key = (SimpleValueMapping)join.getKey();
//			new SimpleValueMapping(mod.findClass(persistentClassName).getPersistentClassMapping().getDatabaseTable());
			if (columnName.length>0) //ComponentMapping
			{
				for(int i=0;i<columnName.length;i++)
				{
					if (mod.findClass(persistentClassName).getPersistentClassMapping().getDatabaseTable().getColumn(columnName[i])!=null)
					key.addColumn(mod.findClass(persistentClassName).getPersistentClassMapping().getDatabaseTable().getColumn(columnName[i]));
				}
			}
//			join.setKey(key);
			join.setPersistentClass((IHibernateClassMapping) mod.findClass(persistentClassName).getPersistentClassMapping());
			((IHibernateClassMapping) mod.findClass(persistentClassName).getPersistentClassMapping()).addJoin(join);
			((ClassMapping)field.getOwnerClass().getPersistentClassMapping()).removeProperty((PropertyMapping) old_mapping);			
			
			field.setMapping(old_mapping);

			return join;
		}

		/**
		 * @param isfinish The isfinish to set.
		 */
		public void setIsfinish(boolean isfinish) {
			this.isfinish = isfinish;
		}

		/**
		 * @return Returns the isinsidecollection.
		 */
		public boolean isIsinsidecollection() {
			return isinsidecollection;
		}

		/**
		 * @param isinsidecollection The isinsidecollection to set.
		 */
		public void setIsinsidecollection(boolean isinsidecollection) {
			this.isinsidecollection = isinsidecollection;
		}

		/**
		 * @return Returns the isindexcollection.
		 */
		public boolean isIsindexcollection() {
			return isindexcollection;
		}

		/**
		 * @param isindexcollection The isindexcollection to set.
		 */
		public void setIsindexcollection(boolean isindexcollection) {
			this.isindexcollection = isindexcollection;
		}
		
		private void goException(String message)
		{
			if ((field!=null)&&(field.getMapping()!=null)&&(field.getMapping().getPersistentValueMapping()==null)) field.setMapping(null);
			RuntimeException myException = new NestableRuntimeException(message);
			ExceptionHandler.displayMessageDialog(myException, ViewPlugin.getActiveWorkbenchShell(), "Field Mapping Wizard", null);
			throw myException;
			
		}

		/**
		 * @return Returns the dirty.
		 */
		public boolean isDirty() {
			if (page4!=null)
				return dirty||page4.isDirty()||page5.getDirty();
			else
				return dirty||page5.getDirty();
		}

		/**
		 * @param dirty The dirty to set.
		 */
		public void setDirty(boolean dirty) {
			this.dirty = dirty;
		}
		
}
