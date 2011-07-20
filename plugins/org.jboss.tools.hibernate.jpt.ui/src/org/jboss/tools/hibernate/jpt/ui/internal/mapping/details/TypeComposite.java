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
package org.jboss.tools.hibernate.jpt.ui.internal.mapping.details;

import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.TransformationPropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.WritablePropertyValueModel;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.TypeConverter;

/**
 * @author Dmitry Geraskov
 *
 */
public class TypeComposite extends Pane<TypeConverter> {

	/**
	 * Creates a new <code>TypeComposite</code>.
	 *
	 * @param parentPane The parent container of this one
	 * @param parent The parent container
	 */
	public TypeComposite(PropertyValueModel<? extends TypeConverter> subjectHolder,
	                             Composite parent,
	                             WidgetFactory widgetFactory) {

		super(subjectHolder, parent, widgetFactory);
	}

	@Override
	protected void initializeLayout(Composite container) {
		// Name widgets
		addText(
			container,
			buildTypeTypeHolder(),
			null,
			buildBooleanHolder()
		);
	}

	private WritablePropertyValueModel<String> buildTypeTypeHolder() {
		return new PropertyAspectAdapter<TypeConverter, String>(getSubjectHolder(), TypeConverter.TYPE_PROPERTY) {
			@Override
			protected String buildValue_() {
				return subject.getHibernateType();
			}

			@Override
			protected void setValue_(String value) {
				subject.setHibernateType(value);
			}
		};
	}
	
	protected PropertyValueModel<Boolean> buildBooleanHolder() {
		return new TransformationPropertyValueModel<TypeConverter, Boolean>(getSubjectHolder()) {
			@Override
			protected Boolean transform(TypeConverter value) {
				if (getSubject() != null && getSubject().getParent().getPersistentAttribute().isVirtual()) {
					return Boolean.FALSE;
				}
				return Boolean.valueOf(value != null);
			}
		};
	}

}
