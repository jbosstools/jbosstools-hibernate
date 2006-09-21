package org.hibernate.eclipse.console;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IShowEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.hibernate.SessionFactory;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.KnownConfigurationsListener;
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

	protected final class ConfigurationCombo extends ControlContribution {
		private KnownConfigurationsListener listener;

		private SelectionAdapter selectionAdapter;

		private Combo consoleSelection;

		private QueryEditor editor;

		protected ConfigurationCombo(String id, QueryEditor qe) {
			super( id );
			this.editor = qe;
		}

		protected Control createControl(Composite parent) {
			Composite panel = new Composite( parent, SWT.NONE );
			panel.setLayout( new RowLayout() );
			consoleSelection = new Combo( panel, SWT.DROP_DOWN | SWT.READ_ONLY );
			consoleSelection.setLayoutData( new RowData(100, SWT.DEFAULT) );
			
			populateCombo( consoleSelection );

			selectionAdapter = new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					editor.setConsoleConfigurationName( consoleSelection
							.getText() );
				}

			};

			consoleSelection.addSelectionListener( selectionAdapter );

			listener = new KnownConfigurationsListener() {

				public void sessionFactoryClosing(
						ConsoleConfiguration configuration,
						SessionFactory closingFactory) {
				}

				public void sessionFactoryBuilt(ConsoleConfiguration ccfg,
						SessionFactory builtFactory) {
				}

				public void configurationRemoved(ConsoleConfiguration root) {
					populateCombo( consoleSelection );
				}

				public void configurationAdded(ConsoleConfiguration root) {
					populateCombo( consoleSelection );
				}
			};
			KnownConfigurations.getInstance().addConsoleConfigurationListener(
					listener );

			return panel;
		}

		private void populateCombo(final Combo config) {
			ConsoleConfiguration[] configurations = KnownConfigurations
					.getInstance().getConfigurations();
			final String[] names = new String[configurations.length];
			for (int i = 0; i < configurations.length; i++) {
				names[i] = configurations[i].getName();
			}

			final String name = getConsoleConfigurationName()==null?"":getConsoleConfigurationName();
			
			config.getDisplay().syncExec( new Runnable() {
			
				public void run() {
					config.setItems( names );			
					config.setText( name );
				}
			
			} );
			
		}

		public void dispose() {
			if ( listener != null ) {
				KnownConfigurations.getInstance().removeConfigurationListener(
						listener );
			}
			if ( selectionAdapter != null ) {
				if ( !consoleSelection.isDisposed() ) {
					consoleSelection.removeSelectionListener( selectionAdapter );
				}
			}
		}

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

		tbm.update( true );

	}
	
	protected void initializeKeyBindingScopes() {
	       setKeyBindingScopes(new String[] { "org.hibernate.eclipse.console.hql" });  //$NON-NLS-1$
	   }

	public QueryInputModel getQueryInputModel() {
		return queryInputModel;
	}
}
