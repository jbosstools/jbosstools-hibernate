package org.hibernate.eclipse.console.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
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

    private Combo urlCombo;

    private ISelection selection;

    private final WizardNewFileCreationPage fileCreation;

    private boolean beenShown = false;

    /**
     * Constructor for SampleNewWizardPage.
     * @param page 
     * 
     * @param pageName
     */
    public NewConfigurationWizardPage(ISelection selection, WizardNewFileCreationPage page) {
        super("wizardPage");
        this.fileCreation = page;
        setTitle("Hibernate Configuration File (cfg.xml)");
        setDescription("This wizard creates a new configuration file to use with Hibernate.");
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

        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 9;
        Label label = new Label(container, SWT.NULL);
        label.setText("&Container:");

        containerText = new Label(container, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        containerText.setLayoutData(gd);        
        fillLabel(container);
        
        label = new Label(container, SWT.NULL);
        label.setText("&File name:");

        fileText = new Label(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        fileText.setLayoutData(gd);
        
        fillLabel(container);

        label = new Label(container, SWT.NULL);
        label.setText("&Session factory name:");
        sessionFactoryNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        sessionFactoryNameText.setLayoutData(gd);
        sessionFactoryNameText.addModifyListener(listener);
        fillLabel(container);

        label = new Label(container, SWT.NULL);
        label.setText("&Database dialect:");
        dialectCombo = new Combo(container, SWT.NULL);
        fillHerUp(dialectCombo, helper.getDialectNames());
        dialectCombo.select(0);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
        dialectCombo.setLayoutData(gd);
        dialectCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                String[] driverClasses = helper.getDriverClasses(helper
                        .getDialectClass(dialectCombo.getText()));
                fillHerUp(driver_classCombo, driverClasses);
                dialogChanged();
            }
        });
        fillLabel(container);

        label = new Label(container, SWT.NULL);
        label.setText("&Driver class:");
        driver_classCombo = new Combo(container, SWT.NULL);
        driver_classCombo.select(0);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
        driver_classCombo.setLayoutData(gd);
        driver_classCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                String[] connectionURLS = helper
                        .getConnectionURLS(driver_classCombo.getText());
                fillHerUp(urlCombo, connectionURLS);
                dialogChanged();
            }
        });
        fillLabel(container);

        label = new Label(container, SWT.NULL);
        label.setText("Connection &URL:");
        urlCombo = new Combo(container, SWT.NULL);
        urlCombo.select(0);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
        urlCombo.setLayoutData(gd);
        urlCombo.addModifyListener(listener);
        fillLabel(container);

        label = new Label(container, SWT.NULL);
        label.setText("User &name:");
        usernameText = new Text(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        usernameText.setLayoutData(gd);
        usernameText.addModifyListener(listener);
        fillLabel(container);

        label = new Label(container, SWT.NULL);
        label.setText("&Password:");
        passwordText = new Text(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        passwordText.setLayoutData(gd);
        passwordText.addModifyListener(listener);
        fillLabel(container);

        initialize();
        dialogChanged();
        
        setControl(container);        
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
        new Label(container, SWT.NULL);
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
                    container = ((IResource) obj).getParent();
                containerText.setText(container.getFullPath().toString());
            }
        }
        fileText.setText("hibernate.cfg.xml");*/
    }

    /**
     * Ensures that contents is ok.
     */
    private void dialogChanged() {
        IResource container = ResourcesPlugin.getWorkspace().getRoot()
                .findMember(new Path(getContainerName()));
        String fileName = getFileName();

        if (getContainerName().length() == 0) {
            updateStatus("File container must be specified");
            return;
        }
        if (container == null
                || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
            updateStatus("File container must exist");
            return;
        }        
        
        if (!container.isAccessible()) {
            updateStatus("Project must be writable");
            return;
        }
                
        if (fileName.length() == 0) {
            updateStatus("File name must be specified");
            return;
        }
        if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
            updateStatus("File name must be valid");
            return;
        }
        if (!fileName.endsWith(".cfg.xml")) {
            updateStatus("File extension must be \"cfg.xml\"");
            return;
        }

        IFile file = ((IContainer) container).getFile(new Path(fileName));
        if(file.exists()) {
            updateStatus("File already exists");
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
        return nullIfEmpty(sessionFactoryNameText.getText());
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
        return nullIfEmpty(dialectCombo.getText());
    }

    /**
     * @return
     */
    public String getDriver() {
        return nullIfEmpty(driver_classCombo.getText());
    }

    /**
     * @return
     */
    public String getConnectionURL() {
        return nullIfEmpty(urlCombo.getText());
    }

    /**
     * @return
     */
    public String getUsername() {
        return nullIfEmpty(usernameText.getText());
    }

    /**
     * @return
     */
    public String getPassword() {
        return nullIfEmpty(passwordText.getText());
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
     */
    public void setVisible(boolean visible) {       
        containerText.setText(fileCreation.getContainerFullPath().toPortableString());
        fileText.setText(fileCreation.getFileName());        
        super.setVisible(visible);
        if(visible) {
            sessionFactoryNameText.setFocus();
        }
        beenShown = true;        
        dialogChanged();               
    }
}