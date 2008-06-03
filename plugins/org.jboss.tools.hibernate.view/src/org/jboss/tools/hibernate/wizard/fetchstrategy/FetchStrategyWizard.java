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
package org.jboss.tools.hibernate.wizard.fetchstrategy;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.core.hibernate.ICollectionMapping;
import org.jboss.tools.hibernate.core.hibernate.IJoinMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToOneMapping;
import org.jboss.tools.hibernate.core.hibernate.IOneToOneMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.PropertyMapping;
import org.jboss.tools.hibernate.view.ViewPlugin;
import org.jboss.tools.hibernate.view.views.ReadOnlyWizard;
import org.jboss.tools.hibernate.view.views.ViewsUtils;


/**
 * @author kaa - akuzmin@exadel.com
 * 
 */
public class FetchStrategyWizard extends ReadOnlyWizard {
	public static final String BUNDLE_NAME = "fetchstrategy"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(FetchStrategyWizard.class.getPackage().getName() + "." + BUNDLE_NAME);
	private ResourceBundle BUNDLE_IMAGE = ViewPlugin.BUNDLE_IMAGE;	
	private IMapping mod;
	private FetchStrategyPage1 page1;
	private FetchStrategyPage2 page2;
	private FetchStrategyPage3 page3;
	private FetchStrategyPage4 page4;
	private FetchStrategyPage5 page5;
	private FetchStrategyPage6 page6;
	public void addPages() {
		page1 = new FetchStrategyPage1();		
		addPage(page1);

		page6 = new FetchStrategyPage6();
		addPage(page6);
		
		page2 = new FetchStrategyPage2();
		addPage(page2);
		
		page3 = new FetchStrategyPage3();
		addPage(page3);

		page5 = new FetchStrategyPage5();
		addPage(page5);
		
		page4 = new FetchStrategyPage4(mod);
		addPage(page4);		
		//page1.setPageComplete(false);
		//page2.setPageComplete(false);

		ImageDescriptor descriptor = null;
		descriptor = ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("Wizard.Title"));
		setDefaultPageImageDescriptor(descriptor);
	}

	   	public boolean performFinish()
	   	{
			try {
				mod.save();
			} catch (IOException e) {
				ExceptionHandler.handle(new InvocationTargetException(e), getShell(), null,	null);
			} catch (CoreException e) {
				ExceptionHandler.handle(e, getShell(), null, null);
			} catch (Throwable e) { 
				ExceptionHandler.logThrowableWarning(e,null); // tau 15.09.2005
			}
	   	return true;
	   	}
	   	
  /* (non-Javadoc)
		 * @see org.eclipse.jface.wizard.Wizard#performCancel()
		 */
		public boolean performCancel() {
        	//TODO (tau-tau) for Exception			
			try {
				
				//edit tau 30.03.2006
				//mod.reload(true);  // edit tau 17.11.2005
				//mod.refresh(false, true);  // edit tau 17.11.2005
				mod.reload(true);				
				
			} catch (IOException e) {
				ExceptionHandler.logThrowableWarning(e,null); // tau 15.09.2005
			} catch (CoreException e) {
				ExceptionHandler.logThrowableWarning(e,null); // tau 15.09.2005
			} catch (Throwable e) { 
				ExceptionHandler.logThrowableWarning(e,null); // tau 15.09.2005
			} 
			return super.performCancel();
		}

public FetchStrategyWizard(IMapping mod, TreeViewer viewer)
   {
  	super(mod, viewer);
  	this.mod=mod;
	this.setWindowTitle(BUNDLE.getString("FetchStrategyWizard.title"));  	
	   }  

