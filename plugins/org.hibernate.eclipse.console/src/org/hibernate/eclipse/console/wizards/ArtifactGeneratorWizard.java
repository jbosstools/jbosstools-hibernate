package org.hibernate.eclipse.console.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.hibernate.cfg.ConfigurableReverseNamingStrategy;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCFilter;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.ConsoleConfiguration.Command;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.tool.hbm2x.ConfigurationNavigator;
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
        setDefaultPageImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.NEW_WIZARD));
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
		final String containerName = page.getConfigurationName();
		final boolean reveng = page.isReverseEngineerEnabled();
		final boolean genjava = page.isGenerateJava();
		final boolean genhbm = page.isGenerateMappings();
		final boolean gencfg = page.isGenerateCfg();
		final IPath output = page.getOutputDirectory();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, output, reveng, genjava, genhbm, gencfg, monitor);
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
			HibernateConsolePlugin.showError(getShell(), "Error under artifact generation", realException);
			return false;
		}
		return true;
	}
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 * @param gencfg
	 * @param genhbm
	 * @param genjava
	 * @param reveng
	 */

	private void doFinish(
		String configName, IPath output,
		boolean reveng, final boolean genjava, final boolean genhbm, boolean gencfg, final IProgressMonitor monitor)
		throws CoreException {
		// create a sample file
		monitor.beginTask("Generating artifacts for " + configName, 10);
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IResource resource = root.findMember(output);
		/*if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Output directory \"" + configName + "\" does not exist.");
		}*/
		/*IContainer container = (IContainer) resource;*/

		ConsoleConfiguration cc = KnownConfigurations.getInstance().find(configName);
		
		if (reveng) monitor.subTask("reading jdbc metadata");
		final Configuration cfg = buildConfiguration(reveng, cc);
		monitor.worked(3);
		
		cc.execute(new Command() {
			public Object execute() {
				File outputdir = resource.getRawLocation().toFile(); 
				
                String[] templatePaths = new String[0];
                
                /*Bundle bundle = Platform.getBundle("org.hibernate.eclipse");
                Path path = new Path("");
                URL fileURL = Platform.find(bundle, path);
                if(fileURL!=null) {
                    try {
                        URL url = Platform.resolve(fileURL);
                        templatePaths = new String[] { url.getPath() };
                    } catch (IOException e) {
                        //
                    }
                }*/
                final ConfigurationNavigator cv = new ConfigurationNavigator();
				final Exporter hbmExporter = new HibernateMappingExporter(cfg, outputdir,templatePaths);
				final Exporter javaExporter = new POJOExporter(cfg, outputdir, templatePaths);
				final Exporter cfgExporter = new HibernateConfigurationExporter(cfg, outputdir); 
				
				if(genhbm) {
					monitor.subTask("mapping files");
					cv.export(cfg, hbmExporter);
					monitor.worked(5);
				}
				
				if(genjava) {
					monitor.subTask("domain code");
					cv.export(cfg, javaExporter);
					monitor.worked(6);
				}
				
				if(genhbm) {
					monitor.subTask("hibernate configuration");
					cv.export(cfg, cfgExporter);
					monitor.worked(7);
				}
				
				monitor.worked(10);
				return null;
			}
		});
	}
	
	/**
	 * @param reveng
	 * @param cc
	 * @return
	 */
	private Configuration buildConfiguration(boolean reveng, ConsoleConfiguration cc) {
		if(reveng) {
			final JDBCMetaDataConfiguration cfg = new JDBCMetaDataConfiguration();
			cc.buildWith(cfg,false);
			cfg.setGeneratingDynamicClasses(false);
			ConfigurableReverseNamingStrategy configurableNamingStrategy = new ConfigurableReverseNamingStrategy();
			configurableNamingStrategy.setPackageName("org.reveng"); // TODO: packagename
			cfg.setReverseNamingStrategy(configurableNamingStrategy);
			
			cc.execute(new Command() { // need to execute in the consoleconfiguration to let it handle classpath stuff!

				public Object execute() {
					cfg.readFromJDBC(new JDBCFilter() {
						public boolean acceptTableName(String name) {
							return !name.startsWith("BIN$"); // to avoid oracle pain. HACK! need to be configurable
						}
					});
                    cfg.buildMappings();
					return null;
				}
			});	
			
			return cfg;
		} else {
			return cc.buildWith(new Configuration(), true);
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