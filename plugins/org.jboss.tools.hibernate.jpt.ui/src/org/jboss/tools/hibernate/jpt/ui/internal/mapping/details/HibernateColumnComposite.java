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

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jpt.common.ui.internal.widgets.IntegerCombo;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.ui.internal.widgets.TriStateCheckBox;
import org.eclipse.jpt.common.utility.internal.collection.CollectionTools;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.TransformationPropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.ModifiablePropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.context.BaseColumn;
import org.eclipse.jpt.jpa.core.context.Column;
import org.eclipse.jpt.jpa.core.context.NamedColumn;
import org.eclipse.jpt.jpa.db.Table;
import org.eclipse.jpt.jpa.ui.internal.JpaHelpContextIds;
import org.eclipse.jpt.jpa.ui.internal.details.BasicMappingComposite;
import org.eclipse.jpt.jpa.ui.internal.details.IdMappingComposite;
import org.eclipse.jpt.jpa.ui.internal.details.JptUiDetailsMessages;
import org.eclipse.jpt.jpa.ui.internal.details.VersionMappingComposite;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateColumn;
import org.jboss.tools.hibernate.jpt.ui.internal.mappings.db.xpl.ColumnCombo;
import org.jboss.tools.hibernate.jpt.ui.internal.mappings.db.xpl.DatabaseObjectCombo;
import org.jboss.tools.hibernate.jpt.ui.internal.mappings.db.xpl.TableCombo;

/**
 * Here the layout of this pane:
 * <pre>
 * -----------------------------------------------------------------------------
 * | ------------------------------------------------------------------------- |
 * | |                                                                       | |
 * | | ColumnCombo                                                           | |
 * | |                                                                       | |
 * | ------------------------------------------------------------------------- |
 * | ------------------------------------------------------------------------- |
 * | |                                                                       | |
 * | | TableCombo                                                            | |
 * | |                                                                       | |
 * | ------------------------------------------------------------------------- |
 * |                                                                           |
 * | > Details                                                                 |
 * |                                                                           |
 * |   x Insertable                                                            |
 * |                                                                           |
 * |   x Updatable                                                             |
 * |                                                                           |
 * |   x Unique                                                                |
 * |                                                                           |
 * |   x Nullable                                                              |
 * |                                                                           |
 * |                      ---------------                                      |
 * |   Length:            | I         |I|  Default (XXX)                       |
 * |                      ---------------                                      |
 * |                      ---------------                                      |
 * |   Precision:         | I         |I|  Default (XXX)                       |
 * |                      ---------------                                      |
 * |                      ---------------                                      |
 * |   Scale:             | I         |I|  Default (XXX)                       |
 * |                      ---------------                                      |
 * |                      ---------------------------------------------------- |
 * |   Column Definition: | I                                                | |
 * |                      ---------------------------------------------------- |
 * -----------------------------------------------------------------------------</pre>
 *
 * @see Column
 * @see ColumnCombo
 * @see TableCombo
 * @see BasicMappingComposite - A container of this pane
 * @see EmbeddedAttributeOverridesComposite - A container of this pane
 * @see IdMappingComposite - A container of this pane
 * @see VersionMappingComposite - A container of this pane
 *
 * @version 2.0
 * @since 1.0
 */
public class HibernateColumnComposite extends Pane<HibernateColumn> {

//	/**
//	 * Creates a new <code>HibernateColumnComposite</code>.
//	 *
//	 * @param parentPane The parent container of this one
//	 * @param subjectHolder The holder of the subject <code>IColumn</code>
//	 * @param parent The parent container
//	 */
//	public HibernateColumnComposite(
//			Pane<?> parentPane,
//	        PropertyValueModel<? extends HibernateColumn> subjectHolder,
//	        Composite parent) {
//
//		super(parentPane, subjectHolder, parent);
//	}
	
	/**
	 * Creates a new <code>ColumnComposite</code>.
	 *
	 * @param parentPane The parent container of this one
	 * @param subjectHolder The holder of the subject <code>IColumn</code>
	 * @param parent The parent container
	 * @param automaticallyAlignWidgets <code>true</code> to make the widgets
	 * this pane aligned with the widgets of the given parent controller;
	 * <code>false</code> to not align them
	 */
	public HibernateColumnComposite(Pane<?> parentPane,
	                       PropertyValueModel<? extends HibernateColumn> subjectHolder,
	                       Composite parent) { //,
	                       //boolean automaticallyAlignWidgets,
	                       //boolean parentManagePane) {

		super(parentPane, subjectHolder, parent); //, automaticallyAlignWidgets, parentManagePane);
	}

