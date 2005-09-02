/**
 * 
 */
package org.hibernate.eclipse.console.wizards;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.console.model.ITableFilter;
import org.hibernate.eclipse.console.utils.AutoResizeTableLayout;
import org.hibernate.eclipse.console.workbench.AnyAdaptableLabelProvider;
import org.hibernate.eclipse.console.workbench.DeferredContentProvider;
import org.hibernate.eclipse.console.workbench.LazyDatabaseSchema;
import org.hibernate.eclipse.console.workbench.TableContainer;
import org.hibernate.mapping.Table;
import org.hibernate.util.StringHelper;

public abstract class TableFilterView extends TableFilterComposite {

	TreeViewer viewer;

	private final IReverseEngineeringDefinition revEngDef;

	private TableViewer tableViewer;

	public TableFilterView(IReverseEngineeringDefinition revEngDef, Composite parent, int style) {
		super( parent, style );
		this.revEngDef = revEngDef;
		tableViewer.setInput(revEngDef);
	}

	protected void initialize() {
		super.initialize();
		tableViewer = createTableFilterViewer();
		
		viewer = new TreeViewer(databaseTree);
		viewer.setLabelProvider(new AnyAdaptableLabelProvider());
		
		viewer.setContentProvider(new DeferredContentProvider());
		
		viewer.setInput( null );
		
						
	}

	private TableViewer createTableFilterViewer() {
		TableViewer result = new TableViewer(tableFilters);
		result.setUseHashlookup(true);
		result.setColumnProperties(new String[] { "inclusion", "catalog", "schema", "name"});
		
		/*AutoResizeTableLayout autoResizeTableLayout = new AutoResizeTableLayout(result.getTable());
		result.getTable().setLayout(autoResizeTableLayout);
		autoResizeTableLayout.addColumnData(new ColumnWeightData(4));
		autoResizeTableLayout.addColumnData(new ColumnWeightData(40));
		autoResizeTableLayout.addColumnData(new ColumnWeightData(40));
		autoResizeTableLayout.addColumnData(new ColumnWeightData(40));
		*/
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
		ConsoleConfiguration configuration = KnownConfigurations.getInstance().find(getConsoleConfigurationName());
		
		viewer.setInput(new LazyDatabaseSchema(configuration));
		
	}

	abstract protected String getConsoleConfigurationName();

	protected void doInclusion() {
		toggle( false );				
	}

	ITableFilter[] getTableFilterList() {
		return revEngDef.getTableFilters();
	}
	
	private void toggle(boolean exclude) {
		ISelection selection = viewer.getSelection();
		
		if(!selection.isEmpty()) {
			StructuredSelection ss = (StructuredSelection) selection;
			Iterator iterator = ss.iterator();
			while(iterator.hasNext()) {
				Object sel = iterator.next();
				ITableFilter filter = null;
				
				if(sel instanceof Table) {
					Table table = (Table) sel;
					filter = revEngDef.createTableFilter();					
					if(StringHelper.isNotEmpty(table.getName())) {
						filter.setMatchName(table.getName());	
					}
					if(StringHelper.isNotEmpty(table.getCatalog())) {
						filter.setMatchCatalog(table.getCatalog());	
					}
					if(StringHelper.isNotEmpty(table.getSchema())) {
						filter.setMatchSchema(table.getSchema());	
					}
					filter.setExclude(Boolean.valueOf(exclude));					
				} else if(sel instanceof TableContainer) { // assume its a schema!
					TableContainer tc = (TableContainer) sel;
					filter = revEngDef.createTableFilter();
					filter.setMatchSchema(tc.getName());
					filter.setExclude(Boolean.valueOf(exclude));					
				}
				if(filter!=null) revEngDef.addTableFilter(filter);
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
			ITableFilter item = (ITableFilter) selection[i].getData();
			revEngDef.removeTableFilter(item);
		}
		tableFilters.setSelection(Math.min(sel, tableFilters.getItemCount()-1));
	}

	protected void doMoveDown() {
		TableItem[] selection = tableFilters.getSelection();
		for (int i = 0; i < selection.length; i++) {
			ITableFilter item = (ITableFilter) selection[i].getData();
			revEngDef.moveTableFilterDown(item);
		}
	}

	protected void doMoveUp() {
		TableItem[] selection = tableFilters.getSelection();
		for (int i = 0; i < selection.length; i++) {
			ITableFilter item = (ITableFilter) selection[i].getData();
			revEngDef.moveTableFilterUp(item);
		}
	}
}