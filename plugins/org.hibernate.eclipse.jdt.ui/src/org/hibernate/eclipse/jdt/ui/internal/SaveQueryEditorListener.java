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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.jdt.internal.ui.refactoring.actions.RefactoringStarter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.hibernate.eclipse.console.AbstractQueryEditor;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

/**
 * @author Dmitry Geraskov
 *
 */
public class SaveQueryEditorListener implements IPropertyListener {
	
	public static final String id = "AbstractQueryEditor.ReplaceString";	//$NON-NLS-1$
	
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
			
			String newQuery = editor.getQueryString();
			
			final DocumentChange change = new DocumentChange(JdtUIMessages.SaveQueryEditorListener_Change_Name, doc);
			TextEdit replaceEdit = new ReplaceEdit(position.x, position.y, newQuery);
			change.setEdit(replaceEdit);
			
			Refactoring ref = new Refactoring(){

				@Override
				public RefactoringStatus checkFinalConditions(
						IProgressMonitor pm) throws CoreException,
						OperationCanceledException {
					return RefactoringStatus.create(Status.OK_STATUS);
				}

				@Override
				public RefactoringStatus checkInitialConditions(
						IProgressMonitor pm) throws CoreException,
						OperationCanceledException {
					return RefactoringStatus.create(Status.OK_STATUS);
				}

				@Override
				public Change createChange(IProgressMonitor pm)
						throws CoreException, OperationCanceledException {
					CompositeChange cc = new CompositeChange(JdtUIMessages.SaveQueryEditorListener_Composite_Change_Name);
					cc.add(change);
					return cc;
				}

				@Override
				public String getName() {
					return JdtUIMessages.SaveQueryEditorListener_Composite_Change_Name; 
				}				
			};
			
			RefactoringWizard wizard = new RefactoringWizard(ref, RefactoringWizard.WIZARD_BASED_USER_INTERFACE){
				@Override
				protected void addUserInputPages() {}
			};
				
			IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			try {
				if ( new RefactoringStarter().activate(ref, wizard, win.getShell(), "", RefactoringSaveHelper.SAVE_ALL)){ //$NON-NLS-1$
					query = newQuery;
					position.y = query.length();
					fromEditorPart.doSave(null);
				}
			} catch (JavaModelException e) {
				HibernateConsolePlugin.getDefault().log(e);
			}
		}			
	}

}
