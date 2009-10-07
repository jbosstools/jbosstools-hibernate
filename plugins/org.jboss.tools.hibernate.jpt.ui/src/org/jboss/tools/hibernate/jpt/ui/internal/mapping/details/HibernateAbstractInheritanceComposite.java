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

import org.eclipse.jpt.core.context.Entity;
import org.eclipse.jpt.core.context.InheritanceType;
import org.eclipse.jpt.ui.internal.JpaHelpContextIds;
import org.eclipse.jpt.ui.internal.mappings.JptUiMappingsMessages;
import org.eclipse.jpt.ui.internal.mappings.db.ColumnCombo;
import org.eclipse.jpt.ui.internal.mappings.details.AbstractEntityComposite;
import org.eclipse.jpt.ui.internal.mappings.details.AbstractInheritanceComposite;
import org.eclipse.jpt.ui.internal.mappings.details.DiscriminatorColumnComposite;
import org.eclipse.jpt.ui.internal.util.ControlEnabler;
import org.eclipse.jpt.ui.internal.widgets.EnumFormComboViewer;
import org.eclipse.jpt.ui.internal.widgets.FormPane;
import org.eclipse.jpt.utility.internal.StringConverter;
import org.eclipse.jpt.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.utility.internal.model.value.PropertyListValueModelAdapter;
import org.eclipse.jpt.utility.model.value.ListValueModel;
import org.eclipse.jpt.utility.model.value.WritablePropertyValueModel;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateEntity;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntity;

