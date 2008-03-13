/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.hibernate.eclipse.jdt.ui.internal;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.hibernate.eclipse.console.AbstractQueryEditor;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

/**
 * @author Dmitry Geraskov
 *
 */
public class SaveQueryEditorListener implements IPropertyListener {
	
	public static final int HQLEditor = 0;
	
	public static final int CriteriaEditor = 1;
	
	private ITextEditor fromEditorPart;
	private AbstractQueryEditor editor;	
	private String query;
	private Point position;
	
	public SaveQueryEditorListener(ITextEditor fromEditorPart, String consoleName, 
			String query, Point position, int editorNum){
		this.fromEditorPart = fromEditorPart;
		this.query = query;
		this.position = position;
		switch (editorNum) {
		case HQLEditor:
			editor = (AbstractQueryEditor) HibernateConsolePlugin.getDefault()
			.openScratchHQLEditor( consoleName, query );
			break;

		default:
			editor = (AbstractQueryEditor) HibernateConsolePlugin.getDefault()
			.openCriteriaEditor( consoleName, query );
		}
		editor.addPropertyListener(this);
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPropertyListener#propertyChanged(java.lang.Object, int)
	 */
	public void propertyChanged(Object source, int propId) {
		if (IEditorPart.PROP_DIRTY == propId && !editor.isDirty()){
			String newQuery = editor.getQueryString();
			
			IDocumentProvider docProvider = fromEditorPart.getDocumentProvider();
			if (docProvider == null){	// editor was disposed							
				editor.removePropertyListener(this);
				return;
			}

			IDocument doc = docProvider.getDocument( fromEditorPart.getEditorInput() );
			boolean isDocChanged = true;
			try {
				if (query.equals(doc.get(position.x, position.y))){
					isDocChanged = false;
				}
			} catch (BadLocationException e1) {
				//document changed and we can get the exception
			}
			
			if (isDocChanged){
				String title = "Document was changed";
				String question = "Document was changed. Can't find string to replace.";
				MessageDialog.openConfirm( null, title, question);
				return;
			}
			
			try {
				// replace old string with new one and change positions
				doc.replace(position.x, position.y, newQuery);
				position.y = newQuery.length();
				query = newQuery;
			} catch (BadLocationException e) {
				HibernateConsolePlugin.getDefault().log(e);
			}
		}
	}

}
