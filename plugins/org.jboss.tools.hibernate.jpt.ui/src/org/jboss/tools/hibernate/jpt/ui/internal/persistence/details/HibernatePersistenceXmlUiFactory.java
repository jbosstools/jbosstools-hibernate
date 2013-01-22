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
package org.jboss.tools.hibernate.jpt.ui.internal.persistence.details;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.utility.internal.model.value.TransformationPropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.jpa.ui.editors.JpaPageComposite;
import org.eclipse.jpt.jpa.ui.internal.persistence.PersistenceUnitConnectionTab;
import org.eclipse.jpt.jpa.ui.internal.persistence.PersistenceUnitPropertiesTab;
import org.eclipse.jpt.jpa.ui.internal.persistence.PersistenceXmlUiFactory;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.BasicHibernateProperties;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernatePersistenceXmlUiFactory implements
		PersistenceXmlUiFactory {

	public ListIterator<JpaPageComposite> createPersistenceUnitComposites(
			PropertyValueModel<PersistenceUnit> subjectHolder,
			Composite parent, WidgetFactory widgetFactory) {
		List<JpaPageComposite> pages = new ArrayList<JpaPageComposite>(1);

		pages.add(new HibernatePersistenceUnitGeneralComposite(subjectHolder, parent, widgetFactory));
		pages.add(new PersistenceUnitConnectionTab(subjectHolder, parent, widgetFactory));
		pages.add(new PersistenceUnitPropertiesTab(subjectHolder, parent, widgetFactory));
		
		// ************Hibernate pages***************
		PropertyValueModel<BasicHibernateProperties> basicHolder = this.buildBasicHolder(subjectHolder);
		pages.add(new HibernatePropertiesComposite(basicHolder, parent, widgetFactory));

		return pages.listIterator();
	}
	
	private PropertyValueModel<BasicHibernateProperties> buildBasicHolder(
			PropertyValueModel<PersistenceUnit> subjectHolder) {
		return new TransformationPropertyValueModel<PersistenceUnit, BasicHibernateProperties>(subjectHolder) {
			@Override
			protected BasicHibernateProperties transform_(PersistenceUnit value) {
				return ((HibernatePersistenceUnit)value).getHibernatePersistenceUnitProperties();
			}
		};
	}

}
