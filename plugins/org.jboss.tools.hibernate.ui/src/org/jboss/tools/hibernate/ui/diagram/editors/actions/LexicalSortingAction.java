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

import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramContentOutlinePage;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmDiagram;

/**
 * Lexical sort items.
 * 
 * @author Vitali Yemialyanchyk
 */
public class LexicalSortingAction extends Action {

	public static final String ACTION_ID = "toggleLexicalSortingId"; //$NON-NLS-1$

	private DiagramViewer diagramViewer;
	private DiagramContentOutlinePage outlinePage;
	
	@SuppressWarnings("restriction")
	public LexicalSortingAction(DiagramViewer diagramViewer, DiagramContentOutlinePage outlinePage) {
		super(DiagramViewerMessages.DiagramViewer_OutlinePage_Sort_label, AS_CHECK_BOX);
		setId(ACTION_ID);
		this.diagramViewer = diagramViewer;
		this.outlinePage = outlinePage;
		setText(DiagramViewerMessages.DiagramViewer_OutlinePage_Sort_label);
		org.eclipse.jdt.internal.ui.JavaPluginImages.setLocalImageDescriptors(this, "alphab_sort_co.gif"); //$NON-NLS-1$
		setToolTipText(DiagramViewerMessages.DiagramViewer_OutlinePage_Sort_tooltip);
		setDescription(DiagramViewerMessages.DiagramViewer_OutlinePage_Sort_description);

		boolean checked = getOrmDiagram().isDeepIntoSort();
		setChecked(checked);
		if (checked) {
			valueChanged(checked);
		}
	}

	public boolean isChecked() {
		return getOrmDiagram().isDeepIntoSort();
	}

	public void run() {
		valueChanged(!getOrmDiagram().isDeepIntoSort());
		setChecked(getOrmDiagram().isDeepIntoSort());
	}

	private void valueChanged(final boolean on) {
		BusyIndicator.showWhile(getDisplay(), new Runnable() {
			public void run() {
				final OrmDiagram od = getOrmDiagram();
				od.setDeepIntoSort(on);
				od.refresh();
				if (outlinePage != null) {
					outlinePage.setContents(od);
				}
			}
		});
	}

	protected Display getDisplay () {
		if (diagramViewer != null && diagramViewer.getEditPartViewer() != null && 
				diagramViewer.getEditPartViewer().getControl() != null) {
			return diagramViewer.getEditPartViewer().getControl().getDisplay();
		}
		if (outlinePage != null && outlinePage.getOutline() != null) {
			return outlinePage.getOutline().getDisplay();
		}
		return null;
	}
	
	public void setOutlinePage(DiagramContentOutlinePage outlinePage) {
		this.outlinePage = outlinePage;
	}

	protected OrmDiagram getOrmDiagram() {
		if (diagramViewer != null) {
			return diagramViewer.getOrmDiagram();
		}
		if (outlinePage != null) {
			return outlinePage.getOrmDiagram();
		}
		return null;
	}
}
