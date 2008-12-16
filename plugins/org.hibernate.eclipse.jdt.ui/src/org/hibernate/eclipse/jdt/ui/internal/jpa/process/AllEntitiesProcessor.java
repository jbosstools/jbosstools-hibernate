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
package org.hibernate.eclipse.jdt.ui.internal.jpa.process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.jdt.internal.ui.refactoring.actions.RefactoringStarter;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.jdt.ui.Activator;
import org.hibernate.eclipse.jdt.ui.internal.JdtUiMessages;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.EntityInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.Utils;

/**
 * Modify entity classes
 *
 * @author Vitali
 */
public class AllEntitiesProcessor {

	/**
	 * place to search compilation units
	 */
	protected IJavaProject javaProject;
	/**
	 * annotation style
	 */
	protected AnnotStyle annotationStyle = AnnotStyle.FIELDS;
	/**
	 * annotation style preference of majority
	 */
	protected AnnotStyle annotationStylePreference = AnnotStyle.FIELDS;

	/**
	 * group all information about changes of document in one structure
	 */
	protected class ChangeStructure {
		public String fullyQualifiedName;
		public IPath path;
		public IDocument document;
		public TextEdit textEdit;
		public ITextFileBuffer textFileBuffer;
		public Change change;
	};
	/**
	 * change info storage
	 */
	protected ArrayList<ChangeStructure> changes = new ArrayList<ChangeStructure>();
	
	public AllEntitiesProcessor() {
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		int value = preferenceStore.getInt(AllEntitiesProcessor.class.toString());
		if (value >= AnnotStyle.values().length) {
			value = 0;
		}
		annotationStyle = AnnotStyle.values()[value];
	}
	
	public void saveAnnotationStylePreference() {
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		int value = 0;
		while (value < AnnotStyle.values().length) {
			if (AnnotStyle.values()[value] == annotationStyle) {
				break;
			}
			value++;
		}
		if (value >= AnnotStyle.values().length) {
			value = 0;
		}
		preferenceStore.setValue(AllEntitiesProcessor.class.toString(), value);
	}

	/**
	 * execute modification for collection of entities
	 * @param project - common java project for collection of entities
	 * @param entities - collection
	 * @param askConfirmation - ask user confirmation (show dialog)
	 */
	public void modify(IJavaProject project, Map<String, EntityInfo> entities,
			boolean askConfirmation) {
		changes.clear();
		setJavaProject(project);
		// get the buffer manager
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
		Iterator<Map.Entry<String, EntityInfo>> it = entities.entrySet().iterator();
		/*String outText = ""; //$NON-NLS-1$
		String ls = System.getProperties().getProperty("line.separator", "\n");  //$NON-NLS-1$//$NON-NLS-2$
		while (it.hasNext()) {
			Map.Entry<String, EntityInfo> entry = it.next();
			if (entry.getValue().isAbstractFlag()) {
				continue;
			}
			if (entry.getValue().isCompilerProblemsFlag()) {
				// TODO: save entity name as has compiler problems
			}
			outText += entry.getKey() + (it.hasNext() ? ls : ""); //$NON-NLS-1$
		}*/
		boolean performChange = true;
        int res = 0;
        if (askConfirmation) {
        	/** /
    		final String outText2 = outText;
            MessageDialog dialog = new MessageDialog(JavaPlugin.getActiveWorkbenchShell(),
            		JdtUiMessages.AllEntitiesProcessor_header, null,
            		JdtUiMessages.AllEntitiesProcessor_message,
            		MessageDialog.QUESTION,
            		new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0) {
            	protected Control createCustomArea(Composite parent) {
            		Text messageText = new Text(parent, SWT.WRAP | SWT.V_SCROLL);
        			messageText.setText(outText2);
        			messageText.setEditable(false);
        			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL)
        				.grab(true, true)
        				.hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH),
        					convertHorizontalDLUsToPixels(2 * IDialogConstants.BUTTON_BAR_HEIGHT)).applyTo(messageText);

            		return messageText;
            	}
            	protected boolean isResizable() {
            		return true;
            	}
            };
            res = dialog.open();
            /**/
        }
        if (res == 0) {
			// TODO:
			// show warning about abstract classes
			// show warning about compiler problems
			// ...
			// modify accepted items
			if (getAnnotationStyle().equals(AnnotStyle.AUTO)) {
				setAnnotationStyle(getAnnotationStylePreference());
				reCollectModification(bufferManager, entities);
				setAnnotationStyle(AnnotStyle.AUTO);
			}
			else {
				reCollectModification(bufferManager, entities);
			}
		}
        else {
        	performChange = false;
        }
        //
        if (askConfirmation) {
        	if (!showRefactoringDialog(entities, bufferManager)) {
        		performChange = false;
        	}
        }
        if (performChange) {
			performChange(bufferManager);
        }
		performDisconnect(bufferManager);
	}

