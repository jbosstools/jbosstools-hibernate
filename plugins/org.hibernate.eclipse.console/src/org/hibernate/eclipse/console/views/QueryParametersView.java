package org.hibernate.eclipse.console.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.hibernate.Hibernate;
import org.hibernate.console.ConsoleQueryParameter;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.workbench.TableModelList;
import org.hibernate.type.NullableType;
import org.hibernate.type.Type;

public class QueryParametersView extends ViewPart {

	private Composite top = null;

	private Table queryParametersTable = null;

	private TableViewer tv;

	public QueryParametersView() {
		super();
	}

	public void createPartControl(Composite parent) {
		top = new Composite( parent, SWT.NONE );
		top.setLayout( new GridLayout() );
		createQueryParametersTable();

	}

	public void setFocus() {
		queryParametersTable.setFocus();

	}
	
	QueryParametersModelList list = new QueryParametersModelList(KnownConfigurations.getInstance().getQueryParameterList()); 
	

	/**
	 * This method initializes queryParametersTable
	 * 
	 */
	private void createQueryParametersTable() {
		GridData gridData = new org.eclipse.swt.layout.GridData();
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		queryParametersTable = new Table( top, SWT.FULL_SELECTION );
		queryParametersTable.setHeaderVisible( true );
		queryParametersTable.setLayoutData( gridData );
		queryParametersTable.setLinesVisible( true );
		TableColumn nameColumn = new TableColumn( queryParametersTable,
				SWT.NONE );
		nameColumn.setWidth( 60 );
		nameColumn.setText( "Name" );
		TableColumn typeColumn = new TableColumn( queryParametersTable,
				SWT.NONE );
		typeColumn.setWidth( 60 );
		typeColumn.setText( "Type" );
		TableColumn valueColumn = new TableColumn( queryParametersTable,
				SWT.NONE );
		valueColumn.setWidth( 100 );
		valueColumn.setText( "Value" );

		tv = new TableViewer( queryParametersTable );

		final List possibleTypes = new ArrayList();
		possibleTypes.add(Hibernate.STRING);
		possibleTypes.add(Hibernate.DATE);
		possibleTypes.add(Hibernate.TIMESTAMP);
		possibleTypes.add(Hibernate.TIME);
		possibleTypes.add(Hibernate.BIG_DECIMAL);
		possibleTypes.add(Hibernate.BIG_INTEGER);
		possibleTypes.add(Hibernate.INTEGER);
		
		tv.setCellModifier( new ICellModifier() {

			public void modify(Object element, String property, Object value) {
				TableItem item = (TableItem) element;
				ConsoleQueryParameter cqp = (ConsoleQueryParameter) item
						.getData();
				if ( "name".equals( property ) ) {
					cqp.setName( (String) value );
				}
				if ( "type".equals( property ) ) {
					Iterator iterator = possibleTypes.iterator();
					int i = 0;
					while(iterator.hasNext()) {
						NullableType type = (NullableType) iterator.next();
						if(i==((Integer)value).intValue()) {
							if(cqp.getType()!=type) {
								cqp.setType(type);
								cqp.setValue(ConsoleQueryParameter.NULL_MARKER); // have to reset to ensure it's working
							}
							break;
						}
						i++;
					}
				}
				if ( "value".equals( property ) ) {
					cqp.setValueFromString((String) value);
				}
				tv.refresh(cqp);
			}

			public Object getValue(Object element, String property) {
				ConsoleQueryParameter cqp = (ConsoleQueryParameter) element;
				if ( "name".equals( property ) ) {
					return cqp.getName();
				}
				if ( "type".equals( property ) ) {
					Iterator iterator = possibleTypes.iterator();
					NullableType type = cqp.getType();
					int i = 0;
					while(iterator.hasNext()) {
						if (type == ((Type) iterator.next())) {
							return new Integer(i);
						}						
						i++;
					}
				}
				if ( "value".equals( property ) ) {
					return cqp.getValueAsString();
				}
				return null;
			}

			public boolean canModify(Object element, String property) {
				return true;
			}
		} );

		tv.setContentProvider( new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				return ((TableModelList) inputElement).getList().toArray(new ConsoleQueryParameter[0]);
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}
		} );

		tv.setColumnProperties( new String[] { "name", "type", "value" } );
		
		
		String[] valueTypes = new String[possibleTypes.size()];
		
		Iterator iterator = possibleTypes.iterator();
		int i=0;
		while ( iterator.hasNext() ) {
			Type element = (Type) iterator.next();
			valueTypes[i++] = element.getName();
		}
		CellEditor[] editors = new CellEditor[3];
		editors[0] = new TextCellEditor( queryParametersTable );
		editors[1] = new ComboBoxCellEditor( queryParametersTable, valueTypes );
		editors[2] = new TextCellEditor( queryParametersTable );
		
		tv.setCellEditors( editors );

		tv.setLabelProvider( new ITableLabelProvider() {

			public void removeListener(ILabelProviderListener listener) {
			}

			public boolean isLabelProperty(Object element, String property) {
				return true;
			}

			public void dispose() {
			}

			public void addListener(ILabelProviderListener listener) {
			}

			public String getColumnText(Object element, int columnIndex) {
				ConsoleQueryParameter cqp = (ConsoleQueryParameter) element;

				switch (columnIndex) {
				case 0:
					return cqp.getName();
				case 1:
					return cqp.getType().getName();
				case 2:
					return cqp.getValueAsString();
				default:
					return null;
				}
			}

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

		} );
		tv.setInput( list );
		
		list.addObserver(new Observer() {
			public void update(java.util.Observable o, Object arg) {
				tv.refresh();
			}
		});
		ConsoleQueryParameter qcp = new ConsoleQueryParameter();
		qcp.setName( "from" );
		qcp.setType( Hibernate.STRING );
		qcp.setValueFromString( "max" );

		ConsoleQueryParameter cqp = new ConsoleQueryParameter();
		cqp.setName("param");
		cqp.setType(Hibernate.DATE);
		cqp.setValueFromString("");
		list.addParameter(cqp);		
		list.addParameter( qcp );

	}

	public void init(IViewSite site) throws PartInitException {
		super.init( site );
		NewRowAction newRowAction = new NewRowAction();
		site.getActionBars().getToolBarManager().add(newRowAction);
		
		site.getActionBars().getToolBarManager().add(new RemoveRowAction());
	}
	
	private class NewRowAction extends Action {
		public NewRowAction() {
			super( "Add parameter" );
		}

		public void run() {
			ConsoleQueryParameter cqp = new ConsoleQueryParameter();
			cqp.setName("param");
			cqp.setType(Hibernate.DATE);
			cqp.setValueFromString("");
			list.addParameter( cqp );
		}
	}

	private class RemoveRowAction extends Action {
		public RemoveRowAction() {
			super( "Remove parameter" );
		}

		public void run() {
			Object firstElement = ((IStructuredSelection)tv.getSelection()).getFirstElement();
			if(firstElement!=null) {
				tv.cancelEditing();
				list.removeParameter((ConsoleQueryParameter) firstElement);
			}
		}
	}
}
