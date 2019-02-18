/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.orm.test;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.model.impl.ExporterFactory;
import org.hibernate.eclipse.console.model.impl.ExporterFactoryStrings;
import org.hibernate.eclipse.launch.CodeGenerationStrings;
import org.hibernate.eclipse.launch.ExporterAttributes;
import org.jboss.tools.hibernate.orm.test.utils.ResourceReadUtils;
import org.jboss.tools.hibernate.orm.test.utils.TestConsoleConfigurationPreferences;
import org.jboss.tools.hibernate.orm.test.utils.project.LaunchConfigTestProject;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.IService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Vitali Yemialyanchyk
 */
public class ExporterAttributesTest {
	
	private static final String HIBERNATE_CFG_XML = 
			"<!DOCTYPE hibernate-configuration PUBLIC                               " +
			"	'-//Hibernate/Hibernate Configuration DTD 3.0//EN'                  " +
			"	'http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd'> " +
			"                                                                       " +
			"<hibernate-configuration>                                              " +
			"	<session-factory/>                                                  " + 
			"</hibernate-configuration>                                             " ;		
			
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private ConsoleConfiguration consoleCfg;
	private LaunchConfigTestProject project;
	private IService service;
	private ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
	private File cfgXmlFile = null;

	@Before
	public void setUp() throws Exception {

		cfgXmlFile = new File(temporaryFolder.getRoot(), "hibernate.cfg.xml");
		FileWriter fw = new FileWriter(cfgXmlFile);
		fw.write(HIBERNATE_CFG_XML);
		fw.close();

		this.project = new LaunchConfigTestProject();

		TestConsoleConfigurationPreferences cfgprefs = 
				new TestConsoleConfigurationPreferences(cfgXmlFile);
		consoleCfg = new ConsoleConfiguration(cfgprefs);
		service = consoleCfg.getHibernateExtension().getHibernateService();
		KnownConfigurations.getInstance().addConfiguration(consoleCfg, true);
	}

	@After
	public void tearDown() throws Exception {
		this.project.deleteIProject();
		this.project = null;

		KnownConfigurations.getInstance().removeAllConfigurations();
		consoleCfg = null;
	}

	@Test
	public void testExporterAttributes() {
		final String fileName_0 = LaunchConfigTestProject.LAUNCH_TEST_FILE_0;
		checkCorrectLaunchConfigurationFile(fileName_0);
		final String fileName_1 = LaunchConfigTestProject.LAUNCH_TEST_FILE_1;
		checkIncorrectLaunchConfigurationFile(fileName_1);
	}