	private ColumnCombo<HibernateColumn> addColumnCombo(Composite container) {

		return new ColumnCombo<HibernateColumn>(this, container) {

			@Override
			protected void initializeLayout(Composite container) {
				super.initializeLayout(container);
				comboBox.addFocusListener(new FocusListener() {
					
					public void focusGained(FocusEvent e) {
						if (comboBox.getSelectionIndex() != 0){
							setPopulating(true);
							comboBox.setText(getSubject().getName());
							setPopulating(false);
						}
					}
					
					public void focusLost(FocusEvent e) {
						if (comboBox.getSelectionIndex() != 0){
							setPopulating(true);
							comboBox.setText(getValue());
							setPopulating(false);
						}
					}
				});
			}
			
			@Override
			protected void addPropertyNames(Collection<String> propertyNames) {
				super.addPropertyNames(propertyNames);
				propertyNames.add(NamedColumn.DEFAULT_NAME_PROPERTY);
				propertyNames.add(NamedColumn.SPECIFIED_NAME_PROPERTY);
				propertyNames.add(BaseColumn.DEFAULT_TABLE_PROPERTY);
				propertyNames.add(BaseColumn.SPECIFIED_TABLE_PROPERTY);
			}

			@Override
			protected void propertyChanged(String propertyName) {
				if (propertyName == BaseColumn.DEFAULT_TABLE_PROPERTY ||
				    propertyName == BaseColumn.SPECIFIED_TABLE_PROPERTY) {
					this.doPopulate();
				} else {
					super.propertyChanged(propertyName);
				}
			}

			@Override
			protected String getDefaultValue() {
				return getSubject().getDefaultDBColumnName();
			}

			@Override
			protected void setValue(String value) {
				getSubject().setSpecifiedName(value);
			}

			@Override
			protected Table getDbTable_() {
				return getSubject().getDbTable();
			}

			@Override
			protected String getValue() {
				String specifiedName = this.getSubject().getSpecifiedName();
				if (specifiedName == null){
					return null;
				}
				String dbColumnName = this.getSubject().getDBColumnName();
				if (specifiedName.equals(dbColumnName)){
					return specifiedName;
				} else {
					return specifiedName + " (" + dbColumnName +")"; //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			
			@Override
			public String toString() {
				return "ColumnComposite.columnCombo"; //$NON-NLS-1$
			}
		};
	}

	private ModifiablePropertyValueModel<String> buildColumnDefinitionHolder() {
		return new PropertyAspectAdapter<Column, String>(getSubjectHolder(), NamedColumn.COLUMN_DEFINITION_PROPERTY) {
			@Override
			protected String buildValue_() {
				return this.subject.getColumnDefinition();
			}

			@Override
			protected void setValue_(String value) {
				if (value.length() == 0) {
					value = null;
				}
				this.subject.setColumnDefinition(value);
			}
		};
	}
	
	private ModifiablePropertyValueModel<Boolean> buildInsertableHolder() {
		return new PropertyAspectAdapter<Column, Boolean>(getSubjectHolder(), BaseColumn.SPECIFIED_INSERTABLE_PROPERTY) {
			@Override
			protected Boolean buildValue_() {
				return this.subject.getSpecifiedInsertable();
			}

			@Override
			protected void setValue_(Boolean value) {
				this.subject.setSpecifiedInsertable(value);
			}
		};
	}

	private PropertyValueModel<String> buildInsertableStringHolder() {

		return new TransformationPropertyValueModel<Boolean, String>(buildInsertableHolder()) {

			@Override
			protected String transform(Boolean value) {

				if ((getSubject() != null) && (value == null)) {
					boolean defaultValue = getSubject().isDefaultInsertable();

					String defaultStringValue = defaultValue ? JptUiDetailsMessages.OptionalComposite_true :
						                                       JptUiDetailsMessages.OptionalComposite_false;

					return NLS.bind(
						JptUiDetailsMessages.ColumnComposite_insertableWithDefault,
						defaultStringValue
					);
				}

				return JptUiDetailsMessages.ColumnComposite_insertable;
			}
		};
	}


	private ModifiablePropertyValueModel<Boolean> buildNullableHolder() {
		return new PropertyAspectAdapter<Column, Boolean>(
				getSubjectHolder(),
				BaseColumn.SPECIFIED_NULLABLE_PROPERTY) {
			
			@Override
			protected Boolean buildValue_() {
				return this.subject.getSpecifiedNullable();
			}
			
			@Override
			protected void setValue_(Boolean value) {
				this.subject.setSpecifiedNullable(value);
			}
		};
	}
	
	private PropertyValueModel<String> buildNullableStringHolder() {
		return new TransformationPropertyValueModel<Boolean, String>(buildDefaultNullableHolder()) {
			@Override
			protected String transform(Boolean value) {
				if (value != null) {
					String defaultStringValue = value.booleanValue() ? JptUiDetailsMessages.OptionalComposite_true : JptUiDetailsMessages.OptionalComposite_false;
					return NLS.bind(JptUiDetailsMessages.ColumnComposite_nullableWithDefault, defaultStringValue);
				}
				return JptUiDetailsMessages.ColumnComposite_nullable;
			}
		};
	}
	
	private PropertyValueModel<Boolean> buildDefaultNullableHolder() {
		return new PropertyAspectAdapter<Column, Boolean>(
				getSubjectHolder(),
				BaseColumn.SPECIFIED_NULLABLE_PROPERTY,
				BaseColumn.DEFAULT_NULLABLE_PROPERTY) {
			
			@Override
			protected Boolean buildValue_() {
				if (this.subject.getSpecifiedNullable() != null) {
					return null;
				}
				return Boolean.valueOf(this.subject.isDefaultNullable());
			}
		};
	}

	private Pane<HibernateColumn> addTableCombo(Composite container) {

		return new DatabaseObjectCombo<HibernateColumn>(this, container) {
			
			@Override
			protected void initializeLayout(Composite container) {
				super.initializeLayout(container);
				comboBox.addFocusListener(new FocusListener() {
					
					public void focusGained(FocusEvent e) {
						if (comboBox.getSelectionIndex() != 0){
							setPopulating(true);
							comboBox.setText(getSubject().getName());
							setPopulating(false);
						}						
					}
					
					public void focusLost(FocusEvent e) {
						if (comboBox.getSelectionIndex() != 0){
							setPopulating(true);
							comboBox.setText(getValue());
							setPopulating(false);
						}												
					}
				});
			}

			@Override
			protected void addPropertyNames(Collection<String> propertyNames) {
				super.addPropertyNames(propertyNames);
				propertyNames.add(BaseColumn.DEFAULT_TABLE_PROPERTY);
				propertyNames.add(BaseColumn.SPECIFIED_TABLE_PROPERTY);
			}

			@Override
			protected String getDefaultValue() {
				return this.getSubject().getDefaultDBTableName();
			}

			@Override
			protected void setValue(String value) {
				this.getSubject().setSpecifiedTable(value);
			}

			@Override
			protected String getValue() {
				String specifiedName = this.getSubject().getSpecifiedTable();
				if (specifiedName == null){
					return null;
				}
				String dbTableName = this.getSubject().getDBTableName();
				if (specifiedName.equals(dbTableName)){
					return specifiedName;
				} else {
					return specifiedName + " (" + dbTableName +")"; //$NON-NLS-1$ //$NON-NLS-2$
				}
			}

			@Override
			protected Iterable<String> getValues_() {
				return this.getSubject().getCandidateTableNames();
			}
			
			@Override
			public String toString() {
				return "ColumnComposite.tableCombo"; //$NON-NLS-1$
			}
		};
	}

	private ModifiablePropertyValueModel<Boolean> buildUniqueHolder() {
		return new PropertyAspectAdapter<Column, Boolean>(
				getSubjectHolder(),
				BaseColumn.SPECIFIED_UNIQUE_PROPERTY) {
			
			@Override
			protected Boolean buildValue_() {
				return this.subject.getSpecifiedUnique();
			}
			
			@Override
			protected void setValue_(Boolean value) {
				this.subject.setSpecifiedUnique(value);
			}
		};
	}

	private PropertyValueModel<String> buildUniqueStringHolder() {
		
		return new TransformationPropertyValueModel<Boolean, String>(buildDefaultUniqueHolder()) {
			
			@Override
			protected String transform(Boolean value) {
				if (value != null) {
					String defaultStringValue = value.booleanValue() ? JptUiDetailsMessages.OptionalComposite_true : JptUiDetailsMessages.OptionalComposite_false;
					return NLS.bind(JptUiDetailsMessages.ColumnComposite_uniqueWithDefault, defaultStringValue);
				}
				return JptUiDetailsMessages.ColumnComposite_unique;
			}
		};
	}
	
	private PropertyValueModel<Boolean> buildDefaultUniqueHolder() {
		return new PropertyAspectAdapter<Column, Boolean>(
				getSubjectHolder(),
				BaseColumn.SPECIFIED_UNIQUE_PROPERTY,
				BaseColumn.DEFAULT_UNIQUE_PROPERTY) {
			
			@Override
			protected Boolean buildValue_() {
				if (this.subject.getSpecifiedUnique() != null) {
					return null;
				}
				return Boolean.valueOf(this.subject.isDefaultUnique());
			}
		};
	}
	
	private ModifiablePropertyValueModel<Boolean> buildUpdatableHolder() {
		return new PropertyAspectAdapter<Column, Boolean>(
				getSubjectHolder(),
				BaseColumn.DEFAULT_UPDATABLE_PROPERTY,
				BaseColumn.SPECIFIED_UPDATABLE_PROPERTY) {
			
			@Override
			protected Boolean buildValue_() {
				return this.subject.getSpecifiedUpdatable();
			}
			
			@Override
			protected void setValue_(Boolean value) {
				this.subject.setSpecifiedUpdatable(value);
			}
		};
	}
	
	private PropertyValueModel<String> buildUpdatableStringHolder() {
		
		return new TransformationPropertyValueModel<Boolean, String>(buildDefaultUpdatableHolder()) {
			
			@Override
			protected String transform(Boolean value) {
				if (value != null) {
					String defaultStringValue = value.booleanValue() ? JptUiDetailsMessages.OptionalComposite_true : JptUiDetailsMessages.OptionalComposite_false;
					return NLS.bind(JptUiDetailsMessages.ColumnComposite_updatableWithDefault, defaultStringValue);
				}
				return JptUiDetailsMessages.ColumnComposite_updatable;
			}
		};
	}
	
	private PropertyValueModel<Boolean> buildDefaultUpdatableHolder() {
		return new PropertyAspectAdapter<Column, Boolean>(
				getSubjectHolder(),
				BaseColumn.SPECIFIED_UPDATABLE_PROPERTY,
				BaseColumn.DEFAULT_UPDATABLE_PROPERTY) {
			
			@Override
			protected Boolean buildValue_() {
				if (this.subject.getSpecifiedUpdatable() != null) {
					return null;
				}
				return Boolean.valueOf(this.subject.isDefaultUpdatable());
			}
		};
	}

	@Override
	protected void initializeLayout(Composite container) {
//		// Column group pane
//		container = addTitledGroup(
//			container,
//			JptUiDetailsMessages.ColumnComposite_columnSection
//		);
//
//		// Column widgets
//		addLabeledComposite(
//			container,
//			JptUiDetailsMessages.ColumnComposite_name,
//			addColumnCombo(container),
//			JpaHelpContextIds.MAPPING_COLUMN
//		);
//
//		// Table widgets
//		addLabeledComposite(
//			container,
//			JptUiDetailsMessages.ColumnComposite_table,
//			addTableCombo(container),
//			JpaHelpContextIds.MAPPING_COLUMN_TABLE
//		);
//
//		// Details sub-pane
//		container = addCollapsibleSubSection(
//			container,
//			JptUiDetailsMessages.ColumnComposite_details,
//			new SimplePropertyValueModel<Boolean>(Boolean.FALSE)
//		);
//
//		new DetailsComposite(this, getSubjectHolder(), addSubPane(container, 0, 16));
		
		// Column widgets
		this.addLabel(container, JptUiDetailsMessages.ColumnComposite_name);
		this.addColumnCombo(container);

		// Table widgets
		this.addLabel(container, JptUiDetailsMessages.ColumnComposite_table);
		this.addTableCombo(container);


		// Details sub-pane
		final Section detailsSection = this.getWidgetFactory().createSection(container, ExpandableComposite.TWISTIE);
		detailsSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		detailsSection.setText(JptUiDetailsMessages.ColumnComposite_details);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		detailsSection.setLayoutData(gridData);

		detailsSection.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanging(ExpansionEvent e) {
				if (e.getState() && detailsSection.getClient() == null) {
					detailsSection.setClient(HibernateColumnComposite.this.initializeDetailsSection(detailsSection));
				}
			}
		});				
	}
	
//	protected class DetailsComposite extends Pane<HibernateColumn> {
//				
//		public DetailsComposite(Pane<?> parentPane,
//            PropertyValueModel<? extends HibernateColumn> subjectHolder,
//            Composite parent) {
//
//			super(parentPane, subjectHolder, parent);
//		}
//
//		@Override
//		protected void initializeLayout(Composite container) {
//
//			// Insertable tri-state check box
//			addTriStateCheckBoxWithDefault(
//				addSubPane(container, 4),
//				JptUiDetailsMessages.ColumnComposite_insertable,
//				buildInsertableHolder(),
//				buildInsertableStringHolder(),
//				JpaHelpContextIds.MAPPING_COLUMN_INSERTABLE
//			);
//
//			// Updatable tri-state check box
//			addTriStateCheckBoxWithDefault(
//				container,
//				JptUiDetailsMessages.ColumnComposite_updatable,
//				buildUpdatableHolder(),
//				buildUpdatableStringHolder(),
//				JpaHelpContextIds.MAPPING_COLUMN_UPDATABLE
//			);
//
//			// Unique tri-state check box
//			addTriStateCheckBoxWithDefault(
//				container,
//				JptUiDetailsMessages.ColumnComposite_unique,
//				buildUniqueHolder(),
//				buildUniqueStringHolder(),
//				JpaHelpContextIds.MAPPING_COLUMN_UNIQUE
//			);
//
//			// Nullable tri-state check box
//			addTriStateCheckBoxWithDefault(
//				container,
//				JptUiDetailsMessages.ColumnComposite_nullable,
//				buildNullableHolder(),
//				buildNullableStringHolder(),
//				JpaHelpContextIds.MAPPING_COLUMN_NULLABLE
//			);
//
//			addLengthCombo(container);
//			addPrecisionCombo(container);
//			addScaleCombo(container);
//
//			// Column Definition widgets
//			addLabeledText(
//				container,
//				JptUiDetailsMessages.ColumnComposite_columnDefinition,
//				buildColumnDefinitionHolder()
//			);
//		}
//
//		private void addLengthCombo(Composite container) {
//			new IntegerCombo<HibernateColumn>(this, container) {
//				
//				@Override
//				protected String getLabelText() {
//					return JptUiDetailsMessages.ColumnComposite_length;
//				}
//			
//				@Override
//				protected String getHelpId() {
//					return JpaHelpContextIds.MAPPING_COLUMN_LENGTH;
//				}
//
//				@Override
//				protected PropertyValueModel<Integer> buildDefaultHolder() {
//					return new PropertyAspectAdapter<Column, Integer>(getSubjectHolder(), Column.DEFAULT_LENGTH_PROPERTY) {
//						@Override
//						protected Integer buildValue_() {
//							return Integer.valueOf(this.subject.getDefaultLength());
//						}
//					};
//				}
//				
//				@Override
//				protected ModifiablePropertyValueModel<Integer> buildSelectedItemHolder() {
//					return new PropertyAspectAdapter<Column, Integer>(getSubjectHolder(), Column.SPECIFIED_LENGTH_PROPERTY) {
//						@Override
//						protected Integer buildValue_() {
//							return this.subject.getSpecifiedLength();
//						}
//
//						@Override
//						protected void setValue_(Integer value) {
//							this.subject.setSpecifiedLength(value);
//						}
//					};
//				}
//			};
//		}
//
//		private void addPrecisionCombo(Composite container) {
//			new IntegerCombo<HibernateColumn>(this, container) {
//				
//				@Override
//				protected String getLabelText() {
//					return JptUiDetailsMessages.ColumnComposite_precision;
//				}
//			
//				@Override
//				protected String getHelpId() {
//					return JpaHelpContextIds.MAPPING_COLUMN_PRECISION;
//				}
//
//				@Override
//				protected PropertyValueModel<Integer> buildDefaultHolder() {
//					return new PropertyAspectAdapter<Column, Integer>(getSubjectHolder(), Column.DEFAULT_PRECISION_PROPERTY) {
//						@Override
//						protected Integer buildValue_() {
//							return Integer.valueOf(this.subject.getDefaultPrecision());
//						}
//					};
//				}
//				
//				@Override
//				protected ModifiablePropertyValueModel<Integer> buildSelectedItemHolder() {
//					return new PropertyAspectAdapter<Column, Integer>(getSubjectHolder(), Column.SPECIFIED_PRECISION_PROPERTY) {
//						@Override
//						protected Integer buildValue_() {
//							return this.subject.getSpecifiedPrecision();
//						}
//
//						@Override
//						protected void setValue_(Integer value) {
//							this.subject.setSpecifiedPrecision(value);
//						}
//					};
//				}
//			};
//		}
//
//		private void addScaleCombo(Composite container) {
//			new IntegerCombo<HibernateColumn>(this, container) {
//				
//				@Override
//				protected String getLabelText() {
//					return JptUiDetailsMessages.ColumnComposite_scale;
//				}
//			
//				@Override
//				protected String getHelpId() {
//					return JpaHelpContextIds.MAPPING_COLUMN_SCALE;
//				}
//
//				@Override
//				protected PropertyValueModel<Integer> buildDefaultHolder() {
//					return new PropertyAspectAdapter<Column, Integer>(getSubjectHolder(), Column.DEFAULT_SCALE_PROPERTY) {
//						@Override
//						protected Integer buildValue_() {
//							return Integer.valueOf(this.subject.getDefaultScale());
//						}
//					};
//				}
//				
//				@Override
//				protected ModifiablePropertyValueModel<Integer> buildSelectedItemHolder() {
//					return new PropertyAspectAdapter<Column, Integer>(getSubjectHolder(), Column.SPECIFIED_SCALE_PROPERTY) {
//						@Override
//						protected Integer buildValue_() {
//							return this.subject.getSpecifiedScale();
//						}
//
//						@Override
//						protected void setValue_(Integer value) {
//							this.subject.setSpecifiedScale(value);
//						}
//					};
//				}
//			};
//		}
//	}
	
