package org.hibernate.eclipse.console.properties;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class HibernatePropertyPage extends PropertyPage {

	private static final int TEXT_FIELD_WIDTH = 50;
	
	Control[] settings;

	private Button enableHibernate;

	private Combo selectedConfiguration;
	

	/**
	 * Constructor for SamplePropertyPage.
	 */
	public HibernatePropertyPage() {
		super();
	}

	private void addFirstSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		enableHibernate = new Button(composite, SWT.CHECK);
		enableHibernate.setText("Enable Hibernate 3 support");
		enableHibernate.addSelectionListener(new SelectionAdapter() {
		
			public void widgetSelected(SelectionEvent e) {
				boolean selection = enableHibernate.getSelection();
				enableSettings(selection);			
			}
		});
	}

	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}

	private void addSecondSection(Composite parent) {
		Composite settingsPart = createDefaultComposite(parent);

		
		// Label for owner field
		Label ownerLabel = new Label(settingsPart, SWT.NONE);
		ownerLabel.setText("Default Hibernate Console configuration:");

		selectedConfiguration = new Combo(parent, SWT.DROP_DOWN);
		GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		selectedConfiguration.setLayoutData(gd);

		// Populate owner text field
			ConsoleConfiguration[] configurations = KnownConfigurations.getInstance().getConfigurations();
			for (int i = 0; i < configurations.length; i++) {
				ConsoleConfiguration configuration = configurations[i];
				selectedConfiguration.add(configuration.getName() );
			}
		
		settings = new Control[] { ownerLabel, selectedConfiguration };
		
	}

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		addFirstSection(composite);
		addSeparator(composite);
		addSecondSection(composite);
		
		loadValues();
		enableSettings(enableHibernate.getSelection() );
		
		return composite;
	}

	private Composite createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}

	protected void performDefaults() {
		// Populate the owner text field with the default value
		//ownerText.setText(DEFAULT_OWNER);
	}
	
	public void loadValues() {
		IJavaProject prj = (IJavaProject) getElement();
		IScopeContext scope = new ProjectScope(prj.getProject() );
		
		Preferences node = scope.getNode("org.hibernate.eclipse.console");
		
		if(node!=null) {
			enableHibernate.setSelection(node.getBoolean("hibernate3.enabled", false) );
			String cfg = node.get("default.configuration", prj.getProject().getName() );
			ConsoleConfiguration configuration = KnownConfigurations.getInstance().find(cfg);
			if(configuration==null) {
				selectedConfiguration.setText("");
			} else {
				selectedConfiguration.setText(cfg);
			}			
		} 
		
	}
	public boolean performOk() {
		IJavaProject prj = (IJavaProject) getElement();
		IScopeContext scope = new ProjectScope(prj.getProject() );
		
		Preferences node = scope.getNode("org.hibernate.eclipse.console");
		
		if(node!=null) {
			node.putBoolean("hibernate3.enabled", enableHibernate.getSelection() );
			node.put("default.configuration", selectedConfiguration.getText() );
			try {
				node.flush();
			} catch (BackingStoreException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage("Could not save changes to preferences", e);
				return false;
			}
		} else {
			return false;
		}
		
		try {
			if(enableHibernate.getSelection() ) {
				ProjectUtils.addProjectNature(prj.getProject(), "org.hibernate.eclipse.console.hibernateNature", new NullProgressMonitor() );
			} else {
				ProjectUtils.removeProjectNature(prj.getProject(), "org.hibernate.eclipse.console.hibernateNature", new NullProgressMonitor() );
			}
		} catch(CoreException ce) {
			HibernateConsolePlugin.getDefault().logErrorMessage("Could not activate Hibernate nature on project " + prj.getProject().getName(), ce);
			HibernateConsolePlugin.getDefault().log(ce.getStatus() );
		}
		return true;
	}

	private void enableSettings(boolean selection) {
		for (int i = 0; i < settings.length; i++) {
			Control comp = settings[i];
			comp.setEnabled(selection);
		}
	}

}