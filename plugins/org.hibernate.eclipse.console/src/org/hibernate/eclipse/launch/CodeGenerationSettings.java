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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.swt.SWT;
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
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.DialogSelectionHelper;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.console.wizards.NewReverseEngineeringFileWizard;

public class CodeGenerationSettings extends	AbstractLaunchConfigurationTab {

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

    private SelectionButtonDialogField useOwnTemplates;
    private StringButtonDialogField templatedir;
    
    
    
	public CodeGenerationSettings() {
		super();
	}

    public void createControl(Composite parent) {
		
		//initializeDialogUnits(parent);
		
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 10;
		
		consoleConfigurationName = new ComboDialogField(SWT.READ_ONLY);
		consoleConfigurationName.setLabelText("Console &configuration:");
		ConsoleConfiguration[] cfg = KnownConfigurations.getInstance().getConfigurationsSortedByName();
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
				IPath[] paths = DialogSelectionHelper.chooseFileEntries(getShell(),  getOutputDirectory(), new IPath[0], "Select output directory", "Choose directory in which the generated files will be stored", new String[] {"cfg.xml"}, false, true, false);
				if(paths!=null && paths.length==1) {
					outputdir.setText( ( (paths[0]).toOSString() ) );
				}					
			}
		});
        outputdir.setDialogFieldListener(fieldlistener);
		outputdir.setLabelText("Output &directory:");
		outputdir.setButtonLabel("&Browse...");
		
        templatedir = new StringButtonDialogField(new IStringButtonAdapter() {
            public void changeControlPressed(DialogField field) {
                IPath[] paths = DialogSelectionHelper.chooseFileEntries(getShell(),  getTemplateDirectory(), new IPath[0], "Select template directory", "Choose directory containing custom templates", new String[0], false, true, false);
                if(paths!=null && paths.length==1) {
                    templatedir.setText( ( (paths[0]).toOSString() ) );
                }                   
            }
        });
        templatedir.setDialogFieldListener(fieldlistener);
        templatedir.setLabelText("Template &directory:");
        templatedir.setButtonLabel("&Browse...");
        
		packageName = new StringDialogField();
        packageName.setDialogFieldListener(fieldlistener);
        packageName.setLabelText("&Package:");
        
        reverseEngineeringStrategy = new StringButtonDialogField(new IStringButtonAdapter() {
		
			public void changeControlPressed(DialogField field) {
				String string = DialogSelectionHelper.chooseImplementation(ReverseEngineeringStrategy.class.getName(), reverseEngineeringStrategy.getText(), "Choose a reverse engineering strategy", getShell());
				if(string!=null) {
					reverseEngineeringStrategy.setText(string);
				}		
			}	
		});
        reverseEngineeringStrategy.setDialogFieldListener(fieldlistener);
        reverseEngineeringStrategy.setLabelText("reveng. s&trategy:");
        reverseEngineeringStrategy.setButtonLabel("&Browse...");
		
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
						"Setup reverse engineering", 
						null, 
						"Do you want to create a new reveng.xml or use an existing file ?", 
						MessageDialog.QUESTION, 
						new String[] { "Create &new...", "Use &existing...", IDialogConstants.CANCEL_LABEL}, 
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
					IPath[] paths = DialogSelectionHelper.chooseFileEntries(getShell(),  reverseEngineeringSettingsFile, new IPath[0], "Select reverse engineering settings file", "Choose file from which settings for the reverse engineering will be read", new String[] {"reveng.xml"}, false, false, true);
					if(paths!=null && paths.length==1) {
						reverseEngineeringSettings.setText( ( (paths[0]).toOSString() ) );
					}		
				}                                	
            }
        });
		reverseEngineeringSettings.setDialogFieldListener(fieldlistener);
        reverseEngineeringSettings.setLabelText("reveng.&xml:");
        reverseEngineeringSettings.setButtonLabel("&Setup...");
		        
		reverseengineer = new SelectionButtonDialogField(SWT.CHECK);
		reverseengineer.setLabelText("Reverse engineer from JDBC Connection");
		reverseengineer.setDialogFieldListener(fieldlistener);
          
        useOwnTemplates = new SelectionButtonDialogField(SWT.CHECK);
        useOwnTemplates.setDialogFieldListener(fieldlistener);
        useOwnTemplates.setLabelText("Use custom templates (for custom file generation)");
       
        preferRawCompositeIds = new SelectionButtonDialogField(SWT.CHECK);
        preferRawCompositeIds.setLabelText("Generate basic typed composite ids");
        preferRawCompositeIds.setSelection(true);
        preferRawCompositeIds.setDialogFieldListener(fieldlistener);
        
        autoManyToMany = new SelectionButtonDialogField(SWT.CHECK);
        autoManyToMany.setLabelText("Detect many-to-many tables");
        autoManyToMany.setSelection(true);
        autoManyToMany.setDialogFieldListener(fieldlistener);
        
        autoVersioning = new SelectionButtonDialogField(SWT.CHECK);
        autoVersioning.setLabelText("Detect optimistic lock columns");
        autoVersioning.setSelection(true);
        autoVersioning.setDialogFieldListener(fieldlistener);
        
		useOwnTemplates.attachDialogField(templatedir);
        reverseengineer.attachDialogFields(new DialogField[] { packageName, preferRawCompositeIds, reverseEngineeringSettings, reverseEngineeringStrategy, autoManyToMany, autoVersioning });
       
		consoleConfigurationName.doFillIntoGrid(container, 3);
		Control[] controls = outputdir.doFillIntoGrid(container, 3);
		// Hack to tell the text field to stretch!
		( (GridData)controls[1].getLayoutData() ).grabExcessHorizontalSpace=true;
		reverseengineer.doFillIntoGrid(container, 3);
        packageName.doFillIntoGrid(container, 3);        
		reverseEngineeringSettings.doFillIntoGrid(container, 3);
		reverseEngineeringStrategy.doFillIntoGrid(container, 3);
		
        fillLabel(container);
        preferRawCompositeIds.doFillIntoGrid(container, 2);
        fillLabel(container);
        autoVersioning.doFillIntoGrid(container, 2);
        fillLabel(container);
        autoManyToMany.doFillIntoGrid(container, 2);
		useOwnTemplates.doFillIntoGrid(container, 3);
        controls = templatedir.doFillIntoGrid(container, 3);
        // Hack to tell the text field to stretch!
        ( (GridData)controls[1].getLayoutData() ).grabExcessHorizontalSpace=true;
        
		dialogChanged();
		setControl(container);
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
			updateStatus("Console configuration must be specified");
			return;
		}
        
        String msg = checkDirectory(getOutputDirectory(), "output directory");
        
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
        	msg = checkFile(getReverseEngineeringSettingsFile(), "reveng.xml");
        	if(msg!=null) {
        		updateStatus(msg);
        		return;
        	}
        }

        if(useOwnTemplates.isSelected() ) {
            msg = checkDirectory(getTemplateDirectory(), "template directory");
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



    protected String checkDirectory(IPath path, String name) {
        IResource res= ResourcesPlugin.getWorkspace().getRoot().findMember(path);
        if (res != null) {
            int resType= res.getType();
            if (resType == IResource.PROJECT || resType == IResource.FOLDER) {
                IProject proj= res.getProject();
                if (!proj.isOpen() ) {
                    return "Project for " + name + " is closed";                    
                }                               
            } else {
                return name + " has to be a folder or project";
            }
        } else {
            return name + " does not exist";
        }
        return null;
    }
    
    protected String checkFile(IPath path, String name) {
        IResource res= ResourcesPlugin.getWorkspace().getRoot().findMember(path);
        if (res != null) {
            int resType= res.getType();
            if (resType == IResource.FILE) {
                return null;
            } else {
            	return name + " must be a file";
            }
        } else {
            return name + " does not exist";
        }
    }
    
    private void updateStatus(String message) {
        setErrorMessage(message);
        updateLaunchConfigurationDialog();
    }
    
	public String getConfigurationName() {
		return consoleConfigurationName.getText();
	}

	private Path pathOrNull(String p) {
		if(p==null || p.trim().length()==0) {
			return null;
		} else {
			return new Path(p);
		}
	}


	/**
	 * @return
	 */
	public boolean isReverseEngineerEnabled() {
		return reverseengineer.isSelected();
	}

	public IPath getOutputDirectory() {
		return pathOrNull(outputdir.getText() );
	}
    
    public IPath getTemplateDirectory() {
        return pathOrNull(templatedir.getText() );
    }

    public String getOutputPackage() {
          return packageName.getText();
    }


	private IPath getReverseEngineeringSettingsFile() {
		return pathOrNull(reverseEngineeringSettings.getText() );
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
           outputdir.setText(safeText(attributes.getOutputPath()));
           reverseengineer.setSelection(attributes.isReverseEngineer());
           reverseEngineeringSettings.setText(safeText(attributes.getRevengSettings()));
           reverseEngineeringStrategy.setText(safeText(attributes.getRevengStrategy()));
           useOwnTemplates.setSelection(attributes.isUseOwnTemplates());
           packageName.setText(safeText(attributes.getPackageName()));
           templatedir.setText(safeText(attributes.getTemplatePath()));
		} catch (CoreException ce) {
			HibernateConsolePlugin.getDefault().logErrorMessage("Problem when reading hibernate tools launch configuration", ce);
		} 		
	}

	private String safeText(String text) {
		return text==null?"":text;
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
		return "Main";
	}
	
	public Image getImage() {
		return EclipseImages.getImage(ImageConstants.MINI_HIBERNATE);
	}

	
}
