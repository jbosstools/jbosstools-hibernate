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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.PluginAction;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.tool.hbm2x.XMLPrettyPrinter;

public class JTidyFormatAction implements IObjectActionDelegate {

    private IWorkbenchPart targetPart;

    /**
	 * Constructor for Action1.
	 */
	public JTidyFormatAction() {
		super();



	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		IStructuredSelection selection = (IStructuredSelection) ( (PluginAction)action).getSelection();

		String out = NLS.bind(HibernateConsoleMessages.JTidyFormatAction_do_you_want_format_xml_files_with_jtidy, selection.size());
        if(selection!=null && MessageDialog.openQuestion(targetPart.getSite().getShell(),
        		HibernateConsoleMessages.JTidyFormatAction_format_with_jtidy, out) ) {
            Iterator iterator = selection.iterator();
            try {
            while(iterator.hasNext() ) {
                IFile file = (IFile) iterator.next();
                InputStream contents = null ;
                ByteArrayOutputStream bos = null;
                ByteArrayInputStream stream = null;
                try {
                    contents = file.getContents();
                    bos = new ByteArrayOutputStream();
                    XMLPrettyPrinter.prettyPrint(contents, bos);
                    stream = new ByteArrayInputStream(bos.toByteArray() );
                    file.setContents(stream, true, true, null);
                } finally {
                    if(stream!=null) stream.close();
                    if(bos!=null) bos.close();
                    if(contents!=null) contents.close();
                }
            }
            } catch (CoreException e) {
                HibernateConsolePlugin.getDefault().showError(targetPart.getSite().getShell(), HibernateConsoleMessages.JTidyFormatAction_error_while_running_jtidy, e);
            } catch (IOException io) {
                HibernateConsolePlugin.getDefault().showError(targetPart.getSite().getShell(), HibernateConsoleMessages.JTidyFormatAction_error_while_running_jtidy, io);
            }
        }
	}

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        // TODO Auto-generated method stub

    }

}
