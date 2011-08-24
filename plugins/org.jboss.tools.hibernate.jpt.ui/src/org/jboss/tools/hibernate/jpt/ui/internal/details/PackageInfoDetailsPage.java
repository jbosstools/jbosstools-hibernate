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

import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.ui.internal.util.PaneEnabler;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.WritablePropertyValueModel;
import org.eclipse.jpt.jpa.ui.internal.details.AbstractJpaDetailsPage;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateGeneratorContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaQueryContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaTypeDefContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernatePackageInfo;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.GenericGeneratorsComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateQueriesComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.TypeDefsComposite;

/**
 * @author Dmitry Geraskov
 *
 */
public class PackageInfoDetailsPage extends AbstractJpaDetailsPage<HibernatePackageInfo> {
	
	/**
	 * @param parent
	 * @param widgetFactory
	 */
	public PackageInfoDetailsPage(Composite parent,
			WidgetFactory widgetFactory) {
		super(parent, widgetFactory);
	}

	@Override
	protected void initializeLayout(Composite container) {
		this.initializeQueriesCollapsibleSection(container);
		this.initializeGenericGeneratorsCollapsibleSection(container);
		this.initializeTypeDefCollapsibleSection(container);
		new PaneEnabler(buildWidgetsEnabledHolder(), this);
	}
	
	protected WritablePropertyValueModel<Boolean> buildWidgetsEnabledHolder() {
		return new PropertyAspectAdapter<HibernatePackageInfo, Boolean>(getSubjectHolder()) {
			@Override
			protected Boolean buildValue_() {
				return Boolean.valueOf(this.subject != null);
			}
		};
	}
	
	protected void initializeGenericGeneratorsCollapsibleSection(Composite container) {
		container = addCollapsibleSection(
				container,
				Messages.PackageInfoDetailsPage_GenericGeneratorsSection);
		this.initializeGenericGeneratorsSection(container, buildGeneratorContainerHolder());
	}
	
	private PropertyValueModel<HibernateGeneratorContainer> buildGeneratorContainerHolder() {
		return new PropertyAspectAdapter<HibernatePackageInfo, HibernateGeneratorContainer>(getSubjectHolder()) {
			@Override
			protected HibernateGeneratorContainer buildValue_() {
				return (HibernateGeneratorContainer)this.subject.getGeneratorContainer();
			}
		};
	}
	
	protected void initializeGenericGeneratorsSection(Composite container, PropertyValueModel<HibernateGeneratorContainer> generatorContainerHolder) {
		new GenericGeneratorsComposite(this, generatorContainerHolder, container);
	}
	
	protected void initializeQueriesCollapsibleSection(Composite container) {
		container = addCollapsibleSection(
				container,
				Messages.PackageInfoDetailsPage_QueriesSection);
		this.initializeQueriesSection(container, buildQueryContainerHolder());
	}
	
	private PropertyValueModel<HibernateJavaQueryContainer> buildQueryContainerHolder() {
		return new PropertyAspectAdapter<HibernatePackageInfo, HibernateJavaQueryContainer>(getSubjectHolder()) {
			@Override
			protected HibernateJavaQueryContainer buildValue_() {
				return (HibernateJavaQueryContainer)this.subject.getQueryContainer();
			}
		};
	}
	
	protected void initializeQueriesSection(Composite container, PropertyValueModel<HibernateJavaQueryContainer> queryContainerHolder) {
		new HibernateQueriesComposite(this, queryContainerHolder, container);
	}
	
	protected void initializeTypeDefCollapsibleSection(Composite container) {
		container = addCollapsibleSection(
				container,
				Messages.PackageInfoDetailsPage_TypeDefinitionsSection);
		this.initializeTypeDefsSection(container, buildTypeDefContainerHolder());
	}
	
	protected void initializeTypeDefsSection(
			Composite container,
			PropertyValueModel<HibernateJavaTypeDefContainer> typeDefContainerHolder) {
		new TypeDefsComposite(this, typeDefContainerHolder, container);
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
