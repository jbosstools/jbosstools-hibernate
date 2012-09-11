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
package org.hibernate.eclipse.console.wizards;

import java.util.Properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.hibernate.cfg.Environment;
import org.hibernate.console.ConnectionProfileUtil;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.utils.DriverClassHelpers;

/**
 * Wizard for creating basic hibernate.cfg.xml
 */

public class NewConfigurationWizardPage extends WizardPage {
    final private DriverClassHelpers helper = new DriverClassHelpers();

    private Label containerText;

    private Label fileText;

    private Text sessionFactoryNameText;

    private Combo dialectCombo;

    private Combo driver_classCombo;

    private Text usernameText;

    private Text passwordText;

    private Text defaultSchemaText;
    private Text defaultCatalogText;

    private Combo urlCombo;

    private Button createConsoleConfiguration;

    private ISelection selection;

    private final WizardNewFileCreationPage fileCreation;

    private boolean beenShown = false;
    
    private String defaultConnectionProfile = null;

    /**
     * Constructor for SampleNewWizardPage.
     * @param page
     *
     * @param pageName
     */
    public NewConfigurationWizardPage(ISelection selection, WizardNewFileCreationPage page) {
        super("wizardPage"); //$NON-NLS-1$
        this.fileCreation = page;
        setTitle(HibernateConsoleMessages.NewConfigurationWizardPage_hibernate_config_file);
        setDescription(HibernateConsoleMessages.NewConfigurationWizardPage_this_wizard_creates);
        this.selection = selection;
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {

        ModifyListener listener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        };

        SelectionListener selectionListener = new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				dialogChanged();
				getContainer().updateButtons();
			}

