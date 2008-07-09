package org.hibernate.eclipse.console;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IShowEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.QueryInputModel;
import org.hibernate.eclipse.console.actions.ClearAction;
import org.hibernate.eclipse.console.actions.ExecuteQueryAction;

public abstract class AbstractQueryEditor extends TextEditor implements
		QueryEditor, IShowEditorInput {

	private ToolBarManager tbm;
	private ExecuteQueryAction execAction = null;
	private ClearAction clearAction = null;
	final private QueryInputModel queryInputModel;

	private String defPartName;
	private Image defTitleImage;
	private Image connectedTitleImage;

	// to enable execution of queries from files - hack for HBX-744
	private String consoleConfigurationName;

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
		if(getEditorInput() instanceof QueryEditorInput) {
			QueryEditorInput hei = (QueryEditorInput) getEditorInput();
			hei.setQuery( getQueryString() );
		}
		performSave( false, progressMonitor );
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
}
