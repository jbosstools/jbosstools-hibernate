/*
 * Created on 10-Dec-2004
 *
 */
package org.hibernate.eclipse.console.actions;

import java.io.File;

import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ConsoleConfiguration.Command;
import org.hibernate.console.node.BaseNode;
import org.hibernate.tool.hbm2x.ConfigurationNavigator;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.VelocityExporter;
import org.hibernate.tool.jdbc2cfg.ConfigurableReverseNamingStrategy;
import org.hibernate.tool.jdbc2cfg.Filter;
import org.hibernate.tool.jdbc2cfg.JDBCMetaDataConfiguration;

/**
 * @author max
 *
 */
public class ArtifactGeneratorAction extends ConsoleConfigurationBasedAction {

	/**
	 * @param text
	 */
	public ArtifactGeneratorAction() {
		super("Generate");
		setSupportMultiple(false);
		setEnabledWhenNoSessionFactory(true);
	}
	
	protected boolean updateState(ConsoleConfiguration consoleConfiguration) {
		return super.updateState(consoleConfiguration);
	}
	
	public void run() {
		BaseNode node = ((BaseNode) getSelectedNonResources().get(0));
		final ConsoleConfiguration cc = node.getConsoleConfiguration();
		
		final JDBCMetaDataConfiguration cfg = new JDBCMetaDataConfiguration();
		cc.buildWith(cfg);
		cfg.setGeneratingDynamicClasses(false);
		ConfigurableReverseNamingStrategy configurableNamingStrategy = new ConfigurableReverseNamingStrategy();
		configurableNamingStrategy.setPackageName("org.reveng");
		cfg.setReverseNamingStrategy(configurableNamingStrategy);
		
		
		cc.execute(new Command() { // need to execute in the consoleconfiguration to let it handle classpath stuff!

			public Object execute() {
				cfg.readFromJDBC(new Filter() {
					public boolean acceptTableName(String name) {
						return name.startsWith("R_");
					}
				});
				return null;
			}
		});
		
		File outputdir = new File("reverseoutput");
		outputdir.mkdirs();
		
		final ConfigurationNavigator cv = new ConfigurationNavigator();
		final Exporter hbmExporter = new HibernateMappingExporter(cfg, outputdir);
		final Exporter javaExporter = new VelocityExporter(cfg, outputdir);
		
		cv.export(cfg, hbmExporter);
		cv.export(cfg, javaExporter);
	
	}
}