			public void widgetSelected(SelectionEvent e) {
				dialogChanged();
				getContainer().updateButtons();
			}

		};

		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		Composite container = new Composite(sc, SWT.NULL);
		sc.setContent(container);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 2;
        layout.verticalSpacing = 9;
        Label label = new Label(container, SWT.NULL);
        label.setText(HibernateConsoleMessages.NewConfigurationWizardPage_container);

        containerText = new Label(container, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        containerText.setLayoutData(gd);

        label = new Label(container, SWT.NULL);
        label.setText(HibernateConsoleMessages.NewConfigurationWizardPage_file_name);

        fileText = new Label(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        fileText.setLayoutData(gd);

        label = new Label(container, SWT.NULL);
        label.setText(HibernateConsoleMessages.NewConfigurationWizardPage_session_factory_name);
        sessionFactoryNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        sessionFactoryNameText.setLayoutData(gd);
        sessionFactoryNameText.addModifyListener(listener);
        
        Link link = new Link(container, SWT.RIGHT);
        link.setText(HibernateConsoleMessages.NewConfigurationWizardPage_getValuesFromConnection);
        link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				SelectConnectionProfileDialog dialog = new SelectConnectionProfileDialog(shell);
				if (defaultConnectionProfile != null) dialog.setDefaultValue(defaultConnectionProfile);
				if (dialog.open() == Window.OK){
					defaultConnectionProfile = dialog.getConnectionProfileName();
					fillPropertiesFromConnectionProfile(defaultConnectionProfile);
				}
			}
		});
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 2;
        link.setLayoutData(gd);
        
        label = new Label(container, SWT.NULL);
        label.setText(HibernateConsoleMessages.NewConfigurationWizardPage_database_dialect);
        dialectCombo = new Combo(container, SWT.NULL);
        fillHerUp(dialectCombo, helper.getDialectNames() );
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
        dialectCombo.setLayoutData(gd);
        dialectCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                String[] driverClasses = helper.getDriverClasses(helper
                        .getDialectClass(dialectCombo.getText() ) );
                fillHerUp(driver_classCombo, driverClasses);
                dialogChanged();
            }
        });

        gd = new GridData(GridData.BEGINNING, GridData.CENTER, false,false);
        gd.horizontalAlignment = SWT.TOP;
        gd.verticalAlignment = SWT.TOP;
        label.setLayoutData(gd);

        Composite driverManagerTabContainer = container;
        label = new Label(driverManagerTabContainer, SWT.NULL);
        label.setText(HibernateConsoleMessages.NewConfigurationWizardPage_driver_class);
        driver_classCombo = new Combo(driverManagerTabContainer, SWT.NULL);
        driver_classCombo.select(0);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
        driver_classCombo.setLayoutData(gd);
        driver_classCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                String[] connectionURLS = helper
                        .getConnectionURLS(driver_classCombo.getText() );
                fillHerUp(urlCombo, connectionURLS);
                dialogChanged();
            }
        });

        label = new Label(driverManagerTabContainer, SWT.NULL);
        label.setText(HibernateConsoleMessages.NewConfigurationWizardPage_connection_url);
        urlCombo = new Combo(driverManagerTabContainer, SWT.NULL);
        urlCombo.select(0);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
        urlCombo.setLayoutData(gd);
        urlCombo.addModifyListener(listener);

        label = new Label(driverManagerTabContainer, SWT.NULL);
        label.setText(HibernateConsoleMessages.NewConfigurationWizardPage_default_schema);
        defaultSchemaText = new Text(driverManagerTabContainer, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        defaultSchemaText.setLayoutData(gd);
        defaultSchemaText.addModifyListener(listener);

        label = new Label(driverManagerTabContainer, SWT.NULL);
        label.setText(HibernateConsoleMessages.NewConfigurationWizardPage_default_catalog);
        defaultCatalogText = new Text(driverManagerTabContainer, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        defaultCatalogText.setLayoutData(gd);
        defaultCatalogText.addModifyListener(listener);

        label = new Label(driverManagerTabContainer, SWT.NULL);
        label.setText(HibernateConsoleMessages.NewConfigurationWizardPage_user_name);
        usernameText = new Text(driverManagerTabContainer, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        usernameText.setLayoutData(gd);
        usernameText.addModifyListener(listener);

        label = new Label(driverManagerTabContainer, SWT.NULL);
        label.setText(HibernateConsoleMessages.NewConfigurationWizardPage_password);
        passwordText = new Text(driverManagerTabContainer, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        passwordText.setLayoutData(gd);
        passwordText.addModifyListener(listener);

        fillLabel(container);
        fillLabel(container);

        fillLabel(container);

        createConsoleConfiguration = new Button(container, SWT.CHECK);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        createConsoleConfiguration.setLayoutData(gd);
        createConsoleConfiguration.setText(HibernateConsoleMessages.NewConfigurationWizardPage_create_console_configuration);
        createConsoleConfiguration.addSelectionListener(selectionListener);
        
        sc.setMinSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        initialize();
        dialogChanged();

        setControl(sc);
    }


    /**
     * @param urlCombo2
     */
    private void fillHerUp(Combo combo, String[] newContent) {

        String original = combo.getText();
        combo.removeAll();
        for (int i = 0; i < newContent.length; i++) {
            String name = newContent[i];
            combo.add(name);
        }
        combo.setText(original);
    }

    /**
     * @param container
     */
    private void fillLabel(Composite container) {
        Label label = new Label(container, SWT.NULL);
        //GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      //  label.setLayoutData(gd);
        //label.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_CYAN));
    }

    /**
     * Tests if the current workbench selection is a suitable container to use.
     */

    private void initialize() {
        updateStatus(null);
        /*if (selection != null && selection.isEmpty() == false
                && selection instanceof IStructuredSelection) {
            IStructuredSelection ssel = (IStructuredSelection) selection;
            if (ssel.size() > 1)
                return;
            Object obj = ssel.getFirstElement();
            if (obj instanceof IResource) {
                IContainer container;
                if (obj instanceof IContainer)
                    container = (IContainer) obj;
                else
                    container = ( (IResource) obj).getParent();
                containerText.setText(container.getFullPath().toString() );
            }
        }
        fileText.setText("hibernate.cfg.xml");*/
    }

    /**
     * Ensures that contents is ok.
     */
    private void dialogChanged() {
        IResource container = ResourcesPlugin.getWorkspace().getRoot()
                .findMember(new Path(getContainerName() ) );
        String fileName = getFileName();

        if (getContainerName().length() == 0) {
            updateStatus(HibernateConsoleMessages.NewConfigurationWizardPage_file_container_must_be_specified);
            return;
        }
        if (container == null
                || (container.getType() & (IResource.PROJECT | IResource.FOLDER) ) == 0) {
            updateStatus(HibernateConsoleMessages.NewConfigurationWizardPage_file_container_must_exist);
            return;
        }

        if (!container.isAccessible() ) {
            updateStatus(HibernateConsoleMessages.NewConfigurationWizardPage_project_must_be_writable);
            return;
        }

        if (fileName.length() == 0) {
            updateStatus(HibernateConsoleMessages.NewConfigurationWizardPage_file_name_must_be_specified);
            return;
        }
        if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
            updateStatus(HibernateConsoleMessages.NewConfigurationWizardPage_file_name_must_be_valid);
            return;
        }

        // TODO: check for driver class availability.
        updateStatus(null);
    }

    private void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null && beenShown);
    }

    private String getContainerName() {
        return containerText.getText();
    }

    private String getFileName() {
        return fileText.getText();
    }

    /**
     * @return
     */
    public String getSessionFactoryName() {
        return nullIfEmpty(sessionFactoryNameText.getText() );
    }

    /**
     * @param text
     * @return
     */
    private String nullIfEmpty(String text) {
        if (text != null && text.trim().length() > 0) {
            return text.trim();
        }
        return null;
    }

    /**
     * @return
     */
    public String getDialect() {
    	return nullIfEmpty(helper.getDialectClass(dialectCombo.getText() ) );
    }

    /**
     * @return
     */
    public String getDriver() {
        return nullIfEmpty(driver_classCombo.getText() );
    }

    /**
     * @return
     */
    public String getConnectionURL() {
        return nullIfEmpty(urlCombo.getText() );
    }

    /**
     * @return
     */
    public String getUsername() {
        return nullIfEmpty(usernameText.getText() );
    }

    /**
     * @return
     */
    public String getPassword() {
        return nullIfEmpty(passwordText.getText() );
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
     */
    public void setVisible(boolean visible) {
        containerText.setText(fileCreation.getContainerFullPath().toPortableString() );
        fileText.setText(fileCreation.getFileName() );
        super.setVisible(visible);
        if(visible) {
            sessionFactoryNameText.setFocus();
        }
        beenShown = true;
        dialogChanged();
    }



	public boolean isCreateConsoleConfigurationEnabled() {
		return createConsoleConfiguration.getSelection();
	}

	public void setCreateConsoleConfigurationVisible(boolean isVisible) {
		// update visibility if button was created
		if (createConsoleConfiguration != null){
			createConsoleConfiguration.setVisible(isVisible);
		}
	}

	public String getDefaultCatalog() {
		return nullIfEmpty(defaultCatalogText.getText());
	}

	public String getDefaultSchema() {
		return nullIfEmpty(defaultSchemaText.getText());
	}

	public String getConnectionProfileName() {
		return defaultConnectionProfile;
	}

	public void setConnectionProfileName(String selectedConnectionName) {
		this.defaultConnectionProfile = selectedConnectionName;
	}
	
    
    private void fillPropertiesFromConnectionProfile(String cpName){
		IConnectionProfile profile = ProfileManager.getInstance().getProfileByName(cpName);
		Properties p = ConnectionProfileUtil.getHibernateConnectionProperties(profile);
		driver_classCombo.setText(p.getProperty(Environment.DRIVER));
		urlCombo.setText(p.getProperty(Environment.URL));
		if (p.containsKey(Environment.USER)){
			 usernameText.setText(p.getProperty(Environment.USER));
		}
		if (p.containsKey(Environment.PASS)){
			 passwordText.setText(p.getProperty(Environment.PASS));
		}
		/*this causes very long timeouts when db is not started
		String dialect = ConnectionProfileUtil.autoDetectDialect(p);
		if (dialect != null){
			dialectCombo.setText(dialect);
		}*/
    }
}