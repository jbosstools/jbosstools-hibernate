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

import java.util.*;

import org.eclipse.jface.viewers.*;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.hibernate.wizard.hibernatecachewizard.HibernateCacheWizard;
import org.jboss.tools.hibernate.wizard.hibernatecachewizard.datamodel.*;


public class CachedRegionsPage extends CommonCachedPage
{
	private Hashtable	 						CacheableRegions	 		= new Hashtable();
	private CacheableItemModifier				CellModifier				= null;	
	private HibernateCacheWizardMouseListener	regionsMouseListener		= new HibernateCacheWizardMouseListener();
	
	public CachedRegionsPage(String pagetitle, Hashtable items) {
		super(pagetitle, ICacheable.bundle.getString("HibernateCacheWizard.LIST_CACHEABLE_REGIONS"), ICacheable.bundle.getString("HibernateCacheWizard.LIST_CACHED_REGIONS"));
		initialize(items);
	}

	public void initialize(Hashtable items) {
		CacheableRegions 	= items;
		Incoming			= new HashSet();
		createIncomingSet();
	}

	private void createIncomingSet() {
		Enumeration keys = CacheableRegions.keys();
		while(keys.hasMoreElements())
		{
			String name = (String)keys.nextElement();
			ArrayList alist = (ArrayList)CacheableRegions.get(name);
			for(Iterator it = alist.iterator(); it.hasNext();) {
				Incoming.add(it.next());
			}
		}
	}
	/**
	 * Create controls for a sheet.
	 */
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

