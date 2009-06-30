/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.mapping.details;

import org.eclipse.jpt.core.context.GeneratorHolder;
import org.eclipse.jpt.ui.internal.mappings.details.GeneratorsComposite;
import org.eclipse.jpt.ui.internal.widgets.Pane;
import org.eclipse.jpt.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.utility.internal.model.value.SimplePropertyValueModel;
import org.eclipse.jpt.utility.model.value.WritablePropertyValueModel;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.GenericGeneratorHolder;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateGeneratorsComposite extends GeneratorsComposite {

	public HibernateGeneratorsComposite(Pane<? extends GeneratorHolder> parentPane, Composite parent) {
		super(parentPane, parent);
	}

	private WritablePropertyValueModel<Boolean> genericGeneratorExpansionStateHolder;
	
	
	@Override
	protected void initialize() {
		super.initialize();
		genericGeneratorExpansionStateHolder = new SimplePropertyValueModel<Boolean>(false);
	}
	
	@Override
	protected void initializeLayout(Composite container) {
		super.initializeLayout(container);
		initializeGenericGeneratorPane(container);
	}
	
	protected void initializeGenericGeneratorPane(Composite container) {

		// Sequence Generator sub-section
		container = addCollapsableSubSection(
			addSubPane(container, 10),
			"Generic Generator",
			genericGeneratorExpansionStateHolder
		);

		// Sequence Generator check box
		Button genericGeneratorCheckBox = addCheckBox(
			addSubPane(container, 5),
			"Generic generator",
			buildGenericGeneratorBooleanHolder(),
			null//TODO add help
		);

		// Sequence Generator pane
		new GenericGeneratorComposite(
			this,
			addSubPane(container, 0, genericGeneratorCheckBox.getBorderWidth() + 16)
		);
		
	}

	private WritablePropertyValueModel<Boolean> buildGenericGeneratorBooleanHolder() {
		return new PropertyAspectAdapter<GeneratorHolder, Boolean>(getSubjectHolder(), GenericGeneratorHolder.GENERIC_GENERATOR_PROPERTY) {
			@Override
			protected Boolean buildValue_() {
				return ((GenericGeneratorHolder)subject).getGenericGenerator() != null;
			}

			@Override
			protected void setValue_(Boolean value) {

				if (value && (((GenericGeneratorHolder)subject).getGenericGenerator() == null)) {
					((GenericGeneratorHolder)subject).addGenericGenerator();
				}
				else if (!value && (((GenericGeneratorHolder)subject).getGenericGenerator() != null)) {
					((GenericGeneratorHolder)subject).removeGenericGenerator();
				}
			}
		};
	}
	
	

}
