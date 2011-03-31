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
package org.jboss.tools.hibernate.jpt.ui.internal.persistence.details.jpa2;

import java.util.ArrayList;
import java.util.ListIterator;

import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.utility.internal.model.value.TransformationPropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.jpa.core.jpa2.context.persistence.PersistenceUnit2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.persistence.connection.JpaConnection2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.persistence.options.JpaOptions2_0;
import org.eclipse.jpt.jpa.ui.details.JpaPageComposite;
import org.eclipse.jpt.jpa.ui.internal.jpa2.persistence.connection.GenericPersistenceUnit2_0ConnectionTab;
import org.eclipse.jpt.jpa.ui.internal.jpa2.persistence.options.GenericPersistenceUnit2_0OptionsTab;
import org.eclipse.jpt.jpa.ui.internal.persistence.details.GenericPersistenceUnitGeneralComposite;
import org.eclipse.jpt.jpa.ui.internal.persistence.details.PersistenceUnitPropertiesComposite;
import org.eclipse.jpt.jpa.ui.internal.persistence.details.PersistenceXmlUiFactory;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.BasicHibernateProperties;
import org.jboss.tools.hibernate.jpt.ui.internal.persistence.details.HibernatePropertiesComposite;

/**
 * @author Dmitry Geraskov
 * 
 */
public class HibernatePersistenceXml2_0UiFactory implements
		PersistenceXmlUiFactory {

	// **************** persistence unit composites ****************************
	public ListIterator<JpaPageComposite> createPersistenceUnitComposites(
			PropertyValueModel<PersistenceUnit> subjectHolder,
			Composite parent, WidgetFactory widgetFactory) {

		ArrayList<JpaPageComposite> pages = new ArrayList<JpaPageComposite>(4);

		PropertyValueModel<JpaConnection2_0> connection2_0Holder = this
				.buildJpaConnection2_0Holder(subjectHolder);
		PropertyValueModel<JpaOptions2_0> options2_0Holder = this
				.buildJpaOptions2_0Holder(subjectHolder);

		pages.add(new GenericPersistenceUnitGeneralComposite(subjectHolder,
				parent, widgetFactory));
		pages.add(new GenericPersistenceUnit2_0ConnectionTab(
				connection2_0Holder, parent, widgetFactory));
		pages.add(new GenericPersistenceUnit2_0OptionsTab(options2_0Holder,
				parent, widgetFactory));
		pages.add(new PersistenceUnitPropertiesComposite(subjectHolder, parent,
				widgetFactory));

		// ************Hibernate pages***************
		PropertyValueModel<BasicHibernateProperties> basicHolder = this
				.buildBasicHolder(subjectHolder);
		pages.add(new HibernatePropertiesComposite(basicHolder, parent,
				widgetFactory));

		return pages.listIterator();
	}

	// ********** private methods **********

	private PropertyValueModel<JpaConnection2_0> buildJpaConnection2_0Holder(
			PropertyValueModel<PersistenceUnit> subjectHolder) {
		return new TransformationPropertyValueModel<PersistenceUnit, JpaConnection2_0>(
				subjectHolder) {
			@Override
			protected JpaConnection2_0 transform_(PersistenceUnit value) {
				return (JpaConnection2_0) ((PersistenceUnit2_0) value)
						.getConnection();
			}
		};
	}

	private PropertyValueModel<JpaOptions2_0> buildJpaOptions2_0Holder(
			PropertyValueModel<PersistenceUnit> subjectHolder) {
		return new TransformationPropertyValueModel<PersistenceUnit, JpaOptions2_0>(
				subjectHolder) {
			@Override
			protected JpaOptions2_0 transform_(PersistenceUnit value) {
				return (JpaOptions2_0) ((PersistenceUnit2_0) value)
						.getOptions();
			}
		};
	}

	private PropertyValueModel<BasicHibernateProperties> buildBasicHolder(
			PropertyValueModel<PersistenceUnit> subjectHolder) {
		return new TransformationPropertyValueModel<PersistenceUnit, BasicHibernateProperties>(
				subjectHolder) {
			@Override
			protected BasicHibernateProperties transform_(PersistenceUnit value) {
				return ((HibernatePersistenceUnit) value)
						.getHibernatePersistenceUnitProperties();
			}
		};
	}

}