	protected Composite initializeDetailsSection(Composite container) {
		Composite detailsClient = this.addSubPane(container, 2, 0, 16, 0, 0);

		// Insertable tri-state check box
		TriStateCheckBox insertableCheckBox = addTriStateCheckBoxWithDefault(
				detailsClient,
				JptUiDetailsMessages.ColumnComposite_insertable,
				buildInsertableHolder(),
				buildInsertableStringHolder(),
				JpaHelpContextIds.MAPPING_COLUMN_INSERTABLE);
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		insertableCheckBox.getCheckBox().setLayoutData(gridData);

		// Updatable tri-state check box
		TriStateCheckBox updatableCheckBox = addTriStateCheckBoxWithDefault(
				detailsClient,
				JptUiDetailsMessages.ColumnComposite_updatable,
				buildUpdatableHolder(),
				buildUpdatableStringHolder(),
				JpaHelpContextIds.MAPPING_COLUMN_UPDATABLE);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		updatableCheckBox.getCheckBox().setLayoutData(gridData);

		// Unique tri-state check box
		TriStateCheckBox uniqueCheckBox = addTriStateCheckBoxWithDefault(
				detailsClient,
				JptUiDetailsMessages.ColumnComposite_unique,
				buildUniqueHolder(),
				buildUniqueStringHolder(),
				JpaHelpContextIds.MAPPING_COLUMN_UNIQUE);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		uniqueCheckBox.getCheckBox().setLayoutData(gridData);

		// Nullable tri-state check box
		TriStateCheckBox nullableCheckBox = addTriStateCheckBoxWithDefault(
				detailsClient,
				JptUiDetailsMessages.ColumnComposite_nullable,
				buildNullableHolder(),
				buildNullableStringHolder(),
				JpaHelpContextIds.MAPPING_COLUMN_NULLABLE);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		nullableCheckBox.getCheckBox().setLayoutData(gridData);

		this.addLabel(detailsClient, JptUiDetailsMessages.ColumnComposite_length);		
		this.addLengthCombo(detailsClient);

		this.addLabel(detailsClient, JptUiDetailsMessages.ColumnComposite_precision);		
		this.addPrecisionCombo(detailsClient);

		this.addLabel(detailsClient, JptUiDetailsMessages.ColumnComposite_scale);		
		this.addScaleCombo(detailsClient);

		// Column Definition widgets
		this.addLabel(detailsClient, JptUiDetailsMessages.ColumnComposite_columnDefinition);
		this.addText(detailsClient, buildColumnDefinitionHolder());

		return detailsClient;
	}

