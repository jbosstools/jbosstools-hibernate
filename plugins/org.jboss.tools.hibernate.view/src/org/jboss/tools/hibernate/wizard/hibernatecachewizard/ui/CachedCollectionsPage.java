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
package org.jboss.tools.hibernate.wizard.hibernatecachewizard.ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jboss.tools.hibernate.wizard.hibernatecachewizard.HibernateCacheWizard;
import org.jboss.tools.hibernate.wizard.hibernatecachewizard.datamodel.ICacheable;


public class CachedCollectionsPage extends CommonCachedPage 
{

	public CachedCollectionsPage(String name, Set items, Set cached_items)
	{
		super(name, ICacheable.bundle.getString("HibernateCacheWizard.LIST_CACHEABLE_COLLECTIONS"), ICacheable.bundle.getString("HibernateCacheWizard.LIST_CACHED_COLLECTIONS"));
		Incoming 	= new HashSet(items);
		Cached		= new HashSet(cached_items);
	}

	public void setCallback(Wizard callback)
	{
		CallBack = (HibernateCacheWizard)callback;
	}
	public void createControl(Composite parent) 
	{
		// create main composite.
		Composite thecomposite = new Composite(parent, SWT.NONE);
		thecomposite.setLayout(new GridLayout(3,false));
    // 	set page content.
		createPageContent(thecomposite);

		setDescription(ICacheable.bundle.getString("HibernateCacheWizard.CACHING_COLLECTION_DESCR"));
	// page settings
		setControl(thecomposite);
		setPageComplete(true);
	}
	private void createPageContent(Composite parent)
	{
		createPageElements(parent);
		createPopupMenu();
	}
	protected void createPageElements(Composite parent)
	{
		createInitialDescriptor(parent);
		createEmptyDescriptor(parent);
		createComposedDescriptor(parent);
		createInitialViewer(parent);
		stuffInitialViewer();
		createControlButtons(parent);		
		createCachedViewer(parent);
		stuffCachedViewer();
		setDefaultSelectionForList(true, true);
		processGroupButtons();
	}
	
	private void createInitialViewer(Composite parent)
	{
		InitialViewer 		= new ListViewer(parent);
		InitialViewer.getControl().setLayoutData(calculateTreeViewerSize(GridData.FILL_BOTH));
		InitialViewer.addSelectionChangedListener(new HibernateCacheWizardSelectionListener());
		
		ListContentProvider = new ListDataModelContentProvider();
		Sorter				= new ListSorter();
		InitialViewer.setContentProvider(ListContentProvider);
		InitialViewer.setSorter(Sorter);
	}
	private void createCachedViewer(Composite parent)
	{
		CachedViewer 		= new ListViewer(parent);
		CachedViewer.getControl().setLayoutData(calculateTreeViewerSize(GridData.FILL_BOTH));
		CachedViewer.addSelectionChangedListener(new HibernateCacheWizardSelectionListener());
		
		ListContentProvider = new ListDataModelContentProvider();
		Sorter				= new ListSorter();
		CachedViewer.setContentProvider(ListContentProvider);
		CachedViewer.setSorter(Sorter);
	}
	private void stuffInitialViewer()
	{
		InitialViewer.setInput(Incoming);
	}
	private void stuffCachedViewer()
	{
		CachedViewer.setInput(Cached);
	}
	
