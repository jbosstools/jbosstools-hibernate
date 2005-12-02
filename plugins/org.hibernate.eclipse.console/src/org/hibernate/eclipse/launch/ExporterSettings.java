package org.hibernate.eclipse.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class ExporterSettings extends AbstractLaunchConfigurationTab {

	private SelectionButtonDialogField generatecfgfile;

	private SelectionButtonDialogField generatejava;
	private SelectionButtonDialogField enableEJB3annotations;
    private SelectionButtonDialogField enableJDK5;
        
    private SelectionButtonDialogField generatedao;
    
	private SelectionButtonDialogField generatemappings;

	private SelectionButtonDialogField generatedocs;
		    
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public ExporterSettings() {
		super();
	}

	
	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		
		//initializeDialogUnits(parent);
		
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 10;
		
		IDialogFieldListener fieldlistener = new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				dialogChanged();
			}
		};
        
        generatejava = new SelectionButtonDialogField(SWT.CHECK);
		generatejava.setLabelText("Generate domain code (.java)");
		generatejava.setDialogFieldListener(fieldlistener);
        
        enableJDK5 = new SelectionButtonDialogField(SWT.CHECK);
        enableJDK5.setLabelText("JDK 1.5 Constructs (generics, etc.)");
        enableJDK5.setDialogFieldListener(fieldlistener);
 
        enableEJB3annotations = new SelectionButtonDialogField(SWT.CHECK);
        enableEJB3annotations.setLabelText("EJB3/JSR-220 annotations (experimental!)");
        enableEJB3annotations.setDialogFieldListener(fieldlistener);
      
        generatejava.attachDialogFields(new DialogField[] {enableJDK5, enableEJB3annotations});
                
        generatedao = new SelectionButtonDialogField(SWT.CHECK);
        generatedao.setLabelText("Generate DAO code (.java)");
        generatedao.setDialogFieldListener(fieldlistener);
        
        
		generatemappings = new SelectionButtonDialogField(SWT.CHECK);
		generatemappings.setLabelText("Generate mappings (hbm.xml)");
		generatemappings.setDialogFieldListener(fieldlistener);
		
		generatedocs = new SelectionButtonDialogField(SWT.CHECK);
		generatedocs.setLabelText("Generate schema html-documentation");
		generatedocs.setDialogFieldListener(fieldlistener);
		
		generatecfgfile = new SelectionButtonDialogField(SWT.CHECK);
		generatecfgfile.setLabelText("Generate hibernate configuration (hibernate.cfg.xml)");
		generatecfgfile.setDialogFieldListener(fieldlistener);
        
        fillLabel(container);
        generatejava.doFillIntoGrid(container, 3);
        fillLabel(container);
        enableJDK5.doFillIntoGrid(container, 2);
        fillLabel(container);
        enableEJB3annotations.doFillIntoGrid(container, 2);
        generatedao.doFillIntoGrid(container, 3);
		generatemappings.doFillIntoGrid(container, 3);
		generatecfgfile.doFillIntoGrid(container, 3);
		generatedocs.doFillIntoGrid(container, 3);
        
		dialogChanged();
		setControl(container);
	}

    private void fillLabel(Composite container) {
        new Label(container, SWT.NULL);
    }


	private void dialogChanged() {
		boolean configSelected = false; //TODO: only active if configname in settings ...getConfigurationName().length()==0;
		generatejava.setEnabled(!configSelected);
		generatecfgfile.setEnabled(!configSelected);
		generatedao.setEnabled(!configSelected);
		generatedocs.setEnabled(!configSelected);
		generatemappings.setEnabled(!configSelected);
		
		if (configSelected) {
			updateStatus("Console configuration must be specified");
			return;
		}
        
		if(!generatecfgfile.isSelected() && !generatejava.isSelected() && !generatedao.isSelected() && !generatedocs.isSelected() && !generatemappings.isSelected()) {
			updateStatus("At least one exporter option must be selected");
			return;
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
    
	private Path pathOrNull(String p) {
		if(p==null || p.trim().length()==0) {
			return null;
		} else {
			return new Path(p);
		}
	}



	public boolean isGenerateJava() {
		return generatejava.isSelected();
	}
    
    public boolean isGenerateDao() {
        return generatedao.isSelected();
    }   

	public boolean isGenerateMappings() {
		return generatemappings.isSelected();
	}

	public boolean isGenerateCfg() {
		return generatecfgfile.isSelected();
	}
    
    public boolean isEJB3Enabled() {
        return enableEJB3annotations.isSelected();
    }

	public boolean isGenerateDoc() {
		return generatedocs.isSelected();
	}

    public boolean isJDK5ConstructsEnabled() {
        return enableJDK5.isSelected();
    }
    
    
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {		
		// TODO
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			generatecfgfile.setSelection(configuration.getAttribute(PREFIX + "hbm2cfgxml",false));
			enableJDK5.setSelection(configuration.getAttribute(PREFIX + "jdk5",false));
			enableEJB3annotations.setSelection(configuration.getAttribute(PREFIX + "ejb3",false));
			generatedao.setSelection(configuration.getAttribute(PREFIX + "hbm2dao",false));
			generatedocs.setSelection(configuration.getAttribute(PREFIX + "hbm2doc",false));
			generatejava.setSelection(configuration.getAttribute(PREFIX + "hbm2java",false));
			generatemappings.setSelection(configuration.getAttribute(PREFIX + "hbm2hbmxml",false));			
		} catch (CoreException ce) {
			HibernateConsolePlugin.getDefault().logErrorMessage("Problem when reading hibernate tools launch configuration", ce);
		} 		
	}

	static String PREFIX = "org.hibernate.tools.";
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		
		configuration.setAttribute(PREFIX + "hbm2cfgxml", isGenerateCfg());
        configuration.setAttribute(PREFIX + "jdk5", isJDK5ConstructsEnabled());
		configuration.setAttribute(PREFIX + "ejb3", isEJB3Enabled());
		configuration.setAttribute(PREFIX + "hbm2dao", isGenerateDao());
		configuration.setAttribute(PREFIX + "hbm2doc", isGenerateDoc());
		configuration.setAttribute(PREFIX + "hbm2java", isGenerateJava());
		configuration.setAttribute(PREFIX + "hbm2hbmxml", isGenerateMappings());				
	}

	public String getName() {
		return "Exporters";		
	}
	
	public Image getImage() {
		return EclipseImages.getImage(ImageConstants.MINI_HIBERNATE);
	}

	
}
