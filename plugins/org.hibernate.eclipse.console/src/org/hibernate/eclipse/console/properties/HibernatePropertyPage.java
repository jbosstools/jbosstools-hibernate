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

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.jpt.jpa.core.JpaProject.Reference;
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
import org.eclipse.swt.widgets.Link;
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
import org.hibernate.eclipse.console.actions.EditConsoleConfiguration;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.console.utils.LaunchHelper;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.utils.HibernateEclipseUtils;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class HibernatePropertyPage extends PropertyPage {

	Control[] settings;

	private Button enableHibernate;
	
	private String initConsoleConfiguration;

	private Combo selectedConfiguration;
	
	private Link details;
	
	private Button enableNamingStrategy;
	
	private Label nsSeparator;
	
	private boolean initNamingStrategy, initEnableHibernate;
	
	private static final String NONE = "<None>"; //$NON-NLS-1$


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

	private Label addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
		return separator;
	}

	private void addSecondSection(Composite parent) {
		// Label for owner field
		Label ownerLabel = new Label(parent, SWT.NONE);
		ownerLabel.setText(HibernateConsoleMessages.HibernatePropertyPage_default_hibernate_console_config);

		Composite settingsPart = createDefaultComposite(parent,2);
		selectedConfiguration = new Combo(settingsPart, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(50);
		selectedConfiguration.setLayoutData(gd);

		selectedConfiguration.add(NONE);
		// Populate owner text field
		ConsoleConfiguration[] configurations = LaunchHelper.findFilteredSortedConsoleConfigs();
		for (int i = 0; i < configurations.length; i++) {
			ConsoleConfiguration configuration = configurations[i];
			selectedConfiguration.add(configuration.getName() );
		}
		
		details = new Link(settingsPart, SWT.NONE);
		details.setText(HibernateConsoleMessages.HibernatePropertyPage_details);
		details.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ConsoleConfiguration config = KnownConfigurations.getInstance().find(selectedConfiguration.getText());
				if (config != null) {
					new EditConsoleConfiguration(config).run();
				}				
			}
		});
		
		selectedConfiguration.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				details.setEnabled(selectedConfiguration.getSelectionIndex() != 0);
			}
		});
				
		Composite settingsPart2 = createDefaultComposite(parent,2);
		
		enableNamingStrategy = new Button(settingsPart2, SWT.CHECK);
		enableNamingStrategy.setText(HibernateConsoleMessages.HibernatePropertyPage_use_naming_strategy);
		enableNamingStrategy.setSelection(initNamingStrategy);
		
		settings = new Control[] { ownerLabel, selectedConfiguration, details, enableNamingStrategy};
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
		nsSeparator = addSeparator(composite);
		
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
		enableHibernate.setSelection(initEnableHibernate);
		enableNamingStrategy.setSelection(initNamingStrategy);
		selectedConfiguration.setText(initConsoleConfiguration);
	}
	
	private boolean isHibernateJpaProject(){
		IProject project = getProject();
//		if (!JpaFacet.isInstalled(project)) {
		/* Replaced previous line by next by Koen after Dali API changes */
		if (!HibernateEclipseUtils.isJpaFacetInstalled(project)) {
			return false;
		}
//		String jpaPlatformId = JptJpaCorePlugin.getJpaPlatformId(project);
		/* Replaced previous line by next by Koen after Dali API changes */
		String jpaPlatformId = HibernateEclipseUtils.getJpaPlatformID(project);
		return HibernatePropertiesConstants.HIBERNATE_JPA_PLATFORM_ID.equals(jpaPlatformId)
				|| HibernatePropertiesConstants.HIBERNATE_JPA2_0_PLATFORM_ID.equals(jpaPlatformId) 
				|| HibernatePropertiesConstants.HIBERNATE_JPA2_1_PLATFORM_ID.equals(jpaPlatformId);
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

	@Override
	public void setVisible(boolean visible) {
		//loadValues();
		nsSeparator.setVisible(isHibernateJpaProject());
		enableNamingStrategy.setVisible(isHibernateJpaProject());
		super.setVisible(visible);
	}

	public void loadValues() {
		IProject project = getProject();
		IScopeContext scope = new ProjectScope(project);

		Preferences node = scope.getNode(HibernatePropertiesConstants.HIBERNATE_CONSOLE_NODE);

		if(node!=null) {
			initEnableHibernate = node.getBoolean(HibernatePropertiesConstants.HIBERNATE3_ENABLED, false);
			enableHibernate.setSelection(initEnableHibernate);
			String cfg = node.get(HibernatePropertiesConstants.DEFAULT_CONFIGURATION, project.getName() );
			ConsoleConfiguration configuration = KnownConfigurations.getInstance().find(cfg);
			if(configuration==null) {
				selectedConfiguration.select(0);
				details.setEnabled(false);
			} else {
				initConsoleConfiguration = cfg;
				selectedConfiguration.setText(cfg);				
			}
			initNamingStrategy = node.getBoolean(HibernatePropertiesConstants.NAMING_STRATEGY_ENABLED, true);
			enableNamingStrategy.setSelection(initNamingStrategy);
		}
	}
	
	protected void rebildProjectIfJpa() {
		if (isHibernateJpaProject()){
			final JpaProject.Reference reference = (Reference) getProject().getAdapter(Reference.class);
			
			final IWorkspaceRunnable wr = new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor)
						throws CoreException {
					try {
						reference.rebuild();
					} catch (InterruptedException e) {
						throw new CoreException(new Status(IStatus.CANCEL, HibernateConsolePlugin.ID, null, e));
					}
					getProject().build(IncrementalProjectBuilder.FULL_BUILD, monitor);
				}
			};
	
			IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) 
						throws InvocationTargetException, InterruptedException {
					try {
						IWorkspace ws = ResourcesPlugin.getWorkspace();
						ws.run(wr, ws.getRoot(), IWorkspace.AVOID_UPDATE, monitor);
					}
					catch(CoreException e) {
						throw new InvocationTargetException(e);
					}
				}
			};
			
			try{
				new ProgressMonitorDialog(getShell()).run(true, false, op);
			} catch (InvocationTargetException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.HibernatePropertyPage_Error_updating_JpaProject, e);
			} catch (InterruptedException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.HibernatePropertyPage_Error_updating_JpaProject, e);
			}
		}
	}
	
	protected boolean saveNamigStrategyChanges(){
		if (initNamingStrategy == enableNamingStrategy.getSelection()) return true;
		IScopeContext scope = new ProjectScope(getProject());

		Preferences node = scope.getNode(HibernatePropertiesConstants.HIBERNATE_CONSOLE_NODE);

		if(node!=null) {
			node.putBoolean(HibernatePropertiesConstants.NAMING_STRATEGY_ENABLED, enableNamingStrategy.getSelection() );
			try {
				node.flush();
				return true;
			} catch (BackingStoreException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.ProjectUtils_could_not_save_changes_to_preferences, e);
			}
		}
		return false;

	}
	
	public boolean performOk() {
		ProjectUtils.toggleHibernateOnProject( getProject(), enableHibernate.getSelection(),
				selectedConfiguration.getSelectionIndex() != 0 ? selectedConfiguration.getText() : ""); //$NON-NLS-1$
		saveNamigStrategyChanges();
		if (enableHibernate.getSelection() != initEnableHibernate
				|| initNamingStrategy != enableNamingStrategy.getSelection()
				|| isConsoleConfigurationChanged()){
			rebildProjectIfJpa();
		}
		return true;
	}

	private void enableSettings(boolean selection) {
		for (int i = 0; i < settings.length; i++) {
			Control comp = settings[i];
			comp.setEnabled(selection);
		}
		if (selection) {
			details.setEnabled(selectedConfiguration.getSelectionIndex() != 0);
		}
	}
	
	protected boolean isConsoleConfigurationChanged(){
		if (initConsoleConfiguration == null ){
			return selectedConfiguration.getSelectionIndex() == 0;
		} else {
			return !initConsoleConfiguration.equals(selectedConfiguration.getText());
		}
	}

}