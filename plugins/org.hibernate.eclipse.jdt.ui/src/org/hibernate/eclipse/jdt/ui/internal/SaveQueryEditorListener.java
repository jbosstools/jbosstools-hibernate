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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.refactoring.actions.RefactoringStarter;
import org.eclipse.jdt.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.MultiStateTextFileChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.hibernate.eclipse.console.AbstractQueryEditor;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.hqleditor.HQLEditor;

/**
 * @author Dmitry Geraskov
 *
 */
@SuppressWarnings("restriction")
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

		if (IEditorPart.PROP_DIRTY == propId && !editor.isDirty()){
			IDocumentProvider docProvider = fromEditorPart.getDocumentProvider();

			final IFile file = ((IFileEditorInput) fromEditorPart.getEditorInput()).getFile();
			final IDocument doc = docProvider.getDocument( fromEditorPart.getEditorInput() );
			boolean isDocChanged = true;
			try {
				if (query.equals(doc.get(position.x, position.y))){
					isDocChanged = false;
				}
			} catch (BadLocationException e1) {
				//document changed and we can get the exception
			}

			final String editorTitle = fromEditorPart.getTitle();

			final String editor_name = editor instanceof HQLEditor
									? JdtUiMessages.SaveQueryEditorListener_hql_editor
									: JdtUiMessages.SaveQueryEditorListener_cri_editor;

			if (isDocChanged){
				String information_message = NLS.bind(JdtUiMessages.SaveQueryEditorListener_replacequestion_confirm, editorTitle);
				MessageDialog.openInformation(null, JdtUiMessages.SaveQueryEditorListener_replacetitle_info, information_message);
				return;
			}

			final String newQuery = editor.getQueryString();

			final String wizard_title = NLS.bind(JdtUiMessages.SaveQueryEditorListener_refactoringtitle, editor_name);

			Refactoring ref = new Refactoring(){

				@Override
				public RefactoringStatus checkFinalConditions(IProgressMonitor pm){
					return RefactoringStatus.create(Status.OK_STATUS);
				}

				@Override
				public RefactoringStatus checkInitialConditions(IProgressMonitor pm) {
					return RefactoringStatus.create(Status.OK_STATUS);
				}

				@Override
				public Change createChange(IProgressMonitor pm){
					String change_name = NLS.bind(JdtUiMessages.SaveQueryEditorListener_change_name, editor_name, editorTitle);
					DocumentChange change = new DocumentChange(change_name, doc);
					TextEdit replaceEdit = new ReplaceEdit(position.x, position.y, newQuery);
					change.setEdit(replaceEdit);

					String cc_name = NLS.bind(JdtUiMessages.SaveQueryEditorListener_composite_change_name, editor_name);
					MultiStateTextFileChange mChange = new MultiStateTextFileChange(cc_name, file);
					mChange.addChange(change);

					return mChange;
				}

				@Override
				public String getName() {
					return JdtUiMessages.SaveQueryEditorListener_composite_change_name;
				}
			};

			RefactoringWizard wizard = new RefactoringWizard(ref, RefactoringWizard.DIALOG_BASED_USER_INTERFACE){
				@Override
				protected void addUserInputPages() {
					UserInputWizardPage page = new UserInputWizardPage(wizard_title){

						public void createControl(Composite parent) {
							Composite container = new Composite(parent, SWT.NULL);
					        GridLayout layout = new GridLayout();
					        container.setLayout(layout);
					        layout.numColumns = 1;
					        layout.verticalSpacing = 9;
					        Label label = new Label(container, SWT.NULL);
					        label.setText(NLS.bind(JdtUiMessages.SaveQueryEditorListener_replacequestion, editor_name, editorTitle ));
					        setControl(container);
						}
					};
					addPage(page);
				}
			};

			wizard.setWindowTitle(wizard_title);
			wizard.setDefaultPageTitle(wizard_title);

			IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if ( new RefactoringStarter().activate(wizard, win.getShell(), wizard_title, RefactoringSaveHelper.SAVE_ALL)){
				query = newQuery;
				position.y = query.length();
				fromEditorPart.doSave(null);
			} else {
				if (editor.getDocumentProvider() instanceof TextFileDocumentProvider){
					((TextFileDocumentProvider)editor.getDocumentProvider()).setCanSaveDocument(editor.getEditorInput());
				}
			}
		}
	}

}
