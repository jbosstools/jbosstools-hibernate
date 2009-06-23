/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.console.wizards;

import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.console.model.ITableFilter;
import org.hibernate.eclipse.console.workbench.DeferredContentProvider;
import org.hibernate.eclipse.console.workbench.LazyDatabaseSchema;
import org.hibernate.eclipse.console.workbench.TableContainer;
import org.hibernate.eclipse.console.workbench.xpl.AnyAdaptableLabelProvider;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.hibernate.util.StringHelper;

public abstract class TableFilterView extends TreeToTableComposite {

	protected TreeViewer viewer;

	private TableViewer tableViewer;

	protected IReverseEngineeringDefinition revEngDef;

	public TableFilterView(Composite parent, int style) {
		super( parent, style );		
	}

	protected TreeViewer createTreeViewer() {
		TreeViewer viewer = new TreeViewer( tree );
		viewer.setLabelProvider( new AnyAdaptableLabelProvider() );
		viewer.setContentProvider( new DeferredContentProvider() );

		viewer.setInput( null );
		return viewer;		
	}

	public void setModel(IReverseEngineeringDefinition revEngDef) {
		this.revEngDef = revEngDef;
		tableViewer.setInput( revEngDef );
	}

	protected void initialize() {
		super.initialize();
		tableViewer = createTableFilterViewer();
		viewer = createTreeViewer();
	}

	private TableViewer createTableFilterViewer() {
		TableViewer result = new TableViewer( rightTable );
		result.setUseHashlookup( true );
		result.setColumnProperties( new String[] { "inclusion", "catalog",  //$NON-NLS-1$//$NON-NLS-2$
				"schema", "name" } ); //$NON-NLS-1$ //$NON-NLS-2$

		/*
		 * AutoResizeTableLayout autoResizeTableLayout = new
		 * AutoResizeTableLayout(result.getTable());
		 * result.getTable().setLayout(autoResizeTableLayout);
		 * autoResizeTableLayout.addColumnData(new ColumnWeightData(4));
		 * autoResizeTableLayout.addColumnData(new ColumnWeightData(40));
		 * autoResizeTableLayout.addColumnData(new ColumnWeightData(40));
		 * autoResizeTableLayout.addColumnData(new ColumnWeightData(40));
		 */
		CellEditor[] editors = new CellEditor[result.getColumnProperties().length];
		editors[0] = new CheckboxCellEditor( result.getTable() );
		editors[1] = new TextCellEditor( result.getTable() );
		editors[2] = new TextCellEditor( result.getTable() );
		editors[3] = new TextCellEditor( result.getTable() );

		result.setCellEditors( editors );
		result.setCellModifier( new TableFilterCellModifier( result ) );

		result.setLabelProvider( new TableFilterLabelProvider() );
		result.setContentProvider( new TableFilterContentProvider( result ) );
		return result;
	}

	protected void doRefreshTree() {
		ConsoleConfiguration configuration = KnownConfigurations.getInstance()
				.find( getConsoleConfigurationName() );

		if(configuration!=null) {
			viewer.setInput( new LazyDatabaseSchema( configuration ) );
		}

	}

	abstract protected String getConsoleConfigurationName();

	protected void doInclusion() {
		toggle( false );
	}

	ITableFilter[] getTableFilterList() {
		return revEngDef.getTableFilters();
	}

