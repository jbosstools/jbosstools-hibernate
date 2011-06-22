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
import org.eclipse.jpt.common.utility.internal.model.value.SimplePropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.WritablePropertyValueModel;
import org.eclipse.jpt.jpa.core.context.GeneratorContainer;
import org.eclipse.jpt.jpa.ui.internal.details.GenerationComposite;
import org.eclipse.jpt.jpa.ui.internal.details.GeneratorComposite.GeneratorBuilder;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenericGenerator;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateGeneratorContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaGeneratorContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaGenericGenerator;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateGenerationComposite extends GenerationComposite {
	
	private WritablePropertyValueModel<Boolean> genericGeneratorExpansionStateHolder;
	private WritablePropertyValueModel<GenericGenerator> generatorHolder;
	private boolean allowMany;
	
	public HibernateGenerationComposite(Pane<?> parentPane,
			PropertyValueModel<? extends HibernateGeneratorContainer> subjectHolder,
			Composite parent, boolean allowMany) {
		super(parentPane, subjectHolder, parent);
		this.allowMany = allowMany;
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		this.genericGeneratorExpansionStateHolder = new SimplePropertyValueModel<Boolean>(Boolean.FALSE);
		this.generatorHolder = buildGeneratorHolder();
	}
	
	private WritablePropertyValueModel<GenericGenerator> buildGeneratorHolder() {
		return new SimplePropertyValueModel<GenericGenerator>();
	}
	
	@Override
	public HibernateJavaGeneratorContainer getSubject() {
		return (HibernateJavaGeneratorContainer) super.getSubject();
	}
	
	@Override
	protected void doPopulate() {
		super.doPopulate();
		this.genericGeneratorExpansionStateHolder .setValue(Boolean.valueOf(getSubject() != null
				&& getSubject().genericGeneratorsSize() > 0));
		this.generatorHolder.setValue(getSubject() != null && getSubject().genericGeneratorsSize() > 0 ? getSubject().genericGenerators().next() : null);
	}
	
	@Override
	protected void initializeLayout(Composite container) {
		super.initializeLayout(container);
		this.initializeGenericGeneratorPane(container);
	}
	
	private void initializeGenericGeneratorPane(Composite container) {
		
		// Generic Generator sub-section
		container = this.addCollapsibleSubSection(
			this.addSubPane(container, 10),
			HibernateUIMappingMessages.HibernateGeneratorsComposite_SectionLabel,
			this.genericGeneratorExpansionStateHolder
		);

		// Generic Generator check box
		Button genericGeneratorCheckBox = addCheckBox(
			this.addSubPane(container, 5),
			HibernateUIMappingMessages.HibernateGeneratorsComposite_CheckBoxLabel,
			buildGenericGeneratorBooleanHolder(),
			null
		);
		
		if (allowMany){
			//FIXME add table with generators
		}
		//FIXME for many generators
		if (getSubject().genericGeneratorsSize() > 0){
			generatorHolder.setValue(getSubject().genericGenerators().next());
		}

		// Generic Generator pane
		this.addGenericGeneratorComposite(
			container, 0,
			genericGeneratorCheckBox.getBorderWidth() + 16);
	}
	
	protected void addGenericGeneratorComposite(Composite container, int topMargin, int leftMargin) {
		new GenericGeneratorComposite(
			this,
			this.generatorHolder,
			this.addSubPane(container, topMargin, leftMargin),
			this.buildGenericGeneratorBuilder()
		);
	}
	
	protected GeneratorBuilder<GenericGenerator> buildGenericGeneratorBuilder() {
		return new GeneratorBuilder<GenericGenerator>() {
			public GenericGenerator addGenerator() {
				HibernateJavaGeneratorContainer container = (HibernateJavaGeneratorContainer)getSubject();
				JavaGenericGenerator generator = container.addGenericGenerator(container.genericGeneratorsSize());
				generatorHolder.setValue(generator);
				return generator;
			}
		};
	}
	
	/*private ListValueModel<JavaGenericGenerator> buildGenericGeneratorHolder() {
		return new ListAspectAdapter<HibernateJavaGeneratorContainer, JavaGenericGenerator>(
			(PropertyValueModel)getSubjectHolder(),
			HibernateGeneratorContainer.GENERIC_GENERATORS_LIST)
		{
			@Override
			protected ListIterator<JavaGenericGenerator> listIterator_() {
				return ((HibernateJavaGeneratorContainer)subject).genericGenerators();
			}

			@Override
			protected int size_() {
				return ((HibernateJavaGeneratorContainer)subject).genericGeneratorsSize();
			}
		};
	}*/
	
	private WritablePropertyValueModel<Boolean> buildGenericGeneratorBooleanHolder() {
		return new PropertyAspectAdapter<GeneratorContainer, Boolean>(getSubjectHolder(), HibernateGeneratorContainer.GENERIC_GENERATORS_LIST) {
			@Override
			protected Boolean buildValue_() {
				return Boolean.valueOf(((HibernateJavaGeneratorContainer)subject).genericGeneratorsSize() > 0);
			}

			@Override
			protected void setValue_(Boolean value) {
				HibernateJavaGeneratorContainer container = (HibernateJavaGeneratorContainer)subject;
				if (value.booleanValue()) {
					JavaGenericGenerator gc = container.addGenericGenerator(container.genericGeneratorsSize());
					generatorHolder.setValue(gc);
				} else if (!value.booleanValue()) {
					for (int i = 0; i < container.genericGeneratorsSize(); i++) {
						container.removeGenericGenerator(0);
					}
					generatorHolder.setValue(null);
				}
			}
		};
	}

}
