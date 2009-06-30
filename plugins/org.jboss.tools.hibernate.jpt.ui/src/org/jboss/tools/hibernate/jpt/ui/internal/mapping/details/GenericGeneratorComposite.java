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
import org.eclipse.jpt.ui.internal.mappings.details.GeneratorComposite;
import org.eclipse.jpt.ui.internal.widgets.Pane;
import org.eclipse.jpt.utility.internal.StringConverter;
import org.eclipse.jpt.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.utility.internal.model.value.SimpleListValueModel;
import org.eclipse.jpt.utility.model.value.WritablePropertyValueModel;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.GenericGenerator;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.GenericGeneratorHolder;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaGenericGeneratorImpl;

/**
 * @author Dmitry Geraskov
 *
 */
public class GenericGeneratorComposite extends GeneratorComposite<GenericGenerator> {

	public GenericGeneratorComposite(Pane<? extends GeneratorHolder> parentPane, Composite parent) {
		super(parentPane, parent);
	}	

	@Override
	protected GenericGenerator buildGenerator(GeneratorHolder subject) {
		return ((GenericGeneratorHolder)subject).addGenericGenerator();
	}
	
	protected GenericGenerator buildGenerator() {
		return this.buildGenerator(this.getSubject());
	}

	@Override
	protected GenericGenerator getGenerator(GeneratorHolder subject) {
		return ((GenericGeneratorHolder)subject).getGenericGenerator();
	}

	@Override
	protected String getPropertyName() {
		return GenericGeneratorHolder.GENERIC_GENERATOR_PROPERTY;
	}

	@Override
	protected void initializeLayout(Composite container) {

		// Name widgets
		addLabeledText(
			container,
			HibernateUIMappingMessages.GenericGeneratorComposite_name,
			buildGeneratorNameHolder(),
			null//TODO add help
		);
		
		addLabeledEditableCombo(
			container,
			HibernateUIMappingMessages.GenericGeneratorComposite_strategy,
			new SimpleListValueModel<String>(JavaGenericGeneratorImpl.generatorClasses),
			buildStrategyHolder(),
			StringConverter.Default.<String>instance(),
			null);//TODO add help
		
		new ParametersComposite(this, container);
	}
	
	protected WritablePropertyValueModel<String> buildStrategyHolder() {
		return new PropertyAspectAdapter<GeneratorHolder, String>(getSubjectHolder(),
				GenericGenerator.GENERIC_STRATEGY_PROPERTY) {
			@Override
			protected String buildValue_() {
				return ((GenericGeneratorHolder)subject).getGenericGenerator() == null ?
						null : ((GenericGeneratorHolder)subject).getGenericGenerator().getStrategy();
			}

			@Override
			protected void setValue_(String value) {
				if ("".equals(value))value = null; //$NON-NLS-1$
				GenericGenerator generator = ((GenericGeneratorHolder)subject).getGenericGenerator();
				if (generator == null){
					generator = ((GenericGeneratorHolder)subject).addGenericGenerator();
				}
				generator.setStrategy(value);
			}
		};
	}	

}
