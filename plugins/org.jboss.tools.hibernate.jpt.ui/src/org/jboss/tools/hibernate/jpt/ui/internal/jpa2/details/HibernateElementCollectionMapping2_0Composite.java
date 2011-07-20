/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.jpa2.details;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.TransformationPropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.WritablePropertyValueModel;
import org.eclipse.jpt.jpa.core.context.Converter;
import org.eclipse.jpt.jpa.core.context.ConvertibleMapping;
import org.eclipse.jpt.jpa.core.jpa2.context.ElementCollectionMapping2_0;
import org.eclipse.jpt.jpa.ui.internal.details.JptUiDetailsMessages;
import org.eclipse.jpt.jpa.ui.internal.jpa2.details.AbstractElementCollectionMapping2_0Composite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.Section;
import org.jboss.tools.hibernate.jpt.core.internal.context.TypeConverter;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateUIMappingMessages;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.TypeComposite;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateElementCollectionMapping2_0Composite extends
		AbstractElementCollectionMapping2_0Composite<ElementCollectionMapping2_0> {

	/**
	 * @param subjectHolder
	 * @param parent
	 * @param widgetFactory
	 */
	public HibernateElementCollectionMapping2_0Composite(
			PropertyValueModel<? extends ElementCollectionMapping2_0> subjectHolder,
                    Composite parent,
                    WidgetFactory widgetFactory) {
		super(subjectHolder, parent, widgetFactory);
	}

	@Override
	protected void initializeBasicValueSection(Composite container) {
		List<Control> oldChildren = Arrays.asList(container.getChildren());
		super.initializeBasicValueSection(container);
		List<Control> newChildren = Arrays.asList(container.getChildren());
		newChildren.removeAll(oldChildren);
		
		//FIXME due to closed method we need to do the search
		Composite converterSection = findTypeSection(((Composite)newChildren.get(0)).getChildren());
		
		PropertyValueModel<Converter> converterHolder = buildConverterHolder();
		// Temporal
		addRadioButton(
			converterSection, 
			HibernateUIMappingMessages.TypeComposite_type,
			buildTypeBooleanHolder(), 
			null);
		registerSubPane(new TypeComposite(buildTypeConverterHolder(converterHolder), converterSection, getWidgetFactory()));
		
	}
	
	private WritablePropertyValueModel<Boolean> buildTypeBooleanHolder() {
		return new PropertyAspectAdapter<ElementCollectionMapping2_0, Boolean>(getSubjectHolder(), ConvertibleMapping.CONVERTER_PROPERTY) {
			@Override
			protected Boolean buildValue_() {
				Converter converter = this.subject.getConverter();
				return Boolean.valueOf(converter.getType() == TypeConverter.class);
			}

			@Override
			protected void setValue_(Boolean value) {
				if (value.booleanValue()) {
					this.subject.setConverter(TypeConverter.class);
				}
			}
		};
	}
	
	private PropertyValueModel<TypeConverter> buildTypeConverterHolder(PropertyValueModel<Converter> converterHolder) {
		return new TransformationPropertyValueModel<Converter, TypeConverter>(converterHolder) {
			@Override
			protected TypeConverter transform_(Converter converter) {
				return converter.getType() == TypeConverter.class ? (TypeConverter) converter : null;
			}
		};
	}
	
	protected PropertyValueModel<Converter> buildConverterHolder() {
		return new PropertyAspectAdapter<ElementCollectionMapping2_0, Converter>(getSubjectHolder(), ConvertibleMapping.CONVERTER_PROPERTY) {
			@Override
			protected Converter buildValue_() {
				return this.subject.getConverter();
			}
		};
	}
	
	protected Composite findTypeSection(Control[] controls){
		for (int i = 0; i < controls.length; i++) {
			if (controls[i] instanceof Section){
				Section section = (Section) controls[i];
				return (Composite) section.getClient();
			}
		}
		return null;
	}
	
}
