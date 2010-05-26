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
import org.eclipse.jpt.ui.internal.java.details.JavaSecondaryTablesComposite;
import org.eclipse.jpt.ui.internal.mappings.JptUiMappingsMessages;
import org.eclipse.jpt.ui.internal.mappings.details.AbstractEntityComposite;
import org.eclipse.jpt.ui.internal.mappings.details.EntityNameComposite;
import org.eclipse.jpt.ui.internal.mappings.details.IdClassComposite;
import org.eclipse.jpt.utility.model.value.PropertyValueModel;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntity;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateGeneratorsComposite;
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
	protected void addSecondaryTablesComposite(Composite container) {
		new JavaSecondaryTablesComposite(this, container);
	}
	
	@Override
	protected void initializeQueriesPane(Composite container) {
		container = addCollapsableSection(
			container,
			JptUiMappingsMessages.EntityComposite_queries
		);
		
		new HibernateQueriesComposite(this, container);
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
	
	protected void initializeGeneralPane(Composite container) {
		int groupBoxMargin = getGroupBoxMargin();

		new HibernateTableComposite(this, container);
		new EntityNameComposite(this, addSubPane(container, 0, groupBoxMargin, 0, groupBoxMargin));
		new IdClassComposite(this, addSubPane(container, 0, groupBoxMargin, 0, groupBoxMargin), false);
	}

}
