/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.console.properties;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.PreferencePage;
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
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class HibernatePropertyPage extends PropertyPage {

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
		enableHibernate.setText(HibernateConsoleMessages.HibernatePropertyPage_enable_hibernate3_support);
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
				openBrowser("http://tools.hibernate.org"); //$NON-NLS-1$
			}
		});

		Button jbossLogoButton = new Button(composite, SWT.NULL);
		jbossLogoButton.setImage(EclipseImages.getImage(ImageConstants.JBOSS_LOGO));
		jbossLogoButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				openBrowser("http://www.jboss.com/products/devstudio"); //$NON-NLS-1$
			}
		});
	}

	protected void openBrowser(String href) {
		IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
		try {
			IWebBrowser browser = support.getExternalBrowser();
			browser.openURL(new URL(urlEncode(href.toCharArray())));
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
				String title = HibernateConsoleMessages.HibernatePropertyPage_open_url;
				String msg = HibernateConsoleMessages.HibernatePropertyPage_unable_open_webbrowser_for_url + href;
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
		ownerLabel.setText(HibernateConsoleMessages.HibernatePropertyPage_default_hibernate_console_config);

		selectedConfiguration = new Combo(parent, SWT.DROP_DOWN);
		GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(50);
		selectedConfiguration.setLayoutData(gd);

		// Populate owner text field
		ConsoleConfiguration[] configurations = KnownConfigurations.getInstance().getConfigurationsSortedByName();
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
		enableHibernate.setSelection(false);
		selectedConfiguration.setText(""); //$NON-NLS-1$
	}

	private IProject getProject() {
		IAdaptable adaptable= getElement();
		if (adaptable != null) {
			IJavaElement elem= (IJavaElement) adaptable.getAdapter(IJavaElement.class);
			if (elem instanceof IJavaProject) {
				return ((IJavaProject) elem).getProject();
			}
		}
		return null;
	}


	public void loadValues() {
		IProject project = getProject();
		IScopeContext scope = new ProjectScope(project);

		Preferences node = scope.getNode("org.hibernate.eclipse.console"); //$NON-NLS-1$

		if(node!=null) {
			enableHibernate.setSelection(node.getBoolean("hibernate3.enabled", false) ); //$NON-NLS-1$
			String cfg = node.get("default.configuration", project.getName() ); //$NON-NLS-1$
			ConsoleConfiguration configuration = KnownConfigurations.getInstance().find(cfg);
			if(configuration==null) {
				selectedConfiguration.setText(""); //$NON-NLS-1$
			} else {
				selectedConfiguration.setText(cfg);
			}
		}

	}
	public boolean performOk() {
		ProjectUtils.toggleHibernateOnProject( getProject(), enableHibernate.getSelection(), selectedConfiguration.getText() );
		return true;
	}


	private void enableSettings(boolean selection) {
		for (int i = 0; i < settings.length; i++) {
			Control comp = settings[i];
			comp.setEnabled(selection);
		}
	}

}