//		public IWizardPage getNextPage(IWizardPage page)
//		{//operation on press NEXT button
//		IWizardPage nextPage = super.getNextPage(page);			
//		thispage=page;		
//		return nextPage;
//		}

  public ArrayList findAssociations(IWizardPage page)
  {
	ArrayList<PropertyMapping> tempobj = new ArrayList<PropertyMapping>();
	IPersistentClassMapping[] elements=mod.getPersistentClassMappings();
	for(int i=0;i<elements.length;i++)
	{
		Iterator iter=elements[i].getFieldMappingIterator();
		while(iter.hasNext())
		{			
			PropertyMapping pm=(PropertyMapping)iter.next();
			if (pm!=null)
			if ((pm.getValue() instanceof ICollectionMapping) ||
				(pm.getValue() instanceof IManyToOneMapping) ||					
				(pm.getValue() instanceof IOneToOneMapping))
			{
				if (pm.getValue().getFetchMode()==null)
				{
					String fetchMode="select";
					if (pm.getPropertyMappingHolder() instanceof IJoinMapping)
						fetchMode="join";
					pm.getValue().setFetchMode(fetchMode);
//					ExceptionHandler.log("Fetch Strategy Wizard : mapping for field"+
//							pm.getValue().getFieldMapping().getPersistentField().getOwnerClass().getName()+
//							pm.getValue().getFieldMapping().getName()+" have a null fetch and force to "+fetchMode);	
				}
//				else
				if (isSutableForPage(pm,page))
					tempobj.add(pm);
				
				
				
			}
//TODO akuzmin add work with component			
		}

	}
	if (tempobj.size() == 0) {
		return null;
	} else {
		String[] sortproperty=new String[tempobj.size()];
		ArrayList<String> newproperty=new ArrayList<String>();
		for(int i=0;i<tempobj.size();i++)
		{
			sortproperty[i]=((IPropertyMapping) tempobj.get(i)).getPersistentField().getOwnerClass().getName()+"."+
				((IPropertyMapping) tempobj.get(i)).getName();
			newproperty.add(sortproperty[i]);
		}
		Arrays.sort(sortproperty);
		ArrayList<PropertyMapping> sortedlist = new ArrayList<PropertyMapping>();
		for (int i = 0; i < sortproperty.length; i++) {
			sortedlist.add(tempobj.get(newproperty.indexOf(sortproperty[i])));
		}
		return sortedlist;
	}
	
  }

private boolean isSutableForPage(IPropertyMapping pm, IWizardPage page) {
	// TODO akuzmin add selecting association for page
	if (page instanceof FetchStrategyPage1)
	{
		if (pm.getValue().getFetchMode().equals("select"))
		{
			if (pm.getValue() instanceof ICollectionMapping)
			{
				if (!((ICollectionMapping)pm.getValue()).isLazy())
					return true;
			}
			else//IManyToOneMapping && IOneToOneMapping
			{
				if ("false".equals(pm.getToOneLazy()))
					return true;
			}
		}
	}
	if (page instanceof FetchStrategyPage6)
	{
		if (pm.getValue().getFetchMode().equals("subselect"))
		{
			if (pm.getValue() instanceof ICollectionMapping)
			{
				if (!((ICollectionMapping)pm.getValue()).isLazy())
					return true;
			}
			else//IManyToOneMapping && IOneToOneMapping
			{
				if ("false".equals(pm.getToOneLazy()))
					return true;
			}
		}
	}	
	if (page instanceof FetchStrategyPage2)
	{
		if (!pm.getValue().getFetchMode().equals("join"))
		{
			if (pm.getValue() instanceof ICollectionMapping)
			{
				if (((ICollectionMapping)pm.getValue()).isLazy())
					return true;
			}
			else//IManyToOneMapping && IOneToOneMapping
			{
			    // #changed# by Konstantin Mishin on 11.03.2006 fixed for ESORM-525
				//if ("true".equals(pm.getToOneLazy()))
				if ("no-proxy".equals(pm.getToOneLazy()))
				// #changed#
					return true;
			}
		}
	}
	if (page instanceof FetchStrategyPage3)
	{
		if (pm.getValue().getFetchMode().equals("join"))
		{
			return true;
		}
		
	}
	// #changed# by Konstantin Mishin on 11.03.2006 fixed for ESORM-524
	//if (page instanceof FetchStrategyPage4)
	if (page instanceof FetchStrategyPage4 && pm.getValue() instanceof ICollectionMapping)
	// #changed#
	{
		return true;
	}
	if (page instanceof FetchStrategyPage5)
	{
		if (!pm.getValue().getFetchMode().equals("join"))//IManyToOneMapping && IOneToOneMapping
		{
			if (!(pm.getValue() instanceof ICollectionMapping))
			{
				if ("proxy".equals(pm.getToOneLazy()))
					return true;
			}
		}
	}
	
	return false;
}

