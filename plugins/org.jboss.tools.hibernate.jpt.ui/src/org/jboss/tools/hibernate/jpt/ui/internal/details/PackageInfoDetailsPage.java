/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.details;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.model.value.ModifiablePropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.ui.details.JptJpaUiDetailsMessages;
import org.eclipse.jpt.jpa.ui.internal.details.AbstractJpaDetailsPageManager;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateGenericGeneratorContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaQueryContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaTypeDefContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernatePackageInfo;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.GenericGeneratorsComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateQueriesComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.TypeDefsComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.utils.PaneVisibilityEnabler;

/**
 * @author Dmitry Geraskov
 *
 */
public class PackageInfoDetailsPage extends AbstractJpaDetailsPageManager<HibernatePackageInfo> {
	
	/**
	 * @param parent
	 * @param widgetFactory
	 */
	public PackageInfoDetailsPage(Composite parent,
			WidgetFactory widgetFactory, ResourceManager resourceManager ) {
		super(parent, widgetFactory, resourceManager);
	}

	@Override
	protected void initializeLayout(Composite container) {
		this.initializeQueriesCollapsibleSection(container);
		this.initializeGenericGeneratorsCollapsibleSection(container);
		this.initializeTypeDefCollapsibleSection(container);
		new PaneVisibilityEnabler(buildWidgetsEnabledHolder(), this);
	}
	
	protected ModifiablePropertyValueModel<Boolean> buildWidgetsEnabledHolder() {
		return new PropertyAspectAdapter<HibernatePackageInfo, Boolean>(getSubjectHolder()) {
			@Override
			protected Boolean buildValue_() {
				return Boolean.valueOf(this.subject != null);
			}
		};
	}
	
	
	protected void initializeGenericGeneratorsCollapsibleSection(Composite container) {
		final Section section = this.getWidgetFactory().createSection(container,
				ExpandableComposite.TITLE_BAR |
				ExpandableComposite.TWISTIE |
				ExpandableComposite.EXPANDED);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		section.setText(JptJpaUiDetailsMessages.BasicSection_title);
		section.setClient(this.initializeGenericGeneratorsSection(section));
//		container = addCollapsibleSection(
//				container,
//				Messages.PackageInfoDetailsPage_GenericGeneratorsSection);
//		this.initializeGenericGeneratorsSection(container);
	}
	
	private PropertyValueModel<HibernateGenericGeneratorContainer> buildGeneratorContainerHolder() {
		return new PropertyAspectAdapter<HibernatePackageInfo, HibernateGenericGeneratorContainer>(getSubjectHolder()) {
			@Override
			protected HibernateGenericGeneratorContainer buildValue_() {
				return (HibernateGenericGeneratorContainer)this.subject.getGeneratorContainer();
			}
		};
	}
	
	protected Control initializeGenericGeneratorsSection(Composite container) {
		return new GenericGeneratorsComposite(
				this, 
				buildGeneratorContainerHolder(), 
				container).getControl();
	}
	
	protected void initializeQueriesCollapsibleSection(Composite container) {
		final Section section = this.getWidgetFactory().createSection(container,
				ExpandableComposite.TITLE_BAR |
				ExpandableComposite.TWISTIE |
				ExpandableComposite.EXPANDED);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		section.setText(JptJpaUiDetailsMessages.BasicSection_title);
		section.setClient(this.initializeQueriesSection(section, buildQueryContainerHolder()));
//		container = addCollapsibleSection(
//				container,
//				Messages.PackageInfoDetailsPage_QueriesSection);
//		this.initializeQueriesSection(container, buildQueryContainerHolder());
	}
	
	private PropertyValueModel<HibernateJavaQueryContainer> buildQueryContainerHolder() {
		return new PropertyAspectAdapter<HibernatePackageInfo, HibernateJavaQueryContainer>(getSubjectHolder()) {
			@Override
			protected HibernateJavaQueryContainer buildValue_() {
				return (HibernateJavaQueryContainer)this.subject.getQueryContainer();
			}
		};
	}
	
	protected Control initializeQueriesSection(Composite container, PropertyValueModel<HibernateJavaQueryContainer> queryContainerHolder) {
		return new HibernateQueriesComposite(this, queryContainerHolder, container).getControl();
	}
	
	protected void initializeTypeDefCollapsibleSection(Composite container) {
		final Section section = this.getWidgetFactory().createSection(container,
				ExpandableComposite.TITLE_BAR |
				ExpandableComposite.TWISTIE |
				ExpandableComposite.EXPANDED);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		section.setText(JptJpaUiDetailsMessages.BasicSection_title);
		section.setClient(this.initializeTypeDefsSection(section, buildTypeDefContainerHolder()));
//		container = addCollapsibleSection(
//				container,
//				Messages.PackageInfoDetailsPage_TypeDefinitionsSection);
//		this.initializeTypeDefsSection(container, buildTypeDefContainerHolder());
	}
	
	protected Control initializeTypeDefsSection(
			Composite container,
			PropertyValueModel<HibernateJavaTypeDefContainer> typeDefContainerHolder) {
		return new TypeDefsComposite(this, typeDefContainerHolder, container).getControl();
	}
	
	private PropertyValueModel<HibernateJavaTypeDefContainer> buildTypeDefContainerHolder() {
		return new PropertyAspectAdapter<HibernatePackageInfo, HibernateJavaTypeDefContainer>(getSubjectHolder()) {
			
			@Override
			protected HibernateJavaTypeDefContainer buildValue_() {
				return this.subject.getTypeDefContainer();
			}
		};
	}
}