	protected void createInitialDescriptor(Composite parent) 
	{
		Label description 	= new Label(parent,SWT.NONE);
		description.setText(ICacheable.bundle.getString("HibernateCacheWizard.LIST_CACHEABLE_COLLECTIONS"));
		GridData gd 		= new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan 	= 1;
		description.setLayoutData(gd);
	}
	protected void createComposedDescriptor(Composite parent) 
	{
		Label description 	= new Label(parent,SWT.NONE);
		description.setText(ICacheable.bundle.getString("HibernateCacheWizard.LIST_CACHED_COLLECTIONS"));
		GridData gd 		= new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan 	= 1;
		description.setLayoutData(gd);
	}
	private void createEmptyDescriptor(Composite parent)
	{
		Label description = new Label(parent,SWT.NONE);
		description.setText(ICacheable.bundle.getString("HibernateCacheWizard.EMPTY_STRING"));
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		description.setLayoutData(gd);
	}
	protected void createPopupMenu()
	{
		super.createPopupMenu();
		//InitialViewer.getControl().setMenu(CacheContextMenu);
		CachedViewer.getControl().setMenu(CacheContextMenu);
	}
	private void cacheableToCachedUITransfer(String name)
	{
		if( Incoming.contains(name) )
		{
			Cached.add(prepareCachedClassName(name));
			//Incoming.remove(name);
		}
	}
	private String prepareCachedClassName(String name)
	{
		String newname = name;
		newname += (ICacheable.bundle.getString("HibernateCacheWizard.CACHED_STRATEGY_TOKEN") + cacheConcurrencyStrategy);
		return newname;
	}
	private String prepareCacheableClassName(String name)
	{
		String newname = name;
		String[] tokens = newname.split(ICacheable.bundle.getString("HibernateCacheWizard.CACHED_STRATEGY_TOKEN"));
		return tokens[0];// return classname w/o cached strategy name.
		//return newname;
	}
	private void collectionNamesTransfer(Iterator it, int transtype)
	{
		while(it.hasNext())
		{
			String name = (String)it.next();
			if(transtype == ICacheable.ADD_TO_CACHE ) 
			{
				addToCache(name);
			} 
			else 
			{
				removeFromCache(name) ;
			}
		}
		updateUI();
	}
	private void addToCache(String name)
	{
		CallBack.cacheCollections(name,cacheConcurrencyStrategy, false);
		cacheableToCachedUITransfer(name);
	}
	private void removeFromCache(String name)
	{
		removeClassFromCache(name);
		cacheToCacheableTransfer(name);
	}
	private void removeClassFromCache(String name)
	{
		CallBack.cacheCollections(name,null, false);
	}
	private void cacheToCacheableTransfer(String name)
	{
		if( Cached.contains(name) )
		{
			Incoming.add(prepareCacheableClassName(name));
			//Cached.remove(name);
		}
	}
	protected void updateUI()
	{
		updateInitialViewer();
		updateComposedViewer();
		
		processGroupButtons();
	}
	protected void updateInitialViewer()
	{
		InitialViewer.setInput(null);
		stuffInitialViewer();
		InitialViewer.refresh(false);
	}
	protected void updateComposedViewer()
	{
		CachedViewer.setInput(null);
		stuffCachedViewer();
		CachedViewer.refresh(false);
	}
	private void cleanSet(Iterator it,boolean incom)
	{
		while(it.hasNext())
		{
			Set temp = (incom) ? Incoming : Cached;
			temp.remove(it.next());
		}
	}
	protected void processAddToCacheButtonPressed()
	{
		StructuredSelection sel = (StructuredSelection)InitialViewer.getSelection();
		if(sel.size() < 1)// if have not selected items - do nothing.
			return;
		collectionNamesTransfer(sel.iterator(),ICacheable.ADD_TO_CACHE);
		cleanSet(sel.iterator(),true);
		updateUI();
		// set default selection;
		setDefaultSelectionForList(true, true);
		processButtonsState();
	}
	protected void processAddAllToCacheButtonPressed()
	{
		if(Incoming.size() < 1)
			return;
		collectionNamesTransfer(Incoming.iterator(), ICacheable.ADD_TO_CACHE);
		Incoming.clear();
		updateUI();
		processButtonsState();
	}
	protected void processRemoveFromCachePressed()
	{
		StructuredSelection sel = (StructuredSelection)CachedViewer.getSelection();
		if(sel.size() < 1)// if have not selected items - do nothing.
			return;
		collectionNamesTransfer(sel.iterator(),ICacheable.REMOVE_FROM_CACHE);
		cleanSet(sel.iterator(), false);
		updateUI();
		// set default selection;
		setDefaultSelectionForList(false, true);
		processButtonsState();
	}
	protected void processRemoveAllFromCachePressed()
	{
		if(Cached.size() < 1)
			return;
		collectionNamesTransfer(Cached.iterator(),ICacheable.REMOVE_FROM_CACHE);
		Cached.clear();
		updateUI();
		processButtonsState();
	}
	protected void WriteToCache(String name)
	{
		String item_name_strat = name;
		String[]item_names = item_name_strat.split(ICacheable.bundle.getString("HibernateCacheWizard.CACHED_STRATEGY_TOKEN")); 
		CallBack.cacheCollections(item_names[0],cacheConcurrencyStrategy, true);
	}
}
