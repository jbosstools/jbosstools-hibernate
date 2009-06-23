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

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.manipulation.FileBufferOperationRunner;
import org.eclipse.core.filebuffers.manipulation.MultiTextEditWithProgress;
import org.eclipse.core.filebuffers.manipulation.TextFileBufferOperation;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.debug.ui.RefreshTab;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.osgi.util.NLS;
import org.eclipse.text.edits.TextEdit;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.model.impl.ExporterFactory;
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.util.ReflectHelper;
import org.hibernate.util.StringHelper;

public class CodeGenerationLaunchDelegate extends
		LaunchConfigurationDelegate {

	private static final class FormatGeneratedCode extends TextFileBufferOperation {
		private FormatGeneratedCode(String name) {
			super( name );
		}

		protected DocumentRewriteSessionType getDocumentRewriteSessionType() {
			return DocumentRewriteSessionType.SEQUENTIAL;
		}

		protected MultiTextEditWithProgress computeTextEdit(
				ITextFileBuffer textFileBuffer, IProgressMonitor progressMonitor)
		throws CoreException, OperationCanceledException {

			IResource bufferRes = ResourcesPlugin.getWorkspace().getRoot().findMember(textFileBuffer.getLocation());
			Map<?, ?> options = null;
			if(bufferRes!=null) {
				IJavaProject project = JavaCore.create(bufferRes.getProject());
				options = project.getOptions(true);
			}

			CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(options);

			IDocument document = textFileBuffer.getDocument();
			String string = document.get();
			TextEdit edit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, string, 0, string.length(), 0, null);
			MultiTextEditWithProgress multiTextEditWithProgress = new MultiTextEditWithProgress(getOperationName());
			if(edit==null) {
				//HibernateConsolePlugin.getDefault().log("empty format for " + textFileBuffer.getLocation().toOSString());
			} else {
				multiTextEditWithProgress.addChild(edit);
			}
			return multiTextEditWithProgress;
		}
	}


	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		Assert.isNotNull(configuration);
		Assert.isNotNull(monitor);
		try {
		    ExporterAttributes attributes = new ExporterAttributes(configuration);

		    List<ExporterFactory> exporterFactories = attributes.getExporterFactories();
		    for (Iterator<ExporterFactory> iter = exporterFactories.iterator(); iter.hasNext();) {
				ExporterFactory exFactory = iter.next();
				if(!exFactory.isEnabled(configuration)) {
					iter.remove();
				}
			}

		    Set<String> outputDirectories = new HashSet<String>();
		    ExporterFactory[] exporters = exporterFactories.toArray( new ExporterFactory[exporterFactories.size()] );
            ArtifactCollector collector = runExporters(attributes, exporters, outputDirectories, monitor);

            for (String path : outputDirectories) {
            	refreshOutputDir( path );
			}

			RefreshTab.refreshResources(configuration, monitor);

			// code formatting needs to happen *after* refresh to make sure eclipse will format the uptodate files!
            if(collector!=null) {
            	formatGeneratedCode( monitor, collector );
			}


		} catch(Exception e) {
			throw new CoreException(HibernateConsolePlugin.throwableToStatus(e, 666));
		} catch(NoClassDefFoundError e) {
			throw new CoreException(HibernateConsolePlugin.throwableToStatus(new HibernateConsoleRuntimeException(HibernateConsoleMessages.CodeGenerationLaunchDelegate_received_noclassdeffounderror,e), 666));
		} finally {
			monitor.done();
		}

	}

	private void formatGeneratedCode(IProgressMonitor monitor, ArtifactCollector collector) {
		final TextFileBufferOperation operation = new FormatGeneratedCode( HibernateConsoleMessages.CodeGenerationLaunchDelegate_formate_generated_code );

		File[] javaFiles = collector.getFiles("java"); //$NON-NLS-1$
		if(javaFiles.length>0) {

			IPath[] locations = new IPath[javaFiles.length];

			for (int i = 0; i < javaFiles.length; i++) {
				File file = javaFiles[i];
				locations[i] = new Path(file.getPath());
			}

			FileBufferOperationRunner runner= new FileBufferOperationRunner(FileBuffers.getTextFileBufferManager(), HibernateConsolePlugin.getShell());
			try {
				runner.execute(locations, operation, monitor);
			}
			catch (OperationCanceledException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.CodeGenerationLaunchDelegate_java_format_cancelled, e);
			}
			catch (CoreException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.CodeGenerationLaunchDelegate_exception_during_java_format, e);
			} catch (Throwable e) { // full guard since the above operation seem to be able to fail with IllegalArugmentException and SWT Invalid thread access while users are editing.
				HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.CodeGenerationLaunchDelegate_exception_during_java_format, e);
			}
		}

	}

	private void refreshOutputDir(String outputdir) {
		IResource bufferRes = PathHelper.findMember(ResourcesPlugin.getWorkspace().getRoot(), outputdir);

		if (bufferRes != null && bufferRes.isAccessible()) {
			try {
				bufferRes.refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				//ignore, maybe merge into possible existing status.
			}
		}
	}

	private ArtifactCollector runExporters (final ExporterAttributes attributes, final ExporterFactory[] exporterFactories, final Set<String> outputDirectories, final IProgressMonitor monitor)
	   throws CoreException
    {

		 	monitor.beginTask(HibernateConsoleMessages.CodeGenerationLaunchDelegate_generating_code_for + attributes.getConsoleConfigurationName(), exporterFactories.length + 1);

			if (monitor.isCanceled())
				return null;

			ConsoleConfiguration cc = KnownConfigurations.getInstance().find(attributes.getConsoleConfigurationName());
			if (attributes.isReverseEngineer()) {
				monitor.subTask(HibernateConsoleMessages.CodeGenerationLaunchDelegate_reading_jdbc_metadata);
			}
			final Configuration cfg = buildConfiguration(attributes, cc, ResourcesPlugin.getWorkspace().getRoot());

			monitor.worked(1);

			if (monitor.isCanceled())
				return null;

			return (ArtifactCollector) cc.execute(new Command() {

				public Object execute() {
					ArtifactCollector artifactCollector = new ArtifactCollector();

                    // Global properties
	                Properties props = new Properties();
	                props.put("ejb3", ""+attributes.isEJB3Enabled()); //$NON-NLS-1$ //$NON-NLS-2$
                    props.put("jdk5", ""+attributes.isJDK5Enabled());  //$NON-NLS-1$//$NON-NLS-2$

                    for (int i = 0; i < exporterFactories.length; i++)
                    {
                       monitor.subTask(exporterFactories[i].getExporterDefinition().getDescription());

                       Properties globalProperties = new Properties();
                       globalProperties.putAll(props);

                       Exporter exporter;
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

	private Configuration buildConfiguration(final ExporterAttributes attributes, ConsoleConfiguration cc, IWorkspaceRoot root) {
		final boolean reveng = attributes.isReverseEngineer();
		final String reverseEngineeringStrategy = attributes.getRevengStrategy();
		final boolean preferBasicCompositeids = attributes.isPreferBasicCompositeIds();
		final IResource revengres = PathHelper.findMember( root, attributes.getRevengSettings());
		
		if(reveng) {
			Configuration configuration = null;
			if(cc.hasConfiguration()) {
				configuration = cc.getConfiguration();
			} else {
				configuration = cc.buildWith( null, false );
			}

			final JDBCMetaDataConfiguration cfg = new JDBCMetaDataConfiguration();
			Properties properties = configuration.getProperties();
			cfg.setProperties( properties );
			cc.buildWith(cfg,false);

			cfg.setPreferBasicCompositeIds(preferBasicCompositeids);

			cc.execute(new Command() { // need to execute in the consoleconfiguration to let it handle classpath stuff!

				public Object execute() {					
					//todo: factor this setup of revengstrategy to core		
					ReverseEngineeringStrategy res = new DefaultReverseEngineeringStrategy();

					OverrideRepository repository = null;
					
					if(revengres!=null) {
						File file = PathHelper.getLocation( revengres ).toFile();
						repository = new OverrideRepository();
						repository.addFile(file);						
					}
					
					if (repository != null){
						res = repository.getReverseEngineeringStrategy(res);
					}

					if(reverseEngineeringStrategy!=null && reverseEngineeringStrategy.trim().length()>0) {
						res = loadreverseEngineeringStrategy(reverseEngineeringStrategy, res);
					}

					ReverseEngineeringSettings qqsettings = new ReverseEngineeringSettings(res)
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
			final Configuration configuration = cc.getConfiguration();

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
	@SuppressWarnings("unchecked")
	private ReverseEngineeringStrategy loadreverseEngineeringStrategy(final String className, ReverseEngineeringStrategy delegate) {
        try {
            Class<ReverseEngineeringStrategy> clazz = ReflectHelper.classForName(className);
			Constructor<ReverseEngineeringStrategy> constructor = clazz.getConstructor(new Class[] { ReverseEngineeringStrategy.class });
            return constructor.newInstance(new Object[] { delegate });
        }
        catch (NoSuchMethodException e) {
			try {
				Class<?> clazz = ReflectHelper.classForName(className);
				ReverseEngineeringStrategy rev = (ReverseEngineeringStrategy) clazz.newInstance();
				return rev;
			}
			catch (Exception eq) {
				String out = NLS.bind(HibernateConsoleMessages.CodeGenerationLaunchDelegate_could_not_create_or_find_with_default_noarg_constructor, className);
				throw new HibernateConsoleRuntimeException(out, eq);
			}
		}
        catch (Exception e) {
			String out = NLS.bind(HibernateConsoleMessages.CodeGenerationLaunchDelegate_could_not_create_or_find_with_one_argument_delegate_constructor, className);
			throw new HibernateConsoleRuntimeException(out, e);
		}
    }

	public boolean preLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
		ExporterAttributes attributes = new ExporterAttributes(configuration);

		String configName = attributes.getConsoleConfigurationName();
		if(StringHelper.isEmpty( configName )) {
			abort(HibernateConsoleMessages.CodeGenerationLaunchDelegate_console_configuration_name_is_empty_in + configuration.getName(), null, ICodeGenerationLaunchConstants.ERR_UNSPECIFIED_CONSOLE_CONFIGURATION);
		}

		if(KnownConfigurations.getInstance().find( configName )==null) {
			String out = NLS.bind(HibernateConsoleMessages.CodeGenerationLaunchDelegate_console_configuration_not_found_in, configName, configuration.getName());
			abort(out, null, ICodeGenerationLaunchConstants.ERR_CONSOLE_CONFIGURATION_NOTFOUND);
		}

		if(StringHelper.isEmpty(attributes.getOutputPath())) {
			abort(HibernateConsoleMessages.CodeGenerationLaunchDelegate_output_has_to_be_specified_in + configuration.getName(), null, ICodeGenerationLaunchConstants.ERR_OUTPUT_PATH_NOTFOUND);
		}

		return super.preLaunchCheck( configuration, mode, monitor );
	}

	protected void abort(String message, Throwable exception, int code)
	throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, HibernateConsolePlugin.ID, code, message, exception));
	}
}
