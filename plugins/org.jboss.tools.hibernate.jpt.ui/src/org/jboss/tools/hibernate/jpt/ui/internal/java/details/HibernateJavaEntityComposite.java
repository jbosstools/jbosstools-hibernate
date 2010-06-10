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
package org.jboss.tools.hibernate.jpt.ui.internal.java.details;

import org.eclipse.jpt.core.context.Entity;
import org.eclipse.jpt.core.context.GeneratorContainer;
import org.eclipse.jpt.core.context.QueryContainer;
import org.eclipse.jpt.ui.WidgetFactory;
import org.eclipse.jpt.ui.internal.details.AbstractEntityComposite;
import org.eclipse.jpt.ui.internal.details.EntityNameComposite;
import org.eclipse.jpt.ui.internal.details.IdClassComposite;
import org.eclipse.jpt.ui.internal.details.java.JavaInheritanceComposite;
import org.eclipse.jpt.ui.internal.details.java.JavaSecondaryTablesComposite;
import org.eclipse.jpt.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.utility.model.value.PropertyValueModel;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateGeneratorContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateIdMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntity;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaQueryContainer;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateGenerationComposite2;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateQueriesComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateTableComposite;

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
	protected void initializeQueriesSection(Composite container, PropertyValueModel<QueryContainer> queryContainerHolder) {
		new HibernateQueriesComposite(this, (PropertyValueModel<? extends HibernateJavaQueryContainer>) queryContainerHolder, container);
	}
	
	private PropertyValueModel<HibernateGeneratorContainer> buildGeneratorContainer() {
		return new PropertyAspectAdapter<HibernateJavaEntity, HibernateGeneratorContainer>(getSubjectHolder()) {
			@Override
			protected HibernateGeneratorContainer buildValue_() {
				return this.subject.getGeneratorContainer();
			}
		};
	}
	
	@Override
	protected void initializeGeneratorsSection(Composite container, PropertyValueModel<GeneratorContainer> generatorContainerHolder) {
		new HibernateGenerationComposite2(this, (PropertyValueModel<? extends HibernateGeneratorContainer>) generatorContainerHolder, addSubPane(container, 10), true);
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
		new JavaInheritanceComposite(this, container);
	}

}
