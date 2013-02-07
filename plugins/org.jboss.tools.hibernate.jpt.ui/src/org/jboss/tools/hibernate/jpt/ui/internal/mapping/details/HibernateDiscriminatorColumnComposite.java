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

import org.eclipse.jpt.common.ui.JptCommonUiMessages;
import org.eclipse.jpt.common.ui.internal.widgets.EnumFormComboViewer;
import org.eclipse.jpt.common.ui.internal.widgets.IntegerCombo;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.model.value.ModifiablePropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.context.DiscriminatorColumn;
import org.eclipse.jpt.jpa.core.context.DiscriminatorType;
import org.eclipse.jpt.jpa.core.context.Entity;
import org.eclipse.jpt.jpa.core.context.NamedColumn;
import org.eclipse.jpt.jpa.core.context.ReadOnlyNamedColumn;
import org.eclipse.jpt.jpa.db.Table;
import org.eclipse.jpt.jpa.ui.internal.JpaHelpContextIds;
import org.eclipse.jpt.jpa.ui.internal.details.AbstractEntityComposite;
import org.eclipse.jpt.jpa.ui.internal.details.JptUiDetailsMessages;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;
import org.jboss.tools.hibernate.jpt.core.internal.context.DiscriminatorFormula;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateEntity;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntity;
import org.jboss.tools.hibernate.jpt.ui.internal.mappings.db.xpl.ColumnCombo;

/**
 * Here the layout of this pane:
 * <pre>
 * -----------------------------------------------------------------------------
 * | > Discriminator Column                                                    |
 * |                                                                           |
 * |                      ---------------------------------------------------- |
 * | Name:                | ColumnCombo                                    |v| |
 * |                      ---------------------------------------------------- |
 * |                      ---------------------------------------------------- |
 * | Formula:             | Text		                                   |v| |
 * |                      ---------------------------------------------------- |
 * |                      ---------------------------------------------------- |
 * | Type:                | EnumComboViewer                                |v| |
 * |                      ---------------------------------------------------- |
 * | > Details
 * |                      ---------------------------------------------------- |
 * | Column Definition:   | I                                                | |
 * |                      ---------------------------------------------------- |
 * |                      -------------                                        |
 * | Length:              | I       |I|                                        |
 * |                      -------------                                        |
 * -----------------------------------------------------------------------------</pre>
 *
 * @see Entity
 * @see AbstractEntityComposite - The parent container
 * @see ColumnCombo
 * @see EnumComboViewer
 * @see PrimaryKeyJoinColumnsComposite
 *
 * @version 2.0
 * @since 2.0
 */
public class HibernateDiscriminatorColumnComposite extends Pane<HibernateJavaEntity> {

//	private ModifiablePropertyValueModel<DiscriminatorFormula> discriminatorFormulaHolder;

	/**
	 * Creates a new <code>InheritanceComposite</code>.
	 *
	 * @param parentPane The parent container of this one
	 * @param parent The parent container
	 */
	public HibernateDiscriminatorColumnComposite(Pane<? extends HibernateJavaEntity> parentPane,
	                            Composite parent) {
		super(parentPane, parent);
	}

	@Override
	protected void initialize() {
		super.initialize();
//		this.discriminatorFormulaHolder = buildDiscriminatorFormulaHolder();
	}