	private void checkCorrectLaunchConfigurationFile(final String fileName) {
		// IWorkspace ws = ResourcesPlugin.getWorkspace();
		String str1, str2;
		ILaunchConfiguration launchConfig = loadLaunchConfigFromFile(fileName);
		Map<String, Object> attrMap = null;
		try {
			attrMap = launchConfig.getAttributes();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(attrMap);
		ExporterAttributes exporterAttributes = null;
		try {
			exporterAttributes = new ExporterAttributes(launchConfig);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(exporterAttributes);
		// check is configuration correct
		Assert.assertNull(exporterAttributes.checkExporterAttributes());
		List<ExporterFactory> exporterFactories = exporterAttributes.getExporterFactories();
		Set<ExporterFactory> selectedExporters = new HashSet<ExporterFactory>();
		selectedExporters.addAll(exporterFactories);
		Set<String> deletedExporterIds = new HashSet<String>();
		ILaunchConfigurationWorkingCopy launchConfigWC = null;
		try {
			launchConfigWC = launchConfig.getWorkingCopy();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(launchConfigWC);
		//
		str1 = project.getSample(fileName);
		str1 = ResourceReadUtils.adjustXmlText(str1);
		//
		Assert.assertNotNull(project);
		Assert.assertNotNull(project.getIProject());
		Assert.assertNotNull(project.getIProject().getFile(fileName));
		//
		InputStream is = null;
		try {
			is = project.getIProject().getFile(fileName).getContents();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(is);
		str2 = ResourceReadUtils.readStream(is);
		str2 = ResourceReadUtils.adjustXmlText(str2);
		Assert.assertEquals(str1, str2);
		// update and save lc - so fileName from the project updated
		try {
			ExporterAttributes.saveExporterFactories(launchConfigWC, exporterFactories,
					selectedExporters, deletedExporterIds);
			launchConfigWC.doSave();
		} catch (CoreException e) {
			Assert.fail(e.getMessage());
		}
		//
		is = null;
		try {
			is = project.getIProject().getFile(fileName).getContents();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(is);
		str2 = ResourceReadUtils.readStream(is);
		str2 = ResourceReadUtils.adjustXmlText(str2);
		Assert.assertEquals(str1, str2);
		//
		IArtifactCollector artifactCollector = service.newArtifactCollector();
		ExporterAttributes expAttr = exporterAttributes;
        // Global properties
        Properties props = new Properties();
        props.put(CodeGenerationStrings.EJB3, "" + expAttr.isEJB3Enabled()); //$NON-NLS-1$
        props.put(CodeGenerationStrings.JDK5, "" + expAttr.isJDK5Enabled()); //$NON-NLS-1$
        consoleCfg.build();
        IConfiguration cfg = consoleCfg.getConfiguration();
		Assert.assertNotNull(cfg);
		Set<String> outputDirectories = new HashSet<String>();
		for (int i = 0; i < exporterFactories.size(); i++) {
			Properties globalProperties = new Properties();
			globalProperties.putAll(props);
			ExporterFactory ef = exporterFactories.get(i);
			//
			Properties propsForTesting = new Properties();
			propsForTesting.putAll(globalProperties);
			propsForTesting.putAll(ef.getProperties());
			//
			IExporter exporter = null;
			outputDirectories.clear();
			try {
				exporter = ef.createConfiguredExporter(cfg,
					expAttr.getOutputPath(), expAttr.getTemplatePath(), globalProperties,
					outputDirectories, artifactCollector, service);
			} catch (CoreException e) {
				e.printStackTrace();
			}
			Assert.assertNotNull(exporter);
			Assert.assertTrue(outputDirectories.size() > 0);
			Properties propsFromExporter = exporter.getProperties();
			String exporterDefinitionId = ef.getExporterDefinitionId();
			// test special handling for GenericExporter
			if (exporterDefinitionId.equals("org.hibernate.tools.hbmtemplate")) { //$NON-NLS-1$
				Assert.assertNull(propsFromExporter.getProperty(ExporterFactoryStrings.FILE_PATTERN));
				Assert.assertNull(propsFromExporter.getProperty(ExporterFactoryStrings.TEMPLATE_NAME));
				Assert.assertNull(propsFromExporter.getProperty(ExporterFactoryStrings.FOR_EACH));
				IGenericExporter ge = exporter.getGenericExporter();
				Assert.assertNotNull(ge);
				Assert.assertEquals(propsForTesting.getProperty(ExporterFactoryStrings.FILE_PATTERN), ge.getFilePattern());
				Assert.assertEquals(propsForTesting.getProperty(ExporterFactoryStrings.TEMPLATE_NAME), ge.getTemplateName());
				// to test GenericExporter should provide public getter but it doesn't
				//assertEquals(propsForTesting.getProperty(ExporterFactoryStrings.FOR_EACH), ge.getForEach());
			}
			// test special handling for Hbm2DDLExporter
			if (exporterDefinitionId.equals("org.hibernate.tools.hbm2ddl")) { //$NON-NLS-1$
				Assert.assertNull(propsFromExporter.getProperty(ExporterFactoryStrings.EXPORTTODATABASE));
				IHbm2DDLExporter ddlExporter = exporter.getHbm2DDLExporter();
				Assert.assertNotNull(ddlExporter);
				// to test Hbm2DDLExporter should provide public getter but it doesn't
				//assertEquals(propsForTesting.getProperty(ExporterFactoryStrings.EXPORTTODATABASE), ddlExporter.getExport());
			}
			// test special handling for QueryExporter
			if (exporterDefinitionId.equals("org.hibernate.tools.query")) { //$NON-NLS-1$
				Assert.assertNull(propsFromExporter.getProperty(ExporterFactoryStrings.QUERY_STRING));
				Assert.assertNull(propsFromExporter.getProperty(ExporterFactoryStrings.OUTPUTFILENAME));
				//IQueryExporter queryExporter = exporter.getQueryExporter();
				// to test QueryExporter should provide public getter but it doesn't
				//List<String> queryStrings = queryExporter.getQueries();
				//assertEquals(1, queryStrings.size());
				//assertEquals(propsForTesting.getProperty(ExporterFactoryStrings.QUERY_STRING), queryStrings.get(0));
				//assertEquals(propsForTesting.getProperty(ExporterFactoryStrings.OUTPUTFILENAME), queryExporter.getFileName());
			}
			//try {
			//	exporter.start();
			//} catch (HibernateException he) {
			//	he.printStackTrace();
			//}
		}
		Assert.assertTrue(artifactCollector.getFileTypes().size() == 0);
	}
	
	private void checkIncorrectLaunchConfigurationFile(final String fileName) {
		ILaunchConfiguration launchConfig = loadLaunchConfigFromFile(fileName);
		Map<String, Object> attrMap = null;
		try {
			attrMap = launchConfig.getAttributes();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(attrMap);
		ExporterAttributes exporterAttributes = null;
		try {
			exporterAttributes = new ExporterAttributes(launchConfig);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(exporterAttributes);
		// check is configuration correct
		Assert.assertNotNull(exporterAttributes.checkExporterAttributes());
	}

	private ILaunchConfiguration loadLaunchConfigFromFile(String fileName) {
		IPath path = new Path(fileName);
		IFile ifile = project.getIProject().getFile(path);
		ILaunchConfiguration launchConfig = launchManager.getLaunchConfiguration((IFile) ifile);
		return launchConfig;
	}
}
