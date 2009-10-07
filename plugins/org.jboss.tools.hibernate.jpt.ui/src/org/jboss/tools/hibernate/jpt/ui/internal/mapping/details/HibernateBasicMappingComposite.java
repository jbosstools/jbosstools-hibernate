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

import org.eclipse.jpt.core.context.BasicMapping;
import org.eclipse.jpt.core.context.Converter;
import org.eclipse.jpt.core.context.ConvertibleMapping;
import org.eclipse.jpt.core.context.EnumeratedConverter;
import org.eclipse.jpt.core.context.TemporalConverter;
import org.eclipse.jpt.ui.WidgetFactory;
import org.eclipse.jpt.ui.details.JpaComposite;
import org.eclipse.jpt.ui.internal.BaseJpaUiFactory;
import org.eclipse.jpt.ui.internal.mappings.JptUiMappingsMessages;
import org.eclipse.jpt.ui.internal.mappings.details.ColumnComposite;
import org.eclipse.jpt.ui.internal.mappings.details.EnumTypeComposite;
import org.eclipse.jpt.ui.internal.mappings.details.FetchTypeComposite;
import org.eclipse.jpt.ui.internal.mappings.details.OptionalComposite;
import org.eclipse.jpt.ui.internal.mappings.details.TemporalTypeComposite;
import org.eclipse.jpt.ui.internal.widgets.FormPane;
import org.eclipse.jpt.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.utility.internal.model.value.TransformationPropertyValueModel;
import org.eclipse.jpt.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.utility.model.value.WritablePropertyValueModel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.Generated;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateColumn;

/**
 * Here the layout of this pane:
 * <pre>
 * -----------------------------------------------------------------------------
 * | ------------------------------------------------------------------------- |
 * | |                                                                       | |
 * | | ColumnComposite                                                       | |
 * | |                                                                       | |
 * | ------------------------------------------------------------------------- |
 * | ------------------------------------------------------------------------- |
 * | |                                                                       | |
 * | | FetchTypeComposite                                                    | |
 * | |                                                                       | |
 * | ------------------------------------------------------------------------- |
 * | ------------------------------------------------------------------------- |
 * | |                                                                       | |
 * | | TemporalTypeComposite                                                 | |
 * | |                                                                       | |
 * | ------------------------------------------------------------------------- |
 * | ------------------------------------------------------------------------- |
 * | |                                                                       | |
 * | | EnumTypeComposite                                                     | |
 * | |                                                                       | |
 * | ------------------------------------------------------------------------- |
 * | ------------------------------------------------------------------------- |
 * | |                                                                       | |
 * | | OptionalComposite                                                     | |
 * | |                                                                       | |
 * | ------------------------------------------------------------------------- |
 * | ------------------------------------------------------------------------- |
 * | |                                                                       | |
 * | | LobComposite                                                          | |
 * | |                                                                       | |
 * | ------------------------------------------------------------------------- |
 * -----------------------------------------------------------------------------</pre>
 *
 * @see BasicMapping
 * @see BaseJpaUiFactory - The factory creating this pane
 * @see ColumnComposite
 * @see EnumTypeComposite
 * @see FetchTypeComposite
 * @see LobComposite
 * @see OptionalComposite
 * @see TemporalTypeComposite
 *
 * @version 2.0
 * @since 1.0
 */
