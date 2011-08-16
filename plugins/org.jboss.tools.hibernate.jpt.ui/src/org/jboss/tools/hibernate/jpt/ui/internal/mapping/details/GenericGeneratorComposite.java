/*******************************************************************************
  * Copyright (c) 2010 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.mapping.details;

import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.internal.StringConverter;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.SimpleListValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.WritablePropertyValueModel;
import org.eclipse.jpt.jpa.ui.internal.details.GeneratorComposite;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenericGenerator;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateGeneratorContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaGenericGeneratorImpl;

/**
 * @author Dmitry Geraskov
 *
 */
public class GenericGeneratorComposite extends GeneratorComposite<GenericGenerator> {
	
	public GenericGeneratorComposite(Pane<?> parentPane,
			PropertyValueModel<GenericGenerator> subjectHolder,
			Composite parent,
			GeneratorBuilder<GenericGenerator> builder) {
		super(parentPane, subjectHolder, parent, builder);
	}
	
	@Override
	protected String getPropertyName() {
		return HibernateGeneratorContainer.GENERIC_GENERATORS_LIST;
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
			
		new ParametersComposite(this, container, getSubjectHolder());
	}
	
	protected WritablePropertyValueModel<String> buildStrategyHolder() {
		return new PropertyAspectAdapter<GenericGenerator, String>(getSubjectHolder(),
				GenericGenerator.GENERIC_STRATEGY_PROPERTY) {
			@Override
			protected String buildValue_() {
				return subject == null ? null : subject.getStrategy();
			}
			
			@Override
			public void setValue(String value) {
				retrieveGenerator().setStrategy(value);
			}
		};
	}
	
	@Override
	protected void addAllocationSizeCombo(Composite container) {}
	
	@Override
	protected void addInitialValueCombo(Composite container) {}

}
