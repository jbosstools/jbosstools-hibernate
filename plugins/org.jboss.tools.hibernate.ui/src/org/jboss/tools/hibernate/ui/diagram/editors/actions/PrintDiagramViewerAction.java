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

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.print.PrintGraphicalViewerOperation;
import org.eclipse.gef.ui.actions.PrintAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.ui.IWorkbenchPart;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;
import org.jboss.tools.hibernate.ui.diagram.print.PrintDiagramViewerOperation;

/**
 * @author Vitali Yemialyanchyk
 */
public class PrintDiagramViewerAction extends PrintAction {

	public PrintDiagramViewerAction(IWorkbenchPart part) {
		super(part);
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		GraphicalViewer viewer;
		viewer = (GraphicalViewer)getWorkbenchPart().getAdapter(GraphicalViewer.class);
		
		PrintDialog dialog = new PrintDialog(viewer.getControl().getShell(), SWT.NULL);
		PrinterData data = dialog.open();
		
		if (data != null) {
			PrintDiagramViewerOperation op = 
				new PrintDiagramViewerOperation(new Printer(data), viewer);
			DiagramViewer dv = (DiagramViewer)getWorkbenchPart();
			op.setZoom(dv.getZoom());
			if (Math.abs(dv.getZoom() - dv.getFitHeightZoomValue()) < 0.00000001) {
				op.setPrintMode(PrintGraphicalViewerOperation.FIT_HEIGHT);
			} else if (Math.abs(dv.getZoom() - dv.getFitWidthZoomValue()) < 0.00000001) {
				op.setPrintMode(PrintGraphicalViewerOperation.FIT_WIDTH);
			} else if (Math.abs(dv.getZoom() - dv.getFitPageZoomValue()) < 0.00000001) {
				op.setPrintMode(PrintGraphicalViewerOperation.FIT_PAGE);
			} else {
				op.setPrintMode(PrintGraphicalViewerOperation.TILE);
			}
			op.run(getWorkbenchPart().getTitle());
		}
	}
}
