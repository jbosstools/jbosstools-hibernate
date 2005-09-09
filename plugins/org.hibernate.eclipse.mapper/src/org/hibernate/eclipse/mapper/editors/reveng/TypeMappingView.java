package org.hibernate.eclipse.mapper.editors.reveng;

import java.util.Iterator;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.console.model.ITableFilter;
import org.hibernate.eclipse.console.model.ITypeMapping;
import org.hibernate.eclipse.console.wizards.TreeToTableComposite;
import org.hibernate.eclipse.console.wizards.TypeMappingCellModifier;
import org.hibernate.eclipse.console.wizards.TypeMappingContentProvider;
import org.hibernate.eclipse.console.wizards.TypeMappingLabelProvider;
import org.hibernate.eclipse.console.workbench.AnyAdaptableLabelProvider;
import org.hibernate.eclipse.console.workbench.DeferredContentProvider;
import org.hibernate.eclipse.console.workbench.LazyDatabaseSchema;

public abstract class TypeMappingView extends TreeToTableComposite {

	static private class NullableTextCellEditor extends TextCellEditor {
		private NullableTextCellEditor(Composite parent) {
			super( parent );
		}

		protected void doSetValue(Object value) {
			if(value==null) { value=""; }
			super.doSetValue( value );
		}

		public Object doGetValue() {
			String str = (String) super.doGetValue();
			if(str==null || str.trim().length()==0) {
				return null;
			} else {
				return super.doGetValue();
			}
		}		
	}
	
	/** CellEditor that works like a texteditor, but returns/accepts Integer values. If the entered string is not parsable it returns null */
	static private final class IntegerCellEditor extends NullableTextCellEditor {
		private IntegerCellEditor(Composite parent) {
			super( parent );
		}

		protected void doSetValue(Object value) {
			if(value!=null && value instanceof Integer) {
				value = ((Integer)value).toString();
			}			
			super.doSetValue( value );
		}

		public Object doGetValue() {
			String str = (String) super.doGetValue();
			if(str==null || str.trim().length()==0) {
				return null;
			} else {
				try {
				return new Integer(Integer.parseInt((String) super.doGetValue()));
				} catch(NumberFormatException nfe) {
					return null;
				}
			}
		}
	}

	public TypeMappingView(Composite parent, int style) {
		super( parent, style );
	}

	TreeViewer viewer;

	private TableViewer tableViewer;

	private IReverseEngineeringDefinition revEngDef;
	
	public void setModel(IReverseEngineeringDefinition revEngDef) {
		this.revEngDef = revEngDef;
		tableViewer.setInput( revEngDef );
	}

	protected void initialize() {
		super.initialize();
		tableViewer = createTypeMappingViewer();

		viewer = new TreeViewer( tree );
		viewer.setLabelProvider( new AnyAdaptableLabelProvider() );
		viewer.setContentProvider( new DeferredContentProvider() );

		viewer.setInput( null );
	}

	private TableViewer createTypeMappingViewer() {
		TableViewer result = new TableViewer( rightTable );
		result.setUseHashlookup( true );
		result.setColumnProperties( new String[] { "jdbctype", "hibernatetype",
				"length", "scale", "precision" } );

		CellEditor[] editors = new CellEditor[result.getColumnProperties().length];
		editors[0] = new NullableTextCellEditor( result.getTable() );
		editors[1] = new NullableTextCellEditor( result.getTable() );
		editors[2] = new IntegerCellEditor( result.getTable() );
		editors[3] = new IntegerCellEditor( result.getTable() );
		editors[4] = new IntegerCellEditor( result.getTable() );

		result.setCellEditors( editors );
		result.setCellModifier( new TypeMappingCellModifier( result ) );
		result.setLabelProvider( new TypeMappingLabelProvider() );
		result.setContentProvider( new TypeMappingContentProvider( result ) );
		
		
		return result;
	}

	protected void doRefreshDatabaseSchema() {
		ConsoleConfiguration configuration = KnownConfigurations.getInstance()
				.find( getConsoleConfigurationName() );

		viewer.setInput( new LazyDatabaseSchema( configuration ) );

	}

	abstract protected String getConsoleConfigurationName();


	ITypeMapping[] getTypeMappingList() {
		return revEngDef.getTypeMappings();
	}

	private void doAdd() {
		ISelection selection = viewer.getSelection();

		if ( !selection.isEmpty() ) {
			StructuredSelection ss = (StructuredSelection) selection;
			Iterator iterator = ss.iterator();
			while ( iterator.hasNext() ) {
				Object sel = iterator.next();
				ITypeMapping typeMapping = null;

				if ( typeMapping != null )
					revEngDef.addTypeMapping( typeMapping );
			}
		} else {
			ITypeMapping createTypeMapping = revEngDef.createTypeMapping();
			createTypeMapping.setJDBCType("VARCHAR");
			createTypeMapping.setHibernateType("string");
			revEngDef.addTypeMapping(createTypeMapping);
		}
	}

	

	protected String[] getAddButtonLabels() {
		return new String[] { "Add..." };
	}

	protected void handleAddButtonPressed(int i) {
		switch (i) {
		case 0:
			doAdd();
			break;
		default:
			throw new IllegalArgumentException( i + " not a known button" );
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
		column.setText("JDBC Type");
		column.setWidth(100);
		
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Hibernate Type");
		column.setWidth(100);
		
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("Length");
		column.setWidth(100);

		column = new TableColumn(table, SWT.LEFT, 3);
		column.setText("Scale");
		column.setWidth(100);
		
		column = new TableColumn(table, SWT.LEFT, 3);
		column.setText("Precision");
		column.setWidth(100);
	}

	protected String getTableTitle() {
		return "Type mappings:";
	}
	
	protected String getTreeTitle() {
		return "Found types:";
	}
	
}
