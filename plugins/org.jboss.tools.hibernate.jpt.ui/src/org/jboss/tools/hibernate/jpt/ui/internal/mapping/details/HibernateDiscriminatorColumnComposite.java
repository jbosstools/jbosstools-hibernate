/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
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

import org.eclipse.jpt.core.context.DiscriminatorColumn;
import org.eclipse.jpt.core.context.DiscriminatorType;
import org.eclipse.jpt.core.context.Entity;
import org.eclipse.jpt.core.context.NamedColumn;
import org.eclipse.jpt.db.Table;
import org.eclipse.jpt.ui.internal.JpaHelpContextIds;
import org.eclipse.jpt.ui.internal.mappings.JptUiMappingsMessages;
import org.eclipse.jpt.ui.internal.mappings.db.ColumnCombo;
import org.eclipse.jpt.ui.internal.mappings.details.AbstractEntityComposite;
import org.eclipse.jpt.ui.internal.mappings.details.DiscriminatorColumnComposite;
import org.eclipse.jpt.ui.internal.util.LabeledControlUpdater;
import org.eclipse.jpt.ui.internal.util.LabeledLabel;
import org.eclipse.jpt.ui.internal.util.PaneEnabler;
import org.eclipse.jpt.ui.internal.widgets.EnumFormComboViewer;
import org.eclipse.jpt.ui.internal.widgets.FormPane;
import org.eclipse.jpt.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.utility.internal.model.value.SimplePropertyValueModel;
import org.eclipse.jpt.utility.internal.model.value.TransformationPropertyValueModel;
import org.eclipse.jpt.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.utility.model.value.WritablePropertyValueModel;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.jboss.tools.hibernate.jpt.core.internal.context.DiscriminatorFormula;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateEntity;

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
public class HibernateDiscriminatorColumnComposite <T extends HibernateEntity> extends FormPane<T> {
	
	private WritablePropertyValueModel<DiscriminatorFormula> discriminatorFormulaHolder;

	/**
	 * Creates a new <code>InheritanceComposite</code>.
	 *
	 * @param parentPane The parent container of this one
	 * @param parent The parent container
	 */
	public HibernateDiscriminatorColumnComposite(FormPane<? extends T> parentPane,
	                            Composite parent) {

		super(parentPane, parent, false);
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		discriminatorFormulaHolder = new SimplePropertyValueModel<DiscriminatorFormula>();
	}

	@Override
	protected void initializeLayout(Composite container) {
		// Discriminator Column sub-pane
		Composite discriminatorColumnContainer = addTitledGroup(
			addSubPane(container, 10),
			JptUiMappingsMessages.InheritanceComposite_discriminatorColumnGroupBox
		);

		PropertyValueModel<DiscriminatorColumn> discriminatorColumnHolder =
			buildDiscriminatorColumnHolder();

		// Name widgets
		addLabeledComposite(
			discriminatorColumnContainer,
			JptUiMappingsMessages.DiscriminatorColumnComposite_name,
			addDiscriminatorColumnCombo(container, discriminatorColumnHolder),
			JpaHelpContextIds.ENTITY_INHERITANCE_DISCRIMINATOR_COLUMN
		);
		
		// Formula widgets
		addLabeledText(
			discriminatorColumnContainer,
			HibernateUIMappingMessages.HibernateDiscriminatorColumnComposite_formula,
			buildDiscriminatorFormulaHolder(),
			null//TODO help
		);

		// Discriminator Type widgets
		addLabeledComposite(
			discriminatorColumnContainer,
			JptUiMappingsMessages.DiscriminatorColumnComposite_discriminatorType,
			addDiscriminatorTypeCombo(container, discriminatorColumnHolder),
			JpaHelpContextIds.ENTITY_INHERITANCE_DISCRIMINATOR_TYPE
		);

		container = addCollapsableSubSection(
			discriminatorColumnContainer,
			JptUiMappingsMessages.InheritanceComposite_detailsGroupBox,
			new SimplePropertyValueModel<Boolean>(Boolean.FALSE)
		);

		new DetailsComposite(this, discriminatorColumnHolder, addSubPane(container, 0, 16));

		new PaneEnabler(buildDiscriminatorColumnEnabledHolder(), this);
	}

