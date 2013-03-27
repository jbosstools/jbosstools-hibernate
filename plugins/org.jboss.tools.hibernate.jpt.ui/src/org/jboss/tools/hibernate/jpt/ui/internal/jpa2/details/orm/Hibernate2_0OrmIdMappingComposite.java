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
package org.jboss.tools.hibernate.jpt.ui.internal.jpa2.details.orm;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.context.SpecifiedAccessReference;
import org.eclipse.jpt.jpa.core.context.orm.OrmIdMapping;
import org.eclipse.jpt.jpa.ui.details.JpaComposite;
import org.eclipse.jpt.jpa.ui.internal.details.AbstractIdMappingComposite;
import org.eclipse.jpt.jpa.ui.internal.details.AccessTypeComboViewer;
import org.eclipse.jpt.jpa.ui.internal.details.orm.OrmMappingNameText;
import org.eclipse.jpt.jpa.ui.internal.jpa2.details.IdMapping2_0MappedByRelationshipPane;
import org.eclipse.jpt.jpa.ui.internal.jpa2.details.IdMappingGeneration2_0Composite;
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
public class Hibernate2_0OrmIdMappingComposite extends AbstractIdMappingComposite<HibernateOrmIdMapping>
implements JpaComposite{
	/**
	 * Creates a new <code>HibernateIdMappingComposite</code>.
	 *
	 * @param subjectHolder The holder of the subject <code>IdMapping</code>
	 * @param parent The parent container
	 * @param widgetFactory The factory used to create various common widgets
	 */
	public Hibernate2_0OrmIdMappingComposite(PropertyValueModel<? extends HibernateOrmIdMapping> subjectHolder,
							  PropertyValueModel<Boolean> enabledModel,
	                          Composite parent,
	                          WidgetFactory widgetFactory,
	                          ResourceManager resourceManager) {

		super(subjectHolder, enabledModel, parent, widgetFactory, resourceManager);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Control initializeIdSection(Composite container) {
		
//		new IdMapping2_0MappedByRelationshipPane(this, getSubjectHolder(), container);
//		new HibernateColumnComposite(this, (PropertyValueModel<? extends HibernateColumn>) buildColumnHolder(), container);
//		new OrmMappingNameChooser(this, getSubjectHolder(), container);
//		new AccessTypeComposite(this, buildAccessHolderHolder(), container);

		container = this.addSubPane(container, 2, 0, 0, 0, 0);

		IdMapping2_0MappedByRelationshipPane mappedByRelationshipPane = new IdMapping2_0MappedByRelationshipPane(this, getSubjectHolder(), container);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		mappedByRelationshipPane.getControl().setLayoutData(gridData);

		// Column widgets
		HibernateColumnComposite columnComposite = new HibernateColumnComposite(this, (PropertyValueModel<? extends HibernateColumn>)buildColumnModel(), container);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		columnComposite.getControl().setLayoutData(gridData);

		// Name widgets
//		this.addLabel(container, JptUiDetailsOrmMessages.OrmMappingNameChooser_name);
		new OrmMappingNameText(this, getSubjectHolder(), container);

		// Access type widgets
//		this.addLabel(container, JptUiMessages.AccessTypeComposite_access);
		new AccessTypeComboViewer(this, this.buildAccessHolderHolder(), container);

		return container;
	}
	
	@Override
	protected void initializeGenerationCollapsibleSection(Composite container) {
		new IdMappingGeneration2_0Composite(this, container);
	}
	
	protected PropertyValueModel<SpecifiedAccessReference> buildAccessHolderHolder() {
		return new PropertyAspectAdapter<OrmIdMapping, SpecifiedAccessReference>(getSubjectHolder()) {
			@Override
			protected SpecifiedAccessReference buildValue_() {
				return this.subject.getPersistentAttribute();
			}
		};
	}

}
