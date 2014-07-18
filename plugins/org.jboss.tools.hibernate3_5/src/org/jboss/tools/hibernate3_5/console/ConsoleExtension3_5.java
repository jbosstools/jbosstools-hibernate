/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate3_5.console;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ui.views.properties.IPropertySource;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.QueryPage;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.console.ext.HibernateException;
import org.hibernate.console.ext.HibernateExtension;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.ext.CompletionProposalsResult;
import org.hibernate.eclipse.console.ext.ConsoleExtension;
import org.hibernate.eclipse.launch.CodeGenerationStrings;
import org.hibernate.eclipse.launch.CodeGenerationUtils;
import org.hibernate.eclipse.launch.PathHelper;
import org.hibernate.proxy.HibernateProxyHelper;
import org.hibernate.tool.ide.completion.IHQLCodeAssist;
import org.jboss.tools.hibernate.spi.IArtifactCollector;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.IExporter;
import org.jboss.tools.hibernate.spi.IOverrideRepository;
import org.jboss.tools.hibernate.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.spi.IService;
import org.jboss.tools.hibernate.spi.ISession;
import org.jboss.tools.hibernate.util.HibernateHelper;
import org.jboss.tools.hibernate3_5.HibernateExtension3_5;


/**
 * @author Dmitry Geraskov
 *
 */
public class ConsoleExtension3_5 implements ConsoleExtension {
	
	private HibernateExtension3_5 hibernateExtension;
	
	public ConsoleExtension3_5(){}

	@Override
	public CompletionProposalsResult hqlCodeComplete(String query, int startPosition, int currentOffset) {
		EclipseHQLCompletionRequestor requestor = new EclipseHQLCompletionRequestor(startPosition);
		if (!hibernateExtension.hasConfiguration()){
			try {
				hibernateExtension.build();
				hibernateExtension.buildMappings();
			} catch (HibernateException e){
				//FIXME
				//String mess = NLS.bind(HibernateConsoleMessages.CompletionHelper_error_could_not_build_cc, consoleConfiguration.getName());
				//HibernateConsolePlugin.getDefault().logErrorMessage(mess, e);
			}
		}
		IHQLCodeAssist hqlEval = HibernateHelper.INSTANCE.getHibernateService().newHQLCodeAssist(hibernateExtension.getConfiguration());
		query = query.replace('\t', ' ');
		hqlEval.codeComplete(query, currentOffset, requestor);
		return new CompletionProposalsResult(requestor.getCompletionProposals(), requestor.getLastErrorMessage());
	}

