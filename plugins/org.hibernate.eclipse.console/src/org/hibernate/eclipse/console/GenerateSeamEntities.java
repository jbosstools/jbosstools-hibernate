package org.hibernate.eclipse.console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableFilter;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.eclipse.console.model.impl.ExporterDefinition;
import org.hibernate.eclipse.console.model.impl.ExporterFactory;
import org.hibernate.eclipse.console.utils.DialogSelectionHelper;
import org.hibernate.eclipse.launch.PathHelper;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.Exporter;

public class GenerateSeamEntities implements IObjectActionDelegate {

	private TreeSelection selection;
	
	private String outputPath;

	private Map<ConsoleConfiguration, List<Table>> tableGroups;

	public void run(IAction action) {
		
		IPath[] paths = DialogSelectionHelper.chooseFolderEntries(null,  null, 
				Messages.GenerateSeamEntities_dialog_title, 
				Messages.GenerateSeamEntities_dialog_message, 
				false);
		
		if (paths.length == 0) return;
		
		outputPath = paths[0].toOSString();

		splitOnGroupsByConsoleCfg();

		ExporterFactory[] exporters = getExporterFactories();

		Set<ConsoleConfiguration> ccs = tableGroups.keySet();

		for (Iterator<ConsoleConfiguration> iterator = ccs.iterator(); iterator
				.hasNext();) {
			ConsoleConfiguration consoleConfiguration = iterator.next();
			runExporters(exporters, consoleConfiguration);
		}

		refreshOutputDir(getOutputPath());
	}

	private ArtifactCollector runExporters(
			final ExporterFactory[] exporterFactories, ConsoleConfiguration cc) {
		final IProgressMonitor monitor = new NullProgressMonitor();

		monitor.beginTask("Generating code for " + cc.getName(), exporterFactories.length + 1);		//$NON-NLS-1$

		final Configuration cfg = buildConfiguration(cc, tableGroups.get(cc));

		monitor.worked(1);

		if (monitor.isCanceled())
			return null;

		return (ArtifactCollector) cc.execute(new Command() {

			public Object execute() {
				ArtifactCollector artifactCollector = new ArtifactCollector();

				// Global properties
				Properties props = new Properties();
				props.put("ejb3", "true");						//$NON-NLS-1$ //$NON-NLS-2$
				props.put("jdk5", "true");						//$NON-NLS-1$ //$NON-NLS-2$

				for (int i = 0; i < exporterFactories.length; i++) {
					monitor.subTask(exporterFactories[i]
							.getExporterDefinition().getDescription());

					Properties globalProperties = new Properties();
					globalProperties.putAll(props);

					Set outputDirectories = new HashSet();
					Exporter exporter;
					try {
						exporter = exporterFactories[i]
								.createConfiguredExporter(cfg, getOutputPath(),
										null, globalProperties,
										outputDirectories, artifactCollector);
					} catch (CoreException e) {
						throw new HibernateConsoleRuntimeException(
								"Error while setting up "					//$NON-NLS-1$
										+ exporterFactories[i]
												.getExporterDefinition(), e);
					}

					exporter.start();
					monitor.worked(1);
				}
				return artifactCollector;
			}

		});

	}

	private ExporterFactory[] getExporterFactories() {
		List<String> exporterNames = new ArrayList<String>();
		exporterNames.add("org.hibernate.tools.hbm2java");				//$NON-NLS-1$
		//exporterNames.add("org.hibernate.tools.hbm2dao");				//$NON-NLS-1$

		Map exDefinitions = ExtensionManager.findExporterDefinitionsAsMap();
		List<ExporterFactory> factories = new ArrayList<ExporterFactory>();

		for (Iterator<String> iterator = exporterNames.iterator(); iterator
				.hasNext();) {
			String exporterId = iterator.next();

			ExporterDefinition expDef = (ExporterDefinition) exDefinitions
					.get(exporterId);
			if (expDef == null) {
				throw new HibernateConsoleRuntimeException(
						"Could not locate exporter for '" + exporterId + "'");		//$NON-NLS-1$ //$NON-NLS-2$
			} else {
				ExporterFactory exporterFactory = new ExporterFactory(expDef,
						exporterId);
				factories.add(exporterFactory);
			}
		}
		return factories.toArray(new ExporterFactory[factories.size()]);
	}

	private Configuration buildConfiguration(ConsoleConfiguration cc,
			final List<Table> tables) {
		Configuration configuration = null;
		if (cc.hasConfiguration()) {
			configuration = cc.getConfiguration();
		} else {
			configuration = cc.buildWith(null, false);
		}

		final JDBCMetaDataConfiguration cfg = new JDBCMetaDataConfiguration();
		Properties properties = configuration.getProperties();		
		cfg.setProperties(properties);		
		cc.buildWith(cfg, false);

		cfg.setPreferBasicCompositeIds(true);

		cc.execute(new Command() { 
			/* need to execute in the console configuration to let it handle classpath stuff!*/
					public Object execute() {

						OverrideRepository repository = new OverrideRepository();

						TableFilter filter = null;
						for (int i = 0; i < tables.size(); i++) {
							Table table = tables.get(i);

							filter = new TableFilter();
							filter.setExclude(false);
							if (table.getCatalog() != null) {
								filter.setMatchCatalog(table.getCatalog());
							}
							if (table.getSchema() != null) {
								filter.setMatchSchema(table.getSchema());
							}
							filter.setMatchName(tables.get(i).getName());

							repository.addTableFilter(filter);
						}

						ReverseEngineeringStrategy res = repository
								.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());

						ReverseEngineeringSettings qqsettings = new ReverseEngineeringSettings(res)
								.setDetectManyToMany(true)
								.setDetectOptimisticLock(true);

						res.setSettings(qqsettings);

						cfg.setReverseEngineeringStrategy(res);

						cfg.readFromJDBC();
						cfg.buildMappings();
						return null;
					}
				});

		return cfg;
	}

	private String getOutputPath() {
		return outputPath;
	}

	private void splitOnGroupsByConsoleCfg() {
		TreePath[] paths = selection.getPaths();
		
		tableGroups = new HashMap<ConsoleConfiguration, List<Table>>();

		for (int i = 0; i < paths.length; i++) {
			if (paths[i].getLastSegment() instanceof Table) {
				ConsoleConfiguration cc = getConsoleConfiguration(paths[i]);
				if (cc == null) continue;
				
				if (tableGroups.containsKey(cc)) {
					List<Table> tables = tableGroups.get(cc);
					tables.add((Table) paths[i].getLastSegment());
				} else {
					List<Table> tables = new ArrayList<Table>();
					tables.add((Table) paths[i].getLastSegment());
					tableGroups.put(cc, tables);
				}
			}
		}
	}

	private ConsoleConfiguration getConsoleConfiguration(TreePath path) {
		for (int i = 0; i < path.getSegmentCount(); i++) {
			if (path.getSegment(i) instanceof ConsoleConfiguration)
				return (ConsoleConfiguration) path.getSegment(i);
		}
		return null;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof TreeSelection) {
			this.selection = (TreeSelection) selection;
		}
	}

	private void refreshOutputDir(String outputdir) {
		IResource bufferRes = PathHelper.findMember(ResourcesPlugin
				.getWorkspace().getRoot(), outputdir);

		if (bufferRes != null && bufferRes.isAccessible()) {
			try {
				bufferRes.refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				// ignore, maybe merge into possible existing status.
			}
		}
	}

}