	private ColumnCombo<DiscriminatorColumn> addDiscriminatorColumnCombo(
		Composite container,
		PropertyValueModel<DiscriminatorColumn> discriminatorColumnHolder) {

		return new ColumnCombo<DiscriminatorColumn>(
			this,
			discriminatorColumnHolder,
			container)
		{

			@Override
			protected void addPropertyNames(Collection<String> propertyNames) {
				super.addPropertyNames(propertyNames);
				propertyNames.add(NamedColumn.SPECIFIED_NAME_PROPERTY);
				propertyNames.add(NamedColumn.DEFAULT_NAME_PROPERTY);
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
				return JptUiMappingsMessages.NoneSelected;
			}
		};
	}
	
	private PropertyValueModel<DiscriminatorColumn> buildDiscriminatorColumnHolder() {
		return new PropertyAspectAdapter<Entity, DiscriminatorColumn>(getSubjectHolder()) {
			@Override
			protected DiscriminatorColumn buildValue_() {
				return this.subject.getDiscriminatorColumn();
			}
		};
	}
	
	private WritablePropertyValueModel<String> buildDiscriminatorFormulaHolder() {
		return new PropertyAspectAdapter<DiscriminatorFormula, String>(discriminatorFormulaHolder, DiscriminatorFormula.VALUE_PROPERTY) {
			@Override
			protected String buildValue_() {
				return subject == null ? null : subject.getValue();
			}

			@Override
			public void setValue(String value) {
				if (value != null && !"".equals(value)) { //$NON-NLS-1$
					DiscriminatorFormula discriminatorFormula = (getSubject().getDiscriminatorFormula() != null 
							? getSubject().getDiscriminatorFormula()
							: getSubject().addDiscriminatorFormula());
					discriminatorFormula.setValue(value);
					discriminatorFormulaHolder.setValue(discriminatorFormula);
				}
				setValue_(value);
			}

			@Override
			protected void setValue_(String value) {
				if ("".equals(value)) {//$NON-NLS-1$
					value = null;
				}
				if (value == null && subject != null){
					getSubject().removeDiscriminatorFormula();
					return;
				} else {
					subject.setValue(value);
				}
			}
		};
	}

