/*******************************************************************************
 * Copyright (c) 2009-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.details.java;

import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.context.GeneratorContainer;
import org.eclipse.jpt.jpa.core.context.QueryContainer;
import org.eclipse.jpt.jpa.ui.internal.details.AbstractEntityComposite;
import org.eclipse.jpt.jpa.ui.internal.details.EntityNameComposite;
import org.eclipse.jpt.jpa.ui.internal.details.IdClassComposite;
import org.eclipse.jpt.jpa.ui.internal.details.java.JavaSecondaryTablesComposite;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateGeneratorContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntity;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaQueryContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaTypeDefContainer;
import org.jboss.tools.hibernate.jpt.ui.internal.details.HibernateTableComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateGenerationComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateQueriesComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.TypeDefsComposite;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaEntityComposite extends AbstractEntityComposite<HibernateJavaEntity> {

	/**
	 * @param subjectHolder
	 * @param parent
	 * @param widgetFactory
	 */
	public HibernateJavaEntityComposite(PropertyValueModel<? extends HibernateJavaEntity> subjectHolder,
			Composite parent, WidgetFactory widgetFactory) {
		super(subjectHolder, parent, widgetFactory);
	}
	
	@Override
	protected void initializeLayout(Composite container) {
		super.initializeLayout(container);
		this.initializeTypeDefCollapsibleSection(container);
	}
	
	protected void initializeTypeDefCollapsibleSection(Composite container) {
		container = addCollapsibleSection(
				container,
				"Type Definitions");
		this.initializeTypeDefsSection(container, buildTypeDefContainerHolder());
	}
	
	protected void initializeTypeDefsSection(
			Composite container,
			PropertyValueModel<HibernateJavaTypeDefContainer> typeDefContainerHolder) {
		new TypeDefsComposite(this, typeDefContainerHolder, container);
	}

	private PropertyValueModel<HibernateJavaTypeDefContainer> buildTypeDefContainerHolder() {
		return new PropertyAspectAdapter<HibernateJavaEntity, HibernateJavaTypeDefContainer>(getSubjectHolder()) {
			@Override
			protected HibernateJavaTypeDefContainer buildValue_() {
				return this.subject.getTypeDefContainer();
			}
		};
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initializeQueriesSection(Composite container, PropertyValueModel<QueryContainer> queryContainerHolder) {
		new HibernateQueriesComposite(this, (PropertyValueModel<? extends HibernateJavaQueryContainer>) queryContainerHolder, container);
	}
	
	@SuppressWarnings("unused")
	private PropertyValueModel<HibernateGeneratorContainer> buildGeneratorContainer() {
		return new PropertyAspectAdapter<HibernateJavaEntity, HibernateGeneratorContainer>(getSubjectHolder()) {
			@Override
			protected HibernateGeneratorContainer buildValue_() {
				return this.subject.getGeneratorContainer();
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void initializeGeneratorsSection(Composite container, PropertyValueModel<GeneratorContainer> generatorContainerHolder) {
		new HibernateGenerationComposite(this, (PropertyValueModel<? extends HibernateGeneratorContainer>) generatorContainerHolder, addSubPane(container, 10));
	}
	
	
	protected void initializeEntitySection(Composite container) {
		new HibernateTableComposite(this, container);
		new EntityNameComposite(this, container);
		new IdClassComposite(this, buildIdClassReferenceHolder(), container);
	}
	
	@Override
	protected void initializeSecondaryTablesSection(Composite container) {
		new JavaSecondaryTablesComposite(this, container);
	}

	@Override
	protected void initializeInheritanceSection(Composite container) {
		new HibernateJavaInheritanceComposite(this, container);
	}

}
