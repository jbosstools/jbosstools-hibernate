/*******************************************************************************
  * Copyright (c) 2008-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jpt.core.JpaProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences.ConfigurationMode;
import org.hibernate.eclipse.console.EclipseConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.utils.DialogSelectionHelper;
import org.hibernate.eclipse.console.utils.DriverClassHelpers;
import org.hibernate.eclipse.console.utils.LaunchHelper;
import org.hibernate.eclipse.launch.PathHelper;
import org.hibernate.tool.hbm2x.StringUtils;
import org.hibernate.util.StringHelper;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.ui.HibernateJptUIPlugin;

/**
 * @author Dmitry Geraskov
 *
 */
public abstract class GenerateInitWizardPage extends WizardPage {
	
	private static final String AUTODETECT = Messages.GenerateInitWizardPage_autodetect;
	
	private DriverClassHelpers helper = new DriverClassHelpers();
	
	private StringButtonDialogField outputdir;
	
	private ComboDialogField connectionProfileName;
	
	private StringButtonDialogField schemaName;
	
	private ComboDialogField consoleConfigurationName;
	
	private ComboDialogField dialectName;
	
	private Button selectMethod;
	
	private Group dbGroup;

	private HibernateJpaProject jpaProject;
	
	protected int numColumns = 3;
	
	protected IDialogFieldListener fieldlistener = new IDialogFieldListener() {
		public void dialogFieldChanged(DialogField field) {
			dialogChanged();
		}
	};
	
