package org.hibernate.eclipse.console.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class ClearHQLEditorAction extends EditorActionDelegate {
		
	/**
	 * Returns the document currently displayed in the editor, or <code>null</code> if none can be 
	 * obtained.
	 * 
	 * @return the current document or <code>null</code>
	 */
	private IDocument getDocument() {
		
		ITextEditor editor= getTextEditor();
		if (editor != null) {
			
			IDocumentProvider provider= editor.getDocumentProvider();
			IEditorInput input= editor.getEditorInput();
			if (provider != null && input != null)
				return provider.getDocument(input);
			
		}
		return null;
	}

	private ITextEditor getTextEditor() {
		if(editor instanceof ITextEditor) {
			return (ITextEditor) editor;
		}
		return null;
	}

	public void run(IAction action) {
		IDocument document = getDocument();
		if(document!=null) {
			document.set("");
		}
		
	}

	
}