	protected void toggle(boolean exclude) {
		ISelection selection = viewer.getSelection();

		if ( !selection.isEmpty() ) {
			StructuredSelection ss = (StructuredSelection) selection;
			Iterator<?> iterator = ss.iterator();
			while ( iterator.hasNext() ) {
				Object sel = iterator.next();
				ITableFilter filter = null;

				if ( sel instanceof Table ) {
					Table table = (Table) sel;
					filter = revEngDef.createTableFilter();
					if ( StringHelper.isNotEmpty( table.getName() ) ) {
						filter.setMatchName( table.getName() );
					}
					if ( StringHelper.isNotEmpty( table.getCatalog() ) ) {
						filter.setMatchCatalog( table.getCatalog() );
					}
					if ( StringHelper.isNotEmpty( table.getSchema() ) ) {
						filter.setMatchSchema( table.getSchema() );
					}
					filter.setExclude( Boolean.valueOf( exclude ) );
				}
				else if ( sel instanceof TableContainer ) { // assume its a
															// schema!
					TableContainer tc = (TableContainer) sel;
					filter = revEngDef.createTableFilter();
					String schema = tc.getName();
					if(schema==null || "".equals(schema)) { //$NON-NLS-1$
						filter.setMatchCatalog(".*"); //$NON-NLS-1$
						filter.setMatchSchema(".*"); //$NON-NLS-1$
					} else { // fake catalog handling
						String catalog = StringHelper.qualifier(schema);
						schema = StringHelper.unqualify(schema);
						filter.setMatchCatalog( "".equals(catalog)?".*":catalog ); //$NON-NLS-1$ //$NON-NLS-2$
						filter.setMatchSchema( "".equals(schema)?".*":schema ); //$NON-NLS-1$ //$NON-NLS-2$
					}
					filter.setMatchName(".*"); //$NON-NLS-1$
					filter.setExclude( Boolean.valueOf( exclude ) );
				} else if ( sel instanceof Column ) {
					// we ignore column since at the moment we dont know which table is there.
					return;
				} else {
					filter = revEngDef.createTableFilter();
					filter.setExclude( Boolean.valueOf( exclude ) );
				}
				if ( filter != null )
					revEngDef.addTableFilter( filter );
			}
		} else {
			ITableFilter filter = revEngDef.createTableFilter();
			filter.setMatchName(".*"); //$NON-NLS-1$
			filter.setExclude( Boolean.valueOf( exclude ) );
			revEngDef.addTableFilter( filter );
		}
	}

	protected void doExclusion() {
		toggle( true );
	}

	protected String[] getAddButtonLabels() {
		return new String[] { HibernateConsoleMessages.TableFilterView_include, HibernateConsoleMessages.TableFilterView_exclude };
	}

	protected void handleAddButtonPressed(int i) {
		switch (i) {
		case 0:
			doInclusion();
			break;
		case 1:
			doExclusion();
			break;
		default:
			throw new IllegalArgumentException(NLS.bind(HibernateConsoleMessages.TableFilterView_not_known_button, i) );
		}
	}

	protected void doRemoveAll() {
		if(MessageDialog.openQuestion( getShell(), HibernateConsoleMessages.TableFilterView_remove_all_filters , HibernateConsoleMessages.TableFilterView_do_you_want_to_remove_all_filters)) {
			revEngDef.removeAllTableFilters();
		}
	}

	protected void doRemove() {
		int sel = rightTable.getSelectionIndex();
		TableItem[] selection = rightTable.getSelection();
		for (int i = 0; i < selection.length; i++) {
			ITableFilter item = (ITableFilter) selection[i].getData();
			revEngDef.removeTableFilter( item );
		}
		rightTable
				.setSelection( Math.min( sel, rightTable.getItemCount() - 1 ) );
	}

	protected void doMoveDown() {
		TableItem[] selection = rightTable.getSelection();
		for (int i = 0; i < selection.length; i++) {
			ITableFilter item = (ITableFilter) selection[i].getData();
			revEngDef.moveTableFilterDown( item );
		}
	}

	protected void doMoveUp() {
		TableItem[] selection = rightTable.getSelection();
		for (int i = 0; i < selection.length; i++) {
			ITableFilter item = (ITableFilter) selection[i].getData();
			revEngDef.moveTableFilterUp( item );
		}
	}

	protected void createTableColumns(org.eclipse.swt.widgets.Table table) {
		TableColumn column = new TableColumn(table, SWT.CENTER, 0);
		column.setText(HibernateConsoleMessages.TableFilterView_sign);
		column.setWidth(20);

		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText(HibernateConsoleMessages.TableFilterView_catalog);
		column.setWidth(100);

		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText(HibernateConsoleMessages.TableFilterView_schema);
		column.setWidth(100);

		column = new TableColumn(table, SWT.LEFT, 3);
		column.setText(HibernateConsoleMessages.TableFilterView_table);
		column.setWidth(100);
	}

	public void dispose() {
		super.dispose();
	}
}