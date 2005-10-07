package org.hibernate.eclipse.mapper.editors.reveng;

import java.util.Iterator;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.hibernate.cfg.reveng.JDBCToHibernateTypeHelper;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.console.model.ITypeMapping;
import org.hibernate.eclipse.console.wizards.TreeToTableComposite;
import org.hibernate.eclipse.console.wizards.TypeMappingCellModifier;
import org.hibernate.eclipse.console.wizards.TypeMappingContentProvider;
import org.hibernate.eclipse.console.wizards.TypeMappingLabelProvider;
import org.hibernate.eclipse.console.workbench.AnyAdaptableLabelProvider;
import org.hibernate.eclipse.console.workbench.DeferredContentProvider;
import org.hibernate.eclipse.console.workbench.LazyDatabaseSchema;
import org.hibernate.mapping.Column;

public abstract class TypeMappingView extends TreeToTableComposite {

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
		editors[0] = new NullableTextCellEditor( result.getTable() );//new ComboBoxCellEditor( result.getTable(), JDBCToHibernateTypeHelper.getJDBCTypes() );
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

	protected void doRefreshTree() {
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
				if(sel instanceof Column) {
					Column col = (Column) sel;
					Integer sqlTypeCode = col.getSqlTypeCode();
					if(sqlTypeCode!=null) {
						ITypeMapping typeMapping = revEngDef.createTypeMapping();
						
						typeMapping.setJDBCType(JDBCToHibernateTypeHelper.getJDBCTypeName(sqlTypeCode.intValue()));
						int length = col.getLength();
						int precision = col.getPrecision();
						int scale = col.getScale();
						typeMapping.setHibernateType(JDBCToHibernateTypeHelper.getPreferredHibernateType(sqlTypeCode.intValue(), length, precision, scale));
						if(JDBCToHibernateTypeHelper.typeHasLength(sqlTypeCode.intValue())) {
							if(length!=0 && Column.DEFAULT_LENGTH!=length) {
								typeMapping.setLength(new Integer(length));		
							}
						} 
						if(JDBCToHibernateTypeHelper.typeHasScaleAndPrecision(sqlTypeCode.intValue())) {
							if(precision!=0 && Column.DEFAULT_PRECISION!=precision) {
								typeMapping.setPrecision(new Integer(precision));
							}
							if(scale!=0 && Column.DEFAULT_SCALE!=scale) {
								typeMapping.setScale(new Integer(scale));
							}
						}
						revEngDef.addTypeMapping( typeMapping );
					}
				} else {
					createDefaultTypeMapping();
				}
			}
		} else {
			createDefaultTypeMapping();
		}
	}

	private void createDefaultTypeMapping() {
		ITypeMapping createTypeMapping = revEngDef.createTypeMapping();
		createTypeMapping.setJDBCType("VARCHAR");
		createTypeMapping.setHibernateType("string");
		revEngDef.addTypeMapping(createTypeMapping);
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
			ITypeMapping item = (ITypeMapping) selection[i].getData();
			revEngDef.removeTypeMapping( item );
		}
		rightTable
				.setSelection( Math.min( sel, rightTable.getItemCount() - 1 ) );
	}

	protected void doMoveDown() {
		TableItem[] selection = rightTable.getSelection();
		for (int i = 0; i < selection.length; i++) {
			ITypeMapping item = (ITypeMapping) selection[i].getData();
			revEngDef.moveTypeMappingDown( item );
		}
	}

	protected void doMoveUp() {
		TableItem[] selection = rightTable.getSelection();
		for (int i = 0; i < selection.length; i++) {
			ITypeMapping item = (ITypeMapping) selection[i].getData();
			revEngDef.moveTypeMappingUp( item );
		}
	}
	
	protected void createTableColumns(org.eclipse.swt.widgets.Table table) {
		TableColumn column = new TableColumn(table, SWT.CENTER, 0);		
		column.setText("JDBC Type");
		column.setWidth(100);
		
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Hibernate Type");
		column.setWidth(150);
		
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
		return "Database schema:";
	}
	
}
