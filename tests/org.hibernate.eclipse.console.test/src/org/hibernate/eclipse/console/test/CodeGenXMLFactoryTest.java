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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;
import org.eclipse.datatools.connectivity.internal.ConnectionProfile;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.ExtensionManager;
import org.hibernate.eclipse.console.model.impl.ExporterDefinition;
import org.hibernate.eclipse.console.model.impl.ExporterFactoryStrings;
import org.hibernate.eclipse.console.test.launchcfg.TestConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.test.launchcfg.TestLaunchConfig;
import org.hibernate.eclipse.console.test.utils.ResourceReadUtils;
import org.hibernate.eclipse.launch.CodeGenXMLFactory;
import org.hibernate.eclipse.launch.CodeGenerationStrings;
import org.hibernate.eclipse.launch.ExporterAttributes;
import org.hibernate.eclipse.launch.HibernateLaunchConstants;

/**
 * @author Vitali Yemialyanchyk
 */
public class CodeGenXMLFactoryTest extends TestCase {
	public static final String SAMPLE_PATH = "res/sample/"; //$NON-NLS-1$
	public static final String PROJECT_LIB_PATH = "res/project/lib/"; //$NON-NLS-1$

	public static final String HBMTEMPLATE0 = "hbm2java"; //$NON-NLS-1$
	public static final String HBMTEMPLATE0_PROPERTIES = HibernateLaunchConstants.ATTR_EXPORTERS
			+ '.' + HBMTEMPLATE0 + ".properties"; //$NON-NLS-1$
	public static final String HBMTEMPLATE1 = "query"; //$NON-NLS-1$
	public static final String HBMTEMPLATE1_PROPERTIES = HibernateLaunchConstants.ATTR_EXPORTERS
			+ '.' + HBMTEMPLATE1 + ".properties"; //$NON-NLS-1$
	public static final String HBMTEMPLATE2 = "hbm2ddl"; //$NON-NLS-1$
	public static final String HBMTEMPLATE2_PROPERTIES = HibernateLaunchConstants.ATTR_EXPORTERS
			+ '.' + HBMTEMPLATE2 + ".properties"; //$NON-NLS-1$
	public static final String OUTDIR_PATH = "outputdir/test"; //$NON-NLS-1$

	public class TestConsoleConfigPref extends TestConsoleConfigurationPreferences {
		public File getConfigXMLFile() {
			final File xmlConfig = new File("project/src/hibernate.cfg.xml"); //$NON-NLS-1$
			return xmlConfig;
		}

		public File getPropertyFile() {
			final File propFile = new File("project/src/hibernate.properties"); //$NON-NLS-1$
			return propFile;
		}
	}

	public class TestConsoleConfigPrefJpa extends TestConsoleConfigurationPreferences {
		public File getConfigXMLFile() {
			return null;
		}

		public File[] getMappingFiles() {
			File[] files = new File[2];
			files[0] = new File("xxx.hbm.xml"); //$NON-NLS-1$
			files[1] = new File("yyy.hbm.xml"); //$NON-NLS-1$
			return files;
		}

		public URL[] getCustomClassPathURLS() {
			URL[] urls = new URL[3];
			try {
				urls[0] = new File("ejb3-persistence.jar").toURL(); //$NON-NLS-1$
				urls[1] = new File("hibernate3.jar").toURL(); //$NON-NLS-1$
				urls[2] = new File("hsqldb.jar").toURL(); //$NON-NLS-1$
			} catch (IOException e) {
			}
			return urls;
		}
		public String getEntityResolverName() {
			return ""; //$NON-NLS-1$
		}

		public ConfigurationMode getConfigurationMode() {
			return ConfigurationMode.JPA;
		}

		public String getNamingStrategy() {
			return "testNamingStrategy"; //$NON-NLS-1$
		}

		public String getPersistenceUnitName() {
			return "testPersistenceUnit"; //$NON-NLS-1$
		}

		public String getConnectionProfileName() {
			return ""; //$NON-NLS-1$
		}

		public String getDialectName() {
			return "testDialect"; //$NON-NLS-1$
		}
	}

	public class TestConsoleConfigPref3 extends TestConsoleConfigPref {
		public String getConnectionProfileName() {
			return "connectionProfileName"; //$NON-NLS-1$
		}

		public String getDialectName() {
			return "testDialect"; //$NON-NLS-1$
		}
	}
	
