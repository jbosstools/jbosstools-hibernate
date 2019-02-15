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
package org.hibernate.eclipse.criteriaeditor;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.hibernate.eclipse.console.QueryEditor;
import org.hibernate.eclipse.console.actions.ExecuteQueryAction;


/**
 * This class installs and manages actions for the Criteria Editor. 
 */
public class CriteriaEditorActionContributor extends TextEditorActionContributor {

	private ExecuteQueryAction executeQueryAction;
	
    public CriteriaEditorActionContributor() {
        super();
        executeQueryAction = new ExecuteQueryAction();    
    }

    /**
     * Contributes items to the Workbench Edit menu.
     * 
     * @param mm the MenuManager to use
     */
    public void contributeToMenu( IMenuManager mm ) {
    	super.contributeToMenu(mm);
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

        if(targetEditor instanceof QueryEditor) {
        	executeQueryAction.setHibernateQueryEditor((QueryEditor) targetEditor);
        }            
                
    }
    
    public void init(IActionBars bars, IWorkbenchPage page) {
    	super.init( bars, page );
    	
    	bars.setGlobalActionHandler("org.hibernate.eclipse.console.actions.ExecuteQueryAction", executeQueryAction); //$NON-NLS-1$
        bars.updateActionBars();
    }
    
    
} 