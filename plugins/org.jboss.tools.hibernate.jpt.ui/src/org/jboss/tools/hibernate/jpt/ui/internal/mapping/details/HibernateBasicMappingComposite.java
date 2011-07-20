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

import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.internal.model.value.TransformationPropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.context.BasicMapping;
import org.eclipse.jpt.jpa.core.context.Converter;
import org.eclipse.jpt.jpa.ui.details.JpaComposite;
import org.eclipse.jpt.jpa.ui.internal.details.AbstractBasicMappingComposite;
import org.eclipse.jpt.jpa.ui.internal.details.ColumnComposite;
import org.eclipse.jpt.jpa.ui.internal.details.EnumTypeComposite;
import org.eclipse.jpt.jpa.ui.internal.details.FetchTypeComposite;
import org.eclipse.jpt.jpa.ui.internal.details.OptionalComposite;
import org.eclipse.jpt.jpa.ui.internal.details.TemporalTypeComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.Generated;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateColumn;
import org.jboss.tools.hibernate.jpt.core.internal.context.IndexHolder;
import org.jboss.tools.hibernate.jpt.core.internal.context.TypeConverter;

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
public class HibernateBasicMappingComposite extends AbstractBasicMappingComposite<BasicMapping>
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
		super.initializeLayout(container);
		this.initializeIndexCollapsibleSection(container);
	}
	
	protected void initializeBasicSection(Composite container) {
		new HibernateColumnComposite(this, (PropertyValueModel<? extends HibernateColumn>) buildColumnHolder(), container);
		if (getSubject() instanceof Generated) {
			new GeneratedComposite((Pane<? extends Generated>) this, container);
		}
		new FetchTypeComposite(this, container);
		new OptionalComposite(this, addSubPane(container, 4));
	}
	
	@Override
	protected void initializeTypeSection(Composite container) {
		super.initializeTypeSection(container);
		
		PropertyValueModel<Converter> converterHolder = buildConverterHolder();

		addRadioButton(
				container, 
				HibernateUIMappingMessages.TypeComposite_type,
				buildConverterBooleanHolder(TypeConverter.class),
				null);
		
		registerSubPane(new TypeComposite(buildHibernateConverterHolder(converterHolder),
				container, getWidgetFactory()));
		
	}
	
	protected PropertyValueModel<TypeConverter> buildHibernateConverterHolder(PropertyValueModel<Converter> converterHolder) {
		return new TransformationPropertyValueModel<Converter, TypeConverter>(converterHolder) {
			@Override
			protected TypeConverter transform_(Converter converter) {
				return converter.getType() == TypeConverter.class ? (TypeConverter) converter : null;
			}
		};
	}
	
	/*protected WritablePropertyValueModel<Boolean> buildHibernateTypeBooleanHolder() {
		return new PropertyAspectAdapter<BasicMapping, Boolean>(getSubjectHolder(),  TypeHolder.TYPE_PROPERTY) {
			@Override
			protected Boolean buildValue_() {
				return Boolean.valueOf(((TypeHolder)subject).getType() != null);
			}

			@Override
			protected void setValue_(Boolean value) {
				if (value.booleanValue() && (((TypeHolder)subject).getType() == null)) {
					((TypeHolder)subject).addType();
				} else if (!value.booleanValue() && (((TypeHolder)subject).getType() != null)) {
					((TypeHolder)subject).removeType();
				}
			}
		};
	}*/

	protected void initializeIndexCollapsibleSection(Composite container) {
		if (getSubject() instanceof IndexHolder) {
			container = addCollapsibleSection(
					container,
					HibernateUIMappingMessages.Index_section_index
				);
			((GridLayout) container.getLayout()).numColumns = 2;
			this.initializeIndexSection(container);
		}
	}
	
	private void initializeIndexSection(Composite container) {
		new IndexHolderComposite((Pane<? extends IndexHolder>) this, container);
	}


}
