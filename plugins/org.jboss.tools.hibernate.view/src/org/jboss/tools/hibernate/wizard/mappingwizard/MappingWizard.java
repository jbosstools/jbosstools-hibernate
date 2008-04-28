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
package org.jboss.tools.hibernate.wizard.mappingwizard;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.jboss.tools.hibernate.core.IDatabaseTablePrimaryKey;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.data.Table;
import org.jboss.tools.hibernate.internal.core.hibernate.ClassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.JoinedSubclassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.PropertyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMappingHelper;
import org.jboss.tools.hibernate.view.ViewPlugin;
import org.jboss.tools.hibernate.view.views.ReadOnlyWizard;

/**
 * @author kaa
 * 
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MappingWizard extends ReadOnlyWizard implements IWizard, IRunnableContext {
	public static final String BUNDLE_NAME = "mappingwizard"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(MappingWizardPage1.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private ResourceBundle BUNDLE_IMAGE = ViewPlugin.BUNDLE_IMAGE;
	private MappingWizardPage1 page1;//Select clase
	private MappingWizardPage4 page2;//Inheritance mapping	
	private MappingWizardPage2 page3;//percistent class mapping property
	private MappingWizardPage8 page4;//primary key
	private MappingWizardPage3 page5;//class identification

	private MappingWizardPage5 page6;//discriminetor mapping
	private MappingWizardPage7 page7;//version mapping
	private MappingWizardPage6 page8;//queries
	private String SelectedPersistentClass;
	private boolean dirty=false;
	private IMapping mod;

	public void addPages() {
 
		IPersistentClass pc = mod.findClass(SelectedPersistentClass);

		if ((SelectedPersistentClass == null) || (pc == null)) {
			page1 = new MappingWizardPage1(mod);
			addPage(page1);

			page2 = new MappingWizardPage4(mod);
			addPage(page2);
		} else {
			if (pc.getSuperClass() != null) {
				page2 = new MappingWizardPage4(mod);
				addPage(page2);
			}
		}

		page3 = new MappingWizardPage2(mod,SelectedPersistentClass);
		addPage(page3);

		page4 = new MappingWizardPage8(mod);
		addPage(page4);
		
		page5 = new MappingWizardPage3(mod);
		addPage(page5);
		
		page6 = new MappingWizardPage5(mod);
		addPage(page6);
		 
		page7 = new MappingWizardPage7(mod);
		addPage(page7);
		
		page8 = new MappingWizardPage6(mod);
		addPage(page8);
		//page8.setPageComplete(false);
		ImageDescriptor descriptor = null;
		descriptor = ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("Wizard.Title"));
		setDefaultPageImageDescriptor(descriptor);

	}

	public boolean performFinish() {
		if (SelectedPersistentClass!=null)
		{
			if (getContainer().getCurrentPage() instanceof MappingWizardPage6)
			{
				setDirty(true);
				page8.SetResaults();
			}
			
        	//TODO (tau-tau) for Exception			
			try {
				if (mod.findClass(SelectedPersistentClass).getPersistentClassMapping()!=null)
				{
					if (((ClassMapping)mod.findClass(SelectedPersistentClass).getPersistentClassMapping()).isClass()) // not Subclass
					{
					    page5.SetVariables(SelectedPersistentClass);
					if (!page5.canFlipToNextPage())
						{
						setDirty(true);
						((IHibernateClassMapping)mod.findClass(SelectedPersistentClass).getPersistentClassMapping()).setIdentifier(null);
						((IHibernateClassMapping)mod.findClass(SelectedPersistentClass).getPersistentClassMapping()).setIdentifierProperty(null);						
						}
					if (((IHibernateClassMapping)mod.findClass(SelectedPersistentClass).getPersistentClassMapping()).getIdentifierProperty()==null)
					{
						IDatabaseTablePrimaryKey pk=((IHibernateClassMapping)mod.findClass(SelectedPersistentClass).getPersistentClassMapping()).getDatabaseTable().getPrimaryKey();
						if (pk!=null)
						{
							String[] columns=new String[pk.getColumnSpan()];
							Iterator coliter=pk.getColumnIterator();
							int i=0;
						while (coliter.hasNext())
						{
							columns[i++]=((Column) coliter.next()).getName();
						}
						for(i=0;i<columns.length;i++)
						{
							pk.removeColumn(columns[i]);
							setDirty(true);
						}
						
						}
					}
					}
					mod.findClass(SelectedPersistentClass).getPersistentClassMapping().getStorage().save(true);
				}
			} catch (IOException e) {
				ExceptionHandler.handle(new InvocationTargetException(e), getShell(), null,
						null);
			} catch (CoreException e) {
				ExceptionHandler.handle(e, getShell(), null, null);
			} catch (Throwable e) { 
				ExceptionHandler.logThrowableWarning(e,null); // tau 15.09.2005
			}
		}
		return true;
	}

	public MappingWizard(IMapping mod, IPersistentClass persistentClass, TreeViewer viewer) {
		super(null, viewer);
		setNeedsProgressMonitor(true);
		SelectedPersistentClass = persistentClass.getName();
		this.mod = mod;
		this.setWindowTitle(BUNDLE.getString("MappingWizard.title"));
	}

	// add tau 04.03.2005
	//	from visitPackage:	
	public MappingWizard(IMapping mod, TreeViewer viewer) {
		super(null, viewer);
		setNeedsProgressMonitor(true);
		this.mod = mod;
		SelectedPersistentClass = null;
		this.setWindowTitle(BUNDLE.getString("MappingWizard.title"));
	}

	public void run(boolean fork, boolean cancelable,
			IRunnableWithProgress runnable) throws InvocationTargetException,
			InterruptedException {

	}

	public IWizardPage getNextPage(IWizardPage page) {//operation on press NEXT
													  // button
		IWizardPage nextPage = super.getNextPage(page);
		if (page instanceof MappingWizardPage1) {
			SelectedPersistentClass = page1.getSelectedClass();
			IPersistentClass pc = mod.findClass(SelectedPersistentClass);
			
			if (pc != null)
			{
				if (pc.getPersistentClassMapping()!=null)
				{
				page3.SetSelectedPC(SelectedPersistentClass);
				page3.FormList();//fill all data

				if (pc.getSuperClass() == null) {
					page3.SetSelectedPC(SelectedPersistentClass);
					nextPage = super.getNextPage(super.getNextPage(page));
				}
				}
				else 
					{
					MessageDialog.openInformation(getShell(), BUNDLE.getString("MappingWizard.MessageDialogTitle"),MessageFormat.format(BUNDLE.getString("MappingWizard.MessageDialogMessageNotMapping"),new Object[]{SelectedPersistentClass,mod.getName()}));
					ExceptionHandler.logInfo("Couldn't find Persistent Class mapping for \""+SelectedPersistentClass+"\" in model "+mod.getName());
					return page;
					}
		}
			else {
				MessageDialog.openInformation(getShell(), BUNDLE.getString("MappingWizard.MessageDialogTitle"),MessageFormat.format(BUNDLE.getString("MappingWizard.MessageDialogMessageNotClass"),new Object[]{SelectedPersistentClass,mod.getName()}));
				ExceptionHandler.logInfo("Couldn't find Persistent Class \""+SelectedPersistentClass+"\" in model "+mod.getName());
				return page;
			}
				
		}
		else if (page instanceof MappingWizardPage2)
		{
			//set new table
			//mod.findClass(SelectedPersistentClass).getPersistentClassMapping().setDatabaseTable(mod.findTable(mod.findClass(SelectedPersistentClass).getPersistentClassMapping().getDatabaseTable().getName()));
		}		
		else if (page instanceof MappingWizardPage4)
		{
			IPersistentClass pc = mod.findClass(SelectedPersistentClass);
			if (pc.getPersistentClassMapping()==null)
			{
				MessageDialog.openInformation(getShell(), BUNDLE.getString("MappingWizard.MessageDialogTitle"),MessageFormat.format(BUNDLE.getString("MappingWizard.MessageDialogMessageNotMapping"),new Object[]{SelectedPersistentClass,mod.getName()}));
				ExceptionHandler.logInfo("Couldn't find Persistent Class mapping for \""+SelectedPersistentClass+"\" in model "+mod.getName());
			}
			if (!page2.getActiveButton().equals("none"))
				try {
					mod.replaceMappingForPersistentClass(pc,page2.getActiveButton());
					setDirty(true);
					if ((page2.getActiveButton().equals(OrmConfiguration.TABLE_PER_SUBCLASS))&&(((JoinedSubclassMapping)pc.getPersistentClassMapping()).getKey()==null))
					{
					//create key
					ClassMapping classMapping = (ClassMapping)pc.getPersistentClassMapping();
					
					SimpleValueMapping fk = HibernateAutoMappingHelper.createAndBindFK(classMapping.getRootClass()
								.getIdentifier(), (Table) HibernateAutoMappingHelper
								.getPrivateTable(classMapping), classMapping.getRootClass()
								.getPersistentClass().getShortName(), false);
					((JoinedSubclassMapping)pc.getPersistentClassMapping()).setKey(fk);
					}
					page3.FormList();	
				} catch (CoreException e) {
	            	//TODO (tau-tau) for Exception					
					ExceptionHandler.handle(e, getShell(), null, null);
				}   
		
			if (((IHibernateClassMapping)pc.getPersistentClassMapping()).isSubclass())
				page2.SetActiveButton(OrmConfiguration.TABLE_PER_HIERARCHY);
			else if (((IHibernateClassMapping)pc.getPersistentClassMapping()).isJoinedSubclass())
				page2.SetActiveButton(OrmConfiguration.TABLE_PER_SUBCLASS);
			else if (((IHibernateClassMapping)pc.getPersistentClassMapping()).isClass())
				page2.SetActiveButton(OrmConfiguration.TABLE_PER_CLASS);
				else if (((IHibernateClassMapping)pc.getPersistentClassMapping()).isUnionSubclass())
				page2.SetActiveButton(OrmConfiguration.TABLE_PER_CLASS_UNION);
			page3.SetSelectedPC(SelectedPersistentClass);
		}		
		
		if (nextPage instanceof MappingWizardPage4) {
			
		} else
		if (nextPage instanceof MappingWizardPage2) {
		
		} else if (nextPage instanceof MappingWizardPage8) {
			if (((ClassMapping)mod.findClass(SelectedPersistentClass).getPersistentClassMapping()).isClass()==false)
			{
				nextPage=page8;
			}
			else page4.SetVariables(SelectedPersistentClass);
				} 
			else if (nextPage instanceof MappingWizardPage3) {
			page3.FormList();//fill all data
			page5.SetVariables(SelectedPersistentClass);
		} else  if (nextPage instanceof MappingWizardPage5) {
				{
//				if (!page8.isPageComplete())//Make Finish button disable
//					// (when press BACK button)
//				{
//					if (page8.isFinish)
//					{
//						page8.isFinish = false;
//					    page8.setPageComplete(false);
//					}
//				}
//					else
//						page8.setPageComplete(false);
				}
						
		} else
			if (nextPage instanceof MappingWizardPage7) {
//				if (page8.isPageComplete())
//				{
//					page8.setPageComplete(false);
//				}
				if (page instanceof MappingWizardPage5)
				{
					    page6.SetSelectedPC(SelectedPersistentClass);
						page6.FormList();
				}				
			}
			else if (nextPage instanceof MappingWizardPage6) {
				if (page instanceof MappingWizardPage7)
				{
				page7.SetSelectedPC(SelectedPersistentClass);
				page7.FormList();
				}
//			    if (!page8.isPageComplete())//Make Finish button disable
//				 (when press BACK button)
//				if (page8.isFinish)
//					page8.isFinish = false;
//				else
//					page8.setPageComplete(true);
		} else {//Make Finish button enable
			page8.SetVariables(SelectedPersistentClass);
//			page8.isFinish = true;
//			page8.setPageComplete(false);
		}

		return nextPage;
	}

	public boolean IsFieldColumn(String Colimnname) {
		ArrayList<String> list = new ArrayList<String>();
		Iterator iter = ((IHibernateClassMapping)(mod.findClass(SelectedPersistentClass).getPersistentClassMapping())).getPropertyIterator();
			while ( iter.hasNext() ) {
				Iterator iter2 =((PropertyMapping)iter.next()).getColumnIterator();
				// #added# by Konstantin Mishin on 21.11.2005 fixed for ESORM-328
				if(iter2!=null)
				// #added#
					while ( iter2.hasNext() ) {
						list.add(((Column)iter2.next()).getName());
					}
			}
			
		if (list.contains(Colimnname))
		return false;
		else return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performCancel()
	 */
	public boolean performCancel() {
		if (SelectedPersistentClass!=null)
		{
        	//TODO (tau-tau) for Exception			
			try {
				if (isDirty())
				{
					mod.findClass(SelectedPersistentClass).getPersistentClassMapping().getStorage().reload();
					mod.refresh(false, true);  // edit tau 17.11.2005
				}
			} catch (IOException e) {
				ExceptionHandler.logThrowableWarning(e,null); // tau 15.09.2005
			} catch (CoreException e) {
				ExceptionHandler.logThrowableWarning(e,null); // tau 15.09.2005
			} catch (Throwable e) { 
				ExceptionHandler.logThrowableWarning(e,null); // tau 15.09.2005
			} 
		}
		return super.performCancel();
	}

	/**
	 * @return Returns the dirty.
	 */
	public boolean isDirty() {
		return dirty||page3.getDirty()||page4.getDirty()||page5.isDirty()||page7.isDirty();
	}

	/**
	 * @param dirty The dirty to set.
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public boolean canFinish() {
		boolean result = super.canFinish();
		if (this.readOnly) result = false;
		return result;		
	}
	

}