	@Override
	protected void initializeLayout(Composite container) {

//		// Discriminator Column sub-pane
//		Composite discriminatorColumnContainer = addTitledGroup(
//			addSubPane(container, 10),
//			JptUiDetailsMessages.InheritanceComposite_discriminatorColumnGroupBox
//		);
//
//		PropertyValueModel<DiscriminatorColumn> discriminatorColumnHolder =
//			buildDiscriminatorColumnHolder();
//
//		// Name widgets
//		addLabeledComposite(
//			discriminatorColumnContainer,
//			JptUiDetailsMessages.DiscriminatorColumnComposite_name,
//			addDiscriminatorColumnCombo(container, discriminatorColumnHolder),
//			JpaHelpContextIds.ENTITY_INHERITANCE_DISCRIMINATOR_COLUMN
//		);
//
//		// Formula widgets
//		addLabeledText(
//			discriminatorColumnContainer,
//			HibernateUIMappingMessages.HibernateDiscriminatorColumnComposite_formula,
//			buildDiscriminatorFormulaValueHolder(),
//			null//TODO help
//		);
//
//		// Discriminator Type widgets
//		addLabeledComposite(
//			discriminatorColumnContainer,
//			JptUiDetailsMessages.DiscriminatorColumnComposite_discriminatorType,
//			addDiscriminatorTypeCombo(container, discriminatorColumnHolder),
//			JpaHelpContextIds.ENTITY_INHERITANCE_DISCRIMINATOR_TYPE
//		);
//
//		container = addCollapsibleSubSection(
//			discriminatorColumnContainer,
//			JptUiDetailsMessages.InheritanceComposite_detailsGroupBox,
//			new SimplePropertyValueModel<Boolean>(Boolean.FALSE)
//		);
//
//		new DetailsComposite(this, discriminatorColumnHolder, addSubPane(container, 0, 16));
//
//		new PaneVisibilityEnabler(buildDiscriminatorColumnEnabledHolder(), this);

		PropertyValueModel<DiscriminatorColumn> discriminatorColumnHolder =
				buildDiscriminatorColumnHolder();
		PropertyValueModel<Boolean> enabledModel = buildDiscriminatorColumnEnabledHolder();

			// Name widgets
			this.addLabel(container, JptUiDetailsMessages.DiscriminatorColumnComposite_name, enabledModel);
			this.addDiscriminatorColumnCombo(container, discriminatorColumnHolder, enabledModel);

			// Discriminator Type widgets
			this.addLabel(container,JptUiDetailsMessages.DiscriminatorColumnComposite_discriminatorType, enabledModel);
			this.addDiscriminatorTypeCombo(container, discriminatorColumnHolder, enabledModel);


			Section detailsSection = this.getWidgetFactory().createSection(container, ExpandableComposite.TWISTIE);
			detailsSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			detailsSection.setText(JptUiDetailsMessages.InheritanceComposite_detailsGroupBox);
			detailsSection.setClient(this.initializeDetailsClient(detailsSection, discriminatorColumnHolder, enabledModel));

		
	}

	private ColumnCombo<DiscriminatorColumn> addDiscriminatorColumnCombo(
			Composite container,
			PropertyValueModel<DiscriminatorColumn> discriminatorColumnHolder, 
			PropertyValueModel<Boolean> enabledModel) {

		return new ColumnCombo<DiscriminatorColumn>(
			this,
			discriminatorColumnHolder,
			container)
		{

			@Override
			protected void addPropertyNames(Collection<String> propertyNames) {
				super.addPropertyNames(propertyNames);
				propertyNames.add(ReadOnlyNamedColumn.SPECIFIED_NAME_PROPERTY);
				propertyNames.add(ReadOnlyNamedColumn.DEFAULT_NAME_PROPERTY);
				propertyNames.add(ReadOnlyNamedColumn.DB_TABLE_PROPERTY);
			}

			@Override
			protected void propertyChanged(String propertyName) {
					if (propertyName.equals(ReadOnlyNamedColumn.DB_TABLE_PROPERTY)) {
						this.doPopulate();
					} else {
						super.propertyChanged(propertyName);
					}
				}

			@Override
			protected String getDefaultValue() {
				return getSubject().getDefaultName();
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
				return getSubject().getSpecifiedName();
			}
				
			@Override
			protected String buildNullDefaultValueEntry() {
				return JptCommonUiMessages.NONE_SELECTED;
			}

			@Override
			protected String getHelpId() {
				return JpaHelpContextIds.ENTITY_INHERITANCE_DISCRIMINATOR_COLUMN;
			}

			@Override
			public String toString() {
				return "DiscriminatorColumnComposite.columnCombo"; //$NON-NLS-1$
			}
		};
	}	
	
//	private ColumnCombo<DiscriminatorColumn> addDiscriminatorColumnCombo(
//		Composite container,
//		PropertyValueModel<DiscriminatorColumn> discriminatorColumnHolder) {
//
//		return new ColumnCombo<DiscriminatorColumn>(
//			this,
//			discriminatorColumnHolder,
//			container)
//		{
//
//			@Override
//			protected void addPropertyNames(Collection<String> propertyNames) {
//				super.addPropertyNames(propertyNames);
//				propertyNames.add(NamedColumn.SPECIFIED_NAME_PROPERTY);
//				propertyNames.add(NamedColumn.DEFAULT_NAME_PROPERTY);
//			}
//
//			@Override
//			protected String getDefaultValue() {
//				return getSubject().getDefaultName();
//			}
//
//			@Override
//			protected void setValue(String value) {
//				getSubject().setSpecifiedName(value);
//			}
//
//			@Override
//			protected Table getDbTable_() {
//				return getSubject().getDbTable();
//			}
//
//			@Override
//			protected String getValue() {
//				return getSubject().getSpecifiedName();
//			}
//
//			@Override
//			protected String buildNullDefaultValueEntry() {
//				return JptCommonUiMessages.NoneSelected;
//			}
//		};
//	}

