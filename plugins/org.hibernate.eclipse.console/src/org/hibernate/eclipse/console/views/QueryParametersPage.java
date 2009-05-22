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
import org.eclipse.jface.viewers.CheckboxCellEditor;
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
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.QueryEditor;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.hql.classic.ParserHelper;
import org.hibernate.type.NullableType;
import org.hibernate.type.Type;
import org.hibernate.util.StringHelper;

public class QueryParametersPage extends Page implements IQueryParametersPage {

	private static final String NAME_PROPERTY = "name"; //$NON-NLS-1$
	private static final String TYPE_PROPERTY = "type"; //$NON-NLS-1$
	private static final String VALUE_PROPERTY = "value"; //$NON-NLS-1$
	private static final String NULL_PROPERTY = "null_prop"; //$NON-NLS-1$

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

	private final QueryEditor editor;


	public QueryParametersPage(QueryEditor editor) {
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
		nameColumn.setText( HibernateConsoleMessages.QueryParametersPage_name );
		TableColumn typeColumn = new TableColumn( queryParametersTable,
				SWT.NONE );
		typeColumn.setWidth(75);
		typeColumn.setText( HibernateConsoleMessages.QueryParametersPage_type );
		TableColumn valueColumn = new TableColumn( queryParametersTable,
				SWT.NONE );
		valueColumn.setWidth( 100 );
		valueColumn.setText( HibernateConsoleMessages.QueryParametersPage_value );
		TableColumn nullColumn = new TableColumn( queryParametersTable,
				SWT.NONE );
		nullColumn.setWidth( 32 );
		nullColumn.setText( HibernateConsoleMessages.QueryParametersPage_null );


		tableViewer = new TableViewer( queryParametersTable );

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				if(statusLabel!=null) {
					Object firstElement = ((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
					if(firstElement instanceof ConsoleQueryParameter) {
						statusLabel.setText(HibernateConsoleMessages.QueryParametersPage_format + ((ConsoleQueryParameter)firstElement).getDefaultFormat());
					} else {
						statusLabel.setText(""); //$NON-NLS-1$
					}
				}

			}

		});
		final List<Type> possibleTypes = new ArrayList<Type>(ConsoleQueryParameter.getPossibleTypes());
		Collections.sort(possibleTypes, new Comparator<Type>() {

			public int compare(Type t1, Type t2) {
				return t1.getName().compareTo(t2.getName());
			}

		});

		tableViewer.setCellModifier( new ICellModifier() {

			public void modify(Object element, String property, Object value) {
				TableItem item = (TableItem) element;
				ConsoleQueryParameter cqp = (ConsoleQueryParameter) item
						.getData();
				if ( NAME_PROPERTY.equals( property ) ) {
					cqp.setName( (String) value );
				}
				if ( TYPE_PROPERTY.equals( property ) ) {
					Iterator<Type> iterator = possibleTypes.iterator();
					int i = 0;
					while(iterator.hasNext()) {
						NullableType type = (NullableType) iterator.next();
						if(i==((Integer)value).intValue()) {
							if(cqp.getType()!=type) {
								cqp.setType(type);
								cqp.setNull(); // have to reset to ensure it's working
							}
							break;
						}
						i++;
					}
				}
				if ( VALUE_PROPERTY.equals( property ) ) {
					cqp.setValueFromString((String) value);
				}
				if ( NULL_PROPERTY.equals( property ) ) {
					if(cqp.isNull()) {
						cqp.setValueFromString( "" ); // best attempt to "unnull" //$NON-NLS-1$
					} else {
						cqp.setNull();
					}
				}
				tableViewer.refresh(cqp);
			}

			public Object getValue(Object element, String property) {
				ConsoleQueryParameter cqp = (ConsoleQueryParameter) element;
				if ( NAME_PROPERTY.equals( property ) ) {
					return cqp.getName();
				}
				if ( TYPE_PROPERTY.equals( property ) ) {
					Iterator<Type> iterator = possibleTypes.iterator();
					NullableType type = cqp.getType();
					int i = 0;
					while(iterator.hasNext()) {
						if (type == iterator.next()) {
							return Integer.valueOf(i);
						}
						i++;
					}
				}
				if ( VALUE_PROPERTY.equals( property ) ) {
					return cqp.getValueAsString();
				}
				if ( NULL_PROPERTY.equals( property )) {
					return Boolean.valueOf(cqp.isNull());
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

		String[] columnProperties = new String[] { NAME_PROPERTY, TYPE_PROPERTY, VALUE_PROPERTY, NULL_PROPERTY };
		tableViewer.setColumnProperties( columnProperties );


		String[] valueTypes = new String[possibleTypes.size()];

		Iterator<Type> iterator = possibleTypes.iterator();
		int i=0;
		while ( iterator.hasNext() ) {
			Type element = iterator.next();
			valueTypes[i++] = element.getName();
		}
		CellEditor[] editors = new CellEditor[columnProperties.length];
		editors[0] = new TextCellEditor( queryParametersTable );
		editors[1] = new ComboBoxCellEditor( queryParametersTable, valueTypes );
		editors[2] = new TextCellEditor( queryParametersTable );
		editors[3] = new CheckboxCellEditor( queryParametersTable );

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
				case 3:
					return null; //cqp.isNull()?"X":"";
				default:
					return null;
				}
			}

			public Image getColumnImage(Object element, int columnIndex) {
				if(columnIndex==3) {
					ConsoleQueryParameter cqp = (ConsoleQueryParameter) element;
					return cqp.isNull()?EclipseImages.getImage( ImageConstants.CHECKBOX_FULL ):EclipseImages.getImage( ImageConstants.CHECKBOX_EMPTY );
				} else {
					return null;
				}
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
			super( "" ); //$NON-NLS-1$
			setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.NEW_PARAMETER));
		}

		public void run() {
			ConsoleQueryParameter[] queryParameters = model.getQueryParameters();

			Map<String, ConsoleQueryParameter> qp = new HashMap<String, ConsoleQueryParameter>();
			for (int i = 0; i < queryParameters.length; i++) {
				ConsoleQueryParameter parameter = queryParameters[i];
				qp.put(parameter.getName(), parameter);
			}

			model.clear();

			String queryString = editor.getQueryString();

			ConsoleQueryParameter cqp = null;
			int[] positions = StringHelper.locateUnquoted( queryString, '?' );
			for (int i = 0; i < positions.length; i++) {
				cqp = qp.get(""+i); //$NON-NLS-1$
				if(cqp==null) {
					cqp = model.createUniqueParameter(""+i);					 //$NON-NLS-1$
				}
				model.addParameter( cqp );
			}

			StringTokenizer st = new StringTokenizer(queryString, ParserHelper.HQL_SEPARATORS);
			Set<String> result = new HashSet<String>();

			while ( st.hasMoreTokens() ) {
				String string = st.nextToken();
				if( string.startsWith(ParserHelper.HQL_VARIABLE_PREFIX) ) {
					result.add( string.substring(1) );
				}
			}

			Iterator<String> iterator = result.iterator();
			while ( iterator.hasNext() ) {
				String paramName = iterator.next();
				cqp = qp.get(paramName);
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
			super( "" ); //$NON-NLS-1$
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
			super(""); //$NON-NLS-1$
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
