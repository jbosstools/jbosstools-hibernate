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
package org.hibernate.eclipse.hqleditor;

import java.util.ResourceBundle;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.QueryEditor;
import org.hibernate.eclipse.console.actions.ExecuteQueryAction;



/**
 * This class installs and manages actions for the HQL Editor.
 */
public class HQLEditorActionContributor extends TextEditorActionContributor {

    protected RetargetTextEditorAction contentAssistProposalAction;
    protected RetargetTextEditorAction contentAssistTipAction;
    protected RetargetTextEditorAction contentFormatAction;

    private ExecuteQueryAction executeHQLAction;


    /**
     * Constructs an instance of this class.  This is the default constructor.
     */
    public HQLEditorActionContributor() {
        super();
        ResourceBundle bundle = ResourceBundle.getBundle(HibernateConsoleMessages.BUNDLE_NAME);

        contentAssistProposalAction = new RetargetTextEditorAction( bundle, "HQLEditor_ContentAssistProposal_" );//$NON-NLS-1$
        contentAssistTipAction =  new RetargetTextEditorAction( bundle, "HQLEditor_ContentAssistTip_" ); //$NON-NLS-1$
        contentFormatAction = new RetargetTextEditorAction( bundle, "HQLEditor_ContentFormat_" ); //$NON-NLS-1$
        executeHQLAction = new ExecuteQueryAction();

    }

    /**
     * Contributes items to the Workbench Edit menu.
     *
     * @param mm the MenuManager to use
     */
    public void contributeToMenu( IMenuManager mm ) {
        IMenuManager editMenu = mm.findMenuUsingPath( IWorkbenchActionConstants.M_EDIT );
        if (editMenu != null) {
            editMenu.add( new Separator() );
            editMenu.add( contentAssistProposalAction );
            editMenu.add( contentFormatAction );
            editMenu.add( contentAssistTipAction );
        }
        /*IMenuManager hqlMenu = mm.findMenuUsingPath( "HQL" );
        if (hqlMenu != null) {
            hqlMenu.add( new Separator() );
            hqlMenu.add( fContentAssistTipAction );
        }*/
    }

    /**
     * Sets the active editor to this contributor.
     * This updates the actions to reflect the current editor.
     *
     * @see org.eclipse.ui.part.EditorActionBarContributor#setActiveEditor(org.eclipse.ui.IEditorPart)
     * @see EditorActionBarContributor#editorChanged
     */
    public void setActiveEditor( IEditorPart targetEditor ) {
        super.setActiveEditor( targetEditor );

        ITextEditor textEditor = null;
        if (targetEditor instanceof ITextEditor) {
            textEditor = (ITextEditor) targetEditor;
        }

        // Set up the standard text editor actions.  These actions each have an
        // "retargetable action" associated with them.  The action needs to be
        // "retargeted" to associate it with the active editor whenever the active
        // editor changes.
        contentAssistProposalAction.setAction( getAction( textEditor, "ContentAssistProposal" )); //$NON-NLS-1$
        contentAssistTipAction.setAction( getAction( textEditor, "ContentAssistTip" )); //$NON-NLS-1$
        contentFormatAction.setAction( getAction( textEditor, "ContentFormat" )); //$NON-NLS-1$

        if(targetEditor instanceof QueryEditor) {
        	executeHQLAction.setHibernateQueryEditor( (QueryEditor) targetEditor);
        }
    }

    public void contributeToToolBar(IToolBarManager toolBarManager) {

    	super.contributeToToolBar( toolBarManager );


    }

    public void init(IActionBars bars, IWorkbenchPage page) {
    	super.init( bars, page );

//    	bars.setGlobalActionHandler("org.hibernate.eclipse.console.actions.ClearHQLEditorAction", fClearHQLEditorAction);
    	bars.setGlobalActionHandler("org.hibernate.eclipse.console.actions.ExecuteQueryAction", executeHQLAction); //$NON-NLS-1$
    	bars.updateActionBars();
    }


}