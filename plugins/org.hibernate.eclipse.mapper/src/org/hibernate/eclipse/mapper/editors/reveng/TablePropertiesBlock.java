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
package org.hibernate.eclipse.mapper.editors.reveng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.eclipse.console.model.IRevEngColumn;
import org.hibernate.eclipse.console.model.IRevEngTable;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.console.workbench.DeferredContentProvider;
import org.hibernate.eclipse.console.workbench.LazyDatabaseSchema;
import org.hibernate.eclipse.console.workbench.xpl.AnyAdaptableLabelProvider;
import org.hibernate.eclipse.mapper.MapperMessages;
import org.hibernate.eclipse.mapper.editors.ReverseEngineeringEditor;
import org.hibernate.eclipse.mapper.model.RevEngColumnAdapter;
import org.hibernate.eclipse.mapper.model.RevEngGeneratorAdapter;
import org.hibernate.eclipse.mapper.model.RevEngParamAdapter;
import org.hibernate.eclipse.mapper.model.RevEngPrimaryKeyAdapter;
import org.hibernate.eclipse.mapper.model.RevEngTableAdapter;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;

public class TablePropertiesBlock extends MasterDetailsBlock {

	private TreeViewer viewer;
	private final ReverseEngineeringEditor editor;

	public TablePropertiesBlock(ReverseEngineeringEditor editor) {
		this.editor = editor;
	}

	public SashForm getComposite() {
		return sashForm;
	}

