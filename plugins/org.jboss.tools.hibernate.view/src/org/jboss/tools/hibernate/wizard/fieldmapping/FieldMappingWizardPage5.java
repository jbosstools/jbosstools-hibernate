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

import java.util.ResourceBundle;

import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.views.properties.PropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.core.hibernate.IAnyMapping;
import org.jboss.tools.hibernate.core.hibernate.IComponentMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateValueMapping;
import org.jboss.tools.hibernate.core.hibernate.IIndexedCollectionMapping;
import org.jboss.tools.hibernate.core.hibernate.IMapMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.hibernate.CollectionMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.IdBagMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.OneToManyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMappingHelper;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.CombinedBeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertySheetPageWithDescription;


/**
 * @author akuzmin - akuzmin@exadel.com
 *
 * 
 */
public class FieldMappingWizardPage5 extends WizardPage {
	public static final String BUNDLE_NAME = "fieldmapping"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(FieldMappingWizardPage5.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private IMapping mod;
	private String persistentClassName;
	private IPersistentField field;
	private PropertySheetPage collectionpage,fkpage;
	private IPersistentValueMapping elementmapping;
	private TabItem item_element,item_collection,item_index,item_fk,item_sql;
	private TabFolder folder;
	private FieldMappingSQLEditor SQLpage;
	private FieldMappingComponentEditor CEforElem,CEforKey; 
	private FieldMappingAnyEditor AEforElem;
	private FieldMappingIdentifierEditor IdBagforIndex;
	private FieldMappingSimpleEditor elempage,keypage;	
	private BeanPropertySourceBase fkbp;
	private CombinedBeanPropertySourceBase colectionbp;
	private boolean isNormalTabs=true;
	private boolean dirty=false;
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		//layout.numColumns = 1;
		layout.marginHeight=0;
		layout.horizontalSpacing=0;
		layout.verticalSpacing = 5;
		container.setLayout(layout);
	    folder = new TabFolder(container, SWT.COLOR_INFO_BACKGROUND);
	    GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3);
		//data.verticalSpan = 3;
	    data.widthHint = 570;
	    data.heightHint=350;		
		folder.setLayoutData(data);
		folder.addSelectionListener(new SelectionAdapter(){

			public void widgetSelected(SelectionEvent e) {
				if (folder.getChildren().length>1)
					{
						if ((((Composite)folder.getItem(0).getControl())!=null)
						// #added# by Konstantin Mishin on 19.12.2005 fixed for ESORM-412
						&&(((Composite)folder.getItem(0).getControl()).getChildren()[0]	instanceof Tree)						
						// #added#
						&&(((Tree)((Composite)folder.getItem(0).getControl()).getChildren()[0]).getSelection().length>0)
						&&((TreeItem)((Tree)((Composite)folder.getItem(0).getControl()).getChildren()[0]).getSelection()[0]).getData() instanceof PropertySheetEntry)
						{
							((PropertySheetEntry)((TreeItem)((Tree)((Composite)folder.getItem(0).getControl()).getChildren()[0]).getSelection()[0]).getData()).applyEditorValue();
							int index=folder.getSelectionIndex();
							if (folder.getItem(0).getControl() instanceof FieldMappingSimpleEditor)
							{
								if (elempage.getBps().isRefresh())
								{
									ElemantRefresh();
									folder.setSelection(index);
								}
							}
							else
							{
								if (colectionbp.isRefresh())
								{
									CollectionRefresh();
									folder.setSelection(index);
								}
							}
						}
					}
				super.widgetSelected(e);
			}
			
		});
		String FieldType;
		FieldType=field.getType().toString();
		if (HibernateAutoMappingHelper.getLinkedType(FieldType)!=0)
		{
			createCollectionView(folder);			
			createFKView(folder);			
			createIndexView(folder);
			
		}
		
		createElementView(folder);
		if (HibernateAutoMappingHelper.getLinkedType(FieldType)!=0)
		{
			createSQLView(folder);
		}		
		
