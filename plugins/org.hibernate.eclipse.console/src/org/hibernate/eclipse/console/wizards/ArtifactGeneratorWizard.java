package org.hibernate.eclipse.console.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.ConsoleConfiguration.Command;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.tool.hbm2x.ConfigurationNavigator;
import org.hibernate.tool.hbm2x.DocExporter;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateConfigurationExporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.POJOExporter;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "mpe". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class ArtifactGeneratorWizard extends Wizard implements INewWizard {
	private BasicGeneratorSettingsPage page;
	private ISelection selection;

	/**
	 * Constructor for ArtifactGeneratorWizard.
	 */
	public ArtifactGeneratorWizard() {
		super();
        setDefaultPageImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.NEW_WIZARD) );
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page = new BasicGeneratorSettingsPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
        final String outputPackage = page.getOutputPackage();
        final IPath output = page.getOutputDirectory();
        
        if(!MessageDialog.openQuestion(getShell(), "Start artifact generation", "Do you want to start generating artifcats into " + output.toPortableString() + ",\npossibly overwriting existing files in this directory ?") ) {
            return false;
        }
        
		final IPath revengsettings = page.getReverseEngineeringSettingsFile();
        final String configurationName = page.getConfigurationName();
		final boolean reveng = page.isReverseEngineerEnabled();
		final boolean genjava = page.isGenerateJava();
		final boolean genhbm = page.isGenerateMappings();
		final boolean gencfg = page.isGenerateCfg();
        final boolean preferRaw = page.isPreferRawCompositeIds();
        final boolean ejb3 = page.isEJB3Enabled();
		final boolean gendoc = page.isGenerateDoc();
		
        final IPath templatedir = page.getTemplateDirectory();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(configurationName, output, outputPackage, revengsettings, reveng, genjava, genhbm, gencfg, monitor, preferRaw, templatedir, ejb3, gendoc);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			HibernateConsolePlugin.getDefault().showError(getShell(), "Error under artifact generation", realException);
			return false;
		}
		return true;
	}
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 * @param outputPackage 
	 * @param revengsettings 
	 * @param gencfg
	 * @param genhbm
	 * @param genjava
	 * @param reveng
	 * @param preferRawCompositeids 
	 * @param gendoc 
	 */

	private void doFinish(
		String configName, IPath output,
		String outputPackage, IPath revengsettings, boolean reveng, final boolean genjava, final boolean genhbm, final boolean gencfg, final IProgressMonitor monitor, boolean preferRawCompositeids, IPath templateDir, final boolean ejb3, final boolean gendoc)
		throws CoreException {
		// create a sample file
		monitor.beginTask("Generating artifacts for " + configName, 10);
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IResource resource = root.findMember(output);
        final IResource templateres = root.findMember(templateDir);
		final IResource revengres = root.findMember(revengsettings);
		/*if (!resource.exists() || !(resource instanceof IContainer) ) {
			throwCoreException("Output directory \"" + configName + "\" does not exist.");
		}*/
		/*IContainer container = (IContainer) resource;*/

		ConsoleConfiguration cc = KnownConfigurations.getInstance().find(configName);
		ReverseEngineeringStrategy res = null;
		if (reveng) {
			monitor.subTask("reading jdbc metadata");
		
			DefaultReverseEngineeringStrategy configurableNamingStrategy = new DefaultReverseEngineeringStrategy();
			configurableNamingStrategy.setPackageName(outputPackage);
			
			res = configurableNamingStrategy;
			if(revengsettings!=null) {
				File file = revengres.getRawLocation().toFile();
				OverrideRepository repository = new OverrideRepository();
				repository.addFile(file);
				res = repository.getReverseEngineeringStrategy(res);
			}
		}
		final Configuration cfg = buildConfiguration(reveng, cc, res, preferRawCompositeids);
		monitor.worked(3);
		
		cc.execute(new Command() {
			public Object execute() {
				File outputdir = resource.getRawLocation().toFile(); 
				
                String[] templatePaths = new String[0];
        
                if(templateres!=null) {
                    templatePaths = new String[] { templateres.getRawLocation().toOSString() };
                }
                
                final HibernateMappingExporter hbmExporter = new HibernateMappingExporter(cfg, outputdir);
				hbmExporter.setTemplatePaths(templatePaths);
				final POJOExporter javaExporter = new POJOExporter(cfg, outputdir); // TODO: expose generics as an option
				javaExporter.setEjb3(ejb3);
				javaExporter.setGenerics(ejb3);
				javaExporter.setTemplatePaths(templatePaths);
				final Exporter cfgExporter = new HibernateConfigurationExporter(cfg, outputdir); 
				
				if(genhbm) {
					monitor.subTask("mapping files");
					hbmExporter.start();
					monitor.worked(5);
				}
				
				if(genjava) {
					monitor.subTask("domain code");
					javaExporter.start();
					monitor.worked(6);
				}
				
				if(gencfg) {
					monitor.subTask("hibernate configuration");
					cfgExporter.start();
					monitor.worked(7);
				}
				
				if(gendoc) {
					monitor.subTask("hibernate doc");
					new DocExporter(cfg, outputdir).start();
					monitor.worked(8);
				}
                try {
                    resource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
                } catch (CoreException e) {
                    HibernateConsolePlugin.getDefault().logErrorMessage("Problem refreshing", e);
                }

				monitor.worked(10);
				return null;
			}
		});
	}
	
	/**
	 * @param reveng
	 * @param cc
	 * @param rawCompositeids 
	 * @param configurableReverseNamingStrategy TODO
	 * @return
	 */
	private Configuration buildConfiguration(boolean reveng, ConsoleConfiguration cc, ReverseEngineeringStrategy revEngStrategy, boolean rawCompositeids) {
		if(reveng) {
			final JDBCMetaDataConfiguration cfg = new JDBCMetaDataConfiguration();
			cc.buildWith(cfg,false);
			cfg.setReverseEngineeringStrategy(revEngStrategy);
			cfg.setPreferRawCompositeIds(rawCompositeids);
            
			cc.execute(new Command() { // need to execute in the consoleconfiguration to let it handle classpath stuff!

				public Object execute() {
					cfg.readFromJDBC();
                    cfg.buildMappings();
					return null;
				}
			});	
			
			return cfg;
		} else {
			final Configuration configuration = new Configuration();
			cc.buildWith(configuration, true);
			
			cc.execute(new Command() {
				public Object execute() {
					
					configuration.buildMappings();
					return configuration;		
				}
			});
			return configuration;
		}
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}