	public GenerateInitWizardPage(HibernateJpaProject jpaProject){
		super("", Messages.GenerateInitWizardPage_title, null); //$NON-NLS-1$
		this.jpaProject = jpaProject;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();		

		container.setLayout(layout);
		layout.numColumns = numColumns;
		layout.verticalSpacing = 10;		
		
		outputdir = new StringButtonDialogField(new IStringButtonAdapter() {
			public void changeControlPressed(DialogField field) {
				IPath[] paths = DialogSelectionHelper.chooseFolderEntries(getShell(),  PathHelper.pathOrNull(outputdir.getText()), HibernateConsoleMessages.CodeGenerationSettingsTab_select_output_dir, HibernateConsoleMessages.CodeGenerationSettingsTab_choose_dir_for_generated_files, false);
				if(paths!=null && paths.length==1) {
					outputdir.setText( ( (paths[0]).toOSString() ) );
				}
			}
		});
		outputdir.setText(getDefaultOutput());
        outputdir.setDialogFieldListener(fieldlistener);
		outputdir.setLabelText(HibernateConsoleMessages.CodeGenerationSettingsTab_output_dir);
		outputdir.setButtonLabel(HibernateConsoleMessages.CodeGenerationSettingsTab_browse);
		
		Control[] controls = outputdir.doFillIntoGrid(container, numColumns);
		// Hack to tell the text field to stretch!
		( (GridData)controls[1].getLayoutData() ).grabExcessHorizontalSpace = true;
		setPageComplete(!StringHelper.isEmpty(getOutputDir()));
		
		createChildControls(container);
		
		selectMethod = new Button(container, SWT.CHECK);
		selectMethod.setText(Messages.GenerateInitWizardPage_use_console_configuration);
		selectMethod.setSelection(true);
		selectMethod.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);				
			}

			public void widgetSelected(SelectionEvent e) {
				consoleConfigurationName.setEnabled(selectMethod.getSelection());
				connectionProfileName.setEnabled(!selectMethod.getSelection());
				schemaName.setEnabled(!selectMethod.getSelection());
				dialectName.setEnabled(!selectMethod.getSelection());
				dialogChanged();		
			}});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		selectMethod.setLayoutData(gd);				
		
		consoleConfigurationName = new ComboDialogField(SWT.READ_ONLY);
		consoleConfigurationName.setLabelText(HibernateConsoleMessages.CodeGenerationSettingsTab_console_configuration);
		ConsoleConfiguration[] cfg = LaunchHelper.findFilteredSortedConsoleConfigs();
		String[] names = new String[cfg.length];
		for (int i = 0; i < cfg.length; i++) {
			ConsoleConfiguration configuration = cfg[i];
			names[i] = configuration.getName();
		}
		consoleConfigurationName.setItems(names);
		consoleConfigurationName.setText(jpaProject.getDefaultConsoleConfigurationName());
        consoleConfigurationName.setDialogFieldListener(fieldlistener);
        consoleConfigurationName.doFillIntoGrid(container, numColumns);        

        createDBGroup(container, numColumns);        

		setControl(container);
		
		if (StringHelper.isEmpty(consoleConfigurationName.getText())) {
				setPageComplete(false);
		}
	}
	
	/**
	 * @param parent
	 */
	protected abstract void createChildControls(Composite parent);


	/**
	 * @param container
	 * @param colCount
	 */
	private void createDBGroup(Composite container, int numColumns) {
		dbGroup = new Group(container, SWT.FILL);
		dbGroup.setLayout(new FillLayout());
		GridLayout layout = new GridLayout();
		dbGroup.setLayout(layout);
		layout.numColumns = numColumns;
		layout.verticalSpacing = 10;
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		dbGroup.setLayoutData(gd);
		dbGroup.setText(Messages.GenerateInitWizardPage_databaseSettings);		
		
		//****************************connection profile*****************
		connectionProfileName = new ComboDialogField(SWT.READ_ONLY);
		connectionProfileName.setLabelText(Messages.GenerateInitWizardPage_databaseSettings_connection);		
				
		connectionProfileName.setItems(dtpConnectionProfileNames());

		String connectionName = getProjectConnectionProfileName();
		if (!StringUtils.isEmpty(connectionName)) {
			connectionProfileName.selectItem(connectionName);
		}
		connectionProfileName.doFillIntoGrid(dbGroup, numColumns);
		connectionProfileName.setDialogFieldListener(fieldlistener);
		connectionProfileName.setEnabled(!selectMethod.getSelection());
		//****************************dialect*****************
		dialectName = new ComboDialogField(SWT.NONE);
		dialectName.setLabelText(HibernateConsoleMessages.NewConfigurationWizardPage_database_dialect);
		dialectName.setItems(getDialectNames());
		dialectName.selectItem(0);
		dialectName.doFillIntoGrid(dbGroup, numColumns);
		dialectName.setEnabled(false);
		dialectName.setDialogFieldListener(fieldlistener);	
		//****************************schema*****************
		schemaName = new StringButtonDialogField(new IStringButtonAdapter(){
			public void changeControlPressed(DialogField field) {
				// TODO Auto-generated method stub
				
			}});
		schemaName.setLabelText(Messages.GenerateInitWizardPage_databaseShema);
		schemaName.setButtonLabel(Messages.GenerateInitWizardPage_refresh);
		Control[] controls = schemaName.doFillIntoGrid(dbGroup, numColumns);
		// Hack to tell the text field to stretch!
		( (GridData)controls[1].getLayoutData() ).grabExcessHorizontalSpace = true;		
		schemaName.setEnabled(!selectMethod.getSelection());
	}

	
	protected void dialogChanged() {
		
		String msg = PathHelper.checkDirectory(getOutputDir(), HibernateConsoleMessages.CodeGenerationSettingsTab_output_directory, false);

        if (msg!=null) {
        	setErrorMessage( msg );
        	setPageComplete( false );
            return;
        }
		
		if (selectMethod.getSelection() && (StringHelper.isEmpty(getConfigurationName()))){
			setPageComplete(false);
			setErrorMessage(Messages.GenerateInitWizardPage_err_msg_select_console_configuration);
			return;
		}
		if (!selectMethod.getSelection() && (StringHelper.isEmpty(getConnectionProfileName()))){
			setPageComplete(false);
			setErrorMessage(Messages.GenerateInitWizardPage_err_msg_select_connection_profile);
			return;
		}
		
		setPageComplete(true);
	}
	
	public String getOutputDir(){
		return outputdir.getText();
	}
	
	private String[] dtpConnectionProfileNames() {
		List<String> list = new ArrayList<String>();

		IConnectionProfile[] cps = ProfileManager.getInstance().getProfiles();
		for (int i = 0; i < cps.length; i++) {
			list.add(cps[i].getName());			
		}
		Collections.sort(list);
		return list.toArray(new String[list.size()]);
	}
	
	private String getProjectConnectionProfileName() {
		return jpaProject.getDataSource().getConnectionProfileName();
	}
	
	public String getConfigurationName() {
		if (selectMethod.getSelection())
			return consoleConfigurationName.getText();
		return createConsoleConfiguration();
	}
	
	public String getConnectionProfileName() {
		return connectionProfileName.getText();
	}
	
	private String createConsoleConfiguration(){		
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		String dialect = determineDialect();
		String ccName = launchManager.generateUniqueLaunchConfigurationNameFrom(HibernateConsoleMessages.AddConfigurationAction_hibernate);
		ConsoleConfigurationPreferences prefs = new EclipseConsoleConfigurationPreferences(ccName, 
				ConfigurationMode.JPA, jpaProject.getName(), true, 
				null, null, null, 
				new IPath[0], new IPath[0], null, null,
				getConnectionProfileName(), dialect);
		
		ConsoleConfiguration cc = new ConsoleConfiguration(prefs);
		KnownConfigurations.getInstance().addConfiguration(cc, false);
		
		return ccName;		
	}
	
	public boolean isTemporaryConfiguration(){
		return !selectMethod.getSelection();
	}
	
	public JpaProject getJpaProject(){
		return jpaProject;
	}
	
	public void setWarningMessage(String warning){
		setMessage(warning, WARNING); 
	}
	
	protected String getDefaultOutput(){
		try{
			if (getJpaProject() == null) return ""; //$NON-NLS-1$
			if (getJpaProject().getJavaProject() == null) return ""; //$NON-NLS-1$
			if (!getJpaProject().getJavaProject().exists()) return ""; //$NON-NLS-1$
			IPackageFragmentRoot[] roots = getJpaProject().getJavaProject().getPackageFragmentRoots();
			for (int i = 0; i < roots.length; i++) {
				IPackageFragmentRoot root = roots[i];
				if (root.getClass() == PackageFragmentRoot.class) {
					if (root.exists()) return root.getResource().getFullPath().toOSString();					
				}							
			}
			return getJpaProject().getJavaProject().getResource().getFullPath().toOSString();
		} catch(JavaModelException e){
			HibernateJptUIPlugin.logException(e);
			return ""; //$NON-NLS-1$
		}
	}
	
	private String[] getDialectNames(){
		String[] dialectNames1 = helper.getDialectNames();
		String[] dialectNames2 = new String[dialectNames1.length + 1];
		dialectNames2[0] = AUTODETECT;
		System.arraycopy(dialectNames1, 0, dialectNames2, 1, dialectNames1.length);
		return dialectNames2;
	}
	
	private String determineDialect() {
		if (!AUTODETECT.equals(dialectName.getText())){
			return helper.getDialectClass(dialectName.getText());
		}
		if (!selectMethod.getSelection()){
			IConnectionProfile profile = ProfileManager.getInstance().getProfileByName(getConnectionProfileName());
			String driver = profile.getProperties(profile.getProviderId()).getProperty("org.eclipse.datatools.connectivity.db.driverClass"); //$NON-NLS-1$
			return helper.getDialect(driver);
		}
		return null;
	}
}