	protected void performDisconnect(ITextFileBufferManager bufferManager) {
		for (int i = 0; i < changes.size(); i++) {
			ChangeStructure cs = changes.get(i);
			try {
				bufferManager.disconnect(cs.path, LocationKind.IFILE, null);
			} catch (CoreException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage("CoreException: ", e); //$NON-NLS-1$
			}
		}
		changes.clear();
	}

	protected void performChange(ITextFileBufferManager bufferManager) {
		for (int i = 0; i < changes.size(); i++) {
			ChangeStructure cs = changes.get(i);
			try {
				if (cs.textFileBuffer != null && cs.document != null && cs.textEdit != null &&
					((cs.change != null && cs.change.isEnabled()) || (cs.change == null))) {
					cs.document = cs.textFileBuffer.getDocument();
					UndoEdit undo = cs.textEdit.apply(cs.document);
					// commit changes to underlying file
					cs.textFileBuffer.commit(null, true);
				}
			} catch (CoreException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage("CoreException: ", e); //$NON-NLS-1$
			} catch (MalformedTreeException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage("MalformedTreeException: ", e); //$NON-NLS-1$
			} catch (BadLocationException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage("BadLocationException: ", e); //$NON-NLS-1$
			}
		}
	}

	public void reCollectModification(ITextFileBufferManager bufferManager, 
			Map<String, EntityInfo> entities) {

		changes.clear();
		Iterator<Map.Entry<String, EntityInfo>> it = entities.entrySet().iterator();
		try {
			while (it.hasNext()) {
				Map.Entry<String, EntityInfo> entry = it.next();
				if (entry.getValue().isAbstractFlag()) {
					continue;
				}
				collectModification(bufferManager, entry.getKey(), entry.getValue());
			}
		} catch (CoreException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage("CoreException: ", e); //$NON-NLS-1$
		}
	}

	public void collectModification(ITextFileBufferManager bufferManager, String fullyQualifiedName,
			EntityInfo entityInfo) throws CoreException {

		ChangeStructure cs = new ChangeStructure();
		cs.fullyQualifiedName = fullyQualifiedName;
		ICompilationUnit icu = Utils.findCompilationUnit(javaProject, fullyQualifiedName);
		org.eclipse.jdt.core.dom.CompilationUnit cu = Utils.getCompilationUnit(icu, false);
		cs.path = cu.getJavaElement().getPath();
		try {
			bufferManager.connect(cs.path, LocationKind.IFILE, null);
			cs.textFileBuffer = bufferManager.getTextFileBuffer(cs.path, LocationKind.IFILE);
			// retrieve the buffer
			cs.document = cs.textFileBuffer.getDocument();
			AST ast = cu.getAST();
			ASTRewrite rewriter = ASTRewrite.create(ast);
			// ... rewrite
			ProcessEntityInfo processor = new ProcessEntityInfo();
			processor.setAnnotationStyle(annotationStyle);
			processor.setEntityInfo(entityInfo);
			processor.setASTRewrite(rewriter);
			cu.accept(processor);
			//
			cs.textEdit = rewriter.rewriteAST(cs.document, JavaCore.getOptions());
			// add change to array of changes
			changes.add(cs);
		} catch (JavaModelException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage("JavaModelException: ", e); //$NON-NLS-1$
		} catch (MalformedTreeException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage("MalformedTreeException: ", e); //$NON-NLS-1$
		}
	}

	public boolean showRefactoringDialog(final Map<String, EntityInfo> entities, 
			final ITextFileBufferManager bufferManager) {

		final String wizard_title = JdtUiMessages.AllEntitiesProcessor_header;

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

				final CompositeChange cc = new CompositeChange(""); //$NON-NLS-1$
				for (int i = 0; i < changes.size(); i++) {
					ChangeStructure cs = changes.get(i);
					String change_name = cs.fullyQualifiedName;
					DocumentChange change = new DocumentChange(change_name, cs.document);
					change.setEdit(cs.textEdit);
					cs.change = change;
					cc.add(change);
				}
				cc.markAsSynthetic();
				return cc;
			}

