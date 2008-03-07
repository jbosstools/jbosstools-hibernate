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
package org.hibernate.eclipse.console.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ISources;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.hibernate.eclipse.console.AbstractQueryEditor;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.actions.OpenMappingAction;

/**
 * @author Dmitry Geraskov
 *
 */
public class OpenHQLHandler extends AbstractHandler {


	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (event.getApplicationContext() instanceof EvaluationContext) {
			EvaluationContext evContext = (EvaluationContext) event.getApplicationContext();
			Object activePart = evContext.getVariable(ISources.ACTIVE_PART_NAME);
			if (activePart instanceof EditorPart) {
				EditorPart editorPart = (EditorPart) activePart;
				if (isAccepted(editorPart.getEditorInput())){
					openHQLEditor(editorPart);
				}				
			}
		}
		return null;
	}
	
	private boolean isAccepted(IEditorInput input){
		if (input instanceof IFile){
			IFile file = (IFile)input;
			return "xml".equals(file.getFileExtension()) 
					|| "java".equals(file.getFileExtension());
		} else if (input instanceof FileEditorInput){
			FileEditorInput fileInput = (FileEditorInput)input;
			return "xml".equals(fileInput.getFile().getFileExtension()) 
			|| "java".equals(fileInput.getFile().getFileExtension());
		}
		return false;
	}
	
	private void openHQLEditor(EditorPart editorPart){
		ITextEditor[] textEditors = OpenMappingAction.getTextEditors(editorPart);
		if (textEditors.length == 0) return;
		ITextEditor fromEditor = null;
		ISelectionProvider selectionProvider = null;
		ITextSelection oldSelection = null;
		for (int i = 0; i < textEditors.length; i++) {
			selectionProvider = textEditors[i].getSelectionProvider();
			if (selectionProvider.getSelection() instanceof ITextSelection) {
				oldSelection = (ITextSelection) selectionProvider.getSelection();
				fromEditor = textEditors[i];
				break;
			}
		}
		if (oldSelection != null && fromEditor != null){
			final ITextEditor fFromEditor = fromEditor;
			final AbstractQueryEditor editor = (AbstractQueryEditor) HibernateConsolePlugin.getDefault().openScratchHQLEditor( null, oldSelection.getText() );
			if (editor != null) {
				IPropertyListener pl = new IPropertyListener(){
					
					private ITextSelection previousSelection = null;
					
					public void propertyChanged(Object source, int propId) {
						if (IEditorPart.PROP_DIRTY == propId && !editor.isDirty()){
							String newString = editor.getQueryString();
							
							//get old string
							ISelection selection = fFromEditor.getSelectionProvider().getSelection();
							if (selection instanceof ITextSelection) {
								ITextSelection tSelection = (ITextSelection) selection;
								if (previousSelection == null) previousSelection = tSelection;
								IDocumentProvider docProvider = fFromEditor.getDocumentProvider();
								if (docProvider == null){
									// editor was disposed
									editor.removePropertyListener(this);
									return;
								}
								if (!tSelection.equals(previousSelection)){
									String title = "Replace selected query";
									String question = "Selection in the editor " + fFromEditor.getTitle() + " was changed."
														+ " Do you really want to replace new selection?";								
									if( !MessageDialog.openConfirm( null, title, question)) return;
								}
								IDocument doc = docProvider.getDocument( fFromEditor.getEditorInput() );
								try {
									// replace old string with new one and change selection
									doc.replace(tSelection.getOffset(), tSelection.getLength(), newString);
									fFromEditor.selectAndReveal(tSelection.getOffset(), newString.length());
									previousSelection = (ITextSelection) fFromEditor.getSelectionProvider().getSelection();
								} catch (BadLocationException e) {
									HibernateConsolePlugin.getDefault().log(e);
								}
							}
						}
					}};
					editor.addPropertyListener(pl);
			}			
		}		
	}
}
