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

import org.eclipse.swt.widgets.Composite;

public class CachedClassTreeViewer extends CommonTreeViewer {

	public CachedClassTreeViewer(Composite parent) {
		super(parent);
		setContentProvider(new CachedClassTreeViewerContentProvider());
		setLabelProvider(new CachedClassTreeViewerLabelProvider());
	}

	protected 	class CachedClassTreeViewerContentProvider extends CommonTreeViewer.CachedCommonTreeContentProvider
	{}

	protected 	class CachedClassTreeViewerLabelProvider extends CommonTreeViewer.CachedCommonTreeLableProvider
	{}

}