	public class CodeGenXMLFactory4Test extends CodeGenXMLFactory {
		public CodeGenXMLFactory4Test(ILaunchConfiguration lc) {
			super(lc);
		}

		public ConsoleConfigurationPreferences getConsoleConfigPreferences(String consoleConfigName) {
			return null;
		}

		public IConnectionProfile getConnectionProfile(String connProfileName) {
			return null;
		}
		
		public String getDriverClass(String connProfileName) {
			return ""; //$NON-NLS-1$
		}
	}

	public class CodeGenXMLFactory4TestNullable extends CodeGenXMLFactory4Test {
		public CodeGenXMLFactory4TestNullable(ILaunchConfiguration lc) {
			super(lc);
		}

		public String getResLocation(String path) {
			return new Path("reslocation/test").toString(); //$NON-NLS-1$
		}
	}

	public class CodeGenXMLFactory4TestSimple extends CodeGenXMLFactory4Test {
		public CodeGenXMLFactory4TestSimple(ILaunchConfiguration lc) {
			super(lc);
		}

		public ConsoleConfigurationPreferences getConsoleConfigPreferences(String consoleConfigName) {
			return new TestConsoleConfigPref();
		}

		public String getResLocation(String path) {
			return new Path("reslocation/test").toString(); //$NON-NLS-1$
		}
	}

	public class CodeGenXMLFactory4TestRelative extends CodeGenXMLFactory4Test {
		public CodeGenXMLFactory4TestRelative(ILaunchConfiguration lc) {
			super(lc);
		}

		public ConsoleConfigurationPreferences getConsoleConfigPreferences(String consoleConfigName) {
			return new TestConsoleConfigPref();
		}
	}

	public class CodeGenXMLFactory4TestJpa extends CodeGenXMLFactory4Test {
		public CodeGenXMLFactory4TestJpa(ILaunchConfiguration lc) {
			super(lc);
		}

		public ConsoleConfigurationPreferences getConsoleConfigPreferences(String consoleConfigName) {
			return new TestConsoleConfigPrefJpa();
		}

		public String getResLocation(String path) {
			return new Path("reslocation/test").toString(); //$NON-NLS-1$
		}
	}

	public class CodeGenXMLFactory4TestProperties extends CodeGenXMLFactory4Test {
		public CodeGenXMLFactory4TestProperties(ILaunchConfiguration lc) {
			super(lc);
		}

		public ConsoleConfigurationPreferences getConsoleConfigPreferences(String consoleConfigName) {
			return new TestConsoleConfigPref3();
		}

		public String getResLocation(String path) {
			return new Path("reslocation/test").toString(); //$NON-NLS-1$
		}

		public IConnectionProfile getConnectionProfile(String connProfileName) {
			IConnectionProfile profile = new ConnectionProfile("testName", null, null) { //$NON-NLS-1$
				public Properties getProperties(String type) {
					Properties res = new Properties();
					res.setProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID, "url"); //$NON-NLS-1$
					res.setProperty(IJDBCDriverDefinitionConstants.USERNAME_PROP_ID, "username"); //$NON-NLS-1$
					res.setProperty(IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID, "passw"); //$NON-NLS-1$
					return res;
				}
			};
			return profile;
		}
		
