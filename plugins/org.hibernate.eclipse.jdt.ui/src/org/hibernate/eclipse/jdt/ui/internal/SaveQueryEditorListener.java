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

import org.eclipse.jdt.internal.ui.dialogs.OptionalMessageDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.hibernate.eclipse.console.AbstractQueryEditor;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.jdt.ui.Activator;

/**
 * @author Dmitry Geraskov
 *
 */
public class SaveQueryEditorListener implements IPropertyListener {
	
	public static final String id = "AbstractQueryEditor.ReplaceString";
	
	public static final int HQLEditor = 0;
	
	public static final int CriteriaEditor = 1;
	
	private ITextEditor fromEditorPart;
	private AbstractQueryEditor editor;	
	private String query;
	private Point position;
	
	public SaveQueryEditorListener(final ITextEditor fromEditorPart, String consoleName, 
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
		editor.showConnected(fromEditorPart);
		final IWorkbenchPart fromWorkbenchPart = fromEditorPart.getEditorSite().getPart();
		IPartListener pl = new IPartListener(){

			public void partActivated(IWorkbenchPart part) {}

			public void partBroughtToTop(IWorkbenchPart part) {}

			public void partClosed(IWorkbenchPart part) {
				if (fromWorkbenchPart == part){
					fromEditorPart.getEditorSite().getPage().removePartListener(this);
					editor.removePropertyListener(SaveQueryEditorListener.this);
					editor.showDisconnected();	
				}							
			}

			public void partDeactivated(IWorkbenchPart part) {}

			public void partOpened(IWorkbenchPart part) {}
			
		};
		fromEditorPart.getEditorSite().getPage().addPartListener(pl);
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPropertyListener#propertyChanged(java.lang.Object, int)
	 */
	public void propertyChanged(Object source, int propId) {
		
		String editorTitle = fromEditorPart.getTitle();		
		
		if (IEditorPart.PROP_DIRTY == propId && !editor.isDirty()){
			String newQuery = editor.getQueryString();
			String question = NLS.bind(JdtUIMessages.SaveQueryEditorListener_replaceQuestion, new String[]{query, editorTitle, newQuery});

			int ans = OptionalMessageDialog.open(id, null, JdtUIMessages.SaveQueryEditorListener_replaceTitle, null, question, 
							MessageDialog.QUESTION, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 0);
			if (OptionalMessageDialog.NOT_SHOWN != ans){
				//write previous answer
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				store.setValue(id, ans == Window.OK);
			} else {
				//read previous answer
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				if (!store.getBoolean(id)) return;					
			}
			if (Window.CANCEL == ans){
				return;
			}
				
			IDocumentProvider docProvider = fromEditorPart.getDocumentProvider();

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
				String confirm_changed = NLS.bind(JdtUIMessages.SaveQueryEditorListener_replaceQuestion_confirm, query, editorTitle);
				MessageDialog.openConfirm( null, JdtUIMessages.SaveQueryEditorListener_replaceTitle_confirm, confirm_changed);
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