	private void addLengthCombo(Composite container) {
		new IntegerCombo<HibernateColumn>(this, container) {				
			@Override
			protected String getHelpId() {
				return JpaHelpContextIds.MAPPING_COLUMN_LENGTH;
			}
			
			@Override
			protected PropertyValueModel<Integer> buildDefaultHolder() {
				return new PropertyAspectAdapter<HibernateColumn, Integer>(getSubjectHolder(), HibernateColumn.DEFAULT_LENGTH_PROPERTY) {
					@Override
					protected Integer buildValue_() {
						return Integer.valueOf(this.subject.getDefaultLength());
					}
				};
			}
			
			@Override
			protected ModifiablePropertyValueModel<Integer> buildSelectedItemHolder() {
				return new PropertyAspectAdapter<HibernateColumn, Integer>(getSubjectHolder(), HibernateColumn.SPECIFIED_LENGTH_PROPERTY) {
					@Override
					protected Integer buildValue_() {
						return this.subject.getSpecifiedLength();
					}
					
					@Override
					protected void setValue_(Integer value) {
						((Column) this.subject).setSpecifiedLength(value);
					}
				};
			}
		};
	}

	private void addPrecisionCombo(Composite container) {
		new IntegerCombo<HibernateColumn>(this, container) {					
			@Override
			protected String getHelpId() {
				return JpaHelpContextIds.MAPPING_COLUMN_PRECISION;
			}
			
			@Override
			protected PropertyValueModel<Integer> buildDefaultHolder() {
				return new PropertyAspectAdapter<HibernateColumn, Integer>(getSubjectHolder(), HibernateColumn.DEFAULT_PRECISION_PROPERTY) {
					@Override
					protected Integer buildValue_() {
						return Integer.valueOf(this.subject.getDefaultPrecision());
					}
				};
			}
			
			@Override
			protected ModifiablePropertyValueModel<Integer> buildSelectedItemHolder() {
				return new PropertyAspectAdapter<HibernateColumn, Integer>(getSubjectHolder(), HibernateColumn.SPECIFIED_PRECISION_PROPERTY) {
					@Override
					protected Integer buildValue_() {
						return this.subject.getSpecifiedPrecision();
					}
					
					@Override
					protected void setValue_(Integer value) {
						((Column) this.subject).setSpecifiedPrecision(value);
					}
				};
			}
		};
	}

	private void addScaleCombo(Composite container) {
		new IntegerCombo<HibernateColumn>(this, container) {					
			@Override
			protected String getHelpId() {
				return JpaHelpContextIds.MAPPING_COLUMN_SCALE;
			}
			
			@Override
			protected PropertyValueModel<Integer> buildDefaultHolder() {
				return new PropertyAspectAdapter<HibernateColumn, Integer>(getSubjectHolder(), HibernateColumn.DEFAULT_SCALE_PROPERTY) {
					@Override
					protected Integer buildValue_() {
						return Integer.valueOf(this.subject.getDefaultScale());
					}
				};
			}
			
			@Override
			protected ModifiablePropertyValueModel<Integer> buildSelectedItemHolder() {
				return new PropertyAspectAdapter<HibernateColumn, Integer>(getSubjectHolder(), HibernateColumn.SPECIFIED_SCALE_PROPERTY) {
					@Override
					protected Integer buildValue_() {
						return this.subject.getSpecifiedScale();
					}
					
					@Override
					protected void setValue_(Integer value) {
						((Column) this.subject).setSpecifiedScale(value);
					}
				};
			}
		};
	}
}