		public String getDriverClass(String connProfileName) {
			return "driverClass"; //$NON-NLS-1$
		}
	}
	
	public void testCodeGenXMLFactoryRevengAll() {
		TestLaunchConfig testLaunchConfig = createTestLaunchConfig(true, true, false);
		CodeGenXMLFactory codeGenFactory = new CodeGenXMLFactory4TestSimple(testLaunchConfig);
		String codeGen = adjustXmlText(codeGenFactory.createCodeGenXML());
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		String sample = getSample("AntCodeGenReveng_test1.xml"); //$NON-NLS-1$
		assertEquals(sample, codeGen);
		assertEquals(codeGenProperties.length(), 0);
	}

	public void testCodeGenXMLFactoryRevengOne() {
		TestLaunchConfig testLaunchConfig = createTestLaunchConfig(true, false, false);
		CodeGenXMLFactory codeGenFactory = new CodeGenXMLFactory4TestSimple(testLaunchConfig);
		String codeGen = adjustXmlText(codeGenFactory.createCodeGenXML());
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		String sample = getSample("AntCodeGenReveng_test2.xml"); //$NON-NLS-1$
		assertEquals(sample, codeGen);
		assertEquals(codeGenProperties.length(), 0);
	}

	public void testCodeGenXMLFactoryAll() {
		TestLaunchConfig testLaunchConfig = createTestLaunchConfig(false, true, false);
		CodeGenXMLFactory codeGenFactory = new CodeGenXMLFactory4TestRelative(testLaunchConfig);
		//
		String strPlace = "project/src"; //$NON-NLS-1$
		codeGenFactory.setPlace2Generate(strPlace);
		codeGenFactory.setWorkspacePath(strPlace);
		//
		String codeGen = adjustXmlText(codeGenFactory.createCodeGenXML());
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		String sample = getSample("AntCodeGen_test1.xml"); //$NON-NLS-1$
		assertEquals(sample, codeGen);
		assertEquals(codeGenProperties.length(), 0);
	}

	public void testCodeGenXMLFactoryOne() {
		TestLaunchConfig testLaunchConfig = createTestLaunchConfig(false, false, false);
		CodeGenXMLFactory codeGenFactory = new CodeGenXMLFactory4TestRelative(testLaunchConfig);
		//
		String strPlace = "project/src"; //$NON-NLS-1$
		codeGenFactory.setPlace2Generate(strPlace);
		codeGenFactory.setWorkspacePath(strPlace);
		//
		String codeGen = adjustXmlText(codeGenFactory.createCodeGenXML());
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		String sample = getSample("AntCodeGen_test2.xml"); //$NON-NLS-1$
		assertEquals(sample, codeGen);
		assertEquals(codeGenProperties.length(), 0);
	}

	public void testCodeGenXMLFactoryJpaAll() {
		TestLaunchConfig testLaunchConfig = createTestLaunchConfig(false, true, true);
		CodeGenXMLFactory codeGenFactory = new CodeGenXMLFactory4TestJpa(testLaunchConfig);
		String codeGen = adjustXmlText(codeGenFactory.createCodeGenXML());
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		codeGen = updatePaths(codeGen);
		String sample = getSample("AntCodeGenJpa_test1.xml"); //$NON-NLS-1$
		assertEquals(sample, codeGen);
		assertEquals(codeGenProperties.length(), 0);
	}

	public void testCodeGenXMLFactoryJpaOne() {
		TestLaunchConfig testLaunchConfig = createTestLaunchConfig(false, false, true);
		CodeGenXMLFactory codeGenFactory = new CodeGenXMLFactory4TestJpa(testLaunchConfig);
		String codeGen = adjustXmlText(codeGenFactory.createCodeGenXML());
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		codeGen = updatePaths(codeGen);
		String sample = getSample("AntCodeGenJpa_test2.xml"); //$NON-NLS-1$
		assertEquals(sample, codeGen);
		assertEquals(codeGenProperties.length(), 0);
	}

	public void testCodeGenXMLFactoryNullableAll() {
		TestLaunchConfig testLaunchConfig = createTestLaunchConfig(false, true, false);
		CodeGenXMLFactory codeGenFactory = new CodeGenXMLFactory4TestNullable(testLaunchConfig);
		String codeGen = adjustXmlText(codeGenFactory.createCodeGenXML());
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		codeGen = updatePaths(codeGen);
		String sample = getSample("AntCodeGenNullable_test1.xml"); //$NON-NLS-1$
		assertEquals(sample, codeGen);
		assertEquals(codeGenProperties.length(), 0);
	}

	public void testCodeGenXMLFactoryNullableOne() {
		TestLaunchConfig testLaunchConfig = createTestLaunchConfig(false, false, false);
		CodeGenXMLFactory codeGenFactory = new CodeGenXMLFactory4TestNullable(testLaunchConfig);
		String codeGen = adjustXmlText(codeGenFactory.createCodeGenXML());
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		codeGen = updatePaths(codeGen);
		String sample = getSample("AntCodeGenNullable_test2.xml"); //$NON-NLS-1$
		assertEquals(sample, codeGen);
		assertEquals(codeGenProperties.length(), 0);
	}

	public void testCodeGenXMLFactoryPropertiesAll() {
		TestLaunchConfig testLaunchConfig = createTestLaunchConfig(false, true, false);
		CodeGenXMLFactory codeGenFactory = new CodeGenXMLFactory4TestProperties(testLaunchConfig);
		String codeGen = adjustXmlText(codeGenFactory.createCodeGenXML());
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		codeGen = updatePaths(codeGen);
		String sample = getSample("AntCodeGenProps_test1.xml"); //$NON-NLS-1$
		String sampleProperties = getSample("AntCodeGenProps.hibernate.properties"); //$NON-NLS-1$
		assertEquals(sample, codeGen);
		assertEquals(sampleProperties.trim(), codeGenProperties);
	}

	public void testCodeGenXMLFactoryPropertiesOne() {
		TestLaunchConfig testLaunchConfig = createTestLaunchConfig(false, false, false);
		CodeGenXMLFactory codeGenFactory = new CodeGenXMLFactory4TestProperties(testLaunchConfig);
		String codeGen = adjustXmlText(codeGenFactory.createCodeGenXML());
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		codeGen = updatePaths(codeGen);
		String sample = getSample("AntCodeGenProps_test2.xml"); //$NON-NLS-1$
		String sampleProperties = getSample("AntCodeGenProps.hibernate.properties"); //$NON-NLS-1$
		assertEquals(sample, codeGen);
		assertEquals(sampleProperties.trim(), codeGenProperties);
	}

	public void testCodeGenXMLFactoryInternalPropertiesAll() {
		TestLaunchConfig testLaunchConfig = createTestLaunchConfig(false, true, false);
		CodeGenXMLFactory codeGenFactory = new CodeGenXMLFactory4TestProperties(testLaunchConfig);
		codeGenFactory.setExternalPropFile(false);
		String codeGen = adjustXmlText(codeGenFactory.createCodeGenXML());
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		codeGen = updatePaths(codeGen);
		String sample = getSample("AntCodeGenInternalProps_test1.xml"); //$NON-NLS-1$
		String sampleProperties = getSample("AntCodeGenPropsInternal.hibernate.properties"); //$NON-NLS-1$
		assertEquals(sample, codeGen);
		assertEquals(sampleProperties.trim(), codeGenProperties);
	}

	public void testCodeGenXMLFactoryInternalPropertiesOne() {
		TestLaunchConfig testLaunchConfig = createTestLaunchConfig(false, false, false);
		CodeGenXMLFactory codeGenFactory = new CodeGenXMLFactory4TestProperties(testLaunchConfig);
		codeGenFactory.setExternalPropFile(false);
		String codeGen = adjustXmlText(codeGenFactory.createCodeGenXML());
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		codeGen = updatePaths(codeGen);
		String sample = getSample("AntCodeGenInternalProps_test2.xml"); //$NON-NLS-1$
		String sampleProperties = getSample("AntCodeGenPropsInternal.hibernate.properties"); //$NON-NLS-1$
		assertEquals(sample, codeGen);
		assertEquals(sampleProperties.trim(), codeGenProperties);
	}
	
	public String updatePaths(String codeGen) {
		String repl = ""; //$NON-NLS-1$
		try {
			URI uri = new File("").toURL().toURI(); //$NON-NLS-1$
			repl = (new File(uri)).getPath();
			repl = (new Path(repl)).toString();
		} catch (MalformedURLException e) {
		} catch (URISyntaxException e) {
		}
		return codeGen.replace(repl + IPath.SEPARATOR, ""); //$NON-NLS-1$
	}
	
	public Map<String, Object> getTestLaunchConfigAttr(boolean reveng, boolean exportersAll, boolean jpa) {
		Map<String, ExporterDefinition> exDefinitions = ExtensionManager.findExporterDefinitionsAsMap();
		Map<String, Object> testLaunchConfigAttr = new HashMap<String, Object>();
		String tmp = "12345678901234567890"; //$NON-NLS-1$
		testLaunchConfigAttr.put(HibernateLaunchConstants.ATTR_TEMPLATE_DIR, tmp);
		testLaunchConfigAttr.put(HibernateLaunchConstants.ATTR_OUTPUT_DIR, tmp);
		testLaunchConfigAttr.put(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER_SETTINGS, tmp);
		if (jpa) {
			testLaunchConfigAttr.put(HibernateLaunchConstants.ATTR_ENABLE_EJB3_ANNOTATIONS, true);
			testLaunchConfigAttr.put(HibernateLaunchConstants.ATTR_ENABLE_JDK5, true);
		}
		List<String> exportersList = new ArrayList<String>();
		if (exportersAll) {
			exportersList.clear();
		} else {
			exportersList.add(HBMTEMPLATE0);
		}
		TreeMap<String, ExporterDefinition> exDefinitionsSorted = new TreeMap<String, ExporterDefinition>();
		exDefinitionsSorted.putAll(exDefinitions);
		for (Map.Entry<String, ExporterDefinition> exDef : exDefinitionsSorted.entrySet()) {
			String tmp0 = exDef.getValue().getExporterTag();
			String tmp1 = ExporterAttributes.getLaunchAttributePrefix(tmp0);
			testLaunchConfigAttr.put(tmp1 + ".extension_id", //$NON-NLS-1$ 
				HibernateLaunchConstants.ATTR_PREFIX + tmp0);
			testLaunchConfigAttr.put(tmp1, Boolean.TRUE);
			if (exportersAll) {
				exportersList.add(tmp0);
			}
		}
		testLaunchConfigAttr.put(HibernateLaunchConstants.ATTR_EXPORTERS, exportersList);
		Map<String, String> expProps2 = new HashMap<String, String>();
		// test properties overlap case: 
		// ExporterFactoryStrings.OUTPUTDIR & CodeGenerationStrings.DESTDIR - is a same property
		// ExporterFactoryStrings.OUTPUTDIR - is a GUI name for the property
		// CodeGenerationStrings.DESTDIR - is Ant script name for the property
		// GUI name is more preferable, i.e. "_test_suffix" should not be in generated file
		expProps2.put(ExporterFactoryStrings.OUTPUTDIR, OUTDIR_PATH);
		expProps2.put(CodeGenerationStrings.DESTDIR, OUTDIR_PATH + "_test_suffix"); //$NON-NLS-1$
		expProps2.put("keyXXX", "valueYYY"); //$NON-NLS-1$ //$NON-NLS-2$
		expProps2.put("keyCCC", "valueYYY"); //$NON-NLS-1$ //$NON-NLS-2$
		expProps2.put("keyAAA", "valueYYY"); //$NON-NLS-1$ //$NON-NLS-2$
		expProps2.put("keyDDD", "valueYYY"); //$NON-NLS-1$ //$NON-NLS-2$
		//
		Map<String, String> expProps3 = new HashMap<String, String>();
		expProps3.put(ExporterFactoryStrings.QUERY_STRING, "from testQuery"); //$NON-NLS-1$
		//
		Map<String, String> expProps4 = new HashMap<String, String>();
		expProps4.put(CodeGenerationStrings.DESTDIR, OUTDIR_PATH + "_test_suffix"); //$NON-NLS-1$
		expProps4.put(CodeGenerationStrings.TEMPLATEPATH, OUTDIR_PATH);
		expProps4.put("export", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		expProps4.put("update", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		expProps4.put("drop", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		expProps4.put("create", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		expProps4.put("delimiter", "@"); //$NON-NLS-1$ //$NON-NLS-2$
		expProps4.put("format", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		expProps4.put("haltonerror", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		expProps4.put("console", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		expProps4.put(CodeGenerationStrings.EJB3, "true"); //$NON-NLS-1$
		expProps4.put(CodeGenerationStrings.JDK5, "true"); //$NON-NLS-1$
		//
		testLaunchConfigAttr.put(HBMTEMPLATE0_PROPERTIES, expProps2);
		testLaunchConfigAttr.put(HBMTEMPLATE1_PROPERTIES, expProps3);
		testLaunchConfigAttr.put(HBMTEMPLATE2_PROPERTIES, expProps4);
		testLaunchConfigAttr.put(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER, reveng);
		testLaunchConfigAttr.put(HibernateLaunchConstants.ATTR_PREFER_BASIC_COMPOSITE_IDS, true);
		return testLaunchConfigAttr;
	}
	
	public TestLaunchConfig createTestLaunchConfig(boolean reveng, boolean exportersAll, boolean jpa) {
		Map<String, Object> testLaunchConfigAttr = getTestLaunchConfigAttr(reveng, exportersAll, jpa);
		TestLaunchConfig testLaunchConfig = new TestLaunchConfig(testLaunchConfigAttr);
		return testLaunchConfig;
	}

	public String getSample(String fileName) {
		return ResourceReadUtils.getSample(SAMPLE_PATH + fileName);
	}

	public String adjustXmlText(String sample) {
		return ResourceReadUtils.adjustXmlText(sample);
	}

}
