package org.hibernate.eclipse.console.views;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.QueryPage;
import org.hibernate.eclipse.console.views.properties.HibernatePropertyHelper;
import org.hibernate.eclipse.console.views.properties.HibernatePropertySourceProvider;



/**
 */

public class QueryPageTabView extends ViewPart implements ISelectionProvider {
	protected TabFolder tabs = null;

	private Set listeners = Collections.synchronizedSet(new HashSet());
	
	protected List pageViewers = Collections.synchronizedList(new ArrayList());
	
	ListDataListener dataListener = new ListDataListener() {
		public void contentsChanged(ListDataEvent e) {
			rebuild();

		}

		public void intervalAdded(ListDataEvent e) {
			contentsChanged(e);

		}

		public void intervalRemoved(ListDataEvent e) {
			contentsChanged(e);

		}
	};

	private QueryPageTabViewActionGroup actionGroup;
	
	/**
	 * Generic contructor
	 */
	public QueryPageTabView() {
		KnownConfigurations.getInstance().getQueryPageModel().addListDataListener(dataListener);
	}


	protected void rebuild() {
		QueryPage selection = getSelectedQueryPage();
		
		Collection additions = getAddedResultSets();
		for (Iterator i = additions.iterator(); i.hasNext();) {
			QueryPage results = (QueryPage) i.next();
			this.pageViewers.add(new QueryPageViewer(this, results));
		}
		
		Collection deletions = getRemovedResultSets(); 
		for (Iterator i = deletions.iterator(); i.hasNext();) {
			QueryPage results = (QueryPage) i.next();
			
			QueryPageViewer viewer = findViewerFor(results);
			this.pageViewers.remove(viewer);
			viewer.dispose();
		}		
		
		// Handle firing selection changes here to cover for when the model fire changes
		// but the Tab widget doesn't (e.g. the first page).
		QueryPage newSelection = getSelectedQueryPage();
		if (selection != null && newSelection == null) {
			fireSelectionChangedEvent();
		} else if (selection == null && newSelection != null) {
			fireSelectionChangedEvent();
		} else if (selection != null && !selection.equals(newSelection)) {
			fireSelectionChangedEvent();
		}
	}


	public void setFocus() {
	}
	
	public void dispose() {
		KnownConfigurations.getInstance().getQueryPageModel().removeListDataListener(dataListener);
		super.dispose();
	}
	
	public void createPartControl(Composite parent) {
		this.tabs = new TabFolder(parent, SWT.NONE);
		tabs.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				System.err.println("should not happen!");

			}
		});
		this.tabs.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				fireSelectionChangedEvent();
			}
		});
		
		rebuild();
		
		initActions();
		provideSelection();
	}

	private void provideSelection() {
		getSite().setSelectionProvider(this);
	}

	public void initActions() {
		
        this.actionGroup = new QueryPageTabViewActionGroup(this,this);
        
        IActionBars actionBars = getViewSite().getActionBars();
        this.actionGroup.fillActionBars(actionBars);
	}

	public void fireSelectionChangedEvent(ISelection selection) {
		for (Iterator i = this.listeners.iterator(); i.hasNext();) {
			ISelectionChangedListener listener = (ISelectionChangedListener) i.next();
			listener.selectionChanged(new SelectionChangedEvent(this, selection));
		}
		
		/*QueryPageViewer results = getSelectedQueryPageViewer();
		if (results != null) {
			results.updateStatusLine();
		} else {
			getViewSite().getActionBars().getStatusLineManager().setMessage("");
		}*/		
	}
	
	/** fire event that query-tab is changed **/
	protected void fireSelectionChangedEvent() {
		ISelection selection = getSelection();
		fireSelectionChangedEvent(selection);
	}

	public Object getAdapter(Class adapter) {

		if (adapter.equals(IPropertySheetPage.class))
		{
			PropertySheetPage page = new PropertySheetPage();
			page.setPropertySourceProvider(new HibernatePropertySourceProvider(this));
			return page;
		}
		return super.getAdapter(adapter);
	}
	
	public ISelection getSelection() {
		QueryPage selection = getSelectedQueryPage();
		
		return selection == null 
			? new StructuredSelection() 
			: new StructuredSelection(selection);	
	}

	/**
	 * @return
	 */
	public QueryPage getSelectedQueryPage() {
		QueryPageViewer viewer = getSelectedQueryPageViewer();
		return viewer == null ? null : viewer.getQueryPage();
	}

	protected QueryPageViewer getSelectedQueryPageViewer() {
		QueryPageViewer selection = null;
		if(this.tabs.isDisposed()) {
			return selection;
		} else {
			int index = this.tabs.getSelectionIndex();
			if (index >= 0) {
				TabItem item = this.tabs.getItem(index);
				for (Iterator i = this.pageViewers.iterator(); 
				selection == null && i.hasNext();) {
					QueryPageViewer viewer = (QueryPageViewer) i.next();
					if (item == viewer.getTabItem()) {
						selection = viewer;
					}
				}
			}
		}
		return selection;
	}


	
	/**
	 * @return
	 */
	private Collection getRemovedResultSets() {
		Collection collection = KnownConfigurations.getInstance().getQueryPageModel().getPagesAsList();
		Collection visible = getQueryPages();
		visible.removeAll(collection);
		return visible;
	}

	private Collection getAddedResultSets() {
		Collection collection = KnownConfigurations.getInstance().getQueryPageModel().getPagesAsList();
		collection.removeAll(getQueryPages());
		return collection;
	}
	
	private Collection getQueryPages() {
		List list = new ArrayList();
		for (Iterator i = this.pageViewers.iterator(); i.hasNext();) {
			QueryPageViewer viewer = (QueryPageViewer) i.next();
			list.add(viewer.getQueryPage());
		}
		return list;
	}
	
	private QueryPageViewer findViewerFor(QueryPage results) {
		QueryPageViewer viewer = null;
		for (Iterator i = this.pageViewers.iterator(); viewer == null && i.hasNext();) {
			QueryPageViewer temp = (QueryPageViewer) i.next();
			if (results != null && results.equals(temp.getQueryPage())) {
				viewer = temp;
			}
		}
		return viewer;
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		this.listeners.remove(listener);
	}


	public void setSelection(ISelection selection) {
		System.out.println("Told to select " + selection);
	}


	

}