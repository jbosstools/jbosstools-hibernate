package org.hibernate.eclipse.console.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.node.ConfigurationNode;
import org.hibernate.eclipse.console.utils.DialogSelectionHelper;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class BasicGeneratorSettingsPage extends WizardPage {
	private ComboDialogField consoleConfigurationName;

	private IStructuredSelection selection;

	private SelectionButtonDialogField reverseengineer;

	private SelectionButtonDialogField generatecfgfile;

	private SelectionButtonDialogField generatejava;
	private SelectionButtonDialogField enableEJB3annotations;
    
    private SelectionButtonDialogField generatedao;
    
	private SelectionButtonDialogField generatemappings;

	private SelectionButtonDialogField generatedocs;
	
	private StringButtonDialogField outputdir;
	
	private StringButtonDialogField reverseEngineeringSettings;
    
    private StringDialogField packageName;

    private SelectionButtonDialogField preferRawCompositeIds;

    private SelectionButtonDialogField useOwnTemplates;
    private StringButtonDialogField templatedir;
    
    //private Package

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public BasicGeneratorSettingsPage(IStructuredSelection selection) {
		super("wizardPage");
		setTitle("Basic settings for artifact generation");
		setDescription("This wizard allows you to generate artifacts (configuration, mapping & source code files)");
		this.selection = selection;
	}

	
	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		
		initializeDialogUnits(parent);
		
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 10;
		
		consoleConfigurationName = new ComboDialogField(SWT.READ_ONLY);
		consoleConfigurationName.setLabelText("Console &configuration:");
		ConsoleConfiguration[] cfg = KnownConfigurations.getInstance().getConfigurations();
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
        generatejava = new SelectionButtonDialogField(SWT.CHECK);
		generatejava.setLabelText("Generate domain code (.java)");
		generatejava.setDialogFieldListener(fieldlistener);
		
        enableEJB3annotations = new SelectionButtonDialogField(SWT.CHECK);
        enableEJB3annotations.setLabelText("EJB3/JSR-220 annotations (experimental!)");
        enableEJB3annotations.setDialogFieldListener(fieldlistener);
        
        generatejava.attachDialogField(enableEJB3annotations);
        
        generatedao = new SelectionButtonDialogField(SWT.CHECK);
        generatedao.setLabelText("Generate DAO code (.java)");
        generatedao.setDialogFieldListener(fieldlistener);
        
        useOwnTemplates = new SelectionButtonDialogField(SWT.CHECK);
        useOwnTemplates.setDialogFieldListener(fieldlistener);
        useOwnTemplates.setLabelText("Use custom templates");
       
        preferRawCompositeIds = new SelectionButtonDialogField(SWT.CHECK);
        preferRawCompositeIds.setLabelText("Generate basic typed composite ids");
        preferRawCompositeIds.setSelection(true);
        preferRawCompositeIds.setDialogFieldListener(fieldlistener);
        
		generatemappings = new SelectionButtonDialogField(SWT.CHECK);
		generatemappings.setLabelText("Generate mappings (hbm.xml)");
		generatemappings.setDialogFieldListener(fieldlistener);
		
		generatedocs = new SelectionButtonDialogField(SWT.CHECK);
		generatedocs.setLabelText("Generate schema html-documentation");
		generatedocs.setDialogFieldListener(fieldlistener);
		
		generatecfgfile = new SelectionButtonDialogField(SWT.CHECK);
		generatecfgfile.setLabelText("Generate hibernate configuration (hibernate.cfg.xml)");
		generatecfgfile.setDialogFieldListener(fieldlistener);
        useOwnTemplates.attachDialogField(templatedir);
        reverseengineer.attachDialogFields(new DialogField[] { packageName, preferRawCompositeIds, reverseEngineeringSettings });
       
		consoleConfigurationName.doFillIntoGrid(container, 3);
		Control[] controls = outputdir.doFillIntoGrid(container, 3);
		// Hack to tell the text field to stretch!
		( (GridData)controls[1].getLayoutData() ).grabExcessHorizontalSpace=true;
		reverseengineer.doFillIntoGrid(container, 3);
        packageName.doFillIntoGrid(container, 3);
		reverseEngineeringSettings.doFillIntoGrid(container, 3);
		
        fillLabel(container);
        preferRawCompositeIds.doFillIntoGrid(container, 2);
		generatejava.doFillIntoGrid(container, 3);
        fillLabel(container);
        enableEJB3annotations.doFillIntoGrid(container, 2);
        generatedao.doFillIntoGrid(container, 3);
		generatemappings.doFillIntoGrid(container, 3);
		generatecfgfile.doFillIntoGrid(container, 3);
		generatedocs.doFillIntoGrid(container, 3);
        useOwnTemplates.doFillIntoGrid(container, 3);
        controls = templatedir.doFillIntoGrid(container, 3);
        // Hack to tell the text field to stretch!
        ( (GridData)controls[1].getLayoutData() ).grabExcessHorizontalSpace=true;
        

		initialize();
		dialogChanged();
		setControl(container);
	}

    private void fillLabel(Composite container) {
        new Label(container, SWT.NULL);
    }
	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof ConfigurationNode) {
				consoleConfigurationName.setText( ( (ConfigurationNode)obj).getConsoleConfiguration().getName() );
			} else if(consoleConfigurationName.getItems().length==1) {
                consoleConfigurationName.setText(consoleConfigurationName.getItems()[0]);
            }
		}
		
		loadSettings();
		
	}



	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {

		
		
		boolean configSelected = getConfigurationName().length()==0;
		outputdir.setEnabled(!configSelected);
		reverseengineer.setEnabled(!configSelected);
		generatejava.setEnabled(!configSelected);
		generatecfgfile.setEnabled(!configSelected);
		generatedao.setEnabled(!configSelected);
		generatedocs.setEnabled(!configSelected);
		generatemappings.setEnabled(!configSelected);
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
        setPageComplete(message==null);
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


	/**
	 * @return
	 */
	public boolean isGenerateJava() {
		return generatejava.isSelected();
	}
    
    /**
     * @return
     */
    public boolean isGenerateDao() {
        return generatedao.isSelected();
    }   


	/**
	 * @return
	 */
	public boolean isGenerateMappings() {
		return generatemappings.isSelected();
	}


	/**
	 * @return
	 */
	public boolean isGenerateCfg() {
		return generatecfgfile.isSelected();
	}


	/**
	 * @return
	 */
	public IPath getOutputDirectory() {
		return pathOrNull(outputdir.getText() );
	}
    
    public IPath getTemplateDirectory() {
        return pathOrNull(templatedir.getText() );
    }


    /**
     * @return
     */
    public String getOutputPackage() {
          return packageName.getText();
    }


    /**
     * @return
     */
    public boolean isPreferBasicCompositeIds() {
        return preferRawCompositeIds.isSelected();
    }


    /**
     * @return
     */
    public boolean isEJB3Enabled() {
        return enableEJB3annotations.isSelected();
    }


	public IPath getReverseEngineeringSettingsFile() {
		return pathOrNull(reverseEngineeringSettings.getText() );
	}


	public boolean isGenerateDoc() {
		return generatedocs.isSelected();
	}


	public void saveSettings() {
		getDialogSettings().put("outputdir", outputdir.getText());
		getDialogSettings().put("schema2hbm", isReverseEngineerEnabled());
		getDialogSettings().put("revengfile", reverseEngineeringSettings.getText());
		getDialogSettings().put("templatepathenabled", useOwnTemplates.isSelected());		
		getDialogSettings().put("configurationname", getConfigurationName());
		getDialogSettings().put("hbm2cfgxml", isGenerateCfg());
		getDialogSettings().put("ejb3", isEJB3Enabled());
		getDialogSettings().put("hbm2dao", isGenerateDao());
		getDialogSettings().put("hbm2doc", isGenerateDoc());
		getDialogSettings().put("hbm2java", isGenerateJava());
		getDialogSettings().put("hbm2hbmxml", isGenerateMappings());
		getDialogSettings().put("package", getOutputPackage());
		getDialogSettings().put("templatepath", templatedir.getText());				
	}
    
	public void loadSettings() {
		if(getDialogSettings().get("outputdir")!=null) {
			outputdir.setText(getDialogSettings().get("outputdir"));
			reverseengineer.setSelection(getDialogSettings().getBoolean("schema2hbm"));
			reverseEngineeringSettings.setText(getDialogSettings().get("revengfile"));
			useOwnTemplates.setSelection(getDialogSettings().getBoolean("templatepathenabled"));
			consoleConfigurationName.setText(getDialogSettings().get("configurationname"));
			generatecfgfile.setSelection(getDialogSettings().getBoolean("hbm2cfgxml"));
			enableEJB3annotations.setSelection(getDialogSettings().getBoolean("ejb3"));
			generatedao.setSelection(getDialogSettings().getBoolean("hbm2dao"));
			generatedocs.setSelection(getDialogSettings().getBoolean("hbm2doc"));
			generatejava.setSelection(getDialogSettings().getBoolean("hbm2java"));
			generatemappings.setSelection(getDialogSettings().getBoolean("hbm2hbmxml"));
			packageName.setText(getDialogSettings().get("package"));
			templatedir.setText(getDialogSettings().get("templatepath"));
		}
	}
}