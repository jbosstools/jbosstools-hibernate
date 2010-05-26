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

import org.eclipse.jpt.ui.internal.widgets.EnumFormComboViewer;
import org.eclipse.jpt.ui.internal.widgets.FormPane;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.Generated;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenerationTime;

/**
 * @author Dmitry Geraskov
 *
 */
public class GeneratedComposite extends FormPane<Generated> {
	/**
	 * Creates a new <code>GeneratedComposite</code>.
	 *
	 * @param parentPane The parent container of this one
	 * @param parent The parent container
	 */
	public GeneratedComposite(FormPane<? extends Generated> parentPane,
	                          Composite parent) {

		super(parentPane, parent);
	}

	private EnumFormComboViewer<Generated, GenerationTime> addGenerationTimeCombo(Composite container) {

		return new EnumFormComboViewer<Generated, GenerationTime>(this, container) {

			@Override
			protected void addPropertyNames(Collection<String> propertyNames) {
				super.addPropertyNames(propertyNames);
				propertyNames.add(Generated.GENERATION_TIME_PROPERTY);
			}

			@Override
			protected GenerationTime[] getChoices() {
				return GenerationTime.values();
			}

			@Override
			protected GenerationTime getDefaultValue() {
				return null;
			}

			@Override
			protected GenerationTime getValue() {
				return getSubject().getGenerationTime();
			}

			@Override
			protected void setValue(GenerationTime value) {
				getSubject().setGenerationTime(value);
			}

			@Override
			protected String displayString(GenerationTime value) {
				return value == null ? null : value.toString();
			}
		};
	}

	@Override
	protected void initializeLayout(Composite container) {

		addLabeledComposite(
			container,
			HibernateUIMappingMessages.BasicGeneralSection_generated,
			addGenerationTimeCombo(container),
			null//TODO help
		);
	}
}
