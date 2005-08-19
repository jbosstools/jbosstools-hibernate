package org.hibernate.eclipse.console.wizards;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.jdt.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.hibernate.cfg.reveng.TableFilter;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.console.workbench.AnyAdaptableLabelProvider;
import org.hibernate.eclipse.console.workbench.DeferredContentProvider;
import org.hibernate.eclipse.console.workbench.LazyDatabaseSchema;
import org.hibernate.eclipse.console.workbench.TableContainer;
import org.hibernate.mapping.Table;
import org.hibernate.util.StringHelper;


public class TableFilterWizardPage extends WizardPage {
	// TODO: clean this up to use a shared wizard model
	
	private ComboDialogField consoleConfigurationName;
	private TableFilterView tfc;
	
	protected TableFilterWizardPage(String pageName) {		
		super( pageName );
		setTitle("Configure Table filters");
		setDescription("Specify which catalog/schema/tables should be included or excluded from the reverse engineering.");
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite container = new Composite(parent, SWT.NULL);
		//container.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_CYAN));
        
		GridLayout layout = new GridLayout();
		
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 10;
		
		consoleConfigurationName = new ComboDialogField(SWT.READ_ONLY);
		consoleConfigurationName.setLabelText("Console &configuration:");
		ConsoleConfiguration[] cfg = KnownConfigurations.getInstance().getConfigurations();
		String[] names = new String[cfg.length];
		for (int i = 0; i < cfg.length; i++) {
			ConsoleConfiguration configuration = cfg[i];
			names[i] = configuration.getName();
		}
		consoleConfigurationName.setItems(names);

		consoleConfigurationName.doFillIntoGrid(container, 3);
		
		TableFilterComposite tfc = createTableFilterPart( container );
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan=3;
		tfc.setLayoutData(gd);
						
		setControl(container);
		
	}

	private TableFilterComposite createTableFilterPart(Composite container) {
		tfc = new TableFilterView( container, SWT.NULL );
		return tfc;
	}

	private final class TableFilterView extends TableFilterComposite {
		private TableFilterList tableFilterList;

		TreeViewer viewer;

		private TableFilterView(Composite parent, int style) {
			super( parent, style );
		}

		protected void initialize() {
			super.initialize();
			TableViewer tableViewer = createTableFilterViewer();
			
			viewer = new TreeViewer(databaseTree);
			viewer.setLabelProvider(new AnyAdaptableLabelProvider());
			
			viewer.setContentProvider(new DeferredContentProvider());
			
			viewer.setInput( null );
			
			tableFilterList = new TableFilterList();
			tableViewer.setInput(tableFilterList);				
		}

		private TableViewer createTableFilterViewer() {
			TableViewer result = new TableViewer(tableFilters);
			result.setUseHashlookup(true);
			result.setColumnProperties(new String[] { "inclusion", "catalog", "schema", "name"});
			
			CellEditor[] editors = new CellEditor[result.getColumnProperties().length];
			editors[0] = new CheckboxCellEditor(result.getTable());				
			editors[1] = new TextCellEditor(result.getTable());
			editors[2] = new TextCellEditor(result.getTable());
			editors[3] = new TextCellEditor(result.getTable());
			
			result.setCellEditors(editors);
			result.setCellModifier(new TableFilterCellModifier(result));
			
			result.setLabelProvider(new TableFilterLabelProvider());
			result.setContentProvider(new TableFilterContentProvider(result));				
			return result;
		}

		protected void doRefreshDatabaseSchema() {
			ConsoleConfiguration configuration = KnownConfigurations.getInstance().find(consoleConfigurationName.getText());
			
			viewer.setInput(new LazyDatabaseSchema(configuration));
			
		}

		protected void doInclusion() {
			toggle( false );				
		}

		List getTableFilterList() {
			return tableFilterList.filters;
		}
		private void toggle(boolean exclude) {
			ISelection selection = viewer.getSelection();
			
			if(!selection.isEmpty()) {
				StructuredSelection ss = (StructuredSelection) selection;
				Iterator iterator = ss.iterator();
				while(iterator.hasNext()) {
					Object sel = iterator.next();
					TableFilter filter = null;
					
					if(sel instanceof Table) {
						Table table = (Table) sel;
						filter = new TableFilter();
						filter.setExclude(Boolean.valueOf(exclude));
						if(StringHelper.isNotEmpty(table.getCatalog())) {
							filter.setMatchCatalog(table.getCatalog());	
						}
						if(StringHelper.isNotEmpty(table.getSchema())) {
							filter.setMatchSchema(table.getSchema());	
						}
						if(StringHelper.isNotEmpty(table.getName())) {
							filter.setMatchName(table.getName());	
						}														
					} else if(sel instanceof TableContainer) { // assume its a schema!
						TableContainer tc = (TableContainer) sel;
						filter = new TableFilter();
						filter.setExclude(Boolean.valueOf(exclude));
						filter.setMatchSchema(tc.getName());
					}
					if(filter!=null) tableFilterList.addTableFilter(filter);
				}
			}
		}

