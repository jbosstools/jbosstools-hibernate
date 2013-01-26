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
package org.jboss.tools.hibernate.jpt.ui.internal.persistence.details;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jpt.common.ui.internal.listeners.SWTPropertyChangeListenerWrapper;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.SimpleListValueModel;
import org.eclipse.jpt.common.utility.internal.transformer.StringObjectTransformer;
import org.eclipse.jpt.common.utility.model.event.PropertyChangeEvent;
import org.eclipse.jpt.common.utility.model.listener.PropertyChangeListener;
import org.eclipse.jpt.common.utility.model.value.ModifiablePropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.hibernate.eclipse.console.FileFilter;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.utils.DriverClassHelpers;
import org.hibernate.eclipse.console.wizards.NewConfigurationWizard;
import org.hibernate.eclipse.console.wizards.NewConfigurationWizardPage;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.BasicHibernateProperties;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernatePropertiesComposite extends Pane<BasicHibernateProperties> {

	private Text cfgFile;

	DriverClassHelpers helper;

	/**
	 * @param subjectHolder
	 * @param container
	 * @param widgetFactory
	 */
	public HibernatePropertiesComposite(
			Pane<BasicHibernateProperties> parentPane, Composite parent) {
		super(parentPane, parent);
	}

	@Override
	protected void initializeLayout(Composite container) {
		GridLayout gl = new GridLayout(3, false);
		container.setLayout(gl);
		this.helper = new DriverClassHelpers();

		final SimpleListValueModel<String> lvmDialect = new SimpleListValueModel<String>(Arrays.asList(this.helper
				.getDialectNames()));
		PropertyValueModel<BasicHibernateProperties> p = getSubjectHolder();
		List<String> drivers = new ArrayList<String>();
		BasicHibernateProperties props = p.getValue();
		if (props != null) {
			String dialectClass = this.helper.getDialectClass(props.getDialect());
			String[] driverClasses = this.helper.getDriverClasses(dialectClass);
			drivers.addAll(Arrays.asList(driverClasses));
		}

		final SimpleListValueModel<String> lvmDriver = new SimpleListValueModel<String>(drivers);

		List<String> urls = new ArrayList<String>();
		if (props != null) {
			String driverClass = props.getDriver();
			String[] connectionURLS = this.helper.getConnectionURLS(driverClass);
			urls.addAll(Arrays.asList(connectionURLS));
		}
		final SimpleListValueModel<String> lvmUrl = new SimpleListValueModel<String>(urls);

		ModifiablePropertyValueModel<String> dialectHolder = buildDialectHolder();
		final ModifiablePropertyValueModel<String> driverHolder = buildDriverHolder();
		final ModifiablePropertyValueModel<String> urlHolder = buildUrlHolder();
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		
		this.addLabel(container, HibernateConsoleMessages.ConsoleConfigurationPropertySource_config_file + ':');
		this.cfgFile = this.addText(container, buildConfigFileHolder());
		Button b = this.addButton(container, HibernateConsoleMessages.CodeGenerationSettingsTab_setup, createSetupAction());
		cfgFile.setLayoutData(gd);		
//		Button b = addButton(section, HibernateConsoleMessages.CodeGenerationSettingsTab_setup, createSetupAction());
//		this.cfgFile = addLabeledText(container,
//				HibernateConsoleMessages.ConsoleConfigurationPropertySource_config_file + ':', buildConfigFileHolder(),
//				b, null);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		this.addLabel(container, HibernateConsoleMessages.NewConfigurationWizardPage_database_dialect);
		Control c = this.addEditableCombo(container, lvmDialect, dialectHolder, StringObjectTransformer.<String>instance(), (String)null);
		gd.horizontalSpan = 2;
		c.setLayoutData(gd);
		
		//		addLabeledEditableCombo(
//				section,
//				HibernateConsoleMessages.NewConfigurationWizardPage_database_dialect,
//				lvmDialect,
//				dialectHolder,
//				StringConverter.Default.<String>instance(),
//				null);

		this.addLabel(container, HibernateConsoleMessages.NewConfigurationWizardPage_driver_class);
		c = this.addEditableCombo(container, lvmDriver, driverHolder, StringObjectTransformer.<String>instance(), (String)null);
		c.setLayoutData(gd);
//		addLabeledEditableCombo(
//				section,
//				HibernateConsoleMessages.NewConfigurationWizardPage_driver_class,
//				lvmDriver,
//				driverHolder,
//				StringConverter.Default.<String>instance(),
//				null);

		this.addLabel(container, HibernateConsoleMessages.NewConfigurationWizardPage_connection_url);
		c = this.addEditableCombo(container, lvmUrl, urlHolder, StringObjectTransformer.<String>instance(), (String)null);
		c.setLayoutData(gd);
//		addLabeledEditableCombo(
//				section,
//				HibernateConsoleMessages.NewConfigurationWizardPage_connection_url,
//				lvmUrl,
//				urlHolder,
//				StringConverter.Default.<String>instance(),
//				null);

		dialectHolder.addPropertyChangeListener(PropertyValueModel.VALUE, new SWTPropertyChangeListenerWrapper(
				new PropertyChangeListener() {
					@Override
					public void propertyChanged(PropertyChangeEvent event) {
						String dialectClass = HibernatePropertiesComposite.this.helper.getDialectClass((String) event.getNewValue());
						String[] driverClasses = HibernatePropertiesComposite.this.helper.getDriverClasses(dialectClass);
						String driver = driverHolder.getValue();//save value
						lvmDriver.clear();
						lvmDriver.addAll(Arrays.asList(driverClasses));
						driverHolder.setValue(driver);		//restore value
					}
				}
			)
		);

		driverHolder.addPropertyChangeListener( PropertyValueModel.VALUE, new SWTPropertyChangeListenerWrapper(
				new PropertyChangeListener() {
					@Override
					public void propertyChanged(PropertyChangeEvent event) {
						String driverClass = (String) event.getNewValue();
						String[] connectionURLS = HibernatePropertiesComposite.this.helper.getConnectionURLS(driverClass);
						String url = urlHolder.getValue();//save value
						lvmUrl.clear();
						lvmUrl.addAll(Arrays.asList(connectionURLS));
						urlHolder.setValue(url);		//restore value
					}
				}
			) );

		this.addLabel(container, HibernateConsoleMessages.NewConfigurationWizardPage_default_schema);
		c = this.addText(container, buildSchemaDefaultHolder());
		c.setLayoutData(gd);
//		addLabeledText(
//				section,
//				HibernateConsoleMessages.NewConfigurationWizardPage_default_schema,
//				buildSchemaDefaultHolder());

		this.addLabel(container, HibernateConsoleMessages.NewConfigurationWizardPage_default_catalog);
		c = this.addText(container, buildCatalogDefaultHolder());
		c.setLayoutData(gd);
//		addLabeledText(
//				section,
//				HibernateConsoleMessages.NewConfigurationWizardPage_default_catalog,
//				buildCatalogDefaultHolder());

		this.addLabel(container, HibernateConsoleMessages.NewConfigurationWizardPage_user_name);
		c = this.addText(container, buildUsernameHolder());
		c.setLayoutData(gd);
//		addLabeledText(
//				section,
//				HibernateConsoleMessages.NewConfigurationWizardPage_user_name,
//				buildUsernameHolder());

		this.addLabel(container, HibernateConsoleMessages.NewConfigurationWizardPage_password);
		c = this.addText(container, buildPasswordHolder());
		c.setLayoutData(gd);
//		addLabeledText(
//				section,
//				HibernateConsoleMessages.NewConfigurationWizardPage_password,
//				buildPasswordHolder());
	}

	private IPath getConfigurationFilePath() {
		String filePath = cfgFile.getText().trim();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IFile[] files = root.findFilesForLocation(new Path(filePath));
		IPath path = null;
		if (files != null && files.length > 0){
			path = new Path(files[0].getProject().getName()).append(files[0].getProjectRelativePath());
			filePath = path.toString();
		}
		if (filePath.length() > 0 ){
			IPackageFragmentRoot[] allPackageFragmentRoots = getSourcePackageFragmentRoots();
			for (IPackageFragmentRoot iPackageFragmentRoot : allPackageFragmentRoots) {
				IResource sourceFolder = iPackageFragmentRoot.getResource();
				if (sourceFolder instanceof IContainer) {
					IContainer folder = (IContainer) sourceFolder;
					if (folder.findMember(filePath) != null){
						return folder.findMember(filePath).getFullPath();
					}
				}
			}
		}
		return path;
	}
	
	public IPackageFragmentRoot[] getSourcePackageFragmentRoots(){
		BasicHibernateProperties props = getSubject();
		if (props != null){
			IProject project = props.getJpaProject().getProject();
			IJavaProject jProject = JavaCore.create(project);
			if (jProject != null){
				try {
					IPackageFragmentRoot[] allPackageFragmentRoots = jProject.getAllPackageFragmentRoots();
					List<IPackageFragmentRoot> sources = new LinkedList<IPackageFragmentRoot>();
					for (IPackageFragmentRoot iPackageFragmentRoot : allPackageFragmentRoots) {
						if (!iPackageFragmentRoot.isArchive() && iPackageFragmentRoot.isOpen()){
							sources.add(iPackageFragmentRoot);
						}
					}
					return sources.toArray(new IPackageFragmentRoot[0]);
				} catch (JavaModelException e) {
					//ignore
				}
			}
		}
		return new IPackageFragmentRoot[0];
	}

	private Runnable createSetupAction() {
		return new Runnable() {
			@Override
			public void run() {
				IPath initialPath = getConfigurationFilePath();
				int defaultChoice = 0;
				if(initialPath!=null) {
		    		defaultChoice = 1;
		    	}
				MessageDialog dialog = createSetupDialog(HibernateConsoleMessages.ConsoleConfigurationMainTab_setup_configuration_file, HibernateConsoleMessages.ConsoleConfigurationMainTab_do_you_want_to_create_new_cfgxml, defaultChoice);
				int answer = dialog.open();
				IPath cfgFile = null;
				if(answer==0) { // create new
					cfgFile = handleConfigurationFileCreate();
				} else if (answer==1) { // use existing
					cfgFile = handleConfigurationFileBrowse();
				}
				if (cfgFile != null){
					HibernatePropertiesComposite.this.cfgFile.setText( makeClassPathRelative(cfgFile).toString() );
				}
			}
			
			protected IPath makeClassPathRelative(IPath cfgFile){
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				IResource res = root.findMember(cfgFile);
				if ( res != null && res.exists() && res.getType() == IResource.FILE) {
					IPackageFragmentRoot[] allPackageFragmentRoots = getSourcePackageFragmentRoots();
					for (IPackageFragmentRoot iPackageFragmentRoot : allPackageFragmentRoots) {
						if (iPackageFragmentRoot.getResource().getFullPath().isPrefixOf(cfgFile)){
							cfgFile = cfgFile.removeFirstSegments(iPackageFragmentRoot.getResource().getFullPath().segmentCount());
							return cfgFile;
						}
					}
				}
				return res.getLocation();
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

			private IPath handleConfigurationFileBrowse() {
				IPath[] paths = chooseFileEntries();
				if(paths!=null && paths.length==1) {
					return paths[0];
				}
				return null;
			}
			
			public IPath[] chooseFileEntries() {
				TypedElementSelectionValidator validator = new TypedElementSelectionValidator(new Class[]{IFile.class}, false);
				IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
				IResource focus= getConfigurationFilePath() != null ? root.findMember(getConfigurationFilePath()) : null;

				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(), new WorkbenchContentProvider(){
					public Object[] getElements(Object element) {
						IPackageFragmentRoot[] sourcePackageFragmentRoots = getSourcePackageFragmentRoots();
						IResource[] ress = new IResource[sourcePackageFragmentRoots.length];
						for (int i = 0; i < sourcePackageFragmentRoots.length; i++) {
							ress[i] = sourcePackageFragmentRoots[i].getResource();
						}
						return ress;
					}
				});
				dialog.setValidator(validator);
				dialog.setAllowMultiple(false);
				dialog.setTitle(HibernateConsoleMessages.ConsoleConfigurationMainTab_select_hibernate_cfg_xml_file);
				dialog.setMessage(HibernateConsoleMessages.ConsoleConfigurationMainTab_choose_file_to_use_as_hibernate_cfg_xml);
				dialog.addFilter(new FileFilter(new String[] {HibernateConsoleMessages.ConsoleConfigurationMainTab_cfg_xml}, null, true, false) );
				dialog.setInput(root);
				dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
				dialog.setInitialSelection(focus);

				if (dialog.open() == Window.OK) {
					Object[] elements= dialog.getResult();
					IPath[] res= new IPath[elements.length];
					for (int i= 0; i < res.length; i++) {
						IResource elem= (IResource)elements[i];
						res[i]= elem.getFullPath();
					}
					return res;
				}
				return null;
			}
			
			private IPath handleConfigurationFileCreate() {
				NewConfigurationWizard wizard = new NewConfigurationWizard();
				wizard.init(PlatformUI.getWorkbench(), StructuredSelection.EMPTY );
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
						return createdFilePath.createNewFile().getFullPath();
					}
				}
				return null;
			}
		};
	}

	private ModifiablePropertyValueModel<String> buildConfigFileHolder() {
		return new PropertyAspectAdapter<BasicHibernateProperties, String>(getSubjectHolder(),
				BasicHibernateProperties.CONFIG_FILE_PROPERTY) {
			@Override
			protected String buildValue_() {
				return this.subject.getConfigurationFile();
			}

			@Override
			protected void setValue_(String value) {
				if ("".equals(value))value = null;//$NON-NLS-1$
				this.subject.setConfigurationFile(value);
			}
		};
	}

	private ModifiablePropertyValueModel<String> buildDialectHolder() {
		return new PropertyAspectAdapter<BasicHibernateProperties, String>(getSubjectHolder(),
				BasicHibernateProperties.DIALECT_PROPERTY) {
			@Override
			protected String buildValue_() {
				return HibernatePropertiesComposite.this.helper.getShortDialectName(this.subject.getDialect());
			}

			@Override
			protected void setValue_(String value) {
				if ("".equals(value))value = null; //$NON-NLS-1$
				this.subject.setDialect(HibernatePropertiesComposite.this.helper.getDialectClass(value));
			}
		};
	}

	private ModifiablePropertyValueModel<String> buildDriverHolder() {
		return new PropertyAspectAdapter<BasicHibernateProperties, String>(getSubjectHolder(),
				BasicHibernateProperties.DRIVER_PROPERTY) {
			@Override
			protected String buildValue_() {
				return this.subject.getDriver();
			}

			@Override
			protected void setValue_(String value) {
				if ("".equals(value))value = null;//$NON-NLS-1$
				this.subject.setDriver(value);
			}
		};
	}

	private ModifiablePropertyValueModel<String> buildUrlHolder() {
		return new PropertyAspectAdapter<BasicHibernateProperties, String>(getSubjectHolder(),
				BasicHibernateProperties.URL_PROPERTY) {
			@Override
			protected String buildValue_() {
				return this.subject.getUrl();
			}

			@Override
			protected void setValue_(String value) {
				if ("".equals(value))value = null;//$NON-NLS-1$
				this.subject.setUrl(value);
			}
		};
	}

	private ModifiablePropertyValueModel<String> buildSchemaDefaultHolder() {
		return new PropertyAspectAdapter<BasicHibernateProperties, String>(getSubjectHolder(),
				BasicHibernateProperties.SCHEMA_DEFAULT_PROPERTY) {
			@Override
			protected String buildValue_() {
				return this.subject.getSchemaDefault();
			}

			@Override
			protected void setValue_(String value) {
				if ("".equals(value))value = null;//$NON-NLS-1$
				this.subject.setSchemaDefault(value);
			}
		};
	}

	private ModifiablePropertyValueModel<String> buildCatalogDefaultHolder() {
		return new PropertyAspectAdapter<BasicHibernateProperties, String>(getSubjectHolder(),
				BasicHibernateProperties.CATALOG_DEFAULT_PROPERTY) {
			@Override
			protected String buildValue_() {
				return this.subject.getCatalogDefault();
			}

			@Override
			protected void setValue_(String value) {
				if ("".equals(value))value = null;//$NON-NLS-1$
				this.subject.setCatalogDefault(value);
			}
		};
	}

	private ModifiablePropertyValueModel<String> buildUsernameHolder() {
		return new PropertyAspectAdapter<BasicHibernateProperties, String>(getSubjectHolder(),
				BasicHibernateProperties.USERNAME_PROPERTY) {
			@Override
			protected String buildValue_() {
				return this.subject.getUsername();
			}

			@Override
			protected void setValue_(String value) {
				if ("".equals(value))value = null;//$NON-NLS-1$
				this.subject.setUsername(value);
			}

		};
	}

	private ModifiablePropertyValueModel<String> buildPasswordHolder() {
		return new PropertyAspectAdapter<BasicHibernateProperties, String>(getSubjectHolder(),
				BasicHibernateProperties.PASSWORD_PROPERTY) {
			@Override
			protected String buildValue_() {
				return this.subject.getPassword();
			}

			@Override
			protected void setValue_(String value) {
				if ("".equals(value))value = null;//$NON-NLS-1$
				this.subject.setPassword(value);
			}
		};
	}

	public Image getPageImage() {
		return null;
	}

}