/**
 * force to Eager
 */
public void doEager(IPropertyMapping object) {
	// TODO akuzmin force to eager
	object.getValue().setFetchMode("join");
	if (object.getValue() instanceof ICollectionMapping)
	{
		((ICollectionMapping)object.getValue()).setLazy(false);
	}
	else//IManyToOneMapping && IOneToOneMapping
	{
		object.setToOneLazy("false");
	}
	
	page3.refreshList();
	
	//add tau 31.03.2006
	object.getPersistentField().getMasterClass().getPersistentClassMapping().getStorage().setDirty(true);	
}
/**
 * force to Lazy
 */
public void doLazy(IPropertyMapping object) {
	// TODO akuzmin force to lazy
	object.getValue().setFetchMode("select");
	if (object.getValue() instanceof ICollectionMapping)
	{
		((ICollectionMapping)object.getValue()).setLazy(true);
	}
	else//IManyToOneMapping && IOneToOneMapping
	{
	    // #changed# by Konstantin Mishin on 11.03.2006 fixed for ESORM-525
		//object.setToOneLazy("true");
		object.setToOneLazy("no-proxy");
	    // #changed#
	}
	
	page2.refreshList();
	
	//add tau 31.03.2006
	object.getPersistentField().getMasterClass().getPersistentClassMapping().getStorage().setDirty(true);	
}
/**
 * force to Immediate
 */
public void doSelect(IPropertyMapping object) {
	// TODO akuzmin force to immediate
	object.getValue().setFetchMode("select");
	if (object.getValue() instanceof ICollectionMapping)
	{
		((ICollectionMapping)object.getValue()).setLazy(false);
	}
	else//IManyToOneMapping && IOneToOneMapping
	{
		object.setToOneLazy("false");
	}
	
	page1.refreshList();

	//add tau 31.03.2006
	object.getPersistentField().getMasterClass().getPersistentClassMapping().getStorage().setDirty(true);	
}
/**
 * force to Immediate Subselect
 */
public void doSubselect(IPropertyMapping object) {
	// TODO akuzmin force to immediate
	object.getValue().setFetchMode("subselect");
	if (object.getValue() instanceof ICollectionMapping)
	{
		((ICollectionMapping)object.getValue()).setLazy(false);
	}
	else//IManyToOneMapping && IOneToOneMapping
	{
		object.setToOneLazy("false");
	}
	
	page6.refreshList();
	
	//add tau 31.03.2006
	object.getPersistentField().getMasterClass().getPersistentClassMapping().getStorage().setDirty(true);	
}
/**
 * force to Proxy
 */
public void doProxy(IPropertyMapping object) {
	// TODO akuzmin force to proxy
	if (!(object.getValue() instanceof ICollectionMapping))//IManyToOneMapping && IOneToOneMapping
	{
		object.getValue().setFetchMode("select");
		object.setToOneLazy("proxy");
		page5.refreshList();
		
		//add tau 31.03.2006
		object.getPersistentField().getMasterClass().getPersistentClassMapping().getStorage().setDirty(true);
		
	}
	
}

// add tau 13.02.2006
// for ESORM-513 Overwrites and changes our Hibernate mapping files, even if they are marked read-only?	
public boolean canFinish() {
	boolean result = super.canFinish();
	
	// use ViewsUtils.ReadOnlyMappimg(theMapping)
	//if (this.readOnly) result = false;
	
	if (ViewsUtils.isReadOnlyMappimg(mod)) {
		result = false;			
	}
	
	return result;
}

}
