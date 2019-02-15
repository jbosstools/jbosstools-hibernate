/*******************************************************************************
 * Copyright (c) 2009-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.details.orm;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.ui.details.JpaComposite;
import org.eclipse.jpt.jpa.ui.details.orm.JptJpaUiDetailsOrmMessages;
import org.eclipse.jpt.jpa.ui.internal.details.AbstractIdMappingComposite;
import org.eclipse.jpt.jpa.ui.internal.details.orm.OrmMappingNameText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateColumn;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmIdMapping;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateColumnComposite;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmIdMappingComposite extends AbstractIdMappingComposite<HibernateOrmIdMapping>
implements JpaComposite{
	/**
	 * Creates a new <code>HibernateIdMappingComposite</code>.
	 *
	 * @param subjectHolder The holder of the subject <code>IdMapping</code>
	 * @param parent The parent container
	 * @param widgetFactory The factory used to create various common widgets
	 */
	public HibernateOrmIdMappingComposite(PropertyValueModel<? extends HibernateOrmIdMapping> subjectHolder,
								PropertyValueModel<Boolean> enabledModel,
	                          Composite parent,
	                          WidgetFactory widgetFactory,
	                          ResourceManager resourceManager) {

		super(subjectHolder, enabledModel, parent, widgetFactory, resourceManager);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Control initializeIdSection(Composite container) {
//		new HibernateColumnComposite(this, (PropertyValueModel<? extends HibernateColumn>) buildColumnHolder(), container);
//		new OrmMappingNameChooser(this, getSubjectHolder(), container);

		container = this.addSubPane(container, 2, 0, 0, 0, 0);

		HibernateColumnComposite columnComposite = new HibernateColumnComposite(this, (PropertyValueModel<? extends HibernateColumn>) buildColumnModel(), container);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		columnComposite.getControl().setLayoutData(gridData);

		// Name widgets
		this.addLabel(container, JptJpaUiDetailsOrmMessages.ORM_MAPPING_NAME_CHOOSER_NAME);
		new OrmMappingNameText(this, getSubjectHolder(), container);

		return container;
	
	}	

}
