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
import org.eclipse.jpt.jpa.ui.internal.persistence.GenericPersistenceUnitGeneralTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;

/**
 * 
 * @author Dmitry Geraskov (geraskov@gmail.com)
 *
 */
public class HibernatePersistenceUnitGeneralComposite extends
		GenericPersistenceUnitGeneralTab {
	
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

	protected void updateGridData(Composite container, Spinner spinner) {

		// It is possible the spinner's parent is not the container of the
		// label, spinner and right control (a pane is sometimes required for
		// painting the spinner's border)
		Composite paneContainer = spinner.getParent();

		while (container != paneContainer.getParent()) {
			paneContainer = paneContainer.getParent();
		}

		Control[] controls = paneContainer.getChildren();

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = false;
		gridData.horizontalAlignment       = GridData.BEGINNING;
		controls[1].setLayoutData(gridData);

		controls[2].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		removeAlignRight(controls[2]);
	}
	
	protected void updateGridData(Composite container) {

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace   = true;
		gridData.horizontalAlignment       = SWT.FILL;
		gridData.verticalAlignment         = SWT.FILL;
		container.setLayoutData(gridData);
	}
	
}