		setControl(container);		
	}
	public FieldMappingWizardPage5(IMapping mod, String persistentClassName, IPersistentField field){
		super("FieldMappingWizard");
		setTitle(BUNDLE.getString("FieldMappingWizardPage5.title"));
		this.persistentClassName=persistentClassName;
		this.field=field;
		setDescription(BUNDLE.getString("FieldMappingWizardPage5.description"));
		this.mod = mod;
	}
	
	public void FormList()
	{
		IPersistentFieldMapping mapping=field.getMapping();
		String MappingType;
		if (mapping!=null)
		{
			
			String FieldType=field.getType().toString();
			if (HibernateAutoMappingHelper.getLinkedType(FieldType)!=0)
			{
				if (field.getMapping().getPersistentValueMapping() instanceof CollectionMapping)
				{
					formCollectionView();
					SQLpage.FormList((CollectionMapping)mapping.getPersistentValueMapping());
				}
				else 
					{//do not show collection page property
					item_collection.getControl().setEnabled(false);
					collectionpage.createControl(this.getShell().getParent());
					fkpage.createControl(this.getShell().getParent());
					elementmapping=null;
					}
			}
			else
			{
				elementmapping=mapping.getPersistentValueMapping();
			}

		if (elementmapping==null)
		{//do not show element page property
			MappingType="No mapping";
		}
		else
		{//show element page property
			MappingType=formElementView();

			
			if ((HibernateAutoMappingHelper.getLinkedType(FieldType)!=0)&&
				(field.getMapping().getPersistentValueMapping() instanceof CollectionMapping))
					if (elementmapping instanceof OneToManyMapping)
					{
						elempage.setCatch(false);
						elempage.getPage().getControl().addMouseListener(
								new MouseAdapter(){

									public void mouseDown(MouseEvent e) {
										if (elempage.getBps().isRefresh())
										{
											ElemantRefresh();
										}
									}});
					}
					else
					{
						elempage.setCatch(true);
						collectionpage.getControl().addMouseListener(
								new MouseAdapter(){

							public void mouseDown(MouseEvent e) {
								if (colectionbp.isRefresh())
								{
									CollectionRefresh();
								}
								
							}});
					}
			
		}
		
		}
		else 
		{//do not show element page property
			MappingType="No mapping";
		}

		item_element.setText(MappingType);
	}
	
	private void repaintIndex(IPersistentValueMapping persistentValueMapping) {
		Composite root = (Composite) this.getControl();
		TabFolder tf=(TabFolder) root.getChildren()[0];
		int itemindex=tf.indexOf(item_index);
		if (persistentValueMapping instanceof IdBagMapping)//add index tab if we haven't
		{
			if (itemindex<0)
			{
			    item_index = new TabItem(tf, SWT.NONE);
		        item_index.setText(BUNDLE.getString("FieldMappingWizardPage5.item_index"));
				setControl(root);
			}
		}
		else//remove index tab if we have it
		{
			if (itemindex>=0)
			{
				tf.getItem(itemindex).dispose();
				setControl(root);
			}			
		}
	}

	public void changeTabs() {
		//determinate if we need to change tabs sequarence(if we have callection with one-to-many)
		IPersistentFieldMapping mapping=field.getMapping();
		if ((mapping!=null)
			&&(field.getMapping()!=null)
			&&(HibernateAutoMappingHelper.getLinkedType(field.getType())!=0)
			&&(field.getMapping().getPersistentValueMapping() instanceof CollectionMapping)
			&&(((CollectionMapping)mapping.getPersistentValueMapping()).getElement() instanceof OneToManyMapping))
		{
			if (isNormalTabs)
			createOneToManyTabs();
		}
		else
			if (!isNormalTabs)
				createNormalTabs();
	}
	
	
	public void SetSQLResaults()
	{	
		if (SQLpage!=null)
		SQLpage.SetResaults();
	}

	public void formCollectionView()
	{
		IPersistentFieldMapping mapping=field.getMapping();
		String FieldType=field.getType().toString();
		if (FieldType.equals("java.util.Collection"))
			repaintIndex(field.getMapping().getPersistentValueMapping());
		colectionbp=(CombinedBeanPropertySourceBase) ((IPropertyMapping)mapping).getPropertySource(mapping.getPersistentValueMapping());
		collectionpage.selectionChanged(null, new StructuredSelection(colectionbp));
		String MappingType=mapping.getPersistentValueMapping().getClass().getName();
		MappingType=MappingType.substring(MappingType.lastIndexOf(".")+1,MappingType.length());
		MappingType=BUNDLE.getString("FieldMappingWizardPage5.item_collection")+" "+BUNDLE.getString("FieldMappingWizardPage5."+MappingType);
		item_collection.setText(MappingType);
		fkbp=(BeanPropertySourceBase) ((CollectionMapping)mapping.getPersistentValueMapping()).getFKPropertySource();
		fkpage.selectionChanged(null, new StructuredSelection(fkbp));
		//show key page property
		if ((field.getMapping().getPersistentValueMapping() instanceof IMapMapping)||(field.getMapping().getPersistentValueMapping() instanceof IdBagMapping))
		{
			IHibernateValueMapping key=null;
			if (field.getMapping().getPersistentValueMapping() instanceof IIndexedCollectionMapping)
			{
				key=((IIndexedCollectionMapping)mapping.getPersistentValueMapping()).getIndex();
				item_index.setText(BUNDLE.getString("FieldMappingWizardPage5.item_index"));
				
				if (key.getType()==null) key.setType(Type.getOrCreateType("string")); 
			}
			
			if (field.getMapping().getPersistentValueMapping()instanceof IdBagMapping)
			{
				IdBagforIndex.FormList((IdBagMapping)mapping.getPersistentValueMapping());
				item_index.setText(BUNDLE.getString("FieldMappingWizardPage5.item_identifier"));
				item_index.setControl(IdBagforIndex);						
			}
			else
			if (key instanceof IComponentMapping)
			{
				CEforKey.FormList((IComponentMapping)key,(IPropertyMapping)mapping);						
				item_index.setControl(CEforKey);
			}
			else
			{
				keypage.FormList((IPersistentValueMapping)key,(IPropertyMapping)mapping);						
				item_index.setControl(keypage);
			}
		}

		
		elementmapping=((CollectionMapping)mapping.getPersistentValueMapping()).getElement();
	}
	
	public String formElementView()
	{
		IPersistentFieldMapping mapping=field.getMapping();
		String FieldType=field.getType().toString();
		
		String MappingType=elementmapping.getClass().getName();
		MappingType=MappingType.substring(MappingType.lastIndexOf(".")+1,MappingType.length());
		MappingType=BUNDLE.getString("FieldMappingWizardPage5."+MappingType);
		if ((FieldType.equals("java.util.Collection")) || (FieldType.equals("java.util.List"))|| (FieldType.equals("java.util.Set"))|| (FieldType.equals("java.util.SortedSet"))||(FieldType.equals("java.util.Map"))||(FieldType.equals("java.util.SortedMap"))||(Signature.getArrayCount(FieldType) != 0))
			MappingType=BUNDLE.getString("FieldMappingWizardPage5.item_element")+" - "+MappingType;
		if (elementmapping instanceof IComponentMapping)
		{
			CEforElem.FormList((IComponentMapping)elementmapping,(IPropertyMapping)mapping);						
			item_element.setControl(CEforElem);
		}
		else 
			if (elementmapping instanceof IAnyMapping)
			{
				AEforElem.FormList((IAnyMapping)elementmapping,(IPropertyMapping)mapping);						
				item_element.setControl(AEforElem);
			}
			else
			{
					elempage.FormList(elementmapping,(IPropertyMapping)mapping);
					item_element.setControl(elempage);
			}
		item_element.getParent().setSelection(0);
		return MappingType;
	}

	private void createFKView(TabFolder tf)
	{
		GridLayout layout = new GridLayout(1,false);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		data.grabExcessHorizontalSpace=true;
		data.grabExcessVerticalSpace=true;
		
		item_fk = new TabItem(tf, SWT.NONE);
		item_fk.setText((BUNDLE.getString("FieldMappingWizardPage5.item_fk")));
		Composite composite2 = new Composite(folder, SWT.NONE);
		composite2.setLayout(layout);

		fkpage=new PropertySheetPageWithDescription();			
		fkpage.createControl(composite2);
		
        fkpage.getControl().setSize(560,1260);
        composite2.setSize(550,20);
        item_fk.setControl(composite2);
		fkpage.getControl().setLayoutData(data);
	}
	
	private void createCollectionView(TabFolder tf)
	{
		GridLayout layout = new GridLayout(1,false);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		data.grabExcessHorizontalSpace=true;
		data.grabExcessVerticalSpace=true;
		
		item_collection = new TabItem(folder, SWT.NONE);
        item_collection.setText((BUNDLE.getString("FieldMappingWizardPage5.item_collection")));
		Composite composite = new Composite(folder, SWT.NONE);
		layout = new GridLayout(1,false);
		composite.setLayout(layout);

		collectionpage=new PropertySheetPageWithDescription();			
		collectionpage.createControl(composite);
		
		collectionpage.getControl().setSize(545,1260);
        composite.setSize(550,20);
		item_collection.setControl(composite);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		data.grabExcessHorizontalSpace=true;
		data.grabExcessVerticalSpace=true;
		collectionpage.getControl().setLayoutData(data);

	}
	
	private void createSQLView(TabFolder tf)
	{
		item_sql = new TabItem(tf, SWT.NONE);		
		item_sql.setText((BUNDLE.getString("FieldMappingWizardPage5.item_sql")));
		SQLpage=new FieldMappingSQLEditor(folder);
		item_sql.setControl(SQLpage);
		
	}
	
	private void createElementView(TabFolder tf)
	{
	    item_element = new TabItem(tf,SWT.NONE );
		elempage=new FieldMappingSimpleEditor(tf,mod,persistentClassName);
		CEforElem = new FieldMappingComponentEditor(tf,mod,persistentClassName);
		CEforKey = new FieldMappingComponentEditor(tf,mod,persistentClassName);
		AEforElem = new FieldMappingAnyEditor(tf,mod,persistentClassName);
	}

	private void createIndexView(TabFolder tf)
	{
		IdBagforIndex=new FieldMappingIdentifierEditor(folder);
		if ((field.getType().equals("java.util.Map"))||(field.getType().equals("java.util.SortedMap"))||(field.getType().equals("java.util.Collection")))
		{
		    item_index = new TabItem(folder, SWT.NONE);
	        item_index.setText(BUNDLE.getString("FieldMappingWizardPage5.item_index"));
	        keypage=new FieldMappingSimpleEditor(folder,mod,persistentClassName);
		}			
	}
	
	private void createOneToManyTabs()
	{
		Composite root = (Composite) this.getControl();
		TabFolder tf=(TabFolder) root.getChildren()[0];
		
//		private TabItem item_element,item_collection,item_index,item_fk,item_sql;
		int itemindex;
		if (item_index!=null)
		{
			itemindex=tf.indexOf(item_index);
			if (itemindex>=0)
				tf.getItem(itemindex).dispose();
		}
		
//		itemindex=tf.indexOf(item_element);		
//		tf.getItem(itemindex).dispose();

		itemindex=tf.indexOf(item_collection);		
		tf.getItem(itemindex).dispose();

		itemindex=tf.indexOf(item_fk);		
		tf.getItem(itemindex).dispose();
		
		itemindex=tf.indexOf(item_sql);		
		tf.getItem(itemindex).dispose();
		
//		createElementView(tf);		
		createIndexView(tf);
		createFKView(tf);
		createCollectionView(tf);
		createSQLView(tf);
		isNormalTabs=false;
		setControl(root);		
		
	}

	private void createNormalTabs()
	{
		Composite root = (Composite) this.getControl();
		TabFolder tf=(TabFolder) root.getChildren()[0];
		
		int itemindex;
		if (item_index!=null)
		{
			itemindex=tf.indexOf(item_index);
			if (itemindex>=0)
				tf.getItem(itemindex).dispose();
		}
		
		itemindex=tf.indexOf(item_element);		
		tf.getItem(itemindex).dispose();

//		itemindex=tf.indexOf(item_collection);		
//		tf.getItem(itemindex).dispose();

		itemindex=tf.indexOf(item_fk);		
		tf.getItem(itemindex).dispose();
		
		itemindex=tf.indexOf(item_sql);		
		tf.getItem(itemindex).dispose();
		
//		createCollectionView(tf);
		createFKView(tf);
		createIndexView(tf);
		createElementView(tf);
		createSQLView(tf);
		isNormalTabs=true;		
		setControl(root);		
		
	}

	protected void CollectionRefresh()
	{
		setDirty(true);
		((CollectionMapping)field.getMapping().getPersistentValueMapping()).createForeignKey();
		((CollectionMapping)field.getMapping().getPersistentValueMapping()).setReferencedPropertyName(null);
		fkbp=(BeanPropertySourceBase) ((CollectionMapping)field.getMapping().getPersistentValueMapping()).getFKPropertySource();
		fkpage.selectionChanged(null, new StructuredSelection(fkbp));
		item_fk.getControl().redraw();
		if (field.getMapping().getPersistentValueMapping()instanceof IdBagMapping)
		{
			IdBagforIndex.FormList((IdBagMapping)field.getMapping().getPersistentValueMapping());
			item_index.setControl(IdBagforIndex);						
		}
		//element&index page
		formCollectionView();
		formElementView();
		colectionbp.setRefresh(false);
	}
	
	protected void ElemantRefresh()
	{
		if (((CollectionMapping)field.getMapping().getPersistentValueMapping()).getElement() instanceof OneToManyMapping)
		{
			if (mod.findClass(((OneToManyMapping)((CollectionMapping)field.getMapping().getPersistentValueMapping()).getElement()).getReferencedEntityName())!=null)
			{	
			IHibernateClassMapping clazzmap = (IHibernateClassMapping) mod.findClass(((OneToManyMapping)((CollectionMapping)field.getMapping().getPersistentValueMapping()).getElement()).getReferencedEntityName()).getPersistentClassMapping();
			IDatabaseTable newtable = clazzmap.getDatabaseTable();
			if ((newtable!=null) && (((CollectionMapping)field.getMapping().getPersistentValueMapping()).getCollectionTable()!=null) 
					&& (!newtable.equals(((CollectionMapping)field.getMapping().getPersistentValueMapping()).getCollectionTable())))
			{
				setDirty(true);
				((CollectionMapping)field.getMapping().getPersistentValueMapping()).setCollectionTable(newtable);
				((CollectionMapping)field.getMapping().getPersistentValueMapping()).setReferencedPropertyName(null);
				((CollectionMapping)field.getMapping().getPersistentValueMapping()).setOwner(clazzmap);
				((CollectionMapping)field.getMapping().getPersistentValueMapping()).createForeignKey();
				fkbp=(BeanPropertySourceBase) ((CollectionMapping)field.getMapping().getPersistentValueMapping()).getFKPropertySource();
				fkpage.selectionChanged(null, new StructuredSelection(fkbp));
				item_fk.getControl().redraw();
				if (field.getMapping().getPersistentValueMapping()instanceof IdBagMapping)
				{
					IdBagforIndex.FormList((IdBagMapping)field.getMapping().getPersistentValueMapping());
					item_index.setControl(IdBagforIndex);						
				}
				//element&index page
				formCollectionView();
				formElementView();
			}
			}
			else 
			{
				setDirty(true);				
			}
		}												
		elempage.getBps().setRefresh(false);		
	}
	
	public boolean getDirty()
	{
		if (colectionbp!=null)
			dirty=dirty||colectionbp.isDirty();
		if (fkbp!=null)
			dirty=dirty||fkbp.isDirty();
		if (CEforElem!=null)
			dirty=dirty||CEforElem.isDirty();
		if (CEforKey!=null)
			dirty=dirty||CEforKey.isDirty();
		if (AEforElem!=null)
			dirty=dirty||AEforElem.isDirty();		
		if (IdBagforIndex!=null)
			dirty=dirty||IdBagforIndex.isDirty();		
		if (elempage!=null)
			dirty=dirty||elempage.isDirty();		
		if (keypage!=null)
			dirty=dirty||keypage.isDirty();
		
		return dirty;
	}
	/**
	 * @param dirty The dirty to set.
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
}
