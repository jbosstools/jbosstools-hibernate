/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jpt.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.ui.WidgetFactory;
import org.eclipse.jpt.ui.details.JpaPageComposite;
import org.eclipse.jpt.ui.internal.GenericJpaUiFactory;
import org.eclipse.jpt.ui.internal.persistence.details.PersistenceUnitConnectionComposite;
import org.eclipse.jpt.ui.internal.persistence.details.PersistenceUnitPropertiesComposite;
import org.eclipse.jpt.utility.internal.model.value.TransformationPropertyValueModel;
import org.eclipse.jpt.utility.model.value.PropertyValueModel;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.BasicHibernateProperties;
import org.jboss.tools.hibernate.jpt.ui.internal.persistence.details.HibernatePropertiesComposite;
import org.jboss.tools.hibernate.jpt.ui.xpl.PersistenceUnitGeneralComposite;

/**
 * @author Dmitry Geraskov
 * 
 */
@SuppressWarnings("restriction")
public class HibernateJpaUiFactory extends GenericJpaUiFactory {

	@SuppressWarnings("unchecked")
	public ListIterator createPersistenceUnitComposites(
			PropertyValueModel<PersistenceUnit> subjectHolder, Composite parent, WidgetFactory widgetFactory) {

		List<JpaPageComposite> pages = new ArrayList<JpaPageComposite>(1);

		//replaced from Dali 2.0
		pages.add(new PersistenceUnitGeneralComposite(subjectHolder, parent, widgetFactory));
		pages.add(new PersistenceUnitConnectionComposite(subjectHolder, parent, widgetFactory));		
		pages.add(new PersistenceUnitPropertiesComposite(subjectHolder, parent, widgetFactory));
		
		// ************Hibernate pages***************
		PropertyValueModel<HibernatePersistenceUnit> hibernatePersistenceUnitHolder = this
			.buildHibernatePersistenceUnitHolder(subjectHolder);

		PropertyValueModel<BasicHibernateProperties> basicHolder = this.buildBasicHolder(hibernatePersistenceUnitHolder);
		pages.add(new HibernatePropertiesComposite(basicHolder, parent, widgetFactory));

		return pages.listIterator();
	}

	private PropertyValueModel<BasicHibernateProperties> buildBasicHolder(
			PropertyValueModel<HibernatePersistenceUnit> subjectHolder) {
		return new TransformationPropertyValueModel<HibernatePersistenceUnit, BasicHibernateProperties>(subjectHolder) {
			@Override
			protected BasicHibernateProperties transform_(HibernatePersistenceUnit value) {
				return value.getBasicProperties();
			}
		};
	}

	private PropertyValueModel<HibernatePersistenceUnit> buildHibernatePersistenceUnitHolder(
			PropertyValueModel<PersistenceUnit> subjectHolder) {
		return new TransformationPropertyValueModel<PersistenceUnit, HibernatePersistenceUnit>(subjectHolder) {
			@Override
			protected HibernatePersistenceUnit transform_(PersistenceUnit value) {
				return (HibernatePersistenceUnit) value;
			}
		};
	}

}