	private EnumFormComboViewer<DiscriminatorColumn, DiscriminatorType> addDiscriminatorTypeCombo(
		Composite container,
		PropertyValueModel<DiscriminatorColumn> discriminatorColumnHolder) {

		return new EnumFormComboViewer<DiscriminatorColumn, DiscriminatorType>(
			this,
			discriminatorColumnHolder,
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
				return buildDisplayString(
					JptUiMappingsMessages.class,
					DiscriminatorColumnComposite.class,
					value
				);
			}
			
			@Override
			protected String nullDisplayString() {
				return JptUiMappingsMessages.NoneSelected;
			}
			
			@Override
			protected DiscriminatorType getValue() {
				return getSubject().getSpecifiedDiscriminatorType();
			}

			@Override
			protected void setValue(DiscriminatorType value) {
				getSubject().setSpecifiedDiscriminatorType(value);
			}
		};
	}
	
	protected WritablePropertyValueModel<Boolean> buildDiscriminatorColumnEnabledHolder() {
		return new PropertyAspectAdapter<Entity, Boolean>(getSubjectHolder(), Entity.SPECIFIED_DISCRIMINATOR_COLUMN_IS_ALLOWED_PROPERTY) {
			@Override
			protected Boolean buildValue_() {
				return Boolean.valueOf(this.subject.specifiedDiscriminatorColumnIsAllowed());
			}
		};
	}
	
	protected class DetailsComposite extends FormPane<DiscriminatorColumn> {
		public DetailsComposite(FormPane<?> parentPane,
            PropertyValueModel<? extends DiscriminatorColumn> subjectHolder,
            Composite parent) {

			super(parentPane, subjectHolder, parent, false);
		}

		@Override
		protected void initializeLayout(Composite container) {
			// Length widgets
			Spinner lengthSpinner = addLabeledSpinner(
				container,
				JptUiMappingsMessages.ColumnComposite_length,
				buildLengthHolder(),
				-1,
				-1,
				Integer.MAX_VALUE,
				addDefaultLengthLabel(container),
				JpaHelpContextIds.MAPPING_COLUMN_LENGTH
			);

			updateGridData(container, lengthSpinner);

			// Column Definition widgets
			addLabeledText(
				container,
				JptUiMappingsMessages.ColumnComposite_columnDefinition,
				buildColumnDefinitionHolder(getSubjectHolder())
			);
		}

		private WritablePropertyValueModel<Integer> buildLengthHolder() {

			return new PropertyAspectAdapter<DiscriminatorColumn, Integer>(getSubjectHolder(), DiscriminatorColumn.SPECIFIED_LENGTH_PROPERTY) {
				@Override
				protected Integer buildValue_() {
					return this.subject.getSpecifiedLength();
				}

				@Override
				protected void setValue_(Integer value) {
					if (value.intValue() == -1) {
						value = null;
					}
					this.subject.setSpecifiedLength(value);
				}
			};
		}

		private Control addDefaultLengthLabel(Composite container) {

			Label label = addLabel(
				container,
				JptUiMappingsMessages.DefaultEmpty
			);

			new LabeledControlUpdater(
				new LabeledLabel(label),
				buildDefaultLengthLabelHolder()
			);

			return label;
		}

		private PropertyValueModel<String> buildDefaultLengthLabelHolder() {

			return new TransformationPropertyValueModel<Integer, String>(buildDefaultLengthHolder()) {

				@Override
				protected String transform(Integer value) {

					int defaultValue = (getSubject() != null) ? getSubject().getDefaultLength() :
					                                             DiscriminatorColumn.DEFAULT_LENGTH;

					return NLS.bind(
						JptUiMappingsMessages.DefaultWithOneParam,
						Integer.valueOf(defaultValue)
					);
				}
			};
		}

		private WritablePropertyValueModel<Integer> buildDefaultLengthHolder() {
			return new PropertyAspectAdapter<DiscriminatorColumn, Integer>(getSubjectHolder(), DiscriminatorColumn.DEFAULT_LENGTH_PROPERTY) {
				@Override
				protected Integer buildValue_() {
					return Integer.valueOf(this.subject.getDefaultLength());
				}

				@Override
				protected synchronized void subjectChanged() {
					Object oldValue = this.getValue();
					super.subjectChanged();
					Object newValue = this.getValue();

					// Make sure the default value is appended to the text
					if (oldValue == newValue && newValue == null) {
						this.fireAspectChange(Integer.valueOf(Integer.MIN_VALUE), newValue);
					}
				}
			};
		}

		private WritablePropertyValueModel<String> buildColumnDefinitionHolder(PropertyValueModel<DiscriminatorColumn> discriminatorColumnHolder) {

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
		
		/**
		 * Changes the layout of the given container by changing which widget will
		 * grab the excess of horizontal space. By default, the center control grabs
		 * the excess space, we change it to be the right control.
		 *
		 * @param container The container containing the controls needing their
		 * <code>GridData</code> to be modified from the default values
		 * @param spinner The spinner that got created
		 */
		private void updateGridData(Composite container, Spinner spinner) {

			// It is possible the spinner's parent is not the container of the
			// label, spinner and right control (a pane is sometimes required for
			// painting the spinner's border)
			Composite paneContainer = spinner.getParent();

			while (container != paneContainer.getParent()) {
				paneContainer = paneContainer.getParent();
			}

			Control[] controls = paneContainer.getChildren();

			GridData gridData = new GridData();
			gridData.grabExcessHorizontalSpace = false;
			gridData.horizontalAlignment       = GridData.BEGINNING;
			controls[1].setLayoutData(gridData);

			controls[2].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			removeAlignRight(controls[2]);
		}

	}
}