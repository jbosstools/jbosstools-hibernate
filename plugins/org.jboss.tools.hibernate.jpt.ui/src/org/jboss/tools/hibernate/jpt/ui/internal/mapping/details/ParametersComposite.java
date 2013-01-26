/*******************************************************************************
 * Copyright (c) 2009-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.mapping.details;

import java.util.Iterator;
import java.util.ListIterator;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jpt.common.ui.internal.swt.ColumnAdapter;
import org.eclipse.jpt.common.ui.internal.util.PaneVisibilityEnabler;
import org.eclipse.jpt.common.ui.internal.widgets.AddRemovePane.Adapter;
import org.eclipse.jpt.common.ui.internal.widgets.AddRemoveTablePane;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.internal.iterator.EmptyListIterator;
import org.eclipse.jpt.common.utility.internal.model.value.CollectionPropertyValueModelAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.ListAspectAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.SimpleCollectionValueModel;
import org.eclipse.jpt.common.utility.internal.model.value.TransformationPropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.CollectionValueModel;
import org.eclipse.jpt.common.utility.model.value.ListValueModel;
import org.eclipse.jpt.common.utility.model.value.ModifiableCollectionValueModel;
import org.eclipse.jpt.common.utility.model.value.ModifiablePropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.jboss.tools.hibernate.jpt.core.internal.context.Parameter;
import org.jboss.tools.hibernate.jpt.core.internal.context.ParametrizedElement;

/**
 * @author Dmitry Geraskov
 *
 */
public class ParametersComposite extends Pane<ParametrizedElement> {

	private ModifiableCollectionValueModel<Parameter> selectedParametersModel;
	private PropertyValueModel<Parameter> selectedParameterModel;

	/**
	 * Creates a new <code>ParametersComposite</code>.
	 *
	 * @param parentPane The parent pane of this one
	 * @param parent The parent container
	 */
	public ParametersComposite(Pane<?> parentPane,
	      Composite container, PropertyValueModel<? extends ParametrizedElement> generatorHolder) {

		super(parentPane, generatorHolder, container);
	}


	private PropertyValueModel<Boolean> buildPaneEnableHolder() {
		return new TransformationPropertyValueModel<ParametrizedElement, Boolean>(getSubjectHolder()) {
			@Override
			protected Boolean transform(ParametrizedElement element) {
				return (element != null);
			}
		};
	}

	private Adapter<Parameter> buildParameterAdapter() {
		return new AddRemoveTablePane.AbstractAdapter<Parameter>() {
//			@Override
//			public void addNewItem(ObjectListSelectionModel listSelectionModel) {
//				Parameter parameter = getSubject().addParameter(getSubject().getParametersSize());
//				ParametersComposite.this.parameterHolder.setValue(parameter);
//			}
//
//			@Override
//			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
//				for (Object item : listSelectionModel.selectedValues()) {
//					getSubject().removeParameter((Parameter) item);
//				}
//			}

			@Override
			public Parameter addNewItem() {
				Parameter parameter = getSubject().addParameter(getSubject().getParametersSize());
//				ParametersComposite.this.parameterHolder.setValue(parameter);
				return parameter;
			}

			@Override
			public void removeSelectedItems(
					CollectionValueModel<Parameter> selectedItemsModel) {
				Iterator<Parameter> iterator = selectedItemsModel.iterator();
				while (iterator.hasNext()) {
					Parameter parameter = iterator.next();
					getSubject().removeParameter(parameter);
				}
			}
		};
	}

	private ModifiableCollectionValueModel<Parameter> buildSelectedParametersModel() {
		return new SimpleCollectionValueModel<Parameter>();
	};

	private ITableLabelProvider buildParameterLabelProvider() {
		return new TableLabelProvider();
	}

	private ListValueModel<Parameter> buildParameterListHolder() {
		return new ListAspectAdapter<ParametrizedElement, Parameter>(
				getSubjectHolder(),
				ParametrizedElement.PARAMETERS_LIST) {
			@Override
			protected ListIterator<Parameter> listIterator_() {
				if (this.subject == null ){
					return EmptyListIterator.instance();
				} else {
					return this.subject.getParameters().iterator();
				}
			}

			@Override
			protected int size_() {
				return this.subject == null ? 0 : this.subject.getParametersSize();
			}
		};
	}

	@Override
	protected void initialize() {
		super.initialize();
		this.selectedParametersModel = buildSelectedParametersModel();
		this.selectedParameterModel = buildSelectedParameterModel(selectedParametersModel);
	}

	private PropertyValueModel<Parameter> buildSelectedParameterModel(CollectionValueModel<Parameter> selectedParametersModel) {
		return new CollectionPropertyValueModelAdapter<Parameter, Parameter>(selectedParametersModel) {
			@Override
			protected Parameter buildValue() {
				if (this.collectionModel.size() == 1) {
					return this.collectionModel.iterator().next();
				}
				return null;
			}
		};
	}

	protected PropertyValueModel<Parameter> getSelectedParameterModel() {
		return this.selectedParameterModel;
	}
	
	@Override
	protected void initializeLayout(Composite container) {

		TablePane tablePane = new TablePane(container);
		installPaneEnabler(tablePane);
	}

