/**
 *
 */
package org.hibernate.eclipse.launch;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences.ConfigurationMode;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.DialogSelectionHelper;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.console.wizards.NewConfigurationWizard;
import org.hibernate.eclipse.console.wizards.NewConfigurationWizardPage;
import org.hibernate.util.StringHelper;

public class ConsoleConfigurationMainTab extends ConsoleConfigurationTab {



	private Button coreMode;
	private Button jpaMode;
	private Button annotationsMode;
	private Button confbutton;

	private Text propertyFileText;
	private Text configurationFileText;
	private Text projectNameText;
	private Text persistenceUnitNameText;

	public String getName() {
		return HibernateConsoleMessages.ConsoleConfigurationMainTab_main;
	}

	public void createControl(Composite parent) {
		Font font = parent.getFont();
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 0;
		comp.setLayout(layout);
		comp.setFont(font);

		createConfigurationMode( comp );

		createProjectEditor( comp );

		createPropertyFileEditor(comp);
		createConfigurationFileEditor(comp);

		createPersistenceUnitEditor( comp );


	}

	private void createConfigurationMode(Composite container) {
		Group group = createGroup( container, HibernateConsoleMessages.ConsoleConfigurationMainTab_type);
		group.setLayout( new RowLayout( SWT.HORIZONTAL ) );
		coreMode = new Button(group, SWT.RADIO);
		coreMode.setText(HibernateConsoleMessages.ConsoleConfigurationMainTab_core);
		coreMode.addSelectionListener( getChangeListener() );
		coreMode.setSelection( true );
		annotationsMode = new Button(group, SWT.RADIO);
		annotationsMode.setText(HibernateConsoleMessages.ConsoleConfigurationMainTab_annotations);
		annotationsMode.addSelectionListener( getChangeListener() );
		jpaMode = new Button(group, SWT.RADIO);
		jpaMode.setText(HibernateConsoleMessages.ConsoleConfigurationMainTab_jpa);
		jpaMode.addSelectionListener( getChangeListener() );
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		group.setLayoutData( gd );
	}