	private PropertyValueModel<DiscriminatorColumn> buildDiscriminatorColumnHolder() {
		return new PropertyAspectAdapter<Entity, DiscriminatorColumn>(getSubjectHolder()) {
			@Override
			protected DiscriminatorColumn buildValue_() {
				return this.subject.getDiscriminatorColumn();
			}
		};
	}

//	private ModifiablePropertyValueModel<String> buildDiscriminatorFormulaValueHolder() {
//		return new PropertyAspectAdapter<DiscriminatorFormula, String>(this.discriminatorFormulaHolder, DiscriminatorFormula.VALUE_PROPERTY) {
//			@Override
//			protected String buildValue_() {
//				return this.subject == null ? null : this.subject.getValue();
//			}
//
//			@Override
//			public void setValue(String value) {
//				if (subject != null) {
//					setValue_(value);
//					return;
//				}
//				
//				if ("".equals(value)){ //$NON-NLS-1$
//					return;
//				}
//					DiscriminatorFormula discriminatorFormula = (getSubject().getDiscriminatorFormula() != null
//							? getSubject().getDiscriminatorFormula()
//							: getSubject().addDiscriminatorFormula());
//					discriminatorFormula.setValue(value);
//					//HibernateDiscriminatorColumnComposite.this.discriminatorFormulaHolder.setValue(discriminatorFormula);
//				//}
//				//setValue_(value);
//			}
//
//			@Override
//			protected void setValue_(String value) {
//				if ("".equals(value)) {//$NON-NLS-1$
//					value = null;
//				}
//				if (value != null){
//					this.subject.setValue(value);
//				} else {
//					getSubjectHolder().getValue().removeDiscriminatorFormula();
//				}
//				
//			}
//		};
//	}