	private PaneVisibilityEnabler installPaneEnabler(TablePane tablePane) {
		return new PaneVisibilityEnabler(buildPaneEnableHolder(), tablePane);
	}

	private static class ParameterColumnAdapter implements ColumnAdapter<Parameter> {

		static final int COLUMN_COUNT = 2;
		static final int NAME_COLUMN_INDEX = 0;
		static final int VALUE_COLUMN_INDEX = 1;

		private ModifiablePropertyValueModel<String> buildNameHolder(Parameter subject) {
			return new PropertyAspectAdapter<Parameter, String>(Parameter.NAME_PROPERTY, subject) {
				@Override
				protected String buildValue_() {
					return this.subject.getName();
				}

				@Override
				protected void setValue_(String value) {
					this.subject.setName(value);
				}
			};
		}

		private ModifiablePropertyValueModel<?> buildValueHolder(Parameter subject) {
			return new PropertyAspectAdapter<Parameter, String>(Parameter.VALUE_PROPERTY, subject) {
				@Override
				protected String buildValue_() {
					return this.subject.getValue();
				}

				@Override
				protected void setValue_(String value) {
					this.subject.setValue(value);
				}
			};
		}

		@Override
		public ModifiablePropertyValueModel<?>[] cellModels(Parameter subject) {
			ModifiablePropertyValueModel<?>[] models = new ModifiablePropertyValueModel<?>[COLUMN_COUNT];
			models[NAME_COLUMN_INDEX]  = buildNameHolder(subject);
			models[VALUE_COLUMN_INDEX] = buildValueHolder(subject);
			return models;
		}

		@Override
		public int columnCount() {
			return COLUMN_COUNT;
		}

		@Override
		public String columnName(int columnIndex) {

			switch (columnIndex) {
				case ParameterColumnAdapter.NAME_COLUMN_INDEX: {
					return HibernateUIMappingMessages.ParametersComposite_nameColumn;
				}

				case ParameterColumnAdapter.VALUE_COLUMN_INDEX: {
					return HibernateUIMappingMessages.ParametersComposite_valueColumn;
				}

				default: {
					return null;
				}
			}
		}
	}

	private class TableLabelProvider extends LabelProvider
	                                 implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {

			Parameter parameter = (Parameter) element;
			String value = ""; //$NON-NLS-1$

			switch (columnIndex) {
				case ParameterColumnAdapter.NAME_COLUMN_INDEX: {
					value = parameter.getName();
					break;
				}

				case ParameterColumnAdapter.VALUE_COLUMN_INDEX: {
					value = parameter.getValue();
					break;
				}
			}

			if (value == null) {
				value = ""; //$NON-NLS-1$
			}

			return value;
		}
	}

	private class TablePane extends AddRemoveTablePane<ParametrizedElement, Parameter> {

		private TablePane(Composite parent) {
			super(ParametersComposite.this,
			      parent,
			      buildParameterAdapter(),
			      buildParameterListHolder(),
			      ParametersComposite.this.selectedParametersModel,
			      buildParameterLabelProvider());
		}

		private CellEditor[] buildCellEditors(Table table) {
			return new CellEditor[] {
				new TextCellEditor(table),
				new TextCellEditor(table)
			};
		}

		private ICellModifier buildCellModifier() {
			return new ICellModifier() {

				@Override
				public boolean canModify(Object element, String property) {
					return true;
				}

				@Override
				public Object getValue(Object element, String property) {
					Parameter parameter = (Parameter) element;
					String value = ""; //$NON-NLS-1$

					if (property == Parameter.NAME_PROPERTY) {
						value = parameter.getName();
					}
					else if (property == Parameter.VALUE_PROPERTY) {
						value = parameter.getValue();
					}

					if (value == null) {
						value = ""; //$NON-NLS-1$
					}

					return value;
				}

				@Override
				public void modify(Object element, String property, Object value) {
					Parameter parameter;

					if (element instanceof TableItem) {
						TableItem tableItem = (TableItem) element;
						parameter = (Parameter) tableItem.getData();
					}
					else {
						parameter = (Parameter) element;
					}

					if (property == Parameter.NAME_PROPERTY) {
						 parameter.setName(value.toString());
					}
					else if (property == Parameter.VALUE_PROPERTY) {
						 parameter.setValue(value.toString());
					}
				}
			};
		}

		@Override
		protected ColumnAdapter<Parameter> buildColumnAdapter() {
			return new ParameterColumnAdapter();
		}

		private String[] buildColumnProperties() {
			return new String[] {
				Parameter.NAME_PROPERTY,
				Parameter.VALUE_PROPERTY
			};
		}

		@Override
		protected void initializeMainComposite(Composite container,
		                                       Adapter<Parameter> adapter,
		                                       ListValueModel<?> listHolder,
		                                       ModifiableCollectionValueModel<Parameter> selectedItemHolder,
		                                       IBaseLabelProvider labelProvider,
		                                       String helpId) {

			super.initializeMainComposite(
				container,
				adapter,
				listHolder,
				selectedItemHolder,
				labelProvider,
				helpId
			);

			Table table = getMainControl();

			TableViewer tableViewer = new TableViewer(table);
			tableViewer.setCellEditors(buildCellEditors(table));
			tableViewer.setCellModifier(buildCellModifier());
			tableViewer.setColumnProperties(buildColumnProperties());
		}
	}
}
