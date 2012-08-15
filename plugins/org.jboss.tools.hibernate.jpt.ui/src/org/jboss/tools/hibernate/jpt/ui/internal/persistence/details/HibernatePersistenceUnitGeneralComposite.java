/*******************************************************************************
  * Copyright (c) 2012 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.persistence.details;

import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.jpa.ui.internal.persistence.details.GenericPersistenceUnitGeneralComposite;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * @author Dmitry Geraskov (geraskov@gmail.com)
 *
 */
public class HibernatePersistenceUnitGeneralComposite extends
		GenericPersistenceUnitGeneralComposite {

	public HibernatePersistenceUnitGeneralComposite(
			PropertyValueModel<PersistenceUnit> subjectHolder,
			Composite container, WidgetFactory widgetFactory) {
		super(subjectHolder, container, widgetFactory);
	}
	
	protected void initializeMappedClassesPane(Composite container) {

		container = addCollapsibleSection(
			container,
			Messages.HibernatePersistenceUnitGeneralComposite_Section_title
		);

		updateGridData(container);
		updateGridData(container.getParent());

		new HibernatePersistenceUnitClassesComposite(this, container);
	}

}
