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
package org.hibernate.eclipse.console.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.texteditor.ITextEditor;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.AbstractQueryEditor;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.utils.EclipseImages;

/**
 * Actor to pin/unpin query results tab to the editor. 
 *
 * @author Vitali Yemialyanchyk
 */
public class StickResTabAction extends Action {

	private ITextViewer fViewer;
	private ITextEditor fTextEditor;

	public StickResTabAction(ITextViewer viewer) {
		super(HibernateConsoleMessages.StickResTabAction_stick_editor, AS_CHECK_BOX);
	    fViewer = viewer;
		setToolTipText(HibernateConsoleMessages.StickResTabAction_stick_editor);
		AbstractQueryEditor queryEditor = getQueryEditor();
		boolean checked = false;
		if (queryEditor != null) {
			checked = queryEditor.getPinToOneResTab();
		}
		setChecked(checked);
		setImageDescriptor(EclipseImages.getImageDescriptor(
			checked ? ImageConstants.PINDOWN : ImageConstants.PINUP));
	}
	
	public StickResTabAction(ITextEditor textEditor) {
		super(HibernateConsoleMessages.StickResTabAction_stick_editor, AS_CHECK_BOX);
		fTextEditor = textEditor;
		setToolTipText(HibernateConsoleMessages.StickResTabAction_stick_editor);
		AbstractQueryEditor queryEditor = getQueryEditor();
		boolean checked = false;
		if (queryEditor != null) {
			checked = queryEditor.getPinToOneResTab();
		}
		setChecked(checked);
		setImageDescriptor(EclipseImages.getImageDescriptor(
				checked ? ImageConstants.PINDOWN : ImageConstants.PINUP));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		setImageDescriptor(EclipseImages.getImageDescriptor(
			isChecked() ? ImageConstants.PINDOWN : ImageConstants.PINUP));
		AbstractQueryEditor queryEditor = getQueryEditor();
		if (queryEditor != null) {
			queryEditor.setPinToOneResTab(isChecked());
		}
	}
	
	public AbstractQueryEditor getQueryEditor() {
		if (fTextEditor instanceof AbstractQueryEditor) {
			return (AbstractQueryEditor)fTextEditor;
		}
		if (fViewer instanceof AbstractQueryEditor) {
			return (AbstractQueryEditor)fViewer;
		}
		return null;
	}
}