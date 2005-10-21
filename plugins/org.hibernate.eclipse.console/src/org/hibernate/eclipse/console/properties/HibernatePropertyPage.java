package org.hibernate.eclipse.console.properties;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.osgi.util.NLS;
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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;
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

	private void addLogoSection(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		GridData data = new GridData(SWT.END,SWT.END, true, true);
		
		composite.setLayoutData(data);


		createLogoButtons(composite);
	}
	
	private void addFirstSection(Composite parent) {
		Composite composite = createDefaultComposite(parent,2);

		enableHibernate = new Button(composite, SWT.CHECK);
		enableHibernate.setText("Enable Hibernate 3 support");
		enableHibernate.addSelectionListener(new SelectionAdapter() {
		
			public void widgetSelected(SelectionEvent e) {
				boolean selection = enableHibernate.getSelection();
				enableSettings(selection);			
			}
		});
		
		
		
	}

	private void createLogoButtons(Composite composite) {
		Button hibernateLogoButton = new Button(composite, SWT.NULL);
		hibernateLogoButton.setImage(EclipseImages.getImage(ImageConstants.HIBERNATE_LOGO));
		hibernateLogoButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				openBrowser("http://tools.hibernate.org");
			}
		});
		
		Button jbossLogoButton = new Button(composite, SWT.NULL);
		jbossLogoButton.setImage(EclipseImages.getImage(ImageConstants.JBOSS_LOGO));
		jbossLogoButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				openBrowser("http://www.jboss.org/products/jbosside");
			}
		});
	}

	protected void openBrowser(String href) {
		IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
		try {
			IWebBrowser browser = support.getExternalBrowser();
			browser.openURL(new URL(urlEncode(href.toCharArray()))); //$NON-NLS-1$
		}
		catch (MalformedURLException e) {
			openWebBrowserError(href, e);
		}
		catch (PartInitException e) {
			openWebBrowserError(href, e);
		}	
	}
	
    private void openWebBrowserError(final String href, final Throwable t) {
        getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
				String title = "Open URL";
				String msg = "Unable to open webbrowser for url: " + href;
				IStatus status = HibernateConsolePlugin.throwableToStatus(t);
                ErrorDialog.openError(getShell(), title, msg, status);
            }
        });
    }

	
	private String urlEncode(char[] input) {
	       StringBuffer retu = new StringBuffer(input.length);
	       for (int i = 0; i < input.length; i++) {
	           if (input[i] == ' ')
	               retu.append("%20"); //$NON-NLS-1$
	           else
	               retu.append(input[i]);
	       }
	       return retu.toString();
	}

	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}

	private void addSecondSection(Composite parent) {
		Composite settingsPart = createDefaultComposite(parent,2);
		
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
		addSeparator(composite);
		addLogoSection(composite);
		loadValues();
		enableSettings(enableHibernate.getSelection() );
		
		return composite;
	}

	private Composite createDefaultComposite(Composite parent, int numColumns) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
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