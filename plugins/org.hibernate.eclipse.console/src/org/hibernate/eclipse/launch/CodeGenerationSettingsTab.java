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
package org.hibernate.eclipse.launch;


import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.DialogSelectionHelper;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.console.utils.LaunchHelper;
import org.hibernate.eclipse.console.wizards.NewReverseEngineeringFileWizard;

@SuppressWarnings("restriction")
public class CodeGenerationSettingsTab extends	AbstractLaunchConfigurationTab {

	private ComboDialogField consoleConfigurationName;

	private IStructuredSelection selection;

	private SelectionButtonDialogField reverseengineer;

	private StringButtonDialogField outputdir;

	private StringButtonDialogField reverseEngineeringSettings;

	private StringButtonDialogField reverseEngineeringStrategy;

    private StringDialogField packageName;

    private SelectionButtonDialogField preferRawCompositeIds;
    private SelectionButtonDialogField autoVersioning;
    private SelectionButtonDialogField autoManyToMany;
    private SelectionButtonDialogField autoOneToOne;

    private SelectionButtonDialogField useOwnTemplates;
    private DirectoryBrowseField templatedir;



	public CodeGenerationSettingsTab() {
		super();
	}

    public void createControl(Composite parent) {

		//initializeDialogUnits(parent);
		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		Composite container = new Composite(sc, SWT.NULL);
        sc.setContent(container);
		GridLayout layout = new GridLayout();

		container.setLayout(layout);
		layout.numColumns = 4;
		layout.verticalSpacing = 10;

		consoleConfigurationName = new ComboDialogField(SWT.READ_ONLY);
		consoleConfigurationName.setLabelText(HibernateConsoleMessages.CodeGenerationSettingsTab_console_configuration);
		ConsoleConfiguration[] cfg = LaunchHelper.findFilteredSortedConsoleConfigs();
		String[] names = new String[cfg.length];
		for (int i = 0; i < cfg.length; i++) {
			ConsoleConfiguration configuration = cfg[i];
			names[i] = configuration.getName();
		}
		consoleConfigurationName.setItems(names);

		IDialogFieldListener fieldlistener = new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				dialogChanged();
			}
		};

        consoleConfigurationName.setDialogFieldListener(fieldlistener);

		outputdir = new StringButtonDialogField(new IStringButtonAdapter() {
			public void changeControlPressed(DialogField field) {
				//IPath[] paths = DialogSelectionHelper.chooseFileEntries(getShell(),  PathHelper.pathOrNull(outputdir.getText()), new IPath[0], "Select output directory", "Choose directory in which the generated files will be stored", new String[] {"cfg.xml"}, false, true, false);
				IPath[] paths = DialogSelectionHelper.chooseFolderEntries(getShell(),  PathHelper.pathOrNull(outputdir.getText()), HibernateConsoleMessages.CodeGenerationSettingsTab_select_output_dir, HibernateConsoleMessages.CodeGenerationSettingsTab_choose_dir_for_generated_files, false);
				if(paths!=null && paths.length==1) {
					outputdir.setText( ( (paths[0]).toOSString() ) );
				}
			}
		});
        outputdir.setDialogFieldListener(fieldlistener);
		outputdir.setLabelText(HibernateConsoleMessages.CodeGenerationSettingsTab_output_dir);
		outputdir.setButtonLabel(HibernateConsoleMessages.CodeGenerationSettingsTab_browse);

        templatedir = new DirectoryBrowseField(null, null, HibernateConsoleMessages.CodeGenerationSettingsTab_select_template_dir, HibernateConsoleMessages.CodeGenerationSettingsTab_choose_dir_custom_templates);
        templatedir.setDialogFieldListener(fieldlistener);
        templatedir.setLabelText(HibernateConsoleMessages.CodeGenerationSettingsTab_template_directory);
        templatedir.setFilesystemBrowseLabel(HibernateConsoleMessages.CodeGenerationSettingsTab_filesystem);
        templatedir.setWorkspaceBrowseLabel(HibernateConsoleMessages.CodeGenerationSettingsTab_workspace);

		packageName = new StringDialogField();
        packageName.setDialogFieldListener(fieldlistener);
        packageName.setLabelText(HibernateConsoleMessages.CodeGenerationSettingsTab_package);

        reverseEngineeringStrategy = new StringButtonDialogField(new IStringButtonAdapter() {

			public void changeControlPressed(DialogField field) {
				String string = DialogSelectionHelper.chooseImplementation(ReverseEngineeringStrategy.class.getName(), reverseEngineeringStrategy.getText(), HibernateConsoleMessages.CodeGenerationSettingsTab_choose_reverse_engineering_strategy, getShell());
				if(string!=null) {
					reverseEngineeringStrategy.setText(string);
				}
			}
		});
        reverseEngineeringStrategy.setDialogFieldListener(fieldlistener);
        reverseEngineeringStrategy.setLabelText(HibernateConsoleMessages.CodeGenerationSettingsTab_reveng_strategy);
        reverseEngineeringStrategy.setButtonLabel(HibernateConsoleMessages.CodeGenerationSettingsTab_browse);

		reverseEngineeringSettings= new StringButtonDialogField(new IStringButtonAdapter() {
            public void changeControlPressed(DialogField field) {
            	int defaultChoice = 0;
            	IPath reverseEngineeringSettingsFile = getReverseEngineeringSettingsFile();

				if(reverseEngineeringSettingsFile==null) {
            		defaultChoice = 0;
            	} else {
            		defaultChoice = 1;
            	}
				MessageDialog dialog = new MessageDialog(getShell(),
						HibernateConsoleMessages.CodeGenerationSettingsTab_setup_reverse_engineering,
						null,
						HibernateConsoleMessages.CodeGenerationSettingsTab_do_you_want_create_reveng_xml,
						MessageDialog.QUESTION,
						new String[] { HibernateConsoleMessages.CodeGenerationSettingsTab_create_new, HibernateConsoleMessages.CodeGenerationSettingsTab_use_existing, IDialogConstants.CANCEL_LABEL},
						defaultChoice);
				int answer = dialog.open();
				if(answer==0) { // create new
					NewReverseEngineeringFileWizard wizard = new NewReverseEngineeringFileWizard();
					wizard.init(PlatformUI.getWorkbench(), selection );
					wizard.setSelectConfiguration(getConfigurationName());
					IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

					WizardDialog wdialog = new WizardDialog(win.getShell(), wizard);
					wdialog.open(); // This opens a dialog
					IPath createdFilePath = wizard.getCreatedFilePath();
					if(createdFilePath!=null) {
						reverseEngineeringSettings.setText(createdFilePath.toOSString());
					}
				} else if (answer==1) { // use existing
					IPath[] paths = DialogSelectionHelper.chooseFileEntries(getShell(),  reverseEngineeringSettingsFile, new IPath[0], HibernateConsoleMessages.CodeGenerationSettingsTab_select_reverse_engineering_settings_file, HibernateConsoleMessages.CodeGenerationSettingsTab_choose_file_read_reverse_settings, new String[] {HibernateConsoleMessages.CodeGenerationSettingsTab_reveng_xml_1}, false, false, true);
					if(paths!=null && paths.length==1) {
						reverseEngineeringSettings.setText( ( (paths[0]).toOSString() ) );
					}
				}
            }
        });
		reverseEngineeringSettings.setDialogFieldListener(fieldlistener);
        reverseEngineeringSettings.setLabelText(HibernateConsoleMessages.CodeGenerationSettingsTab_reveng_xml_2);
        reverseEngineeringSettings.setButtonLabel(HibernateConsoleMessages.CodeGenerationSettingsTab_setup);

		reverseengineer = new SelectionButtonDialogField(SWT.CHECK);
		reverseengineer.setLabelText(HibernateConsoleMessages.CodeGenerationSettingsTab_reverse_engineer_from_jdbc_connection);
		reverseengineer.setDialogFieldListener(fieldlistener);

        useOwnTemplates = new SelectionButtonDialogField(SWT.CHECK);
        useOwnTemplates.setDialogFieldListener(fieldlistener);
        useOwnTemplates.setLabelText(HibernateConsoleMessages.CodeGenerationSettingsTab_use_custom_templates);

        preferRawCompositeIds = new SelectionButtonDialogField(SWT.CHECK);
        preferRawCompositeIds.setLabelText(HibernateConsoleMessages.CodeGenerationSettingsTab_generate_basic_typed_composite_ids);
        preferRawCompositeIds.setSelection(true);
        preferRawCompositeIds.setDialogFieldListener(fieldlistener);

        autoManyToMany = new SelectionButtonDialogField(SWT.CHECK);
        autoManyToMany.setLabelText(HibernateConsoleMessages.CodeGenerationSettingsTab_detect_many_to_many_tables);
        autoManyToMany.setSelection(true);
        autoManyToMany.setDialogFieldListener(fieldlistener);

        autoOneToOne = new SelectionButtonDialogField(SWT.CHECK);
        autoOneToOne.setLabelText(HibernateConsoleMessages.CodeGenerationSettingsTab_detect_one_to_one_associations);
        autoOneToOne.setSelection(true);
        autoOneToOne.setDialogFieldListener(fieldlistener);
        
        
        autoVersioning = new SelectionButtonDialogField(SWT.CHECK);
        autoVersioning.setLabelText(HibernateConsoleMessages.CodeGenerationSettingsTab_detect_optimistic_lock_columns);
        autoVersioning.setSelection(true);
        autoVersioning.setDialogFieldListener(fieldlistener);

		useOwnTemplates.attachDialogField(templatedir);
        reverseengineer.attachDialogFields(new DialogField[] { packageName, preferRawCompositeIds, reverseEngineeringSettings, reverseEngineeringStrategy, autoManyToMany, autoOneToOne, autoVersioning });

		consoleConfigurationName.doFillIntoGrid(container, 4);
		Control[] controls = outputdir.doFillIntoGrid(container, 4);
		// Hack to tell the text field to stretch!
		( (GridData)controls[1].getLayoutData() ).grabExcessHorizontalSpace=true;
		reverseengineer.doFillIntoGrid(container, 4);
        packageName.doFillIntoGrid(container, 4);
		reverseEngineeringSettings.doFillIntoGrid(container, 4);
		reverseEngineeringStrategy.doFillIntoGrid(container, 4);

        fillLabel(container);
        preferRawCompositeIds.doFillIntoGrid(container, 3);
        fillLabel(container);
        autoVersioning.doFillIntoGrid(container, 3);
        fillLabel(container);
        autoManyToMany.doFillIntoGrid(container, 3);
        fillLabel(container);
        autoOneToOne.doFillIntoGrid(container, 3);
		useOwnTemplates.doFillIntoGrid(container, 4);
        controls = templatedir.doFillIntoGrid(container, 4);
        // Hack to tell the text field to stretch!
        ( (GridData)controls[1].getLayoutData() ).grabExcessHorizontalSpace=true;

        sc.setMinSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        dialogChanged();
        setControl(sc);
	}

    private void fillLabel(Composite container) {
        new Label(container, SWT.NULL);
    }


	private void dialogChanged() {
		boolean configSelected = getConfigurationName().length()==0;
		outputdir.setEnabled(!configSelected);
		reverseengineer.setEnabled(!configSelected);
		useOwnTemplates.setEnabled(!configSelected);

		if (configSelected) {
			updateStatus(HibernateConsoleMessages.CodeGenerationSettingsTab_console_cfg_must_be_specified);
			return;
		}

        String msg = PathHelper.checkDirectory(outputdir.getText(), HibernateConsoleMessages.CodeGenerationSettingsTab_output_directory, true);

        if (msg!=null) {
            updateStatus(msg);
            return;
        }

        if(packageName.isEnabled() && getOutputPackage().length()>0) {
            IStatus val= JavaConventions.validatePackageName(getOutputPackage() );
            if (val.getSeverity() == IStatus.ERROR || val.getSeverity() == IStatus.WARNING) {
                updateStatus(val.getMessage() );
                return;
            }
        }

        if(reverseEngineeringSettings.getText().trim().length()>0) {
            msg = PathHelper.checkFile(reverseEngineeringSettings.getText(), HibernateConsoleMessages.CodeGenerationSettingsTab_reveng_xml_3, true);
        	if(msg!=null) {
        		updateStatus(msg);
        		return;
        	}
        }

        if(useOwnTemplates.isSelected() ) {
            msg = PathHelper.checkDirectory(templatedir.getText(), HibernateConsoleMessages.CodeGenerationSettingsTab_template_dir, true);
            if (msg!=null) {
                updateStatus(msg);
                return;
            } else {
            	// imprecise and inefficient to check recursively all for .vm
                /*IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(getTemplateDirectory() );
                IResource[] files = new IFile[0];
                boolean found = false;

                if(resource.getType() == IResource.FOLDER) {
                    try {
                        found = ( (IFolder)resource).accept(new IResourceProxyVisitor() {

							public boolean visit(IResourceProxy proxy) throws CoreException {
								return false;
							}

						});
                    } catch (CoreException e) {
                        // noop
                    }
                }

                if(!found) {
                    setMessage("No templates (*.vm) found in template directory", IMessageProvider.WARNING);
                } else {
                    setMessage(null);
                }*/
            }
        } else {
            setMessage(null);
        }

		updateStatus(null);
	}



    protected String checkFile(IPath path, String name) {
        IResource res= ResourcesPlugin.getWorkspace().getRoot().findMember(path);
        if (res != null) {
            int resType= res.getType();
            if (resType == IResource.FILE) {
                return null;
            } else {
            	return NLS.bind(HibernateConsoleMessages.CodeGenerationSettingsTab_must_be_file, name);
            }
        } else {
            return NLS.bind(HibernateConsoleMessages.CodeGenerationSettingsTab_does_not_exist, name);
        }
    }

	public boolean isValid(ILaunchConfiguration launchConfig) {
		if (getErrorMessage() == null) {
			return true;
		}
		return false;
	}

    private void updateStatus(String message) {
        setErrorMessage(message);
        updateLaunchConfigurationDialog();
    }

	public String getConfigurationName() {
		return consoleConfigurationName.getText();
	}



	/**
	 * @return
	 */
	public boolean isReverseEngineerEnabled() {
		return reverseengineer.isSelected();
	}

	private String resolve(String expression)  {
		if(expression==null) return null;
		IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();

		try {
			return variableManager.performStringSubstitution(expression, false);
		} catch (CoreException e) {
			// ignore possible errors during substitution and just return the orginal expression
			return expression;
		}
	}


    String getOutputPackage() {
          return packageName.getText();
    }


	private IPath getReverseEngineeringSettingsFile() {
		return PathHelper.pathOrNull(reverseEngineeringSettings.getText() );
	}

	private String getReverseEngineeringStrategy() {
		return reverseEngineeringStrategy.getText();
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
//	   try {
//	      attributes = new ExporterAttributes(configuration);
//       } catch (CoreException ce) {
//          HibernateConsolePlugin.getDefault().logErrorMessage("Problem when setting up defaults for launch configuration", ce);
//       }
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
           ExporterAttributes attributes = new ExporterAttributes(configuration);
           consoleConfigurationName.setText(attributes.getConsoleConfigurationName());
           preferRawCompositeIds.setSelection(attributes.isPreferBasicCompositeIds());
           autoManyToMany.setSelection( attributes.detectManyToMany() );
           autoVersioning.setSelection( attributes.detectOptimisticLock() );
           autoOneToOne.setSelection( attributes.detectOneToOne());
           outputdir.setText(safeText(attributes.getOutputPath()));
           reverseengineer.setSelection(attributes.isReverseEngineer());
           reverseEngineeringSettings.setText(safeText(attributes.getRevengSettings()));
           reverseEngineeringStrategy.setText(safeText(attributes.getRevengStrategy()));
           useOwnTemplates.setSelection(attributes.isUseOwnTemplates());
           packageName.setText(safeText(attributes.getPackageName()));
           templatedir.setText(safeText(attributes.getTemplatePath()));
		} catch (CoreException ce) {
			HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.CodeGenerationSettingsTab_problems_when_reading, ce);
		}
	}

	private String safeText(String text) {
		return text==null?"":text; //$NON-NLS-1$
	}

	private String strOrNull(String text) {
		if(text==null || text.trim().length()==0) {
			return null;
		} else {
			return text;
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(HibernateLaunchConstants.ATTR_OUTPUT_DIR, strOrNull(outputdir.getText()));
		configuration.setAttribute(HibernateLaunchConstants.ATTR_PREFER_BASIC_COMPOSITE_IDS, preferRawCompositeIds.isSelected());
		configuration.setAttribute(HibernateLaunchConstants.ATTR_AUTOMATIC_MANY_TO_MANY, autoManyToMany.isSelected());
		configuration.setAttribute(HibernateLaunchConstants.ATTR_AUTOMATIC_ONE_TO_ONE, autoOneToOne.isSelected());
		configuration.setAttribute(HibernateLaunchConstants.ATTR_AUTOMATIC_VERSIONING, autoVersioning.isSelected());
		configuration.setAttribute(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER, isReverseEngineerEnabled());
		configuration.setAttribute(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER_STRATEGY, strOrNull(reverseEngineeringStrategy.getText()));
		configuration.setAttribute(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER_SETTINGS, strOrNull(reverseEngineeringSettings.getText()));


		configuration.setAttribute(HibernateLaunchConstants.ATTR_USE_OWN_TEMPLATES, useOwnTemplates.isSelected());
		configuration.setAttribute(HibernateLaunchConstants.ATTR_TEMPLATE_DIR, strOrNull(templatedir.getText()));

		configuration.setAttribute(HibernateLaunchConstants.ATTR_CONSOLE_CONFIGURATION_NAME, getConfigurationName());
		configuration.setAttribute(HibernateLaunchConstants.ATTR_PACKAGE_NAME, getOutputPackage());

	}

	public String getName() {
		return HibernateConsoleMessages.CodeGenerationSettingsTab_main;
	}

	public Image getImage() {
		return EclipseImages.getImage(ImageConstants.MINI_HIBERNATE);
	}


}
