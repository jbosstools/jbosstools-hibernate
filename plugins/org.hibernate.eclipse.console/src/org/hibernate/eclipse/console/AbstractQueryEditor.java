/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IShowEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.internal.editors.text.NLSUtility;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.QueryInputModel;
import org.hibernate.eclipse.console.actions.ClearAction;
import org.hibernate.eclipse.console.actions.ExecuteQueryAction;
import org.hibernate.eclipse.console.actions.StickResTabAction;

public abstract class AbstractQueryEditor extends TextEditor implements
		QueryEditor, IShowEditorInput {

	private ToolBarManager tbm;
	private ExecuteQueryAction execAction = null;
	private ClearAction clearAction = null;
	private StickResTabAction stickResTabAction = null;
	final private QueryInputModel queryInputModel;

	private String defPartName;
	private Image defTitleImage;
	private Image connectedTitleImage;

	// to enable execution of queries from files - hack for HBX-744
	private String consoleConfigurationName;

	protected boolean pinToOneResTab = false;
	
	public AbstractQueryEditor() {
		queryInputModel = new QueryInputModel();
	}

	final public boolean askUserForConfiguration(String name) {
		String out = NLS.bind(HibernateConsoleMessages.AbstractQueryEditor_do_you_want_open_session_factory, name);
		return MessageDialog.openQuestion( HibernateConsolePlugin.getDefault()
				.getWorkbench().getActiveWorkbenchWindow().getShell(),
				HibernateConsoleMessages.AbstractQueryEditor_open_session_factory, out );
	}

	final public ConsoleConfiguration getConsoleConfiguration() {
		return KnownConfigurations.getInstance().find(
				getConsoleConfigurationName() );
	}

	final public String getConsoleConfigurationName() {
		//TODO: these should be stored as resource info
		if(getEditorInput() instanceof QueryEditorInput) {
			QueryEditorInput hei = (QueryEditorInput) getEditorInput();
			return hei.getConsoleConfigurationName();
		} else {
			return consoleConfigurationName;
		}
	}

	final public void setConsoleConfigurationName(String name) {
		if(getEditorInput() instanceof QueryEditorInput) {
			QueryEditorInput hei = (QueryEditorInput) getEditorInput();
			hei.setConsoleConfigurationName( name );
			hei.setQuery( getQueryString() );
			hei.resetName();
		}
		this.consoleConfigurationName = name;

		showEditorInput( getEditorInput() );
	}

	protected void updateExecButton(){
		/*if (getSourceViewer() != null ){
			execAction.setEnabled(getConsoleConfigurationName().trim().length() != 0
					&& getSourceViewer().getDocument().get().trim().length() > 0);
		} else {
			execAction.setEnabled(false);
		}*/
		execAction.setEnabled(getQueryString().trim().length() > 0
				&& getConsoleConfigurationName().length() != 0);
	}

	public void showEditorInput(IEditorInput editorInput) {

		try {
			doSetInput( editorInput );
			updateExecButton();
		}
		catch (CoreException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage(
					HibernateConsoleMessages.AbstractQueryEditor_could_not_show_query_editor_input, e );
		}
	}

	final public void doSave(IProgressMonitor progressMonitor) {
		// super.doSave(progressMonitor);
		QueryEditorInput hei = null;
		if (getEditorInput() instanceof QueryEditorInput) {
			hei = (QueryEditorInput) getEditorInput();
			hei.setQuery( getQueryString() );
		}
		IDocumentProvider p = getDocumentProvider();
		if (p != null && p.isDeleted(getEditorInput())) {
			if (isSaveAsAllowed()) {
				// 1GEUSSR: ITPUI:ALL - User should never loose changes made in the editors.
				// Changed Behavior to make sure that if called inside a regular save (because
				// of deletion of input element) there is a way to report back to the caller.
				performSaveAs(progressMonitor);

			} else {
				Shell shell = getSite().getShell();
				String title = HibernateConsoleMessages.AbstractQueryEditor_cannot_save;
				String msg = HibernateConsoleMessages.AbstractQueryEditor_the_file_has_been_deleted_or_is_not_accessible;
				MessageDialog.openError(shell, title, msg);
			}
		} else {
			performSave( false, progressMonitor );
		}
	}

	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
	}

	final public String getQueryString() {
		IEditorInput editorInput = getEditorInput();
		IDocumentProvider docProvider = getDocumentProvider();
		IDocument doc = docProvider.getDocument( editorInput );
		return doc.get();
	}

	/**
	 * Dispose of resources held by this editor.
	 *
	 * @see IWorkbenchPart#dispose()
	 */
	final public void dispose() {
		super.dispose();
		if ( tbm != null )
			tbm.dispose();
	}

	final protected void createToolbar(Composite parent) {
		ToolBar bar = new ToolBar( parent, SWT.HORIZONTAL );
		bar.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

		tbm = new ToolBarManager( bar );
		execAction = new ExecuteQueryAction( this );
		clearAction = new ClearAction( this );
		stickResTabAction = new StickResTabAction( this );

		ActionContributionItem item = new ActionContributionItem( execAction );
		tbm.add( item );

		item = new ActionContributionItem( clearAction );
		tbm.add( item );

		ControlContribution cc = new ConfigurationCombo( "hql-target", this ); //$NON-NLS-1$
		tbm.add( cc );

		tbm.add( new Separator() );

		cc = new ComboContribution("maxResults") { //$NON-NLS-1$

			SelectionAdapter selectionAdapter =	new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					Integer maxResults = null;

					try {
						maxResults = new Integer(getText());
					}
					catch (NumberFormatException e1) {
						maxResults = null;
					}
					queryInputModel.setMaxResults( maxResults );
				}

			};

			protected Control createControl(Composite parent) {


				Control control = super.createControl( parent );

				comboControl.addModifyListener( new ModifyListener() {

					public void modifyText(ModifyEvent e) {
						Integer maxResults = null;

						try {
							maxResults = new Integer(getText());
						}
						catch (NumberFormatException e1) {
							maxResults = null;
						}
						queryInputModel.setMaxResults( maxResults );
					}
				} );
				return control;
			}
			protected int getComboWidth() {
				return 75;
			}
			protected String getLabelText() {
				return HibernateConsoleMessages.AbstractQueryEditor_max_results;
			}

			protected boolean isReadOnly() {
				return false;
			}

			protected SelectionListener getSelectionAdapter() {
				return selectionAdapter;
			}

			void populateComboBox() {
				comboControl.getDisplay().syncExec( new Runnable() {

					public void run() {
						String[] items = new String[] { "", "10", "20", "30", "50"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
						comboControl.setItems( items );
					}

				} );


			}

		};
		tbm.add(cc);

		tbm.add( new Separator() );
		item = new ActionContributionItem( stickResTabAction );
		tbm.add( item );

		tbm.update( true );

	}

	protected void initializeKeyBindingScopes() {
	       setKeyBindingScopes(new String[] { "org.hibernate.eclipse.console.hql" });  //$NON-NLS-1$
	   }

	public QueryInputModel getQueryInputModel() {
		return queryInputModel;
	}

	public void showConnected(IEditorPart editor){
		defPartName = getPartName();
		defTitleImage = getTitleImage();
		setPartName(defPartName + "->" + editor.getTitle()); //$NON-NLS-1$
		if (connectedTitleImage == null){
			connectedTitleImage = HibernateConsolePlugin.getImageDescriptor(getConnectedImageFilePath()).createImage();
		}
		setTitleImage(connectedTitleImage);
	}

	public void showDisconnected(){
		setPartName(defPartName);
		if (defTitleImage != null && !defTitleImage.isDisposed()){
			setTitleImage(defTitleImage);
		} else {
			setTitleImage(null);
		}
		connectedTitleImage.dispose();
	}

	protected abstract String getConnectedImageFilePath();

	/**
	 * returns file extension for "Save As" dialog
	 */
	protected abstract String getSaveAsFileExtension();

	/**
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#performSaveAs(IProgressMonitor)
	 * the same method except line: "dialog.setOriginalName(getSaveAsFileExtension());"
	 */
	@SuppressWarnings("restriction")
	protected void performSaveAs(IProgressMonitor progressMonitor) {
		Shell shell= getSite().getShell();
		final IEditorInput input= getEditorInput();

		IDocumentProvider provider= getDocumentProvider();
		final IEditorInput newInput;

		if (input instanceof IURIEditorInput && !(input instanceof IFileEditorInput)) {
			FileDialog dialog= new FileDialog(shell, SWT.SAVE);
			IPath oldPath= URIUtil.toPath(((IURIEditorInput)input).getURI());
			if (oldPath != null) {
				dialog.setFileName(oldPath.lastSegment());
				dialog.setFilterPath(oldPath.toOSString());
			}

			String path= dialog.open();
			if (path == null) {
				if (progressMonitor != null)
					progressMonitor.setCanceled(true);
				return;
			}

			// Check whether file exists and if so, confirm overwrite
			final File localFile= new File(path);
			if (localFile.exists()) {
		        MessageDialog overwriteDialog= new MessageDialog(
		        		shell,
		        		HibernateConsoleMessages.AbstractQueryEditor_save_as,
		        		null,
		        		NLSUtility.format(HibernateConsoleMessages.AbstractQueryEditor_already_exists_do_you_want_to_replace_it, path),
		        		MessageDialog.WARNING,
		        		new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL },
		        		1); // 'No' is the default
				if (overwriteDialog.open() != Window.OK) {
					if (progressMonitor != null) {
						progressMonitor.setCanceled(true);
						return;
					}
				}
			}

			IFileStore fileStore;
			try {
				fileStore= EFS.getStore(localFile.toURI());
			} catch (CoreException ex) {
				EditorsPlugin.log(ex.getStatus());
				String title= HibernateConsoleMessages.AbstractQueryEditor_problems_during_save_as;
				String msg= NLSUtility.format(HibernateConsoleMessages.AbstractQueryEditor_save_could_not_be_completed, ex.getMessage());
				MessageDialog.openError(shell, title, msg);
				return;
			}

			IFile file= getWorkspaceFile(fileStore);
			if (file != null)
				newInput= new FileEditorInput(file);
			else
				newInput= new FileStoreEditorInput(fileStore);

		} else {
			SaveAsDialog dialog= new SaveAsDialog(shell);

			IFile original= (input instanceof IFileEditorInput) ? ((IFileEditorInput) input).getFile() : null;
			if (original != null) {
				dialog.setOriginalFile(original);
			}
			else {
				dialog.setOriginalName(getSaveAsFileExtension());
			}

			dialog.create();

			if (provider.isDeleted(input) && original != null) {
				String message= NLSUtility.format(HibernateConsoleMessages.AbstractQueryEditor_the_original_file_has_been_deleted_or_is_not_accessible, original.getName());
				dialog.setErrorMessage(null);
				dialog.setMessage(message, IMessageProvider.WARNING);
			}

			if (dialog.open() == Window.CANCEL) {
				if (progressMonitor != null)
					progressMonitor.setCanceled(true);
				return;
			}

			IPath filePath= dialog.getResult();
			if (filePath == null) {
				if (progressMonitor != null)
					progressMonitor.setCanceled(true);
				return;
			}

			IWorkspace workspace= ResourcesPlugin.getWorkspace();
			IFile file= workspace.getRoot().getFile(filePath);
			newInput= new FileEditorInput(file);

		}

		if (provider == null) {
			// editor has programmatically been  closed while the dialog was open
			return;
		}

		boolean success= false;
		try {

			provider.aboutToChange(newInput);
			provider.saveDocument(progressMonitor, newInput, provider.getDocument(input), true);
			success= true;

		} catch (CoreException x) {
			final IStatus status= x.getStatus();
			if (status == null || status.getSeverity() != IStatus.CANCEL) {
				String title= HibernateConsoleMessages.AbstractQueryEditor_problems_during_save_as;
				String msg= NLSUtility.format(HibernateConsoleMessages.AbstractQueryEditor_save_could_not_be_completed, x.getMessage());
				MessageDialog.openError(shell, title, msg);
			}
		} finally {
			provider.changed(newInput);
			if (success)
				setInput(newInput);
		}

		if (progressMonitor != null)
			progressMonitor.setCanceled(!success);
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#getWorkspaceFile(IFileStore)
	 * the same PROTECTED method instead of PRIVATE
	 */
	protected IFile getWorkspaceFile(IFileStore fileStore) {
		IWorkspaceRoot workspaceRoot= ResourcesPlugin.getWorkspace().getRoot();
		IFile[] files= workspaceRoot.findFilesForLocation(URIUtil.toPath(fileStore.toURI()));
		if (files != null && files.length == 1)
			return files[0];
		return null;
	}

	public boolean initTextAndToolTip(String text) {
		if (execAction != null) {
			execAction.initTextAndToolTip(text);
			return true;
		}
		return false;
	}

	public boolean getPinToOneResTab() {
		return pinToOneResTab;
	}

	public void setPinToOneResTab(boolean pinToOneResTab) {
		this.pinToOneResTab = pinToOneResTab;
	}
}
