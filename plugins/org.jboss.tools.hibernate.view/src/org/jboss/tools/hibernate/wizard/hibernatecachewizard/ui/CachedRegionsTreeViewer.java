/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.wizard.hibernatecachewizard.ui;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.wizard.hibernatecachewizard.datamodel.*;


public class CachedRegionsTreeViewer extends CommonTreeViewer 
{
	public CachedRegionsTreeViewer(Composite parent)
	{
		super(parent);
		getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		setContentProvider(new CachedRegionsTreeViewerContentProvider());
		setLabelProvider(new CachedRegionsTreeViewerLabelProvider());
	}
		protected 	class CachedRegionsTreeViewerContentProvider extends CommonTreeViewer.CachedCommonTreeContentProvider
		{}
		protected 	class CachedRegionsTreeViewerLabelProvider extends CommonTreeViewer.CachedCommonTreeLableProvider
		{}
}
