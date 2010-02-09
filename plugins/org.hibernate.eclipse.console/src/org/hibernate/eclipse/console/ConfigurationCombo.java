/**
 * 
 */
package org.hibernate.eclipse.console;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.hibernate.SessionFactory;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.KnownConfigurationsAdapter;
import org.hibernate.console.KnownConfigurationsListener;
import org.hibernate.eclipse.console.utils.LaunchHelper;

final class ConfigurationCombo extends ComboContribution {

	private KnownConfigurationsListener listener;

	private SelectionAdapter selectionAdapter;

	private QueryEditor editor;

	protected ConfigurationCombo(String id, QueryEditor qe) {
		super( id );
		this.editor = qe;
	}

	protected Control createControl(Composite parent) {
		
		selectionAdapter = new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				editor.setConsoleConfigurationName( comboControl.getText() );
			}

		};

		Control control = super.createControl( parent );

		listener = new KnownConfigurationsAdapter() {

			public void sessionFactoryClosing(
					ConsoleConfiguration configuration,
					SessionFactory closingFactory) {
			}

			public void sessionFactoryBuilt(ConsoleConfiguration ccfg,
					SessionFactory builtFactory) {
			}

			public void configurationRemoved(ConsoleConfiguration root, boolean forUpdate) {
				populateComboBox();
			}

			public void configurationAdded(ConsoleConfiguration root) {
				populateComboBox();
			}

			public void configurationReset(ConsoleConfiguration ccfg) {				
				
			}
		};
		KnownConfigurations.getInstance().addConsoleConfigurationListener(
				listener );			
		
		return control;
	}

	protected void populateComboBox() {
		ConsoleConfiguration[] configurations = LaunchHelper.findFilteredSortedConsoleConfigs();
		final String[] names = new String[configurations.length];
		for (int i = 0; i < configurations.length; i++) {
			names[i] = configurations[i].getName();
		}

		final String name = editor.getConsoleConfigurationName()==null?"":editor.getConsoleConfigurationName(); //$NON-NLS-1$
		
		comboControl.getDisplay().syncExec( new Runnable() {
		
			public void run() {
				comboControl.setItems( names );			
				comboControl.setText( name );
				editor.setConsoleConfigurationName( comboControl.getText() );
			}
		
		} );
		

		
	}

	public void dispose() {
		super.dispose();
		if ( listener != null ) {
			KnownConfigurations.getInstance().removeConfigurationListener(
					listener );
		}
	}

	protected SelectionListener getSelectionAdapter() {
		return selectionAdapter;
	}

	
}