		setDescription(ICacheable.bundle.getString("HibernateCacheWizard.CACHING_REGIONS_DESCR"));
	// page settings
		setControl(thecomposite);
		setPageComplete(true);
	}
	private void createPageContent(Composite parent)
	{
		createPageElements(parent);
		 
		stuffInitialViewer();
		stuffCachedTreeViewer();
		ToCache.setEnabled(false);
		
	}
	protected void createPageElements(Composite parent)
	{
		CreateInitialListDescriptor(parent);
		createEmptyDescriptor(parent);
		createCachedTreeDescriptor(parent);
		createInitilaViewer(parent);
		createControlButtons(parent);
		createCachedTreeViewer(parent);
	}
	protected void createControlButtons(Composite parent)
	{
		Group buttonsform = new Group(parent, SWT.NONE);
		GridLayout gridlayout = new GridLayout();
		gridlayout.numColumns = 1;
		buttonsform.setLayout(gridlayout);

		GridData gdata = new GridData(GridData.FILL_HORIZONTAL);
		int style = SWT.PUSH | SWT.CENTER;
	
		
		Button btn1 = createButton(buttonsform,style,SWT.CENTER,
								  "HibernateCacheWizard.REMOVE_ALL_FROM_CACHE_TEXT", gdata);
		btn1.setEnabled(false);
		btn1.setVisible(false);

		ToCache = createButton(buttonsform,style,SWT.CENTER,
				  "HibernateCacheWizard.MOVE_TO_REGION", gdata);
		
		Button btn2 = createButton(buttonsform,style,SWT.CENTER,
				  "HibernateCacheWizard.REMOVE_ALL_FROM_CACHE_TEXT", gdata);
		btn2.setEnabled(false);
		btn2.setVisible(false);

		if(ToCache != null)
		{
			ToCache.addSelectionListener(new SelectionAdapter() {
	            public void widgetSelected(SelectionEvent event) { processAddToCacheButtonPressed();   }
	        });
		}
		buttonsform.pack();
	}
	
	protected void CreateInitialListDescriptor(Composite parent) 
	{
		Label description = new Label(parent,SWT.NONE);
		description.setText(ICacheable.bundle.getString("HibernateCacheWizard.LIST_CACHEABLE_REGIONS"));
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		description.setLayoutData(gd);
	}
	protected void createCachedTreeDescriptor(Composite parent) 
	{
		Label description = new Label(parent,SWT.NONE);
		description.setText(ICacheable.bundle.getString("HibernateCacheWizard.LIST_CACHED_REGIONS"));
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		description.setLayoutData(gd);
	}
	protected void createCachedTreeViewer(Composite parent)
	{
		CachedTreeViewer = new CachedClassTreeViewer(parent);
		CachedTreeViewer.getControl().setLayoutData(calculateTreeViewerSize(GridData.FILL_BOTH));
		CachedTreeViewer.setSorter(new TreeSorter());
		CellEditor[] editors = new CellEditor[1];
		editors[0] = new TextCellEditor(CachedTreeViewer.getTree());

		String[] columns = new String[1];
		columns[0] = "default";
		CachedTreeViewer.setColumnProperties(columns);
		CellModifier = new CacheableItemModifier(CachedTreeViewer);
		CellModifier.setCallback(this);
		CachedTreeViewer.setCellModifier(CellModifier);
		CachedTreeViewer.setCellEditors(editors);
		
		CachedTreeViewer.addTreeListener(new CachedRegionsTreeViewerListener());
		CachedTreeViewer.addSelectionChangedListener(new CachedRegionsTreeViewerSelListener());
	}
	protected void createInitilaViewer(Composite parent)
	{
		InitialViewer = new ListViewer(parent);
		InitialViewer.getControl().setLayoutData(calculateTreeViewerSize(GridData.FILL_BOTH));
		ListContentProvider = new ListDataModelContentProvider();
		Sorter				= new ListSorter();
		InitialViewer.setContentProvider(ListContentProvider);
		InitialViewer.setSorter(Sorter);
		InitialViewer.addSelectionChangedListener(new CachedRegionsTreeViewerSelListener() );
	}
	public ListViewer getInitialViewer()
	{
		return InitialViewer;
	}
	private void createEmptyDescriptor(Composite parent)
	{
		Label description = new Label(parent,SWT.NONE);
		description.setText(ICacheable.bundle.getString("HibernateCacheWizard.EMPTY_STRING"));
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		description.setLayoutData(gd);
	}

	public void stuffInitialViewer()
	{
		InitialViewer.setInput(null);
		InitialViewer.setInput(Incoming);
	}
	
	public void stuffCachedTreeViewer()
	{	
		CachedTreeViewer.setInput(null);
		TreeDataModel rootnode = new TreeDataModel(ICacheable.bundle.getString("HibernateCacheWizard.ROOT_ELEMENT_NAME"),ICacheable.REGION, ICacheable.bundle.getString("HibernateCacheWizard.LIST_CACHED_REGIONS"));
		stuffViewer(rootnode, CacheableRegions.keys());
		CachedTreeViewer.setInput(rootnode);
	}

	private void stuffViewer(TreeDataModel rootnode, Enumeration keys)
	{
		while(keys.hasMoreElements())
		{
			String name = (String)keys.nextElement();
			TreeDataModel regionnode = new TreeDataModel(name,ICacheable.REGION,ICacheable.bundle.getString("HibernateCacheWizard.ROOT_ELEMENT_NAME"),ICacheable.bundle.getString("HibernateCacheWizard.read-only"));
			ArrayList alist = (ArrayList)CacheableRegions.get(name);
			for(Iterator it = alist.iterator(); it.hasNext();)
			{
				regionnode.addNode(new TreeDataModel((String)it.next(),ICacheable.REGION,name,ICacheable.bundle.getString("HibernateCacheWizard.EMPTY_STRING")));
			}
			rootnode.addNode(regionnode);
		}
	}

	private void changeRegionItems(Iterator it, TreeDataModel newnode) {
		ArrayList<String> items = new ArrayList<String>();
		while(it.hasNext()) {
			items.add( (String)it.next() );
		}
		for(int i = 0; i < items.size(); i++) {
			if(isAllowChangeRegion(newnode)) { // move to region not to item.
				transferItemToNewRegion(newnode,(String)items.get(i) );
			}
		}
		updateUI();
	}
	
	private void transferItemToNewRegion(TreeDataModel newregion, String itemname)
	{
		TreeDataModel root = (TreeDataModel)CachedTreeViewer.getInput();
		Collection regions = root.getContents().values();
		TreeDataModel tdm  = null; 
		for(Iterator it = regions.iterator(); it.hasNext();)
		{
						tdm 	= ((TreeDataModel)it.next());
			//Collection 	items 	= tdm.getContents().values();
			Comparator<Object> 	cmp		= getComparator();

			Object[] children = tdm.getChildren();
			Arrays.sort(children, getSortComparator());
			if( Arrays.binarySearch(children, itemname, cmp) >= 0 )
			{
				// remove from old region. 
				TreeDataModel data = (TreeDataModel)tdm.getContents().get(itemname);
				if(tdm.getName().equals(newregion.getName()))
				{		return;				}

				tdm.removeNode(itemname);
				// set new region for an item.
				data.setParentName(newregion.getName());
				newregion.addNode(data);
				CallBack.setRegionName(itemname,newregion.getName());
				break;
			}
		}
	}
	private Comparator<Object> getComparator() {
		Comparator<Object> cmp = new Comparator<Object>() {
			public int compare(Object arg0, Object arg1) {
				TreeDataModel t1 = (TreeDataModel)arg0;
				String name = (String)arg1;
				return t1.getName().compareTo(name);
			}
		};
		return cmp;
	}
	
	private Comparator<Object> getSortComparator() {
		Comparator<Object> sortcomp = new Comparator<Object>() {
			public int compare(Object arg0, Object arg1) {
				TreeDataModel t1 	= (TreeDataModel)arg0;
				TreeDataModel t2 	= (TreeDataModel)arg1;
				return t1.getName().compareTo((String)t2.getName());
			}			
		};
		return sortcomp;
	}

	private boolean isAllowChangeRegion(TreeDataModel targetnode) {
		return isTargetRegionSelected(targetnode);
	}
	
	/**
	 * Node wich will contain moving item must be a region. 
	 * @param targetnode node for checking region entity.
	 * @return true - if selected node - is region.
	 */
	private boolean isTargetRegionSelected(TreeDataModel targetnode)
	{
		boolean sign = false;
		if( targetnode.getParentName().equals((Object)ICacheable.bundle.getString("HibernateCacheWizard.ROOT_ELEMENT_NAME")) )
		{
			sign = true;
		}
		return sign;
	}
	private void disableRegionsTreeScrollBars()
	{
		CachedTreeViewer.getTree().setEnabled(false);
		CachedTreeViewer.getControl().setVisible(false);
		CachedTreeViewer.getTree().getHorizontalBar().setEnabled(false);
		CachedTreeViewer.getTree().getVerticalBar().setEnabled(false);
	}
	private void enableRegionsTreeScrollBars()
	{
		CachedTreeViewer.getTree().getVerticalBar().setEnabled(true);
		CachedTreeViewer.getTree().getHorizontalBar().setEnabled(true);
		CachedTreeViewer.getTree().setEnabled(true);
		CachedTreeViewer.getControl().setVisible(true);
	}
	synchronized protected void updateUI()
	{
		disableRegionsTreeScrollBars();
		TreeItem	CurrItem;
		CurrItem = CachedTreeViewer.getTree().getTopItem();		
		CachedTreeViewer.refresh(false);
		// get expanded items.
		Object[] expanded = new Object[0];
		expanded = CachedTreeViewer.getExpandedElements();

		StructuredSelection sel_region = (StructuredSelection)CachedTreeViewer.getSelection();
		if(sel_region.getFirstElement() == null)
		{		enableRegionsTreeScrollBars(); return;			}
		String reg_name = ((TreeDataModel)sel_region.getFirstElement()).getName();
		if(!((TreeDataModel)CachedTreeViewer.getInput()).getContents().containsKey(reg_name) )
		{// if selected item - return. (need region).
			enableRegionsTreeScrollBars(); return;
		}
		CachedTreeViewer.setExpandedElements(expanded);

		fearfulTreeUpdater();
		CachedTreeViewer.refresh();
		enableRegionsTreeScrollBars();
		if((CurrItem == null) || (CurrItem.isDisposed())) { return; }
		
		if(CurrItem.getDisplay() == null)
		{	enableRegionsTreeScrollBars(); return;}
		if( (CurrItem.getText() != null) && !CurrItem.getText().equals(ICacheable.bundle.getString("HibernateCacheWizard.EMPTY_STRING")) )
			{CachedTreeViewer.getTree().setTopItem(CurrItem);}
	}

	/**
	 * temporary function for remove pluses...
	 *
	 */
	private void fearfulTreeUpdater()
	{
		TreeItem[] children = CachedTreeViewer.getTree().getItems();
		for(int i = 0; i < children.length; i++)
		{
			fearfulTreeItemUpdater(children[i]);
			if( ((TreeDataModel)children[i].getData()).getContents().size() < 1)
			{
				children[i].setExpanded(true);
			}
		}
		CachedTreeViewer.refresh(true);//!
	}
	
	private void fearfulTreeItemUpdater(TreeItem child)
	{
		TreeItem[] subchildren = child.getItems();
		if(child.getExpanded())
		{
			for(int j = 0; j < subchildren.length; j++)
			{
				subchildren[j].setExpanded(true);
			}
		}
	}
	private int getIndexByName(String name)
	{
		int index = -1;
		TreeItem[] items = CachedTreeViewer.getTree().getItems();
		for(int i = 0; i < items.length; i++)
		{
			if(name.endsWith(items[i].getText()))
			{
				index = i; break;
			}
		}
		return index;
	}

	synchronized public void updateTreeStructure(final String newname, final String oldname)
	{// 
 		TreeDataModel root = (TreeDataModel)CachedTreeViewer.getInput();
 		Hashtable regions = root.getContents();
 		if(regions.containsKey(oldname))
 		{
 			// create new region.
 	 		TreeDataModel newregion = createNewRegion(newname);
 			TreeDataModel oldnode = (TreeDataModel)regions.get(oldname);
 			
 			changeRegionItems(oldnode.getContents().keySet().iterator(),newregion);
 			root.removeNode(oldnode);
 			CachedTreeViewer.refresh();
 		}
	}
 	
 	private TreeDataModel createNewRegion(String newregionname)
 	{
 		TreeDataModel root = (TreeDataModel)CachedTreeViewer.getInput();
 		TreeDataModel newregion = new TreeDataModel(newregionname,ICacheable.REGION,root.getName(),ICacheable.bundle.getString("HibernateCacheWizard.read-only")); 
 		root.addNode(newregion);
 		return newregion;
 	}
	protected void processAddToCacheButtonPressed()
	{
		StructuredSelection sel_items = (StructuredSelection)InitialViewer.getSelection();
		StructuredSelection sel_region = (StructuredSelection)CachedTreeViewer.getSelection();
		if(!isSelectionValid(sel_items, sel_region))
		{
			return;
		}
		String name = ((TreeDataModel)sel_region.getFirstElement()).getName();
		int index = getIndexByName(name);
		if(index != -1)
		{// item found.
			TreeItem ti = CachedTreeViewer.getTree().getItem(index);	
			ti.setExpanded(true);
		}
		changeRegionItems(sel_items.iterator(),(TreeDataModel)sel_region.getFirstElement());
	}
	private boolean isSelectionValid(StructuredSelection cacheable, StructuredSelection cached)
	{
		boolean valid = true;
		if(cacheable.size() < 1)// if have not selected items - do nothing.
			valid = false;
		
		if(cached.size() != 1)// only single new region can be selected.
			valid = false;
		return valid;

	}
	public void setEnabledMoveToButton(final boolean sign)
	{
		ToCache.setEnabled(sign);
	}
	private class HibernateCacheWizardMouseListener implements MouseListener
	{
		public void mouseDoubleClick(MouseEvent e) 
		{}
		public void mouseDown(MouseEvent e) 
		{
			if (e.getSource() instanceof Button) 
			{// button pressed
				processButtonPressed((Button)e.getSource());
			}
			if(e.getSource() instanceof Table)
			{}
		}

		public void mouseUp(MouseEvent e) 
		{}

		private void processButtonPressed(Button btnevent)
		{
			if(btnevent.getText().equals(ICacheable.bundle.getString("HibernateCacheWizard.MOVE_TO_REGION")))
			{
				CachedTreeViewer.cancelEditing();
				processAddToCacheButtonPressed();
			}
		}
	}
	
	private class CachedRegionsTreeViewerListener implements ITreeViewerListener
	{
		public void treeCollapsed(TreeExpansionEvent event) {}

		public void treeExpanded(TreeExpansionEvent event)
		{
			String name = ( (TreeDataModel)event.getElement()).getName();
			int index = getIndexByName(name);
			if(index == -1) {	treeCollapsed(event); return;		}
			disableRegionsTreeScrollBars();
			TreeItem selected = CachedTreeViewer.getTree().getItem(index);
			selected.setExpanded(true);
			fearfulTreeItemUpdater(selected);
			CachedTreeViewer.refresh(true);
			enableRegionsTreeScrollBars();
		}
	}
	private class CachedRegionsTreeViewerSelListener implements ISelectionChangedListener
	{
		public void selectionChanged(SelectionChangedEvent event) 
		{
			StructuredSelection sel_items 	= (StructuredSelection)InitialViewer.getSelection();
			StructuredSelection sel_region 	= (StructuredSelection)CachedTreeViewer.getSelection();
			boolean enabled = false;
			
			enabled = ( (isSelectionValid(sel_items, sel_region)) && (isTargetRegionSelected( (TreeDataModel)sel_region.getFirstElement() )) ) ? true : false;
			ToCache.setEnabled(enabled);
		}
		
	}
}