	/* (non-Javadoc)
	 * @see org.hibernate.eclipse.console.ext.ConsoleExtension#launchExporters(org.hibernate.eclipse.console.ext.ILaunchConfiguration, java.lang.String, org.hibernate.eclipse.console.ext.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public Map<String, File[]> launchExporters(ILaunchConfiguration configuration,
			String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		Assert.isNotNull(configuration);
		Assert.isNotNull(monitor);
		ExporterAttributes attributes = new ExporterAttributes(configuration);
		List<ExporterFactory> exporterFactories = attributes.getExporterFactories();
		for (Iterator<ExporterFactory> iter = exporterFactories.iterator(); iter.hasNext();) {
			ExporterFactory exFactory = iter.next();
			if (!exFactory.isEnabled(configuration)) {
				iter.remove();
			}
		}
		
		try {
		    Set<String> outputDirectories = new HashSet<String>();
		    ExporterFactory[] exporters = exporterFactories.toArray( new ExporterFactory[exporterFactories.size()] );
            IArtifactCollector collector = runExporters(attributes, exporters, outputDirectories, monitor);

            for (String path : outputDirectories) {
            	CodeGenerationUtils.refreshOutputDir(path);
			}

			//RefreshTab.refreshResources(configuration, monitor);

			// code formatting needs to happen *after* refresh to make sure eclipse will format the uptodate files!
            if(collector!=null) {
            	Map<String, File[]> map = new HashMap<String, File[]>();
            	Set<String> types = collector.getFileTypes();
            	for (String type : types) {
            		File[] files = collector.getFiles(type.toString());
            		map.put(type, files);
				}
            	return map;
			}
		} catch(Exception e) {
			throw new CoreException(HibernateConsolePlugin.throwableToStatus(e, 666));
		} catch(NoClassDefFoundError e) {
			throw new CoreException(HibernateConsolePlugin.throwableToStatus(new HibernateConsoleRuntimeException(HibernateConsoleMessages.CodeGenerationLaunchDelegate_received_noclassdeffounderror,e), 666));
		} finally {
			monitor.done();
		}
		return null;
	}

	private IArtifactCollector runExporters (final ExporterAttributes attributes, final ExporterFactory[] exporterFactories, final Set<String> outputDirectories, final IProgressMonitor monitor)
			   throws CoreException
		    {

				 	monitor.beginTask(HibernateConsoleMessages.CodeGenerationLaunchDelegate_generating_code_for + attributes.getConsoleConfigurationName(), exporterFactories.length + 1);

					if (monitor.isCanceled())
						return null;

					ConsoleConfiguration cc = KnownConfigurations.getInstance().find(attributes.getConsoleConfigurationName());
					if (attributes.isReverseEngineer()) {
						monitor.subTask(HibernateConsoleMessages.CodeGenerationLaunchDelegate_reading_jdbc_metadata);
					}
					final IConfiguration cfg = buildConfiguration(attributes, cc, ResourcesPlugin.getWorkspace().getRoot());

					monitor.worked(1);

					if (monitor.isCanceled())
						return null;

					return (IArtifactCollector) cc.execute(new Command() {

						public Object execute() {
							IArtifactCollector artifactCollector = HibernateHelper.INSTANCE.getHibernateService().newArtifactCollector();

		                    // Global properties
			                Properties props = new Properties();
			                props.put(CodeGenerationStrings.EJB3, "" + attributes.isEJB3Enabled()); //$NON-NLS-1$
		                    props.put(CodeGenerationStrings.JDK5, "" + attributes.isJDK5Enabled()); //$NON-NLS-1$

		                    for (int i = 0; i < exporterFactories.length; i++)
		                    {
		                       monitor.subTask(exporterFactories[i].getExporterDefinition().getDescription());

		                       Properties globalProperties = new Properties();
		                       globalProperties.putAll(props);

		                       IExporter exporter;
							try {
								exporter = exporterFactories[i].createConfiguredExporter(cfg, attributes.getOutputPath(), attributes.getTemplatePath(), globalProperties, outputDirectories, artifactCollector);
							} catch (CoreException e) {
								throw new HibernateConsoleRuntimeException(HibernateConsoleMessages.CodeGenerationLaunchDelegate_error_while_setting_up + exporterFactories[i].getExporterDefinition(), e);
							}

							try {
		                       exporter.start();
							} catch(HibernateException he) {
								throw new HibernateConsoleRuntimeException(HibernateConsoleMessages.CodeGenerationLaunchDelegate_error_while_running + exporterFactories[i].getExporterDefinition().getDescription(), he);
							}
		                       monitor.worked(1);
		                    }
							return artifactCollector;
						}

					});


				}
	
	
	private IConfiguration buildConfiguration(final ExporterAttributes attributes, ConsoleConfiguration cc, IWorkspaceRoot root) {
		final boolean reveng = attributes.isReverseEngineer();
		final String reverseEngineeringStrategy = attributes.getRevengStrategy();
		final boolean preferBasicCompositeids = attributes.isPreferBasicCompositeIds();
		final IResource revengres = PathHelper.findMember( root, attributes.getRevengSettings());
		
		if(reveng) {
			IConfiguration configuration = null;
			if(cc.hasConfiguration()) {
				configuration = cc.getConfiguration();
			} else {
				configuration = cc.buildWith( null, false );
			}
			
			final IConfiguration cfg = HibernateHelper.INSTANCE.getHibernateService().newJDBCMetaDataConfiguration();

//			final JDBCMetaDataConfiguration cfg = new JDBCMetaDataConfiguration();
			Properties properties = configuration.getProperties();
			cfg.setProperties( properties );
			cc.buildWith(cfg,false);

			cfg.setPreferBasicCompositeIds(preferBasicCompositeids);

			cc.execute(new Command() { // need to execute in the consoleconfiguration to let it handle classpath stuff!

				public Object execute() {					
					//todo: factor this setup of revengstrategy to core		
					
					IService service = HibernateHelper.INSTANCE.getHibernateService();

					IReverseEngineeringStrategy res = service.newDefaultReverseEngineeringStrategy();

					
					IOverrideRepository repository = null;
					
					if(revengres!=null) {
						File file = PathHelper.getLocation( revengres ).toFile();
						repository = service.newOverrideRepository();
						repository.addFile(file);						
					}
					
					if (repository != null){
						res = repository.getReverseEngineeringStrategy(res);
					}

					if(reverseEngineeringStrategy!=null && reverseEngineeringStrategy.trim().length()>0) {
						res = service.newReverseEngineeringStrategy(reverseEngineeringStrategy, res);
					}

					IReverseEngineeringSettings qqsettings = service.newReverseEngineeringSettings(res)
					.setDefaultPackageName(attributes.getPackageName())
					.setDetectManyToMany( attributes.detectManyToMany() )
					.setDetectOneToOne( attributes.detectOneToOne() )
					.setDetectOptimisticLock( attributes.detectOptimisticLock() );

					res.setSettings(qqsettings);

					cfg.setReverseEngineeringStrategy( res );

					cfg.readFromJDBC();
                    cfg.buildMappings();
					return null;
				}
			});

			return cfg;
		} else {
			cc.build();
			cc.buildMappings();
			return cc.getConfiguration();
		}
	}
	
	@Override
	public void setHibernateExtention(HibernateExtension hibernateExtension) {
		this.hibernateExtension = (HibernateExtension3_5) hibernateExtension;
	}

	@Override
	public IPropertySource getPropertySource(Object object,
			QueryPage selectedQueryPage) {
		ISession currentSession = ((ISession)selectedQueryPage.getSession());
		if((currentSession.isOpen() && currentSession.contains(object)) || hasMetaData( object, currentSession) ) {
			return new EntityPropertySource(object, currentSession, hibernateExtension);
		}
		return null;
	}
	
	private boolean hasMetaData(Object object, ISession currentSession) {
		return currentSession.getSessionFactory().getClassMetadata(HibernateProxyHelper.getClassWithoutInitializingProxy(object))!=null;
	}

}
