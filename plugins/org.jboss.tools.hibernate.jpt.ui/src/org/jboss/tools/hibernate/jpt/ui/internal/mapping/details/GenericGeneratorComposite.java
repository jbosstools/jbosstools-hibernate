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
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.SimpleListValueModel;
import org.eclipse.jpt.common.utility.internal.transformer.StringObjectTransformer;
import org.eclipse.jpt.common.utility.model.value.ModifiablePropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.ui.internal.details.GeneratorComposite;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenericGenerator;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateGeneratorContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaDbGenericGenerator;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaDbGenericGeneratorImpl;
//import org.eclipse.jpt.jpa.ui.internal.details.GeneratorComposite;

/**
 * @author Dmitry Geraskov
 *
 */
public class GenericGeneratorComposite extends GeneratorComposite<JavaDbGenericGenerator> {
	
	public GenericGeneratorComposite(Pane<?> parentPane,
			PropertyValueModel<JavaDbGenericGenerator> subjectHolder,
			Composite parent,
			GeneratorBuilder<JavaDbGenericGenerator> builder) {
		super(parentPane, subjectHolder, parent, builder);
	}
	
	@Override
	protected String getPropertyName() {
		return HibernateGeneratorContainer.GENERIC_GENERATORS_LIST;
	}

	@Override
	protected void initializeLayout(Composite container) {

		// Name widgets
		this.addLabel(container, HibernateUIMappingMessages.GenericGeneratorComposite_name);
		this.addText(container, this.buildGeneratorNameHolder(), null);

		// Generic generator widgets
		this.addLabel(container, HibernateUIMappingMessages.GenericGeneratorComposite_strategy);
		this.addEditableCombo(container,
				new SimpleListValueModel<String>(JavaDbGenericGeneratorImpl.generatorClasses),
				buildStrategyHolder(),
				StringObjectTransformer.<String>instance(),
				(String)null
			);
			
		new ParametersComposite(this, container, getSubjectHolder());
	}
	
	protected ModifiablePropertyValueModel<String> buildStrategyHolder() {
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
