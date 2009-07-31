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

import org.eclipse.jpt.ui.WidgetFactory;
import org.eclipse.jpt.ui.internal.java.details.JavaEntityComposite;
import org.eclipse.jpt.ui.internal.mappings.JptUiMappingsMessages;
import org.eclipse.jpt.ui.internal.widgets.Pane;
import org.eclipse.jpt.utility.model.value.PropertyValueModel;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateQueryContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntity;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateGeneratorsComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateQueriesComposite;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaEntityComposite extends JavaEntityComposite {

	/**
	 * @param subjectHolder
	 * @param parent
	 * @param widgetFactory
	 */
	public HibernateJavaEntityComposite(PropertyValueModel<? extends HibernateJavaEntity> subjectHolder,
			Composite parent, WidgetFactory widgetFactory) {
		super(subjectHolder, parent, widgetFactory);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void initializeQueriesPane(Composite container) {
		container = addCollapsableSection(
			container,
			JptUiMappingsMessages.EntityComposite_queries
		);
		
		new HibernateQueriesComposite((Pane<? extends HibernateQueryContainer>) this, container);
	}
	
	@Override
	protected void initializeGeneratorsPane(Composite container) {
		container = addCollapsableSection(
			container,
			JptUiMappingsMessages.IdMappingComposite_primaryKeyGenerationSection
		);
		
		new HibernateGeneratorsComposite(this, container);
	}
	
	@Override
	protected void addInheritanceComposite(Composite container) {
		new HibernateJavaInheritanceComposite(this, container);
	}

}