public class HibernateBasicMappingComposite extends FormPane<BasicMapping>
                                   implements JpaComposite
{
	/**
	 * Creates a new <code>HibernateBasicMappingComposite</code>.
	 *
	 * @param subjectHolder The holder of the subject <code>IBasicMapping</code>
	 * @param parent The parent container
	 * @param widgetFactory The factory used to create various common widgets
	 */
	public HibernateBasicMappingComposite(PropertyValueModel<? extends BasicMapping> subjectHolder,
	                             Composite parent,
	                             WidgetFactory widgetFactory) {

		super(subjectHolder, parent, widgetFactory);
	}

	@Override
	protected void initializeLayout(Composite container) {
		initializeGeneralPane(container);
		initializeTypePane(container);
	}
	
	@SuppressWarnings("unchecked")
	private void initializeGeneralPane(Composite container) {
		int groupBoxMargin = getGroupBoxMargin();

		new HibernateColumnComposite(this, buildColumnHolder(), container);

		// Align the widgets under the ColumnComposite
		container = addSubPane(container, 0, groupBoxMargin, 0, groupBoxMargin);

		if (getSubject() instanceof Generated) {
			new GeneratedComposite((FormPane<? extends Generated>) this, container);			
		}
		
		new FetchTypeComposite(this, container);
		new OptionalComposite(this, addSubPane(container, 4));

	}
	private void initializeTypePane(Composite container) {

		container = addCollapsableSection(
			container,
			JptUiMappingsMessages.TypeSection_type
		);
		((GridLayout) container.getLayout()).numColumns = 2;

		// No converter
		Button noConverterButton = addRadioButton(
			container, 
			JptUiMappingsMessages.TypeSection_default, 
			buildNoConverterHolder(), 
			null);
		((GridData) noConverterButton.getLayoutData()).horizontalSpan = 2;
		
		// Lob
		Button lobButton = addRadioButton(
			container, 
			JptUiMappingsMessages.TypeSection_lob, 
			buildLobConverterHolder(), 
			null);
		((GridData) lobButton.getLayoutData()).horizontalSpan = 2;
		
		PropertyValueModel<Converter> specifiedConverterHolder = buildSpecifiedConverterHolder();
		// Temporal
		addRadioButton(
			container, 
			JptUiMappingsMessages.TypeSection_temporal, 
			buildTemporalBooleanHolder(), 
			null);
		registerSubPane(new TemporalTypeComposite(buildTemporalConverterHolder(specifiedConverterHolder), container, getWidgetFactory()));
		
		
		// Enumerated
		addRadioButton(
			container, 
			JptUiMappingsMessages.TypeSection_enumerated, 
			buildEnumeratedBooleanHolder(), 
			null);
		registerSubPane(new EnumTypeComposite(buildEnumeratedConverterHolder(specifiedConverterHolder), container, getWidgetFactory()));
	}

	private PropertyValueModel<HibernateColumn> buildColumnHolder() {
		return new TransformationPropertyValueModel<BasicMapping, HibernateColumn>(getSubjectHolder()) {
			@Override
			protected HibernateColumn transform_(BasicMapping value) {
				return (HibernateColumn) value.getColumn();
			}
		};
	}

	private WritablePropertyValueModel<Boolean> buildNoConverterHolder() {
		return new PropertyAspectAdapter<BasicMapping, Boolean>(getSubjectHolder(), ConvertibleMapping.SPECIFIED_CONVERTER_PROPERTY) {
			@Override
			protected Boolean buildValue_() {
				return Boolean.valueOf(this.subject.getSpecifiedConverter() == null);
			}

			@Override
			protected void setValue_(Boolean value) {
				if (value.booleanValue()) {
					this.subject.setSpecifiedConverter(Converter.NO_CONVERTER);
				}
			}
		};
	}
	
	private WritablePropertyValueModel<Boolean> buildLobConverterHolder() {
		return new PropertyAspectAdapter<BasicMapping, Boolean>(getSubjectHolder(), ConvertibleMapping.SPECIFIED_CONVERTER_PROPERTY) {
			@Override
			protected Boolean buildValue_() {
				Converter converter = this.subject.getSpecifiedConverter();
				if (converter == null) {
					return Boolean.FALSE;
				}
				return Boolean.valueOf(converter.getType() == Converter.LOB_CONVERTER);
			}

			@Override
			protected void setValue_(Boolean value) {
				if (value.booleanValue()) {
					this.subject.setSpecifiedConverter(Converter.LOB_CONVERTER);
				}
			}
		};
	}
	
	private PropertyValueModel<Converter> buildSpecifiedConverterHolder() {
		return new PropertyAspectAdapter<BasicMapping, Converter>(getSubjectHolder(), ConvertibleMapping.SPECIFIED_CONVERTER_PROPERTY) {
			@Override
			protected Converter buildValue_() {
				return this.subject.getSpecifiedConverter();
			}
		};
	}
	
	private PropertyValueModel<TemporalConverter> buildTemporalConverterHolder(PropertyValueModel<Converter> converterHolder) {
		return new TransformationPropertyValueModel<Converter, TemporalConverter>(converterHolder) {
			@Override
			protected TemporalConverter transform_(Converter converter) {
				return (converter != null && converter.getType() == Converter.TEMPORAL_CONVERTER) ? (TemporalConverter) converter : null;
			}
		};
	}
	
	private PropertyValueModel<EnumeratedConverter> buildEnumeratedConverterHolder(PropertyValueModel<Converter> converterHolder) {
		return new TransformationPropertyValueModel<Converter, EnumeratedConverter>(converterHolder) {
			@Override
			protected EnumeratedConverter transform_(Converter converter) {
				return (converter != null && converter.getType() == Converter.ENUMERATED_CONVERTER) ? (EnumeratedConverter) converter : null;
			}
		};
	}

	private WritablePropertyValueModel<Boolean> buildTemporalBooleanHolder() {
		return new PropertyAspectAdapter<BasicMapping, Boolean>(getSubjectHolder(), BasicMapping.SPECIFIED_CONVERTER_PROPERTY) {
			@Override
			protected Boolean buildValue_() {
				Converter converter = this.subject.getSpecifiedConverter();
				if (converter == null) {
					return Boolean.FALSE;
				}
				return Boolean.valueOf(converter.getType() == Converter.TEMPORAL_CONVERTER);
			}

			@Override
			protected void setValue_(Boolean value) {
				if (value.booleanValue()) {
					this.subject.setSpecifiedConverter(Converter.TEMPORAL_CONVERTER);
				}
			}
		};
	}
	
	private WritablePropertyValueModel<Boolean> buildEnumeratedBooleanHolder() {
		return new PropertyAspectAdapter<BasicMapping, Boolean>(getSubjectHolder(), BasicMapping.SPECIFIED_CONVERTER_PROPERTY) {
			@Override
			protected Boolean buildValue_() {
				Converter converter = this.subject.getSpecifiedConverter();
				if (converter == null) {
					return Boolean.FALSE;
				}
				return Boolean.valueOf(converter.getType() == Converter.ENUMERATED_CONVERTER);
			}

			@Override
			protected void setValue_(Boolean value) {
				if (value.booleanValue()) {
					this.subject.setSpecifiedConverter(Converter.ENUMERATED_CONVERTER);
				}
			}
		};
	}
}