/**
 * Here the layout of this pane:
 * <pre>
 * -----------------------------------------------------------------------------
 * |                      ---------------------------------------------------- |
 * | Strategy:            | EnumComboViewer                                |v| |
 * |                      ---------------------------------------------------- |
 * |                      ---------------------------------------------------- |
 * | Value:               | I                                              |v| |
 * |                      ---------------------------------------------------- |
 * |                                                                           |
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
 * |                      ---------------------------------------------------- |
 * | Column Definition:   | I                                                | |
 * |                      ---------------------------------------------------- |
 * |                      -------------                                        |
 * | Length:              | I       |I|                                        |
 * |                      -------------                                        |
 * | ------------------------------------------------------------------------- |
 * | |                                                                       | |
 * | | PrimaryKeyJoinColumnsComposite                                        | |
 * | |                                                                       | |
 * | ------------------------------------------------------------------------- |
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
@SuppressWarnings("nls")
public abstract class HibernateAbstractInheritanceComposite<T extends HibernateEntity> extends FormPane<T> {

	/**
	 * A key used to represent the default value, this is required to convert
	 * the selected item from a combo to <code>null</code>. This key is most
	 * likely never typed the user and it will help to convert the value to
	 * <code>null</code> when it's time to set the new selected value into the
	 * model.
	 */
	protected static String DEFAULT_KEY = "?!#!?#?#?default?#?!#?!#?";
	
	protected static String NONE_KEY = "?!#!?#?#?none?#?!#?!#?";

	/**
	 * Creates a new <code>InheritanceComposite</code>.
	 *
	 * @param parentPane The parent container of this one
	 * @param parent The parent container
	 */
	public HibernateAbstractInheritanceComposite(FormPane<? extends T> parentPane,
	                            Composite parent) {

		super(parentPane, parent, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initializeLayout(Composite container) {

		int groupBoxMargin = getGroupBoxMargin();

		Composite subPane = addSubPane(
			container, 0, groupBoxMargin, 0, groupBoxMargin
		);

		// Strategy widgets
		addLabeledComposite(
			subPane,
			JptUiMappingsMessages.InheritanceComposite_strategy,
			addStrategyCombo(subPane),
			JpaHelpContextIds.ENTITY_INHERITANCE_STRATEGY
		);

		// Discriminator Value widgets
		CCombo discriminatorValueCombo = addEditableCCombo(
			subPane,
			buildDiscriminatorValueListHolder(),
			buildDiscriminatorValueHolder(),
			buildDiscriminatorValueConverter()
		);
		Label discriminatorValueLabel = addLabel(
			subPane, 
			JptUiMappingsMessages.InheritanceComposite_discriminatorValue
		);
		addLabeledComposite(
			subPane,
			discriminatorValueLabel,
			discriminatorValueCombo.getParent(),
			null,
			JpaHelpContextIds.ENTITY_INHERITANCE_DISCRIMINATOR_VALUE
		);
	
		new ControlEnabler(buildDiscriminatorValueEnabledHolder(), discriminatorValueCombo, discriminatorValueLabel);
		
		if (getSubject() instanceof HibernateJavaEntity) {
			new HibernateDiscriminatorColumnComposite((FormPane<? extends HibernateJavaEntity>) this, container);			
		} else {
			new DiscriminatorColumnComposite<Entity>(this, container);
		}
		

		// Primary Key Join Columns widgets
		addPrimaryKeyJoinColumnsComposite(addSubPane(container, 5));
	}
	
	protected WritablePropertyValueModel<Boolean> buildDiscriminatorValueEnabledHolder() {
		return new PropertyAspectAdapter<Entity, Boolean>(getSubjectHolder(), Entity.SPECIFIED_DISCRIMINATOR_VALUE_IS_ALLOWED_PROPERTY) {
			@Override
			protected Boolean buildValue_() {
				return Boolean.valueOf(this.subject.specifiedDiscriminatorValueIsAllowed());
			}
		};
	}

	private ListValueModel<String> buildDefaultDiscriminatorListValueHolder() {
		return new PropertyListValueModelAdapter<String>(
			buildDefaultDiscriminatorValueHolder()
		);
	}

	private WritablePropertyValueModel<String> buildDefaultDiscriminatorValueHolder() {
		return new PropertyAspectAdapter<Entity, String>(getSubjectHolder(), Entity.DEFAULT_DISCRIMINATOR_VALUE_PROPERTY, Entity.DISCRIMINATOR_VALUE_IS_UNDEFINED_PROPERTY) {
			@Override
			protected String buildValue_() {
				String value = this.subject.getDefaultDiscriminatorValue();
				if (value == null && this.subject.discriminatorValueIsUndefined()) {
					return NONE_KEY;
				}

				if (value == null) {
					value = DEFAULT_KEY;
				}
				else {
					value = DEFAULT_KEY + value;
				}

				return value;
			}
		};
	}

	private StringConverter<String> buildDiscriminatorValueConverter() {
		return new StringConverter<String>() {
			public String convertToString(String value) {

				if (getSubject() == null) {
					return null;
				}

				if (value == null) {
					value = getSubject().getDefaultDiscriminatorValue();
					if (value == null && getSubject().discriminatorValueIsUndefined()) {
						value = NONE_KEY;
					}
					else {
						value = (value != null) ? 
								DEFAULT_KEY + value
							: 
								DEFAULT_KEY;
					}
				}
				if (value.startsWith(DEFAULT_KEY)) {
					String defaultName = value.substring(DEFAULT_KEY.length());

					if (defaultName.length() > 0) {
						value = NLS.bind(
							JptUiMappingsMessages.DefaultWithOneParam,
							defaultName
						);
					}
					else {
						value = JptUiMappingsMessages.ProviderDefault;
					}
				}
				if (value.startsWith(NONE_KEY)) {
					value = JptUiMappingsMessages.NoneSelected;
				}
				return value;
			}
		};
	}

	private WritablePropertyValueModel<String> buildDiscriminatorValueHolder() {
		return new PropertyAspectAdapter<Entity, String>(getSubjectHolder(), Entity.SPECIFIED_DISCRIMINATOR_VALUE_PROPERTY) {
			@Override
			protected String buildValue_() {
				return this.subject.getSpecifiedDiscriminatorValue();
			}

			@Override
			protected void setValue_(String value) {

				// Convert the default value or an empty string to null
				if ((value != null) &&
				   ((value.length() == 0) || value.startsWith(DEFAULT_KEY) || value.startsWith(NONE_KEY))) {

					value = null;
				}

				this.subject.setSpecifiedDiscriminatorValue(value);
			}
		};
	}

	private ListValueModel<String> buildDiscriminatorValueListHolder() {
		return buildDefaultDiscriminatorListValueHolder();
	}

	private EnumFormComboViewer<Entity, InheritanceType> addStrategyCombo(Composite container) {

		return new EnumFormComboViewer<Entity, InheritanceType>(this, container) {

			@Override
			protected void addPropertyNames(Collection<String> propertyNames) {
				super.addPropertyNames(propertyNames);
				propertyNames.add(Entity.DEFAULT_INHERITANCE_STRATEGY_PROPERTY);
				propertyNames.add(Entity.SPECIFIED_INHERITANCE_STRATEGY_PROPERTY);
			}

			@Override
			protected InheritanceType[] getChoices() {
				return InheritanceType.values();
			}

			@Override
			protected InheritanceType getDefaultValue() {
				return getSubject().getDefaultInheritanceStrategy();
			}

			@Override
			protected String displayString(InheritanceType value) {
				return buildDisplayString(
					JptUiMappingsMessages.class,
					AbstractInheritanceComposite.class,
					value
				);
			}

			@Override
			protected InheritanceType getValue() {
				return getSubject().getSpecifiedInheritanceStrategy();
			}

			@Override
			protected void setValue(InheritanceType value) {
				getSubject().setSpecifiedInheritanceStrategy(value);
			}
		};
	}

	protected abstract void addPrimaryKeyJoinColumnsComposite(Composite container);
}