	protected void createProjectEditor(Composite parent) {
		Group group = createGroup( parent, HibernateConsoleMessages.ConsoleConfigurationMainTab_project );
		projectNameText = createBrowseEditor( parent, group);
		createBrowseButton( group, new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleProjectBrowse();
			}
		} );
	}

	private void createPropertyFileEditor(Composite parent) {
		Group group = createGroup( parent, HibernateConsoleMessages.ConsoleConfigurationMainTab_property_file );
		propertyFileText = createBrowseEditor( parent, group);
		createSetupButton( group, new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handlePropertyFileSetup();
			}
		} );
	}


	private void createConfigurationFileEditor(Composite parent) {
		Group group = createGroup( parent, HibernateConsoleMessages.ConsoleConfigurationMainTab_configuration_file );
		configurationFileText = createBrowseEditor( parent, group);
		confbutton = createSetupButton( group, new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleConfigurationFileSetup();
			}
		});
	}


	private void createPersistenceUnitEditor(Composite parent) {
		Group group = createGroup( parent, HibernateConsoleMessages.ConsoleConfigurationMainTab_persistence_unit );
		persistenceUnitNameText = new Text(group, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		persistenceUnitNameText.setFont( parent.getFont() );
		persistenceUnitNameText.setLayoutData(gd);
		persistenceUnitNameText.addModifyListener(getChangeListener());
	}


	public void performApply(ILaunchConfigurationWorkingCopy configuration) {

		configuration.setAttribute( IConsoleConfigurationLaunchConstants.CONFIGURATION_FACTORY, getConfigurationMode().toString());
		configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, nonEmptyTrimOrNull( projectNameText ));
		configuration.setAttribute(IConsoleConfigurationLaunchConstants.PROPERTY_FILE, nonEmptyTrimOrNull(propertyFileText));
		configuration.setAttribute(IConsoleConfigurationLaunchConstants.CFG_XML_FILE, nonEmptyTrimOrNull(configurationFileText));
		configuration.setAttribute( IConsoleConfigurationLaunchConstants.PERSISTENCE_UNIT_NAME, nonEmptyTrimOrNull(persistenceUnitNameText));

	}

	public void initializeFrom(ILaunchConfiguration configuration) {

		try {
			ConfigurationMode cm = ConfigurationMode.parse(configuration.getAttribute( IConsoleConfigurationLaunchConstants.CONFIGURATION_FACTORY, "" )); //$NON-NLS-1$
			coreMode.setSelection( cm.equals( ConfigurationMode.CORE ) );
			annotationsMode.setSelection( cm.equals( ConfigurationMode.ANNOTATIONS ) );
			jpaMode.setSelection( cm.equals( ConfigurationMode.JPA ) );

			projectNameText.setText( configuration.getAttribute( IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "" ) ); //$NON-NLS-1$
			propertyFileText.setText( configuration.getAttribute( IConsoleConfigurationLaunchConstants.PROPERTY_FILE, "" ) ); //$NON-NLS-1$
			configurationFileText.setText( configuration.getAttribute( IConsoleConfigurationLaunchConstants.CFG_XML_FILE, "" )); //$NON-NLS-1$
			persistenceUnitNameText.setText( configuration.getAttribute( IConsoleConfigurationLaunchConstants.PERSISTENCE_UNIT_NAME, "" )); //$NON-NLS-1$
		}
		catch (CoreException e) {
			HibernateConsolePlugin.getDefault().log( e );
		}
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {}

	private void handlePropertyFileSetup() {
		int defaultChoice = 0;
		IPath initialPath = getPropertyFilePath() != null ? getPropertyFilePath() : null;

		if(initialPath!=null) {
    		defaultChoice = 1;
    	}
		MessageDialog dialog = createSetupDialog(HibernateConsoleMessages.ConsoleConfigurationMainTab_setup_property_file, HibernateConsoleMessages.ConsoleConfigurationMainTab_do_you_want_to_create_new_property_file, defaultChoice);

		int answer = dialog.open();
		if(answer==0) { // create new
			handlePropertyFileCreate();
		} else if (answer==1) { // use existing
			handlePropertyFileBrowse();
		}
	}


	private void handlePropertyFileBrowse() {
		IPath initialPath = getPropertyFilePath() != null ? getPropertyFilePath() : new Path(getProjectName());
		IPath[] paths = org.hibernate.eclipse.console.utils.xpl.DialogSelectionHelper.chooseFileEntries(getShell(),  initialPath, new IPath[0], HibernateConsoleMessages.ConsoleConfigurationMainTab_select_propertyfile, HibernateConsoleMessages.ConsoleConfigurationMainTab_choose_file_to_use_as_hibernate_properties, new String[] {HibernateConsoleMessages.ConsoleConfigurationMainTab_properties}, false, false, true);
		if(paths!=null && paths.length==1) {
			propertyFileText.setText( (paths[0]).toOSString() );
		}
	}

	private void handleProjectBrowse() {
		IJavaProject paths = DialogSelectionHelper.chooseJavaProject( getShell(), findJavaProject(), HibernateConsoleMessages.ConsoleConfigurationMainTab_select_java_project, HibernateConsoleMessages.ConsoleConfigurationMainTab_java_project_to_determine_default_classpath );
		if(paths!=null) {
			projectNameText.setText( paths.getProject().getName() );
		} else {
			projectNameText.setText(""); //$NON-NLS-1$
		}
	}

	private IJavaProject findJavaProject(){
		IPath path = pathOrNull(getProjectName());
		if (path != null && path.segmentCount() >= 1){
			String projectName = path.segment(0);
			return ProjectUtils.findJavaProject( projectName );
		}
		return null;
	}

	private void handlePropertyFileCreate() {
		Wizard wizard = new Wizard(){

			String pageName = HibernateConsoleMessages.ConsoleConfigurationMainTab_create_property_file;

			WizardNewFileCreationPage cPage = null;

			@Override
			public void addPages() {
				StructuredSelection selection = null;
				IJavaProject project = findJavaProject();
				if (project != null){
					selection = new StructuredSelection(project);
				} else {
					selection = StructuredSelection.EMPTY;
				}
				cPage = new WizardNewFileCreationPage(pageName, selection);
				cPage.setTitle( HibernateConsoleMessages.ConsoleConfigurationMainTab_create_hibernate_properties_file );
			    cPage.setDescription( HibernateConsoleMessages.ConsoleConfigurationMainTab_create_new_properties_file );
			    cPage.setFileName("hibernate.properties"); //$NON-NLS-1$
			    addPage( cPage );
			}

			@Override
			public boolean performFinish() {
				final IFile file = cPage.createNewFile();
				propertyFileText.setText( file.getFullPath().toOSString() );
				return true;
			}};

			IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			WizardDialog wdialog = new WizardDialog(win.getShell(), wizard);
			wdialog.open();
	}

	private MessageDialog createSetupDialog(String title, String question, int defaultChoice){
		return new MessageDialog(getShell(),
				title,
				null,
				question,
				MessageDialog.QUESTION,
				new String[] { HibernateConsoleMessages.ConsoleConfigurationMainTab_create_new, HibernateConsoleMessages.ConsoleConfigurationMainTab_use_existing, IDialogConstants.CANCEL_LABEL},
				defaultChoice);
	}

	private void handleConfigurationFileSetup() {
		int defaultChoice = 0;
		IPath initialPath = getConfigurationFilePath() != null ? getConfigurationFilePath() : null;

		if(initialPath!=null) {
    		defaultChoice = 1;
    	}
		MessageDialog dialog = createSetupDialog(HibernateConsoleMessages.ConsoleConfigurationMainTab_setup_configuration_file, HibernateConsoleMessages.ConsoleConfigurationMainTab_do_you_want_to_create_new_cfgxml, defaultChoice);
		int answer = dialog.open();
		if(answer==0) { // create new
			handleConfigurationFileCreate();
		} else if (answer==1) { // use existing
			handleConfigurationFileBrowse();
		}
	}

	private void handleConfigurationFileBrowse() {
		IPath initialPath = getConfigurationFilePath() != null ? getConfigurationFilePath() : new Path(getProjectName());
		IPath[] paths = DialogSelectionHelper.chooseFileEntries(getShell(),  initialPath, new IPath[0], HibernateConsoleMessages.ConsoleConfigurationMainTab_select_hibernate_cfg_xml_file, HibernateConsoleMessages.ConsoleConfigurationMainTab_choose_file_to_use_as_hibernate_cfg_xml, new String[] {HibernateConsoleMessages.ConsoleConfigurationMainTab_cfg_xml}, false, false, true);
		if(paths!=null && paths.length==1) {
			configurationFileText.setText( (paths[0]).toOSString() );
		}
	}

	private void handleConfigurationFileCreate() {
		StructuredSelection selection = null;
		IJavaProject project = findJavaProject();
		if (project != null){
			selection = new StructuredSelection(project);
		} else {
			selection = StructuredSelection.EMPTY;
		}
		NewConfigurationWizard wizard = new NewConfigurationWizard();
		wizard.init(PlatformUI.getWorkbench(), selection );
		IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		WizardDialog wdialog = new WizardDialog(win.getShell(), wizard);
		wdialog.create();
		IWizardPage configPage = wizard.getPage(HibernateConsoleMessages.ConsoleConfigurationMainTab_wizard_page);
		if (configPage != null && configPage instanceof NewConfigurationWizardPage){
			((NewConfigurationWizardPage)configPage).setCreateConsoleConfigurationVisible(false);
		}
		// This opens a dialog
		if (wdialog.open() == Window.OK){
			WizardNewFileCreationPage createdFilePath = ((WizardNewFileCreationPage)wizard.getStartingPage());
			if(createdFilePath!=null) {
				// createNewFile() does not creates new file if it was created by wizard (OK was pressed)
				configurationFileText.setText(createdFilePath.createNewFile().getFullPath().toOSString());
			}
		}
	}


	private Path pathOrNull(String p) {
		if(p==null || p.trim().length()==0) {
			return null;
		} else {
			return new Path(p);
		}
	}

	public Path getConfigurationFilePath() {
		return pathOrNull(configurationFileText.getText() );
	}

	public Path getPropertyFilePath() {
		String p = propertyFileText.getText();
		return pathOrNull(p);
	}

	public boolean isValid(ILaunchConfiguration launchConfig) {
		setErrorMessage( null );
		setMessage( null );
		String propertyFilename = propertyFileText.getText();
		String configurationFilename = configurationFileText.getText();

		configurationFileText.setEnabled( /* TODO !configurationFileWillBeCreated && */ !getConfigurationMode().equals( ConfigurationMode.JPA ) );
		confbutton.setEnabled( !getConfigurationMode().equals( ConfigurationMode.JPA ) );

		persistenceUnitNameText.setEnabled( getConfigurationMode().equals( ConfigurationMode.JPA) );

		if(getProjectName()!=null && StringHelper.isNotEmpty(getProjectName().trim())) {
			Path projectPath = new Path(getProjectName());
			if (projectPath.segmentCount() > 1){
				setErrorMessage(HibernateConsoleMessages.ConsoleConfigurationMainTab_path_for_project_must_have_only_one_segment);
				return false;
			}
			IJavaProject findJavaProject = ProjectUtils.findJavaProject( getProjectName() );
			if(findJavaProject==null || !findJavaProject.exists()) {
				String out = NLS.bind(HibernateConsoleMessages.ConsoleConfigurationMainTab_the_java_project_does_not_exist, getProjectName());
				setErrorMessage(out);
				return false;
			}
		}

		/* TODO: warn about implicit behavior of loading /hibernate.cfg.xml, /hibernate.properties and /META-INF/persistence.xml
		 * if (propertyFilename.length() == 0 && configurationFilename.trim().length() == 0) {
			setErrorMessage("Property or Configuration file must be specified");
			return;
		} */

		if (propertyFilename.length() > 0) {
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(propertyFilename);
			String msg = checkForFile(HibernateConsoleMessages.ConsoleConfigurationMainTab_property_file_2, resource);
			if(msg!=null) {
				setErrorMessage(msg);
				return false;
			}
		}

		if (/*!configurationFileWillBeCreated &&*/ configurationFilename.length() > 0) {
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(configurationFilename);
			String msg = checkForFile(HibernateConsoleMessages.ConsoleConfigurationMainTab_configuration_file_2,resource);
			if(msg!=null) {
				setErrorMessage(msg);
				return false;
			}
		}

		/*if((useProjectClassPath() && StringHelper.isEmpty( getProjectName() )) && classPathViewer.getTable().getItemCount()==0) {
			setErrorMessage( "Need to specify a project or setup a classpath" );
			return;
		}

		if((!useProjectClassPath() && classPathViewer.getTable().getItemCount()==0)) {
			setErrorMessage( "Need to specify a classpath when not using a project classpath" );
			return;
		} TODO*/

		return true;
	}

	private ConfigurationMode getConfigurationMode() {
		if(annotationsMode.getSelection()) {
			return ConfigurationMode.ANNOTATIONS;
		} else if(jpaMode.getSelection()) {
			return ConfigurationMode.JPA;
		} else {
			return ConfigurationMode.CORE;
		}
	}

	String getProjectName() {
		return projectNameText.getText();
	}

	private String checkForFile(String msgPrefix, IResource resource) {
		if(resource!=null) {
			if(resource instanceof IFile) {
				return null;
			} else {
				return msgPrefix + HibernateConsoleMessages.ConsoleConfigurationMainTab_is_not_file;
			}
		} else {
			return msgPrefix + HibernateConsoleMessages.ConsoleConfigurationMainTab_does_not_exist;
		}
	}

	public Image getImage() {
		return EclipseImages.getImage(ImageConstants.MINI_HIBERNATE);
	}


}