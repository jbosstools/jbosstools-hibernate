package org.hibernate.eclipse.console.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.hibernate.console.ConsoleQueryParameter;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.QueryInputModel;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.hqleditor.HQLEditor;
import org.hibernate.hql.classic.ParserHelper;
import org.hibernate.type.NullableType;
import org.hibernate.type.Type;
import org.hibernate.util.StringHelper;

public class QueryParametersPage extends Page implements IQueryParametersPage {

	private Composite top = null;

	private Table queryParametersTable = null;

	private TableViewer tableViewer;

	private Label statusLabel;

	final QueryInputModel model;

	private ToggleActive toggleActive; 

	private Observer observer = new Observer() {
		public void update(java.util.Observable o, Object arg) {
			if(!tableViewer.getTable().isDisposed()) {
				tableViewer.refresh();
				tableViewer.getTable().setEnabled(!model.ignoreParameters());
			}
		}
	};

	private final HQLEditor editor;

		
	public QueryParametersPage(HQLEditor editor) {
		this.editor = editor;
		model = editor.getQueryInputModel();
	}

	public Control getControl() {
		return top;
	}	
	
	public void createControl(Composite parent) {
		top = new Composite( parent, SWT.NONE );
		top.setLayout( new GridLayout() );
		createQueryParametersTable();
		createStatusLabel();
		
		model.addObserver(observer);
		
		toggleActive.setChecked(model.ignoreParameters());
		tableViewer.getTable().setEnabled(!model.ignoreParameters());
		tableViewer.setInput(model);
		

	}

	private void createStatusLabel() {
		GridData gridData = new org.eclipse.swt.layout.GridData();
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;		
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		statusLabel = new Label( top, SWT.NULL );
		//statusLabel.setText("max");
		statusLabel.setLayoutData(gridData);
	}

	public void setFocus() {
		queryParametersTable.setFocus();
	}

	

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
		nameColumn.setWidth(100);
		nameColumn.setText( "Name" );
		TableColumn typeColumn = new TableColumn( queryParametersTable,
				SWT.NONE );
		typeColumn.setWidth(100);
		typeColumn.setText( "Type" );
		TableColumn valueColumn = new TableColumn( queryParametersTable,
				SWT.NONE );
		valueColumn.setWidth( 100 );
		valueColumn.setText( "Value" );
		
		tableViewer = new TableViewer( queryParametersTable );

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				if(statusLabel!=null) {
					Object firstElement = ((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
					if(firstElement instanceof ConsoleQueryParameter) {
						statusLabel.setText("Format: " + ((ConsoleQueryParameter)firstElement).getDefaultFormat());
					} else {
						statusLabel.setText("");
					}
				}
				
			}
			
		});
		final List possibleTypes = new ArrayList(ConsoleQueryParameter.getPossibleTypes());
		Collections.sort(possibleTypes, new Comparator() {
		
			public int compare(Object o1, Object o2) {
				Type t1 = (Type) o1;
				Type t2 = (Type) o2;
				return t1.getName().compareTo(t2.getName());
			}
		
		});
		
		tableViewer.setCellModifier( new ICellModifier() {

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
				tableViewer.refresh(cqp);
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

		tableViewer.setContentProvider( new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				return ((QueryInputModel) inputElement).getQueryParameters();
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}
		} );

		tableViewer.setColumnProperties( new String[] { "name", "type", "value" } );
		
		
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
		
		tableViewer.setCellEditors( editors );

		tableViewer.setLabelProvider( new ITableLabelProvider() {

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
		
	}

	public void init(IPageSite site) {
		super.init( site );
		NewRowAction newRowAction = new NewRowAction();
		site.getActionBars().getToolBarManager().add(newRowAction);
		
		site.getActionBars().getToolBarManager().add(new RemoveRowAction());
		
		toggleActive = new ToggleActive();
		site.getActionBars().getToolBarManager().add(toggleActive);
		
	}
	private class NewRowAction extends Action {
		public NewRowAction() {
			super( "" );
			setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.NEW_PARAMETER));
		}

		public void run() {
			ConsoleQueryParameter[] queryParameters = model.getQueryParameters();
			
			Map qp = new HashMap();
			for (int i = 0; i < queryParameters.length; i++) {
				ConsoleQueryParameter parameter = queryParameters[i];
				qp.put(parameter.getName(), parameter);
			}
			
			model.clear();
			
			String queryString = editor.getQuery();
			
			ConsoleQueryParameter cqp = null;
			int[] positions = StringHelper.locateUnquoted( queryString, '?' );
			for (int i = 0; i < positions.length; i++) {
				cqp = (ConsoleQueryParameter) qp.get(""+i);
				if(cqp==null) {
					cqp = model.createUniqueParameter(""+i);					
				}
				model.addParameter( cqp );
			}
			
			StringTokenizer st = new StringTokenizer(queryString, ParserHelper.HQL_SEPARATORS);
			Set result = new HashSet();

			while ( st.hasMoreTokens() ) {
				String string = st.nextToken();
				if( string.startsWith(ParserHelper.HQL_VARIABLE_PREFIX) ) {
					result.add( string.substring(1) );
				}
			}

			Iterator iterator = result.iterator();			
			while ( iterator.hasNext() ) {
				String paramName = (String) iterator.next();
				cqp = (ConsoleQueryParameter) qp.get(paramName);
				if(cqp==null) {
					cqp = model.createUniqueParameter(paramName);	
				}				
				model.addParameter(cqp);
			}
			
/*			if(cqp==null) {
				cqp = model.createUniqueParameter("param");
				model.addParameter( cqp );
			}*/
		}
	}
	
	private class RemoveRowAction extends Action {
		public RemoveRowAction() {
			super( "" );
			setImageDescriptor(getSite().getWorkbenchWindow().getWorkbench().getSharedImages().getImageDescriptor(org.eclipse.ui.ISharedImages.IMG_TOOL_DELETE));
		}

		public void run() {
			Object firstElement = ((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
			if(firstElement!=null) {
				tableViewer.cancelEditing();
				model.removeParameter((ConsoleQueryParameter) firstElement);
				if(model.getParameterCount()>0) {
					tableViewer.setSelection(new StructuredSelection(model.getQueryParameters()[0]));
				}
			}
		}
	}
	
	private class ToggleActive extends Action {
		public ToggleActive() {
			super("");
			setChecked(false);
			setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.IGNORE_PARAMETER));
		}
		
		public void run() {
			model.setIgnoreParameters(isChecked());
			setChecked(model.ignoreParameters());
		}
	}
	
	
	public void dispose() {
		super.dispose();
		
	}

	

}
