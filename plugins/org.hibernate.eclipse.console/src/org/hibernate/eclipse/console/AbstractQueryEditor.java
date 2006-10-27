package org.hibernate.eclipse.console;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IShowEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.QueryInputModel;
import org.hibernate.eclipse.console.actions.ExecuteQueryAction;

public abstract class AbstractQueryEditor extends TextEditor implements
		QueryEditor, IShowEditorInput {

	private ToolBarManager tbm;
	final private QueryInputModel queryInputModel;
	
	public AbstractQueryEditor() {
		queryInputModel = new QueryInputModel();
	}
	
	final public boolean askUserForConfiguration(String name) {
		return MessageDialog.openQuestion( HibernateConsolePlugin.getDefault()
				.getWorkbench().getActiveWorkbenchWindow().getShell(),
				"Open Session factory",
				"Do you want to open the session factory for " + name + " ?" );
	}

	final public ConsoleConfiguration getConsoleConfiguration() {
		return KnownConfigurations.getInstance().find(
				getConsoleConfigurationName() );
	}

	final public String getConsoleConfigurationName() {
		QueryEditorInput hei = (QueryEditorInput) getEditorInput();
		return hei.getConsoleConfigurationName();
	}

	final public void setConsoleConfigurationName(String name) {
		QueryEditorInput hei = (QueryEditorInput) getEditorInput();
		hei.setConsoleConfigurationName( name );
		hei.resetName();
		showEditorInput( hei );
	}

	public void showEditorInput(IEditorInput editorInput) {

		try {
			doSetInput( editorInput );
		}
		catch (CoreException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage(
					"Could not show query editor input", e );
		}
	}

	final public void doSave(IProgressMonitor progressMonitor) {
		// super.doSave(progressMonitor);
		QueryEditorInput hei = (QueryEditorInput) getEditorInput();
		hei.setQuery( getQueryString() );
		performSave( false, progressMonitor );
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
		ActionContributionItem item = new ActionContributionItem(
				new ExecuteQueryAction( this ) );

		tbm.add( item );

		ControlContribution cc = new ConfigurationCombo( "hql-target", this );
		tbm.add( cc );

		tbm.add( new Separator() );
		
		cc = new ComboContribution("maxResults") {

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
				return 30;
			}
			protected String getLabelText() {
				return "Max results:";
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
						String[] items = new String[] { "", "10", "20", "30", "50"};
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
}