	private EnumFormComboViewer<DiscriminatorColumn, DiscriminatorType> addDiscriminatorTypeCombo(
		    Composite container,
			PropertyValueModel<DiscriminatorColumn> discriminatorColumnHolder,
			PropertyValueModel<Boolean> enabledModel) {

		return new EnumFormComboViewer<DiscriminatorColumn, DiscriminatorType>(
			this,
			discriminatorColumnHolder,
			enabledModel,
			container)
		{
			@Override
			protected void addPropertyNames(Collection<String> propertyNames) {
				super.addPropertyNames(propertyNames);
				propertyNames.add(DiscriminatorColumn.DEFAULT_DISCRIMINATOR_TYPE_PROPERTY);
				propertyNames.add(DiscriminatorColumn.SPECIFIED_DISCRIMINATOR_TYPE_PROPERTY);
			}

			@Override
			protected DiscriminatorType[] getChoices() {
				return DiscriminatorType.values();
			}

			@Override
			protected DiscriminatorType getDefaultValue() {
				return getSubject().getDefaultDiscriminatorType();
			}

			@Override
			protected String displayString(DiscriminatorType value) {
				switch (value) {
					case CHAR :
						return JptUiDetailsMessages.DiscriminatorColumnComposite_char;
					case INTEGER :
						return JptUiDetailsMessages.DiscriminatorColumnComposite_integer;
					case STRING :
						return JptUiDetailsMessages.DiscriminatorColumnComposite_string;						default :
					throw new IllegalStateException();
				}
			}
				
			@Override
			protected String nullDisplayString() {
				return JptCommonUiMessages.NONE_SELECTED;
			}
				
			@Override
			protected DiscriminatorType getValue() {
				return getSubject().getSpecifiedDiscriminatorType();
			}

			@Override
			protected void setValue(DiscriminatorType value) {
				getSubject().setSpecifiedDiscriminatorType(value);
			}

			@Override
			protected String getHelpId() {
				return JpaHelpContextIds.ENTITY_INHERITANCE_DISCRIMINATOR_TYPE;
			}
		};
	}

	
	
	
//	private EnumFormComboViewer<DiscriminatorColumn, DiscriminatorType> addDiscriminatorTypeCombo(
//		Composite container,
//		PropertyValueModel<DiscriminatorColumn> discriminatorColumnHolder) {
//
//		return new EnumFormComboViewer<DiscriminatorColumn, DiscriminatorType>(
//			this,
//			discriminatorColumnHolder,
//			container)
//		{
//			@Override
//			protected void addPropertyNames(Collection<String> propertyNames) {
//				super.addPropertyNames(propertyNames);
//				propertyNames.add(DiscriminatorColumn.DEFAULT_DISCRIMINATOR_TYPE_PROPERTY);
//				propertyNames.add(DiscriminatorColumn.SPECIFIED_DISCRIMINATOR_TYPE_PROPERTY);
//			}
//
//			@Override
//			protected DiscriminatorType[] getChoices() {
//				return DiscriminatorType.values();
//			}
//
//			@Override
//			protected DiscriminatorType getDefaultValue() {
//				return getSubject().getDefaultDiscriminatorType();
//			}
//
//			@Override
//			protected String displayString(DiscriminatorType value) {
//				switch (value) {
//					case CHAR :
//						return JptUiDetailsMessages.DiscriminatorColumnComposite_char;
//					case INTEGER :
//						return JptUiDetailsMessages.DiscriminatorColumnComposite_integer;
//					case STRING :
//						return JptUiDetailsMessages.DiscriminatorColumnComposite_string;
//					default :
//						throw new IllegalStateException();
//				}
//			}
//			
//			@Override
//			protected String nullDisplayString() {
//				return JptCommonUiMessages.NoneSelected;
//			}
//			
//			@Override
//			protected DiscriminatorType getValue() {
//				return getSubject().getSpecifiedDiscriminatorType();
//			}
//
//			@Override
//			protected void setValue(DiscriminatorType value) {
//				getSubject().setSpecifiedDiscriminatorType(value);
//			}
//		};
//	}

	protected ModifiablePropertyValueModel<Boolean> buildDiscriminatorColumnEnabledHolder() {
		return new PropertyAspectAdapter<Entity, Boolean>(getSubjectHolder(), Entity.SPECIFIED_DISCRIMINATOR_COLUMN_IS_ALLOWED_PROPERTY) {
			@Override
			protected Boolean buildValue_() {
				return Boolean.valueOf(this.subject.specifiedDiscriminatorColumnIsAllowed());
			}
		};
	}
	
	protected Control initializeDetailsClient(Section detailsSection, PropertyValueModel<DiscriminatorColumn> discriminatorColumnHolder, PropertyValueModel<Boolean> enabledModel) {
		Composite detailsClient = this.addSubPane(detailsSection, 2, 0, 0, 0, 0);
		detailsSection.setClient(detailsClient);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		detailsSection.setLayoutData(gridData);

		// Length widgets
		Label lengthLabel = this.addLabel(detailsClient, JptUiDetailsMessages.ColumnComposite_length, enabledModel);
		gridData = new GridData();
		gridData.horizontalIndent = 16;
		lengthLabel.setLayoutData(gridData);
		this.addLengthCombo(detailsClient, discriminatorColumnHolder, enabledModel);

		// Column Definition widgets
		Label columnDefinitionLabel = this.addLabel(detailsClient, JptUiDetailsMessages.ColumnComposite_columnDefinition, enabledModel);
		gridData = new GridData();
		gridData.horizontalIndent = 16;
		columnDefinitionLabel.setLayoutData(gridData);
		this.addText(detailsClient, this.buildColumnDefinitionHolder(discriminatorColumnHolder), null, enabledModel);

		return detailsClient;
	}

