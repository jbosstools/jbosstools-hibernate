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
//import org.eclipse.gmf.runtime.common.ui.action.actions.IPrintActionHelper;

/**
 * Initial test implementation for Print preview for Mapping Diagram
 * 
 * @author Vitali Yemialyanchyk
 */
public class PrintPreViewDiagramViewerAction extends AbstractHandler {

	private org.jboss.tools.hibernate.ui.diagram.editors.print.PrintPreviewHelper 
		printPreviewHelper = new org.jboss.tools.hibernate.ui.diagram.editors.print.PrintPreviewHelper();
//	private IPrintActionHelper helper = null; 
		//new org.eclipse.gmf.runtime.diagram.ui.printing.render.actions.EnhancedPrintActionHelper();
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
//		printPreviewHelper.doPrintPreview(helper);
		return null;
	}

}