	protected void createMasterPart(final IManagedForm managedForm,	Composite parent) {
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,true));
		final ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Section section = toolkit.createSection( parent, Section.DESCRIPTION |
				Section.FOCUS_TITLE | Section.TWISTIE | Section.EXPANDED);
		section.setText( MapperMessages.TablePropertiesBlock_tables_columns );
		section.setDescription( MapperMessages.TablePropertiesBlock_explicitly_control_settings_for_table_columns );
		section.marginWidth = 10;
		section.marginHeight = 5;
		toolkit.createCompositeSeparator( section );
		Composite client = toolkit.createComposite( section, SWT.WRAP );
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		client.setLayout( layout );
		Tree t = toolkit.createTree( client, SWT.NULL );
		GridData gd = new GridData( GridData.FILL_BOTH );
		gd.heightHint = 20;
		gd.widthHint = 100;
		gd.verticalSpan = 2;
		t.setLayoutData( gd );
		toolkit.paintBordersFor( client );

		Button btnAdd = toolkit.createButton( client, MapperMessages.TablePropertiesBlock_add, SWT.PUSH );
		btnAdd.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				doAdd();
			}

		});
		gd = new GridData( GridData.VERTICAL_ALIGN_BEGINNING );
		btnAdd.setLayoutData( gd );

		Button btnDel = toolkit.createButton( client, MapperMessages.TablePropertiesBlock_delete, SWT.PUSH );
		btnDel.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				doDelete();
			}

		});
		gd = new GridData( GridData.VERTICAL_ALIGN_BEGINNING );
		btnDel.setLayoutData( gd );

		section.setClient( client );
		final SectionPart spart = new SectionPart( section ) {
			public boolean setFormInput(Object input) {
				if(input instanceof IReverseEngineeringDefinition) {
					viewer.setInput(input);
					return true;
				}
				return false;
			}
		};
		managedForm.addPart( spart );
		viewer = new TreeViewer( t );
		viewer.addSelectionChangedListener( new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged( spart, event.getSelection() );
			}
		} );
		viewer.setLabelProvider( new TablePropertiesLabelProvider() );
		TablePropertiesContentProvider tablePropertiesContentProvider = new TablePropertiesContentProvider();
		viewer.setContentProvider( tablePropertiesContentProvider );
	}

	protected void doAdd() {
		CheckedTreeSelectionDialog dialog = createTreeSelectionDialog();


		LazyDatabaseSchema lds = editor.getLazyDatabaseSchema();

		Map tables = new HashMap();
		Map columns = new HashMap();

		if (lds == null) {
			String tableName = "", namePrefix = "TABLE_";  //$NON-NLS-1$  //$NON-NLS-2$
			IRevEngTable retable = editor.getReverseEngineeringDefinition().createTable();
			retable.setCatalog(""); //$NON-NLS-1$
			retable.setSchema(""); //$NON-NLS-1$
			TreeSet ts = new TreeSet();
			IRevEngTable[] retables = editor.getReverseEngineeringDefinition().getTables();
			char separartor = '%';
			for (int i = 0; i < retables.length; i++) {
				ts.add(retables[i].getCatalog() + separartor + retables[i].getSchema() + 
						separartor + retables[i].getName());
			}
			String strCatalogSchema = retable.getCatalog() + separartor + retable.getSchema() + separartor;
			int i = 0;
			do {
				tableName = namePrefix + (i++);
			} while (ts.contains(strCatalogSchema + tableName));
			retable.setName(tableName);
			editor.getReverseEngineeringDefinition().addTable(retable);
		}
		else {
			dialog.setTitle(MapperMessages.TablePropertiesBlock_add_tables_columns);
			dialog.setMessage(MapperMessages.TablePropertiesBlock_select_tables_columns);
			dialog.setInput(lds);
			dialog.setContainerMode(true);
			dialog.open();
			Object[] result = dialog.getResult();
			TableIdentifier lastTable = null;
			if(result!=null) {
				for (int i = 0; i < result.length; i++) {
					Object object = result[i];
					if(object instanceof Table) {
						Table table = (Table) object;
						tables.put(TableIdentifier.create(table), object);
						lastTable = TableIdentifier.create(table);
					} else if (object instanceof Column) {
						List existing = (List) columns.get(lastTable);
						if(existing==null) {
							existing = new ArrayList();
							columns.put(lastTable,existing);
						}
						existing.add(object);
					} else if (object instanceof PrimaryKey) {
						List existing = (List) columns.get(lastTable);
						if(existing==null) {
							existing = new ArrayList();
							columns.put(lastTable,existing);
						}
						existing.addAll(((PrimaryKey)object).getColumns());
					}
				}
			}

			Iterator iterator = tables.entrySet().iterator();
			while ( iterator.hasNext() ) {
				Map.Entry element = (Map.Entry) iterator.next();
				Table table = (Table) element.getValue();
				IRevEngTable retable = null;
				//	editor.getReverseEngineeringDefinition().findTable(TableIdentifier.create(table));
				if(retable==null) {
					retable = editor.getReverseEngineeringDefinition().createTable();
					retable.setCatalog(table.getCatalog());
					retable.setSchema(table.getSchema());
					retable.setName(table.getName());
					editor.getReverseEngineeringDefinition().addTable(retable);
				}

				List columnList = (List) columns.get(element.getKey());
				if(columnList!=null) {
					Iterator colIterator = columnList.iterator();
					while ( colIterator.hasNext() ) {
						Column column = (Column) colIterator.next();
						IRevEngColumn revCol = editor.getReverseEngineeringDefinition().createColumn();
						revCol.setName(column.getName());
						if (column.getSqlType() != null){
							revCol.setJDBCType(column.getSqlType()); // TODO: should not be required
						}
						retable.addColumn(revCol);
					}
				}
			}
			//editor.getReverseEngineeringDefinition();
		}


	}

	protected void doDelete() {
		ISelection sel = viewer.getSelection();
		if (sel.isEmpty() || !(sel instanceof TreeSelection)) {
			return;
		}
		boolean updateSelection = false;
		TreeSelection ts = (TreeSelection)sel;
		List list = ts.toList();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Object obj = it.next();
			if (obj instanceof IRevEngTable) {
				IRevEngTable retable = (IRevEngTable)obj;
				if (retable instanceof RevEngTableAdapter) {
					updateSelection = true;
				}
				editor.getReverseEngineeringDefinition().removeTable(retable);
			}
			else if (obj instanceof IRevEngColumn) {
				IRevEngColumn recolumn = (IRevEngColumn)obj;
				if (recolumn instanceof RevEngColumnAdapter) {
					updateSelection = true;
				}
				editor.getReverseEngineeringDefinition().removeColumn(recolumn);
			}
		}
		if (updateSelection) {
			// if it possible select first item
			TreeItem[] treeItems = viewer.getTree().getItems();
			if (treeItems.length > 0) {
				viewer.getTree().setSelection(treeItems[0]);
			}
		}
	}

	private CheckedTreeSelectionDialog createTreeSelectionDialog() {
		return new CheckedTreeSelectionDialog(getComposite().getShell(), new AnyAdaptableLabelProvider(), new DeferredContentProvider()) {

			protected Composite createSelectionButtons(Composite composite) {
		        Composite buttonComposite = new Composite(composite, SWT.RIGHT);
		        GridLayout layout = new GridLayout();
		        layout.numColumns = 2;
		        buttonComposite.setLayout(layout);
		        buttonComposite.setFont(composite.getFont());
		        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END
		                | GridData.GRAB_HORIZONTAL);
		        data.grabExcessHorizontalSpace = true;
		        composite.setData(data);
		        Button selectButton = createButton(buttonComposite,
		                IDialogConstants.SELECT_ALL_ID, MapperMessages.TablePropertiesBlock_select_all_children,
		                false);
		        SelectionListener listener = new SelectionAdapter() {
		            public void widgetSelected(SelectionEvent e) {
		                IStructuredSelection viewerElements = (IStructuredSelection) getTreeViewer().getSelection();
		                Iterator iterator = viewerElements.iterator();
		                while(iterator.hasNext()) {
		                	getTreeViewer().setSubtreeChecked(iterator.next(), true);
		                }
		                updateOKStatus();
		            }
		        };
		        selectButton.addSelectionListener(listener);
		        Button deselectButton = createButton(buttonComposite,
		                IDialogConstants.DESELECT_ALL_ID, MapperMessages.TablePropertiesBlock_deselect_all,
		                false);
		        listener = new SelectionAdapter() {
		            public void widgetSelected(SelectionEvent e) {
		                getTreeViewer().setCheckedElements(new Object[0]);
		                updateOKStatus();
		            }
		        };
		        deselectButton.addSelectionListener(listener);
		        return buttonComposite;
		    }


		};
	}

	protected void createToolBarActions(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();

		Action haction = new Action( "hor", IAction.AS_RADIO_BUTTON ) { //$NON-NLS-1$
			public void run() {
				sashForm.setOrientation( SWT.HORIZONTAL );
				form.reflow( true );
			}
		};
		haction.setChecked( true );
		haction.setToolTipText( MapperMessages.TablePropertiesBlock_horizontal_orientation );
		Action vaction = new Action( "ver", IAction.AS_RADIO_BUTTON ) { //$NON-NLS-1$
			public void run() {
				sashForm.setOrientation( SWT.VERTICAL );
				form.reflow( true );
			}
		};
		vaction.setChecked( false );
		vaction.setToolTipText( MapperMessages.TablePropertiesBlock_vertical_orientation );
		form.getToolBarManager().add( haction );
		form.getToolBarManager().add( vaction );
	}

	protected void registerPages(DetailsPart dp) {
		dp.registerPage( RevEngColumnAdapter.class, new ColumnDetailsPage() );
		dp.registerPage( RevEngTableAdapter.class, new TableDetailsPage() );
		dp.registerPage( RevEngGeneratorAdapter.class, new GeneratorDetailsPage() );
		dp.registerPage( RevEngParamAdapter.class, new ParamDetailsPage() );
		dp.registerPage( RevEngPrimaryKeyAdapter.class, new PrimaryKeyDetailsPage() );
		//dp.registerPage( org.hibernate.mapping.Table.class, new TypeOneDetailsPage() );
	}
}