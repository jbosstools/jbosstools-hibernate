/*
 * Created on 2004-10-30 by max
 * 
 */
package org.hibernate.eclipse.console.views;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.hibernate.console.QueryPage;
import org.hibernate.eclipse.console.HibernateConsolePlugin;


/**
 * @author max
 *
 */
public class QueryPageViewer {

	class LabelProviderImpl implements ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		public String getColumnText(Object element, int columnIndex) {
			Object value = element; //TODO: should just be a columnprovider per querypage....
			if (element instanceof QueryPage) {
				value = ((QueryPage) element).getList().get(columnIndex);
			}
			
			if (value.getClass().isArray()) {
				Object[] arr = (Object[]) value;
				if (columnIndex > arr.length - 1) {
					return "<Unknown value>";
				}
				return "" + arr[columnIndex];
			} else {
			return value == null ? "" : value.toString();
			}
		}
		public void addListener(ILabelProviderListener listener) {
		}
		public void dispose() {
		}
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}
		public void removeListener(ILabelProviderListener listener) {
		}
	}
	
	// should map to our table model instead
	class ContentProviderImpl implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof QueryPage) {
				QueryPage qp = ((QueryPage) inputElement);
				Object[] objects = qp.getList().toArray();
				if(qp.getExceptions().isEmpty()) {
					return objects;
				} else {
					Throwable[] throwables = (Throwable[])qp.getExceptions().toArray(new Throwable[0]);
					HibernateConsolePlugin.logErrorMessage("Exception while executing HQL Query", throwables);
					return throwables; // TODO: provide actual error page					
				}
			} else {
				return null;
			}
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		
	}
	

	private final QueryPage queryPage;
	private TableViewer tableViewer;
	private TabItem tabItem;
	private final QueryPageTabView qrView;
	
	public QueryPageViewer(QueryPageTabView qrView, QueryPage queryPage) {
		this.qrView = qrView;
		this.queryPage = queryPage;
		
		createControl();
		
		
	}
	
	protected TabItem getTabItem() {
		return this.tabItem;
	}
	
	public Table getTable() {
		return this.tableViewer.getTable();
	}
	
	protected void createControl() {
    	this.tabItem = new TabItem(this.qrView.tabs, SWT.NONE);
    	
    	int index = this.qrView.tabs.getItems().length;    	
    	Composite composite = new Composite(this.qrView.tabs, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.tabItem.setText(this.queryPage.getQueryString().replace('\n', ' ').replace('\r', ' '));
		/* different icon dependent on java/hql etc.
		if (this.queryPage.getEntity() == null) {
			this.tabItem.setImage(ImageStore.getImage(ImageStore.SCRIPT));
			this.tabItem.setText(this.queryPage.getBookmark().getName());
			this.tabItem.setToolTipText(this.queryPage.getQuery());
		} else if (this.queryPage.isMetaData()) {
			this.tabItem.setImage(ImageStore.getImage(ImageStore.TABLE_DETAILS));
			this.tabItem.setText(this.queryPage.getBookmark().getName() + ":" + 
					this.queryPage.getEntity().getQualifiedName());
			tabItem.setToolTipText(this.queryPage.getEntity().getQualifiedName());
		} else {
			this.tabItem.setImage(ImageStore.getImage(ImageStore.TABLE));
			this.tabItem.setText(this.queryPage.getBookmark().getName() + ":" + 
					this.queryPage.getEntity().getQualifiedName());
			this.tabItem.setToolTipText(this.queryPage.getEntity().getQualifiedName());
		}*/
		
		createTable(composite);
		this.tabItem.setControl(composite);
		initializePopUpMenu();

		this.qrView.tabs.setSelection(index-1);
	}


	/**
	 * @param tabItem
	 * @param composite
	 */
	private void createTable(Composite composite) {
		final Table table = new Table(composite,  SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
    	table.setLayout(new GridLayout());
    	table.setLayoutData(new GridData(GridData.FILL_BOTH));

		addColumnsToTable(table);
		this.tableViewer = new TableViewer(table);
		this.tableViewer.setLabelProvider(new LabelProviderImpl());
		this.tableViewer.setContentProvider(new ContentProviderImpl());
		this.tableViewer.setInput(this.queryPage);

		packColumns(table);
	}

	/**
	 * @param table
	 */
	private void packColumns(final Table table) {
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumn(i).pack();
		}
	}

	/**
	 * @param table
	 * @return
	 */
	private int addColumnsToTable(final Table table) {
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		List columns = this.queryPage.getPathNames();
		int columnCount = columns.size();
		for (int i = 0; i < columnCount; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(columns.get(i).toString());
		}
		return columnCount;
	}

	public void propertyChange(PropertyChangeEvent event) {
		if ("rows".equals(event.getPropertyName())) {
			this.tableViewer.refresh();
		} else if ("columns".equals(event.getPropertyName())) {
			Table table = this.tableViewer.getTable();
			TableColumn[] columns = table.getColumns();
			for (int i = 0, length = columns == null ? 0 : columns.length; i < length; i++) {
				columns[i].dispose();
			}
			addColumnsToTable(table);
			this.tableViewer.setInput(this.queryPage);
			packColumns(table);
			table.layout();
		}
	//	updateStatusLine();
	}
	
	public void dispose() {
		//this.queryPage.removePropertyChangeListener(this);
		this.tabItem.dispose();
	}

	protected QueryPage getQueryPage() {
		return this.queryPage;
	}
	
	private void initializePopUpMenu() {
        MenuManager manager = new MenuManager();
        manager.setRemoveAllWhenShown(true);
        manager.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager menuManager) {
                //QueryPageViewer.this.tableView.actionGroup.fillContextMenu(menuManager);
            }
        });
        Menu contextMenu = manager.createContextMenu(this.tableViewer.getControl());
        this.tableViewer.getControl().setMenu(contextMenu);
        // register the menu to the site so that we can allow 
        // actions to be plugged in
        //this.tableView.getSite().registerContextMenu(manager, this.tableView);
	}

	
	
	protected ISelection getSelection() {
		return this.tableViewer.getSelection();
	}
}