			@Override
			public String getName() {
				return JdtUiMessages.SaveQueryEditorListener_composite_change_name;
			}
		};

		final ModifyListener ml = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				int idx = ((Combo)e.getSource()).getSelectionIndex();
				if (idx == 0 && !getAnnotationStyle().equals(AnnotStyle.FIELDS)) {
					setAnnotationStyle(AnnotStyle.FIELDS);
					reCollectModification(bufferManager, entities);
				}
				else if (idx == 1 && !getAnnotationStyle().equals(AnnotStyle.GETTERS)) {
					setAnnotationStyle(AnnotStyle.GETTERS);
					reCollectModification(bufferManager, entities);
				}
				else if (idx == 2 && !getAnnotationStyle().equals(AnnotStyle.AUTO)) {
					setAnnotationStyle(getAnnotationStylePreference());
					reCollectModification(bufferManager, entities);
					setAnnotationStyle(AnnotStyle.AUTO);
				}
			}
			
		};

		RefactoringWizard wizard = new RefactoringWizard(ref, RefactoringWizard.DIALOG_BASED_USER_INTERFACE) {

			@Override
			protected void addUserInputPages() {
				UserInputWizardPage page = new UserInputWizardPage(wizard_title) {
					public void createControl(Composite parent) {
	        		    initializeDialogUnits(parent);
						Composite container = new Composite(parent, SWT.NULL);
				        GridLayout layout = new GridLayout();
				        container.setLayout(layout);
				        layout.numColumns = 1;
				        Label label = new Label(container, SWT.NULL);
				        label.setText(JdtUiMessages.AllEntitiesProcessor_message);

				        TableViewer listViewer = new TableViewer(container, SWT.SINGLE | SWT.H_SCROLL
								| SWT.V_SCROLL | SWT.BORDER);
						//listViewer.setComparator(getViewerComparator());
						Control control = listViewer.getControl();
						GridData data = new GridData(GridData.FILL_BOTH
								| GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
						data.heightHint = convertHeightInCharsToPixels(10);
						control.setLayoutData(data);
						listViewer.setContentProvider(new IStructuredContentProvider() {
							public Object[] getElements(Object inputElement) {
								return entities.values().toArray();
							}

							public void dispose() {

							}

							public void inputChanged(Viewer viewer, Object oldInput,
									Object newInput) {

							}
						});

						listViewer.setLabelProvider(new LabelProvider() {

							private Image classImage;

							{
								classImage = JavaElementImageProvider.getTypeImageDescriptor(false, false, 0, false).createImage();

							}
							@Override
							public String getText(Object element) {
								EntityInfo info = (EntityInfo) element;
								return info.getFullyQualifiedName();
							}

							@Override
							public Image getImage(Object element) {
								return classImage;
							}

							@Override
							public void dispose() {
								classImage.dispose();
								super.dispose();
							}
						});

						listViewer.setInput(entities);
						GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL)
	        				.grab(true, true)
	        				.hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH),
	        					convertHorizontalDLUsToPixels(2 * IDialogConstants.BUTTON_BAR_HEIGHT)).applyTo(listViewer.getControl());
						//Button generateChoice = new Button(container, SWT.CHECK);
						//generateChoice.setText("fdwsdfv");
						Composite combolabel = new Composite(container, SWT.NULL);
				        layout = new GridLayout();
				        combolabel.setLayout(layout);
				        layout.numColumns = 2;
						Label labelChoice = new Label(combolabel, SWT.NULL);
						labelChoice.setText(JdtUiMessages.AllEntitiesProcessor_setup_annotation_generation_preference);
						Combo generateChoice = new Combo(combolabel, SWT.READ_ONLY);
						generateChoice.add(JdtUiMessages.AllEntitiesProcessor_annotate_fields);
						generateChoice.add(JdtUiMessages.AllEntitiesProcessor_annotate_getters);
						generateChoice.add(JdtUiMessages.AllEntitiesProcessor_auto_select_from_class_preference);
						int idx = 0;
						if (getAnnotationStyle().equals(AnnotStyle.FIELDS)) {
							idx = 0;
						}
						else if (getAnnotationStyle().equals(AnnotStyle.GETTERS)) {
							idx = 1;
						}
						else if (getAnnotationStyle().equals(AnnotStyle.AUTO)) {
							idx = 2;
						}
						generateChoice.select(idx);
						generateChoice.addModifyListener(ml);
	            		setControl(container);
					}
				};
				addPage(page);
			}

		};

		wizard.setWindowTitle(wizard_title);
		wizard.setDefaultPageTitle(wizard_title);

		IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		RefactoringStarter rStarter = new RefactoringStarter();
		boolean res = rStarter.activate(wizard, win.getShell(), wizard_title, RefactoringSaveHelper.SAVE_ALL);
		RefactoringStatus rs = rStarter.getInitialConditionCheckingStatus();
		return res;
	}

	protected void setJavaProject(IJavaProject project) {
		javaProject = project;
	}

	public AnnotStyle getAnnotationStyle() {
		return annotationStyle;
	}

	public void setAnnotationStyle(AnnotStyle annotationStyle) {
		this.annotationStyle = annotationStyle;
	}

	public AnnotStyle getAnnotationStylePreference() {
		return annotationStylePreference;
	}

	public void setAnnotationStylePreference(AnnotStyle annotationStylePreference) {
		this.annotationStylePreference = annotationStylePreference;
	}
}