		protected void doExclusion() {
			toggle(true);
		}

		protected void doRemove() {
			int sel = tableFilters.getSelectionIndex();
			TableItem[] selection = tableFilters.getSelection();
			for (int i = 0; i < selection.length; i++) {
				TableFilter item = (TableFilter) selection[i].getData();
				tableFilterList.removeTableFilter(item);
			}
			tableFilters.setSelection(Math.min(sel, tableFilterList.filters.size()-1));
		}

		protected void doMoveDown() {
			TableItem[] selection = tableFilters.getSelection();
			for (int i = 0; i < selection.length; i++) {
				TableFilter item = (TableFilter) selection[i].getData();
				tableFilterList.moveDown(item);
			}
		}

		protected void doMoveUp() {
			TableItem[] selection = tableFilters.getSelection();
			for (int i = 0; i < selection.length; i++) {
				TableFilter item = (TableFilter) selection[i].getData();
				tableFilterList.moveUp(item);
			}
		}
	}

	private final class TableFilterCellModifier implements ICellModifier {
		private final TableViewer tv;

		public TableFilterCellModifier(TableViewer tv) {
			this.tv = tv;
		}

		public void modify(Object element, String property, Object value) {
			TableFilter tf = (TableFilter) ((TableItem)element).getData();
			if("inclusion".equals(property)) {
				tf.setExclude((Boolean) value);
			}
			if("catalog".equals(property)) {
				tf.setMatchCatalog((String) value);
			}
			if("schema".equals(property)) {
				tf.setMatchSchema((String) value);
			}
			if("name".equals(property)) {
				tf.setMatchName((String) value);
			}			
			tv.update(new Object[] { tf }, new String[] { property });
		}

		public Object getValue(Object element, String property) {
			TableFilter tf = (TableFilter) element;
			if("inclusion".equals(property)) {
				return tf.getExclude();
			}
			if("catalog".equals(property)) {
				return tf.getMatchCatalog();
			}
			if("schema".equals(property)) {
				return tf.getMatchSchema();
			}
			if("name".equals(property)) {
				return tf.getMatchName();
			}		
			return null;
		}

		public boolean canModify(Object element, String property) {
			return true;
		}
	}

	static public class TableFilterLabelProvider extends LabelProvider implements ITableLabelProvider {

		//		 Names of images used to represent checkboxes	
		public static final String CHECKED_IMAGE 	= "checked";
		public static final String UNCHECKED_IMAGE  = "unchecked";
		
		
		public Image getColumnImage(Object element, int columnIndex) {
			if(columnIndex==0) {
				TableFilter tf = (TableFilter)element;
				if(tf.getExclude()!=null) {
					String key = tf.getExclude().booleanValue() ? ImageConstants.CLOSE : null ; // TODO: find a better image
					return EclipseImages.getImage(key);
				} else {
					return null;
				}
				
			}
			return  null;
		}

		
		public String getColumnText(Object element, int columnIndex) {
			TableFilter tf = (TableFilter) element;
			String result = "";
			
			switch (columnIndex) {
			case 0:
				return result;
			case 1:
				return tf.getMatchCatalog();
			case 2: 
				return tf.getMatchSchema();
			case 3: 
				return tf.getMatchName();
			default:
				return result;
			}			
		}
	}
	
	static public class TableFilterContentProvider implements IStructuredContentProvider, Observer {

		private final TableViewer tv;

		public TableFilterContentProvider(TableViewer tv) {
			this.tv = tv;			
		}
		
		public Object[] getElements(Object inputElement) {
			return ((TableFilterList)inputElement).filters.toArray();
		}

		public void dispose() {
			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput != null) {
				((TableFilterList) newInput).addObserver(this);
			}
			if (oldInput != null) {
				((TableFilterList) oldInput).deleteObserver(this);
			}
		}

		public void update(Observable o, Object arg) {
			tv.refresh();			
		}
		
	}
	
	static public class TableFilterList extends Observable {
		List filters;
		
		public TableFilterList() {
			filters = new ArrayList();
		}
		
		public void addTableFilter(TableFilter tf) {
			filters.add(tf);
			setChanged();
			notifyObservers();
		}
		
		public void removeTableFilter(TableFilter tf) {
			filters.remove(tf);
			setChanged();
			notifyObservers();
		}
		
		public void moveUp(TableFilter tf) {
			move( tf, -1 );
		}

		private void move(TableFilter tf, int shift) {
			int i = filters.indexOf(tf);
			
			if(i>=0) {
				if(i+shift<filters.size() && i+shift>=0) { 
					filters.remove(i);
					filters.add(i+shift, tf);
				}
			}
			setChanged();
			notifyObservers();
		}
		
		public void moveDown(TableFilter tf) {
			move( tf, 1 );
		}
	}
	
	public List getTableFilters() {
		return tfc.getTableFilterList();
	}
}
