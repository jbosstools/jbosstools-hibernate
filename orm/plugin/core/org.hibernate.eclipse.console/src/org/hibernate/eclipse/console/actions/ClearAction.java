/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.console.actions;


import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.ITextEditor;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.utils.EclipseImages;

/**
 * Clears the output in a text viewer.
 *
 */
public class ClearAction extends Action {

	private ITextViewer fViewer;
	private ITextEditor fTextEditor;

	/**
	 * Constructs an action to clear the document associated with a text viewer.
	 *
	 * @param viewer viewer whose document this action is associated with
	 */
	public ClearAction(ITextViewer viewer) {
	    fViewer = viewer;

		//setToolTipText(HibernateConsoleMessages.ClearOutputAction_toolTipText); //$NON-NLS-1$
		setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.CLEAR) );
		setToolTipText(HibernateConsoleMessages.ClearAction_clear_editor);
		//setHoverImageDescriptor(ConsolePluginImages.getImageDescriptor(IConsoleConstants.IMG_LCL_CLEAR));
		//setDisabledImageDescriptor(ConsolePluginImages.getImageDescriptor(IInternalConsoleConstants.IMG_DLCL_CLEAR));
		//setImageDescriptor(ConsolePluginImages.getImageDescriptor(IInternalConsoleConstants.IMG_ELCL_CLEAR));
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IConsoleHelpContextIds.CLEAR_CONSOLE_ACTION);
	}
	
	public ClearAction(ITextEditor textEditor) {
		fTextEditor = textEditor;
		setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.CLEAR) );
		setToolTipText(HibernateConsoleMessages.ClearAction_clear_editor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		BusyIndicator.showWhile(getStandardDisplay(), new Runnable() {
			public void run() {
				IDocument document = null;
				if (fViewer != null){
					document = fViewer.getDocument();
				} else {
					Assert.isNotNull(fTextEditor);
					Assert.isNotNull(fTextEditor.getDocumentProvider());
					Assert.isNotNull(fTextEditor.getEditorInput());					
					document = fTextEditor.getDocumentProvider().getDocument(fTextEditor.getEditorInput());
				}
				if (document != null) {
					document.set(""); //$NON-NLS-1$
				}
				if (fViewer != null) fViewer.setSelectedRange(0, 0);
			}
		});
	}

	/**
	 * Returns the standard display to be used. The method first checks, if
	 * the thread calling this method has an associated display. If so, this
	 * display is returned. Otherwise the method returns the default display.
	 */
	private static Display getStandardDisplay() {
		Display display= Display.getCurrent();
		if (display == null) {
			display= Display.getDefault();
		}
		return display;
	}
}