package org.hibernate.eclipse.launch;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.debug.ui.RefreshTab;
import org.eclipse.jface.util.Assert;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.tool.hbm2x.DAOExporter;
import org.hibernate.tool.hbm2x.DocExporter;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateConfigurationExporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tool.hbm2x.seam.SeamExporter;
import org.hibernate.util.ReflectHelper;

public class CodeGenerationLaunchDelegate extends
		LaunchConfigurationDelegate {

	private static final String PREFIX = "org.hibernate.tools."; // move to HibernateLaunchConstants

	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		Assert.isNotNull(configuration);
		Assert.isNotNull(monitor);
		try {		
			String consoleConfigurationName = configuration.getAttribute(PREFIX + "configurationname","");		
			String outputdir = configuration.getAttribute(PREFIX + "outputdir","");
			boolean reverseengineer = configuration.getAttribute(PREFIX + "schema2hbm", false);
			String reverseEngineeringSettings = configuration.getAttribute(PREFIX + "revengfile", "");
			String reverseEngineeringStrategy = configuration.getAttribute(PREFIX + "revengstrategy", "");
			boolean useOwnTemplates = configuration.getAttribute(PREFIX + "templatepathenabled",false);		
			boolean generatecfgfile = configuration.getAttribute(PREFIX + "hbm2cfgxml",false);
			boolean enableJDK5 = configuration.getAttribute(PREFIX + "jdk5",false);
			boolean enableEJB3annotations = configuration.getAttribute(PREFIX + "ejb3",false);
			boolean generatedao = configuration.getAttribute(PREFIX + "hbm2dao",false);
			boolean generatedocs = configuration.getAttribute(PREFIX + "hbm2doc",false);
			boolean generateseam = configuration.getAttribute(PREFIX + "hbm2seam",false);
			boolean generatejava = configuration.getAttribute(PREFIX + "hbm2java",false);
			boolean generatemappings = configuration.getAttribute(PREFIX + "hbm2hbmxml",false);
			String packageName = configuration.getAttribute(PREFIX + "package","");
			String templatedir = configuration.getAttribute(PREFIX + "templatepath","");
			boolean preferBasicCompositeIds = configuration.getAttribute(PREFIX + "prefercompositeids", true);
			
			if(!useOwnTemplates) {
				templatedir = null;
			}
			doFinish(consoleConfigurationName, pathOrNull(outputdir), packageName, pathOrNull(reverseEngineeringSettings), reverseEngineeringStrategy, reverseengineer, generatejava, generatedao, generatemappings, generatecfgfile, monitor, preferBasicCompositeIds, pathOrNull(templatedir), enableEJB3annotations, enableJDK5, generatedocs, generateseam);

			// refresh resources
			RefreshTab.refreshResources(configuration, monitor);

		} catch(Exception e) {
			throw new CoreException(HibernateConsolePlugin.throwableToStatus(e, 666)); 
		} finally {
			monitor.done();
		} 
		
	}
	
	private Path pathOrNull(String p) {
		if(p==null || p.trim().length()==0) {
			return null;
		} else {
			return new Path(p);
		}
	}

	private void doFinish(
			String configName, IPath output,
	String outputPackage, IPath revengsettings, String reverseEngineeringStrategy, boolean reveng, final boolean genjava, final boolean gendao, final boolean genhbm, final boolean gencfg, final IProgressMonitor monitor, boolean preferBasicCompositeids, IPath templateDir, final boolean ejb3, final boolean generics, final boolean gendoc, final boolean generateseam)
			throws CoreException {
			
		 	monitor.beginTask("Generating code for " + configName, 10);
		
			if (monitor.isCanceled())
				return;
			
			
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			final IResource resource = root.findMember(output);
	        final IResource templateres = root.findMember(templateDir);
			final IResource revengres = revengsettings==null?null:root.findMember(revengsettings);
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
				if(revengres!=null) {
					/*Configuration configuration = cc.buildWith(new Configuration(), false);*/				
					/*Settings settings = cc.getSettings(configuration);*/
					File file = getLocation( revengres ).toFile();
					OverrideRepository repository = new OverrideRepository(null,null);///*settings.getDefaultCatalogName(),settings.getDefaultSchemaName()*/);
					repository.addFile(file);
					res = repository.getReverseEngineeringStrategy(res);
				}
				
			}
			final Configuration cfg = buildConfiguration(reveng, reverseEngineeringStrategy, cc, res, preferBasicCompositeids);
			
			monitor.worked(3);
			
			if (monitor.isCanceled())
				return;
			
			cc.execute(new Command() {
				public Object execute() {
					File outputdir = getLocation( resource ).toFile(); 
					
	                String[] templatePaths = new String[0];
	        
	                if(templateres!=null) {
	                    templatePaths = new String[] { getLocation( templateres ).toOSString() };
	                }
	                
	                Properties props = new Properties();
	                
					if(genhbm) {
						monitor.subTask("mapping files");
						final HibernateMappingExporter hbmExporter = new HibernateMappingExporter();
						hbmExporter.setProperties(props);
						hbmExporter.setOutputDirectory(outputdir);
						hbmExporter.setConfiguration(cfg);
						hbmExporter.setTemplatePath(templatePaths);						               
						hbmExporter.start();
						monitor.worked(5);
					}
					
					if(genjava) {
						monitor.subTask("domain code");
						final POJOExporter javaExporter = new POJOExporter(); // TODO: expose jdk5 as an option
						javaExporter.setOutputDirectory(outputdir);
						javaExporter.setConfiguration(cfg);
						javaExporter.setTemplatePath(templatePaths);	                
						
						javaExporter.setEjb3(ejb3);
						javaExporter.setJdk5(generics);
											
						javaExporter.start();
						monitor.worked(6);
					}
	                
	                if(gendao) {
	                    monitor.subTask("DAO code");
	                    final DAOExporter daoExporter = new DAOExporter();
	                    
	                    daoExporter.setOutputDirectory(outputdir);
						daoExporter.setConfiguration(cfg);
						daoExporter.setTemplatePath(templatePaths);
	    			    
	                    daoExporter.setEjb3(ejb3);
	                    daoExporter.setJdk5(generics);
	                    
	                    daoExporter.start();
	                    monitor.worked(7);
	                }
					
					if(gencfg) {
						monitor.subTask("hibernate configuration");
						final HibernateConfigurationExporter cfgExporter = new HibernateConfigurationExporter();
						
						cfgExporter.setOutputDirectory(outputdir);
						cfgExporter.setConfiguration(cfg);
						cfgExporter.setTemplatePath(templatePaths);
						cfgExporter.setEjb3(ejb3);
						cfgExporter.start();
						
						monitor.worked(8);
					}
					
					if(gendoc) {
						monitor.subTask("hibernate doc");
						Exporter docExporter = new DocExporter();
						docExporter.setOutputDirectory(outputdir);
						docExporter.setConfiguration(cfg);
						docExporter.setTemplatePath(templatePaths);
						docExporter.start();
						monitor.worked(9);
					}
					
					if(generateseam) {
						monitor.subTask("Seam skeleton");
						Exporter docExporter = new SeamExporter();
						Properties p = new Properties();
						p.setProperty("seam_appname", "Seam Application");
						p.setProperty("seam_shortname", "seamapp");
						docExporter.setProperties(p);
						docExporter.setOutputDirectory(outputdir);
						docExporter.setConfiguration(cfg);
						docExporter.setTemplatePath(templatePaths);
						docExporter.start();
						monitor.worked(9);
					}
					
					monitor.worked(10);
					return null;
				}
			});
		}

	private IPath getLocation(final IResource resource) {		
		if (resource.getRawLocation() == null) { 
			return resource.getLocation(); 
		} 
		else return resource.getRawLocation();  
	}

	private Configuration buildConfiguration(boolean reveng, final String reverseEngineeringStrategy, ConsoleConfiguration cc, final ReverseEngineeringStrategy revEngStrategy, boolean preferBasicCompositeids) {
		if(reveng) {
			final JDBCMetaDataConfiguration cfg = new JDBCMetaDataConfiguration();
			cc.buildWith(cfg,false);
			
			cfg.setPreferBasicCompositeIds(preferBasicCompositeids);
            
			cc.execute(new Command() { // need to execute in the consoleconfiguration to let it handle classpath stuff!

				public Object execute() {
					
					if(reverseEngineeringStrategy!=null && reverseEngineeringStrategy.trim().length()>0) {
						ReverseEngineeringStrategy res = loadreverseEngineeringStrategy(reverseEngineeringStrategy, revEngStrategy);
						cfg.setReverseEngineeringStrategy(res);
					} else {
						cfg.setReverseEngineeringStrategy(revEngStrategy);
					}
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

	// TODO: merge with revstrategy load in JDBCConfigurationTask
	private ReverseEngineeringStrategy loadreverseEngineeringStrategy(final String className, ReverseEngineeringStrategy delegate) {
        try {
            Class clazz = ReflectHelper.classForName(className);			
			Constructor constructor = clazz.getConstructor(new Class[] { ReverseEngineeringStrategy.class });
            return (ReverseEngineeringStrategy) constructor.newInstance(new Object[] { delegate }); 
        } 
        catch (NoSuchMethodException e) {
			try {
				Class clazz = ReflectHelper.classForName(className);						
				ReverseEngineeringStrategy rev = (ReverseEngineeringStrategy) clazz.newInstance();
				return rev;
			} 
			catch (Exception eq) {
				throw new HibernateConsoleRuntimeException("Could not create or find " + className + " with default no-arg constructor", eq);
			}
		} 
        catch (Exception e) {
			throw new HibernateConsoleRuntimeException("Could not create or find " + className + " with one argument delegate constructor", e);
		} 
    }
}