	private void addLengthCombo(Composite container, PropertyValueModel<DiscriminatorColumn> subjectHolder, PropertyValueModel<Boolean> enabledModel) {
		new IntegerCombo<DiscriminatorColumn>(this, subjectHolder, enabledModel, container) {			
			@Override
			protected String getHelpId() {
				return JpaHelpContextIds.MAPPING_COLUMN_LENGTH;
			}

			@Override
			protected PropertyValueModel<Integer> buildDefaultHolder() {
				return new PropertyAspectAdapter<DiscriminatorColumn, Integer>(getSubjectHolder(), DiscriminatorColumn.DEFAULT_LENGTH_PROPERTY) {
					@Override
					protected Integer buildValue_() {
						return Integer.valueOf(this.subject.getDefaultLength());
					}
				};
			}
			
			@Override
			protected ModifiablePropertyValueModel<Integer> buildSelectedItemHolder() {
				return new PropertyAspectAdapter<DiscriminatorColumn, Integer>(getSubjectHolder(), DiscriminatorColumn.SPECIFIED_LENGTH_PROPERTY) {
					@Override
					protected Integer buildValue_() {
						return this.subject.getSpecifiedLength();
					}

					@Override
					protected void setValue_(Integer value) {
						this.subject.setSpecifiedLength(value);
					}
				};
			}
		};
	}
	
	private ModifiablePropertyValueModel<String> buildColumnDefinitionHolder(PropertyValueModel<DiscriminatorColumn> discriminatorColumnHolder) {

		return new PropertyAspectAdapter<DiscriminatorColumn, String>(discriminatorColumnHolder, NamedColumn.COLUMN_DEFINITION_PROPERTY) {
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

//	protected class DetailsComposite extends Pane<DiscriminatorColumn> {
//		public DetailsComposite(Pane<?> parentPane,
//            PropertyValueModel<? extends DiscriminatorColumn> subjectHolder,
//            Composite parent) {
//
//			super(parentPane, subjectHolder, parent);
//		}
//
//		@Override
//		protected void initializeLayout(Composite container) {
//			// Length widgets
//			addLengthCombo(container);
//
//			// Column Definition widgets
//			addLabeledText(
//				container,
//				JptUiDetailsMessages.ColumnComposite_columnDefinition,
//				buildColumnDefinitionHolder(getSubjectHolder())
//			);
//		}
//
//		private void addLengthCombo(Composite container) {
//			new IntegerCombo<DiscriminatorColumn>(this, container) {
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
//					return new PropertyAspectAdapter<DiscriminatorColumn, Integer>(getSubjectHolder(), DiscriminatorColumn.DEFAULT_LENGTH_PROPERTY) {
//						@Override
//						protected Integer buildValue_() {
//							return Integer.valueOf(this.subject.getDefaultLength());
//						}
//					};
//				}
//
//				@Override
//				protected ModifiablePropertyValueModel<Integer> buildSelectedItemHolder() {
//					return new PropertyAspectAdapter<DiscriminatorColumn, Integer>(getSubjectHolder(), DiscriminatorColumn.SPECIFIED_LENGTH_PROPERTY) {
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
//		private ModifiablePropertyValueModel<String> buildColumnDefinitionHolder(PropertyValueModel<DiscriminatorColumn> discriminatorColumnHolder) {
//
//			return new PropertyAspectAdapter<DiscriminatorColumn, String>(discriminatorColumnHolder, NamedColumn.COLUMN_DEFINITION_PROPERTY) {
//				@Override
//				protected String buildValue_() {
//					return this.subject.getColumnDefinition();
//				}
//
//				@Override
//				protected void setValue_(String value) {
//					if (value.length() == 0) {
//						value = null;
//					}
//					this.subject.setColumnDefinition(value);
//				}
//			};
//		}
//	}
	
	private ModifiablePropertyValueModel<DiscriminatorFormula> buildDiscriminatorFormulaHolder() {
		return new PropertyAspectAdapter<HibernateJavaEntity, DiscriminatorFormula>(getSubjectHolder(), HibernateEntity.DISCRIMINATOR_FORMULA_PROPERTY) {
			@Override
			protected DiscriminatorFormula buildValue_() {
				return this.subject.getDiscriminatorFormula();
			}
		};
	}
}
