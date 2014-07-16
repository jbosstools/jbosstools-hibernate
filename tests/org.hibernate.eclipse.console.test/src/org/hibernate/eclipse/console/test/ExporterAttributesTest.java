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
package org.hibernate.eclipse.console.test;

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
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.core.LaunchManager;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.model.impl.ExporterFactory;
import org.hibernate.eclipse.console.model.impl.ExporterFactoryStrings;
import org.hibernate.eclipse.console.test.launchcfg.TestConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.test.project.LaunchConfigTestProject;
import org.hibernate.eclipse.console.test.utils.ResourceReadUtils;
import org.hibernate.eclipse.launch.CodeGenerationStrings;
import org.hibernate.eclipse.launch.ExporterAttributes;
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.Hbm2DDLExporter;
import org.hibernate.tool.hbm2x.QueryExporter;

import junit.framework.TestCase;

/**
 * @author Vitali Yemialyanchyk
 */
@SuppressWarnings("restriction")
public class ExporterAttributesTest extends TestCase {

	private ConsoleConfiguration consoleCfg;
	private LaunchConfigTestProject project;
	private LaunchManager launchManager = new LaunchManager();

	public ExporterAttributesTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		this.project = new LaunchConfigTestProject();

		TestConsoleConfigurationPreferences cfgprefs = new TestConsoleConfigurationPreferences();
		consoleCfg = new ConsoleConfiguration(cfgprefs);
		KnownConfigurations.getInstance().addConfiguration(consoleCfg, true);
	}

	protected void tearDown() throws Exception {
		this.project.deleteIProject();
		this.project = null;

		KnownConfigurations.getInstance().removeAllConfigurations();
		consoleCfg = null;
	}

	public void testExporterAttributes() {
		final String fileName_0 = LaunchConfigTestProject.LAUNCH_TEST_FILE_0;
		checkCorrectLaunchConfigurationFile(fileName_0);
		final String fileName_1 = LaunchConfigTestProject.LAUNCH_TEST_FILE_1;
		checkIncorrectLaunchConfigurationFile(fileName_1);
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	public void checkCorrectLaunchConfigurationFile(final String fileName) {
		// IWorkspace ws = ResourcesPlugin.getWorkspace();
		String str1, str2;
		ILaunchConfiguration launchConfig = loadLaunchConfigFromFile(fileName);
		Map attrMap = null;
		try {
			attrMap = launchConfig.getAttributes();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		assertNotNull(attrMap);
		ExporterAttributes exporterAttributes = null;
		try {
			exporterAttributes = new ExporterAttributes(launchConfig);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		assertNotNull(exporterAttributes);
		// check is configuration correct
		assertNull(exporterAttributes.checkExporterAttributes());
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
		assertNotNull(launchConfigWC);
		//
		str1 = getProject().getSample(fileName);
		str1 = ResourceReadUtils.adjustXmlText(str1);
		//
		assertNotNull(getProject());
		assertNotNull(getProject().getIProject());
		assertNotNull(getProject().getIProject().getFile(fileName));
		//
		InputStream is = null;
		try {
			is = getProject().getIProject().getFile(fileName).getContents();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		assertNotNull(is);
		str2 = ResourceReadUtils.readStream(is);
		str2 = ResourceReadUtils.adjustXmlText(str2);
		assertEquals(str1, str2);
		// update and save lc - so fileName from the project updated
		try {
			ExporterAttributes.saveExporterFactories(launchConfigWC, exporterFactories,
					selectedExporters, deletedExporterIds);
			launchConfigWC.doSave();
		} catch (CoreException e) {
			fail(e.getMessage());
		}
		//
		is = null;
		try {
			is = getProject().getIProject().getFile(fileName).getContents();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		assertNotNull(is);
		str2 = ResourceReadUtils.readStream(is);
		str2 = ResourceReadUtils.adjustXmlText(str2);
		assertEquals(str1, str2);
		//
		ArtifactCollector artifactCollector = new ArtifactCollector();
		ExporterAttributes expAttr = exporterAttributes;
        // Global properties
        Properties props = new Properties();
        props.put(CodeGenerationStrings.EJB3, "" + expAttr.isEJB3Enabled()); //$NON-NLS-1$
        props.put(CodeGenerationStrings.JDK5, "" + expAttr.isJDK5Enabled()); //$NON-NLS-1$
        consoleCfg.build();
        Configuration cfg = consoleCfg.getConfiguration();
		assertNotNull(cfg);
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
			Exporter exporter = null;
			outputDirectories.clear();
			try {
				exporter = ef.createConfiguredExporter(cfg,
					expAttr.getOutputPath(), expAttr.getTemplatePath(), globalProperties,
					outputDirectories, artifactCollector);
			} catch (CoreException e) {
				e.printStackTrace();
			}
			assertNotNull(exporter);
			assertTrue(outputDirectories.size() > 0);
			Properties propsFromExporter = exporter.getProperties();
			String exporterDefinitionId = ef.getExporterDefinitionId();
			// test special handling for GenericExporter
			if (exporterDefinitionId.equals("org.hibernate.tools.hbmtemplate")) { //$NON-NLS-1$
				assertTrue(exporter instanceof GenericExporter);
				assertNull(propsFromExporter.getProperty(ExporterFactoryStrings.FILE_PATTERN));
				assertNull(propsFromExporter.getProperty(ExporterFactoryStrings.TEMPLATE_NAME));
				assertNull(propsFromExporter.getProperty(ExporterFactoryStrings.FOR_EACH));
				GenericExporter ge = (GenericExporter) exporter;
				assertEquals(propsForTesting.getProperty(ExporterFactoryStrings.FILE_PATTERN), ge.getFilePattern());
				assertEquals(propsForTesting.getProperty(ExporterFactoryStrings.TEMPLATE_NAME), ge.getTemplateName());
				// to test GenericExporter should provide public getter but it doesn't
				//assertEquals(propsForTesting.getProperty(ExporterFactoryStrings.FOR_EACH), ge.getForEach());
			}
			// test special handling for Hbm2DDLExporter
			if (exporterDefinitionId.equals("org.hibernate.tools.hbm2ddl")) { //$NON-NLS-1$
				assertTrue(exporter instanceof Hbm2DDLExporter);
				assertNull(propsFromExporter.getProperty(ExporterFactoryStrings.EXPORTTODATABASE));
				Hbm2DDLExporter ddlExporter = (Hbm2DDLExporter) exporter;
				// to test Hbm2DDLExporter should provide public getter but it doesn't
				//assertEquals(propsForTesting.getProperty(ExporterFactoryStrings.EXPORTTODATABASE), ddlExporter.getExport());
			}
			// test special handling for QueryExporter
			if (exporterDefinitionId.equals("org.hibernate.tools.query")) { //$NON-NLS-1$
				assertTrue(exporter instanceof QueryExporter);
				assertNull(propsFromExporter.getProperty(ExporterFactoryStrings.QUERY_STRING));
				assertNull(propsFromExporter.getProperty(ExporterFactoryStrings.OUTPUTFILENAME));
				QueryExporter queryExporter = (QueryExporter)exporter;
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
		assertTrue(artifactCollector.getFileTypes().size() == 0);
	}
	
	@SuppressWarnings({ "rawtypes" })
	public void checkIncorrectLaunchConfigurationFile(final String fileName) {
		ILaunchConfiguration launchConfig = loadLaunchConfigFromFile(fileName);
		Map attrMap = null;
		try {
			attrMap = launchConfig.getAttributes();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		assertNotNull(attrMap);
		ExporterAttributes exporterAttributes = null;
		try {
			exporterAttributes = new ExporterAttributes(launchConfig);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		assertNotNull(exporterAttributes);
		// check is configuration correct
		assertNotNull(exporterAttributes.checkExporterAttributes());
	}

	protected LaunchConfigTestProject getProject() {
		return this.project;
	}

	public ILaunchConfiguration loadLaunchConfigFromFile(String fileName) {
		IPath path = new Path(fileName);
		IFile ifile = getProject().getIProject().getFile(path);
		ILaunchConfiguration launchConfig = launchManager.getLaunchConfiguration((IFile) ifile);
		return launchConfig;
	}
}
