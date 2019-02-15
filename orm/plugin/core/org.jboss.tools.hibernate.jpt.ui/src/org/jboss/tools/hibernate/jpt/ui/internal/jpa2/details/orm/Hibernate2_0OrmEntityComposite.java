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
import org.eclipse.jpt.jpa.core.context.orm.OrmEntity;
import org.eclipse.jpt.jpa.core.jpa2.context.Cacheable2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.CacheableReference2_0;
import org.eclipse.jpt.jpa.ui.JptJpaUiMessages;
import org.eclipse.jpt.jpa.ui.details.JptJpaUiDetailsMessages;
import org.eclipse.jpt.jpa.ui.details.orm.JptJpaUiDetailsOrmMessages;
import org.eclipse.jpt.jpa.ui.internal.details.AccessTypeComboViewer;
import org.eclipse.jpt.jpa.ui.internal.details.EntityNameCombo;
import org.eclipse.jpt.jpa.ui.internal.details.IdClassChooser;
import org.eclipse.jpt.jpa.ui.internal.details.orm.AbstractOrmEntityComposite;
import org.eclipse.jpt.jpa.ui.internal.details.orm.MetadataCompleteTriStateCheckBox;
import org.eclipse.jpt.jpa.ui.internal.details.orm.OrmInheritanceComposite;
import org.eclipse.jpt.jpa.ui.internal.details.orm.OrmJavaClassChooser;
import org.eclipse.jpt.jpa.ui.internal.details.orm.OrmSecondaryTablesComposite;
import org.eclipse.jpt.jpa.ui.internal.jpa2.details.EntityOverridesComposite2_0;
import org.eclipse.jpt.jpa.ui.internal.jpa2.details.GenerationComposite2_0;
import org.eclipse.jpt.jpa.ui.internal.jpa2.details.QueriesComposite2_0;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmEntity;
import org.jboss.tools.hibernate.jpt.ui.internal.details.HibernateTableComposite;

/**
 * @author Dmitry Geraskov
 *
 */
public class Hibernate2_0OrmEntityComposite extends AbstractOrmEntityComposite<HibernateOrmEntity> {

	/**
	 * @param subjectHolder
	 * @param parent
	 * @param widgetFactory
	 */
	public Hibernate2_0OrmEntityComposite(PropertyValueModel<? extends HibernateOrmEntity> subjectHolder,
			Composite parent, WidgetFactory widgetFactory, ResourceManager resourceManager) {
		super(subjectHolder, parent, widgetFactory, resourceManager);
	}
	
	@Override
	protected Control initializeEntitySection(Composite container) {

//		new OrmJavaClassChooser(this, getSubjectHolder(), container, false);
//		new HibernateTableComposite(this, container);
//		new EntityNameComposite(this, container);
//		new AccessTypeComposite(this, buildAccessHolder(), container);
//		new IdClassComposite(this, buildIdClassReferenceHolder(), container);
//		new Cacheable2_0Pane(this, buildCacheableHolder(), container);
//		new MetadataCompleteComposite(this, getSubjectHolder(), container);

		container = this.addSubPane(container, 2, 0, 0, 0, 0);
		
		// Java class widgets
		Hyperlink javaClassHyperlink = this.addHyperlink(container, JptJpaUiDetailsOrmMessages.ORM_JAVA_CLASS_CHOOSER_JAVA_CLASS);
		new OrmJavaClassChooser(this, buildPersistentTypeReferenceModel() , container, javaClassHyperlink);

		// Table widgets
		HibernateTableComposite tableComposite = new HibernateTableComposite(this, container);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		tableComposite.getControl().setLayoutData(gridData);

		// Entity name widgets
		this.addLabel(container, JptJpaUiDetailsMessages.ENTITY_NAME_COMPOSITE_NAME);
		new EntityNameCombo(this, container);

		// Access type widgets
		this.addLabel(container, JptJpaUiMessages.ACCESS_TYPE_COMPOSITE_ACCESS);
		new AccessTypeComboViewer(this, buildAccessHolder(), container);

		// Id class widgets
		Hyperlink hyperlink = this.addHyperlink(container,JptJpaUiDetailsMessages.ID_CLASS_COMPOSITE_LABEL);
		new IdClassChooser(this, buildIdClassReferenceModel(), container, hyperlink);

		// Metadata complete widgets
		MetadataCompleteTriStateCheckBox metadataCompleteCheckBox = new MetadataCompleteTriStateCheckBox(this, getSubjectHolder(), container);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		metadataCompleteCheckBox.getControl().setLayoutData(gridData);

		return container;
	}
	
	protected PropertyValueModel<Cacheable2_0> buildCacheableHolder() {
		return new PropertyAspectAdapter<OrmEntity, Cacheable2_0>(getSubjectHolder()) {
			@Override
			protected Cacheable2_0 buildValue_() {
				return ((CacheableReference2_0) this.subject).getCacheable();
			}
		};
	}
	
	@Override
	protected Control initializeAttributeOverridesSection(Composite container) {
		return new EntityOverridesComposite2_0(this, container).getControl();
	}

	@Override
	protected Control initializeGeneratorsSection(Composite container) {
		return new GenerationComposite2_0(this, buildGeneratorContainerModel(), container).getControl();
	}

	@Override
	protected Control initializeQueriesSection(Composite container) {
		return new QueriesComposite2_0(this, buildQueryContainerModel(), container).getControl();
	}
	
	protected PropertyValueModel<SpecifiedAccessReference> buildAccessHolder() {
		return new PropertyAspectAdapter<OrmEntity, SpecifiedAccessReference>(
			getSubjectHolder())
		{
			@Override
			protected SpecifiedAccessReference buildValue_() {
				return this.subject.getPersistentType();
			}
		};
	}
	@Override
	protected Control initializeSecondaryTablesSection(Composite container) {
		return new OrmSecondaryTablesComposite(this, container).getControl();
	}

	@Override
	protected Control initializeInheritanceSection(Composite container) {
		return new OrmInheritanceComposite(this, container).getControl();
	}
}
