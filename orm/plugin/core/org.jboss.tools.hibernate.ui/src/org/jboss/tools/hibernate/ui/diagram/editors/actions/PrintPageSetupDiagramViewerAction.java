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
package org.jboss.tools.hibernate.ui.diagram.editors.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

/**
 * Initial test implementation for Print Page Setup for Mapping Diagram
 *
 * @author Vitali Yemialyanchyk
 */
public class PrintPageSetupDiagramViewerAction extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		new org.jboss.tools.hibernate.ui.diagram.editors.print.PageSetupDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()).open();
		return null;
	}

}
